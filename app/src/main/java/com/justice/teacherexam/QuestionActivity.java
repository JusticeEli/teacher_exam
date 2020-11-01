package com.justice.teacherexam;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.SetOptions;

public class QuestionActivity extends AppCompatActivity {
    private TextView questionTxtView;

    private RadioGroup radioGroup;
    private RadioButton aRadioButton;
    private RadioButton bRadioButton;
    private RadioButton cRadioButton;
    private Button saveBtn;
    private Button editBtn;
    private QuestionModel questionDataOriginal;
    private TextView timerTxtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        questionDataOriginal = ApplicationClass.documentSnapshot.toObject(QuestionModel.class);
        initWidgets();
        setOnClickListeners();
        setDefaultValues();
    }

    private void setDefaultValues() {
        questionTxtView.setText(questionDataOriginal.getQuestion());
        aRadioButton.setText(questionDataOriginal.getOption_a());
        bRadioButton.setText(questionDataOriginal.getOption_b());
        cRadioButton.setText(questionDataOriginal.getOption_c());
        timerTxtView.setText(questionDataOriginal.getTimer() + " seconds to answer the question");

        setTheAnswerForQuestion();

    }

    private void setTheAnswerForQuestion() {

        if (questionDataOriginal.getAnswer().equals(questionDataOriginal.getOption_a())) {
            aRadioButton.setChecked(true);
        } else if (questionDataOriginal.getAnswer().equals(questionDataOriginal.getOption_b())) {
            bRadioButton.setChecked(true);
        } else if (questionDataOriginal.getAnswer().equals(questionDataOriginal.getOption_c())) {
            cRadioButton.setChecked(true);

        }
    }

    private void setOnClickListeners() {

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuestionActivity.this, QuestionAddActivity.class));
                onBackPressed();
            }
        });
    }

    private void initWidgets() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        questionTxtView = findViewById(R.id.questionTxtView);
        radioGroup = findViewById(R.id.radioGroup);
        aRadioButton = findViewById(R.id.aRadioBtn);
        bRadioButton = findViewById(R.id.bRadioBtn);
        cRadioButton = findViewById(R.id.cRadioBtn);
        timerTxtView = findViewById(R.id.timerTxtView);
        saveBtn = findViewById(R.id.saveBtn);
        editBtn = findViewById(R.id.editBtn);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteMenu) {
            ApplicationClass.documentSnapshot.getReference().delete();
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        updateAnswer();
        super.onBackPressed();
    }

    private void updateAnswer() {
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String answer = radioButton.getText().toString();

        questionDataOriginal.setAnswer(answer);
         ApplicationClass.documentSnapshot.getReference().set(questionDataOriginal, SetOptions.merge());
    }
}
