package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.Model.Users;
import com.example.instagram.Utility.UtilityClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
    private ImageView close, done;
    private CircleImageView profileImage;
    private TextView updateprofileImage;
    FirebaseUser firebaseUser;
    private MaterialEditText name, username, bio;
    private Uri imageUri;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        close = findViewById(R.id.back_btn);
        done = findViewById(R.id.done_btn);
        profileImage = findViewById(R.id.editprofile_userimg);
        updateprofileImage = findViewById(R.id.chng_profileImg_btn);
        name = findViewById(R.id.editname);
        username = findViewById(R.id.editUserName);
        bio = findViewById(R.id.editBio);

        fetchPreviousData();

        updateprofileImage.setOnClickListener(v -> {
            CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
        });

        String previousUsername = username.getText().toString();
        /*username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0)
                    checkUsername(s.toString(), previousUsername);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        done.setOnClickListener(v -> {


            if (imageUri != null) {
                final ProgressDialog pd = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
                pd.setMessage("Updating Profile..");
                pd.show();

                StorageReference sRef = FirebaseStorage.getInstance().getReference("ProfileImages")
                        .child(firebaseUser.getUid())
                        .child(firebaseUser.getUid() + "." + UtilityClass.getFileExtension(this, imageUri));
                sRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageURL = uri.toString();
                                Log.d("ImageUrl", imageURL);
                                Map<String, Object> map = new HashMap<>();
                                map.put("Username", username.getText().toString());
                                map.put("Name", name.getText().toString());
                                map.put("Bio", bio.getText().toString());
                                map.put("ImageUrl", imageURL);

                                for (Map.Entry<String, Object> m : map.entrySet()) {
                                    Log.d("Map", m.getKey() + " : " + m.getValue());
                                }

                                ref.child(firebaseUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class)
                                                    .putExtra("publisherId", firebaseUser.getUid()));
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditProfileActivity.this, MainActivity.class)
                                                .putExtra("publisherId", firebaseUser.getUid()));
                                    }
                                });
                            }
                        });
                    }
                });
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                imageUri = result.getUri();
            }
            profileImage.setImageURI(imageUri);
        }
    }

    private void checkUsername(String uName, String previousUsername) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users.getUsername().equals(uName) && !users.getUsername().equals(previousUsername)) {
                        //user name exists
                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0);
                    } else {
                        //good to go
                        username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_ok, 0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchPreviousData() {
        ref.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                //profile image
                if (!users.getImageUrl().equals("default"))
                    Picasso.get().load(users.getImageUrl()).into(profileImage);
                else
                    profileImage.setImageResource(R.mipmap.ic_launcher);

                //fullname
                name.setText(users.getName());
                //username
                username.setText(users.getUsername());
                //bio
                if (!TextUtils.isEmpty(users.getBio()))
                    bio.setText(users.getBio());
                else
                    bio.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}