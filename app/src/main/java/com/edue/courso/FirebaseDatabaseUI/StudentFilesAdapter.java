package com.edue.courso.FirebaseDatabaseUI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.edue.courso.FilesS;
import com.edue.courso.Webview;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StudentFilesAdapter extends FirebaseRecyclerAdapter<FilesS, StudentFilesHolder> {
    private static final String TAG = StudentFilesAdapter.class.getSimpleName();
    Context context;

    public StudentFilesAdapter(Class<FilesS> modelClass, int modelLayout, Class<StudentFilesHolder> viewHolderClass, DatabaseReference ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(final StudentFilesHolder viewHolder, FilesS model, int position) {
        final String fileName = model.getFileName();
        final String fileUrl = model.getFileUrl();

        viewHolder.name.setText(fileName);

        viewHolder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //download file
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(fileUrl), ".pdf");

                Intent chooser = Intent.createChooser(browserIntent,"choose app");
                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooser);
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
//                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(browserIntent);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //download file
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(fileUrl), ".pdf");

                Intent chooser = Intent.createChooser(browserIntent,"choose app");
                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooser);
            }
        });
//        StorageReference reference = FirebaseStorage.getInstance().getReference("course/pechu.jpg");
//        final long ONE_MEG = 2048 * 2048;
//        reference.getBytes(ONE_MEG).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
////                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
////                viewHolder.thumbnail.setImageBitmap(bitmap);
////                // create a new renderer
////        PdfRenderer pdfRenderer = null;
////        try {
////            pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(new File(fileUrl), ParcelFileDescriptor.MODE_READ_ONLY));
////            //Open the first page of the document
////            PdfRenderer.Page page = pdfRenderer.openPage(0);
////
////            //Define a target width for the resulting bitmap and use it to calculate the final height
////            int width = 2048;
////            int height = (page.getHeight() * (width / page.getHeight()));
////
////            //Create the bitmap to render the page to
////            Bitmap pageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
////
////            //render the page to the bitmap
////            page.render(pageBitmap , null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
////
////            viewHolder.thumbnail.setImageBitmap(pageBitmap);
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, "Bytes failed", Toast.LENGTH_SHORT).show();
//            }
//        });























//        // create a new renderer
//        PdfRenderer pdfRenderer = null;
//        try {
//            pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(, ParcelFileDescriptor.MODE_READ_ONLY));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Open the first page of the document
//        PdfRenderer.Page page = pdfRenderer.openPage(0);
//
//        //Define a target width for the resulting bitmap and use it to calculate the final height
//        int width = 2048;
//        int height = (page.getHeight() * (width / page.getHeight()));
//
//        //Create the bitmap to render the page to
//        Bitmap pageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        //render the page to the bitmap
//        page.render(pageBitmap , null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//        viewHolder.thumbnail.setImageBitmap(pageBitmap);

//        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent webViewIntent = new Intent(context, Webview.class);
//                webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                webViewIntent.putExtra("urlForWebView", fileUrl);
//                context.startActivity(webViewIntent);
//            }
//        });
    }

    //below override code shows list in reverse order. That is makes newest items appear first
    @Override
    public FilesS getItem(int position) {
        return super.getItem(super.getItemCount() - position - 1);
    }
}
