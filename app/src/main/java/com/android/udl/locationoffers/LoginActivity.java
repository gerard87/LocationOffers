package com.android.udl.locationoffers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.udl.locationoffers.database.CommerceSQLiteHelper;
import com.android.udl.locationoffers.database.DatabaseUtilities;
import com.android.udl.locationoffers.domain.Commerce;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText et_user, et_pass;
    private SharedPreferences sharedPreferences;
    private Commerce commerce;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_user = (EditText) findViewById(R.id.editText_login_user);
        et_pass = (EditText) findViewById(R.id.editText_login_pass);

        Button btn = (Button) findViewById(R.id.button_login);
        Button btn_reg = (Button) findViewById(R.id.button_register);

        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);

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
    }

    private boolean login (String s) {
        return et_user.getText().toString().equals(s); //&& et_pass.getText().toString().equals(s);
    }

    private boolean loginCommerce () {

        CommerceSQLiteHelper csh =
                new CommerceSQLiteHelper(getApplicationContext(), "DBCommerces", null, 1);
        DatabaseUtilities databaseUtilities = new DatabaseUtilities("Commerces", csh);
        List<Commerce> commerces = databaseUtilities.getCommerceDataFromDB();
        for (Commerce commerce: commerces) {
            if (commerce.getName().equals(et_user.getText().toString()) &&
                    commerce.getPassword().equals(et_pass.getText().toString())) {
                this.commerce = commerce;
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

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}
