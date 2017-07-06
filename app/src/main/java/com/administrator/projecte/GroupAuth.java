package com.administrator.projecte;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by bluekey630 on 5/26/2017.
 */

public class GroupAuth extends AppCompatActivity {
    EditText password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupauth);

        setUI();
    }

    private void setUI() {
        password = (EditText) findViewById(R.id.password);

        login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                if (pass.length() < 1) {
                    password.setError("can't be blank");
                }
                else {
                    if (pass.equals(UserDetails.bufGroupPassword)) {
                        UserDetails.chatWith = UserDetails.bufGrooupName;
                        UserDetails.groupPassword = UserDetails.bufGroupPassword;
                    }
                    else {
                        UserDetails.chatWith = "";
                        UserDetails.groupPassword = "";
                    }

                    startActivity(new Intent(GroupAuth.this, MainActivity.class));
                }
            }
        });
    }


}
