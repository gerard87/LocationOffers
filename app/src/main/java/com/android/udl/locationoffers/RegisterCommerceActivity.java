package com.android.udl.locationoffers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.udl.locationoffers.Utils.BitmapUtils;
import com.android.udl.locationoffers.database.CommercesSQLiteHelper;

public class RegisterCommerceActivity extends AppCompatActivity {

    private static final int PERMISSION_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE = 1;

    private ImageView imageView;
    private EditText et_name, et_pass;
    private Bitmap bitmap;
    private Button btn_img, btn_ok;
    private CommercesSQLiteHelper csh;
    private SharedPreferences sharedPreferences;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_commerce);

        csh = new CommercesSQLiteHelper(getApplicationContext(), "DBCommerces", null, 1);

        et_name = (EditText) findViewById(R.id.editText_register_name);
        et_pass = (EditText) findViewById(R.id.editText_register_password);
        btn_img = (Button) findViewById(R.id.button_form_image);
        btn_ok = (Button) findViewById(R.id.button_register_ok);
        imageView = (ImageView) findViewById(R.id.image_form);

        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

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
                saveToDatabase();
            }
        });
    }

    private void saveToDatabase () {
        byte[] image = BitmapUtils.bitmapToByteArray(bitmap);

        if (et_name != null && et_pass != null && image != null
                && !et_name.getText().toString().equals("")
                && !et_pass.toString().equals("")) {
            ContentValues data = new ContentValues();
            data.put("name", et_name.getText().toString());
            data.put("password", et_pass.getText().toString());
            data.put("image", image);

            save(data);


            Toast.makeText(getApplicationContext(), "Registered succesfully", Toast.LENGTH_SHORT).show();

            saveToSharedPreferencesAndStart((int)id, et_name.getText().toString(),
                    getString(R.string.commerce));

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.field_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void save (final ContentValues data) {
        SQLiteDatabase db = csh.getWritableDatabase();
        id = db.insert("Commerces", null, data);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    bitmap = BitmapFactory.decodeFile(filePath);

                    imageView.setImageBitmap(bitmap);

                }
        }
    }

    private void saveToSharedPreferencesAndStart (int id, String name, String mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
        editor.putString("user", name);
        editor.putString("mode", mode);
        editor.apply();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
