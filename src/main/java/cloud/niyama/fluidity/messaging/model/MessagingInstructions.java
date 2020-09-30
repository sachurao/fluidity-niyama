package cloud.niyama.fluidity.messaging.model;

import cloud.niyama.fluidity.messaging.ondemand.MessageBroker;
import cloud.niyama.fluidity.messaging.ondemand.MessageProducer;
import cloud.niyama.fluidity.messaging.ondemand.MessageSubscriber;

public class MessagingInstructions {
    public static class RegisterMessageBroker {
        private final MessageBroker messageBroker;

        public RegisterMessageBroker(MessageBroker messageBroker) {
            this.messageBroker = messageBroker;
        }

        public MessageBroker getMessageBroker() {
            return messageBroker;
        }
    }

    public static  class SendMessage<E> {
        private final String messageKey;
        private final Message<E> message;

        public SendMessage(String messageKey, Message<E> message) {
            this.messageKey = messageKey;
            this.message = message;
        }

        public String getMessageKey() {
            return messageKey;
        }

        public Message<E> getMessage() {
            return message;
        }
    }

    public static  class RegisterMessageSubscriber {
        private final String messageKey;
        private final MessageSubscriber messageSubscriber;


        public RegisterMessageSubscriber(String messageKey, MessageSubscriber messageSubscriber) {
            this.messageKey = messageKey;
            this.messageSubscriber = messageSubscriber;
        }

        public String getMessageKey() {
            return messageKey;
        }

        public MessageSubscriber getMessageSubscriber() {
            return messageSubscriber;
        }
    }

    public static  class RegisterMessageProducer {
        private final String messageKey;
        private final MessageProducer messageProducer;


        public RegisterMessageProducer(String messageKey, MessageProducer messageProducer) {
            this.messageKey = messageKey;
            this.messageProducer = messageProducer;
        }

        public String getMessageKey() {
            return messageKey;
        }

        public MessageProducer getMessageProducer() {
            return messageProducer;
        }
    }

    public static  class RequestNextMessage {
        private final String messageKey;

        public RequestNextMessage(String messageKey) {
            this.messageKey = messageKey;
        }

        public String getMessageKey() {
            return messageKey;
        }
    }
}
