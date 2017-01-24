# Direct Reply Notification

I'm showing you how to implement the **Android Direct Reply Notification**. The user could **reply to notifications from the notification bar**. You know that this feature has been started in **Android N (API 24)**. But I'll show you how to make **the same functionality to pre-marshmallow devices**.

Adding inline reply actions
-------------
* Create an instance of [RemoteInput.Builder](https://developer.android.com/reference/android/support/v4/app/RemoteInput.Builder.html) to **adding to your notification action**. This class's constructor accepts a string that the system uses as the key for the text input. Later, your handheld app uses that key **to retrieve the text of the input**.
```
String replyLabel = "Reply on the notification";
RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build();
```
* Attach the [RemoteInput](https://developer.android.com/reference/android/support/v4/app/RemoteInput.html) object to an action using ```addRemoteInput()```.
```
NotificationCompat.Action compatAction =
    new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Reply",
        replyPendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build();
```
* **Apply the action to a notification** and issue the notification.
```
// Build the notification and add the action.
NotificationCompat.Builder mBuilder =
    new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Title")
        .setContentText("Content")
        .setShowWhen(true)
        .addAction(compatAction);

// Issue the notification.
NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
notificationManager.notify(mNotificationId, mBuilder.build());
```

Retriving user input from the inline reply
-------------
* Call ```getResultsFromIntent()``` by passing the notification action’s intent as the input parameter. This method returns a ```Bundle``` that contains the text response.
```
Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
if (remoteInput != null) {
  return remoteInput.getCharSequence(KEY_TEXT_REPLY);
}
return null;
```
* Query the bundle **using the result key** (provided to the ```RemoteInput.Builder``` constructor).
```
CharSequence message = MainActivity.getReplyMessage(intent);
int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);

Toast.makeText(context.getApplicationContext(),
    "Message ID: " + messageId + "\nMessage: " + message, Toast.LENGTH_LONG).show();

// update notification
int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1);
updateNotification(context, notifyId);
```
* Build and issue another notification, using the **same notification ID that you provided for the previous notification**.  When working with this new notification, use the context that gets passed to the receiver's ```onReceive()``` method.
```
private void updateNotification(Context context, int notifyId) {
  NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

  NotificationCompat.Builder builder =
      new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
          .setContentText("Message sent!");

  notificationManager.notify(notifyId, builder.build());
}
```

For interactive apps, such as chats, **you can include additional context when handling retrieved text**. For example, these apps could show multiple lines of chat history. When the user responds via RemoteInput, you can **update the reply history** using the ```setRemoteInputHistory()``` method.

The notification must be either updated or cancelled after the app has received remote input. When the user replies to a remote update using Direct Reply, do not cancel the notification. Instead, **update the notification to display the user's reply**. For notifications using ```MessagingStyle```, you should add the reply as the latest message. When using other templates, you can append the user's reply to the remote-input history.

**Note**: we [handle the compatibility](https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html) from Android 4.1 and higher.

>Personally, I don't recommend to use backwards in previous version of Android N. But if your app is based on the notification, you should do it. What do you think? Do you think this new feature of Android is worthy? 

#### Not all notification features are available for a particular version, even though the methods to set them are in the support library class NotificationCompat.Builder. For example, action buttons, which depend on expanded notifications, only appear on Android 4.1 and higher, because expanded notifications themselves are only available on Android 4.1 and higher.
 
----------
### External helpful links:

- **[Android Direct Reply Notification](http://gonzapico.xyz/android-direct-reply-notification)**
- [Notifications (Android API Guide)](https://developer.android.com/guide/topics/ui/notifiers/notifications.html)
- [Improved notifications with Direct reply in Android N](https://segunfamisa.com/posts/notifications-direct-reply-android-nougat)
