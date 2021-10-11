package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapter.HashTagAdapter;
import com.example.instagram.Adapter.UserAdapter;
import com.example.instagram.Model.Users;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SocialAutoCompleteTextView searchBar;
    private RecyclerView userRecyclerView;

    private List<Users> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView tagRecyclerView;
    private List<String> mHashTags;
    private List<String> mHashTagCount;
    private HashTagAdapter tagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchBar = view.findViewById(R.id.search_bar);
        userRecyclerView = view.findViewById(R.id.recycleView_users);
        userRecyclerView.setHasFixedSize(false);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        userRecyclerView.setAdapter(userAdapter);
        readUser();

        tagRecyclerView = view.findViewById(R.id.recyclerView_hashTags);
        tagRecyclerView.setHasFixedSize(false);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mHashTags = new ArrayList<>();
        mHashTagCount = new ArrayList<>();
        tagAdapter = new HashTagAdapter(getContext(), mHashTags, mHashTagCount);
        tagRecyclerView.setAdapter(tagAdapter);
        readHashtags();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return view;
    }

    private void filter(String txt) {
        List<String> searchTags = new ArrayList<>();
        List<String> searchTagsCount = new ArrayList<>();
        for (String s : mHashTags) {
            if (s.toLowerCase().contains(txt.toLowerCase())) {
                searchTags.add(s);
                searchTagsCount.add(mHashTagCount.get(mHashTags.indexOf(s)));
            }
        }
        tagAdapter.filter(searchTags, searchTagsCount);
    }

    private void readHashtags() {

        FirebaseDatabase.getInstance().getReference().child("Hash Tags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTags.clear();
                mHashTagCount.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mHashTags.add(dataSnapshot.getKey());
                    mHashTagCount.add(String.valueOf(dataSnapshot.getChildrenCount()));
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("Username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    mUsers.add(users);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUser() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(searchBar.getText().toString())) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Users users = dataSnapshot.getValue(Users.class);
                        mUsers.add(users);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}