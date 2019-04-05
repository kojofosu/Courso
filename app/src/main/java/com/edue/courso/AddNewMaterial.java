package com.edue.courso;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class AddNewMaterial extends AppCompatActivity {

    TextInputLayout addCourseCode_TIL, addCourseTitle_TIL;
    TextInputEditText addCourseCode_TIE, addCourseTitle_TIE;
    CollapsingToolbarLayout addNewCollapsingToolbarLayout;
    AppBarLayout addNewAppBarLayout;
    TextView addNewFileTV;
    Toolbar addNewtoolbar;
    String courseCodeText ;
    String courseCodeHint;

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

        courseCodeText = addCourseCode_TIE.getText().toString();
        courseCodeHint = addCourseCode_TIE.getHint().toString();


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
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, GET_FILE);
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
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")){
                        Cursor cursor = null;
                        try {
                            cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()){
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Toast.makeText(this, displayName, Toast.LENGTH_SHORT).show();
                            }
                        }finally {
                                cursor.close();
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
