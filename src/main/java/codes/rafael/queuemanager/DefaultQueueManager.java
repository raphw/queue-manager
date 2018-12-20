package codes.rafael.queuemanager;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DefaultQueueManager implements QueueManager, Runnable {

    private final Collection<QueueHandle<?>> handles = Collections.newSetFromMap(new ConcurrentHashMap<QueueHandle<?>, Boolean>());

    public static ScheduledFuture<?> schedule(ScheduledExecutorService scheduledExecutorService, long interval, TimeUnit timeUnit) {
        return scheduledExecutorService.scheduleAtFixedRate(new DefaultQueueManager(), interval, interval, timeUnit);
    }

    public <T> boolean register(T element, Queue<? super T> queue) {
        return register(element, queue, null);
    }

    public <T> boolean register(T element, Queue<? super T> queue, QueueCallback<T> callback) {
        return register(element, queue, callback, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public <T> boolean register(T element, Queue<? super T> queue, QueueCallback<T> callback, long timeout, TimeUnit timeUnit) {
        QueueHandle<T> handle = new QueueHandle<T>(element, queue, callback, timeUnit.toMillis(timeout));
        if (handle.trial()) {
            return true;
        } else {
            handles.add(handle);
            return false;
        }
    }

    public void run() {
        Iterator<QueueHandle<?>> iterator = handles.iterator();
        while (iterator.hasNext()) {
            QueueHandle<?> instruction = iterator.next();
            if (instruction.trial()) {
                iterator.remove();
            }
        }
    }
}
