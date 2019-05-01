package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;

import com.edue.courso.FilesS;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

public class FilesAdapter extends FirebaseRecyclerAdapter<FilesS, FilesHolder> {
    private static final String TAG = FilesAdapter.class.getSimpleName();
    Context context;

    public FilesAdapter(Class<FilesS> modelClass, int modelLayout, Class<FilesHolder> viewHolderClass, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(FilesHolder viewHolder, FilesS model, int position) {
        String fileName = model.getFileName();

        viewHolder.name.setText(fileName);
    }
}
