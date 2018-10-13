package com.example.user.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity implements View.OnClickListener {
    //extras gathered form other activity
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.user.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.user.geoquiz.answer_shwo";
    private static final String DID_CHEAT = "cheated";


    //views
    private TextView answerTextView;
    private Button showAnswer;
    private TextView apiVersion;

    private boolean answerIsTrue;
    private boolean cheated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //get extra for answer form parent activity
        answerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        //text view showing answer text
        answerTextView = findViewById(R.id.answer_text_view);

        //text view showing the actual answer
        showAnswer = findViewById(R.id.show_answer_button);
        showAnswer.setOnClickListener(this);

        //api version text view
        apiVersion = findViewById(R.id.sdk_version);
        apiVersion.setText("Phone api version is:" + Build.VERSION.RELEASE);

        //check if after screen rotation cheating button is pressed value still equal to true
        if (savedInstanceState != null) {
            cheated = savedInstanceState.getBoolean(DID_CHEAT, false);

        }
        //if value equal true then cheated equals to true
        if (cheated) {
            setAnswerShownResutl(true);
          showAnswer.performClick();

        }
    }

    //save data after screen rotation for pressing cheat button
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DID_CHEAT, cheated);
    }

    //call intent to start this activitty form another class without knowing what extra it is passing
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent in = new Intent(packageContext, CheatActivity.class);
        in.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return in;
    }

    //method needed to get value of cheated in actiivty result of parent activity
    //returns intent extra with key extra_answer_shown and value false
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    //set value of EXTRA_ANSWER_SHOWN with value of cheated and return value with method (wasAnswerShown)
    private void setAnswerShownResutl(boolean isAnswerShown) {

        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);

    }

    //on click listeners
    @Override
    public void onClick(View view) {
        if (view.getId() == showAnswer.getId()) {
            if (answerIsTrue) {
                answerTextView.setText(R.string.true_button);
            } else {
                answerTextView.setText(R.string.false_button);
            }
            //boolean to store true if cheated after rotating screen
            cheated = true;
            //method to set value of cheated
            setAnswerShownResutl(true);

            //method to animate show answer button
            animateButton(showAnswer);

        }

    }

    //method to animate our button
    private void animateButton(final View view) {
        //check if api 21 supported if so animate show answer button to hide from width into the center
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            view.post(new Runnable() {
                @Override
                public void run() {
                    //center x and y
                    int cx = view.getWidth() / 2;
                    int cy = view.getHeight() / 2;
                    //radius
                    float radius = view.getWidth();
                    //animation invoke
                    Animator animation = ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0);
                    animation.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            // after animation finishe hide butotn
                            view.setVisibility(View.INVISIBLE);

                        }
                    });
                    animation.start();
                }
            });

        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
