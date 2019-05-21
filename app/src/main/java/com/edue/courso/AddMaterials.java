package com.edue.courso;

import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.edue.courso.FirebaseDatabaseUI.FilesAdapter;
import com.edue.courso.FirebaseDatabaseUI.FilesHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddMaterials extends AppCompatActivity {
    
    RecyclerView addMaterialsRecyclerView;
    CollapsingToolbarLayout addMaterialCollapsingToolbarLayout;
    AppBarLayout addMaterialAppBarLayout;
    Toolbar addMaterialToolbar;
    BottomAppBar bottomAppBar;
    TextView fileName;
    TextView showCode, showName, showProgramme, showLevel, showDept;
    ImageButton deleteFile;
    FloatingActionButton floatingActionButton;
    //private RecyclerView FirebaseRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FilesAdapter filesAdapter;
    SharedPreferences sharedPreferences;
    //Database References
    DatabaseReference mDatabaseReference;
    DatabaseReference uploadsDatabaseReference, filesDatabaseReference;
    String getUDBKey;
    private static final String TAG = "AddMaterial Activity";
    String dept,level,programme,uploadkey,title,code;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_materials);
        
        //SharedPrefs
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        getUDBKey = sharedPreferences.getString("userID", "");
        
        //initialize
        init();


        code = getIntent().getStringExtra("Code");
        level = getIntent().getStringExtra("Level");
        //programme = getIntent().getStringExtra("Programme");
        title = getIntent().getStringExtra("Title");
        dept = getIntent().getStringExtra("Dept");
        uploadkey = getIntent().getStringExtra("UploadKey");

        Log.d(TAG, code);
        Log.d(TAG, level);
        //Log.d(TAG, programme);
        Log.d(TAG, title);
        Log.d(TAG, dept);
        Log.d(TAG, uploadkey);

        showName.setText(title);
        showLevel.setText(String.format("Level %s", level));
        showDept.setText(dept);
        showCode.setText(code);

        linearLayoutManager = new LinearLayoutManager(this);
        addMaterialsRecyclerView = (RecyclerView) findViewById(R.id.add_material_recyclerView);
        addMaterialsRecyclerView.setHasFixedSize(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
        filesDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads/" + uploadkey + "/files");
        addMaterialsRecyclerView.setLayoutManager(linearLayoutManager);
        filesAdapter = new FilesAdapter(FilesS.class, R.layout.add_materials_items, FilesHolder.class, filesDatabaseReference, this);
        addMaterialsRecyclerView.setAdapter(filesAdapter);


        Log.d(TAG, "fireUi : " + filesAdapter);


        //firebase database
        firebaseDatabase();

        //collapsible toolbar
        collapsibleToolbar();

        //Toolbar
        topToolbar();
    }


    private void init() {
//        addMaterialsRecyclerView = findViewById(R.id.add_material_recyclerView);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        floatingActionButton = findViewById(R.id.add_new_material_fab);
        fileName = findViewById(R.id.add_material_item_name);
        deleteFile = findViewById(R.id.add_material_item_delete_thumbnail);
        showCode = findViewById(R.id.show_code);
        showDept = findViewById(R.id.show_dept);
        showLevel = findViewById(R.id.show_level);
        showName = findViewById(R.id.show_name);
        showProgramme = findViewById(R.id.show_programme);
        addMaterialCollapsingToolbarLayout = findViewById(R.id.add_material_CollapsingToolbarLayout);
        addMaterialAppBarLayout = findViewById(R.id.add_material_AppBar);
        addMaterialToolbar = findViewById(R.id.add_material_toolbar);
    }

    private void firebaseDatabase() {
        getUDBKey = sharedPreferences.getString("userID", "");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
        filesDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads/" + uploadkey + "/files");


        List<Upload> uploadList = new ArrayList<>();
        // Read from the database
        filesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                listKeys = dataSnapshot.getKey();
//                listItems = dataSnapshot.child("courseCodes").getValue();

                filesAdapter = new FilesAdapter(FilesS.class, R.layout.add_materials_items, FilesHolder.class, filesDatabaseReference, getApplicationContext());
                addMaterialsRecyclerView.setAdapter(filesAdapter);

                Log.d(TAG, "recycler VAL : " + addMaterialsRecyclerView);

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
        addMaterialToolbar.getNavigationIcon();
        addMaterialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void collapsibleToolbar() {
        //appBar offset to determine when collapsible toolbar is collapsed or expanded
        addMaterialAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
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
                        addMaterialCollapsingToolbarLayout.isTitleEnabled();
                        addMaterialCollapsingToolbarLayout.setTitle(code);
                    }else{
                        addMaterialCollapsingToolbarLayout.setTitle(code);
                    }
                    isShow = true;
                }else if(isShow){
                    addMaterialCollapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });


    }

}
