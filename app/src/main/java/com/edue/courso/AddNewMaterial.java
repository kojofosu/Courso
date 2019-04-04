package com.edue.courso;

import android.graphics.drawable.Drawable;
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
import android.widget.EditText;

public class AddNewMaterial extends AppCompatActivity {

    TextInputLayout addCourseCode_TIL, addCourseTitle_TIL;
    TextInputEditText addCourseCode_TIE, addCourseTitle_TIE;
    CollapsingToolbarLayout addNewCollapsingToolbarLayout;
    AppBarLayout addNewAppBarLayout;
    Toolbar addNewtoolbar;
    String courseCodeText ;
    String courseCodeHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_material);

        //initialize
        init();

        //collapsible toolbar
        collapsibleToolbar();

        //toolbar
        toolbar();



    }


    private void init() {
        addCourseCode_TIL = findViewById(R.id.add_course_code_TIL);
        addCourseTitle_TIL = findViewById(R.id.add_course_title_TIL);
        addCourseCode_TIE = findViewById(R.id.add_course_code_TIE);
        addCourseTitle_TIE = findViewById(R.id.add_course_title_TIE);
        addNewCollapsingToolbarLayout = findViewById(R.id.addNNew_CollapsingToolbarLayout);
        addNewtoolbar = findViewById(R.id.addNew_toolbar);
        addNewAppBarLayout = findViewById(R.id.addNew_AppBar);

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




    private void toolbar() {
//        if(courseCodeText != null){
//            addNewtoolbar.setTitle(courseCodeText);
//        }else{
//            addNewtoolbar.setTitle(courseCodeHint);
//        }
    }

}
