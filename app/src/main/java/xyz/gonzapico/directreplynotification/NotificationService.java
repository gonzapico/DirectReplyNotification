package xyz.gonzapico.directreplynotification;

/**
 * Created by gfernandez on 24/01/17.
 */

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import static xyz.gonzapico.directreplynotification.MainActivity.REPLY_ACTION;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class NotificationService extends BroadcastReceiver {
  private static String KEY_NOTIFICATION_ID = "key_noticiation_id";
  private static String KEY_MESSAGE_ID = "key_message_id";

  public NotificationService() {
  }

  public static Intent getReplyMessageIntent(Context context, int notificationId, int messageId) {
    Intent intent = new Intent(context, NotificationService.class);
    intent.setAction(REPLY_ACTION);
    intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
    intent.putExtra(KEY_MESSAGE_ID, messageId);
    return intent;
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (REPLY_ACTION.equals(intent.getAction())) {
      // do whatever you want with the message. Send to the server or add to the db.
      // for this tutorial, we'll just show it in a toast;
      CharSequence message = MainActivity.getReplyMessage(intent);
      int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);



      // update notification
      int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1);
      updateNotification(context, notifyId);
    }
  }

  private void updateNotification(Context context, int notifyId) {
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_notification)
            .setContentText(context.getString(R.string.message_sent));

    notificationManager.notify(notifyId, builder.build());
  }
}
