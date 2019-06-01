package com.edue.courso;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgottenPassword extends AppCompatActivity {
    TextInputLayout forgotPassTIL;
    TextInputEditText forgotPassTIE;
    Button forgotPassBtn, forgotPassBackBtn;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        //init
        init();

        auth = FirebaseAuth.getInstance();

        //eventListeners
        evenListeners();
    }

    private void evenListeners() {
        forgotPassTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String email = String.valueOf(forgotPassTIE.getText());

                if (!isEmailValid(email) || TextUtils.isEmpty(email)){
                    forgotPassTIL.setError("Invalid Email");
                }else if (isEmailValid(email)){
                    forgotPassTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotPassTIE.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    forgotPassTIL.setErrorEnabled(true);
                    forgotPassTIL.setError("Enter Registered Email ID");
                }else if (isEmailValid(email) || !email.isEmpty()) {
                    forgotPassTIL.setErrorEnabled(false);
                    //show progress dialog
                    progressDialog = new ProgressDialog(ForgottenPassword.this);
                    progressDialog.setMessage("Sending password reset to Email...");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(0);
                    progressDialog.show();

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Failed : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        forgotPassBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        forgotPassTIL = findViewById(R.id.forgot_pass_email_TIL);
        forgotPassTIE = findViewById(R.id.forgot_pass_email_TIE);
        forgotPassBtn = findViewById(R.id.btn_reset_password);
        forgotPassBackBtn = findViewById(R.id.forgot_pass_btn_back);
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
