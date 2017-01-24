package xyz.gonzapico.directreplynotification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import static xyz.gonzapico.directreplynotification.MainActivity.REPLY_ACTION;

/**
 * Created by gfernandez on 24/01/17.
 */

public class ReplyActivity extends AppCompatActivity {

  private static final String KEY_MESSAGE_ID = "key_message_id";
  private static final String KEY_NOTIFY_ID = "key_notify_id";

  private int mMessageId;
  private int mNotifyId;

  private ImageButton mSendButton;
  private EditText mEditReply;
  private RelativeLayout relativeLayout;

  public static Intent getReplyMessageIntent(Context context, int notifyId, int messageId) {
    Intent intent = new Intent(context, ReplyActivity.class);
    intent.setAction(REPLY_ACTION);
    intent.putExtra(KEY_MESSAGE_ID, messageId);
    intent.putExtra(KEY_NOTIFY_ID, notifyId);
    return intent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reply);

    Intent intent = getIntent();

    if (REPLY_ACTION.equals(intent.getAction())) {
      mMessageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);
      mNotifyId = intent.getIntExtra(KEY_NOTIFY_ID, 0);
    }

    mEditReply = (EditText) findViewById(R.id.edit_reply);
    mSendButton = (ImageButton) findViewById(R.id.button_send);

    mSendButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        sendMessage(mNotifyId, mMessageId);
      }
    });

    relativeLayout = (RelativeLayout) findViewById(R.id.activity_reply);
  }

  private void sendMessage(int notifyId, int messageId) {
    // update notification
    updateNotification(notifyId);

    String message = mEditReply.getText().toString().trim();
    // handle send message
    Snackbar.make(relativeLayout, this.getString(R.string.message_snackbar, messageId, message),
        Snackbar.LENGTH_SHORT)
        .setAction(getResources().getString(R.string.confirmation), new View.OnClickListener() {
          @Override public void onClick(View v) {
            finish();
          }
        })
        .show();
  }

  private void updateNotification(int notifyId) {
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(getResources().getString(R.string.message_sent))
            .setSubText(this.getString(R.string.notification, notifyId));

    notificationManager.notify(notifyId, builder.build());
  }
}
