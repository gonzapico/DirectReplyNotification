package xyz.gonzapico.directreplynotification;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  // Key for the string that's delivered in the action's intent.
  private static final String KEY_TEXT_REPLY = "key_text_reply";
  private static final int MAX_MESSAGE_ID = 100;
  private static final int MAX_NOTIFICATION_ID = 10000;
  public static String REPLY_ACTION = "www.gonzapico.xyz";
  private int mNotificationId = 1234567890;
  private int mMessageId = 0;
  private PendingIntent replyPendingIntent = null;

  public static CharSequence getReplyMessage(Intent intent) {
    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
    if (remoteInput != null) {
      return remoteInput.getCharSequence(KEY_TEXT_REPLY);
    }
    return null;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    replyPendingIntent = buildIntentToNotification();
    buildInlineReplyNotification();

    findViewById(R.id.btnShowNotification).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        buildInlineReplyNotification();
      }
    });
  }

  private PendingIntent buildIntentToNotification() {
    Random randomGenerator = new Random();

    mMessageId = randomGenerator.nextInt(MAX_MESSAGE_ID);
    mNotificationId = randomGenerator.nextInt(MAX_NOTIFICATION_ID);
    Intent intent;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      // start a
      // (i)  broadcast receiver which runs on the UI thread or
      // (ii) service for a background task to b executed , but for the purpose of this codelab, will be doing a broadcast receiver
      intent = NotificationService.getReplyMessageIntent(this, mNotificationId, mMessageId);
      return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
          PendingIntent.FLAG_UPDATE_CURRENT);
    } else {
      // start your activity
      intent = ReplyActivity.getReplyMessageIntent(this, mNotificationId, mMessageId);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      return PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
  }

  private void buildInlineReplyNotification() {
    // Create an instance of RemoteInput.Builder that you can add to your notification action.
    // This class's constructor accepts a string that the system uses as the key for the text input.
    // Later, your handheld app uses that key to retrieve the text of the input.
    RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(
        getResources().getString(R.string.reply_label)).build();

    // Attach the RemoteInput object to an action using addRemoteInput().
    NotificationCompat.Action compatAction =
        new NotificationCompat.Action.Builder(R.mipmap.ic_reply,
            getResources().getString(R.string.reply), replyPendingIntent).addRemoteInput(
            remoteInput).setAllowGeneratedReplies(true).build();

    // Build the notification and add the action.
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_notification)
            .setContentTitle(
                getResources().getString(R.string.notification_created) + mNotificationId)
            .setContentText(getResources().getString(R.string.type_reply))
            .setShowWhen(true)
            .addAction(compatAction);

    // Issue the notification.
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    notificationManager.notify(mNotificationId, mBuilder.build());
  }
}