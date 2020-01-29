package com.iquipsys.tracker.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class SignalSender {
    public static final String MESSAGE_SIGNAL = "com.iquipsys.tracker.phone.service.action.SIGNAL";
    public static final String EXTRA_SIGNAL = "signal";

    public static void subscribe(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, new IntentFilter(MESSAGE_SIGNAL));
    }

    public static void unsubscribe(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(receiver);
    }

    public static void broadcastSignal(Context context, int signal) {
        Intent intent = new Intent(MESSAGE_SIGNAL);
        intent.putExtra(EXTRA_SIGNAL, signal);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }
}