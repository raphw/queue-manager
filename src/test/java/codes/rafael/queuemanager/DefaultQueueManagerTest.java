package codes.rafael.queuemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import static junit.framework.TestCase.fail;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class DefaultQueueManagerTest {

    private Queue<String> queue;

    private DefaultQueueManager manager;

    @Before
    public void setUp() {
        queue = new ArrayBlockingQueue<String>(1);
        manager = new DefaultQueueManager();
    }

    @Test
    public void testManagerAddsToQueue() {
        assertThat(manager.register("foo", queue), is(true));
    }

    @Test
    public void testManagerAddsToQueueIfAvailable() {
        assertThat(queue.add("foo"), is(true));
        assertThat(manager.register("bar", queue), is(false));
        manager.run();
        queue.clear();
        manager.run();
        assertThat(queue.size(), is(1));
        assertThat(queue.remove(), is("bar"));
    }

    @Test
    public void testManagerCallsCallback() {
        assertThat(queue.add("foo"), is(true));
        assertThat(manager.register("bar", queue, new QueueCallback<String>() {
            @Override
            public void onInsert(String element, Queue<? super String> queue) {
                assertThat(element, is("bar"));
                assertThat(queue.remove(), is((Object) "bar"));
            }

            @Override
            public void onTimeout(String element, Queue<? super String> queue) {
                fail();
            }

            @Override
            public void onError(String element, Queue<? super String> queue, Throwable throwable) {
                fail();
            }
        }), is(false));
        queue.clear();
        manager.run();
        assertThat(queue.size(), is(0));
    }

    @Test
    public void testManagerCancelsOnTimeout() throws Exception {
        assertThat(queue.add("foo"), is(true));
        assertThat(manager.register("bar", queue, null, 1, TimeUnit.MILLISECONDS), is(false));
        Thread.sleep(100);
        manager.run();
        queue.clear();
        manager.run();
        assertThat(queue.size(), is(0));
    }
}