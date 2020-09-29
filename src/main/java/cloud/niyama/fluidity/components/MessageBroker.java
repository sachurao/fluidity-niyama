package cloud.niyama.fluidity.components;

import java.util.function.Consumer;

public interface MessageBroker {
    void createTopic(String topic, TopicConfiguration config);
    <E> void submit(String topic, E e);
    <E> void subscribe(String topic, Consumer<E> consumer);
    <E> void unsubscribe(String topic, Consumer<E> consumer);
    void deleteTopic(String topic);
}
