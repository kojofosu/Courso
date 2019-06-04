package com.edue.courso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadMaterial extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button uploadMaterialBtn;
    RecyclerView uploadMaterialRecyclerView;
    TextView uploadMaterialFileTV;
    AppBarLayout uploadMaterialAppBarLayout;
    Toolbar uploadMaterialToolbar;
    String uploadCode, uploadLevel, uploadTitle, uploadDept, uploadkey, uploadGetUDBKey, uploadProgramme;
    private static final int GET_FILE = 1313;
    private static String TAG = "UploadMaterial Activity";

    Uri filePath;
    File myFile;
    String path;
    String displayName;
    String fileUrl;
    Uri singleUri;
    Uri multipleUri;
    String uriString;
    String getUDBKey;
    String getFilesKey;

    //Declaring a DatabaseReference
    DatabaseReference mDatabaseReference;
    DatabaseReference uploadsDatabaseReference;
    DatabaseReference filesDatabaseReference;
    DatabaseReference forStudentsDatabaseReference, filesForStudentsDatabaseReference;
    DatabaseReference getCodeValueRef;

    List<String> fileNameList;
    UploadListAdapter uploadListAdapter;

    //Declaring a StorageReference
    private StorageReference mStorageRef;
    StorageReference fileStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material);

        //sharedPreferences
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        getUDBKey = sharedPreferences.getString("userID", "");

        //init
        init();

        //get intent extras
        uploadCode = getIntent().getStringExtra("uploadCode").toUpperCase();
        uploadLevel = getIntent().getStringExtra("uploadLevel");
        uploadTitle = getIntent().getStringExtra("uploadTitle");
        uploadDept = getIntent().getStringExtra("uploadDept");
        uploadkey = getIntent().getStringExtra("uploadUploadKey");
        uploadGetUDBKey = getIntent().getStringExtra("uploadGetUDBKey");
        uploadProgramme = getIntent().getStringExtra("uploadProgramme");

        Log.d(TAG, uploadCode);
        Log.d(TAG, uploadLevel);
        Log.d(TAG, uploadProgramme);
        Log.d(TAG, uploadTitle);
        Log.d(TAG, uploadDept);
        Log.d(TAG, uploadkey);

        //Initializing the databaseReference and storageReference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        forStudentsDatabaseReference = FirebaseDatabase.getInstance().getReference("students");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");

        fileNameList = new ArrayList<>();
        uploadListAdapter = new UploadListAdapter(fileNameList);
        uploadMaterialRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        uploadMaterialRecyclerView.setHasFixedSize(true);
        uploadMaterialRecyclerView.setAdapter(uploadListAdapter);

        //select file event
        selectFileEvent();

        //toolbar
        topToolbar();

    }

    private void init() {
        uploadMaterialBtn = findViewById(R.id.upload_material_upload_btn);
        uploadMaterialRecyclerView = findViewById(R.id.upload_material_recyclerView);
        uploadMaterialFileTV = findViewById(R.id.upload_material_fileTV);
        uploadMaterialAppBarLayout = findViewById(R.id.upload_material_AppBar);
        uploadMaterialToolbar = findViewById(R.id.upload_material_toolbar);
    }

    private void selectFileEvent() {

        uploadMaterialFileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for greater than lolipop versions we need the permissions asked on runtime
                //so if the permission is not available user will go to the screen to allow storage permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(UploadMaterial.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
//                    return;
                } else {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("file/*");
                    startActivityForResult(Intent.createChooser(intent,"Select files..."), GET_FILE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        switch (requestCode){
            case GET_FILE:
                if(requestCode == GET_FILE && resultCode == RESULT_OK) {
                    //Get data for multiple selected files
                    if (Objects.requireNonNull(data).getClipData() != null) {
                        fileNameList.clear();
                        final int totalItemSelected = Objects.requireNonNull(data.getClipData()).getItemCount();
                        for (int num =0; num < totalItemSelected; num++) {
                            multipleUri = data.getClipData().getItemAt(num).getUri();
                            filePath = multipleUri;
                            uriString = multipleUri.toString();
                            myFile = new File(uriString);
                            path = myFile.getAbsolutePath();
                            displayName = myFile.getName();
                            fileNameList.add(displayName);
                            uploadListAdapter.notifyDataSetChanged();

                        }
                            //when upload button is clicked
                            uploadMaterialBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //this String is here just to prevent Upload from pushing more than once
                                    final int newtotalItemSelected = Objects.requireNonNull(data.getClipData()).getItemCount();
                                    for (int snum = 0; snum < newtotalItemSelected; snum++) {

                                        multipleUri = data.getClipData().getItemAt(snum).getUri();
                                        filePath = multipleUri;
                                        uriString = multipleUri.toString();
                                        myFile = new File(uriString);
                                        path = myFile.getAbsolutePath();
                                        displayName = myFile.getName();
                                        uploadListAdapter.notifyDataSetChanged();


//                                        Toast.makeText(UploadMaterial.this, filePath.toString(), Toast.LENGTH_SHORT).show();

                                       //final Uri perFile = Uri.parse(fileNameList.get(snum));

                                        Log.d("multi : " , "multi : " + filePath);
                                        Log.d("multiDispName : " , "multiDisplay Name : " + filePath);

//                                        //adding to database to filesS
//                                        final FilesS filesS = new FilesS();
//                                        filesS.setFileName(displayName);


                                        //Initializing the StorageReference
                                        mStorageRef = FirebaseStorage.getInstance().getReference();

                                        // The simplest way to upload to your storage bucket is by uploading a local file,
                                        // such as photos and videos from the camera, using the putFile() method.
                                        // You can also upload raw data using putBytes() or from an InputStream using putStream().

                                        fileStorageRef = mStorageRef.child("CourseMaterials/" + uploadDept +"/"+ uploadProgramme +"/"+ uploadLevel +"/"+ uploadCode.toUpperCase() +"/"+ filePath);
//                                        //now after upload, we get its key and create child inside it for FilesS
//                                        filesDatabaseReference = uploadsDatabaseReference.child(uploadkey).child("files");
//                                        forStudentsDatabaseReference.child(uploadCode.toUpperCase()).child("files");

//                                        if (perFile.getLastPathSegment() != null) {
                                            final ProgressDialog progressDialog = new ProgressDialog(UploadMaterial.this);
                                            progressDialog.setTitle("Uploading...");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();

                                        //adding to database to filesS
                                        final FilesS filesS = new FilesS();
                                        filesS.setFileName(displayName);
                                            //fetching key from mDatabaseReference to use as child for the rest
                                            //key = mDatabaseReference.push().getKey();

//                                            UploadTask uploadTask = fileStorageRef.putFile(filePath);
                                        fileStorageRef.putFile(filePath)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                            // Get a URL to the uploaded content
                                                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                                            progressDialog.dismiss();
                                                            Toast.makeText(UploadMaterial.this, "Uploaded Successfully" + filePath, Toast.LENGTH_SHORT).show();
                                                            Log.d("Uploaded Successfully" , "Uploaded Successfully : " + filePath);

                                                            //now after upload, we get its key and create child inside it for FilesS
                                                            filesDatabaseReference = uploadsDatabaseReference.child(uploadkey).child("files");
                                                            forStudentsDatabaseReference.child(uploadCode.toUpperCase()).child("files");
                                                            filesForStudentsDatabaseReference = forStudentsDatabaseReference.child(uploadCode.toUpperCase()).child("files");

//                                                            for (int snum = 0; snum < newtotalItemSelected; snum++) {
                                                           //     if (uploadkey != null) {

                                                                    //getting download url of file
                                                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            final int newtotalItemSelected = Objects.requireNonNull(data.getClipData()).getItemCount();

//                                                                            for ( int mm = 0; mm < newtotalItemSelected; mm++) {
                                                                                Uri URI = uri;
                                                                                //storing url to fileUrl
                                                                                fileUrl = URI.toString();
                                                                                Log.d("file Url ", "url is : " + fileUrl);

//                                                                            //adding to database to filesS
//                                                                            FilesS filesS = new FilesS();
//                                                                            filesS.setFileName(displayName);
                                                                            filesS.setFileUrl(fileUrl);
                                                                                // filesForStudentsDatabaseReference.push().setValue(filesS);
                                                                            String filesKey = filesDatabaseReference.push().getKey();
                                                                            if (filesKey != null) {
                                                                                //String filesKey = filesDatabaseReference.push().getKey();
                                                                                //filesDatabaseReference.push().setValue(filesS);
                                                                                filesS.setFileKey(filesKey);
                                                                                filesDatabaseReference.child(filesKey).setValue(filesS);
                                                                                filesForStudentsDatabaseReference.child(filesKey).setValue(filesS);
                                                                                getFilesKey = filesKey;
                                                                            }else if (filesKey == null){
                                                                                Log.d("filekey", "filekey empty");
                                                                                Toast.makeText(UploadMaterial.this, "filekey empty", Toast.LENGTH_SHORT).show();
                                                                            }
//                                                                            }
                                                                        }
                                                                    });
//                                                                }
                                                        //    }



                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Handle unsuccessful uploads
                                                            // ...
                                                            progressDialog.dismiss();
                                                            Toast.makeText(UploadMaterial.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    progressDialog.setMessage(((int) progress + "% uploaded..."));
                                                }
                                            });

//                                        }else {
//                                            Toast.makeText(getApplicationContext(), "Empty file path", Toast.LENGTH_SHORT).show();
//                                            Log.d("EmptyFilePath ", "Empty file path for multiple files");
//                                        }

//                                        firebaseStorage();
                                    }
                                }
                            });
