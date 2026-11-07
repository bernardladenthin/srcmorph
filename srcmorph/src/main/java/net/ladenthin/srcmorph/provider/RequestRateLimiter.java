// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.InterruptedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;
import lombok.ToString;
import net.ladenthin.srcmorph.support.UrlUserInfo;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds requests to one server back so that no more than {@code requestsPerMinute} of them start
 * within any minute: it keeps the start times of the requests of the last minute, and when that many
 * or more are in it, the next request waits until fewer than that many are left: until the newest of
 * the starts that have to leave the window leaves it. The limit is the request's, the window the
 * server's.
 *
 * <p>A server answers a request over its own limit with {@code 429} (the GitHub Copilot API Gateway's
 * {@code rateLimitPerMinute}). The {@code openai-compatible} provider retries a {@code 429} after a
 * backoff, but a fast model would spend its retries on the limit and fail files; waiting here keeps it
 * under the limit instead.</p>
 *
 * <p>{@link #shared(String)} hands every provider of one server (its {@code baseUrl} and key variable)
 * the same window, so the models and the phases of one run, and the runs of one long-lived JVM (mvnd,
 * an IDE, an embedder), count together. Each request names its model's own limit
 * ({@link #acquire(int)}): it waits until fewer than that many started in the window, whichever model
 * started them, so a later run that names a higher limit sends at it. A process does not know the
 * requests of an earlier one: a run started right after another can still meet a full server
 * window.</p>
 */
@ToString
final class RequestRateLimiter {

    /** The window the limit counts requests in. */
    static final long WINDOW_NANOS = TimeUnit.MINUTES.toNanos(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRateLimiter.class);

    /** One window per server, for every provider of this process. */
    private static final ConcurrentMap<String, RequestRateLimiter> SHARED = new ConcurrentHashMap<>();

    /** Waits a number of nanoseconds; {@link Condition#awaitNanos(long)} unless a test replaces it. */
    @FunctionalInterface
    interface Waiter {

        /**
         * Waits.
         *
         * @param nanos how long
         * @throws InterruptedException if the thread is interrupted while waiting
         */
        void await(long nanos) throws InterruptedException;
    }

    /** The server, its URL masked ({@link UrlUserInfo#mask(String)}): the log line of a wait and toString show it. */
    private final String server;

    private final long windowNanos;

    @ToString.Exclude
    private final LongSupplier clock;

    @ToString.Exclude
    private final Waiter waiter;

    @ToString.Exclude
    private final ReentrantLock lock = new ReentrantLock();

    @ToString.Exclude
    private final Condition windowMoved = lock.newCondition();

    @ToString.Exclude
    private final Deque<Long> starts = new ArrayDeque<>();

    /**
     * Creates a limiter.
     *
     * @param server      names the server in the log line of a wait, masked there
     * @param windowNanos the window
     * @param clock       the current time in nanoseconds ({@link System#nanoTime()})
     * @param waiter      waits, {@code null} for {@link Condition#awaitNanos(long)}
     */
    RequestRateLimiter(
            final String server, final long windowNanos, final LongSupplier clock, final @Nullable Waiter waiter) {
        this.server = UrlUserInfo.mask(Objects.requireNonNull(server, "server"));
        this.windowNanos = windowNanos;
        this.clock = Objects.requireNonNull(clock, "clock");
        // the loop in acquire() re-checks the window after every wake-up, spurious ones included
        this.waiter = waiter == null ? windowMoved::awaitNanos : waiter;
    }

    /**
     * The window of one server, shared by every provider of this process that names the same server.
     *
     * @param server the server: its {@code baseUrl} and the name of its key variable
     * @return the shared limiter
     */
    static RequestRateLimiter shared(final String server) {
        return SHARED.computeIfAbsent(
                server, k -> new RequestRateLimiter(server, WINDOW_NANOS, System::nanoTime, null));
    }

    /**
     * Waits until fewer than {@code perMinute} requests to the server started within the window, then
     * counts this one as started.
     *
     * @param perMinute the request's model's {@code requestsPerMinute}, positive
     * @throws InterruptedIOException   if the thread is interrupted while waiting (its interrupt flag is
     *                                  set again)
     * @throws IllegalArgumentException if {@code perMinute} is below one
     */
    void acquire(final int perMinute) throws InterruptedIOException {
        if (perMinute < 1) {
            throw new IllegalArgumentException("requestsPerMinute must be positive: " + perMinute);
        }
        lock.lock();
        try {
            for (long now = clock.getAsLong(); ; now = clock.getAsLong()) {
                dropOlderThanTheWindow(now);
                if (starts.size() < perMinute) {
                    starts.addLast(now);
                    return;
                }
                // every start up to this one has to leave the window before fewer than perMinute are in it
                final long wait = startAt(starts.size() - perMinute) + windowNanos - now;
                LOGGER.info(String.format(
                        Locale.ROOT,
                        "Holding the next request to %s for %.1f s: %d requests started in the last minute"
                                + " (requestsPerMinute %d)",
                        server,
                        wait / (double) TimeUnit.SECONDS.toNanos(1),
                        starts.size(),
                        perMinute));
                await(wait);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * How many requests started within the window before {@code now}.
     *
     * @return the count
     */
    int startedInWindow() {
        lock.lock();
        try {
            dropOlderThanTheWindow(clock.getAsLong());
            return starts.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * The start at a position of the window, the oldest at {@code 0}.
     *
     * @param index the position, below the window's size
     * @return the start time
     */
    private long startAt(final int index) {
        final Iterator<Long> iterator = starts.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    private void dropOlderThanTheWindow(final long now) {
        for (Long first = starts.peekFirst(); first != null && now - first >= windowNanos; first = starts.peekFirst()) {
            starts.removeFirst();
        }
    }

    private void await(final long nanos) throws InterruptedIOException {
        try {
            waiter.await(nanos);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            final InterruptedIOException interrupted =
                    new InterruptedIOException("Interrupted while holding a request to " + server);
            interrupted.initCause(e);
            throw interrupted;
        }
    }
}
