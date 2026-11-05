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
    private RequestRateLimiter limiter(final int perMinute, final long... advances) {
        final int[] next = {0};
        return new RequestRateLimiter("http://gateway K", perMinute, WINDOW, () -> now, nanos -> {
            waits.add(nanos);
            now += next[0] < advances.length ? advances[next[0]++] : nanos;
        });
    }

    // <editor-fold defaultstate="collapsed" desc="acquire">
    @Test
    public void acquire_belowTheLimit_doesNotWait() throws Exception {
        final RequestRateLimiter limiter = limiter(2);

        limiter.acquire();
        now = 10;
        limiter.acquire();

        assertThat(waits.isEmpty(), is(true));
        assertThat(limiter.startedInWindow(), is(2));
    }

    @Test
    public void acquire_atTheLimit_waitsUntilTheOldestStartLeavesTheWindow() throws Exception {
        final RequestRateLimiter limiter = limiter(2);
        limiter.acquire();
        now = 10;
        limiter.acquire();
        now = 20;

        limiter.acquire();

        assertThat(waits, is(Arrays.asList(WINDOW - 20)));
        assertThat(now, is(WINDOW));
        // the start at 0 left the window, the ones at 10 and at WINDOW are in it
        assertThat(limiter.startedInWindow(), is(2));
    }

    @Test
    public void acquire_aStartExactlyOneWindowOld_hasLeftIt() throws Exception {
        final RequestRateLimiter limiter = limiter(1);
        limiter.acquire();

        now = WINDOW - 1;
        assertThat(limiter.startedInWindow(), is(1));
        now = WINDOW;
        limiter.acquire();

        assertThat(waits.isEmpty(), is(true));
    }

    @Test
    public void acquire_wokenEarly_waitsForTheRest() throws Exception {
        final RequestRateLimiter limiter = limiter(1, WINDOW / 4);
        limiter.acquire();

        limiter.acquire();

        assertThat(waits, is(Arrays.asList(WINDOW, WINDOW - WINDOW / 4)));
        assertThat(now, is(WINDOW));
    }

    @Test
    public void acquire_interrupted_throwsAndKeepsTheInterrupt() throws Exception {
        final InterruptedException cause = new InterruptedException("stop");
        final RequestRateLimiter limiter = new RequestRateLimiter("http://gateway K", 1, WINDOW, () -> now, nanos -> {
            throw cause;
        });
        limiter.acquire();

        final InterruptedIOException thrown = assertThrows(InterruptedIOException.class, limiter::acquire);

        assertThat(Thread.interrupted(), is(true));
        assertThat(thrown.getMessage(), is("Interrupted while holding a request to http://gateway K"));
        assertThat(thrown.getCause(), is(sameInstance(cause)));
        assertThat(limiter.startedInWindow(), is(1));
    }

    @Test
    public void acquire_waiting_logsTheServerTheWaitAndTheLimit() throws Exception {
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(RequestRateLimiter.class);
        final Level previous = logger.getLevel();
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);
        try {
            final long second = TimeUnit.SECONDS.toNanos(1);
            final RequestRateLimiter limiter =
                    new RequestRateLimiter("http://gateway K", 1, 3 * second, () -> now, nanos -> now += nanos);
            limiter.acquire();
            now = second / 2;

            limiter.acquire();

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
        final long window = TimeUnit.MILLISECONDS.toNanos(50);
        final RequestRateLimiter limiter =
                new RequestRateLimiter("http://gateway K", 1, window, System::nanoTime, null);
        limiter.acquire();
        final long start = System.nanoTime();

        limiter.acquire();

        assertThat(System.nanoTime() - start, is(greaterThanOrEqualTo(window * 9 / 10)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="shared">
    /** Two models of one server with 30 and 20 requests a minute share one window at 20, not two (50). */
    @Test
    public void shared_oneWindowPerServer_atTheSmallestLimit() {
        final RequestRateLimiter first = RequestRateLimiter.shared("http://shared-test K", 30);

        assertThat(RequestRateLimiter.shared("http://shared-test K", 31), is(sameInstance(first)));
        assertThat(first.toString(), containsString("perMinute=30"));
        assertThat(RequestRateLimiter.shared("http://shared-test K", 20), is(sameInstance(first)));
        assertThat(first.toString(), containsString("perMinute=20"));
        assertThat(RequestRateLimiter.shared("http://shared-test L", 30), is(not(sameInstance(first))));
    }

    @Test
    public void lowerLimitTo_aSmallerLimit_holdsTheNextRequest_aLargerOneNot() throws Exception {
        // arrange: three requests may start; then a larger limit, then a smaller one
        final List<Long> waits = new ArrayList<>();
        final RequestRateLimiter limiter = new RequestRateLimiter("http://gateway K", 3, WINDOW, () -> 0L, nanos -> {
            waits.add(nanos);
            throw new InterruptedException("stop");
        });
        limiter.lowerLimitTo(5);
        limiter.acquire();
        limiter.acquire();

        // act
        limiter.lowerLimitTo(2);

        // assert: the third request is held at 2, where 3 (or the 5) would have let it start
        assertThrows(InterruptedIOException.class, limiter::acquire);
        assertThat(waits.size(), is(1));
        assertThat(limiter.startedInWindow(), is(2));
    }

    @Test
    public void shared_countsAMinuteOnTheSystemClock() throws Exception {
        final RequestRateLimiter limiter = RequestRateLimiter.shared("http://shared-clock-test K", 2);

        limiter.acquire();

        assertThat(limiter.startedInWindow(), is(1));
        assertThat(RequestRateLimiter.WINDOW_NANOS, is(TimeUnit.MINUTES.toNanos(1)));
        assertThat(limiter.toString(), containsString("perMinute=2"));
        assertThat(limiter.toString(), containsString("windowNanos=" + RequestRateLimiter.WINDOW_NANOS));
    }

    @Test
    public void constructor_nullArguments_throw() {
        assertThrows(NullPointerException.class, () -> new RequestRateLimiter(null, 1, WINDOW, () -> 0, null));
        assertThrows(NullPointerException.class, () -> new RequestRateLimiter("s", 1, WINDOW, null, null));
        assertThat(new RequestRateLimiter("s", 1, WINDOW, () -> 0, null), is(instanceOf(RequestRateLimiter.class)));
    }
    // </editor-fold>
}
