package com.edue.courso.FirebaseDatabaseUI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edue.courso.R;

public class CourseCodeHolder extends RecyclerView.ViewHolder {
    private static final String TAG = CourseCodeHolder.class.getSimpleName();
    public TextView title, code;


    public CourseCodeHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.item_title);
        code = itemView.findViewById(R.id.item_code);
    }
}
