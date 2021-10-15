package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.CommentsSectionActivity;
import com.example.instagram.Model.Posts;
import com.example.instagram.Model.Saved;
import com.example.instagram.Model.Users;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Posts> mPosts;
    private String publisherName, publishersImgUrl;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(mContext, CommentsSectionActivity.class);
        Posts posts = mPosts.get(position);
        Picasso.get().load(posts.getImageUrl()).into(holder.postImage);
        holder.postDescription.setText(posts.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                publisherName = null;
                publishersImgUrl = null;
                //assert users != null;
                if (users.getImageUrl().equals("default")) {
                    holder.userProfileImg.setImageResource(R.mipmap.ic_launcher);
                } else {
                    publishersImgUrl = users.getImageUrl();
                    Picasso.get().load(publishersImgUrl).into(holder.userProfileImg);
                }
                publisherName = users.getUsername();
                holder.userProfileName1.setText(publisherName);
                holder.userProfileName2.setText(publisherName);
                intent.putExtra("publishersName", publisherName);
                intent.putExtra("publishersImgUrl", publishersImgUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkLiked(posts.getPostId(), holder.like);
        setLikeCountText(posts.getPostId(), holder.noOfLikes);
        getNoOfComments(posts.getPostId(), holder.noOfComments);


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(posts.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsSectionActivity.class);
                intent.putExtra("PostId", posts.getPostId());
                intent.putExtra("UserId", posts.getUserId());
                /*intent.putExtra("publishersName", publisherName);
                intent.putExtra("publishersImgUrl", publishersImgUrl);*/
                mContext.startActivity(intent);
            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent.putExtra("PostId", posts.getPostId());
                intent.putExtra("UserId", posts.getUserId());
                /*intent.putExtra("publishersName", publisherName);
                intent.putExtra("publishersImgUrl", publishersImgUrl);*/
                mContext.startActivity(intent);
            }
        });

        checkIsSaved(posts.getPostId(), holder.save);
        holder.save.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid());
            if (holder.save.getTag().equals("NotSaved")) {
                map.put("PostId", posts.getPostId());
                map.put("ImageUrl", posts.getImageUrl());

                ref.child(posts.getPostId()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Post saved", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ref.child(posts.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Post unasaved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void checkIsSaved(String postId, ImageView save) {

        FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    save.setImageResource(R.drawable.ic_save_clicked);
                    save.setTag("Saved");
                } else {
                    save.setImageResource(R.drawable.ic_save);
                    save.setTag("NotSaved");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNoOfComments(String postId, TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("children count", String.valueOf(snapshot.getChildrenCount()));
                text.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikeCountText(String postId, TextView noOfLikes) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String text = snapshot.getChildrenCount() + " likes";
                        noOfLikes.setText(text);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkLiked(String postId, ImageView like) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(firebaseUser.getUid()).exists()) {
                            like.setImageResource(R.drawable.ic_like_clicked);
                            like.setTag("liked");
                        } else {
                            like.setImageResource(R.drawable.ic_like);
                            like.setTag("like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userProfileImg;
        public ImageView more, like, comments, save, postImage;
        public TextView userProfileName1, noOfLikes, userProfileName2, noOfComments;
        public SocialTextView postDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImg = itemView.findViewById(R.id.user_profile_img);
            more = itemView.findViewById(R.id.more_options);
            like = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            save = itemView.findViewById(R.id.save);
            postImage = itemView.findViewById(R.id.post_image);

            userProfileName1 = itemView.findViewById(R.id.user_profile_name_1);
            userProfileName2 = itemView.findViewById(R.id.user_profile_name_2);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            postDescription = itemView.findViewById(R.id.post_description);
        }
    }
}
