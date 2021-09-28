package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private ImageView close;
    private ImageView previewImg;
    private ImageView addImage;
    private TextView post;
    private SocialAutoCompleteTextView description;
    private Uri imageUri;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.post_closeBtn);
        previewImg = findViewById(R.id.post_previewImg);
        post = findViewById(R.id.post_PostBtn);
        addImage = findViewById(R.id.addImage);
        description = findViewById(R.id.description);
        description.setHashtagColor(500180);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(PostActivity.this);
            }
        });

        /* move to image selector intent */
        CropImage.activity().start(PostActivity.this);
        /* if (imageUri!=null) {*/
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        //}
    }

    private void uploadImage() {

        if (imageUri != null) {
            final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
            pd.setMessage("Uploading");
            pd.show();

            /* under Posts node for each user, corresponding image post is stored */
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            /* dealing with uploaded image url*/
                            imageURL = uri.toString();
                            DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Posts"); // reference to "Posts" node in FirebaseDatabase
                            String postId = postReference.push().getKey();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("Image Url", imageURL);
                            map.put("Post Id", postId);
                            map.put("User Id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            map.put("Description", description.getText().toString());
                            if (postId != null) {
                                postReference.child(postId).setValue(map); //For each post id corresponding info's are stored
                            }

                            /*dealing with hashtags */
                            DatabaseReference hashTagsRef = FirebaseDatabase.getInstance().getReference().child("Hash Tags"); // reference to "Hash tags" node in FirebaseDatabase
                            List<String> hashTags = description.getHashtags();
                            if (!hashTags.isEmpty()) {
                                for (String tags : hashTags) {
                                    map.clear();
                                    map.put("Hash tag", tags.toLowerCase());
                                    map.put("Post Id", postId);
                                    hashTagsRef.child(tags.toLowerCase()).setValue(map);
                                }
                            }

                            /* after successful upload */
                            pd.dismiss();
                            Toast.makeText(PostActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        /* Failure in info updation */

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                /* Failure in image upload */

                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(PostActivity.this, "Image cannont be uploaded, Try again..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Image not added, Tap the \"Plus icon\".", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                imageUri = result.getUri();
            }
            previewImg.setImageURI(imageUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PostActivity.this, MainActivity.class));
        finish();
    }
}