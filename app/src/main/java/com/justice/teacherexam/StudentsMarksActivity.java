package com.justice.teacherexam;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.justice.teacherexam.ApplicationClass.COLLECTION_ALL_RESULTS;
import static com.justice.teacherexam.ApplicationClass.COLLECTION_RESULTS;


public class StudentsMarksActivity extends AppCompatActivity {
    private static final String TAG = "StudentsMarksActivity";

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    public ProgressBar progressBar;
    private StudentsMarksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_marks);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        setUpRecyclerAdapter();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteMenu) {
            deleteAllStudentsMarks();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllStudentsMarks() {
        firebaseFirestore.collection(COLLECTION_ALL_RESULTS).document(ApplicationClass.code).collection(COLLECTION_RESULTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    documentSnapshot.getReference().delete();
                }
            }
        });
    }

    private void setUpRecyclerAdapter() {


//setting progress bar to visible
        Log.d(TAG, "setUpRecyclerAdapter: setting progress bar to visible");
        progressBar.setVisibility(View.VISIBLE);
        Query query = firebaseFirestore.collection(COLLECTION_ALL_RESULTS).document(ApplicationClass.code).collection(COLLECTION_RESULTS);
        FirestoreRecyclerOptions<Student> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student.class).setLifecycleOwner(this).build();
        adapter = new StudentsMarksAdapter(this, firestoreRecyclerOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "setUpRecyclerAdapter: num of items" + adapter.getItemCount());


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: deletion success");
                            Toast.makeText(StudentsMarksActivity.this, "deletion success", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onComplete: error" + task.getException());
                            Toast.makeText(StudentsMarksActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

    }
}
