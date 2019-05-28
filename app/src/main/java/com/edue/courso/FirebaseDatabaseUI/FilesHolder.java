package com.edue.courso.FirebaseDatabaseUI;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.edue.courso.R;
public class FilesHolder extends RecyclerView.ViewHolder {
    private static final String TAG = FilesHolder.class.getSimpleName();
    TextView name;
    ImageView thumbnail;
    ImageView download;
    ImageButton deleteFile;

    public FilesHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.add_material_item_name);
        thumbnail = itemView.findViewById(R.id.add_material_item_IV);
        download = itemView.findViewById(R.id.add_material_download);
        deleteFile = itemView.findViewById(R.id.add_material_item_delete_thumbnail);
    }
}
