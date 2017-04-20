package com.android.udl.locationoffers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText et_user, et_pass;
    private SharedPreferences sharedPreferences;
    private String mode;

    private static final int RC_SIGN_IN = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final int USER = 0;
    private static final int COMMERCE = 1;

    private static final String STATE_USER = "user";
    private static final String STATE_PASS = "pass";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    private FirebaseDatabase db;
    private DatabaseReference reference;
    private StorageReference imageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private String placesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            if (checkPlayServices()) {
                configureGoogleSignIn();
                configureGoogleApiClient();
            }

            et_user = (EditText) findViewById(R.id.editText_login_user);
            et_pass = (EditText) findViewById(R.id.editText_login_pass);
            firebaseAuth = FirebaseAuth.getInstance();

            Button btn = (Button) findViewById(R.id.button_login);
            Button btn_reg = (Button) findViewById(R.id.button_register);
            SignInButton btn_glogin = (SignInButton) findViewById(R.id.button_google_login);

            sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);

            if (sharedPreferences.contains("id")
                    && sharedPreferences.contains("user")
                    && sharedPreferences.contains("mode")) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.welcome) + sharedPreferences.getString("user", null),
                        Toast.LENGTH_SHORT).show();

                start();

            }

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!fieldsBlank()) {
                        firebaseAuth.signInWithEmailAndPassword(et_user.getText().toString(),
                                et_pass.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.login_failed),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_input_error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btn_reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
            });

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d("Google sign in", "onAuthStateChanged: Signed in");

                        reference = db.getReference("Users").child(user.getUid());

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mode = dataSnapshot.child("mode").getValue(String.class);
                                    saveToSharedPreferencesAndStart(user.getDisplayName(), mode);
                                } else {
                                    selectMode(dataSnapshot);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if (user.getDisplayName() != null)
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.signed_in_message)+" "+user.getDisplayName(),
                                    Toast.LENGTH_SHORT)
                                    .show();

                    } else {
                        Log.d("Google sign in", "onAuthStateChanged: Signed out");
                    }
                }
            };

            btn_glogin.setOnClickListener(this);

            db = FirebaseDatabase.getInstance();


        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.network_error))
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false).create().show();
        }
    }

    private boolean fieldsBlank () {
        return et_user.getText().toString().equals("") || et_pass.getText().toString().equals("");
    }

    private void configureGoogleSignIn () {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
    }

    private void configureGoogleApiClient () {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_google_login:
                signIn();
                break;
        }
    }

    private void selectMode (final DataSnapshot dataSnapshot) {
        CharSequence[] charSequence = new CharSequence[2];
        charSequence[0] = "User";
        charSequence[1] = "Commerce";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.login_user_mode))
                .setItems(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case USER:
                        Log.d("Mode", "User selected");
                        mode = getString(R.string.user);
                        break;
                    case COMMERCE:
                        Log.d("Mode", "Commerce selected");
                        mode = getString(R.string.commerce);
                        uploadImage();
                        displayPlacePicker();
                        break;
                }
                dataSnapshot.getRef().child("mode").setValue(mode);

            }
        });
        builder.create().show();
    }

    private void uploadImage () {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference =
                storage.getReferenceFromUrl(getString(R.string.STORAGE_URL));
        imageReference =
                storageReference.child(getString(R.string.STORAGE_PATH)+user.getUid()+getString(R.string.STORAGE_FORMAT));

        String url = user.getPhotoUrl().toString();
        if (url != null) {
            Log.d("Firebase Storage:", url);
            new DownloadImageTask().execute(url);
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                return downloadImage(params[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageReference.putBytes(BitmapUtils.bitmapToByteArray(result));
        }
    }


    private Bitmap downloadImage(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int response = connection.getResponseCode();
            Log.d("","The response is: "+response);

            is = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);

            return image;

        } finally {
            if (is != null) is.close();
        }
    }


    private void displayPlacePicker() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult( builder.build( this ), PLACE_PICKER_REQUEST );
        } catch ( GooglePlayServicesRepairableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
        } catch ( GooglePlayServicesNotAvailableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
        }
    }

    private void signIn () {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d("Google sign in", "onActivityResult ok");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if(resultCode == RESULT_OK && data != null){
                placesID = PlacePicker.getPlace(data, this).getId();
                reference.child("place").setValue(placesID);
                saveToSharedPreferencesAndStart(user.getDisplayName(), mode);
            }
        }
    }

    private void handleSignInResult (GoogleSignInResult result) {
        if (result.isSuccess()) {
            Log.d("Google sign in", "result success");
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            Log.d("Google sign in", "result not success");
        }
    }

    private void firebaseAuthWithGoogle (GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Google sign in", "signInWithCredential:onComplete:"+task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.login_error_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void saveToSharedPreferencesAndStart (String name, String mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", name);
        editor.putString("mode", mode);
        editor.apply();

        start();

    }

    private void start () {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Google sign in", "Connection failed");
    }


    private boolean checkPlayServices () {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(result)) {
                googleApi.getErrorDialog(this, result, 1).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_USER, et_user.getText().toString());
        savedInstanceState.putString(STATE_PASS, et_pass.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        et_user.setText(savedInstanceState.getString(STATE_USER));
        et_pass.setText(savedInstanceState.getString(STATE_PASS));
    }
}
