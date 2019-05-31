package com.edue.courso;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    View navHeader;
    String fullName, email, phone;
    TextView navName, navPhone, navEmail;

    FloatingActionButton fab;
    ProgressBar homeProgressBar;
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

    //private RecyclerView FirebaseRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CoursecodeAdapter coursecodeAdapter;


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
//
//        if (firebaseDatabase == null) {
//            firebaseDatabase=FirebaseDatabase.getInstance();
//            firebaseDatabase.setPersistenceEnabled(true);
//        }

       // getActivity().setTitle(getString(R.string.recipe_categories));
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.home_recyclerView);
        recyclerView.setHasFixedSize(true);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");
        //childRef = mDatabaseRef.child("recipes");
        recyclerView.setLayoutManager(linearLayoutManager);
        coursecodeAdapter = new CoursecodeAdapter(Upload.class, R.layout.item, CourseCodeHolder.class, uploadsDatabaseReference, this);
        recyclerView.setAdapter(coursecodeAdapter);


        Log.d(TAG, "fireUi : " + coursecodeAdapter);



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
//        toolbar.setNavigationIcon(R.drawable.ic_dehaze_black_24dp);
//        //Code below opens drawer
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar , R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        actionBarDrawerToggle = navigationView.getActionBarDrawerToggle();
        //Code below opens drawer
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



        //setting details to string variables
        fullName = sharedPreferences.getString("lecturerName", "");
        email = sharedPreferences.getString("lecturerEmail", "");
        phone = sharedPreferences.getString("lecturerPhone", "");

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()){
            //fireBase database
            firebaseDB();
        }else {
            //set values to the various editTexts
            navName.setText(fullName);
            navEmail.setText(email);
            navPhone.setText(phone);
            //toolbar title
//            toolbar.setTitle(fullName);
        }

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void firebaseDB() {
        //Initializing the databaseReference
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //below code takes data from DB
                User user = dataSnapshot.child(getUDBKey).getValue(User.class);

                if (user != null) {
                    //saving in string variables
                    email = user.getEmail();
                    fullName = user.getFullName();
                    phone = user.getPhone();
                    //get the values and storing it in sharedPrefs
                    sharedPreferences.edit().putString("lecturerName", fullName).apply();
                    sharedPreferences.edit().putString("lecturerEmail",email).apply();
                    sharedPreferences.edit().putString("lecturerPhone", phone).apply();

                    //set values to the various editTexts
                    navName.setText(fullName);
                    navEmail.setText(email);
                    navPhone.setText(phone);
                    //toolbar title
//                    toolbar.setTitle(userName);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AddNewActivity : ", "Failed to read value.", databaseError.toException());
            }
        });

    }


    private void firebaseDatabase() {

        //startProgressBar
        homeProgressBar.setVisibility(View.VISIBLE);
        getUDBKey = sharedPreferences.getString("userID", "");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");

        List<Upload> uploadList = new ArrayList<>();
        // Read from the database
        uploadsDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //stopProgressBar
                homeProgressBar.setVisibility(View.GONE);
//                listKeys = dataSnapshot.getKey();
//                listItems = dataSnapshot.child("courseCodes").getValue();

                coursecodeAdapter = new CoursecodeAdapter(Upload.class, R.layout.item, CourseCodeHolder.class, uploadsDatabaseReference, getApplicationContext());
                recyclerView.setAdapter(coursecodeAdapter);

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
        homeProgressBar = findViewById(R.id.home_progressbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        navEmail = navHeader.findViewById(R.id.email);
        navName = navHeader.findViewById(R.id.name);
        navPhone = navHeader.findViewById(R.id.phone);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Handles clicks on the toolbar
        int id = menuItem.getItemId();

        if (id == R.id.create_class){
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent createClassIntent = new Intent(Home.this, AddNewMaterial.class);
            startActivity(createClassIntent);
        }else if (id == R.id.profile){
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(Home.this, Profile.class));
        }else if (id == R.id.about){
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(Home.this, About.class));
        }
        if (id == R.id.logout){
            drawerLayout.closeDrawer(GravityCompat.START);
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Logout?\n")
                    .setMessage("Are you sure you want to logout of your account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with logout
                            try {

                                //sign user out from fireBase
                                signOut();
                                //Go to sign in page
                                startActivity(new Intent(Home.this, SignIn.class));
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_bubble_chart_black_24dp)
                    .show();

        }if (id == R.id.profile){
            startActivity(new Intent(Home.this, Profile.class));
        }
        return true;
    }
}
