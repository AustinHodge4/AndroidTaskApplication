package com.example.austin.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.transition.Fade;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   HomeFragment.OnNoteEvent {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MainActivity";
    private FirebaseFirestore mFirestore;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    public static MainActivityViewModel mViewModel;
    public static DocumentReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mFirestore = FirebaseFirestore.getInstance();
        mViewModel = new MainActivityViewModel();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());

        HomeFragment fragment = HomeFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, HomeFragment.TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if(isArchiveFragmentVisible() || isHomeFragmentVisible())
            finish();
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.app_bar_search:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_home:
                mToolbar.setTitle(R.string.home_name);
                if(isHomeFragmentVisible())
                    break;

                HomeFragment homeFragment = HomeFragment.newInstance();

                FragmentTransaction homeTransaction = getSupportFragmentManager().beginTransaction();
                homeTransaction.replace(R.id.fragment_container, homeFragment, HomeFragment.TAG);
                homeTransaction.addToBackStack(null);
                homeTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                homeTransaction.commit();
                break;
            case R.id.nav_archive:
                mToolbar.setTitle(R.string.archive_name);
                if(isArchiveFragmentVisible())
                    break;
                ArchiveFragment archiveFragment = ArchiveFragment.newInstance();

                FragmentTransaction archiveTransaction = getSupportFragmentManager().beginTransaction();
                archiveTransaction.replace(R.id.fragment_container, archiveFragment, ArchiveFragment.TAG);
                archiveTransaction.addToBackStack(null);
                archiveTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                archiveTransaction.commit();
                break;
            case R.id.nav_trash:
                mToolbar.setTitle(R.string.trash_name);
                if(isTrashFragmentVisible())
                    break;
                TrashFragment trashFragment = TrashFragment.newInstance();

                FragmentTransaction trashTransaction = getSupportFragmentManager().beginTransaction();
                trashTransaction.replace(R.id.fragment_container, trashFragment, TrashFragment.TAG);
                trashTransaction.addToBackStack(null);
                trashTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                trashTransaction.commit();
                break;
            case R.id.nav_settings:
                mToolbar.setTitle(R.string.settings);
                break;
            case R.id.nav_signout:
                signOut();
                startSignIn();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onStart(){
        super.onStart();
        if(shouldStartSignIn()){
            startSignIn();
            return;
        }
        if(!isUserSet()) setUser();

        View headerView = mNavigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.user_name);
        TextView navUserEmail = headerView.findViewById(R.id.user_email);
        ImageView navUserIcon = headerView.findViewById(R.id.user_icon);

        navUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        navUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Glide.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(navUserIcon);

    }
    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onArchiveEvent(final ArrayList<DocumentSnapshot> snapshots){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.constraint_layout),
                R.string.message_note_archived, Snackbar.LENGTH_SHORT);
        mySnackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteUtil.unarchiveDocument(snapshots);
            }
        });
        mySnackbar.show();
    }

    @Override
    public void onTrashEvent(final ArrayList<DocumentSnapshot> snapshots){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.constraint_layout),
                R.string.message_note_trashed, Snackbar.LENGTH_SHORT);
        mySnackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteUtil.untrashDocument(snapshots);
            }
        });
        mySnackbar.show();
    }

    private boolean isHomeFragmentVisible(){
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
        return (homeFragment != null && homeFragment.isVisible());
    }
    private boolean isArchiveFragmentVisible(){
        ArchiveFragment archiveFragment = (ArchiveFragment) getSupportFragmentManager().findFragmentByTag(ArchiveFragment.TAG);
        return (archiveFragment != null && archiveFragment.isVisible());
    }
    private boolean isTrashFragmentVisible(){
        TrashFragment trashFragment = (TrashFragment) getSupportFragmentManager().findFragmentByTag(TrashFragment.TAG);
        return (trashFragment != null && trashFragment.isVisible());
    }
    private void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success", "Logged Out!");
                    }
                });
    }
    public boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }
    public void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher_new)
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn();
            }
            if(!isUserSet()) setUser();
            Log.d("Success", "Logged In!!!");
            sendData();
        }
    }
    public static boolean isUserSet(){
        return mUserRef != null;
    }
    public static void setUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            mUserRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        }
    }
    public boolean isUserSignIn(){
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }
    private void sendData(){
        if(isUserSignIn()) {
            mUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            mUserRef.update("last_login", new Date()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"Updated User");
                                }
                            });
                        }
                        else{
                            Map<String, Object> map = new HashMap<>();
                            map.put("last_login", new Date());
                            Map<String, Object> labels = new HashMap<>();
                            map.put("lables", labels);
                            mUserRef.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"Created User");
                                }
                            });
                        }
                    }
                    else {
                        Log.w(TAG, "User setup error");
                    }
                }
            });
        }
    }
}
