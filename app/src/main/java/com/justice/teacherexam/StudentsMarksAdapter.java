package com.justice.teacherexam;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static com.justice.teacherexam.ApplicationClass.STUDENT_ID;

public class StudentsMarksAdapter extends FirestoreRecyclerAdapter<Student, StudentsMarksAdapter.ViewHolder> {
    private static final String TAG = "StudentsMarksAdapter";
    private Context context;

    public StudentsMarksAdapter(Context context, @NonNull FirestoreRecyclerOptions<Student> options) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Student model) {
        holder.studentNameTxtView.setText(model.getFirstName()+" "+model.getLastName());
     //   holder.studentIdTxtView.setText(model.getId());

        Results results=model.getResults();
        int outOf= (int) (results.getCorrect()+results.getWrong()+results.getUnanswered());
        holder.studentMarksTxtView.setText(results.getCorrect()+" out of "+outOf);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ///setting progress bar back to gone
        Log.d(TAG, "onCreateViewHolder: setting progress bar to invisible");
        StudentsMarksActivity studentsMarksActivity=(StudentsMarksActivity)context;
        studentsMarksActivity.progressBar.setVisibility(View.GONE);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_students_marks, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView studentNameTxtView;
       private TextView studentMarksTxtView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTxtView = itemView.findViewById(R.id.studentNameTxtView);
            studentMarksTxtView = itemView.findViewById(R.id.studentsMarksTxtView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,TestCompleteActivity.class);
                    intent.putExtra(STUDENT_ID,getItem(getAdapterPosition()).getStudentId());
                    context.startActivity(intent);


                }
            });
        }
    }


}
