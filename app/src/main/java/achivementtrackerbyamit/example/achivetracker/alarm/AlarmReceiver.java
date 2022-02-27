package achivementtrackerbyamit.example.achivetracker.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String GoalName = intent.getExtras().getString("goal"); //Fetching Goal name

        NotificationHelper notificationHelper = new NotificationHelper(context, GoalName); //passing Goal Name in Notificatio Helper
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(GoalName); //passing Goal Name in Notificatio Helper
        notificationHelper.getManager().notify(1, nb.build());

        // we will use vibrator first
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(5000);

        Toast.makeText(context, "Reminder: "+GoalName, Toast.LENGTH_LONG).show();

        //Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //Uri alarmUri = Uri.parse("android.resource://achivementtrackerbyamit.example.achivetracker"+"/raw/reminder" + R.raw.reminder);
        // if (alarmUri == null) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //  }
        // setting default ringtone
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        // play ringtone
        ringtone.play();
    }

}
