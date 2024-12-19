package com.example.mayfu11;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class Menu extends Fragment {

    private Switch notificationSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        createNotificationChannel(); // Create notification channel for Android Oreo and above

        // Load notification switch state from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isNotificationEnabled = sharedPreferences.getBoolean("notificationEnabled", false);
        notificationSwitch.setChecked(isNotificationEnabled);

        // Enable notifications if switch is already active
        if (isNotificationEnabled) {
            scheduleAllMealNotifications(requireContext());
        }

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save switch state to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationEnabled", isChecked);
            editor.apply();

            if (isChecked) {
                // Schedule notifications
                scheduleAllMealNotifications(requireContext());
                Toast.makeText(getContext(), "Meal notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Cancel all notifications
                cancelAllMealNotifications(requireContext());
                Toast.makeText(getContext(), "Meal notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Refresh button action (optional)
        ImageButton refreshButton = view.findViewById(R.id.refresh);
        refreshButton.setOnClickListener(v -> Toast.makeText(getContext(), "Refreshing the menu", Toast.LENGTH_SHORT).show());

        return view;
    }

    // Schedule all meal notifications
    private void scheduleAllMealNotifications(Context context) {
        setMealNotification(context, 6, 0, "Breakfast Reminder", "It's time for breakfast!");
        setMealNotification(context, 11, 0, "Lunch Reminder", "It's time for lunch!");
        setMealNotification(context, 19, 0, "Dinner Reminder", "It's time for dinner!");
    }

    // Cancel all meal notifications
    private void cancelAllMealNotifications(Context context) {
        cancelMealNotification(context, 6, 0);
        cancelMealNotification(context, 11, 0);
        cancelMealNotification(context, 19, 0);
    }

    // Schedule a single notification
    private void setMealNotification(Context context, int hour, int minute, String title, String message) {
        // Check if the device is running Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Request permission for exact alarms if not already granted
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, 1);
                return;
            }
        }

        // Prepare the intent and PendingIntent
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        int requestCode = getNotificationRequestCode(hour, minute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set up the alarm time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the time has already passed for today, set for tomorrow
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    // Cancel a specific notification
    private void cancelMealNotification(Context context, int hour, int minute) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        int requestCode = getNotificationRequestCode(hour, minute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    // Generate a unique request code for each notification based on its time
    private int getNotificationRequestCode(int hour, int minute) {
        return hour * 100 + minute; // Simple encoding of hour and minute
    }

    // Create Notification Channel for Android Oreo and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Meal Notifications";
            String description = "Channel for meal reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("mealChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Handle permission results (required for Android 12 and above)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Exact alarm permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Exact alarm permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
