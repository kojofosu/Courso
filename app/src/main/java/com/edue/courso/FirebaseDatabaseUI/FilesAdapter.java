package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

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
        final String fileUrl = model.getFileUrl();

        viewHolder.name.setText(fileName);
        viewHolder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "URL : " + fileUrl, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //below override code shows list in reverse order. That is makes newest items appear first
    @Override
    public FilesS getItem(int position) {
        return super.getItem(super.getItemCount() - position - 1);
    }
}
