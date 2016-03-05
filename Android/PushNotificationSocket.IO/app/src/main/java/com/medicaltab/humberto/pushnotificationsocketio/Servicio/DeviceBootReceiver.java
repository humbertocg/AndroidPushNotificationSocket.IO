package com.medicaltab.humberto.pushnotificationsocketio.Servicio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Humberto on 04/03/2016.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent in = new Intent(context,Servicio_Notificacion.class);
            if(!Servicio_Notificacion.isRunning())
                context.startService(in);
        }
    }
}
