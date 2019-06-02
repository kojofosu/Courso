package com.edue.courso;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edue.courso.FirebaseDatabaseUI.StudentFilesAdapter;
import com.edue.courso.FirebaseDatabaseUI.StudentFilesHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentMaterials extends AppCompatActivity{

    TextView studentMaterialNoItemTV;
    ImageView studentMaterialNoItemIV;
    ProgressBar studentMaterialProgressBar;

    TextView studentFileName;
    TextView studentShowCode, studentShowDept, studentShowLevel, studentShowName, studentShowProgramme;
    CollapsingToolbarLayout studentMaterialCollapsingToolbarLayout;
    AppBarLayout studentMaterialAppbarLayout;
    Toolbar studentMaterialToolbar;
    StudentFilesAdapter studentFilesAdapter;
    String studentSideCode, studentSideTitle, studentSideProgramme, studentSideLevel, studentSideDept;

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

//        // create a new renderer
//        PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(documentFile, ParcelFileDescriptor.MODE_READ_ONLY));
//
//        //Open the first page of the document
//        PdfRenderer.Page page = pdfRenderer.openPage(0);
//
//        //Define a target width for the resulting bitmap and use it to calculate the final height
//        int width = 2048;
//        int height = (page.getHeight() * (width / page.getHeight()));
//
//        //Create the bitmap to render the page to
//        Bitmap pageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        //render the page to the bitmap
//        page.render(pageBitmap , null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);


        code = getIntent().getStringExtra("StudentsCode");
        Log.d(TAG, code);

        studentShowCode.setText(code);
        linearLayoutManager = new LinearLayoutManager(this);
        studentMaterialsRecyclerView = findViewById(R.id.student_material_recyclerView);

        studentMaterialDatabaseReference = FirebaseDatabase.getInstance().getReference("students/" + code);
        studentsFilesDatabaseReference = FirebaseDatabase.getInstance().getReference("students/"+ code + "/files");



        Log.d(TAG, "fireUi : " + studentFilesAdapter);


        //firebase database
        firebaseDatabase();

        //collapsible toolbar
        collapsibleToolbar();

        //Toolbar
        topToolbar();
    }

    private void init() {
        studentFileName = findViewById(R.id.student_material_item_name);
        studentShowCode = findViewById(R.id.student_show_code);
        studentShowDept = findViewById(R.id.student_show_dept);
        studentShowLevel = findViewById(R.id.student_show_level);
        studentShowName = findViewById(R.id.student_show_name);
        studentShowProgramme = findViewById(R.id.student_show_programme);
        studentMaterialCollapsingToolbarLayout = findViewById(R.id.student_material_CollapsingToolbarLayout);
        studentMaterialAppbarLayout = findViewById(R.id.student_material_AppBar);
        studentMaterialToolbar = findViewById(R.id.student_material_toolbar);
        studentMaterialNoItemIV = findViewById(R.id.student_material_no_itemIV);
        studentMaterialNoItemTV = findViewById(R.id.student_material_no_itemTV);
        studentMaterialProgressBar = findViewById(R.id.student_material_progressbar);
    }

    private void firebaseDatabase() {
        studentMaterialDatabaseReference = FirebaseDatabase.getInstance().getReference("students/" + code);
        studentsFilesDatabaseReference = FirebaseDatabase.getInstance().getReference("students/"+ code + "/files");

        studentMaterialProgressBar.setVisibility(View.VISIBLE);

        studentMaterialDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    if (upload != null) {
                        studentSideTitle = upload.getCourseName();
                        studentSideCode = upload.getCourseCodes();
                        studentSideDept = upload.getDeptName();
                        studentSideLevel = upload.getLevelNum();
                        studentSideProgramme = upload.getProgramme();

                        studentShowCode.setText(studentSideCode);
                        studentShowDept.setText(studentSideDept);
                        studentShowLevel.setText(String.format("Level %s",studentSideLevel));
                        studentShowName.setText(studentSideTitle);
                        studentShowProgramme.setText(studentSideProgramme);
                    }
                }

                if (dataSnapshot.hasChild("files")){
                    // Read from the database
                    studentsFilesDatabaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            studentMaterialProgressBar.setVisibility(View.GONE);
                            studentMaterialNoItemIV.setVisibility(View.GONE);
                            studentMaterialNoItemTV.setVisibility(View.GONE);

                            studentMaterialsRecyclerView.setLayoutManager(linearLayoutManager);
                            studentMaterialsRecyclerView.setHasFixedSize(true);
                            studentFilesAdapter = new StudentFilesAdapter(FilesS.class, R.layout.student_materials_items, StudentFilesHolder.class, studentsFilesDatabaseReference, StudentMaterials.this);
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
                            studentMaterialProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            studentMaterialProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            studentMaterialProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            studentMaterialProgressBar.setVisibility(View.GONE);
                        }
                    });

                }else if (!dataSnapshot.hasChild("files")){
                    studentMaterialProgressBar.setVisibility(View.GONE);
                    studentMaterialNoItemIV.setVisibility(View.VISIBLE);
                    studentMaterialNoItemTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                studentMaterialProgressBar.setVisibility(View.GONE);
            }
        });


        List<Upload> uploadList = new ArrayList<>();

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
                    if(!studentSideCode.toUpperCase().isEmpty()){
                        studentMaterialCollapsingToolbarLayout.isTitleEnabled();
                        studentMaterialCollapsingToolbarLayout.setTitle(studentSideCode);
                    }else{
                        studentMaterialCollapsingToolbarLayout.setTitle(studentSideCode);
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
