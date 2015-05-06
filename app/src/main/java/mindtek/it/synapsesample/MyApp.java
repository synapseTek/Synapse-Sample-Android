package mindtek.it.synapsesample;

import android.app.Application;

import mindtek.synapse.SynapseManager;


/**
 * @author Claudio Suardi - claudio.suardi@mindtek.it
 */
public class MyApp extends Application {
    private static final String TAG = "MyApp";
    private static MessageManager messageManager;
    private static SynapseManager synapseManager;

    public static MessageManager getMessageManager() {
        return messageManager;
    }

    public static SynapseManager getSynapseManager() {
        return synapseManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messageManager = new MessageManager(this);
        synapseManager = new SynapseManager(this, getString(R.string.client_id), getString(R.string.push_sender_id));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        synapseManager.onTerminate();
    }

}
