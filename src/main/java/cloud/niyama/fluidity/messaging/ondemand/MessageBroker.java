package cloud.niyama.fluidity.messaging.ondemand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static cloud.niyama.fluidity.messaging.model.MessagingInstructions.*;

public class MessageBroker extends MessagingParticipant {
    private final Map<String, MessageSubscriber> subscriberMap = new ConcurrentHashMap<>();
    private final Map<String, MessagingParticipant> producerMap = new ConcurrentHashMap<>();

    public MessageBroker() {
        super();
    }

    public MessageBroker(Executor executor) {
        super(executor);
    }

    @Override
    protected void executeInstruction(Object instruction) {
        if (instruction instanceof RegisterMessageSubscriber) {
            var registration = (RegisterMessageSubscriber) instruction;
            subscriberMap.put(registration.getMessageKey(), registration.getMessageSubscriber());
            registration.getMessageSubscriber().instruct(new RegisterMessageBroker(this));
        }
        if (instruction instanceof RegisterMessageProducer) {
            var sendMsgInstruction
                    = (RegisterMessageProducer) instruction;
            producerMap.put(sendMsgInstruction.getMessageKey(), sendMsgInstruction.getMessageProducer());
            sendMsgInstruction.getMessageProducer().instruct(new RegisterMessageBroker(this));
        }
        if (instruction instanceof RequestNextMessage) {
            var askNextMsgInstruction
                    = (RequestNextMessage) instruction;
            var messageProducer = this.producerMap.get(askNextMsgInstruction.getMessageKey());
            if (messageProducer!=null) {
                messageProducer.instruct(askNextMsgInstruction);
            }
        }
        if (instruction instanceof SendMessage) {
            var sendMsgInstruction = (SendMessage) instruction;
            var consumer = this.subscriberMap.get(sendMsgInstruction.getMessageKey());
            if (consumer!=null) {
                consumer.instruct(sendMsgInstruction);
            }
        }
    }
}
