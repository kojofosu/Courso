package com.edue.courso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfRenderer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.edue.courso.FirebaseDatabaseUI.CourseCodeHolder;
import com.edue.courso.FirebaseDatabaseUI.CoursecodeAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity {
    FloatingActionButton fab;
    ListView listView;
    ImageButton deleteClass;
    TextView itemTitle, testTV;
    String getUDBKey;
    String uploadChildKey;
    //Database References
    DatabaseReference mDatabaseReference;
    DatabaseReference uploadsDatabaseReference;
    //SharePreferences
    SharedPreferences sharedPreferences;
    //Toolbar
    private Toolbar toolbar;
    //Adapter ArrayLists
    List<Upload> listItems;
    List<Upload> listKeys;
    RecyclerView recyclerView;
    CourseCodeAdapter adapter;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    //private RecyclerView recipeRecyclerview;
    private LinearLayoutManager linearLayoutManager;
    private CoursecodeAdapter mRecipeAdapter;


    private static final String TAG = "Home Activity";

    FirebaseDatabase firebaseDatabase;

    //fireBase download reference
    StorageReference islandRef = FirebaseStorage.getInstance().getReference();

    // FireBase database
    //FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SharedPreferences
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);

        //init
        init();

        if (firebaseDatabase == null) {
            firebaseDatabase=FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }

       // getActivity().setTitle(getString(R.string.recipe_categories));
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.home_recyclerView);
        recyclerView.setHasFixedSize(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
        //childRef = mDatabaseRef.child("recipes");
        recyclerView.setLayoutManager(linearLayoutManager);
        mRecipeAdapter = new CoursecodeAdapter(Upload.class, R.layout.item, CourseCodeHolder.class, uploadsDatabaseReference, this);
        recyclerView.setAdapter(mRecipeAdapter);


        Log.d(TAG, "fireUi : " + mRecipeAdapter);



        listItems = new ArrayList<>();
        listKeys = new ArrayList<>();
//        //recyclerView = findViewById(R.id.home_recyclerView);
//        recyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new CourseCodeAdapter(this, listItems);
//        recyclerView.setAdapter(adapter);

//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_single_choice,
//                listItems);
//        listView.setAdapter(adapter);
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listView.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent,
//                                            View view, int position, long id) {
//                        selectedPosition = position;
//                        itemSelected = true;
//                        //deleteButton.setEnabled(true);
//                    }
//                });


        //fireBase download from storage
        //firebaseDownloadFromStorage();

        //fireBase database
        firebaseDatabase();

        //Toolbar
        topToolbar();

        //floating actionbar
        fABar();

    }

    private void fABar() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewMaterial.class);
                startActivity(intent);
            }
        });

    }

    private void topToolbar() {
        setSupportActionBar(toolbar);

    }

    private void firebaseDatabase() {
        getUDBKey = sharedPreferences.getString("userDatabaseKey", "");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");

        List<Upload> uploadList = new ArrayList<>();
        // Read from the database
        uploadsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                listKeys = dataSnapshot.getKey();
//                listItems = dataSnapshot.child("courseCodes").getValue();

                mRecipeAdapter = new CoursecodeAdapter(Upload.class, R.layout.item, CourseCodeHolder.class, uploadsDatabaseReference, getApplicationContext());
                recyclerView.setAdapter(mRecipeAdapter);

                 Log.d(TAG, "recycler VAL : " + recyclerView);

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

    private void init() {
        fab = findViewById(R.id.fab);
        itemTitle = findViewById(R.id.item_title);
        //listView = findViewById(R.id.main_listview);
        toolbar = findViewById(R.id.toolbar);
        deleteClass = findViewById(R.id.delete_thumbnail);
    }

    private void firebaseDownloadFromStorage() {
        islandRef.child("CourseMaterials/Dept. Computer Science and Information Technology/Non-Degree/100/CSC 303/Internship%20Evaluation%20Form.pdf");


        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void signOut(){
        //sign user out
        FirebaseAuth.getInstance().signOut();
        //clear sharePrefs
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates menu and adds to the toolbar or actionbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handles clicks on the toolbar
        int id = item.getItemId();

        if (id == R.id.logout){
            //sign user out from firebase
            signOut();
            //Go to sign in page
            startActivity(new Intent(Home.this, SignIn.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
