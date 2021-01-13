package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

const val DOWNLOAD_ID_ARG_NAME = "download_id"

fun Context.getDownloadManager(): DownloadManager {
    return getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
}

fun Context.getNotificationManager(): NotificationManager {
    return getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
}

/**
 * Utility extension function for sending app specific notifications.
 * The download ID is provided as the pending intent's request code
 * to allow opening multiple download detail simultaneously.
 */
fun NotificationManager.sendNotification(downloadId: Int, title: String, context: Context) {
    val detailIntent = Intent(context, DetailActivity::class.java)
    detailIntent.putExtra(DOWNLOAD_ID_ARG_NAME, downloadId)

    val pendingDetailIntent = PendingIntent.getActivity(
        context,
        downloadId,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(
        0,
        context.getString(R.string.notification_details_button),
        pendingDetailIntent
    ).build()

    val builder =
        NotificationCompat.Builder(context, context.getString(R.string.default_channel_id))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.notification_description))
            .setAutoCancel(true)
            .setContentIntent(pendingDetailIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(action)

    notify(downloadId, builder.build())
}


fun createDefaultNotificationChannel(appContext: Context) {
    val notificationChannel = NotificationChannel(
        appContext.getString(R.string.default_channel_id),
        appContext.getString(R.string.default_channel_name),
        NotificationManager.IMPORTANCE_HIGH
    )

        .apply {
            setShowBadge(false)
        }

    notificationChannel.enableLights(true)
    notificationChannel.lightColor = appContext.getColor(R.color.colorPrimary)
    notificationChannel.enableVibration(true)
    notificationChannel.description = appContext.getString(R.string.default_channel_description)

    appContext.getNotificationManager().createNotificationChannel(notificationChannel)
}