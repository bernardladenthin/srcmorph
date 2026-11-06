// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class RequestRateLimiterTest {

    private static final long WINDOW = 1000;

    /** The fake clock's time. */
    private long now;

    /** Every wait the limiter asked for, in order. */
    private final List<Long> waits = new ArrayList<>();

    /** A limiter on the fake clock whose waits pass the asked time, or the next of {@code advances} when one is left. */
    private RequestRateLimiter limiter(final long... advances) {
        final int[] next = {0};
        return new RequestRateLimiter("http://gateway K", WINDOW, () -> now, nanos -> {
            waits.add(nanos);
            now += next[0] < advances.length ? advances[next[0]++] : nanos;
        });
    }

    // <editor-fold defaultstate="collapsed" desc="acquire">
    @Test
    public void acquire_belowTheLimit_doesNotWait() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter();

        // act
        limiter.acquire(2);
        now = 10;
        limiter.acquire(2);

        // assert
        assertThat(waits.isEmpty(), is(true));
        assertThat(limiter.startedInWindow(), is(2));
    }

    @Test
    public void acquire_atTheLimit_waitsUntilTheOldestStartLeavesTheWindow() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter();
        limiter.acquire(2);
        now = 10;
        limiter.acquire(2);
        now = 20;

        // act
        limiter.acquire(2);

        // assert
        assertThat(waits, is(Arrays.asList(WINDOW - 20)));
        assertThat(now, is(WINDOW));
        // the start at 0 left the window, the ones at 10 and at WINDOW are in it
        assertThat(limiter.startedInWindow(), is(2));
    }

    @Test
    public void acquire_aStartExactlyOneWindowOld_hasLeftIt() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter();
        limiter.acquire(1);

        now = WINDOW - 1;

        // pre-assert
        assertThat(limiter.startedInWindow(), is(1));

        // act
        now = WINDOW;
        limiter.acquire(1);

        // assert
        assertThat(waits.isEmpty(), is(true));
    }

    @Test
    public void acquire_wokenEarly_waitsForTheRest() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter(WINDOW / 4);
        limiter.acquire(1);

        // act
        limiter.acquire(1);

        // assert
        assertThat(waits, is(Arrays.asList(WINDOW, WINDOW - WINDOW / 4)));
        assertThat(now, is(WINDOW));
    }

    @Test
    public void acquire_interrupted_throwsAndKeepsTheInterrupt() throws Exception {
        // arrange
        final InterruptedException cause = new InterruptedException("stop");
        final RequestRateLimiter limiter = new RequestRateLimiter("http://gateway K", WINDOW, () -> now, nanos -> {
            throw cause;
        });
        limiter.acquire(1);

        // act
        final InterruptedIOException thrown = assertThrows(InterruptedIOException.class, () -> limiter.acquire(1));

        // assert
        assertThat(Thread.interrupted(), is(true));
        assertThat(thrown.getMessage(), is("Interrupted while holding a request to http://gateway K"));
        assertThat(thrown.getCause(), is(sameInstance(cause)));
        assertThat(limiter.startedInWindow(), is(1));
    }

    @Test
    public void acquire_waiting_logsTheServerTheWaitAndTheLimit() throws Exception {
        // arrange
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(RequestRateLimiter.class);
        final Level previous = logger.getLevel();
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);
        try {
            final long second = TimeUnit.SECONDS.toNanos(1);
            final RequestRateLimiter limiter =
                    new RequestRateLimiter("http://gateway K", 3 * second, () -> now, nanos -> now += nanos);
            limiter.acquire(1);
            now = second / 2;

            // act
            limiter.acquire(1);

            // assert
            assertThat(appender.list.size(), is(1));
            assertThat(
                    appender.list.get(0).getFormattedMessage(),
                    is("Holding the next request to http://gateway K for 2.5 s: 1 requests started in the last"
                            + " minute (requestsPerMinute 1)"));
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(previous);
            appender.stop();
        }
    }

    /**
     * Without a test waiter the limiter waits on its condition for real; a 50 ms window keeps it short.
     *
     * @throws Exception if the wait is interrupted
     */
    @Test
    public void acquire_realWaiter_waitsTheWindowOut() throws Exception {
        // arrange
        final long window = TimeUnit.MILLISECONDS.toNanos(50);
        final RequestRateLimiter limiter = new RequestRateLimiter("http://gateway K", window, System::nanoTime, null);
        limiter.acquire(1);
        final long start = System.nanoTime();

        // act
        limiter.acquire(1);

        // assert
        assertThat(System.nanoTime() - start, is(greaterThanOrEqualTo(window * 9 / 10)));
    }

    /**
     * A later run of the same JVM that names a higher limit sends at it: the earlier, lower limit is
     * the earlier run's, not the server's.
     *
     * @throws Exception if a wait is interrupted
     */
    @Test
    public void acquire_aHigherLimitAfterALowerOne_startsAtTheHigherLimit() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter();
        limiter.acquire(1);

        // act
        limiter.acquire(3);
        limiter.acquire(3);

        // assert
        assertThat(waits.isEmpty(), is(true));
        assertThat(limiter.startedInWindow(), is(3));
    }

    /**
     * A request at a lower limit counts the requests of every model of the server in the window, not
     * only its own.
     *
     * @throws Exception if a wait is interrupted
     */
    @Test
    public void acquire_aLowerLimitAfterAHigherOne_countsTheOtherRequestsToo() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter();
        limiter.acquire(5);
        limiter.acquire(5);
        limiter.acquire(5);
        now = 10;

        // act
        limiter.acquire(2);

        // assert: the three starts at 0 had to leave the window first
        assertThat(waits, is(Arrays.asList(WINDOW - 10)));
        assertThat(limiter.startedInWindow(), is(1));
    }

    @Test
    public void acquire_limitBelowOne_throws() {
        // arrange
        final RequestRateLimiter limiter = limiter();

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> limiter.acquire(0));

        // assert
        assertThat(thrown.getMessage(), is("requestsPerMinute must be positive: 0"));
        assertThat(limiter.startedInWindow(), is(0));
    }

    @Test
    public void acquire_limitOfOne_startsTheFirstRequest() throws Exception {
        // arrange
        final RequestRateLimiter limiter = limiter();

        // act
        limiter.acquire(1);

        // assert
        assertThat(waits.isEmpty(), is(true));
        assertThat(limiter.startedInWindow(), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="shared">
    /** Every model of one server shares one window; another key variable is another window. */
    @Test
    public void shared_oneWindowPerServer() {
        // arrange
        final RequestRateLimiter first = RequestRateLimiter.shared("http://shared-test K");

        // act, assert
        assertThat(RequestRateLimiter.shared("http://shared-test K"), is(sameInstance(first)));
        assertThat(RequestRateLimiter.shared("http://shared-test L"), is(not(sameInstance(first))));
        assertThat(first.toString(), containsString("server=http://shared-test K"));
    }

    @Test
    public void shared_defaultWindow_countsAMinuteOnTheSystemClock() throws Exception {
        // arrange
        final RequestRateLimiter limiter = RequestRateLimiter.shared("http://shared-clock-test K");

        // act
        limiter.acquire(2);

        // assert
        assertThat(limiter.startedInWindow(), is(1));
        assertThat(RequestRateLimiter.WINDOW_NANOS, is(TimeUnit.MINUTES.toNanos(1)));
        assertThat(limiter.toString(), containsString("windowNanos=" + RequestRateLimiter.WINDOW_NANOS));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_nullArguments_throw() {
        // act, assert
        assertThrows(NullPointerException.class, () -> new RequestRateLimiter(null, WINDOW, () -> 0, null));
        assertThrows(NullPointerException.class, () -> new RequestRateLimiter("s", WINDOW, null, null));
        assertThat(new RequestRateLimiter("s", WINDOW, () -> 0, null), is(instanceOf(RequestRateLimiter.class)));
    }
    // </editor-fold>
}
