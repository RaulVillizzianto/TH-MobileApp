package com.example.alarmaapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import Classes.HTTP;
import Classes.UserData;

public class ServicioNotificaciones extends Service {
    private HTTP http = null;
    private UserData userData = null;
    private NotificationChannel notificationChannel;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        http = new HTTP();
                        userData = new UserData(intent);

                        while (true) {
                            if(userData.getJWT() == null)
                            {
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                JSONArray notificaciones = http.ObtenerNotificacionesNoEntregadas(userData.getJWT());
                                if(notificaciones != null)
                                {
                                    if(notificaciones.length() > 0)
                                    {
                                        for(int i = 0; i<notificaciones.length();i++)
                                        {

                                            try {
                                                JSONObject notificacion = notificaciones.getJSONObject(i);
                                                Log.d("Notificacion", notificacion.toString());
                                                String titulo = notificacion.getString("titulo");
                                                String descripcion = notificacion.getString("texto");
                                                String idNotificacion = notificacion.getString("id");
                                                generarNotificacion(titulo,descripcion,idNotificacion);
                                                http.MarcarNotificacionComoEntregada(idNotificacion);
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                        }
                                    }
                                }
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        ).start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {

            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
        }
    }
    private void generarNotificacion(String titulo,String descripcion,String idNotificacion) {

        notificationChannel = new NotificationChannel("houseapp.notifications", "HouseApp Notifications", NotificationManager.IMPORTANCE_HIGH);
        notificationManager =getSystemService(NotificationManager.class);
        notificationChannel.setDescription("Channel description");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        notificationChannel.enableVibration(true);
        notificationChannel.setGroup("houseapp.notifications");
        notificationChannel.setShowBadge(true);
        assert notificationManager != null;
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("houseapp.notifications", "grupo1"));
        notificationManager.createNotificationChannel(notificationChannel);
        notificationBuilder = new NotificationCompat.Builder(this, "houseapp.notifications");

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.person_alert_fill0_wght300_grad0_opsz24)
                .setTicker("Eventos")
                .setContentTitle(titulo)
                .setContentIntent(ObtenerPendingIntent(userData.getJWT(), idNotificacion))
                .setContentText(descripcion)
                .setContentInfo("New");
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notificationBuilder.build());

    }

    private PendingIntent ObtenerPendingIntent(String jwt, String id)
    {

        Intent i = new Intent(getApplicationContext(), PantallaDetalleNotificacion.class);
        i.putExtra("jwt",userData.getJWT());
        i.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_IMMUTABLE);

        return pendingIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
