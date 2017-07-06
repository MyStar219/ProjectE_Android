package com.administrator.projecte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.administrator.projecte.R.layout.activity_contacts;

// com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by bluekey630 on 5/31/2017.
 */

public class UsersActivity extends AppCompatActivity {

    ListView userList;
    ImageView popupImage;
    LinearLayout user_item;
    Button createButton;
    RelativeLayout popup;
    Button viewGroup;
    Button sendInvitation;
    RelativeLayout groupPopup;
    ListView groupList;
    TextView clickHere;

    ProgressDialog pd;
    int totalUser = 0;
    ArrayList<String> user = new ArrayList<>();
    ArrayList<String> checked = new ArrayList<>();
    ArrayList<String> tokens = new ArrayList<>();
    ArrayList<String> groups = new ArrayList<>();
    ArrayList<String> passwords = new ArrayList<>();
    private static final String TAG = "UsersActivity";
    private static int state = 0;
    private static String selectedUser = "";

    private static String selectedToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_contacts);

        setUI();

        pd = new ProgressDialog(UsersActivity.this);
        pd.setMessage("Loading...");
        pd.show();
        String url = "https://projecte-65124.firebaseio.com/users/" + UserDetails.username + "/contacts.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(UsersActivity.this);
        rQueue.add(request);
    }

    private void setUI() {

        userList = (ListView) findViewById(R.id.contact_list);
        popupImage = (ImageView) findViewById(R.id.popup_back);
        user_item = (LinearLayout)findViewById(R.id.list_user);
        createButton = (Button) findViewById(R.id.btn_create_contact);
        popup = (RelativeLayout) findViewById(R.id.ly_popup);
        popup.setVisibility(RelativeLayout.GONE);

        viewGroup = (Button) findViewById(R.id.btn_view_groups);
        sendInvitation = (Button) findViewById(R.id.btn_send_invitation);

        groupPopup = (RelativeLayout) findViewById(R.id.group_popup);
        groupList = (ListView) findViewById(R.id.group_list);
        groupPopup.setVisibility(RelativeLayout.GONE);
        clickHere = (TextView) findViewById(R.id.click_here);

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsersActivity.this, MainActivity.class));
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = String.valueOf(R.drawable.table_cell_uncheck);
                if (checked.get(position).equals(str)) {
                    checked.set(position, String.valueOf(R.drawable.table_cell_check));
                }
                else {
                    checked.set(position, String.valueOf(R.drawable.table_cell_uncheck));
                }

                selectedUser = user.get(position);
                selectedToken = tokens.get(position);
                userList.invalidateViews();

                popup.setVisibility(RelativeLayout.VISIBLE);
            }
        });

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (state == 0) {

                }
                else if (state == 1) {
                    String text =UserDetails.username + "->" + "Group: " + groups.get(position) + " Password: " + passwords.get(position);
                    try {
                        sendNotification(text, "", selectedToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                groupPopup.setVisibility(RelativeLayout.GONE);
            }
        });



        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UsersActivity.this, AddNewContact.class));
            }
        });

        popupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setVisibility(RelativeLayout.GONE);
            }
        });

        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 0;
                popup.setVisibility(RelativeLayout.GONE);
                groupPopup.setVisibility(RelativeLayout.VISIBLE);

                pd = new ProgressDialog(UsersActivity.this);
                pd.setMessage("Loading...");
                pd.show();
                String url = "https://projecte-65124.firebaseio.com/groups/" + selectedUser + ".json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        doOnSuccessViewGroup(s);
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(UsersActivity.this);
                rQueue.add(request);
            }
        });

        sendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = 1;
                popup.setVisibility(RelativeLayout.GONE);
                groupPopup.setVisibility(RelativeLayout.VISIBLE);

                pd = new ProgressDialog(UsersActivity.this);
                pd.setMessage("Loading...");
                pd.show();
                String url = "https://projecte-65124.firebaseio.com/groups/" + UserDetails.username + ".json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        doOnSuccessInvitation(s);
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(UsersActivity.this);
                rQueue.add(request);
            }
        });

