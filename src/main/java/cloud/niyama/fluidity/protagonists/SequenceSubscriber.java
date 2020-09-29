package cloud.niyama.fluidity.protagonists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class SequenceSubscriber<E> implements Flow.Subscriber<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceSubscriber.class);
    private Flow.Subscription subscription;
    private final Consumer<E> consumer;

    public SequenceSubscriber(Consumer<E> consumer) {
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void onNext(E item) {
        LOGGER.info("Received next item");
        try {
            //Do something with item;
            consumer.accept(item);
        } finally {
            if (subscription!=null) subscription.request(1);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.error("Experienced an error: ", throwable);
    }

    @Override
    public void onComplete() {
        LOGGER.info("We are done here!");
        if (subscription!=null) subscription.cancel();
    }
}
