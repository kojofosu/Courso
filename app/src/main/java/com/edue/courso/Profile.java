package com.edue.courso;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    EditText addNewLecturerNameET, addNewLecturerEmailET, addNewLecturerContactET;
    DatabaseReference mDatabaseReference , uploadsDatabaseReference;
    String getUDBKey;
    String key;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //SharedPrefs
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        //initialize
        init();

        //fireBase database
        firebaseDB();

        //Toobar
        topToolbar();
    }
    private void topToolbar() {
        setSupportActionBar(toolbar);
        toolbar.getNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void firebaseDB() {
        //Initializing the databaseReference
        getUDBKey = sharedPreferences.getString("userDatabaseKey", "");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        //fetching key from mDatabaseReference to use as child for the rest
        key = mDatabaseReference.push().getKey();
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //below code takes data from DB
                User user = dataSnapshot.child(getUDBKey).getValue(User.class);
                String userName, userEmail, userPhone;
                if (user != null) {
                    //get the values
                    userName = user.getFullName();
                    userEmail = user.getEmail();
                    userPhone = user.getPhone();
                    //set values to the various editTexts
                    addNewLecturerNameET.setText(userName);
                    addNewLecturerEmailET.setText(userEmail);
                    addNewLecturerContactET.setText(userPhone);
                    //toolbar title
                    toolbar.setTitle(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AddNewActivity : ", "Failed to read value.", databaseError.toException());
            }
        });

    }

    private void init() {
        addNewLecturerNameET = findViewById(R.id.addNew_lecturer_name_ET);
        addNewLecturerEmailET = findViewById(R.id.addNew_lecturer_email_ET);
        addNewLecturerContactET = findViewById(R.id.addNew_lecturer_contact_ET);
        toolbar = findViewById(R.id.profile_toolbar);
    }
}
