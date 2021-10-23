package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close, done;
    private CircleImageView profileImage;
    private TextView updateprofileImage;
    private EditText name, username;
    private SocialEditText bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.back_btn);
        done = findViewById(R.id.done_btn);
        profileImage = findViewById(R.id.editprofile_userimg);
        updateprofileImage = findViewById(R.id.chng_profileImg_btn);
        name = findViewById(R.id.editname);
        username = findViewById(R.id.editUserName);
        bio = findViewById(R.id.editBio);

        fetchPreviousData();

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0)
                    checkUsername(s.toString());
            }
        });

    }

    private void checkUsername(String uName) {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users.getUsername().equals(uName)) {
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
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                //profile image
                if (users.getImageUrl().equals("default"))
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