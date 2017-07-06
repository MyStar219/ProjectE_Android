package com.administrator.projecte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.Iterator;

/**
 * Created by bluekey630 on 6/6/2017.
 */

public class AddNewContact extends AppCompatActivity {

    EditText phonenumber;
    Button register;
    TextView clickHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_contact);

        setUI();
    }

    private void setUI() {
        register = (Button) findViewById(R.id.registerButton);
        phonenumber = (EditText) findViewById(R.id.phone_number);
        clickHere = (TextView) findViewById(R.id.register);

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewContact.this, UsersActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = phonenumber.getText().toString();

                if(phone.equals("")){
                    phonenumber.setError("can't be blank");
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(AddNewContact.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://projecte-65124.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");


                            if(s.equals("null")) {
                                Toast.makeText(AddNewContact.this, "not found user.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    Iterator i = obj.keys();
                                    String key = "";
                                    JSONObject pswObj = null;
                                    boolean flag = false;
                                    while(i.hasNext()){
                                        key = i.next().toString();
                                        pswObj = obj.getJSONObject(key);
                                        String number = pswObj.getString("phone");
                                        String token = pswObj.getString("token");
                                        if (number.equals(phone)) {
                                            reference.child(UserDetails.username).child("contacts").child(key).setValue(token);
                                            //startActivity(new Intent(AddNewContact.this, UsersActivity.class));
                                            flag = true;
                                            break;
                                        }
                                    }

                                    if (flag == false) {
                                        Toast.makeText(AddNewContact.this, "not found user.", Toast.LENGTH_LONG).show();
                                        //startActivity(new Intent(AddNewContact.this, UsersActivity.class));
                                    }

                                    //Toast.makeText(AddNewContact.this, obj.toString(), Toast.LENGTH_LONG).show();

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

                    RequestQueue rQueue = Volley.newRequestQueue(AddNewContact.this);
                    rQueue.add(request);
                }
            }
        });
    }
}
