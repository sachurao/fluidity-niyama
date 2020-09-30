package cloud.niyama.fluidity.messaging.model;

public class Message<E> {
    private final E content;

    public Message(E content) {
        this.content = content;
    }

    public E getContent() {
        return content;
    }
}
