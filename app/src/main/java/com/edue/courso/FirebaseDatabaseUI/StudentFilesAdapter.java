package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

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
        final String fileName = model.getFileName();
        final String fileUrl = model.getFileUrl();

        viewHolder.name.setText(fileName);

        viewHolder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open link in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(browserIntent);
            }
        });
    }

    //below override code shows list in reverse order. That is makes newest items appear first
    @Override
    public FilesS getItem(int position) {
        return super.getItem(super.getItemCount() - position - 1);
    }
}
