package com.example.zajecia6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zajecia6.callback.UserRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String PACKAGE_NAME;
    private static Context context;

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private FirestoreDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getPackageName();
        mAuth = FirebaseAuth.getInstance();
        dao = new FirestoreDAO(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.main_activity);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        if(mAuth.getCurrentUser() == null){
            menu.getItem(0).setTitle("Zaloguj się lub utwórz konto");
        }else{
            menu.getItem(0).setTitle("Twoje konto");
            dao.getUserById(mAuth.getCurrentUser().getUid(), new UserRetrievedCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    if(user.isAdmin()){
                        menu.getItem(1).setVisible(true);
                    }else{
                        menu.getItem(1).setVisible(false);
                    }
                }
            });
        }

        ActionBarDrawerToggle myToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(myToggle);
        myToggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }

        SharedPreferences sharedPreferences
                = getSharedPreferences(
                        "sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor
                = sharedPreferences.edit();
        final boolean isDarkModeOn
                = sharedPreferences
                .getBoolean(
                        "isDarkModeOn", false);

        if(isDarkModeOn) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
            menu.findItem(R.id.contrast_mode).setTitle("Tryb domyślny").setIcon(R.drawable.ic_lightmode);
            View header = navigationView.getHeaderView(0);
            //header.findViewById(R.id.imageView).setVisibility(View.INVISIBLE);
            header.findViewById(R.id.imageView2).setVisibility(View.VISIBLE);

        }
        else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
            menu.findItem(R.id.contrast_mode).setTitle("Tryb kontrastu");
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new HomeFragment()).commit();
                break;
            case R.id.menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new MenuFragment()).commit();
                break;
            case R.id.gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new GalleryFragment()).commit();
                break;
            case R.id.contact:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new ContactFragment()).commit();
                break;
            case R.id.profile:
                if(mAuth.getCurrentUser() == null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new LoginRegisterFragment()).commit();
                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new ProfileFragment()).commit();
                }
                break;
            case R.id.contrast_mode:

                SharedPreferences sharedPreferences
                        = getSharedPreferences(
                        "sharedPrefs", MODE_PRIVATE);
                final SharedPreferences.Editor editor
                        = sharedPreferences.edit();
                final boolean isDarkModeOn
                        = sharedPreferences
                        .getBoolean(
                                "isDarkModeOn", false);

               if(isDarkModeOn) {
                   AppCompatDelegate
                           .setDefaultNightMode(
                                   AppCompatDelegate
                                           .MODE_NIGHT_NO);
                   editor.putBoolean(
                           "isDarkModeOn", false);
                   editor.apply();
               }
               else {
                   AppCompatDelegate
                           .setDefaultNightMode(
                                   AppCompatDelegate
                                           .MODE_NIGHT_YES);
                   editor.putBoolean(
                           "isDarkModeOn", true);
                   editor.apply();
               }
                break;

            case R.id.admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new AdminFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}