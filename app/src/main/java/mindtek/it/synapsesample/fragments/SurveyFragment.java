package mindtek.it.synapsesample.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mindtek.it.synapsesample.MainActivity;
import mindtek.it.synapsesample.MyApp;
import mindtek.it.synapsesample.R;
import mindtek.it.synapsesample.adapters.SurveyAdapter;
import mindtek.synapse.data.Action;
import mindtek.synapse.surveys.Answer;
import mindtek.synapse.surveys.CloseQuestion;
import mindtek.synapse.surveys.OnAnswerListener;
import mindtek.synapse.surveys.OpenQuestion;
import mindtek.synapse.surveys.SurveyPlusAnswer;
import mindtek.synapse.surveys.SurveyPlusQuestion;


public class SurveyFragment extends Fragment {

    private static String TAG = "SurveyFragment";
    private Action action;
    ArrayList<SurveyPlusQuestion> questions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = this.getArguments();
        action = (Action) args.getSerializable("action");
        return inflater.inflate(R.layout.survey, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.bkg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).hideKeyboard();
            }
        });


        // btn back
        getView().findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        // title
        TextView txt_title = (TextView) getView().findViewById(R.id.txt_title);
        if (action.getTitle() != null)
            txt_title.setText(action.getTitle());
        else
            txt_title.setVisibility(View.GONE);


        // create a structure with references to all questions (open and closed)
        questions = new ArrayList<SurveyPlusQuestion>();
        if (action.getOpenQuestions()!=null) {
            for (OpenQuestion question : action.getOpenQuestions()) {
                questions.add(new SurveyPlusQuestion(question));
            }
        }
        if (action.getSurveys()!=null) {
            for (mindtek.synapse.surveys.Survey question : action.getSurveys()) {
                questions.add(new SurveyPlusQuestion(question));
            }
        }

        updateView();
    }









    private void updateView() {

        LinearLayout questionList = (LinearLayout) getView().findViewById(R.id.questions);

        // clear
        if (questionList.getChildCount()>0)
            questionList.removeAllViews();

        // show question list
        final SurveyAdapter adapter = new SurveyAdapter(getView().getContext(), R.layout.row_survey_question, questions);
        final int adapterCount = adapter.getCount();
        for (int i = 0; i < adapterCount; i++) {

            View item = adapter.getView(i, null, null);
            questionList.addView(item);
            final int index = i;

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final SurveyPlusQuestion item = adapter.getItem(index);

                    // IF OPEN QUESTION
                    if (adapter.getItem(index).getOpenQuestion()!=null) {
                        // answer modal
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(item.getOpenQuestion().getQuestion())
                                .setView(getActivity().getLayoutInflater().inflate(R.layout.open_question_req, null))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        TextView input = (TextView) ((AlertDialog) dialog).findViewById(R.id.answer);
                                        item.getOpenQuestion().setAnswer(input.getText().toString());
                                        updateView();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        if (item.getOpenQuestion().getAnswer()!=null)
                            ((TextView) dialog.findViewById(R.id.answer)).setText(item.getOpenQuestion().getAnswer());
                    }



                    // IF CLOSE QUESTION
                    else if (adapter.getItem(index).getSurvey()!=null) {

                        // answers modal
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(item.getSurvey().getQuestion())
                                .setView(getActivity().getLayoutInflater().inflate(R.layout.close_question_req, null))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        // read answer
                                        RadioGroup risposte = (RadioGroup) ((AlertDialog) dialog).findViewById(R.id.answers);
                                        int radioButtonID = risposte.getCheckedRadioButtonId();
                                        View radioButton = risposte.findViewById(radioButtonID);
                                        int idx = risposte.indexOfChild(radioButton);
                                        item.getSurvey().setAnswerId(item.getSurvey().getAnswers().get(idx).getId());

                                        updateView();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                        // set radiogroup
                        RadioGroup risposte = (RadioGroup) dialog.findViewById(R.id.answers);
                        RadioButton temp;
                        for (Answer risposta :item.getSurvey().getAnswers()) {
                            temp = new RadioButton(view.getContext());
                            temp.setText(risposta.getText());
                            temp.setTextAppearance(getView().getContext(), R.style.text_mid_size);
                            temp.setTextColor(view.getResources().getColor(android.R.color.black));
                            RadioGroup.LayoutParams params= new RadioGroup.LayoutParams(view.getContext(), null);
                            params.setMargins(0, 5, 0, 5);
                            temp.setLayoutParams(params);
                            risposte.addView(temp);
                        }

                    }
                }
            });
        }


        // send answers button
        getView().findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkAnswers())
                    Toast.makeText(getView().getContext(), getString(R.string.missing_answers), Toast.LENGTH_LONG).show();
                else
                    sendAnswers(view);
            }
        });

    }





    private boolean checkAnswers() {
        // check if all questions have answer
        for (SurveyPlusQuestion question : questions) {
            if (question.getSurvey()!=null && ( question.getSurvey().getAnswerId()==null || question.getSurvey().getAnswerId().equals("") ) ) {
                Toast.makeText(getView().getContext(), getString(R.string.missing_answers), Toast.LENGTH_LONG).show();
                return false;
            }

            if (question.getOpenQuestion()!=null && (question.getOpenQuestion().getAnswer()==null || question.getOpenQuestion().getAnswer().equals(""))) {
                Toast.makeText(getView().getContext(), getString(R.string.missing_answers), Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }



    private void sendAnswers(View view) {

        // disable send button
        view.setEnabled(true);

        // prepare answers
        SurveyPlusAnswer response = new SurveyPlusAnswer(action.getSurveyplusID());
        for (SurveyPlusQuestion question : questions) {
            if (question.getSurvey()!=null)
                response.getSurveys().add(new CloseQuestion(question.getSurvey().getId(), question.getSurvey().getAnswerId()));
            else
                response.getOpenQuestions().add(new OpenQuestion(null, question.getOpenQuestion().getAnswer(), question.getOpenQuestion().getId()) );
        }

        // send answers
        MyApp.getSynapseManager().answerSurvey(response, new OnAnswerListener() {
            @Override
            public void onAnswerRegistered() {
                Toast.makeText(getView().getContext(), getString(R.string.survey_ok), Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).onBackPressed();
            }

            @Override
            public void onAnswerError() {
                Toast.makeText(getView().getContext(), getString(R.string.survey_fail), Toast.LENGTH_LONG).show();
                getView().findViewById(R.id.btn_send).setEnabled(true);
            }
        });
    }



    private void error() {
        Toast.makeText(getView().getContext(), getString(R.string.missing_fields), Toast.LENGTH_LONG).show();
    }


}