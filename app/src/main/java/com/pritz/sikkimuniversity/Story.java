package com.pritz.sikkimuniversity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import com.pritz.sikkimuniversity.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.pritz.sikkimuniversity.R.id.postimage;
import static com.pritz.sikkimuniversity.R.id.strimage;


public class Story extends Fragment {
    Uri imageurl=null;
    EditText storymes;
    ImageButton send;
    private RecyclerView pods;
    private DatabaseReference mdatabase;
    ImageButton adde;
    ImageButton opner;
    Uri imgll = null;
    ImageButton cam;
    public MediaPlayer mp;
    private final int img=1;
    Bitmap bitmap;
    ImageView image;
    LinearLayout opener1;
    ProgressBar progressBar1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST =1888;
    private static final int GALLERY_REQUEST=1;
    private StorageReference mStorageRef;
    public ProgressDialog progressDialog;
    public Story() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_story, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        pods=(RecyclerView)getActivity().findViewById(R.id.pos);
        cam=(ImageButton)getActivity().findViewById(R.id.cameras);
        opner=(ImageButton)getActivity().findViewById(R.id.opener);
        send=(ImageButton)getActivity().findViewById(R.id.sendbtn);
        image = (ImageView)getActivity().findViewById(R.id.image);
        adde=(ImageButton)getActivity().findViewById(R.id.adde);
        opener1 = (LinearLayout)getActivity().findViewById(R.id.add);
        storymes=(EditText)getActivity().findViewById(R.id.strymsg);
        progressBar1=(ProgressBar)getActivity().findViewById(R.id.progressBar);
        mp=MediaPlayer.create(getContext(),R.raw.sentmessage);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("story");
        onStart();
        pods.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mdatabase.keepSynced(true);
        pods.setLayoutManager(mLayoutManager);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startposting();
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST );
            }
        });
        adde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        opner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(opener1.getVisibility() == View.GONE)
                    opener1.setVisibility(View.VISIBLE);
                else
                    opener1.setVisibility(View.GONE);
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, GALLERY_REQUEST);
            }
        });
    }

    private void startposting(){
        final DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("story");
        mStorageRef= FirebaseStorage.getInstance().getReference();
        progressDialog.setMessage("Just Wait.....");
        final String title = storymes.getText().toString().trim();
        if (!TextUtils.isEmpty(title) && imageurl == null)
        { progressDialog.setTitle("Posting");
            progressDialog.setMessage("Just a while!");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            DatabaseReference databaseReference=mref.push();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            String name = sharedPreferences.getString("s_name","");
            databaseReference.child("stitle").setValue(name+"\n"+title);
            final String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
            databaseReference.child("sdate").setValue(currentDateTimeString);
            databaseReference.child("simage").setValue("khjkjh");
            storymes.setText("");
            mp.start();
            progressDialog.dismiss();
        }

        if (!TextUtils.isEmpty(title)   && imageurl != null)
        {
            progressDialog.setTitle("Uploading Image...");
            progressDialog.setMessage("Please wait while we upload and process the image.");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            StorageReference reference=mStorageRef.child("storypics").child(imageurl.getLastPathSegment());
            reference.putFile(imageurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    Uri downloaduri=taskSnapshot.getDownloadUrl();
                    DatabaseReference databaseReference=mref.push();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    String name = sharedPreferences.getString("s_name","");
                    String a=name+"\n"+title;
                    databaseReference.child("stitle").setValue(a);
                    final String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
                    databaseReference.child("sdate").setValue(currentDateTimeString);
                    databaseReference.child("simage").setValue(downloaduri.toString());
                    progressDialog.dismiss();
                    mp.start();
                    Story c=new Story();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content,c).commit();
                }
            });

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<forstory, Story.storyholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<forstory, Story.storyholder>
                (forstory.class,
                        R.layout.str,
                        Story.storyholder.class,
                        mdatabase) {
            @Override
            protected void populateViewHolder(Story.storyholder viewHolder, forstory model, int position) {
                viewHolder.setStitle(model.getStitle());
                viewHolder.setSdate(model.getSdate());
                viewHolder.setSimage(getActivity(),model.getSimage());
            }
        };
        pods.setAdapter(firebaseRecyclerAdapter);


    }
    public  static class storyholder extends RecyclerView.ViewHolder
    {
        View mView;
        public storyholder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setStitle(String stitle) {
            TextView ptitle = (TextView) mView.findViewById(R.id.strtitle);
            ptitle.setText(stitle);
        }


        public void setSimage(Context ctx, String simage) {
            ImageView post = (ImageView) mView.findViewById(strimage);
            Picasso.with(ctx).load(simage).resize(1000, 1000).centerCrop().into(post);

        }

        public void setSdate(String sdate) {
            TextView date1 = (TextView) mView.findViewById(R.id.strdate);
            date1.setText(sdate);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==img && resultCode==RESULT_OK && data!=null){
            imageurl=data.getData();
                image.setImageURI(imageurl);
                image.setVisibility(View.VISIBLE);



        }

        if (requestCode == CAMERA_REQUEST && resultCode ==RESULT_OK){
            Uri path=data.getData();
            final DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("story");
            mStorageRef= FirebaseStorage.getInstance().getReference();
            progressDialog.setMessage("Just Wait.....");
            /*imageurl=data.getData();
            image.setImageURI(imageurl);
            image.setVisibility(View.VISIBLE);*/
            progressDialog.setTitle("Uploading Image...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            StorageReference reference=mStorageRef.child("storypics").child(path.getLastPathSegment());
            reference.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    Uri downloaduri=taskSnapshot.getDownloadUrl();
                    DatabaseReference databaseReference=mref.push();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    String name = sharedPreferences.getString("s_name","");
                     databaseReference.child("stitle").setValue(name);
                    final String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
                    databaseReference.child("sdate").setValue(currentDateTimeString);
                    databaseReference.child("simage").setValue(downloaduri.toString());
                    progressDialog.dismiss();
                    Story c=new Story();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content,c).commit();
                }
            });


        }


    }

  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageurl = data.getData();
            CropImage.activity(imageurl)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setInitialCropWindowPaddingRatio(0)
                    //.setAspectRatio(1, 1)
                    .start(getContext(), this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgll = result.getUri();
                image.setImageURI(imgll);
                image.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            imageurl=data.getData();
            CropImage.activity(imageurl)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setInitialCropWindowPaddingRatio(0)
                    .start(getContext(), this);
          //  image.setImageURI(imageurl);
        }

    }*/
}