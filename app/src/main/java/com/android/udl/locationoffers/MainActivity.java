package com.android.udl.locationoffers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.database.UserMessagesSQLiteHelper;
import com.android.udl.locationoffers.database.UserSQLiteManage;
import com.android.udl.locationoffers.domain.Commerce;
import com.android.udl.locationoffers.fragments.CommerceFragment;
import com.android.udl.locationoffers.fragments.LocationFragment;
import com.android.udl.locationoffers.fragments.MessageDetailFragment;
import com.android.udl.locationoffers.fragments.NewMessageFormFragment;
import com.android.udl.locationoffers.fragments.PlacesInterestsFragment;
import com.android.udl.locationoffers.fragments.UserFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CommerceFragment.OnFragmentInteractionListener,
        NewMessageFormFragment.OnFragmentInteractionListener,
        MessageDetailFragment.OnFragmentInteractionListener {

    private static final String TAG_USER = "tag_user";
    private static final String TAG_COMMERCE = "tag_commerce";
    private static final String TAG_LOCATION = "tag_location";

    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private boolean doubleBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        String mode = sharedPreferences.getString("mode", null);
        if (mode.equals(getString(R.string.user))) {
            navigationView.inflateMenu(R.menu.drawer_user);
            navigationView.inflateMenu(R.menu.drawer);
            startFragment(new UserFragment(), TAG_USER);
            setTitle(getString(R.string.messages));
        } else {
            navigationView.inflateMenu(R.menu.drawer_commerce);
            navigationView.inflateMenu(R.menu.drawer);
            CommerceFragment commerceFragment = CommerceFragment.newInstance("messages");
            startFragment(commerceFragment, TAG_COMMERCE);
            setTitle(getString(R.string.messages));
        }

        TextView tv = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.textView);
        tv.setText(sharedPreferences.getString("user", null));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            CommerceFragment commerceFragment = (CommerceFragment) getSupportFragmentManager().findFragmentByTag(TAG_COMMERCE);
            if(commerceFragment != null && commerceFragment.isFabOpened()){
                commerceFragment.closeFab();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if (backAction()) {
                        super.onBackPressed();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public boolean backAction(){
        if(doubleBack) return true;
        this.doubleBack = true;
        Toast.makeText(this, getString(R.string.double_back), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBack = false;
                }
            }, 2000);
        return false;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startFragment(Fragment fragment, String tag) {
        //Toast.makeText(this,fragment.toString(),Toast.LENGTH_SHORT).show();
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, fragment, tag)
                    .commit();
        }
    }

    private void startFragmentBackStack(Fragment fragment) {
        //Toast.makeText(this,fragment.toString(),Toast.LENGTH_SHORT).show();
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String title = getString(R.string.app_name);

        if (id == R.id.nav_commerce_list) {
            CommerceFragment commerceFragment = CommerceFragment.newInstance("messages");
            startFragment(commerceFragment, TAG_COMMERCE);
            title = getString(R.string.messages);
        } else if (id == R.id.nav_commerce_new) {
            startFragmentBackStack(new NewMessageFormFragment());
            title = getString(R.string.new_message);
        } else if (id == R.id.nav_commerce_trash) {
            CommerceFragment commerceFragment = CommerceFragment.newInstance("removed");
            startFragment(commerceFragment, TAG_COMMERCE);
            title = "Trash";

        } else if (id == R.id.nav_commerce_scanQR) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_user_list) {
            startFragment(new UserFragment(), TAG_USER);
            title = getString(R.string.messages);
        } else if (id == R.id.nav_user_location) {
            startFragment(new LocationFragment(), TAG_LOCATION);
            title = "Location";
        }else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else if (id == R.id.nav_exit) {
            finish();
        } else if (id == R.id.nav_user_selectInterests){
            startFragmentBackStack(new PlacesInterestsFragment());
            title = "Select Interests";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast.makeText(this,contents,Toast.LENGTH_LONG).show();
                // Handle successful scan
                UserSQLiteManage userManager = new UserSQLiteManage(getApplicationContext());

                int messageID = Integer.parseInt(contents.replace("USER",""));
                if(userManager.checkIfMessageIsUsedByID(messageID)){
                    Toast.makeText(getApplicationContext(),"codi no valid",Toast.LENGTH_SHORT).show();
                }else{
                    userManager.setMessageAsUsed(messageID);
                    Toast.makeText(getApplicationContext(),"codi correcte",Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    @Override
    public void onFABNewMessageCommerce(String title) {
        fabAction(title);
    }

    private void fabAction (String title) {
        setTitle(title);
        checkItem(R.id.nav_commerce_new);
    }

    @Override
    public void onMessageAdded(String title) {
        setTitle(title);
        checkItem(R.id.nav_commerce_list);
    }

    private void checkItem (int id) {
        if (navigationView != null) {
            navigationView.setCheckedItem(id);
        }
    }

    private void setTitle (String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onEditMessageDetail(String title) {
        setTitle(title);
        checkItem(R.id.nav_commerce_new);
    }
}
