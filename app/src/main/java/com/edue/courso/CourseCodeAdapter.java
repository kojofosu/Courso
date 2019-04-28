package com.edue.courso;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CourseCodeAdapter extends RecyclerView.Adapter<CourseCodeAdapter.CourseViewHolder>{

    private Context mContext;
    private List<Upload> datalist;
    private OnItemClickListener mListener;
    RecyclerView recyclerView;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CourseCodeAdapter(Context mContext, List<Upload> datalist) {
        this.mContext = mContext;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, viewGroup, false);

        recyclerView = view.findViewById(R.id.home_recyclerView);

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder courseViewHolder, int i) {
        Upload currentItem = datalist.get(i);

        String course_title = currentItem.getCourseCodes();

        courseViewHolder.title.setText(course_title);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView title;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);

            //For when an item is clicked on the recycler view in the adapter
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
