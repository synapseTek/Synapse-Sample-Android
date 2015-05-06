package mindtek.it.synapsesample.data;

import java.io.Serializable;

import mindtek.synapse.data.Action;

/**
 * @author claudio.suardi@mindtek.it
 */
public class MessageData extends Object implements Serializable {

    private String type, title, text, cta;
    private int action, imgRes;
    private Action actionData;

    public MessageData(String type, String title, String text, String cta, int action, Action actionData, int imgRes) {
        this.type = type;
        this.title = title;
        this.text = text;
        this.cta = cta;
        this.action = action;
        this.imgRes = imgRes;
        this.actionData = actionData;
    }

    public MessageData(String type, String title, String text, String cta, int action, Action actionData) {
        this.type = type;
        this.title = title;
        this.text = text;
        this.cta = cta;
        this.action = action;
        this.actionData = actionData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCta() {
        return cta;
    }

    public void setCta(String cta) {
        this.cta = cta;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Action getActionData() {
        return actionData;
    }

    public void setActionData(Action actionData) {
        this.actionData = actionData;
    }
}
