package mindtek.it.synapsesample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mindtek.it.synapsesample.R;
import mindtek.synapse.surveys.SurveyPlusQuestion;


public class SurveyAdapter extends ArrayAdapter<SurveyPlusQuestion> {

    private static final String TAG = "SurveyAdapter";
    private int res;


    public SurveyAdapter(Context context, int resource, List<SurveyPlusQuestion> objects) {
        super(context, resource, objects);
        res = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // layout init
        ViewHolder holder;
        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(res, null);

            // Set up the ViewHolder.
            holder = new ViewHolder();
            holder.check = (ImageView) convertView.findViewById(R.id.check);
            holder.txt_question  = (TextView) convertView.findViewById(R.id.txt_label);

            // Store the holder with the view.
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        // text and imgs
        final SurveyPlusQuestion item = getItem(position);
        if (item.getOpenQuestion()!=null) {
            holder.txt_question.setText(item.getOpenQuestion().getQuestion());

            if (item.getOpenQuestion().getAnswer()==null || item.getOpenQuestion().getAnswer().length()==0)
                holder.check.setImageDrawable(getContext().getResources().getDrawable(R.drawable.checkbox_off_background_white));
            else
                holder.check.setImageDrawable(getContext().getResources().getDrawable(R.drawable.checkbox_on_background_white));

        } else {
            holder.txt_question.setText(item.getSurvey().getQuestion());

            if (item.getSurvey().getAnswerId()==null || item.getSurvey().getAnswerId().length()==0)
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