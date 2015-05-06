package mindtek.it.synapsesample.fragments;


import mindtek.it.synapsesample.data.MessageData;

/**
 * @author claudio.suardi@mindtek.it
 */
public interface MessageObserver {
    public void onMessageTapped(MessageData messageData);
}
