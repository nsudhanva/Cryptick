package sudhanva.narayana.cryptick.model;

public class MessageChatModel {

    private String message;
    private String recipient;
    private String sender;
    private String tick;

    private int mRecipientOrSenderStatus;

    /* Setter */

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTick(){ return tick; }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String givenRecipient) {
        recipient = givenRecipient;
    }


    /* Getter */

    public String getSender() {
        return sender;
    }

    public void setSender(String givenSender) {
        sender = givenSender;
    }

    public int getRecipientOrSenderStatus() {
        return mRecipientOrSenderStatus;
    }

    public void setRecipientOrSenderStatus(int recipientOrSenderStatus) {
        this.mRecipientOrSenderStatus = recipientOrSenderStatus;
    }
}
