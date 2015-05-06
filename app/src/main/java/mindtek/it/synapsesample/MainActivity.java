package mindtek.it.synapsesample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import mindtek.common.ui.SystemIntents;
import mindtek.it.synapsesample.data.MessageData;
import mindtek.it.synapsesample.fragments.DetailFragment;
import mindtek.it.synapsesample.fragments.Message;
import mindtek.it.synapsesample.fragments.MessageObserver;
import mindtek.it.synapsesample.fragments.SurveyFragment;
import mindtek.synapse.communication.OnUpdateListener;
import mindtek.synapse.data.Action;
import mindtek.synapse.SynapseManagerListener;
import mindtek.synapse.data.RegionMessage;
import mindtek.synapse.push.SynapsePushListener;

/**
 * @author Claudio Suardi - claudio.suardi@mindtek.it
 */
public class MainActivity extends FragmentActivity implements SynapseManagerListener, MessageObserver {

    private static final String TAG = "MainActivity";
    private MainActivity _this;
    private Message newMessage;
    private List<FragmentTransaction> oldTransactions;
    private boolean btOffWarning = false;
    private boolean connectionOffWarning = false;
    private SystemIntents systemRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _this = this;
        systemRequests = new SystemIntents(MainActivity.this);
        registerReceiver(messageReceiver, new IntentFilter("got_message"));


        MyApp.getSynapseManager().processPushMessage(getIntent(), new SynapsePushListener() {
            @Override
            public void onGotPushContent(Action content) {

                if (content==null)
                    Log.d(TAG, "push doesn't have associated content!");
                else
                    executeAction(content);
            }

            @Override
            public void onGotPushContentError(String errorMsg) {
                Log.e(TAG, errorMsg);
            }
        });

        updateAndStartSynapse();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }



    private void updateAndStartSynapse() {
        MyApp.getSynapseManager().updateConfig(new OnUpdateListener() {
            @Override
            public void onConfigUpdated() {
                Log.i(TAG, "SDK config updated");
                MyApp.getSynapseManager().addListener(_this);
                MyApp.getSynapseManager().start();
            }

            @Override
            public void onConfigRestored(String errorMsg) {
                Log.i(TAG, "SDK config restored from memory");
                Log.e(TAG, errorMsg);
                MyApp.getMessageManager().sendAlert(getString(R.string.update_fail_title), getString(R.string.update_fail_message), getString(R.string.update_config), R.string.update_config, null, R.drawable.alert);
                MyApp.getSynapseManager().addListener(_this);
                MyApp.getSynapseManager().start();
            }

            @Override
            public void onConfigError(String errorMsg) {
                Log.e(TAG, "SDK config error, can't start...");
                Log.e(TAG, errorMsg);
            }
        });
    }







    @Override
    public void onBleNotAvailable() {
        MyApp.getMessageManager().sendAlert(getString(R.string.no_ble_title), getString(R.string.no_ble_message), null, R.string.do_nothing, null, R.drawable.bt);
    }

    @Override
    public void onConfigNotAvailable() {
        MyApp.getMessageManager().sendAlert(getString(R.string.no_config_title), getString(R.string.no_config_message), null, R.string.update_config, null,R.drawable.alert);
    }

    @Override
    public void onBluetoothOff() {
        MyApp.getMessageManager().sendAlert(getString(R.string.ble_off_title), getString(R.string.ble_off_message), getString(R.string.turn_on), R.string.bluetooth_settings, null, R.drawable.bt);
        btOffWarning = true;
    }

    @Override
    public void onBluetoothOn() {
        if (btOffWarning) {
            newMessage.destroy();
            btOffWarning = false;
        }
    }

    @Override
    public void onConnectionOff() {
        MyApp.getMessageManager().sendAlert(getString(R.string.internet_off_title), getString(R.string.internet_off_message), getString(R.string.turn_on), R.string.internet_settings, null, R.drawable.alert);
        connectionOffWarning = true;
    }

    @Override
    public void onConnectionOn() {
        if (connectionOffWarning) {
            newMessage.destroy();
            connectionOffWarning = false;
        }
    }


    @Override
    public void onEnterRegion(boolean b) {
        try {
            RegionMessage msg = MyApp.getSynapseManager().getWelcomeMessage();
            if (msg!=null)
                MyApp.getMessageManager().sendInfo(msg.getTitle(), msg.getMessage(), null, R.string.do_nothing, null);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onExitRegion(boolean b) {
        try {
            RegionMessage msg = MyApp.getSynapseManager().getGoodbyeMessage();
            if (msg!=null)
                MyApp.getMessageManager().sendInfo(msg.getTitle(), msg.getMessage(), null, R.string.do_nothing, null);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeaconEvent(Action action) {
        MyApp.getMessageManager().sendInfo(action.getTitle(), action.getMessage(), getString(R.string.btn_read), R.string.react_to_beacon, action);
    }







    @Override
    public void onMessageTapped(MessageData messageData) {

        Log.d(TAG, "message tapped");

        switch (messageData.getAction()) {
            case R.string.do_nothing:
                break;
            case R.string.bluetooth_settings:
                systemRequests.openBluetoothSettings();
                break;
            case R.string.internet_settings:
                systemRequests.openSettings();
                break;
            case R.string.react_to_beacon:
                executeAction(messageData.getActionData());
                break;
            case R.string.update_config:
                updateAndStartSynapse();
                break;
            default:
                newMessage.close();
        }
    }



    public void executeAction(Action action) {

        Log.d(TAG, "react to beacon, " + action.getType());

        if (action.getType().equals("detail")) {
            if (newMessage!=null)
                newMessage.close();

            openDetail(action);

        } else if (action.getType().equals("video")) {
            if (newMessage!=null)
                newMessage.close();
            systemRequests.openYTVideo("http://www.youtube.com/watch?v=" + action.getYoutube());

        } else if (action.getType().equals("surveyplus")) {
            if (newMessage!=null)
                newMessage.close();
            if (getFragmentManager().findFragmentByTag("custom_survey")==null)
                openSurvey(action);
        }
    }



    private void openDetail(Action action) {
        Fragment frag = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("action", action);
        frag.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.bkg, frag, "detail");
        transaction.addToBackStack("detail");
        transaction.commit();
    }


    public void openSurvey(Action action) {

        SurveyFragment frag = new SurveyFragment();
        Bundle args = new Bundle();
        args.putSerializable("action", action);
        frag.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.bkg, frag, "survey");
        transaction.addToBackStack("survey");
        transaction.commit();
    }












    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "message received");

            if (findViewById(R.id.notification_area)==null)
                return;

            try {
                newMessage.destroy();
            } catch (Exception e) {/**/}

            newMessage = new Message();
            newMessage.setArguments(intent.getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.bkg, newMessage, "msg");
            try {
                transaction.commitAllowingStateLoss();
            } catch (IllegalStateException e) {
                oldTransactions.add(transaction);
            }

            newMessage.registerObserver(MainActivity.this);
        }
    };




    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {/**/}
    }

}