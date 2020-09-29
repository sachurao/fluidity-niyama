package cloud.niyama.fluidity.protagonists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//TODO: Tackle ConcurrentModificationException.
//TODO: Ensure execution uses the provided executor properly.

/**
 * Forwarding consumer forwards an incoming item to the consumer chain,
 * based on the defined forwarding algorithm (fan-out, round-robin, etc.).
 * @param <E>
 */
public class ForwardingConsumer<E> implements BiConsumer<E, Executor>, Consumer<E> {
    private static Logger LOG = LoggerFactory.getLogger(ForwardingConsumer.class);
    private final ForwardingAlgorithm forwardingAlgorithm;
    private final BlockingQueue<Consumer<E>> consumerQueue = new LinkedBlockingQueue<>();
    public ForwardingConsumer(ForwardingAlgorithm forwardingModel) {
        forwardingAlgorithm = forwardingModel;
    }

    public ForwardingConsumer() {
        this(ForwardingAlgorithm.FAN_OUT);
    }

    public void addConsumer(Consumer<E> consumer) {
        consumerQueue.add(consumer);
    }

    public<E> void removeConsumer(Consumer<E> consumer) {
        consumerQueue.removeIf(c -> c.equals(consumer));
    }

    @Override
    public void accept(E e, Executor executor) {
        if (forwardingAlgorithm == ForwardingAlgorithm.ROUND_ROBIN) {
            forwardToConsumers(1, e, executor);
        }
        else if (forwardingAlgorithm == ForwardingAlgorithm.FAN_OUT) {
            forwardToConsumers(consumerQueue.size(), e, executor);
        }
    }

    @Override
    public void accept(E e) {
        this.accept(e, ForkJoinPool.commonPool());
    }

    private void forwardToConsumers(int totalConsumers, E e, Executor executor) {
        int queueSize = consumerQueue.size();
        if (totalConsumers > queueSize) totalConsumers = queueSize;
        for (int i = 0; i < totalConsumers; i++) {
            try {
                Consumer<E> consumer = consumerQueue.take();
                executor.execute(() -> {
                    try {
                        consumer.accept(e);
                    } finally {
                        consumerQueue.offer(consumer);
                    }
                });
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }
}

