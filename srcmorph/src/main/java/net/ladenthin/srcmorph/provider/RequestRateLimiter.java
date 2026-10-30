// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.InterruptedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds requests to one server back so that no more than {@code requestsPerMinute} of them start
 * within any minute: it keeps the start times of the requests of the last minute, and when that many
 * are in it, the next request waits until the oldest one leaves the window.
 *
 * <p>A server answers a request over its own limit with {@code 429} (the GitHub Copilot API Gateway's
 * {@code rateLimitPerMinute}), and the {@code openai-compatible} provider does not retry; a fast model
 * would stop the run after a few files. Waiting here keeps it under the limit instead.</p>
 *
 * <p>{@link #shared(String, int)} hands every provider of one server (its {@code baseUrl} and key
 * variable) with the same limit the same window, so the models and the phases of one run count
 * together. A process does not know the requests of an earlier one: a run started right after
 * another can still meet a full server window.</p>
 */
@ToString
final class RequestRateLimiter {

    /** The window the limit counts requests in. */
    static final long WINDOW_NANOS = TimeUnit.MINUTES.toNanos(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRateLimiter.class);

    /** One window per server and limit, for every provider of this process. */
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

    private final String server;
    private final int perMinute;
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
     * @param server      names the server in the log line of a wait
     * @param perMinute   how many requests may start within {@code windowNanos}
     * @param windowNanos the window
     * @param clock       the current time in nanoseconds ({@link System#nanoTime()})
     * @param waiter      waits, {@code null} for {@link Condition#awaitNanos(long)}
     */
    RequestRateLimiter(
            final String server,
            final int perMinute,
            final long windowNanos,
            final LongSupplier clock,
            final @Nullable Waiter waiter) {
        this.server = Objects.requireNonNull(server, "server");
        this.perMinute = perMinute;
        this.windowNanos = windowNanos;
        this.clock = Objects.requireNonNull(clock, "clock");
        // the loop in acquire() re-checks the window after every wake-up, spurious ones included
        this.waiter = waiter == null ? windowMoved::awaitNanos : waiter;
    }

    /**
     * The limiter of one server, shared by every provider of this process that names the same server
     * and limit.
     *
     * @param server    the server: its {@code baseUrl} and the name of its key variable
     * @param perMinute the limit, positive
     * @return the shared limiter
     */
    static RequestRateLimiter shared(final String server, final int perMinute) {
        return SHARED.computeIfAbsent(
                server + '\n' + perMinute,
                k -> new RequestRateLimiter(server, perMinute, WINDOW_NANOS, System::nanoTime, null));
    }

    /**
     * Waits until a request may start, then counts it as started.
     *
     * @throws InterruptedIOException if the thread is interrupted while waiting (its interrupt flag is
     *                                set again)
     */
    void acquire() throws InterruptedIOException {
        lock.lock();
        try {
            for (long now = clock.getAsLong(); ; now = clock.getAsLong()) {
                dropOlderThanTheWindow(now);
                if (starts.size() < perMinute) {
                    starts.addLast(now);
                    return;
                }
                final long wait = starts.getFirst() + windowNanos - now;
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
