package cloud.niyama.fluidity.components;



import cloud.niyama.fluidity.protagonists.ForwardingConsumer;
import cloud.niyama.fluidity.protagonists.SequenceContainer;
import cloud.niyama.fluidity.protagonists.SequenceSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * This is a topic-based message broker for reactive producers and consumers.
 * It supports multiple topics, multiple producers and multiple consumers.
 * It supports fan-out and round-robin capabilities for consumers.
 */
public class FluidMessageBroker implements MessageBroker, ControllableComponent {

    private static Logger LOG = LoggerFactory.getLogger(FluidMessageBroker.class);
    private final Map<String, ReactivePair> map
            = new ConcurrentHashMap<String, ReactivePair>();
    private AtomicBoolean isActive = new AtomicBoolean(false);
    @Override
    public void createTopic(String topic, TopicConfiguration config) {
        checkActivation();
        if (map.containsKey(topic)) throw new IllegalArgumentException("Topic already exists: "+topic);
        map.put(topic, createReactivePair(config));
    }


    @Override
    public <E> void submit(String topic, E e) {
        checkActivation();
        ReactivePair<E> rp = map.get(topic);
        if (rp == null) raiseIllegalArgException(topic);
        rp.getSequenceContainer().submit(e);
    }

    @Override
    public <E> void subscribe(String topic, Consumer<E> consumer) {
        checkActivation();
        ReactivePair<E> rp = map.get(topic);
        if (rp == null) raiseIllegalArgException(topic);
        rp.getForwardingConsumer().addConsumer(consumer);
    }

    @Override
    public <E> void unsubscribe(String topic, Consumer<E> consumer) {
        checkActivation();
        ReactivePair<E> rp = map.get(topic);
        if (rp == null) raiseIllegalArgException(topic);
        rp.getForwardingConsumer().removeConsumer(consumer);
    }

    @Override
    public void deleteTopic(String topic) {
        checkActivation();
        ReactivePair rp = map.remove(topic);
        if (rp!=null) {
            rp.getSequenceContainer().close();

        }
    }

    private void raiseIllegalArgException(String topic) {
        if (map.containsKey(topic)) throw new IllegalArgumentException(
                "Topic does not exist.  Please call createTopic first: "+topic);

    }

    private void checkActivation() {
        if (!isActive.get()) throw new IllegalStateException("Please start the message broker first.");
    }

    private <E> ReactivePair<E> createReactivePair(TopicConfiguration config) {
        SequenceContainer<E> seqContainer = new SequenceContainer(
                config.getExecutor(), config.getBufferCapacity());
        ForwardingConsumer<E> forwardingConsumer = new ForwardingConsumer(config.getForwardingAlgorithm());
        seqContainer.subscribe(new SequenceSubscriber(forwardingConsumer));
        ReactivePair<E> reactivePair = new ReactivePair<>(seqContainer, forwardingConsumer);
        return reactivePair;
    }

    @Override
    public void start() {
        isActive.compareAndSet(false, true);
    }

    @Override
    public void shutdown() {
        this.map.forEach((k, rp) -> rp.getSequenceContainer().close());
    }

    private class ReactivePair<E> {
        private final SequenceContainer<E> sequenceContainer;
        private final ForwardingConsumer<E> forwardingConsumer;


        ReactivePair(SequenceContainer<E> sequenceContainer, ForwardingConsumer<E> forwardingConsumer) {
            this.sequenceContainer = sequenceContainer;
            this.forwardingConsumer = forwardingConsumer;
        }


        public ForwardingConsumer<E> getForwardingConsumer() {
            return forwardingConsumer;
        }

        public SequenceContainer<E> getSequenceContainer() {
            return sequenceContainer;
        }
    }
}