//        inviteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                int selected = 0;
//                for (int i = 0; i < checked.size(); i++) {
//                    String str = String.valueOf(R.drawable.table_cell_check);
//                    if (checked.get(i).equals(str)) {
//                        selected++;
//                    }
//                }
//
//                if (selected == 0) {
//                    Toast.makeText(UsersActivity.this, "Please select users.",
//                            Toast.LENGTH_SHORT).show();
//                }
//                else if (groupName.getText().toString().length()<1) {
//                    groupName.setError("Can't be blank");
//                }
//                else if (groupPassword.getText().toString().length() < 1) {
//                    groupPassword.setError("Can't be blank");
//                }
//                else {
//
//                    for (int i = 0; i < checked.size(); i++) {
//                        String str = String.valueOf(R.drawable.table_cell_check);
//                        if (checked.get(i).equals(str)) {
//                            String token = tokens.get(i);
//                            Log.d(TAG, token);
//                            try {
//                                String msg = "Group Name: " + groupName.getText().toString() + " Password: " + groupPassword.getText().toString();
//                                sendNotification(msg, "fd", token);
//                                startActivity(new Intent(UsersActivity.this, MainActivity.class));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                }
//            }
//        });
    }

    private void doOnSuccessViewGroup(String s) {
        int count = 0;
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();

            String key = "";

            groups.clear();
            passwords.clear();

            while(i.hasNext()){
                key = i.next().toString();
                String password = obj.getString(key);

                groups.add(key);
                passwords.add(password);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(count <1){
            groupPopup.setVisibility(RelativeLayout.GONE);
            groupList.setVisibility(View.GONE);
            Toast.makeText(UsersActivity.this, "This user has no any group.",
                    Toast.LENGTH_LONG).show();
        }
        else{
            groupList.setVisibility(View.VISIBLE);
            groupList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups));
        }
        pd.dismiss();
    }

    private void doOnSuccessInvitation(String s) {
        int count = 0;
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();

            String key = "";

            groups.clear();
            passwords.clear();

            while(i.hasNext()){
                key = i.next().toString();
                String password = obj.getString(key);

                groups.add(key);
                passwords.add(password);
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(count <1){
            groupPopup.setVisibility(RelativeLayout.GONE);
            groupList.setVisibility(View.GONE);
            Toast.makeText(UsersActivity.this, "This user has no any group.",
                    Toast.LENGTH_LONG).show();
        }
        else{
            groupList.setVisibility(View.VISIBLE);
            groupList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groups));
        }
        pd.dismiss();
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();

            String key = "";
            JSONObject pswObj = null;
            while(i.hasNext()){
                key = i.next().toString();
                //pswObj = obj.getJSONObject(key);
                String token = obj.getString(key);

                if (UserDetails.username.equals(key)) {
                    continue;
                }
                user.add(key);
                checked.add(String.valueOf(R.drawable.table_cell_uncheck));
                tokens.add(token);
                totalUser++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUser <1){
            userList.setVisibility(View.GONE);
            Toast.makeText(UsersActivity.this, "You have no any contact.",
                    Toast.LENGTH_LONG).show();
        }
        else{
            userList.setVisibility(View.VISIBLE);

            CustomAdapter adapter = new CustomAdapter(getApplicationContext(), user, checked);
            userList.setAdapter(adapter);
        }
        pd.dismiss();
    }

    private void sendNotification(String text, String senderId, String receiverId) throws JSONException {
        String url = "https://fcm.googleapis.com/fcm/send";
        final String API_KEY = "AIzaSyBuYM21TMOAmp7knB5MOXWBeFzT8q0n1eQ";
        String token = receiverId;

        final JSONObject json = new JSONObject();
        JSONObject notification = new JSONObject();
        notification.put("title", "Hi");
        notification.put("body", text);
        json.put("to",token);
        json.put("notification", notification);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(UsersActivity.this, "Sent Invitation!.",
                        Toast.LENGTH_LONG).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("" + error);
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", "key="+API_KEY);
                return headers;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(UsersActivity.this);
        rQueue.add(request);
    }

}
