package com.edue.courso;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity {

    TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    TextInputEditText emailTextInputEditText, passwordTextInputEditText;
    TextView goToSignUpTextView, studentLogin;
    Button signInBtn;
    ProgressDialog progressDialog;

    private static final String TAG = "SignIn Activity";
    //FirebaseAuth
    private FirebaseAuth mAuth;
    //SharePreferences
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //SharedPreferences
        sharedPreferences = getSharedPreferences("login" , MODE_PRIVATE);

        //Initialize
        init();

        //onClicks
        onClickEvents();

        //editTextChange Events
        textChangeEvents();

        //initializing the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

    }

    private void init(){
        emailTextInputLayout = findViewById(R.id.sign_in_email_text_input_layout);
        passwordTextInputLayout = findViewById(R.id.sign_in_password_text_input_layout);
        emailTextInputEditText = findViewById(R.id.sign_in_email_text_input_edittext);
        passwordTextInputEditText = findViewById(R.id.sign_in_password_text_input_edittext);
        goToSignUpTextView = findViewById(R.id.gotoSignUpTV);
        signInBtn = findViewById(R.id.buttonSignin);
        studentLogin = findViewById(R.id.student_login);
    }

    private void onClickEvents(){
        //textView to go to sign up page
        goToSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, SignUp.class));
                finish();
            }
        });

        //login button
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String email = String.valueOf(emailTextInputEditText.getText());
                String password = String.valueOf(passwordTextInputEditText.getText());

                if (isEmailValid(email) && !password.isEmpty()){
                    passwordTextInputLayout.setErrorEnabled(false);
                    //show progress dialog
                    progressDialog = new ProgressDialog(SignIn.this);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(0);
                    progressDialog.show();

                    try {
                        signUserIn();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (email.isEmpty()){
                    emailTextInputLayout.setError("Email cannot be empty");
                }else if (password.isEmpty()){
                    passwordTextInputLayout.setError("Password cannot be empty");
                }
            }
        });

        studentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(SignIn.this);
                dialog.setContentView(R.layout.student_login_dialog);
                TextInputLayout studentLoginTextInputLayout = dialog.findViewById(R.id.student_login_text_input_layout);
                TextInputEditText studentLoginInputEditText = dialog.findViewById(R.id.student_login_text_input_edittext);
                dialog.show();
            }
        });
    }

    private void textChangeEvents(){
        emailTextInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String email = String.valueOf(emailTextInputEditText.getText());

                if (!isEmailValid(email) || TextUtils.isEmpty(email)){
                    emailTextInputLayout.setError("Invalid Email");
                }else if (isEmailValid(email)){
                    emailTextInputLayout.setErrorEnabled(false);
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
                final String password = String.valueOf(passwordTextInputEditText.getText());

                if (password.length() <= 0){
                    passwordTextInputLayout.setError("Password cannot be empty");
                }else {
                    passwordTextInputLayout.setErrorEnabled(false);
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

    private void signUserIn(){
        String email = String.valueOf(emailTextInputEditText.getText());
        String password = String.valueOf(passwordTextInputEditText.getText());

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //task.get
                            Log.d(TAG, "Firebase signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Firebase signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the FireBase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FireBaseUser.getToken() instead.
            String uid = user.getUid();

            //saving values to shared preferences
            sharedPreferences.edit().putString("userName", name).apply();
            sharedPreferences.edit().putString("userEmail", email).apply();
            sharedPreferences.edit().putString("userPhone", phone).apply();
            sharedPreferences.edit().putString("userID", uid).apply();

            //Intent to page after signing user in
            startActivity(new Intent(SignIn.this, Home.class));
            finish();

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

}
