package mindtek.it.synapsesample.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mindtek.common.ui.ULog;
import mindtek.it.synapsesample.R;
import mindtek.it.synapsesample.adapters.QuizStatsAdapter;
import mindtek.synapse.quiz.QuizStats;

/**
 * Created by Riccardo on 26/06/15.
 */
public class StatsDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "StatsDialogFragment";
    private ListView mListView;
    private List<QuizStats> mStats;
    private Button mOkButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStats = (List<QuizStats>) getArguments().getSerializable("stats_list");
        Collections.sort(mStats, new Comparator<QuizStats>() {
            @Override
            public int compare(QuizStats lhs, QuizStats rhs) {
                return (lhs.getPoints() > rhs.getPoints() ? -1 : (lhs.getPoints() == rhs.getPoints() ? 0 : 1));
            }
        });
        Log.d(TAG, mStats.toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mListView = (ListView) view.findViewById(R.id.listViewStats);
        mOkButton = (Button) view.findViewById(R.id.buttonExitStats);

        mListView.setAdapter(new QuizStatsAdapter(getActivity(), R.layout.row_stats, mStats));
        mOkButton.setOnClickListener(this);

        if(mListView.getAdapter().getCount()==0) {
            mListView.setVisibility(View.INVISIBLE);
            (view.findViewById(R.id.textViewNoStats)).setVisibility(View.VISIBLE);
        }

        mListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ULog.wtf(TAG, "" + convertPixelsToDp(mListView.getHeight()));
                if(convertPixelsToDp(mListView.getHeight()) >= 220) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(220));
                    mListView.setLayoutParams(params);
                }
            }
        });
    }

    public float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static StatsDialogFragment newInstance(List<QuizStats> stats) {
        StatsDialogFragment f = new StatsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("stats_list", (Serializable) stats);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
