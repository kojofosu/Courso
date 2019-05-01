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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddNewMaterial extends AppCompatActivity {
    TextInputLayout addCourseCode_TIL, addCourseTitle_TIL;
    TextInputEditText addCourseCode_TIE, addCourseTitle_TIE;
    CollapsingToolbarLayout addNewCollapsingToolbarLayout;
    AppBarLayout addNewAppBarLayout;
    ImageButton uploadBtn;
    Spinner addNewDeptSpinner, addNewProgrammeSpinner, addNewLevelSpinner, addNewSemesterSpinner;
    String[] deptArray = {"Dept. Computer Science and Information Technology", "Department of Education"};
    String[] programmeArray = {"Non-Degree", "Undergraduate", "Masters/Graduate", "Doctorate"};
    String[] levelArray = {"100", "200", "300", "400", "500", "600"};
    String[] semesterArray = {"First Semester" , "Second Semester"};
    TextView addNewFileTV;
    Toolbar addNewtoolbar;
    String courseCodeText, courseCodeHint, courseTitleText, courseTitleHint, deptText, programmeText, levelText;
    Uri filePath;
    File myFile;
    String path;
    String displayName;
    String fileUrl;
    Uri singleUri;
    Uri multipleUri;
    String uriString;
    String getUDBKey, getUploadKey, getFilesKey;
    String key, keyCourseCode;

    SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    private List<String> fileNameList;
    private UploadListAdapter uploadListAdapter;

    //Declaring a StorageReference
    private StorageReference mStorageRef;
    StorageReference fileStorageRef;

    //Declaring a DatabaseReference
    DatabaseReference mDatabaseReference;
    DatabaseReference uploadsDatabaseReference;
    DatabaseReference filesDatabaseReference;
    DatabaseReference getCodeValueRef;
    private static final int GET_FILE = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_material);

        //sharedPreferences
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        getUDBKey = sharedPreferences.getString("userDatabaseKey", "");

        //Initializing the databaseReference and storageReference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
        //The stress below is to get the course code from tne database LMAO.
        uploadsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot uniquekeyDatasnapshot : dataSnapshot.getChildren()) {
                    key = uniquekeyDatasnapshot.getKey();
                    Log.d("uniquekeyDatasnapshot" , "uniquekeyDatasnapshot is : " +key);
                    getCodeValueRef = uploadsDatabaseReference.child(key);

                    //on button clicked
                    uploadBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deptText = addNewDeptSpinner.getSelectedItem().toString();
                            programmeText = addNewProgrammeSpinner.getSelectedItem().toString();
                            levelText = addNewLevelSpinner.getSelectedItem().toString();

                            //checks if there is an existing course code or course name
                            getCodeValueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshott) {

                                    for (DataSnapshot innerSnapshot : dataSnapshot.getChildren()) {
                                        String aa = innerSnapshot.getValue().toString();
                                        Log.d("inner : ", "inner  : " + aa);
                                    }
//                                        getCodeValueRef.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                for (DataSnapshot anotherkeyDatasnapshot : dataSnapshot.)
//                                                keyCourseCode = dataSnapshot.getValue().toString();
//                                                if (keyCourseCode.equalsIgnoreCase(courseCodeText)){
//                                                    Toast.makeText(AddNewMaterial.this, "Course Already Exits" , Toast.LENGTH_LONG).show();
//                                                }else if (!keyCourseCode.equalsIgnoreCase(courseCodeText)){
//                                                    Toast.makeText(AddNewMaterial.this, "DOESNT EIxist eih", Toast.LENGTH_SHORT).show();
////                                                            //start the firebase upload
////                                                            firebaseStorage();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        })
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //initialize
        init();

        fileNameList = new ArrayList<>();
        uploadListAdapter = new UploadListAdapter(fileNameList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(uploadListAdapter);

        //collapsible toolbar
        collapsibleToolbar();

        //select file onClick event
        selectFileEvent();

        //initializing spinners
        spinners();

        //fireBase Database
        //readFromDatabase();

    }

//    private void readFromDatabase() {
//        //Initializing the databaseReference
//        getUDBKey = sharedPreferences.getString("userDatabaseKey", "");
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
//        //fetching key from mDatabaseReference to use as child for the rest
//        key = mDatabaseReference.push().getKey();
//        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
//
//
//        mDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //below code takes data from DB
//                User user = dataSnapshot.child(getUDBKey).getValue(User.class);
//                String userName, userEmail, userPhone;
//                if (user != null) {
//                    //get the values
//                    userName = user.getFullName();
//                    userEmail = user.getEmail();
//                    userPhone = user.getPhone();
//                    //set values to the various editTexts
//                    addNewLecturerNameET.setText(userName);
//                    addNewLecturerEmailET.setText(userEmail);
//                    addNewLecturerContactET.setText(userPhone);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w("AddNewActivity : ", "Failed to read value.", databaseError.toException());
//            }
//        });
//
//    }

    private void spinners() {
        //initializing spinner array for department
        ArrayAdapter<String> deptArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, deptArray);
        addNewDeptSpinner.setAdapter(deptArrayAdapter);

        //initializing spinner array for programmes
        ArrayAdapter<String> programmeArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, programmeArray);
        addNewProgrammeSpinner.setAdapter(programmeArrayAdapter);

        //initializing spinner array for level
        ArrayAdapter<String> levelArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, levelArray);
        addNewLevelSpinner.setAdapter(levelArrayAdapter);

        //initializing spinner array for semester
        ArrayAdapter<String> semesterArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, semesterArray);
        addNewSemesterSpinner.setAdapter(semesterArrayAdapter);


    }

    private void firebaseStorage() {
        //Initializing the StorageReference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // The simplest way to upload to your storage bucket is by uploading a local file,
        // such as photos and videos from the camera, using the putFile() method.
        // You can also upload raw data using putBytes() or from an InputStream using putStream().

        fileStorageRef = mStorageRef.child("CourseMaterials/" + deptText +"/"+ programmeText +"/"+ levelText +"/"+ courseCodeText +"/"+displayName);

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
                            Toast.makeText(AddNewMaterial.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();


                            //adding to database to upload
                            Upload upload = new Upload();
                            upload.setDeptName(deptText);
                            upload.setLevelNum(levelText);
                            upload.setCourseCodes(courseCodeText);
                            upload.setCourseName(courseTitleText);
                            String uploadKey = uploadsDatabaseReference.push().getKey();
                            if (uploadKey != null) {
                                upload.setUploadKey(uploadKey);
                                uploadsDatabaseReference.child(uploadKey).setValue(upload);

                                getUploadKey = uploadKey;
                                //now after upload, we get its key and create child inside it for FilesS
                                filesDatabaseReference = uploadsDatabaseReference.child(uploadKey).child("files");
                            }


                            //getting download url of file
                            fileStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri URI = task.getResult();
                                        //storing url to fileUrl
                                        fileUrl = URI.toString();
                                        Log.d("file Url ", "url is : " + fileUrl);

                                        //adding to database to filesS
                                        FilesS filesS = new FilesS();
                                        filesS.setFileName(displayName);
                                        filesS.setFileUrl(fileUrl);
                                        String filesKey = filesDatabaseReference.push().getKey();
                                        if (filesKey != null) {
                                            filesS.setFileKey(filesKey);
                                            filesDatabaseReference.child(filesKey).setValue(filesS);
                                            getFilesKey = filesKey;
                                        }

                                    } else if (!task.isSuccessful()) {
                                        Toast.makeText(AddNewMaterial.this, "FAILED TO GET URL ", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddNewMaterial.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void init() {
        addCourseCode_TIL = findViewById(R.id.add_course_code_TIL);
        addCourseTitle_TIL = findViewById(R.id.add_course_title_TIL);
        addCourseCode_TIE = findViewById(R.id.add_course_code_TIE);
        addCourseTitle_TIE = findViewById(R.id.add_course_title_TIE);
        addNewCollapsingToolbarLayout = findViewById(R.id.addNNew_CollapsingToolbarLayout);
        addNewtoolbar = findViewById(R.id.addNew_toolbar);
        addNewAppBarLayout = findViewById(R.id.addNew_AppBar);
        addNewFileTV = findViewById(R.id.addNew_fileTV);
        uploadBtn = findViewById(R.id.upload_btn);

        addNewDeptSpinner = findViewById(R.id.addNew_dept_Spinner);
        addNewProgrammeSpinner = findViewById(R.id.addNew_programme_Spinner);
        addNewLevelSpinner = findViewById(R.id.addNew_level_Spinner);
        addNewSemesterSpinner = findViewById(R.id.addNew_semester_Spinner);
        recyclerView = findViewById(R.id.addNew_recyclerView);

        courseCodeText = addCourseCode_TIE.getText().toString();
        courseCodeHint = addCourseCode_TIE.getHint().toString();
        courseTitleText = addCourseTitle_TIE.getText().toString();
        courseTitleHint = addCourseTitle_TIE.getHint().toString();

        addCourseCode_TIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                courseCodeText = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addCourseTitle_TIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                courseTitleText = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void collapsibleToolbar() {
        //appBar offset to determine when collapsible toolbar is collapsed or expanded
        addNewAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow =  true;
            int scrollRange = -1;

            @Override
            //i here is the vertical offset
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0){
                    if(!courseCodeText.isEmpty()){
                        addNewCollapsingToolbarLayout.isTitleEnabled();
                        addNewCollapsingToolbarLayout.setTitle(courseCodeText);
                    }else{
                        addNewCollapsingToolbarLayout.setTitle(courseCodeHint);
                    }
                    isShow = true;
                }else if(isShow){
                    addNewCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });


    }

    private void selectFileEvent() {

        addNewFileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for greater than lolipop versions we need the permissions asked on runtime
                //so if the permission is not available user will go to the screen to allow storage permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(AddNewMaterial.this,
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
                    if (data.getClipData() != null) {
                        final int totalItemSelected = data.getClipData().getItemCount();
                        for (int num =0; num < totalItemSelected; num++){
                            multipleUri = data.getClipData().getItemAt(num).getUri();
                            filePath = multipleUri;
                            uriString = multipleUri.toString();
                            myFile = new File(uriString);
                            path = myFile.getAbsolutePath();
                            displayName = myFile.getName();
                            fileNameList.add(displayName);
                            uploadListAdapter.notifyDataSetChanged();

                            //when upload button is clicked
                            uploadBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deptText = addNewDeptSpinner.getSelectedItem().toString();
                                    programmeText = addNewProgrammeSpinner.getSelectedItem().toString();
                                    levelText = addNewLevelSpinner.getSelectedItem().toString();

                                    for (int snum = 0; snum < totalItemSelected; snum++) {
                                        multipleUri = data.getClipData().getItemAt(snum).getUri();
                                        filePath = multipleUri;
                                        uriString = multipleUri.toString();
                                        myFile = new File(uriString);
                                        path = myFile.getAbsolutePath();
                                        displayName = myFile.getName();

                                        uploadListAdapter.notifyDataSetChanged();
                                        firebaseStorage();
                                    }
                                }
                            });
                        }
                    } else if (data.getData() != null) {
                        //Get the data of a single selected file
                        singleUri = data.getData();
                        filePath = singleUri;
                        uriString = singleUri.toString();
                        myFile = new File(uriString);
                        path = myFile.getAbsolutePath();

//                        if (uriString.startsWith("content://")) {
//                            try (Cursor cursor = getApplicationContext().getContentResolver().query(singleUri, null, null, null, null)) {
//                                if (cursor != null && cursor.moveToFirst()) {
//                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                                    Toast.makeText(this, displayName, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                            fileNameList.add(displayName);
                            uploadListAdapter.notifyDataSetChanged();


//                        }
                    }
                }break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
