package com.administrator.projecte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 6/12/2017.
 */

public class CustomNotificationAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> sendName;
    ArrayList<String> groupName;
    ArrayList<String> groupPassword;
    ArrayList<String> accepted;
   // Button acceptButton;
    LayoutInflater inflater;

    public CustomNotificationAdapter(Context applicationContext, ArrayList<String> sendName, ArrayList<String> groupName, ArrayList<String> groupPassword, ArrayList<String> accepted) {
        this.context = applicationContext;
        this.sendName = sendName;
        this.groupName = groupName;
        this.groupPassword = groupPassword;
        this.accepted =accepted;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return sendName.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int index = i;
        view = inflater.inflate(R.layout.list_item_notification, null);
        TextView send_name = (TextView)view.findViewById(R.id.send_name);
        final Button acceptButton = (Button) view.findViewById(R.id.add_conatact);
        TextView groupNameLabel = (TextView) view.findViewById(R.id.inv_group_name);
        TextView groupPasswordLabel = (TextView) view.findViewById(R.id.inv_password);

        send_name.setText(sendName.get(i) + " added you");
        groupNameLabel.setText("Invitation for " + groupName.get(i));
        groupPasswordLabel.setText("Password: " + groupPassword.get(i));

        if (accepted.get(i).equals("accepted")) {
            acceptButton.setVisibility(Button.GONE);
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://projecte-65124.firebaseio.com/users.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("groups").child(UserDetails.username);
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("invite").child(UserDetails.username);
                        if(s.equals("null")) {
                            Toast.makeText(context, "not found user.", Toast.LENGTH_LONG).show();
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
                                    String str = sendName.get(index);
                                    if (key.equals(str)) {
                                        pswObj = obj.getJSONObject(key);
                                        //String user = pswObj.getString("phone");
                                        String token = pswObj.getString("token");
                                        reference.child(UserDetails.username).child("contacts").child(key).setValue(token);
                                        acceptButton.setVisibility(Button.GONE);
                                        reference1.child(groupName.get(index)).setValue(groupPassword.get(index));
                                        reference2.child(sendName.get(index)).child(groupName.get(index)).child("accepted").setValue("accepted");
                                        flag = true;
                                        break;
                                    }
                                }

                                if (flag == false) {
                                    Toast.makeText(context, "not found user.", Toast.LENGTH_LONG).show();
                                    //startActivity(new Intent(AddNewContact.this, UsersActivity.class));
                                }

                                //Toast.makeText(AddNewContact.this, obj.toString(), Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                       // pd.dismiss();
                    }

                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError );
                       // pd.dismiss();
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(context);
                rQueue.add(request);
            }
        });
        return view;
    }
}
