package mindtek.it.synapsesample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mindtek.it.synapsesample.R;
import mindtek.synapse.quiz.QuizQuestion;

import java.util.List;

/**
 * Created by Riccardo on 18/09/15.
 */


public class QuizAdapter extends ArrayAdapter<QuizQuestion> {

    private static final String TAG = "QuizAdapter";
    private int res;


    public QuizAdapter(Context context, int resource, List<QuizQuestion> objects) {
        super(context, resource, objects);
        res = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inizializzo il layout
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res, null);

            // Set up the ViewHolder.
            holder = new ViewHolder();
            holder.check = (ImageView) convertView.findViewById(R.id.check);
            holder.txt_question = (TextView) convertView.findViewById(R.id.txt_label);


            // Store the holder with the view.
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // aggiungo testi e immagine
        final QuizQuestion item = getItem(position);

        if (item.getQuestion() != null) {
            holder.txt_question.setText(item.getQuestion());

            if (!item.isAnswered() || item.getAnswers().size() == 0)
                holder.check.setImageDrawable(getContext().getResources().getDrawable(R.drawable.checkbox_off_background_white));
            else
                holder.check.setImageDrawable(getContext().getResources().getDrawable(R.drawable.checkbox_on_background_white));

        }


        return convertView;
    }


    class ViewHolder {
        ImageView check;
        TextView txt_question;
    }

}