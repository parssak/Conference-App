package UseCases;

import Entities.Conversation;
import Entities.Message;
import Entities.User;
import Gateways.JsonDatabase;
import Util.UserType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The use case class for messaging.
 * <p>
 * Maps usernames to lists of conversations. Conversations are mutable objects; user1 and user2 would share
 * the same conversation instance between them. Any two users can only have one conversations between them.
 *
 * @author Alex
 */
public class MessageManager implements Serializable {
    private JsonDatabase<Conversation> messageDatabase;
    private AccountManager accountManager;

    /**
     * Constructor.
     *
     */
    public MessageManager() {
        this.accountManager = new AccountManager();
        messageDatabase = new JsonDatabase("Messages", Conversation.class);
    }

    /**
     * If participants of this message have a conversation, adds message to conversation. If not,
     * makes a new conversation for sender and recipient and adds message to it.
     *
     * @param sender sender of message
     * @param recipient recipient of message
     * @param messageBody body of message
     * @return boolean if sent
     */
    public boolean sendMessage(String sender, String recipient, String messageBody) {
        boolean hasMessaged = hasMessaged(sender, recipient);
        if (!hasMessaged) newConversation(sender, recipient);
        Message message = new Message(sender, recipient, messageBody);
        getConversationThread(sender, recipient).add(message);

        return true;
    }

    /**
     * Checks if user1 and user2 had a conversation. Does not matter if users exists or not.
     *
     * @param user1
     * @param user2
     * @return true iff user1 and user2 have a conversation
     */
    private boolean hasMessaged(String user1, String user2) {
        List<String> conversations = messageDatabase.getIds();
        for (String c : conversations) {
            String u1 = c.substring(0, c.indexOf("-"));
            if (u1 == user1 || u1 == user2){
                String u2 = c.substring(c.indexOf("-")+1);
                if (u2 == user1 || u2 == user2){
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Returns a list of all usernames this user can message.
     *
     * @param user
     * @return list of usernames
     */
    public List<String> getContacts(String user) {
        return accountManager.getUsernames().stream()
                .filter(s -> canMessage(user, s) && s != user)
                .collect(Collectors.toList());
    }


    /**
     * Gets the messages between user1 and user2 as a list of formatted strings in the format:
     * "[ username ] body"
     * <p>
     * Note: getMessages(user1, user2) is equivalent to getMessages(user2, user1)
     *
     * @param user1
     * @param user2
     * @return list of formatted strings; empty string if there is no conversation.
     */
    public ArrayList<Message> getMessages(String user1, String user2) {
        return (ArrayList<Message>) getConversationThread(user1, user2);
    }

    /**
     * Searches conversations of user1 to see if there is a conversation with user2.
     *
     * @param user1
     * @param user2
     * @return the conversation between user1 and user2; null if user1 or user2 DNE in db, or there's no conversation.
     */
    private List<Message> getConversationThread(String user1, String user2) {
        List<String> conversations = messageDatabase.getIds();
        for (String c : conversations) {
            String u1 = c.substring(0, c.indexOf("-"));
            if (u1 == user1 || u1 == user2){
                String u2 = c.substring(c.indexOf("-")+1);
                if (u2 == user1 || u2 == user2){
                    return messageDatabase.read(c).getMessages();
                }
            }
        }
        return null;
    }

    /**
     * Creates a conversation between two users and puts it in the list of their conversations.
     *
     * <b>This should be used iff user1 and user2 don't have conversation (i.e., messaging for the
     * first time).</b>
     * <p>
     * Both users share the same conversation object; it is mutable.
     *
     * @param user1 user 1
     * @param user2 user 2
     */
    private void newConversation(String user1, String user2) {
        Conversation conversation = new Conversation(user1, user2);
        messageDatabase.write(conversation, user1+"-"+user2);
    }

    /**
     * Returns of a list of usernames this person has messaged.
     *
     * @param user username
     * @return list of usernames; empty list if user not in database
     */
    public ArrayList<String> getMyInbox(String user) {
        List<String> conversations = messageDatabase.getIds();
        ArrayList<String> myInbox = new ArrayList<>();
        for (String c : conversations) {
            String otherUser = c;
            otherUser.replace("-","");
            otherUser.replace(user,"");
            if (c.contains(user)) myInbox.add(otherUser);
        }
        return myInbox;
    }

    /**
     * Checks if the conversation has been read.
     * @param messages
     * @return true if the conversation is read, otherwise, false if not read.
     */
    public boolean conversationIsRead(List<Message> messages) {
        return messageDatabase.read(getIDfromMessages(messages)).getIsRead();

    }

    /**
     * Marks a conversation as read.
     * @param messages
     */
    public void markAsRead(List<Message> messages) {
        messageDatabase.read(getIDfromMessages(messages)).markAsRead();
    }

    private String getIDfromMessages(List<Message> messages) {
        if (messages.size() == 0) return null;
        return messages.get(0).getSender()+"-"+ messages.get(0).getRecipient();
    }

    public void deleteMessages(List<String> messageIds) {
        for (String a : messageIds) {
            messageDatabase.delete(a);
        }
    }

    public void archiveMessages(List<Message> messages) {
        for (Message a : messages) {
            a.markAsArchived();
        }
    }

    public List<Message> getArchivedMessages(String username) {
        ArrayList<String> inbox = getMyInbox(username);
        ArrayList<Message> archivedMessages = new ArrayList<>();
        for (String s : inbox) {
            List<Message> messages =getConversationThread(username, s);
            for (Message m : messages) {
                if (m.getIsArchived()) archivedMessages.add(m);
            }
        }
        return archivedMessages;
    }

    /**
     * TODO UNFINISHED
     * Checks if sender can message recipient.
     * <p>
     * They can message iff:
     * - both users exist, and
     * - sender is speaker or organizer, or
     * - sender is attendee and recipient attendy or speaker, or
     * - sender is attendee and recipient is organizer if they messaged before
     * - otherwise, true if they exist and have messaged before.
     *
     * @param senderUsername    username of sender
     * @param recipientUsername username of recipient
     * @return true if they can message.
     */
    public boolean canMessage(String senderUsername, String recipientUsername) {
        UserType sender = accountManager.getUserType(senderUsername);
        UserType recipient = accountManager.getUserType(senderUsername);

        if (sender == null || recipient == null) return false;

        switch (sender) {
            case ATTENDEE:
                switch (recipient) {
                    case ATTENDEE:
                        break;
                    case SPEAKER:
                        return true;

                }
            case SPEAKER:
                break;
            case ORGANIZER:
                return true;
        }
        return true;
    }
}
