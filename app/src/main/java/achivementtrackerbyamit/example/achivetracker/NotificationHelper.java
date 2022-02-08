package achivementtrackerbyamit.example.achivetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    private  NotificationManager mManger;
    AudioAttributes audioAttributes = new AudioAttributes.Builder() //Audio Attribute
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build();
    //   Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public static final String channel = "channel"; //creating channels
    public static final String channelName = "Channel 1";
    public String name;
    public NotificationHelper(Context base, String goalName) {
        super(base);
        name = goalName; //Storing Goal name in Global String
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Builder Version Check
            createChannels();
        }
        createChannels();
    }

    private void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(channel, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);  //Setting Notification Attributes
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.blue);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel1.setSound(Uri.parse("android.resource://"+getPackageName()+"/raw/reminder"), audioAttributes);
        channel1.setVibrationPattern(new long[]{1000, 1000});

        getManager().createNotificationChannel(channel1); //creates a new notification channel
    }
    public NotificationManager getManager() {
        if(mManger==null) {
            mManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManger;
    }
    public NotificationCompat.Builder getChannelNotification(String title) {
        return new NotificationCompat.Builder(getApplicationContext(), channel)
                .setContentTitle("Reminder") //Setting Notification Attributes
                .setContentText(name) //Goal Name
                .setSmallIcon(R.drawable.main_kogo) //App Logo
                .setVibrate(new long[]{1000, 1000}) //Vibration Pattern
                .setSound(Uri.parse("android.resource://"+getPackageName()+"/raw/reminder" + R.raw.reminder)) //Notification Sound
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE);
    }
}
