package mindtek.it.synapsesample;

import android.content.Context;
import android.content.Intent;

import mindtek.it.synapsesample.data.MessageData;
import mindtek.synapse.data.Action;

/**
 * Created by claudio.suardi@mindtek.it
 */
public class MessageManager {

    Context context;

    public MessageManager(Context context) {
        this.context = context;
    }

    // message management
    public void sendAlert(String title, String text, String cta, int action, Action actionData) {
        MessageData data = new MessageData("alert", title, text, cta, action, actionData);
        Intent i = new Intent("got_message");
        i.putExtra("data", data);
        context.sendBroadcast(i);
    }

    public void sendAlert(String title, String text, String cta, int action, Action actionData, int imgRes) {
        MessageData data = new MessageData("alert", title, text, cta, action, actionData, imgRes);
        Intent i = new Intent("got_message");
        i.putExtra("data", data);
        context.sendBroadcast(i);
    }


    public void sendInfo(String title, String text, String cta, int action, Action actionData) {
        MessageData data = new MessageData("info", title, text, cta, action, actionData);
        Intent i = new Intent("got_message");
        i.putExtra("data", data);
        context.sendBroadcast(i);
    }

    public void sendInfo(String title, String text, String cta, int action, Action actionData, int imgRes) {
        MessageData data = new MessageData("info", title, text, cta, action, actionData, imgRes);
        Intent i = new Intent("got_message");
        i.putExtra("data", data);
        context.sendBroadcast(i);
    }
}