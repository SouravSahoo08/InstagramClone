package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.GalleryAdapter;
import com.example.instagram.Adapter.SavedAdapter;
import com.example.instagram.EditProfileActivity;
import com.example.instagram.Model.Posts;
import com.example.instagram.Model.Saved;
import com.example.instagram.Model.Users;
import com.example.instagram.R;
import com.example.instagram.StartupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView backButton, menuOption;
    private TextView username;
    private CircleImageView userDp;
    private TextView postCount, followerCount, followingCount;
    private TextView userFullname;
    private SocialTextView bio;
    private AppCompatButton editProfileOrFollowBtn, mesgBtn;
    private ImageButton postSelector, saveSelector;
    private RecyclerView recyclerViewPostList, recyclerViewSavedList;
    private GalleryAdapter galleryAdapter;
    private List<Posts> photos;
    private SavedAdapter savedAdapter;
    private List<Saved> savedPosts;
    private FirebaseUser firebaseUser;
    private String profileId;

    private Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //profileId = firebaseUser.getUid();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                .getString("profileID", "none");
        if (data.equals("none")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
        }

        toolbar = view.findViewById(R.id.toolbar);
        backButton = view.findViewById(R.id.back_btn);
        menuOption = view.findViewById(R.id.option_menu);
        username = view.findViewById(R.id.username_profilePage);
        userDp = view.findViewById(R.id.user_profile_dp);
        postCount = view.findViewById(R.id.posts_count);
        followerCount = view.findViewById(R.id.follewers_count);
        followingCount = view.findViewById(R.id.following_count);
        userFullname = view.findViewById(R.id.users_fullname);
        bio = view.findViewById(R.id.user_Bio);
        editProfileOrFollowBtn = view.findViewById(R.id.edit_profile);
        mesgBtn = view.findViewById(R.id.message);
        postSelector = view.findViewById(R.id.post_Selector);
        saveSelector = view.findViewById(R.id.saved_Selector);
        recyclerViewPostList = view.findViewById(R.id.recyclerView_post_list);
        recyclerViewSavedList = view.findViewById(R.id.recyclerView_saved_list);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeFragment.class));
                requireActivity().finish();
            }
        });

        updateUserInfo();
        getPostCount();
        getFollowersAndFollowingCount();

        if (profileId.equals(firebaseUser.getUid())) {
            editProfileOrFollowBtn.setText("Edit Profile");
        } else {
            checkFollowingStat();
        }

        editProfileOrFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editProfileOrFollowBtn.getText().toString().equals("Edit Profile")) {
                    if (editProfileOrFollowBtn.getText().toString().equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following")
                                .child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId).child("followers")
                                .child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following")
                                .child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("follow").child(profileId).child("followers")
                                .child(firebaseUser.getUid()).removeValue();
                    }
                } else {
                    //editProfile
                    startActivity(new Intent(getActivity(), EditProfileActivity.class));
                }
            }
        });

        postSelector.setOnClickListener(v -> {
            recyclerViewPostList.setVisibility(View.VISIBLE);
            recyclerViewSavedList.setVisibility(View.GONE);
        });
        photos = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(getContext(), photos);
        recyclerViewPostList.setHasFixedSize(true);
        recyclerViewPostList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewPostList.setAdapter(galleryAdapter);
        getUsersPosts();

        saveSelector.setOnClickListener(v -> {
            recyclerViewPostList.setVisibility(View.GONE);
            recyclerViewSavedList.setVisibility(View.VISIBLE);
        });
        savedPosts = new ArrayList<>();
        savedAdapter = new SavedAdapter(getContext(), savedPosts);
        recyclerViewSavedList.setHasFixedSize(true);
        recyclerViewSavedList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewSavedList.setAdapter(savedAdapter);
        getUsersSavedPosts();

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartupActivity.class));
                getActivity().finish();
            }
        });

        return view;

    }

    private void getUsersSavedPosts() {

        FirebaseDatabase.getInstance().getReference().child("Saved").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedPosts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Saved saved = dataSnapshot.getValue(Saved.class);
                    savedPosts.add(saved);
                }
                Collections.reverse(photos);
                savedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUsersPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Posts posts = dataSnapshot.getValue(Posts.class);

                    if (posts.getUserId().equals(profileId))
                        photos.add(posts);
                }
                Collections.reverse(photos);
                galleryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkFollowingStat() {
        FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    editProfileOrFollowBtn.setText("Following");
                    mesgBtn.setVisibility(View.VISIBLE);
                } else {
                    editProfileOrFollowBtn.setText("Follow");
                    mesgBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Posts posts = dataSnapshot.getValue(Posts.class);
                    if (posts.getUserId().equals(profileId))
                        counter++;
                }
                postCount.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("follow");
        ref.child(profileId).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerCount.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child(profileId).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCount.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                username.setText(users.getUsername());
                if (users.getImageUrl().equals("default")) {
                    userDp.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(userDp);
                }
                userFullname.setText(users.getName());
                bio.setText(users.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}