package com.android.udl.locationoffers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.database.CommercesSQLiteHelper;
import com.android.udl.locationoffers.database.DatabaseQueries;
import com.android.udl.locationoffers.domain.Commerce;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText et_user, et_pass;
    private TextView textView;
    private SharedPreferences sharedPreferences;
    private Commerce commerce;


    // Firebase
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_user = (EditText) findViewById(R.id.editText_login_user);
        et_pass = (EditText) findViewById(R.id.editText_login_pass);
        textView = (TextView) findViewById(R.id.tvuserlog);

        Button btn = (Button) findViewById(R.id.button_login);
        Button btn_reg = (Button) findViewById(R.id.button_register);
        SignInButton btn_glogin = (SignInButton) findViewById(R.id.button_google_login);

        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("id")
                && sharedPreferences.contains("user")
                && sharedPreferences.contains("mode")) {
            Toast.makeText(getApplicationContext(),
                    "Welcome " + sharedPreferences.getString("user", null),
                    Toast.LENGTH_SHORT).show();

            start();

        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (login(getString(R.string.user))) {
                    saveToSharedPreferencesAndStart(1,"", getString(R.string.user));

                } else if (loginCommerce()) {
                    saveToSharedPreferencesAndStart(commerce.getId(),
                            commerce.getName(), getString(R.string.commerce));

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Username or password invalid!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterCommerceActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Google sign in", "onAuthStateChanged: Signed in");
                    textView.setText("Signed in as "+user.getDisplayName());
                    saveToSharedPreferencesAndStart(1,
                            user.getDisplayName(), getString(R.string.commerce));
                } else {
                    Log.d("Google sign in", "onAuthStateChanged: Signed out");
                }
            }
        };



        configureGoogleSignIn();
        configureGoogleApiClient();

        btn_glogin.setOnClickListener(this);



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
                                    "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean login (String s) {
        return et_user.getText().toString().equals(s); //&& et_pass.getText().toString().equals(s);
    }

    private boolean loginCommerce () {

        CommercesSQLiteHelper csh =
                new CommercesSQLiteHelper(getApplicationContext(), "DBCommerces", null, 1);
        DatabaseQueries databaseQueries = new DatabaseQueries("Commerces", csh);

        String name = et_user.getText().toString();
        String password = et_pass.getText().toString();

        if (!name.equals("") && !password.equals("")) {
            List<String> fields = Arrays.asList("name","password");
            List<String> values = Arrays.asList(name, password);
            List<Commerce> commerces = databaseQueries.getCommerceDataByFieldsFromDB(fields, values);
            if (commerces != null && commerces.size() > 0) {
                this.commerce = commerces.get(0);
                return true;
            }
        }
        return false;
    }

    private void saveToSharedPreferencesAndStart (int id, String name, String mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
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
}
