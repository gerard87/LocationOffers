package com.android.udl.locationoffers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.graphics.Bitmap.createScaledBitmap;

public class RegisterActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemSelectedListener{

    private GoogleApiClient mGoogleApiClient;
    private static final int PLACE_PICKER_REQUEST = 2;

    private static final int PERMISSION_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE = 1;

    private static final String STATE_NAME = "name";
    private static final String STATE_MAIL = "mail";
    private static final String STATE_PASS = "pass";
    private static final String STATE_SPINNER = "spinner";
    private static final String STATE_IMAGE = "image";
    private static final String STATE_PLACE = "place";

    private ImageView imageView;
    private EditText et_name, et_pass, et_mail;
    private TextView textViewFormImage;
    private Bitmap bitmap;
    private Button btn_img, btn_ok, btn_placesID;
    private String placesID;
    private byte[] image;
    private Spinner spinner;
    private boolean isCommerce;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase db;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buildGoogleApiClient();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        et_name = (EditText) findViewById(R.id.editText_register_name);
        et_mail = (EditText) findViewById(R.id.editText_register_mail);
        et_pass = (EditText) findViewById(R.id.editText_register_password);
        btn_img = (Button) findViewById(R.id.button_form_image);
        btn_ok = (Button) findViewById(R.id.button_register_ok);
        btn_placesID = (Button) findViewById(R.id.button_selectPlacesID);
        imageView = (ImageView) findViewById(R.id.image_form);
        textViewFormImage = (TextView) findViewById(R.id.textView_form_image);

        spinner = (Spinner) findViewById(R.id.mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.spinner_items, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(et_name.getText().toString())
                        .build();

                if (user != null) {
                    Log.d("Google sign in", "onAuthStateChanged: Signed in");

                    user.updateProfile(profileUpdates);

                    reference = db.getReference("Users/"+user.getUid());

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (isCommerce) {
                                reference.child("mode").setValue(getString(R.string.commerce));
                                reference.child("place").setValue(placesID);
                                uploadImage(user, image);


                            } else {
                                reference.child("mode").setValue(getString(R.string.user));
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        };

        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_EXTERNAL_STORAGE);
                } else {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");
                    startActivityForResult(pickIntent, PICK_IMAGE);
                }

            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMailFirebase();
            }
        });

        btn_placesID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerMailFirebase () {

        if (registerOk()) {
            firebaseAuth.createUserWithEmailAndPassword(et_mail.getText().toString(),
                    et_pass.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), getString(R.string.register_failed),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.register_successful),
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.field_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage (FirebaseUser user, byte[] image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference =
                storage.getReferenceFromUrl(getString(R.string.STORAGE_URL));
        StorageReference imageReference =
                storageReference.child(getString(R.string.STORAGE_PATH)+user.getUid()+getString(R.string.STORAGE_FORMAT));

        imageReference.putBytes(image);

    }

    private boolean registerOk () {
        if (isCommerce){
            return et_name != null && et_mail != null && et_pass != null && image != null
                    && placesID != null && !et_name.getText().toString().equals("")
                    && !et_pass.toString().equals("");
        }

        return et_name != null && et_mail != null && et_pass != null
                && !et_name.getText().toString().equals("")
                && !et_pass.toString().equals("");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    bitmap = BitmapFactory.decodeFile(filePath);
                    if (sizeOfBitmap() > 9999999) reduceSize();

                    imageView.setImageBitmap(bitmap);
                    image = BitmapUtils.bitmapToByteArray(bitmap);

                }break;
            case PLACE_PICKER_REQUEST:
                if(resultCode == RESULT_OK && intent != null){
                    placesID = PlacePicker.getPlace(intent, this).getId();
                }

                break;
        }
    }

    private int sizeOfBitmap () {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    private void reduceSize () {
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        bitmap = createScaledBitmap(bitmap, 512, nh, true);
    }


    //RELATED TO GOOGLE API
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

    private void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .enableAutoManage( this, 0, this )
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ON CONNECTION FAILED","Google Places API connection failed with error code:");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        String item = parent.getItemAtPosition(position).toString();
        if (item.equals(getString(R.string.user))) {
            isCommerce = false;
            btn_placesID.setVisibility(View.INVISIBLE);
            btn_img.setVisibility(View.INVISIBLE);
            textViewFormImage.setVisibility(View.INVISIBLE);
        } else if (item.equals(getString(R.string.commerce))) {
            isCommerce = true;
            btn_placesID.setVisibility(View.VISIBLE);
            btn_img.setVisibility(View.VISIBLE);
            textViewFormImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_NAME, et_name.getText().toString());
        savedInstanceState.putString(STATE_MAIL, et_mail.getText().toString());
        savedInstanceState.putString(STATE_PASS, et_pass.getText().toString());
        savedInstanceState.putInt(STATE_SPINNER, spinner.getSelectedItemPosition());
        savedInstanceState.putString(STATE_PLACE, placesID);
        savedInstanceState.putByteArray(STATE_IMAGE, image);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String s = savedInstanceState.getString(STATE_NAME);
        if (s != null) et_name.setText(s);
        s = savedInstanceState.getString(STATE_MAIL);
        if (s != null) et_mail.setText(s);
        s = savedInstanceState.getString(STATE_PASS);
        if (s != null) et_pass.setText(s);
        spinner.setSelection(savedInstanceState.getInt(STATE_SPINNER, 0));
        s = savedInstanceState.getString(STATE_PLACE);
        if (s != null) placesID = s;

        byte[] b = savedInstanceState.getByteArray(STATE_IMAGE);
        if (b != null) {
            image = b;
            bitmap = BitmapUtils.byteArrayToBitmap(image);
            imageView.setImageBitmap(bitmap);
        }


    }
}
