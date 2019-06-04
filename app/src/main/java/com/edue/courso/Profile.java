package com.edue.courso;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    String pass;
    String emailToUpdate;

    String ffEmail;
    String ffName;
    String ffPhone;

    String checkEmail;
    String checkName;
    String checkPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initialize
        init();

        //SharedPrefs
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);
        getUDBKey = sharedPreferences.getString("userID", "");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");


        pass = sharedPreferences.getString("userPass", "");

        //setting details to string variables
        fullName = sharedPreferences.getString("lecturerName", "");
        email = sharedPreferences.getString("lecturerEmail", "");
        phone = sharedPreferences.getString("lecturerPhone", "");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()){
            //fireBase database
            firebaseDB();
        }else {
            //set values to the various editTexts
            addNewLecturerNameET.setText(fullName);
            addNewLecturerEmailET.setText(email);
            addNewLecturerContactET.setText(phone);
            //toolbar title
            toolbar.setTitle(fullName);
        }

        addNewLecturerEmailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ffEmail = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ffEmail = charSequence.toString();
                Toast.makeText(Profile.this, ffEmail, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addNewLecturerNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ffName = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ffName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addNewLecturerContactET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ffPhone = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ffPhone = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //disable btn when all fields haven't changed
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User user = dataSnapshot1.getValue(User.class);
                    if (user != null) {
                        checkEmail = user.getEmail();
                        checkName = user.getFullName();
                        checkPhone = user.getPhone();

                        Log.d("tag ", "checkEmail :" + checkEmail);
                        Log.d("tag ", "checkName :" + checkName);
                        Log.d("tag ", "checkPhone :" + checkPhone);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.d("tag ", "ffEmail :" + ffEmail);
        Log.d("tag ", "ffkName :" + ffName);
        Log.d("tag ", "ffPhone :" + ffPhone);

//        if (checkEmail.equals(ffEmail) && checkName.equals(ffName) && checkPhone.equals(ffPhone)){
//            updateProfileBtn.setEnabled(false);
//            Toast.makeText(Profile.this, "coke", Toast.LENGTH_SHORT).show();
////                            updateProfileBtn.setBackgroundColor(getResources().getColor(R.color.disabledBtnColor));
//            updateProfileBtn.setTextColor(getResources().getColor(R.color.disabledBtnColor));
//        }else if (!checkEmail.equals(ffEmail) || !checkName.equals(ffName) || !checkPhone.equals(ffPhone)){
//            updateProfileBtn.setEnabled(true);
//            Toast.makeText(Profile.this, "prrr", Toast.LENGTH_SHORT).show();
//        }


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



                if (fullName.length() >= 1 && isEmailValid(email) && phone.length() == 10){
                    final ProgressDialog progressDialog = new ProgressDialog(Profile.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Updating profile...");
                    progressDialog.show();
                    //changing user email in firebase auth side but before that user must be re-authenticated

                    // Get auth credentials from the user for re-authentication. The example below shows
                    // email and password credentials but there are multiple possible providers,
                    // such as GoogleAuthProvider or FacebookAuthProvider.
                   emailToUpdate = firebaseUser.getEmail();
                    AuthCredential credential = null;
                    if (emailToUpdate != null) {
                        credential = EmailAuthProvider
                                .getCredential(emailToUpdate, pass);
                    }

                    // Prompt the user to re-provide their sign-in credentials
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("UserReAuth", "User re-authenticated.");

                                        firebaseUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mDatabaseReference.child(getUDBKey).child("email").setValue(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sharedPreferences.edit().putString("lecturerEmail", email).apply();
                                                    }
                                                });
                                                mDatabaseReference.child(getUDBKey).child("fullName").setValue(fullName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sharedPreferences.edit().putString("lecturerName", fullName ).apply();
                                                    }
                                                });
                                                mDatabaseReference.child(getUDBKey).child("phone").setValue(phone)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressDialog.dismiss();
                                                                Snackbar.make(findViewById(R.id.Id_Profile), "Profile Updated Successfully", Snackbar.LENGTH_SHORT).show();
                                                                //setting details to string variables
                                                                 sharedPreferences.edit().putString("lecturerPhone", phone).apply();

                                                            }
                                                        });
                                                Log.d("Profile Activity", "User email address updated.");
                                            }

                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Snackbar.make(findViewById(R.id.Id_Profile), "Profile Update FAILED", Snackbar.LENGTH_SHORT).show();
                                                Log.d("UpdatePrf", "profile update : " + e.getMessage());
                                            }
                                        });
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(Profile.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                        Log.d("UserReAuth", "User was not re-authenticated.");
                                    }
                                }
                            });

                }else if (!isEmailValid(email)){
                    addNewLecturerEmailET.setError("Invalid Email");
                }else if (fullName.length() < 1){
                    addNewLecturerNameET.setError("Name cannot be empty");
                }else if (phone.length() > 10 || phone.length() <10){
                    addNewLecturerContactET.setError("Phone number should be 10 digits");
                }
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
        //getUDBKey = sharedPreferences.getString("userID", "");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_account){
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Delete Account?\n")
                    .setMessage("Are you sure you want to permanently delete your account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with logout
                            try {
                                final ProgressDialog progressDialog = new ProgressDialog(Profile.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Deleting Account...");
                                progressDialog.show();

                                //Delete user details from the database
                                mDatabaseReference.child(getUDBKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Get auth credentials from the user for re-authentication. The example below shows
                                        // email and password credentials but there are multiple possible providers,
                                        // such as GoogleAuthProvider or FacebookAuthProvider.
                                        emailToUpdate = firebaseUser.getEmail();
                                        AuthCredential credential = null;
                                        if (emailToUpdate != null) {
                                            credential = EmailAuthProvider
                                                    .getCredential(emailToUpdate, pass);
                                        }
                                        // Prompt the user to re-provide their sign-in credentials
                                        firebaseUser.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("UserReAuth", "User re-authenticated.");
                                                            firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    progressDialog.dismiss();
                                                                    //Go to sign in page
                                                                    startActivity(new Intent(Profile.this, SignIn.class));
                                                                    finish();
                                                                    //clear sharePrefs
                                                                    sharedPreferences.edit().clear().apply();

                                                                    Log.d("Profile Activity", "User account deleted.");
                                                                }

                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Snackbar.make(findViewById(R.id.Id_Profile), "Account deletion FAILED", Snackbar.LENGTH_SHORT).show();
                                                                    Log.d("UpdatePrf", "account deletion failed : " + e.getMessage());
                                                                }
                                                            });
                                                        }else{
                                                            Toast.makeText(Profile.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                                            Log.d("UserReAuth", "User was not re-authenticated.");
                                                        }
                                                    }
                                                });

                                    }
                                });


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
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .show();

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmailValid(String email) {
        //return email.contains("@");
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
