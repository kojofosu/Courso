package com.edue.courso.FirebaseDatabaseUI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edue.courso.R;

public class StudentFilesHolder extends RecyclerView.ViewHolder {
    private static final String TAG = FilesHolder.class.getSimpleName();
    TextView name;
    ImageView thumbnail;

    public StudentFilesHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.student_material_item_name);
        thumbnail = itemView.findViewById(R.id.student_material_item_IV);
    }
}
