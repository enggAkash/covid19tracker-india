package `in`.engineerakash.covid19india.util

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.ui.home.MainActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    fun createNotification(
        context: Context, channelId: String, title: String, description: String,
        smallIconDrawable: Int, longDesc: String = "",
        priorityForBelowAndroid8: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {

        //get from activity or where ever this notification is being created
        val notificationTapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationTapIntent, 0)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIconDrawable)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setPriority(priorityForBelowAndroid8)
            .setAutoCancel(true)

        if (longDesc.isNotEmpty()) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(description))
        }

        val notificationId = Math.random().toInt()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
            Helper.saveNotificationId(context, notificationId)
        }

    }

    fun createAllNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelNameList =
                context.resources.getStringArray(R.array.notification_channel_name_list)
            val channelDescList =
                context.resources.getStringArray(R.array.notification_channel_desc_list)
            val channelIdList =
                context.resources.getStringArray(R.array.notification_channel_id_list)
            val channelImportanceList =
                context.resources.getIntArray(R.array.notification_channel_importance_list)

            for ((index, s) in channelNameList.withIndex()) {
                createNotificationChannel(
                    context, channelNameList[index], channelDescList[index],
                    channelIdList[index], channelImportanceList[index]
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createNotificationChannel(
        context: Context, channelName: String, channelDesc: String, channelId: String,
        channelImportance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
            notificationChannel.description = channelDesc
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    fun removeNotifications(context: Context) {
        val currentNotificationId = Helper.getNotificationId(context)

        if (currentNotificationId == 0)
            return

        with(NotificationManagerCompat.from(context)) {
            cancel(currentNotificationId)
            Helper.saveNotificationId(context, 0)
        }

    }
}