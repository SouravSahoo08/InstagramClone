package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.CommentAdapter;
import com.example.instagram.Model.Comments;
import com.example.instagram.Model.Posts;
import com.example.instagram.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.socialview.Mention;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsSectionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView userProfileImage;
    private SocialAutoCompleteTextView commentBox;
    private TextView buttonPost;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comments> commentList;


    private String PostId, UserId, publishersName, publishersImgUrl;
    private FirebaseUser firebaseUser;
    private CircleImageView prevDespUserImg;
    private TextView prevDespUsername;
    private SocialTextView prevDesp;

    public String getPostId() {
        return PostId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_section);

        toolbar = findViewById(R.id.comment_section_topBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        commentBox = findViewById(R.id.comment_box);
        buttonPost = findViewById(R.id.btn_post);
        userProfileImage = findViewById(R.id.user_profile_commentSection);

        Intent intent = getIntent();
        PostId = intent.getStringExtra("PostId");
        UserId = intent.getStringExtra("UserId");
        publishersName = intent.getStringExtra("publishersName");
        publishersImgUrl = intent.getStringExtra("publishersImgUrl");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        prevDespUserImg = findViewById(R.id.prev_desc_profileImage);
        prevDespUsername = findViewById(R.id.prev_desc_username);
        prevDesp = findViewById(R.id.prevDesp);
        setUserProfileImageAndPrevDescription();

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(commentBox.getText().toString())) {
                    addComment();
                }
            }
        });

        recyclerViewComments = findViewById(R.id.recyclerView_comments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setHasFixedSize(true);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, PostId);
        recyclerViewComments.setAdapter(commentAdapter);
        getComments();


    }

    private void getComments() {

        FirebaseDatabase.getInstance().getReference().child("Comments").child(PostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comments comments = dataSnapshot.getValue(Comments.class);
                    commentList.add(comments);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addComment() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(PostId);
        String commnetId = ref.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.clear();
        map.put("comment", commentBox.getText().toString());
        map.put("commnetId", commnetId);
        map.put("userName", firebaseUser.getUid());
        ref.child(commnetId).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                DatabaseReference hashTagsRef = FirebaseDatabase.getInstance().getReference().child("Hash Tags"); // reference to "Hash tags" node in FirebaseDatabase
                List<String> hashTags = commentBox.getHashtags();
                if (!hashTags.isEmpty()) {
                    for (String tags : hashTags) {
                        map.clear();
                        map.put("Hashtag", tags.toLowerCase());
                        map.put("PostId", PostId);
                        hashTagsRef.child(tags.toLowerCase()).child(PostId).setValue(map);
                    }
                }

                Toast.makeText(CommentsSectionActivity.this, "comment added", Toast.LENGTH_SHORT).show();
                commentBox.setText("");
                commentBox.setHint("Add comments...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentsSectionActivity.this, "Something went wrong, try again..", Toast.LENGTH_SHORT).show();
                commentBox.setText("");
                commentBox.setHint("Add comments...");
            }
        });
    }


    private void setUserProfileImageAndPrevDescription() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (users.getImageUrl().equals("default")) {
                    userProfileImage.setImageResource(R.mipmap.ic_launcher);
                    prevDespUserImg.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(userProfileImage);
                    Picasso.get().load(publishersImgUrl).into(prevDespUserImg);
                }
                prevDespUsername.setText(publishersName);
                FirebaseDatabase.getInstance().getReference().child("Posts").child(PostId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Posts posts = snapshot.getValue(Posts.class);
                        prevDesp.setText(posts.getDescription());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayAdapter<Hashtag> hashtagArrayAdapter = new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("Hash Tags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    hashtagArrayAdapter.add(new Hashtag(dataSnapshot.getKey(), (int) snapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentBox.setHashtagAdapter(hashtagArrayAdapter);

        ArrayAdapter<Mention> mentionArrayAdapter = new MentionArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    mentionArrayAdapter.add(new Mention(users.getUsername(), users.getName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentBox.setMentionAdapter(mentionArrayAdapter);

    }
}
