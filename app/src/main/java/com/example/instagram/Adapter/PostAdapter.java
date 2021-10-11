package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.Posts;
import com.example.instagram.Model.Users;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Posts> mPosts;

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
        Posts posts = mPosts.get(position);
        Picasso.get().load(posts.getImageUrl()).into(holder.postImage);
        holder.postDescription.setText(posts.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                //assert users != null;
                if (users.getImageUrl().equals("default")) {
                    holder.userProfileImg.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(holder.userProfileImg);
                }
                holder.userProfileName1.setText(users.getUsername());
                holder.userProfileName2.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(posts.getUserId())
                        .child("LikeCount");

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userProfileImg, more, like, comments, save, postImage;
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
