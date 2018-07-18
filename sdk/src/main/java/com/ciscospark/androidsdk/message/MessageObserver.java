package com.ciscospark.androidsdk.message;

/**
 * The struct of a message event
 * @since 1.4.0
 */
public interface MessageObserver {

    /**
     *
     */
    abstract class MessageEvent {
    }

    /**
     * The struct of a new message received event
     * @since 1.4.0
     */
    class MessageArrived extends  MessageEvent {
        private Message message;

        public MessageArrived(Message message) {
            this.message = message;
        }
        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    /**
     * The struct of a message delete event
     * @since 1.4.0
     */
    class MessageDeleted extends MessageEvent {
        private String messageId;

        public MessageDeleted(String messageId) {
            this.messageId = messageId;
        }

        public String getMessageId() {
            return messageId;
        }
    }

    /**
     * Call back when message arrived.
     * @param event     Message event
     */
    void onEvent(MessageEvent event);
}
