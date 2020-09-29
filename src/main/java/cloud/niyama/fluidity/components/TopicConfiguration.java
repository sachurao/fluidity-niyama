package cloud.niyama.fluidity.components;




import cloud.niyama.fluidity.protagonists.ForwardingAlgorithm;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class TopicConfiguration {
    private final int bufferCapacity;
    private final Executor executor;
    private final ForwardingAlgorithm forwardingAlgorithm;

    public TopicConfiguration(int bufferCapacity, Executor executor, ForwardingAlgorithm forwardingAlgorithm) {
        this.bufferCapacity = bufferCapacity;
        this.executor = executor;
        this.forwardingAlgorithm = forwardingAlgorithm;
    }

    public TopicConfiguration() {
        this(256, ForkJoinPool.commonPool(), ForwardingAlgorithm.FAN_OUT);
    }

    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public Executor getExecutor() {
        return executor;
    }

    public ForwardingAlgorithm getForwardingAlgorithm() {
        return forwardingAlgorithm;
    }
}
