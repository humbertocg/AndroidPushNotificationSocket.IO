package com.medicaltab.humberto.pushnotificationsocketio.Servicio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import com.medicaltab.humberto.pushnotificationsocketio.MainActivity;
import com.medicaltab.humberto.pushnotificationsocketio.R;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Humberto on 04/03/2016.
 */
public class Servicio_Notificacion extends Service {

    private static Servicio_Notificacion instance  = null;
    String Servidor= "http://(ipServidor):(puerto)"; //ejemplo http://192.168.1.100:3000

    public static boolean isRunning() {
        return instance != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Context context;
    Socket socket;

    @Override
    public void onCreate() {
        instance=this;

    }

    @Override
    public void onDestroy() {
        instance = null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        try
        {
            SocketConnect();
        }
        catch(Exception e)
        {

        }

    }

    public void SocketConnect() {
        context= Servicio_Notificacion.this;
        try {
            socket = IO.socket(Servidor);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    //socket.emit("foo", "hi");
                    //socket.disconnect();
                }

            }).on("Notificacion", new Emitter.Listener() {

                @Override
                public void call(final Object... args) {
                    //JSONArray obj = (JSONArray) args[0];
                    lanzarNotificacion(args[0].toString());
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                }

            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    void lanzarNotificacion(String mensaje){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notManager = (NotificationManager) getSystemService(ns);

        Context contexto = Servicio_Notificacion.this;

        Vibrator v;
        Intent notificationIntent = new Intent(contexto, MainActivity.class);
        notificationIntent.putExtra("Mensaje", mensaje);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);//| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contIntent;
        String Notificacion_texto = "";
        Notificacion_texto = mensaje;

        contIntent = PendingIntent.getActivity(contexto, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT < 11) {

            Notification noti = new Notification.Builder(contexto)
                    .setContentTitle("Notificacion")
                    .setContentText(Notificacion_texto)
                    .setSmallIcon(R.mipmap.ic_launcher)
                            //.setLargeIcon(aBitmap)
                    .build();
            notManager.notify(0, noti);

            v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(700);
        }

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            Uri soundUri;
            Notification.Builder builder;
            Notification.BigTextStyle n;

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK); //| Intent.FLAG_ACTIVITY_SINGLE_TOP
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder = new Notification.Builder(contexto)
                    .setContentTitle("Notificacion")
                    .setContentText(Notificacion_texto)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(contIntent)
                    .setSound(soundUri)
                    .setAutoCancel(true);
            n = new Notification.BigTextStyle()
                    .setBigContentTitle("Notificacion")
                    .bigText(mensaje);
            //.build();

            builder.setStyle(n);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Intent push = new Intent();
                push.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);//Intent.FLAG_ACTIVITY_NEW_TASK);
                push.setClass(contexto, MainActivity.class);

                PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(contexto, 0,
                        push, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                Notification.Builder builder1 = builder
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setContentText(Notificacion_texto)
                        .setFullScreenIntent(fullScreenPendingIntent, true);
                //builder.build();
            }
            notManager.notify(0, builder.build());

        }

        //}
    }
}