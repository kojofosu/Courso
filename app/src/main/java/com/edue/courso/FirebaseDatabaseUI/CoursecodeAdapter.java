package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;

import com.edue.courso.Upload;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class CoursecodeAdapter extends FirebaseRecyclerAdapter<Upload, CourseCodeHolder> {
    private static final String TAG = CoursecodeAdapter.class.getSimpleName();
    private Context context;

//    public CoursecodeAdapter(Class<Upload> modelClass, int modelLayout, Class<CourseCodeHolder> viewHolderClass, Query ref) {
//        super(modelClass, modelLayout, viewHolderClass, ref);
//        this.context = context;
//    }

    public CoursecodeAdapter(Class<Upload> modelClass, int modelLayout, Class<CourseCodeHolder> viewHolderClass, DatabaseReference ref, Context context ) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;

    }

    @Override
    protected void populateViewHolder(CourseCodeHolder viewHolder, Upload model, int position) {
        viewHolder.title.setText(model.getCourseName());
        viewHolder.code.setText(model.getCourseCodes());
    }
}
