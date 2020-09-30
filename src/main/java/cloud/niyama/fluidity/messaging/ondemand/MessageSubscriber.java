package cloud.niyama.fluidity.messaging.ondemand;

import java.util.concurrent.Executor;

import static cloud.niyama.fluidity.messaging.model.MessagingInstructions.*;

public abstract class MessageSubscriber<E> extends MessagingParticipant {
    private MessageBroker messageBroker;

    protected MessageSubscriber() {
        super();
    }

    protected MessageSubscriber(Executor executor) {
        super(executor);
    }

    @Override
    protected void executeInstruction(Object instruction) {
        if (instruction instanceof RegisterMessageBroker) {
            var registerMsgBroker = (RegisterMessageBroker) instruction;
            messageBroker = registerMsgBroker.getMessageBroker();
        }
        if (instruction instanceof SendMessage) {
            var sendMsgInstruction = (SendMessage<E>) instruction;
            this.onMessageReceived(sendMsgInstruction.getMessage().getContent());
        }
    }

    protected abstract <E> void onMessageReceived(E messageContent);

}
