package achivementtrackerbyamit.example.achivetracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import achivementtrackerbyamit.example.achivetracker.R;

public class MyFireBaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived ( remoteMessage );


        if (remoteMessage.getData ().isEmpty ())
        {
            showNotification ( remoteMessage.getNotification ().getTitle (),remoteMessage.getNotification ().getBody () );

        }
        else {
            showNotification ( remoteMessage.getData () );
        }

    }

    private void  showNotification (Map<String,String> data) {
        String title= data.get ( "title" ).toString ();
        String body = data.get ( "body" ).toString ();


        NotificationManager notificationManager = (NotificationManager)getSystemService ( Context.NOTIFICATION_SERVICE );
        String NOTIFICATION_CHANNEL_ID = "achivementtrackerbyamit.example.services.test";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel ( NOTIFICATION_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription ( "achivementtrackerbyamit" );
            notificationChannel.enableLights ( true );
            notificationChannel.setLightColor ( Color.BLUE );
            notificationChannel.enableLights ( true );
            notificationManager.createNotificationChannel ( notificationChannel );
        }

        Uri uri = RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION );

        NotificationCompat.Builder notifiactionBuilder = new NotificationCompat.Builder ( this,NOTIFICATION_CHANNEL_ID );
        notifiactionBuilder.setAutoCancel ( true )
                .setDefaults ( Notification.DEFAULT_ALL )
                .setWhen ( System.currentTimeMillis () )
                .setSmallIcon ( R.drawable.logo )
                .setContentTitle ( title )
                .setSound ( uri )
                .setContentText ( body )
                .setContentInfo ( "Info" );


        notificationManager.notify ( new Random( ).nextInt (),notifiactionBuilder.build () );
    }

    private  void showNotification (String title,String body){

        NotificationManager notificationManager = (NotificationManager)getSystemService ( Context.NOTIFICATION_SERVICE );
        String NOTIFICATION_CHANNEL_ID = "achivementtrackerbyamit.example.services.test";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel ( NOTIFICATION_CHANNEL_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription ( "achivementtrackerbyamit" );
            notificationChannel.enableLights ( true );
            notificationChannel.setLightColor ( Color.BLUE );
            notificationChannel.enableLights ( true );
            notificationManager.createNotificationChannel ( notificationChannel );
        }

        Uri uri = RingtoneManager.getDefaultUri ( RingtoneManager.TYPE_NOTIFICATION );
        NotificationCompat.Builder notifiactionBuilder = new NotificationCompat.Builder ( this,NOTIFICATION_CHANNEL_ID );
        notifiactionBuilder.setAutoCancel ( true )
                .setDefaults ( Notification.DEFAULT_ALL )
                .setWhen ( System.currentTimeMillis () )
                .setSmallIcon ( R.drawable.logo)
                .setSound ( uri )
                .setContentTitle ( title )
                .setContentText ( body )
                .setContentInfo ( "Info" );

        notificationManager.notify ( new Random ( ).nextInt (),notifiactionBuilder.build () );

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken ( s );


        Log.d ( "TOKENFIREBASE",s );
    }
}