//                        }
                    } else if (data.getData() != null) {
                        //Get the data of a single selected file
                        singleUri = data.getData();
                        filePath = singleUri;
                        uriString = singleUri.toString();
                        myFile = new File(uriString);
                        path = myFile.getAbsolutePath();

                        displayName = myFile.getName();
                        fileNameList.add(displayName);
                        uploadListAdapter.notifyDataSetChanged();

                        //on button clicked
                        uploadMaterialBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firebaseStorage();

                            }
                        });
                    }
                }break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseStorage() {
        //Initializing the StorageReference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // The simplest way to upload to your storage bucket is by uploading a local file,
        // such as photos and videos from the camera, using the putFile() method.
        // You can also upload raw data using putBytes() or from an InputStream using putStream().

        fileStorageRef = mStorageRef.child("CourseMaterials/" + uploadDept +"/"+ uploadProgramme +"/"+ uploadLevel +"/"+ uploadCode.toUpperCase() +"/"+displayName);

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //fetching key from mDatabaseReference to use as child for the rest
            //key = mDatabaseReference.push().getKey();

            fileStorageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            progressDialog.dismiss();
                            Toast.makeText(UploadMaterial.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            if (uploadkey != null) {
                                //now after upload, we get its key and create child inside it for FilesS
                                filesDatabaseReference = uploadsDatabaseReference.child(uploadkey).child("files");
                            }
                            filesForStudentsDatabaseReference = forStudentsDatabaseReference.child(uploadCode).child("files");


                            //getting download url of file
                            fileStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri URI = task.getResult();
                                        //storing url to fileUrl
                                        fileUrl = URI.toString();
                                        Log.d("file Url ", "url is : " + fileUrl);

                                        filesDatabaseReference = uploadsDatabaseReference.child(uploadkey).child("files");
                                        filesForStudentsDatabaseReference = forStudentsDatabaseReference.child(uploadCode.toUpperCase()).child("files");


                                        //adding to database to filesS
                                        FilesS filesS = new FilesS();
                                        filesS.setFileName(displayName);
                                        filesS.setFileUrl(fileUrl);

                                        String filesKey = filesDatabaseReference.push().getKey();
                                        if (filesKey != null) {
                                            filesS.setFileKey(filesKey);
                                            filesDatabaseReference.child(filesKey).setValue(filesS);
                                            filesForStudentsDatabaseReference.child(filesKey).setValue(filesS);
                                            getFilesKey = filesKey;
                                        }

                                    } else if (!task.isSuccessful()) {
                                        Toast.makeText(UploadMaterial.this, "FAILED TO GET URL ", Toast.LENGTH_SHORT).show();
                                        Log.d("file Url ", "FAILED TO GET URL : ");
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(UploadMaterial.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage(((int) progress + "% uploaded..."));
                }
            });

        }else {
            Toast.makeText(this, "Empty file path", Toast.LENGTH_SHORT).show();
        }
    }

    private void topToolbar() {
        uploadMaterialToolbar.getNavigationIcon();
        uploadMaterialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
