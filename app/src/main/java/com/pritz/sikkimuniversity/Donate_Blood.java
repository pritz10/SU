package com.pritz.sikkimuniversity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;
public class Donate_Blood extends AppCompatActivity {
    Spinner spinner;
    EditText ph;
    ImageView pdf2;
    Uri img=null;
    String bloodgroup;
    private static final int GALLERY_REQUEST = 1;
    private StorageReference mStorageRefe= FirebaseStorage.getInstance().getReference();
    private ProgressDialog progressDialog;
    final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("blood_donation");
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate__blood);
        getSupportActionBar().setTitle("Donate Blood");
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.bloodgrp, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        pdf2 = (ImageView) findViewById(R.id.pdf);
        pdf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, GALLERY_REQUEST);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + "", Toast.LENGTH_LONG).show();
                  bloodgroup = spinner.getSelectedItem().toString();            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final MediaPlayer mp=MediaPlayer.create(this,R.raw.lo);
        Button button = (Button) findViewById(R.id.button11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Just Wait.....\n" +
                        "\nYou have chosen to donate your blood_gettersetter.Thanks For Your Cooperation.You are a real life hero. Salute to you. You will get a call when any one need blood_gettersetter. So if you are willing to give then you can donate otherwise you can tell them that you are not interested right now!");
                ph=(EditText)findViewById(R.id.phnumber);
                final String phone=ph.getText().toString();
                if ((img != null &&!TextUtils.isEmpty(phone))&& !bloodgroup.equals("Enter Your Blood Group")) {

                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    StorageReference reference=mStorageRefe.child("blodn").child(img.getLastPathSegment());
                    reference.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                            Uri downloaduri=taskSnapshot.getDownloadUrl();
                            DatabaseReference databaseReference=mref.push();
                            databaseReference.child("blodgrp").setValue(bloodgroup.toString());
                            databaseReference.child("Phone").setValue(phone);
                            databaseReference.child("image").setValue(downloaduri.toString());
                            SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                            String name = sharedPreferences.getString("s_name","");
                            databaseReference.child("name").setValue(name);
                            SharedPreferences got = getSharedPreferences("blodgr", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = got.edit();
                            editor.putString("phone", phone);
                            editor.apply();
                            mp.start();
                            progressDialog.dismiss();
                            Intent i=new Intent(Donate_Blood.this,Blood.class);
                            finish();
                            startActivity(i);
                        }
                    });

                }


            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
             Uri imageurl = data.getData();
            CropImage.activity(imageurl)
                    .setGuidelines(CropImageView.Guidelines.ON)
                  .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                img = result.getUri();
                pdf2.setImageURI(img);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}