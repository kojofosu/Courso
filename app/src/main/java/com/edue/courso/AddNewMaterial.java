package com.edue.courso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class AddNewMaterial extends AppCompatActivity {

    TextInputLayout addCourseCode_TIL, addCourseTitle_TIL;
    TextInputEditText addCourseCode_TIE, addCourseTitle_TIE;
    CollapsingToolbarLayout addNewCollapsingToolbarLayout;
    AppBarLayout addNewAppBarLayout;
    ImageButton uploadBtn;
    EditText addNewLecturerNameET, addNewLecturerEmailET, addNewLecturerContactET, addNewAuthorET;
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

    //Declaring a StorageReference
    private StorageReference mStorageRef;

    //Declaring a DatabaseReference
    DatabaseReference mDatabaseReference;
    DatabaseReference levelDatabaseReference;
    DatabaseReference courseDatabaseReference;
    DatabaseReference courseFileDatabaseReference;

    private static final int GET_FILE = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_material);

        //initialize
        init();

        //collapsible toolbar
        collapsibleToolbar();

        //select file onClick event
        selectFileEvent();

        //FireBase Storage
        firebaseStorage();

        //initializing spinners
        spinners();

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deptText = addNewDeptSpinner.getSelectedItem().toString();
                programmeText = addNewProgrammeSpinner.getSelectedItem().toString();
                levelText = addNewLevelSpinner.getSelectedItem().toString();
                firebaseStorage();
            }
        });

    }

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

        //Initializing the databaseReference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        //fetching key from mDatabaseReference to use as child for the rest
        final String key = mDatabaseReference.push().getKey();
        levelDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS +"/"+ key + "/Level");
        courseDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS +"/"+ key +"/Level/Course");
        courseFileDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS +"/"+ key +"/Level/Course" + "/Files");

        // The simplest way to upload to your storage bucket is by uploading a local file,
        // such as photos and videos from the camera, using the putFile() method.
        // You can also upload raw data using putBytes() or from an InputStream using putStream().
        //Uri file = Uri.fromFile(new File(path));

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference riversRef = mStorageRef.child("CourseMaterials/" + deptText +"/"+ programmeText +"/"+ levelText +"/"+ courseCodeText +"/"+displayName);

            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            progressDialog.dismiss();
                            Toast.makeText(AddNewMaterial.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            //adding to database to upload
                            Upload upload = new Upload();
                            upload.setDeptName(deptText);
                            mDatabaseReference.child(key).setValue(upload);
                            //mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);

                            Level level = new Level();
                            level.setLevelNum(levelText);
                            levelDatabaseReference.setValue(level);

                            Course course = new Course();
                            course.setCourseCodes(courseCodeText);
                            course.setCourseName(courseTitleText);
                            course.setFile(displayName);
                            courseDatabaseReference.setValue(course);
                            //courseDatabaseReference.child(levelDatabaseReference.push().getKey()).setValue(course);

//                            //Adding to database to course code
//                            CourseCode courseCode = new CourseCode();
//                            courseCode.setCodeName(courseCodeText);
//
//                            courseCodeDatabaseReference.child(courseFileDatabaseReference.push().getKey()).setValue(courseCode);
//
//                            //Adding to database to files
//                            Files files = new Files();
//                            files.setFileName(displayName);
//
//                            courseFileDatabaseReference.child(courseFileDatabaseReference.push().getKey()).setValue(files);
//
//
//
//

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
        addNewLecturerNameET = findViewById(R.id.addNew_lecturer_name_ET);
        addNewLecturerEmailET = findViewById(R.id.addNew_lecturer_email_ET);
        addNewLecturerContactET = findViewById(R.id.addNew_lecturer_contact_ET);
        addNewDeptSpinner = findViewById(R.id.addNew_dept_Spinner);
        addNewProgrammeSpinner = findViewById(R.id.addNew_programme_Spinner);
        addNewLevelSpinner = findViewById(R.id.addNew_level_Spinner);
        addNewSemesterSpinner = findViewById(R.id.addNew_semester_Spinner);

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
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, GET_FILE);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case GET_FILE:
                if(resultCode == RESULT_OK){
                    //Get the URI of the selected file
                    Uri uri = data.getData();
                    filePath = uri;
                    String uriString = uri.toString();
                    myFile = new File(uriString);
                    path = myFile.getAbsolutePath();
                    displayName = null;

                    if (uriString.startsWith("content://")){
                        try (Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Toast.makeText(this, displayName, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if(uriString.startsWith("file://")){
                        displayName = myFile.getName();
                        //display file name on the TextView
                        addNewFileTV.setText(displayName);
                        Toast.makeText(this, displayName, Toast.LENGTH_SHORT).show();
                    }
                }break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
