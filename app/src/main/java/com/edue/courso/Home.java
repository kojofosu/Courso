package com.edue.courso;

import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Home extends AppCompatActivity {
    FloatingActionButton fab;
    TextView itemTitle, testTV;

    private static final String TAG = "Home Activity";


//    FirebaseDatabase firebaseDatabase;

    //fireBase download reference
    StorageReference islandRef = FirebaseStorage.getInstance().getReference();

    // FireBase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init
        init();

        //fireBase download from storage
        //firebaseDownloadFromStorage();

        //fireBase database
        firebaseDatabase();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewMaterial.class);
                startActivity(intent);
            }
        });

    }

    private void firebaseDatabase() {
//        firebaseDatabase = FirebaseDatabase.getInstance();
        // DatabaseReference myRef = database.getReference("message/next");
       // myRef.setValue("Hello, World!");

        DatabaseReference myRef = database.getReference("Coursematerials/Department");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Upload upload = dataSnapshot.getValue(Upload.class);
                String deptname = upload.getDeptName();
                Log.d(TAG, "Value is: " + deptname);
                testTV.setText(deptname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void init() {
        fab = findViewById(R.id.fab);
        itemTitle = findViewById(R.id.item_title);
        testTV = findViewById(R.id.test_TV);
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
}
