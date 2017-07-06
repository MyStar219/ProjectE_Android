package com.administrator.projecte;

/**
 * Created by bluekey630 on 5/26/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Groups extends AppCompatActivity {
    ListView groupList;
    TextView noGroupsText;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> passwordArray = new ArrayList<>();
    int totalGroups = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupList = (ListView)findViewById(R.id.groupList);
        noGroupsText = (TextView)findViewById(R.id.noGroupsText);

        pd = new ProgressDialog(Groups.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://projecte-65124.firebaseio.com/groups/" + UserDetails.username + ".json";

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

        RequestQueue rQueue = Volley.newRequestQueue(Groups.this);
        rQueue.add(request);

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.bufGrooupName = al.get(position);
                UserDetails.bufGroupPassword = passwordArray.get(position);
                startActivity(new Intent(Groups.this, GroupAuth.class));
            }
        });
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();

            String key = "";
            String password = "";
            while(i.hasNext()){
                key = i.next().toString();
                password = obj.getString(key);
                al.add(key);
                passwordArray.add(password);
                totalGroups++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalGroups <1){
            noGroupsText.setVisibility(View.VISIBLE);
            groupList.setVisibility(View.GONE);
        }
        else{
            noGroupsText.setVisibility(View.GONE);
            groupList.setVisibility(View.VISIBLE);
            groupList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

        pd.dismiss();
    }
}