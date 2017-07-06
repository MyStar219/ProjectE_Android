package com.administrator.projecte;

/**
 * Created by bluekey630 on 5/25/2017.
 */

//src="http://3.bp.blogspot.com/-f-9PcxeC_ro/UxbMJgPCWMI/AAAAAAAACkw/z3QYu8mOdz0/s1600/256.png"


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final String ATTACH_FILE_URL = "http://3.bp.blogspot.com/-f-9PcxeC_ro/UxbMJgPCWMI/AAAAAAAACkw/z3QYu8mOdz0/s1600/256.png";

    private FirebaseAuth mFirebaseAuth;

    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;
    Button fab, attachment;
    Button btnContacts;
    Button btnGroups;
    Button btnCreate;
    ListView listOfMessage;
    EditText input;
    Button notification;

    RelativeLayout popupNotification;
    ImageView backImage;
    ListView invList;
    ArrayList<String> sendName = new ArrayList<>();
    ArrayList<String> groupName = new ArrayList<>();
    ArrayList<String> groupPassword = new ArrayList<>();
    ArrayList<String> accepted = new ArrayList<>();


    TextView messageText, messageTime, messageUser;
    ImageView messageImageView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (UserDetails.chatWith.length() > 0) {
            displayChatMessage();
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    ChatMessage tempMessage = new ChatMessage(null, UserDetails.username, UserDetails.chatWith, LOADING_IMAGE_URL);
                    FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD).child(UserDetails.chatWith).push().setValue(tempMessage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                String key = databaseReference.getKey();
                                StorageReference storageReference =
                                        FirebaseStorage.getInstance()
                                                .getReference(mFirebaseAuth.getCurrentUser().getUid())
                                                .child(key)
                                                .child(uri.getLastPathSegment());
                                if (storageReference != null) {
                                    putImageInStorage(storageReference, uri, key, UserDetails.chatWith);
                                }

                            } else {
                                Log.w(TAG, "Unable to write message to database.", databaseError.toException());
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        signInAnonymously();
        setUI();

        if (UserDetails.chatWith.length() > 0) {
            displayChatMessage();
            fab.setEnabled(true);
            attachment.setEnabled(true);
            input.setEnabled(true);
            //btnContacts.setEnabled(true);
        }
        else {
            Toast.makeText(MainActivity.this, "You have not joined in group.",
                    Toast.LENGTH_LONG).show();
            fab.setEnabled(false);
            attachment.setEnabled(false);
            input.setEnabled(false);
            //btnContacts.setEnabled(false);
        }
    }

    public void setUI() {
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fab = (Button) findViewById(R.id.fab);
        btnContacts = (Button) findViewById(R.id.btn_contacts);
        btnGroups = (Button) findViewById(R.id.btn_groups);
        attachment = (Button) findViewById(R.id.btn_attachment);
        btnCreate = (Button) findViewById(R.id.btn_create);
        listOfMessage = (ListView) findViewById(R.id.list_of_message);
        input = (EditText) findViewById(R.id.input);
        notification = (Button) findViewById(R.id.notification);

        popupNotification = (RelativeLayout) findViewById(R.id.popupNotification);
        backImage = (ImageView) findViewById(R.id.back_image);

        popupNotification.setVisibility(RelativeLayout.GONE);

        notification.setBackgroundResource(R.drawable.notification_off);
        invList = (ListView) findViewById(R.id.inv_list);

        if (UserDetails.recieved) {
            notification.setBackgroundResource(R.drawable.notification_on);
        }
        else {
            notification.setBackgroundResource(R.drawable.notification_off);
        }

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (UserDetails.recieved)
                {
                    notification.setBackgroundResource(R.drawable.notification_off);
                    UserDetails.recieved = false;
                    popupNotification.setVisibility(RelativeLayout.VISIBLE);

                    final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://projecte-65124.firebaseio.com/invite/" + UserDetails.username + ".json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("groups").child(UserDetails.username);

                            if(s.equals("null")) {
                                //reference.child(UserDetails.rev_groupName).setValue(UserDetails.rev_password);
                                Toast.makeText(MainActivity.this, "There is no any invitation.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    Iterator i = obj.keys();

                                    String key = "";

                                    sendName.clear();
                                    groupName.clear();
                                    groupPassword.clear();
                                    accepted.clear();

                                    int count = 0;

                                    while(i.hasNext()){
                                        key = i.next().toString();

                                        JSONObject buf = obj.getJSONObject(key);

                                        Iterator j = buf.keys();
                                        String groupKey = "";
                                        while (j.hasNext()) {
                                            sendName.add(key);
                                            groupKey = j.next().toString();
                                            JSONObject buf1 = buf.getJSONObject(groupKey);
                                            groupName.add(groupKey);
                                            groupPassword.add(buf1.getString("group_password"));
                                            accepted.add(buf1.getString("accepted"));
                                            count++;
                                        }
                                    }

                                    if (count > 0) {
                                        CustomNotificationAdapter adapter = new CustomNotificationAdapter(MainActivity.this, sendName, groupName, groupPassword, accepted);
                                        invList.setAdapter(adapter);
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

                    RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                    rQueue.add(request);
                }
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupNotification.setVisibility(RelativeLayout.GONE);
            }
        });

//        addContact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupNotification.setVisibility(RelativeLayout.GONE);
//                UserDetails.recieved = false;
//                notification.setBackgroundResource(R.drawable.notification_off);
//
//                final ProgressDialog pd = new ProgressDialog(MainActivity.this);
//                pd.setMessage("Loading...");
//                pd.show();
//
//                String url = "https://projecte-65124.firebaseio.com/groups/" + UserDetails.username + ".json";
//
//                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
//                    @Override
//                    public void onResponse(String s) {
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("groups").child(UserDetails.username);
//
//                        if(s.equals("null")) {
//                            reference.child(UserDetails.rev_groupName).setValue(UserDetails.rev_password);
//                            Toast.makeText(MainActivity.this, "registration successful", Toast.LENGTH_LONG).show();
//                        }
//                        else {
//                            try {
//                                JSONObject obj = new JSONObject(s);
//
//                                if (!obj.has(UserDetails.rev_groupName)) {
//                                    reference.child(UserDetails.rev_groupName).setValue(UserDetails.rev_password);
//                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
//                                    Toast.makeText(MainActivity.this, "registration successful", Toast.LENGTH_LONG).show();
//                                } else {
//                                    Toast.makeText(MainActivity.this, "Group already exists", Toast.LENGTH_LONG).show();
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        pd.dismiss();
//
//                        UserDetails.send_name = "";
//                        UserDetails.rev_groupName = "";
//                        UserDetails.rev_password = "";
//                    }
//
//                },new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        System.out.println("" + volleyError );
//                        pd.dismiss();
//                    }
//                });
//
//                RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
//                rQueue.add(request);
//            }
//        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input.getText().toString().length() > 0 && UserDetails.chatWith.length() > 0) {
                    FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD).child(UserDetails.chatWith).push().setValue(new ChatMessage(input.getText().toString(),UserDetails.username, UserDetails.chatWith, null));
                    input.setText("");
                }
            }
        });

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
            }
        });

        btnGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Groups.class));
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("video/*|application/msword|application/x-excel|application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                //video/*|application/msword|application/x-excel|application/vnd.openxmlformats-officedocument.wordprocessingml.document
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewGroup.class));
            }
        });

        listOfMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ChatMessage listItem = (ChatMessage)(listOfMessage.getItemAtPosition(position));
                if (listItem != null) {

                    if (listItem.getImagUrl() != null && listItem.getImagUrl().startsWith("https://firebasestorage.googleapis.com")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage("Do you want download this file?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReferenceFromUrl(listItem.getImagUrl());

                                        storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                            @Override
                                            public void onSuccess(StorageMetadata storageMetadata) {
                                                String CurrentString = storageMetadata.getContentType();
                                                String[] separated = CurrentString.split("/");
                                                String dirName = separated[0]; // this will contain "Fruit"
                                                String fileType = separated[1]; // this will contain " they taste good"
                                                String ContentString = storageMetadata.getContentDisposition();
                                                String[] spliteString  = ContentString.split("\'\'");
                                                String fileName = spliteString[1];
                                                Log.d("PATH", dirName + " to " + fileType);

                                                writeFile(listItem.getImagUrl(), dirName, fileName, fileType);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });
                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                }
            }
        });
    }

    private  void writeFile(String url, String dirName, final String fileName, String fileType) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);


        final String fName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ProjectE/" + fileName + "." + fileType;

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("PATH", fName);
                File file = new File(fName);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/ProjectE");
                try {

                    path.mkdirs();

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void signInAnonymously() {
        //showProgressDialog();
        // [START signin_anonymously]
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(MainActivity.this, "Authentication successed.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
    }



    private void displayChatMessage() {

        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD).child(UserDetails.chatWith)) {

            @Override
            protected void populateView(View v, final ChatMessage model, int position) {
                //get references to the view of list_item.xml

                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                messageImageView = (ImageView) v.findViewById(R.id.messageImageView);

                if (model.getImagUrl() == null) {
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser()+"("+UserDetails.chatWith+")");
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                }
                else {
                    messageUser.setText(model.getMessageUser()+"("+UserDetails.chatWith+")");
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                    String imageUrl = model.getImagUrl();

                    if (imageUrl.startsWith("https://firebasestorage.googleapis.com")) {
                        Glide.with(messageImageView.getContext())
                                .load(ATTACH_FILE_URL)
                                .into(messageImageView);
                    }
                    else {
                        Glide.with(messageImageView.getContext())
                                .load(model.getImagUrl())
                                .into(messageImageView);
                    }

                    messageImageView.setVisibility(ImageView.VISIBLE);
                    messageText.setVisibility(TextView.GONE);
                }
            }
        };

        listOfMessage.setAdapter(adapter);
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key, final String chatWith) {
        storageReference.putFile(uri).addOnCompleteListener(MainActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    ChatMessage friendlyMessage = new ChatMessage(null, UserDetails.username, UserDetails.chatWith, task.getResult().getDownloadUrl().toString());
                    FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD).child(chatWith).child(key).setValue(friendlyMessage);
                } else {
                    Log.w(TAG, "Image upload task was not successful.", task.getException());
                }
                displayChatMessage();
            }
        });
    }
}
