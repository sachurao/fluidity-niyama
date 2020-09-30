package cloud.niyama.fluidity.messaging.ondemand;

import cloud.niyama.fluidity.messaging.model.Message;

import java.util.concurrent.Executor;

import static cloud.niyama.fluidity.messaging.model.MessagingInstructions.*;

public abstract class MessageProducer<E> extends MessagingParticipant {

    private MessageBroker messageBroker;

    protected MessageProducer() {
        super();
    }

    protected MessageProducer(Executor executor) {
        super(executor);
    }

    @Override
    protected void executeInstruction(Object instruction) {
        if (instruction instanceof RegisterMessageBroker) {
            var registerMsgBroker = (RegisterMessageBroker) instruction;
            messageBroker = registerMsgBroker.getMessageBroker();
        }
        if (instruction instanceof RequestNextMessage) {
            if (messageBroker!=null) {
                var askNextMsgInstruction = (RequestNextMessage) instruction;
                E nextMessageContent = produceNextMessageContent();
                var message = new Message<E>(nextMessageContent);
                messageBroker.instruct(new SendMessage<E>(askNextMsgInstruction.getMessageKey(), message));
            }
        }
    }

    abstract E produceNextMessageContent();
}
