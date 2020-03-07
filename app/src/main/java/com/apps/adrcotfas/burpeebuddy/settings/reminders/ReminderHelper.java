package com.apps.adrcotfas.burpeebuddy.settings.reminders;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.apps.adrcotfas.burpeebuddy.R;
import com.apps.adrcotfas.burpeebuddy.common.BuddyApplication;
import com.apps.adrcotfas.burpeebuddy.common.utilities.StringUtils;
import com.apps.adrcotfas.burpeebuddy.settings.SettingsHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;


import timber.log.Timber;

import static android.app.AlarmManager.RTC_WAKEUP;

public class ReminderHelper extends ContextWrapper implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "ReminderHelper";

    private static final String BUDDY_REMINDER_NOTIFICATION = "buddy_reminder_notification";
    public final static String REMINDER_ACTION = "buddy.reminder_action";
    public final static int REMINDER_REQUEST_CODE = 11;
    public static final int REMINDER_NOTIFICATION_ID = 99;

    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;

    public ReminderHelper(Context context) {
        super(context);
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            initChannel();
        }
        if (SettingsHelper.isReminderEnabled()) {
            enableBootReceiver();
            scheduleNotification();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initChannel() {
        Timber.tag(TAG).d("initChannel");
        NotificationChannel c = new NotificationChannel(
                BUDDY_REMINDER_NOTIFICATION,
                getString(R.string.reminder_notification_description),
                NotificationManager.IMPORTANCE_DEFAULT);
        c.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(c);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsHelper.ENABLE_REMINDER)) {
            Timber.tag(TAG).d("onSharedPreferenceChanged");
            if (SettingsHelper.isReminderEnabled()) {
                enableBootReceiver();
            } else {
                disableBootReceiver();
                unscheduledNotification();
            }
        } else if (key.equals(SettingsHelper.REMINDER_TIME_VALUE)) {
            scheduleNotification();
        }
    }

    public void enableBootReceiver() {
        Timber.tag(TAG).d("enableBootReceiver");
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableBootReceiver() {
        Timber.tag(TAG).d("disableBootReceiver");
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private AlarmManager getAlarmManager() {
        if (alarmManager  == null) {
            alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        }
        return alarmManager;
    }

    private PendingIntent getReminderPendingIntent() {
        if (pendingIntent == null) {
            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.setAction(REMINDER_ACTION);
            pendingIntent = PendingIntent.getBroadcast(
                    this,
                    REMINDER_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    private void unscheduledNotification() {
        Timber.tag(TAG).d("unscheduledNotification");
        getAlarmManager().cancel(getReminderPendingIntent());
    }

    public void scheduleNotification() {
        if (SettingsHelper.isReminderEnabled()) {

            long calendarMillis = SettingsHelper.getTimeOfReminder();
            Timber.tag(TAG).d("time of reminder: %s", StringUtils.formatDateAndTime(calendarMillis));

            final DateTime now = new DateTime();
            Timber.tag(TAG).d("now: %s", StringUtils.formatDateAndTime(now.getMillis()));
            if (now.isAfter(calendarMillis)) {
                calendarMillis  = new LocalTime(calendarMillis).toDateTimeToday().plusDays(1).getMillis();
            }

            Timber.tag(TAG).d("scheduleNotification at %s", StringUtils.formatDateAndTime(calendarMillis));

            getAlarmManager().setInexactRepeating(
                    RTC_WAKEUP,
                    calendarMillis,
                    AlarmManager.INTERVAL_DAY,
                    getReminderPendingIntent());
        }
    }

    public static void notifyReminder(Context context) {
        final PendingIntent pendingIntent =
                BuddyApplication.getNavigationIntent(context, R.id.mainFragment);

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, BUDDY_REMINDER_NOTIFICATION)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(pendingIntent)
                        .setShowWhen(false)
                        .setOnlyAlertOnce(true)
                        .setContentTitle("Daily workout")
                        .setContentText("Hey buddy, it's time to work out!");
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(REMINDER_NOTIFICATION_ID, builder.build());
    }

    public static void removeNotification(Context context) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(REMINDER_NOTIFICATION_ID);
    }
}
