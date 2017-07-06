package com.administrator.projecte;

/**
 * Created by bluekey630 on 5/26/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class NewGroup extends AppCompatActivity {

    EditText groupname, password;
    Button registerButton;
    String group, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgroup);

        groupname = (EditText)findViewById(R.id.groupname);
        password = (EditText)findViewById(R.id.password);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group = groupname.getText().toString();
                pass = password.getText().toString();

                if(group.equals("")){
                    groupname.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else if(!group.matches("[A-Za-z0-9]+")){
                    groupname.setError("only number allowed");
                }
                else if(group.length()<5){
                    groupname.setError("at least 5 characters long");
                }
                else if(pass.length()<5){
                    password.setError("at least 5 characters long");
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(NewGroup.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://projecte-65124.firebaseio.com/groups/" + UserDetails.username + ".json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("groups").child(UserDetails.username);

                            if(s.equals("null")) {
                                reference.child(group).setValue(pass);
                                startActivity(new Intent(NewGroup.this, MainActivity.class));
                                Toast.makeText(NewGroup.this, "registration successful", Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(group)) {
                                        reference.child(group).setValue(pass);
                                        startActivity(new Intent(NewGroup.this, MainActivity.class));
                                        Toast.makeText(NewGroup.this, "registration successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(NewGroup.this, "username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }

                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError );
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(NewGroup.this);
                    rQueue.add(request);
                }
            }
        });
    }
}
