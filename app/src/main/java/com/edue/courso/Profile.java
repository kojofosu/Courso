package com.edue.courso;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    EditText addNewLecturerNameET, addNewLecturerEmailET, addNewLecturerContactET;
    DatabaseReference mDatabaseReference , uploadsDatabaseReference;
    String getUDBKey;
    String key;
    Toolbar toolbar;
    Button updateProfileBtn;
    SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser;
    String userName, userEmail, userPhone;
    String fullName, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize
        init();

        //SharedPrefs
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        getUDBKey = sharedPreferences.getString("userID", "");

        //setting details to string variables
        fullName = sharedPreferences.getString("lecturerName", "");
        email = sharedPreferences.getString("lecturerEmail", "");
        phone = sharedPreferences.getString("lecturerPhone", "");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fullName == null || email == null || phone == null){
            //fireBase database
            firebaseDB();
        }else {
            //set values to the various editTexts
            addNewLecturerNameET.setText(fullName);
            addNewLecturerEmailET.setText(email);
            addNewLecturerContactET.setText(phone);
            //toolbar title
            toolbar.setTitle(userName);
        }



        //Toolbar
        topToolbar();

        //event listeners
        eventListeners();
    }

    private void eventListeners() {
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = addNewLecturerEmailET.getText().toString();
                fullName = addNewLecturerNameET.getText().toString();
                phone = addNewLecturerContactET.getText().toString();
                //update user email in firebase auth side
                Toast.makeText(Profile.this, firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                firebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mDatabaseReference.child(getUDBKey).child("email").setValue(email);
                            mDatabaseReference.child(getUDBKey).child("fullName").setValue(fullName);
                            mDatabaseReference.child(getUDBKey).child("phone").setValue(phone)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Log.d("Profile Activity", "User email address updated.");
                            Toast.makeText(getApplicationContext(), "The email updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
        getUDBKey = sharedPreferences.getString("userID", "");
        //fetching key from mDatabaseReference to use as child for the rest
        key = mDatabaseReference.push().getKey();
        uploadsDatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ getUDBKey + "/uploads");


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //below code takes data from DB
                User user = dataSnapshot.child(getUDBKey).getValue(User.class);

                if (user != null) {
                    //saving in string variables
                    userEmail = user.getEmail();
                    userName = user.getFullName();
                    userPhone = user.getPhone();
                    //get the values and storing it in sharedPrefs
                    sharedPreferences.edit().putString("lecturerName", userName).apply();
                    sharedPreferences.edit().putString("lecturerEmail",userEmail).apply();
                    sharedPreferences.edit().putString("lecturerPhone", userPhone).apply();

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
        updateProfileBtn = findViewById(R.id.button_update_profile);
    }
}
