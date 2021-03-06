package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.NotificationFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private final int navHome = R.id.nav_home;
    private final int navSearch = R.id.nav_search;
    private final int navAdd = R.id.nav_add;
    private final int navProfile = R.id.nav_profile;
    private final int navHeart = R.id.nav_heart;
    private BottomNavigationView buttomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttomNavigationView = findViewById(R.id.buttom_navigation);
        buttomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case navHome:
                        selectorFragment = new HomeFragment();
                        break;
                    case navSearch:
                        selectorFragment = new SearchFragment();
                        break;
                    case navAdd:
                        selectorFragment = null;
                        startActivity(new Intent(MainActivity.this, PostActivity.class));
                        finish();
                        break;
                    case navHeart:
                        selectorFragment = new NotificationFragment();
                        break;
                    case navProfile:
                        selectorFragment = new ProfileFragment();
                        break;
                }
                if (selectorFragment != null) {
                    getSharedPreferences("PROFILE", MODE_PRIVATE).edit().clear().apply();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileID", profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            //buttomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }
}