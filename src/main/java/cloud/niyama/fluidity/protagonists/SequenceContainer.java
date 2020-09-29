package cloud.niyama.fluidity.protagonists;


import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiConsumer;

/**
 * This is a "reactive" sequence.
 * @param <E>
 */
public class SequenceContainer<E> {
    private final SubmissionPublisher<E> sequence;

    public SequenceContainer() {
        sequence = new SubmissionPublisher<>();
    }

    public SequenceContainer(int maxBufferCapacity)  {
        sequence = new SubmissionPublisher<>(ForkJoinPool.commonPool(), maxBufferCapacity);
    }

    public SequenceContainer(Executor executor, int maxBufferCapacity) {
        sequence = new SubmissionPublisher<>(executor, maxBufferCapacity);
    }

    public SequenceContainer(Executor executor, int maxBufferCapacity,
                             BiConsumer<Flow.Subscriber, Throwable> consumer) {
        sequence = new SubmissionPublisher<>(executor, maxBufferCapacity, consumer);
    }

    public void submit(E e) {
        sequence.submit(e);
    }

    public void subscribe(Flow.Subscriber<E> subscriber) {
        sequence.subscribe(subscriber);
    }

    public void close() {
        sequence.close();
    }
}
