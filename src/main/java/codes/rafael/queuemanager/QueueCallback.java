package codes.rafael.queuemanager;

import java.util.Queue;

public interface QueueCallback<T> {

    void onInsert(T element, Queue<? super T> queue);

    void onTimeout(T element, Queue<? super T> queue);

    void onError(T element, Queue<? super T> queue, Throwable throwable);
}
