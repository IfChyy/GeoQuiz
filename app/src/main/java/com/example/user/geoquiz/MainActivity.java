package com.example.user.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    //buttons and text views on scree
    private Button trueButton;
    private Button falseButton;
    private Button cheatButton;

    private ImageButton leftArrow;
    private ImageButton rightArrow;

    private TextView questionTextView;

    //array holding each question from res and its answer
    private Question[] questionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    //key values to store different parameters after screen rotation
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String DID_CHEAT = "cheating";
    private static final String CHEATED_QUESTIONS = "cheated_questions";

    private static final int REQUEST_CODE_CHEAT = 0;

    //class viriables for question index and if cheated
    private int currentIndex = 0, question;
    private boolean isCheater;


    private ArrayList<Integer> cheatedQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        setContentView(R.layout.activity_main);

        //------app question text view
        questionTextView = findViewById(R.id.question_text);
        //------CHALLENGE
        questionTextView.setOnClickListener(this);

        //--------- true button
        trueButton = findViewById(R.id.true_button);
        trueButton.setOnClickListener(this);

        //-----------face button
        falseButton = findViewById(R.id.false_button);
        falseButton.setOnClickListener(this);

        //----------cheat button
        cheatButton = findViewById(R.id.cheat_button);
        cheatButton.setOnClickListener(this);

        //------CHALLENGE
        //----------left arrow button
        leftArrow = findViewById(R.id.left_arrow);
        leftArrow.setOnClickListener(this);

        //------CHALLENGE
        //----------right arrow button
        rightArrow = findViewById(R.id.right_arrow);
        rightArrow.setOnClickListener(this);

        //init the array list holding the numbers of the cheated questions
        cheatedQuestions = new ArrayList<>();
        //get the saved bundle data
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            isCheater = savedInstanceState.getBoolean(DID_CHEAT, false);
            cheatedQuestions = savedInstanceState.getIntegerArrayList(CHEATED_QUESTIONS);

        }
        //update question method
        updateQuestion();

    }

    //get result from child activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if result not equal to OK then return
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //if request code equal cheat code and data null return else add true if cheated for each question
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;

            }
            isCheater = CheatActivity.wasAnswerShown(data);
            //add the number of current question to the int arrayList
            cheatedQuestions.add(currentIndex);
            //check question and cheated status


        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        //save the current index of question in the bundle
        outState.putInt(KEY_INDEX, currentIndex);
        outState.putBoolean(DID_CHEAT, isCheater);
        //save the cheated queistion after rotationg screen
        outState.putIntegerArrayList(CHEATED_QUESTIONS, cheatedQuestions);


    }

    //-----------updates the question when used in a button click method
    public void updateQuestion() {
        question = questionBank[currentIndex].getTextResId();
        questionTextView.setText(question);
    }

    //-----------checks if the answered value is true or false depending on the question
    //------------return a toast showing if its correct or not
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = questionBank[currentIndex].isAnswerTrue();

        int messageResId = 0;
        if (isCheater) {
            messageResId = R.string.judgment_toast;

        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }

        }
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        //------CHALLENGE --- text view on click
        if (view.getId() == questionTextView.getId()) {
            currentIndex = (currentIndex + 1) % questionBank.length;
            updateQuestion();
        }

        //---------true button on click
        if (view.getId() == trueButton.getId()) {
            checkAnswer(true);
        }

        //---------false button on click
        if (view.getId() == falseButton.getId()) {
            checkAnswer(false);
        }

        //------CHALLENGE
        //---------left arrow button on click
        if (view.getId() == leftArrow.getId()) {
            currentIndex = (currentIndex - 1) % questionBank.length;

            if (currentIndex < 0) {
                currentIndex = questionBank.length - 1;
            }


            // check if answer cheated
            if (cheatedQuestions.contains(currentIndex)) {
                isCheater = true;
            } else {
                isCheater = false;
            }
            updateQuestion();
        }

        //------CHALLENGE
        //---------rigth arrow button on click
        if (view.getId() == rightArrow.getId()) {
            currentIndex = (currentIndex + 1) % questionBank.length;
            if (currentIndex > questionBank.length) {
                currentIndex = 0;
            }

            // check if answer cheated
            if (cheatedQuestions.contains(currentIndex)) {
                isCheater = true;
            } else {
                isCheater = false;
            }
            updateQuestion();
        }

        //---------cheat button on click
        if (view.getId() == cheatButton.getId()) {
            boolean answerIsTrue = questionBank[currentIndex].isAnswerTrue();
            Intent in = CheatActivity.newIntent(this, answerIsTrue);
            startActivityForResult(in, REQUEST_CODE_CHEAT);
        }


    }

    //------------------testing when lifecylces parts are invoked
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }
}
