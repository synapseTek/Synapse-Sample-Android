package mindtek.it.synapsesample.fragments;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mindtek.common.ui.SystemIntents;
import mindtek.it.synapsesample.MyApp;
import mindtek.it.synapsesample.R;
import mindtek.it.synapsesample.adapters.GalleryAdapter;
import mindtek.synapse.data.Action;
import mindtek.synapse.surveys.Answer;
import mindtek.synapse.surveys.OnAnswerListener;
import mindtek.synapse.surveys.SurveyAnswer;


public class DetailFragment extends Fragment {

    private static String TAG = "DetailFragment";
    private Action action;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        action = (Action) args.getSerializable("action");
        return inflater.inflate(R.layout.detail, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        context = view.getContext();

        // btn back
        getView().findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        // text
        String msg = (action.getType().equals("toast")) ? action.getMessage() : action.getText();
        setContent(action.getTitle(), null, null, msg);

        // video
        if (action.getYoutube() != null && !action.getYoutube().equals(""))
            setVideo(action.getYoutube());

        // gallery
        try {
            ArrayList<String> imgURLs = new ArrayList<String>();
            for (String url : action.getImages()) {
                imgURLs.add(url);
            }
            setGallery(imgURLs, action.getTitle(), null);
        } catch (Exception e) {
            setGallery(new ArrayList(), "", null);
        }

        // poll
        if (action.getSurvey()!=null) {

            // did i already answered the poll?
            if (!MyApp.getSynapseManager().isSurveyAnswered(action.getSurvey().getId()))
                setPoll(view);
        }
    }





    private void setContent(String title, String startDate, String endDate, String text) {

        TextView txt_title = (TextView) getView().findViewById(R.id.txt_title);

        if (title != null)
            txt_title.setText(title);
        else
            txt_title.setVisibility(View.GONE);

        if (text != null) {
            text = "<style>p, html, body, span {font-family: sans-serif-condensed; color:white; margin:1px 0 0 0;} a {color:white}</style>" + text;
            WebView wv_content = (WebView) getView().findViewById(R.id.contenuto);
            wv_content.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");
            wv_content.setBackgroundColor(Color.argb(1, 0, 0, 0));
            wv_content.setHorizontalScrollBarEnabled(false);
            wv_content.setVerticalScrollBarEnabled(false);
        }
    }


    protected void setVideo(String _embedCode) {

        final String url = "http://www.youtube.com/watch?v=" + _embedCode;

        // setting video button
        RelativeLayout btn_video = (RelativeLayout) getView().findViewById(R.id.btn_youtube);
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemIntents helper = new SystemIntents(context);
                helper.openYTVideo(url);
            }
        });
        btn_video.setVisibility(View.VISIBLE);
    }


    protected void setGallery(final ArrayList<String> _images, final String _title, final String _tag) {

        if (_images==null || _images.size()==0) {
            // hide gallery
            getView().findViewById(R.id.upper_section).setVisibility(View.GONE);

            //+10dp margintop title_bar
            RelativeLayout titleView = (RelativeLayout) getView().findViewById(R.id.title_bar);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) titleView.getLayoutParams();
            p.setMargins(0, 10, 0, 0);
            titleView.requestLayout();
        }

        final ViewPager viewPager = (ViewPager) getView().findViewById(R.id.view_pager);
        GalleryAdapter adapter = new GalleryAdapter(getView().getContext(), _images);
        viewPager.setAdapter(adapter);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {/**/}

            @Override
            public void onPageSelected(int i) {
                if (i==_images.size()-1)
                    getView().findViewById(R.id.gallery_next).setVisibility(View.GONE);
                else
                    getView().findViewById(R.id.gallery_next).setVisibility(View.VISIBLE);

                if (i==0)
                    getView().findViewById(R.id.gallery_prev).setVisibility(View.GONE);
                else
                    getView().findViewById(R.id.gallery_prev).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        getView().findViewById(R.id.gallery_prev).setVisibility(View.GONE);

        if (_images.size()<2) {
            getView().findViewById(R.id.gallery_next).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.gallery_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
            });
            getView().findViewById(R.id.gallery_prev).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                }
            });
        }
    }



    private void setPoll(View view) {
        view.findViewById(R.id.btn_answer).setEnabled(true);

        try {
            view.findViewById(R.id.survey).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.survey_title)).setText(action.getSurvey().getQuestion());

            // loading options
            final RadioGroup answers = (RadioGroup) view.findViewById(R.id.answers);
            answers.setVisibility(View.VISIBLE);

            // adding options to radiogroup
            RadioButton temp;
            for (Answer answer : action.getSurvey().getAnswers()) {
                temp = new RadioButton(view.getContext());
                temp.setButtonDrawable(view.getResources().getDrawable(R.drawable.checkbox_selector_white));
                temp.setText(answer.getText());
                temp.setTextColor(view.getResources().getColor(android.R.color.white));
                temp.setTextAppearance(getView().getContext(), R.style.text_mid_size);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(view.getContext(), null);
                params.setMargins(0, 5, 0, 5);
                temp.setLayoutParams(params);
                answers.addView(temp);
            }

            // answer button
            view.findViewById(R.id.btn_answer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // disable send button
                    getView().findViewById(R.id.btn_answer).setEnabled(true);

                    // read answer
                    int radioButtonID = answers.getCheckedRadioButtonId();
                    View radioButton = answers.findViewById(radioButtonID);
                    int idx = answers.indexOfChild(radioButton);
                    String survey = action.getSurvey().get_id();
                    String answer = action.getSurvey().getAnswers().get(idx).getText();
                    Log.d(TAG, "answer: " + answer);

                    // send answer
                    SurveyAnswer answerData = new SurveyAnswer(survey, answer);
                    MyApp.getSynapseManager().answerSurvey(answerData, new OnAnswerListener() {
                        @Override
                        public void onAnswerRegistered() {
                            // hide the poll
                            getView().findViewById(R.id.survey).setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnswerError() {
                            Toast.makeText(getView().getContext(), getString(R.string.survey_fail), Toast.LENGTH_LONG).show();
                            getView().findViewById(R.id.btn_answer).setEnabled(true);
                        }
                    });

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}