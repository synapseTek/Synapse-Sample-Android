package mindtek.it.synapsesample.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import mindtek.common.data.JSON;
import mindtek.common.ui.UToast;
import mindtek.it.synapsesample.MainActivity;
import mindtek.it.synapsesample.MyApp;
import mindtek.it.synapsesample.R;
import mindtek.it.synapsesample.adapters.QuizAdapter;
import mindtek.synapse.data.Action;
import mindtek.synapse.quiz.QuizAnswer;
import mindtek.synapse.quiz.QuizQuestion;
import mindtek.synapse.quiz.QuizQuestionResponse;
import mindtek.synapse.quiz.QuizResponse;
import mindtek.synapse.surveys.OnAnswerListener;


public class Quiz extends Fragment {

    private static String TAG = "QuizFragment";
    private Action action;
    ArrayList<QuizQuestion> questions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = this.getArguments();
        action = (Action) args.getSerializable("action");
        Log.d(TAG, "title: " + action.getTitle() + ", text: " + action.getText());
        // i tablet landscape hanno layout personalizzato in landscape
        MainActivity _activity = (MainActivity) getActivity();
        int currentOrientation = getResources().getConfiguration().orientation;

            return inflater.inflate(R.layout.quiz, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Log.d(TAG, "questions: " + action.getQuestions());

        view.findViewById(R.id.bkg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).hideKeyboard();
            }
        });


        // btn back
        view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        // creo una struttura contenente riferimenti a tutte le domande
        try {
            if (action.getQuestions() != null) {
                questions = (ArrayList<QuizQuestion>) action.getQuestions();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        updateView();
    }


    private void updateView() {

        Log.d(TAG, "update view");

        final LinearLayout questionList = (LinearLayout) getView().findViewById(R.id.questions);

        // elimino ogni eventuale item dall'elenco
        if (questionList.getChildCount() > 0)
            questionList.removeAllViews();


        final Quiz _instance = this;


        // PULSANTE PER INVIO QUIZ
        Button btn_send = (Button) getView().findViewById(R.id.btn_rispondi);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "SEND ANSWERS");

                // controllo che le risposte chiuse abbiano risposta, altrimenti error
                for (QuizQuestion question : questions) {
                    if (!question.isAnswered()) {
                        Toast.makeText(getView().getContext(), getString(R.string.surveyplus_missing_answers), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // attivo pulsante di invio
                view.setEnabled(true);

                // preparo risposte per l'invio
                QuizResponse response = new QuizResponse(action.getQuizID(), "");

                try {
                    JSON.encode(questions);


                    ArrayList<QuizQuestionResponse> questionResponses = new ArrayList<>();
                    QuizQuestionResponse temp;

                    for (QuizQuestion question : questions) {
                        temp = new QuizQuestionResponse(question.getId());
                        for (QuizAnswer singleAnswer : question.getAnswers()) {
                            if (singleAnswer.isChecked())
                                temp.setAnswer(singleAnswer.getId());
                        }
                        Log.d(TAG, "Sono dentro il ciclo question: " + JSON.encode(temp));
                        questionResponses.add(temp);
                        Log.d(TAG, "Somma parziale di tutto: " + JSON.encode(questionResponses));
                    }
                    Log.d(TAG, "Ho finito il ciclo questions: " + JSON.encode(questionResponses));
                    response.setQuestions(questionResponses);

                    //test per vedere il risultato finale se va bene
                    response.setUserid(MyApp.getSynapseManager().getData().getAccessToken());

                    Log.d(TAG, "RISULTATO: " + JSON.encode(MyApp.getSynapseManager().getData()));
                    Log.d(TAG, JSON.encode(response));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MyApp.getSynapseManager().answerQuiz(response, new OnAnswerListener() {
                    @Override
                    public void onAnswerRegistered() {
                        UToast.show(getView().getContext(), getString(R.string.quiz_ok), true);
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onAnswerError() {
                        Toast.makeText(getView().getContext(), getString(R.string.survey_fail), Toast.LENGTH_LONG).show();
                        // riattivo pulsante di invio
                        getView().findViewById(R.id.btn_rispondi).setEnabled(true);
                    }
                });
            }
        });


        // visualizzo elenco di domande
        final QuizAdapter adapter = new QuizAdapter(getView().getContext(), R.layout.row_quiz_question, questions);

        final int adapterCount = adapter.getCount();
        for (int i = 0; i < adapterCount; i++) {

            View item = adapter.getView(i, null, null);
            questionList.addView(item);

            final int index = i;

            item.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    final QuizQuestion item = adapter.getItem(index);

                    if (adapter.getItem(index) != null) {

                        Log.d(TAG, item.getQuestion());


                        // apro modale con elenco di risposte
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(item.getQuestion())
                                .setView(getActivity().getLayoutInflater().inflate(R.layout.quiz_question_req, null))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        // imposto il valore della risposta in base a quello che ha scelto l'utente
                                        RadioGroup risposte = (RadioGroup) ((AlertDialog) dialog).findViewById(R.id.answers);
                                        int radioButtonID = risposte.getCheckedRadioButtonId();
                                        View radioButton = risposte.findViewById(radioButtonID);
                                        int idx = risposte.indexOfChild(radioButton);


                                        try {
                                            Log.d(TAG + " quiz", JSON.encode(item.getQuestion()));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (idx >= 0) {
                                            Log.d(TAG, "risposta text: " + item.getAnswers().get(idx).getText());
                                            Log.d(TAG, "risposta id: " + item.getAnswers().get(idx).getId());

                                            item.checkAnAnswer(idx, true);
                                        } else {
                                            for (int i = 0; i < item.getAnswers().size(); i++)
                                                item.checkAnAnswer(i, false);
                                        }

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


                        //imposto radiogroup
                        RadioGroup risposte = (RadioGroup) dialog.findViewById(R.id.answers);

                        // aggiungo risposte al radiogroup
                        RadioButton temp;
                        for (QuizAnswer risposta : item.getAnswers()) {
                            temp = new RadioButton(view.getContext());
                            //temp.setButtonDrawable(view.getResources().getDrawable(R.drawable.checkbox_selector));
                            temp.setText(risposta.getText());
                            temp.setTextAppearance(getView().getContext(), R.style.text_mid_size);
                            temp.setTextColor(view.getResources().getColor(android.R.color.black));
                            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(view.getContext(), null);
                            params.setMargins(0, 5, 0, 5);
                            temp.setLayoutParams(params);
                            risposte.addView(temp);
                        }
                    }
                }
            });
        }
    }


}