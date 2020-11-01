package com.justice.teacherexam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

import static com.justice.teacherexam.ApplicationClass.COLLECTION_ALL_QUESTIONS;
import static com.justice.teacherexam.ApplicationClass.COLLECTION_QUESTIONS;


public class QuestionAddActivity extends AppCompatActivity {
    private static final String TAG = "QuestionAddActivity";
    private ProgressBar progressBar;
    private TextInputLayout questionEdtTxt;
    private TextInputLayout aChoiceEdtTxt;
    private TextInputLayout bChoiceEdtTxt;
    private TextInputLayout cChoiceEdtTxt;
    private TextInputLayout timerEdtTxt;
    private Button saveBtn;

    private boolean updating = false;
    private QuestionModel questionDataOriginal;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add);
        initWidgets();
        setOnClickListeners();
        check_if_question_is_being_updated();

    }

    private void check_if_question_is_being_updated() {
        if (ApplicationClass.documentSnapshot != null) {
            Log.d(TAG, "check_if_question_is_being_updated: updating a question");
            questionDataOriginal = ApplicationClass.documentSnapshot.toObject(QuestionModel.class);
            updating = true;
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        Log.d(TAG, "setDefaultValues: setting default values");
        questionEdtTxt.getEditText().setText(questionDataOriginal.getQuestion());
        aChoiceEdtTxt.getEditText().setText(questionDataOriginal.getOption_a());
        bChoiceEdtTxt.getEditText().setText(questionDataOriginal.getOption_b());
        cChoiceEdtTxt.getEditText().setText(questionDataOriginal.getOption_c());
        timerEdtTxt.getEditText().setText(questionDataOriginal.getTimer() + "");

    }

    private void setOnClickListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        QuestionModel questionData = new QuestionModel();
        String question = questionEdtTxt.getEditText().getText().toString().trim();
        String first = aChoiceEdtTxt.getEditText().getText().toString().trim();
        String second = bChoiceEdtTxt.getEditText().getText().toString().trim();
        String third = cChoiceEdtTxt.getEditText().getText().toString().trim();
        String timer = timerEdtTxt.getEditText().getText().toString().trim();


        if (question.isEmpty() || first.isEmpty() || second.isEmpty() || third.isEmpty()) {
            Log.d(TAG, "saveData: some edit text are empty please fill all of them");
            Toasty.error(this, "Please fill fields", Toast.LENGTH_SHORT).show();
            return;
        }

        questionData.setQuestion(question);
        questionData.setOption_a(first);
        questionData.setOption_b(second);
        questionData.setOption_c(third);
        questionData.setTimer(Long.parseLong(timer));
        //  questionData.setAnswer(first);
        questionData.setDate(null);

      /*  Map<String, Object> map = null;
        map = new HashMap<>();
        map.put("question", questionData.getQuestion());
        map.put("option_a", questionData.getFirstChoice());
        map.put("option_b", questionData.getSecondChoice());
        map.put("option_c", questionData.getThirdChoice());
        map.put("timer", questionData.getForthChoice());
        map.put("answer", questionData.getFirstChoice());
        map.put("date", FieldValue.serverTimestamp());
*/
        saveDataInDatabase(questionData);


    }

    private void saveDataInDatabase(QuestionModel questionData) {
        Log.d(TAG, "saveDataInDatabase: saving data to database");
        if (updating) {
            Log.d(TAG, "saveDataInDatabase: starting update");
            questionData.setAnswer(questionDataOriginal.getAnswer());
            progressBar.setVisibility(View.VISIBLE);

           /* ApplicationClass.documentSnapshot.getReference().set(questionData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: update done");
                    Toasty.success(QuestionAddActivity.this, "Question saved ", Toast.LENGTH_SHORT).show();
                    finish();
                    progressBar.setVisibility(View.GONE);

                }
            });*/

            ApplicationClass.documentSnapshot.getReference().set(questionData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: question saved");
                        Toasty.success(QuestionAddActivity.this, "Question saved ", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Log.e(TAG, "onComplete: Error", task.getException());
                        Toast.makeText(QuestionAddActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    progressBar.setVisibility(View.GONE);

                }
            });
        } else {
            Log.d(TAG, "saveDataInDatabase: adding a question");
            questionData.setAnswer(questionData.getOption_a());
            progressBar.setVisibility(View.VISIBLE);
            firebaseFirestore.collection(COLLECTION_ALL_QUESTIONS).document(ApplicationClass.code).collection(COLLECTION_QUESTIONS).add(questionData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        startQuestionActivity(task);

                    } else {
                        Toast.makeText(QuestionAddActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


    }

    private void startQuestionActivity(Task<DocumentReference> task) {
        progressBar.setVisibility(View.VISIBLE);
        task.getResult().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ApplicationClass.documentSnapshot = task.getResult();
                    Log.d(TAG, "onComplete: question added successfully");
                    Toasty.success(QuestionAddActivity.this, "Question saved ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(QuestionAddActivity.this, QuestionActivity.class));
                    finish();

                } else {
                    Log.d(TAG, "onComplete: error adding question" + task.getException().toString());

                    Toast.makeText(QuestionAddActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void initWidgets() {
        progressBar = findViewById(R.id.progressBar);
        questionEdtTxt = findViewById(R.id.questionEdtTxt);
        aChoiceEdtTxt = findViewById(R.id.achoiceEdtTxt);
        bChoiceEdtTxt = findViewById(R.id.bchoiceEdtTxt);
        cChoiceEdtTxt = findViewById(R.id.cchoiceEdtTxt);
        timerEdtTxt = findViewById(R.id.dchoiceEdtTxt);
        saveBtn = findViewById(R.id.saveBtn);

    }
}
