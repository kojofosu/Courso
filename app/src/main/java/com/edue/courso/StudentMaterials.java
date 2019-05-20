package com.edue.courso;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.edue.courso.FirebaseDatabaseUI.StudentFilesAdapter;
import com.edue.courso.FirebaseDatabaseUI.StudentFilesHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentMaterials extends AppCompatActivity{

    BottomAppBar studentBottomAppBar;
    FloatingActionButton studentFloatingActionButton;
    TextView studentFileName;
    TextView studentShowCode, studentShowDept, studentShowLevel, studentShowName, studentShowProgramme;
    CollapsingToolbarLayout studentMaterialCollapsingToolbarLayout;
    AppBarLayout studentMaterialAppbarLayout;
    Toolbar studentMaterialToolbar;
    StudentFilesAdapter studentFilesAdapter;

    private static final String TAG = "StudentsMaterial";
    private LinearLayoutManager linearLayoutManager;
    RecyclerView studentMaterialsRecyclerView;
    String code;
    DatabaseReference studentsFilesDatabaseReference, studentMaterialDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_materials);

        //init
        init();


//        Bundle bundle = new Bundle();
//        bundle.getString("StudentsCode");
        code = getIntent().getStringExtra("StudentsCode");
        Log.d(TAG, code);

        studentShowCode.setText(code);

        linearLayoutManager = new LinearLayoutManager(this);
        studentMaterialsRecyclerView = (RecyclerView) findViewById(R.id.student_material_recyclerView);
        studentMaterialsRecyclerView.setHasFixedSize(true);
        studentMaterialDatabaseReference = FirebaseDatabase.getInstance().getReference("students/" + code);
        studentsFilesDatabaseReference = FirebaseDatabase.getInstance().getReference("students/"+ code + "/files");
        studentMaterialsRecyclerView.setLayoutManager(linearLayoutManager);
        studentFilesAdapter = new StudentFilesAdapter(FilesS.class, R.layout.student_materials_items, StudentFilesHolder.class, studentsFilesDatabaseReference, this);
        studentMaterialsRecyclerView.setAdapter(studentFilesAdapter);


        Log.d(TAG, "fireUi : " + studentFilesAdapter);


        //firebase database
        firebaseDatabase();

        //collapsible toolbar
        collapsibleToolbar();

        //Toolbar
        topToolbar();
    }

    private void init() {
        studentBottomAppBar = findViewById(R.id.student_bottom_app_bar);
        studentFloatingActionButton = findViewById(R.id.student_material_fab);
        studentFileName = findViewById(R.id.student_material_item_name);
        studentShowCode = findViewById(R.id.student_show_code);
        studentShowDept = findViewById(R.id.student_show_dept);
        studentShowLevel = findViewById(R.id.student_show_level);
        studentShowName = findViewById(R.id.student_show_name);
        studentShowProgramme = findViewById(R.id.student_show_programme);
        studentMaterialCollapsingToolbarLayout = findViewById(R.id.student_material_CollapsingToolbarLayout);
        studentMaterialAppbarLayout = findViewById(R.id.student_material_AppBar);
        studentMaterialToolbar = findViewById(R.id.student_material_toolbar);
    }

    private void firebaseDatabase() {
        studentMaterialDatabaseReference = FirebaseDatabase.getInstance().getReference("students/" + code);
        studentsFilesDatabaseReference = FirebaseDatabase.getInstance().getReference("students/"+ code + "/files");

        studentMaterialDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        List<Upload> uploadList = new ArrayList<>();
        // Read from the database
        studentsFilesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                studentFilesAdapter = new StudentFilesAdapter(FilesS.class, R.layout.student_materials_items, StudentFilesHolder.class, studentsFilesDatabaseReference, getApplicationContext());
                studentMaterialsRecyclerView.setAdapter(studentFilesAdapter);

                Log.d(TAG, "recycler VAL : " + studentMaterialsRecyclerView);

//                listKeys.add(dataSnapshot.getKey());
//                adapter.add((String) dataSnapshot.child("courseCodes").getValue());
//
//
//                uploadChildKey = dataSnapshot.getKey();
//                Log.d(TAG, "VALUE IS added: " + uploadChildKey);
//                uploadsDatabaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        // This method is called once with the initial value and again
//                        // whenever data at this location is updated.
//                        for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
//                            Upload upload = dataSnapshot.child(uploadChildKey).getValue(Upload.class);
//
//                            Log.d(TAG, "Value is: " + uploadChildKey);
//
//                            if (upload != null) {
//                                String CC = upload.getCourseName();
//                                testTV.setText(CC);
//                            }else testTV.setText("null");
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Failed to read value
//                        Log.w(TAG, "Failed to read value.", error.toException());
//                    }
//                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void topToolbar() {
        studentMaterialToolbar.getNavigationIcon();
        studentMaterialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void collapsibleToolbar() {
        //appBar offset to determine when collapsible toolbar is collapsed or expanded
        studentMaterialAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow =  true;
            int scrollRange = -1;

            @Override
            //i here is the vertical offset
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0){
                    if(!code.isEmpty()){
                        studentMaterialCollapsingToolbarLayout.isTitleEnabled();
                        studentMaterialCollapsingToolbarLayout.setTitle(code);
                    }else{
                        studentMaterialCollapsingToolbarLayout.setTitle(code);
                    }
                    isShow = true;
                }else if(isShow){
                    studentMaterialCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });


    }
}
