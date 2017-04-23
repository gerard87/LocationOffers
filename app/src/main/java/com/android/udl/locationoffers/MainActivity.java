package com.android.udl.locationoffers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.domain.Message;
import com.android.udl.locationoffers.fragments.ListFragment;
import com.android.udl.locationoffers.fragments.MessageDetailFragment;
import com.android.udl.locationoffers.fragments.NewMessageFormFragment;
import com.android.udl.locationoffers.fragments.PlacesInterestsFragment;
import com.android.udl.locationoffers.services.NotificationService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ListFragment.OnFragmentInteractionListener,
        NewMessageFormFragment.OnFragmentInteractionListener,
        MessageDetailFragment.OnFragmentInteractionListener {


    FirebaseDatabase db;

    private static final String TAG_LIST = "tag_list";

    private static final int MENU_START_SERVICE = 10;
    private static final int MENU_STOP_SERVICE = 20;

    private NavigationView navigationView;

    private SharedPreferences sharedPreferences;

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public static String sPref = null;
    private NetworkReceiver receiver = new NetworkReceiver();
    public static boolean hasConnection = true;

    private boolean doubleBack = false;

    private ImageView iv;

    private boolean removed = false;

    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateConnectedFlags();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        db = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);

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

        iv = (ImageView) navigationView.getHeaderView(0)
                .findViewById(R.id.imageView);

        mode = sharedPreferences.getString("mode", null);
        if (mode.equals(getString(R.string.user))) {
            navigationView.inflateMenu(R.menu.drawer_user);
            navigationView.inflateMenu(R.menu.drawer);

            if(sharedPreferences.getBoolean("serviceEnabled", true)){
                Intent serviceIntent;
                serviceIntent = new Intent(this, NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                startService(serviceIntent);
            }

        } else {
            navigationView.inflateMenu(R.menu.drawer_commerce);
            navigationView.inflateMenu(R.menu.drawer);
            downloadImage();
        }
        setTitle(getString(R.string.messages));

        TextView tv = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.textView);
        tv.setText(sharedPreferences.getString("user", null));

        if (savedInstanceState == null) {

            ListFragment listFragment = ListFragment.newInstance("messages", null);
            startFragment(listFragment, TAG_LIST);

            Message message = getIntent().getParcelableExtra("Message");
            if(message != null){
                MessageDetailFragment messageDetailFragment =
                        MessageDetailFragment.newInstance(message);
                startFragmentBackStack(messageDetailFragment);
            }
        }

    }

    private void downloadImage () {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference =
                storage.getReferenceFromUrl(getString(R.string.STORAGE_URL));
        if (user != null) {
            StorageReference imageReference =
                    storageReference.child(getString(
                            R.string.STORAGE_PATH) + user.getUid() +
                            getString(R.string.STORAGE_FORMAT));
            imageReference.getBytes(1024*1024).addOnSuccessListener(
                    new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            iv.setImageBitmap(BitmapUtils.byteArrayToBitmap(bytes));
                        }
                    });
        }

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);

        Message message = intent.getExtras().getParcelable("Message");
        MessageDetailFragment messageDetailFragment = MessageDetailFragment.newInstance(message);
        startFragmentBackStack(messageDetailFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            ListFragment listFragment =
                    (ListFragment) getSupportFragmentManager().findFragmentByTag(TAG_LIST);
            if(listFragment != null && listFragment.isFabOpened()){
                listFragment.closeFab();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String title = getString(R.string.app_name);

        if (id == R.id.nav_commerce_list) {
            ListFragment listFragment = ListFragment.newInstance("messages", null);
            startFragment(listFragment, TAG_LIST);
            title = getString(R.string.messages);
        } else if (id == R.id.nav_commerce_new) {
            startFragmentBackStack(new NewMessageFormFragment());
            title = getString(R.string.new_message);
        } else if (id == R.id.nav_commerce_trash) {
            ListFragment listFragment = ListFragment.newInstance("removed", null);
            startFragment(listFragment, TAG_LIST);
            title = getString(R.string.trash);

        } else if (id == R.id.nav_commerce_scanQR) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_user_list) {
            ListFragment listFragment = ListFragment.newInstance("messages", null);
            startFragment(listFragment, TAG_LIST);
            title = getString(R.string.messages);
        } else if (id == R.id.nav_user_trash) {
            ListFragment listFragment = ListFragment.newInstance("removed", null);
            startFragment(listFragment, TAG_LIST);
            title = getString(R.string.trash);
        }else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            stopLocationService();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else if (id == R.id.nav_exit) {
            finish();
        } else if (id == R.id.nav_user_selectInterests){
            startFragmentBackStack(new PlacesInterestsFragment());
            title = getString(R.string.select_interests);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void stopLocationService(){
        if(NotificationService.isServiceRunning()){
            Intent serviceIntent;
            serviceIntent = new Intent(this, NotificationService.class);
            serviceIntent.addCategory(NotificationService.TAG);
            this.stopService(serviceIntent);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");

                try{
                    String user = contents.split("::")[0];
                    String messageId = contents.split("::")[1];

                    setMessageAsUsed(user, messageId);
                }catch (ArrayIndexOutOfBoundsException e){
                    Toast.makeText(getApplicationContext(),getString(R.string.MESSAGE_INVALID),Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    public void setMessageAsUsed (final String user, final String messageId) {
        DatabaseReference umsgRef = db.getReference("User messages")
                .child(user).child(messageId);
        umsgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if(message.isUsed()){
                        Toast.makeText(getApplicationContext(),getString(R.string.MESSAGE_ALREADY_USED),Toast.LENGTH_SHORT).show();
                    }else{
                        message.setUsed(true);
                        dataSnapshot.getRef().setValue(message);
                        Toast.makeText(getApplicationContext(),getString(R.string.MESSAGE_NOT_USED),Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public void onRemovedMessage(boolean removed) {
        this.removed = removed;
    }

    @Override
    public boolean onReturnFromRemoved() {
        boolean aux = removed;
        removed = false;
        return aux;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mode.equals(getString(R.string.user))) {
            menu.add(Menu.NONE,MENU_START_SERVICE,Menu.NONE, getString(R.string.start_message_detection));
            menu.add(Menu.NONE,MENU_STOP_SERVICE,Menu.NONE, getString(R.string.stop_message_detection));
        }
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serviceIntent;
        sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (item.getItemId()) {
            case MENU_START_SERVICE:
                editor.putBoolean("serviceEnabled",true);
                editor.apply();

                serviceIntent = new Intent(this, NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                startService(serviceIntent);
                break;

            case MENU_STOP_SERVICE:
                editor.putBoolean("serviceEnabled",false);
                editor.apply();

                serviceIntent = new Intent(this, NotificationService.class);
                serviceIntent.addCategory(NotificationService.TAG);
                stopService(serviceIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (((wifiConnected || mobileConnected) && ANY.equals(sPref)) ||
                    (wifiConnected && WIFI.equals(sPref))) {
                hasConnection = true;
            }
        } else {
            setConnectionsToFalse();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("pref_wifi", "Wi-Fi");

        updateConnectedFlags();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setConnectionsToFalse();
    }

    public void setConnectionsToFalse () {
        wifiConnected = false;
        mobileConnected = false;
        hasConnection = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                hasConnection = true;
            } else if (ANY.equals(sPref) && networkInfo != null) {
                hasConnection = true;
            } else {
                hasConnection = false;
            }
        }
    }
}
