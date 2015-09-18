package mindtek.it.synapsesample.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Vector;

import mindtek.common.ui.images.ImageDownloader;
import mindtek.it.synapsesample.R;
import mindtek.it.synapsesample.data.MessageData;


/**
 * @author claudio.suardi@mindtek.it
 */
public class Message extends Fragment {

    private static final String TAG = "Message";
    private Vector<MessageObserver> observers = new Vector<MessageObserver>();
    private MessageData data;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        data = (MessageData) args.getSerializable("data");
        return inflater.inflate(R.layout.message, container, false);
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // colors
        RelativeLayout background = (RelativeLayout) view.findViewById(R.id.background);
        RelativeLayout borderImg = (RelativeLayout) view.findViewById(R.id.cornice);
        String type = data.getType();

        int color = getResources().getColor(R.color.notification_blue);
        if (type.equals("alert"))
            color = getResources().getColor(R.color.notification_red);

        background.setBackgroundColor(color);
        borderImg.setBackgroundColor(color);

        int borderColor = getResources().getColor(R.color.notification_blue_border);
        if (type.equals("alert"))
            borderColor = getResources().getColor(R.color.notification_red_border);

        try {
            view.findViewById(R.id.upper_border).setBackgroundColor(borderColor);
            view.findViewById(R.id.lower_border).setBackgroundColor(borderColor);
        } catch (Exception e) {/**/}


        // text
        ((TextView) view.findViewById(R.id.title)).setText(data.getTitle());
        ((TextView) view.findViewById(R.id.txt)).setText(data.getText());


        // image
        ImageView img = (ImageView) view.findViewById(R.id.img);
        if ( data.getActionData()!=null &&
                data.getActionData().getImages()!=null &&
                data.getActionData().getImages().size()>0) {
            ImageDownloader.init(view.getContext());
            ImageDownloader.loadImage(view.getContext(), img, data.getActionData().getImages().get(0));
        } else {
            if (data.getImgRes()>0)
                img.setImageDrawable(view.getResources().getDrawable(data.getImgRes()));
            else
                img.setImageDrawable(view.getResources().getDrawable(R.mipmap.ic_launcher));
        }


        // animation
        Animation openMessage = AnimationUtils.loadAnimation(view.getContext(), R.anim.open_message);
        view.startAnimation(openMessage);


        // buttons
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        try {
            if (data.getCta() == null ||
                    (data.getActionData() != null && data.getActionData().getType().equals("toast")))
                view.findViewById(R.id.btn_leggi).setVisibility(View.GONE);
            else
                ((Button) view.findViewById(R.id.btn_leggi)).setText(data.getCta());
            view.findViewById(R.id.btn_leggi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (MessageObserver observer : observers)
                        observer.onMessageTapped(data);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {

        try {
            Animation closeMessage = AnimationUtils.loadAnimation(getView().getContext(), R.anim.close_message);
            getView().startAnimation(closeMessage);
            closeMessage.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {/**/}

                @Override
                public void onAnimationEnd(Animation animation) {
                    animation.setAnimationListener(null);
                    destroy();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {/**/}
            });
        } catch (Exception e) {/**/}
    }


    public void destroy() {

        try {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception e) {/**/}
    }





    public void registerObserver(MessageObserver o) {
        observers.add(o);
    }

    public void removeObserver(MessageObserver o) {
        observers.remove(o);
    }

}