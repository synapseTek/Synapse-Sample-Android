package mindtek.it.synapsesample.adapters;

/**
 * Created by Riccardo on 26/06/15.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mindtek.it.synapsesample.R;
import mindtek.synapse.quiz.QuizStats;


public class QuizStatsAdapter extends ArrayAdapter<QuizStats> {

    private static final String TAG = "QuizStatsAdapter";
    private int count;
    private int res;


    public QuizStatsAdapter(Context context, int resource, List<QuizStats> objects) {
        super(context, resource, objects);
        Log.d(TAG, objects.toString());
        count = objects.size();
        res = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG + " gtView", "User: " + getItem(position).getUser() + ", Points: " + getItem(position).getPoints());

        // inizializzo il layout
        ViewHolder holder;
        if (convertView == null) {
            Log.d(TAG, "convertview == null");
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res, null);

            // Set up the ViewHolder.
            holder = new ViewHolder();
            holder.txt_user = (TextView) convertView.findViewById(R.id.textViewUserName);
            holder.txt_points = (TextView) convertView.findViewById(R.id.textViewPoints);

            // Store the holder with the view.
            convertView.setTag(holder);

        } else {
            Log.d(TAG, "convertView!=null");
            holder = (ViewHolder) convertView.getTag();
        }

        final QuizStats item = getItem(position);
        if (item != null) {
            holder.txt_user.setText(item.getUser());
            holder.txt_points.setText("" + item.getPoints());
            Log.d(TAG, "user: " + item.getUser() + ", points: " + item.getPoints());
        } else
            Log.d(TAG, "item==null");

        return convertView;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + count);
        return count;
    }

    class ViewHolder {
        TextView txt_user;
        TextView txt_points;
    }

}