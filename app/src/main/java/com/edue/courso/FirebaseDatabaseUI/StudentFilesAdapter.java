package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;

import com.edue.courso.FilesS;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

public class StudentFilesAdapter extends FirebaseRecyclerAdapter<FilesS, StudentFilesHolder> {
    private static final String TAG = StudentFilesAdapter.class.getSimpleName();
    Context context;

    public StudentFilesAdapter(Class<FilesS> modelClass, int modelLayout, Class<StudentFilesHolder> viewHolderClass, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(StudentFilesHolder viewHolder, FilesS model, int position) {
        String fileName = model.getFileName();

        viewHolder.name.setText(fileName);
    }

    //below override code shows list in reverse order. That is makes newest items appear first
    @Override
    public FilesS getItem(int position) {
        return super.getItem(super.getItemCount() - position - 1);
    }
}
