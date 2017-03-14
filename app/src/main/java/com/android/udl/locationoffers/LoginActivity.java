package com.android.udl.locationoffers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText et_user, et_pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_user = (EditText) findViewById(R.id.editText_login_user);
        et_pass = (EditText) findViewById(R.id.editText_login_pass);

        Button btn = (Button) findViewById(R.id.button_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (login(getString(R.string.user))) {
                    startModeActivity(getString(R.string.user));
                } else if (login(getString(R.string.comerce))) {
                    startModeActivity(getString(R.string.comerce));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Username or password invalid!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean login (String s) {
        return et_user.getText().toString().equals(s); //&& et_pass.getText().toString().equals(s);
    }

    private void startModeActivity (String s) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("mode", s);
        startActivity(intent);
        finish();
    }

}
