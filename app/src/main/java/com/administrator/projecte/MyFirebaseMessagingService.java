package com.administrator.projecte;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by bluekey630 on 6/2/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "FROM" + remoteMessage.getFrom());

        //Check if the message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());

        }

        //Check if the message contains notification

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Display the notification
     * @param body
     */

    private void sendNotification(String body) {
        UserDetails.recieved = true;
        String[] separated = body.split("->");
        UserDetails.send_name = separated[0];
        String buf1 = separated[1];
        String[] separated1 = buf1.split(" Password: ");
        UserDetails.rev_password = separated1[1];
        String buf2 = separated1[0];
        String[] separated2 = buf2.split("Group: ");
        UserDetails.rev_groupName = separated2[1];
        //test1->Group: Apple1 Password: aaaaa
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("invite").child(UserDetails.username).child(UserDetails.send_name).child(UserDetails.rev_groupName);
        //reference.child("group_name").setValue(UserDetails.rev_groupName);
        reference.child("group_password").setValue(UserDetails.rev_password);
        String str = reference.child("accepted").getKey().toString();
        //if (str == null){
        reference.child("accepted").setValue("none");
        //}


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/*Request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifiBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ProjectE")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0/* ID of notification */, notifiBuilder.build());

    }
}
