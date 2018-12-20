package codes.rafael.queuemanager;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public interface QueueManager {

    <T> boolean register(T element, Queue<? super T> queue, QueueCallback<T> callback);

    <T> boolean register(T element, Queue<? super T> queue, QueueCallback<T> callback, long timeout, TimeUnit timeUnit);
}
