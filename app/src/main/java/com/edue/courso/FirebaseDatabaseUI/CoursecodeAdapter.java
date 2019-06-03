package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.edue.courso.AddMaterials;
import com.edue.courso.FilesS;
import com.edue.courso.Home;
import com.edue.courso.R;
import com.edue.courso.SignIn;
import com.edue.courso.Upload;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

public class CoursecodeAdapter extends FirebaseRecyclerAdapter<Upload, CourseCodeHolder>{
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
        final String CODE = model.getCourseCodes().toUpperCase();
        final String DEPT = model.getDeptName();
        final String UPLOADKEY = model.getUploadKey();
        final String PROGRAMME = model.getProgramme();
        final String LEVEL = model.getLevelNum();

        //SharedPreferences
        final SharedPreferences sharedPreferences = context.getSharedPreferences("login" , MODE_PRIVATE);

        final String getUDBKey = sharedPreferences.getString("userID", "");

        viewHolder.title.setText(TITLE);
        viewHolder.code.setText(CODE);


        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(context, "onLong on " + viewHolder.code.getText().toString(), Toast.LENGTH_SHORT).show();


                viewHolder.deleteClass.setVisibility(View.VISIBLE);
                viewHolder.animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce_down);
                viewHolder.deleteClass.startAnimation(viewHolder.animation);
                viewHolder.animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        viewHolder.code.setVisibility(View.INVISIBLE);
                        Log.d("pupu : ", "Pupu : " + viewHolder.code.getText().toString());
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewHolder.deleteClass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //final String getCode = getItem(position).getCourseCodes().toUpperCase();
                                Toast.makeText(context, " delete " + CODE, Toast.LENGTH_SHORT).show();
                                final DatabaseReference uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
                                final DatabaseReference forStudentsDatabaseReference = FirebaseDatabase.getInstance().getReference("students");
                                uploadsDatabaseReference.child(UPLOADKEY).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Deleted" + CODE, Toast.LENGTH_SHORT).show();
                                            Log.d("code ", "code is : " + CODE);
                                            notifyDataSetChanged();

                                            forStudentsDatabaseReference.child(CODE).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        // Toast.makeText(context, "student side for "+ getCode + "is deleted successfully", Toast.LENGTH_SHORT).show();
                                                        Log.d("StudentSideDel" , "student side for "+ CODE + "is deleted successfully");
                                                    }else if (!task.isSuccessful()){
                                                        //Toast.makeText(context, "student side for "+ getCode + "UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
                                                        Log.d("StudentSideDel" , "student side for "+ CODE + "UNSUCCESSFUL");
                                                    }
                                                }
                                            });
                                        }else if (!task.isSuccessful()){
                                            Toast.makeText(context, "Deletion Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

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
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        viewHolder.deleteClass.clearAnimation();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                //returning true makes sure no other things are called like the onClick function
                                return true;
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
//                Intent newIntent = new Intent(context.getApplicationContext(), FilesAdapter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Title", TITLE);
                intent.putExtra("Code", CODE);
                intent.putExtra("Dept", DEPT);
                intent.putExtra("UploadKey", UPLOADKEY);
//                newIntent.putExtra("UploadKey", UPLOADKEY);
                intent.putExtra("Programme", PROGRAMME);
                intent.putExtra("Level", LEVEL);
                //put in sharedPrefs for files of student side
                sharedPreferences.edit().putString("codeForStudentSide", CODE).apply();
                sharedPreferences.edit().putString("upKeyForStudentSide", UPLOADKEY).apply();
                context.startActivity(intent);
//                notifyDataSetChanged();


            }
        });
    }

    //below override code shows list in reverse order. That is makes newest items appear first
    @Override
    public Upload getItem(int position) {
        return super.getItem(super.getItemCount() - position - 1);
    }

}
