package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Toast;

import com.edue.courso.FilesS;
import com.edue.courso.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FilesAdapter extends FirebaseRecyclerAdapter<FilesS, FilesHolder> {
    private static final String TAG = FilesAdapter.class.getSimpleName();
    Context context;

    public FilesAdapter(Class<FilesS> modelClass, int modelLayout, Class<FilesHolder> viewHolderClass, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(final FilesHolder viewHolder, FilesS model, final int position) {
        String fileName = model.getFileName();
        final String fileUrl = model.getFileUrl();

        viewHolder.name.setText(fileName);

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce_down);
                viewHolder.deleteFile.setVisibility(View.VISIBLE);
                viewHolder.deleteFile.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        viewHolder.name.setVisibility(View.INVISIBLE);
                        Log.d("pupu : ", "Pupu : " + viewHolder.itemView.getId());
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        viewHolder.deleteFile.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.bounce_up);
                                viewHolder.deleteFile.startAnimation(animation1);
                                viewHolder.deleteFile.setVisibility(View.GONE);
                                animation1.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        viewHolder.name.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        viewHolder.deleteFile.clearAnimation();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                //returning true makes sure no other things are called like the onClick function
                                return true;
                            }
                        });
                        viewHolder.deleteFile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String fileKey = getItem(position).getFileKey();
                                final DatabaseReference forStudentsDatabaseReference = FirebaseDatabase.getInstance().getReference("students/files" );
                                getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                            Log.d("code ", "code is : " + fileKey);


                                            forStudentsDatabaseReference.child(fileKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        // Toast.makeText(context, "student side for "+ getCode + "is deleted successfully", Toast.LENGTH_SHORT).show();
                                                        Log.d("StudentSideDel" , "student side for "+ fileKey + "is deleted successfully");
                                                    }else if (!task.isSuccessful()){
                                                        //Toast.makeText(context, "student side for "+ getCode + "UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
                                                        Log.d("StudentSideDel" , "student side for "+ fileKey + "UNSUCCESSFUL");
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
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                //returning true makes sure no other things are called like the onClick function
                return true;
            }
        });

        viewHolder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open link in a browser
               // WebView webView = new WebView()

                //Download to phone
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
