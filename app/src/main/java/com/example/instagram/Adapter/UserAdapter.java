package com.example.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Model.Users;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Users> mUsers;
    private final boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<Users> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Users users = mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.isFollowing.setVisibility(View.VISIBLE);
        holder.username.setText(users.getUsername());
        holder.name.setText(users.getName());

        Picasso.get().load(users.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.image_profile);
        isFollowed(users.getUserId(), holder.btnFollow, holder.isFollowing);

        if (firebaseUser.getUid().equals(users.getUserId())) {
            holder.btnFollow.setVisibility(View.GONE);
            holder.isFollowing.setVisibility(View.GONE);
        }

        /* Follow Unfollow feature */
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.btnFollow.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following").child(users.getUserId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(users.getUserId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following").child(users.getUserId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(users.getUserId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }

            }
        });

    }

    private void isFollowed(String userId, Button btnFollow, TextView isFollowing) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid())
                .child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()) {
                    isFollowing.setVisibility(View.VISIBLE);
                    isFollowing.setText("Following");
                    btnFollow.setText("Unfollow");
                } else {
                    btnFollow.setText("Follow");
                    isFollowing.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView image_profile;
        public TextView username;
        public TextView name;
        public Button btnFollow;
        public TextView isFollowing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            isFollowing = itemView.findViewById(R.id.isFollowing);
        }
    }
}
