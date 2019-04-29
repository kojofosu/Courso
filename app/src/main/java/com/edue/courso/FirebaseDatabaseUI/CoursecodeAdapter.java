package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.edue.courso.AddMaterials;
import com.edue.courso.R;
import com.edue.courso.Upload;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

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
    protected void populateViewHolder(final CourseCodeHolder viewHolder, Upload model, final int position) {
        final String TITLE = model.getCourseName();
        final String CODE = model.getCourseCodes();
        final String DEPT = model.getDeptName();

        viewHolder.title.setText(TITLE);
        viewHolder.code.setText(CODE);

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce_down);
                viewHolder.deleteClass.startAnimation(animation);
                viewHolder.deleteClass.setVisibility(View.VISIBLE);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        viewHolder.code.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "delete btn must show", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewHolder.deleteClass.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.bounce_up);
                                viewHolder.deleteClass.startAnimation(animation1);
                                viewHolder.deleteClass.setVisibility(View.GONE);
                                animation1.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        viewHolder.code.setVisibility(View.VISIBLE);
                                        Toast.makeText(context, "animation up start", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        viewHolder.deleteClass.clearAnimation();
                                        Toast.makeText(context, "animation up ended", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                //returning true makes sure no other things are called like the onClick function
                                return true;
                            }
                        });
                        viewHolder.deleteClass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                                        }else if (!task.isSuccessful()){
                                            Toast.makeText(context, "Deletion Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                //returning true makes sure no other things are called like the onClick function
                return true;
            }
        });

        //onClick listener for items in recycler view
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddMaterials.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Title", TITLE);
                intent.putExtra("Code", CODE);
                intent.putExtra("Dept", DEPT);
                context.startActivity(intent);
                notifyDataSetChanged();
                Toast.makeText(context, getItem(position).getCourseCodes(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
