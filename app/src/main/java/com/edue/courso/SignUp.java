package com.edue.courso;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {


    TextInputLayout emailTextInputLayout, passwordTextInputLayout, nameTextInputLayout, phoneTextInputLayout, passwordConfirmTextInputLayout;
    TextInputEditText emailTextInputEditText, passwordTextInputEditText, nameTextInputEditText, phoneTextInputEditText, passwordConfirmTextInputEditText;
    TextView goToSignInTextView;
    Button signUpBtn;
    private FirebaseAuth mAuth;
    private static final String TAG = "Sign Up Activity";

    SharedPreferences sharedPreferences;
    //Declaring a DatabaseReference
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initializing SharePreferences
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);

        // initializing the FireBaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //Getting dataBase instance reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        //Initialize
        init();

        //onClicks
        onClickEvents();

        //editTextChange Events
        textChangeEvents();

    }

    private void init(){
        emailTextInputLayout = findViewById(R.id.sign_up_email_text_input_layout);
        passwordTextInputLayout = findViewById(R.id.sign_up_password_text_input_layout);
        nameTextInputLayout = findViewById(R.id.sign_up_name_text_input_layout);
        phoneTextInputLayout = findViewById(R.id.sign_up_phone_text_input_layout);
        passwordConfirmTextInputLayout = findViewById(R.id.sign_up_password_confirm_text_input_layout);
        emailTextInputEditText = findViewById(R.id.sign_up_email_text_input_edittext);
        passwordTextInputEditText = findViewById(R.id.sign_up_password_text_input_edittext);
        nameTextInputEditText = findViewById(R.id.sign_up_name_text_input_edittext);
        phoneTextInputEditText = findViewById(R.id.sign_up_phone_text_input_edittext);
        passwordConfirmTextInputEditText = findViewById(R.id.sign_up_password_confirm_text_input_edittext);
        goToSignInTextView = findViewById(R.id.gotoSignInTV);
        signUpBtn = findViewById(R.id.buttonSignup);
    }

    private void onClickEvents(){
        // Go to sign in page
        goToSignInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, SignIn.class));
                finish();
            }
        });

        //Register button
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String name = String.valueOf(nameTextInputEditText.getText());
                final String email = String.valueOf(emailTextInputEditText.getText());
                final String phone = String.valueOf(phoneTextInputEditText.getText());
                String password = String.valueOf(passwordTextInputEditText.getText());
                String passwordConfirm = String.valueOf(passwordConfirmTextInputEditText.getText());
                hideKeyboard();
                if (name.length() > 0 && isEmailValid(email) && phone.length() == 10 && password.length() > 0 && passwordConfirm.length() > 0 && password.equals(passwordConfirm)) {
                    //Display progress bar
                    final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                    progressDialog.setMessage("Signing up ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    //Create user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        //dismiss progress bar
                                        progressDialog.dismiss();

                                        //just to get user uuid
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            String userID = firebaseUser.getUid();

                                            //Put User details in sharedPref
                                            sharedPreferences.edit().putString("userName", name).apply();
                                            sharedPreferences.edit().putString("userEmail",email).apply();
                                            sharedPreferences.edit().putString("userPhone", phone).apply();
                                            sharedPreferences.edit().putString("userID", userID).apply();

                                            //Add data to User object to be added the database
                                            User user = new User();
                                            user.setFullName(name);
                                            user.setEmail(email);
                                            user.setPhone(phone);
                                            user.setUUID(userID);

                                            //getting database key for user
                                            //String key = mDatabaseReference.push().getKey();
                                            sharedPreferences.edit().putString("userDatabaseKey", userID).apply();
                                            if (userID != null) {
                                                mDatabaseReference.child(userID).setValue(user);
                                            }else{
                                                Toast.makeText(SignUp.this, "Database push key is empty", Toast.LENGTH_SHORT).show();
                                            }

                                            Intent intent = new Intent(SignUp.this, Home.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        //dismiss progress bar
                                        progressDialog.dismiss();

                                        Snackbar.make(view,"Error: Failed", Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });
                }else if (name.isEmpty()){
                    nameTextInputLayout.setError("Name cannot be empty");
                }else if (email.isEmpty()){
                    emailTextInputLayout.setError("Email cannot be empty");
                }else if (phone.isEmpty()){
                    phoneTextInputLayout.setError("Phone number cannot be empty");
                }else if (password.isEmpty()){
                    passwordTextInputLayout.setError("Password cannot be empty");
                }else if (!password.equals(passwordConfirm)){
                    passwordConfirmTextInputLayout.setError("Password do not match");
                }
            }
        });
    }

    private void textChangeEvents(){
        nameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = String.valueOf(nameTextInputEditText.getText());
                if (name.length() <= 0){
                    nameTextInputLayout.setError("Name cannot be empty");
                }else {
                    nameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emailTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = String.valueOf(emailTextInputEditText.getText());
                if (!isEmailValid(email)){
                    emailTextInputLayout.setError("Invalid Email");
                }else if (isEmailValid(email)){
                    emailTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phoneTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = String.valueOf(phoneTextInputEditText.getText());
                if (phone.length() == 10){
                    phoneTextInputLayout.setErrorEnabled(false);
                }else{
                    phoneTextInputLayout.setError("Must be 10 digits");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = String.valueOf(passwordTextInputEditText.getText());
                if (password.length() <= 0){
                    passwordTextInputLayout.setError("Password cannot be empty");
                }else{
                    passwordTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordConfirmTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwordConfirm = String.valueOf(passwordConfirmTextInputEditText.getText());
                if (passwordConfirm.length() <= 0){
                    passwordConfirmTextInputLayout.setError("Password cannot be empty");
                }else{
                    passwordConfirmTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
