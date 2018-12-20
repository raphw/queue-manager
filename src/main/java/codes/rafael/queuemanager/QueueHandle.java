package codes.rafael.queuemanager;

import java.util.Queue;

class QueueHandle<T> {

    private final T element;

    private final Queue<? super T> queue;

    private final QueueCallback<T> callback;

    private final long expiration;

    QueueHandle(T element, Queue<? super T> queue, QueueCallback<T> callback, long timeout) {
        this.queue = queue;
        this.element = element;
        this.callback = callback;
        expiration = timeout == Long.MAX_VALUE ? Long.MAX_VALUE : timeout + System.currentTimeMillis();
    }

    boolean trial() {
        try {
            if (queue.offer(element)) {
                if (callback != null) {
                    callback.onInsert(element, queue);
                }
                return true;
            } else if (expiration != Long.MAX_VALUE && System.currentTimeMillis() > expiration) {
                if (callback != null) {
                    callback.onTimeout(element, queue);
                }
                return true;
            } else {
                return false;
            }
        } catch (Throwable throwable) {
            try {
                if (callback != null) {
                    callback.onError(element, queue, throwable);
                }
            } catch (Throwable ignored) {
            }
            return true;
        }
    }
}
