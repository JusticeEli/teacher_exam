package com.justice.teacherexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

import static com.justice.teacherexam.ApplicationClass.COLLECTION_ALL_QUESTIONS;
import static com.justice.teacherexam.ApplicationClass.COLLECTION_QUESTIONS;
import static com.justice.teacherexam.MainActivity.CODE;

public class TeacherFirstPageActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fob;
    private TeacherFirstPageAdapter adapter;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_first_page);

        //    deleteAllQuestions();
        initWidgets();
        setOnClickListeners();
        setUpRecyclerAdapter();
    }

    private void setUpRecyclerAdapter() {
        Query query = firebaseFirestore.collection(COLLECTION_ALL_QUESTIONS).document(ApplicationClass.code).collection(COLLECTION_QUESTIONS);
        FirestoreRecyclerOptions<QuestionModel> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionModel>().setQuery(query, QuestionModel.class).setLifecycleOwner(this).build();

        adapter = new TeacherFirstPageAdapter(this, firestoreRecyclerOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                progressBar.setVisibility(View.VISIBLE);
                adapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(TeacherFirstPageActivity.this, "deletion success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(TeacherFirstPageActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });


            }
        }).attachToRecyclerView(recyclerView);

    }


    private void setOnClickListeners() {
        fob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationClass.documentSnapshot = null;
                startActivity(new Intent(TeacherFirstPageActivity.this, QuestionAddActivity.class));
            }
        });
    }

    private void initWidgets() {
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        fob = findViewById(R.id.fob);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteMenu) {

            deleteAllQuestions();

        } else if (item.getItemId() == R.id.studentMenu) {
            startActivity(new Intent(this, StudentsMarksActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllQuestions() {
        firebaseFirestore.collection(COLLECTION_ALL_QUESTIONS).document(ApplicationClass.code).collection(COLLECTION_QUESTIONS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    documentSnapshot.getReference().delete();
                }
            }
        });
    }
}
