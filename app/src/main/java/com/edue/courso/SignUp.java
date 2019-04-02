package com.edue.courso;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {


    TextInputLayout emailTextInputLayout, passwordTextInputLayout, nameTextInputLayout, phoneTextInputLayout, locationTextInputLayout, passwordConfirmTextInputLayout;
    TextInputEditText emailTextInputEditText, passwordTextInputEditText, nameTextInputEditText, phoneTextInputEditText, locationTextInputEditText, passwordConfirmTextInputEditText;
    TextView goToSignInTextView;
    Button signUpBtn;
    Spinner businessTypeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Initialize
        init();

        //onClicks
        onClickEvents();

        //editTextChange Events
        textChangeEvents();

        //initializing spinner array
        String[] businessTypeList = {"Shuttle", "Rental", "Haulage"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, businessTypeList);
        businessTypeSpinner.setAdapter(adapter);
    }


    private void init(){
        businessTypeSpinner = findViewById(R.id.sign_up_type_spinner);
        emailTextInputLayout = findViewById(R.id.sign_up_email_text_input_layout);
        passwordTextInputLayout = findViewById(R.id.sign_up_password_text_input_layout);
        nameTextInputLayout = findViewById(R.id.sign_up_name_text_input_layout);
        phoneTextInputLayout = findViewById(R.id.sign_up_phone_text_input_layout);
        locationTextInputLayout = findViewById(R.id.sign_up_location_text_input_layout);
        passwordConfirmTextInputLayout = findViewById(R.id.sign_up_password_confirm_text_input_layout);
        emailTextInputEditText = findViewById(R.id.sign_up_email_text_input_edittext);
        passwordTextInputEditText = findViewById(R.id.sign_up_password_text_input_edittext);
        nameTextInputEditText = findViewById(R.id.sign_up_name_text_input_edittext);
        phoneTextInputEditText = findViewById(R.id.sign_up_phone_text_input_edittext);
        locationTextInputEditText = findViewById(R.id.sign_up_location_text_input_edittext);
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
            public void onClick(View view) {
                String name = String.valueOf(nameTextInputEditText.getText());
                String email = String.valueOf(emailTextInputEditText.getText());
                String phone = String.valueOf(phoneTextInputEditText.getText());
                String location = String.valueOf(locationTextInputEditText.getText());
                String password = String.valueOf(passwordTextInputEditText.getText());
                String passwordConfirm = String.valueOf(passwordConfirmTextInputEditText.getText());
                hideKeyboard();
                if (name.length() > 0 && isEmailValid(email) && phone.length() == 10 && location.length() > 0 && password.length() > 0 && passwordConfirm.length() > 0 && password.equals(passwordConfirm)) {
                    Toast.makeText(SignUp.this, "Signing up...", Toast.LENGTH_SHORT).show();
                }else if (name.isEmpty()){
                    nameTextInputLayout.setError("Business' name cannot be empty");
                }else if (email.isEmpty()){
                    emailTextInputLayout.setError("Email cannot be empty");
                }else if (phone.isEmpty()){
                    phoneTextInputLayout.setError("Phone number cannot be empty");
                }else if (location.isEmpty()){
                    locationTextInputLayout.setError("Location cannot be empty");
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
                    nameTextInputLayout.setError("Business' name cannot be empty");
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

        locationTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String location = String.valueOf(locationTextInputEditText.getText());
                if (location.length() <= 0){
                    locationTextInputLayout.setError("Location cannot be empty");
                }else {
                    locationTextInputLayout.setErrorEnabled(false);
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
