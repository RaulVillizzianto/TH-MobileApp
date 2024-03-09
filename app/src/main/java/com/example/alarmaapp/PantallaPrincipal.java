package com.example.alarmaapp;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import Classes.HTTP;
import Classes.UserData;

public class PantallaPrincipal extends AppCompatActivity {

    private HTTP http = null;
    private UserData userData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        http = new HTTP();
        userData = new UserData(this.getIntent());

        try {
            Button botonUsuario = (Button) findViewById(R.id.botonUsuario);
            Button botonAgregarDispositivo = (Button) findViewById(R.id.botonAgregarDispositivo);
            Button botonEliminarDispositivo = (Button) findViewById(R.id.botonEliminarDispositivo);
            MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

            topAppBar.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.syncronize)
                {
                    Sincronizar();
                }
                else if(item.getItemId() == R.id.notifications)
                {
                    NavegarPantalla(R.layout.activity_notificaciones,PantallaNotificaciones.class);
                }
                return true;
            });

            CargarDispositivos();
            CargarInformacionUsuario();
            CargarNotificaciones();
            IniciarServicioNotificaciones();

            botonUsuario.setOnClickListener((view -> {
                NavegarPantalla(R.layout.activity_usuario, PantallaUsuario.class);
            }));

            botonAgregarDispositivo.setOnClickListener((view -> {
                NavegarPantalla(R.layout.activity_agregar_dispositivo, PantallaAgregarDispositivo.class);
            }));

            botonEliminarDispositivo.setOnClickListener((view -> {
                NavegarPantalla(R.layout.activity_eliminar_dispositivo, PantallaEliminarDispositivo.class);
            }));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void Sincronizar()
    {
        try {
            CargarNotificaciones();
            CargarInformacionUsuario();
            CargarDispositivos();
        }
        catch (JSONException e)
        {

        }
    }
    private void IniciarServicioNotificaciones()
    {
        if(!servicioNotificacionesCorriendo())
        {
            Intent serviceIntent = new Intent(this, ServicioNotificaciones.class);
            serviceIntent.putExtra("jwt", userData.getJWT());
            startForegroundService(serviceIntent);
        }
    }

    private void CargarNotificaciones()
    {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        Resources res = getResources();
        JSONArray jArray = http.ObtenerNotificacionesNuevas(userData.getJWT());
        if(jArray != null)
        {
            if(jArray.length() > 0)
            {

                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.notifications_active_fill0_wght400_grad0_opsz24, null);
                topAppBar.getMenu().findItem(R.id.notifications).setIcon(drawable);
            }
            else
            {
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.notifications_24, null);
                topAppBar.getMenu().findItem(R.id.notifications).setIcon(drawable);
            }
        }

    }
    private void CargarInformacionUsuario()
    {
        try {
            EditText textViewBienvenido = findViewById(R.id.textoBienvenido);
            String textoBienvenido = "Bienvenido ";
            JSONObject data = http.ObtenerUsuario(userData.getJWT());
            Log.e("UserData" ,data.toString() );
            if(data.has("usuario"))
            {
                textoBienvenido += data.getString("nombre");
            }
            textViewBienvenido.setText(textoBienvenido);
        }
        catch (Exception e)
        {

        }
    }

    private void CargarDispositivos() throws JSONException {
        JSONArray dispositivos =  http.ObtenerDispositivos(userData.getJWT());
        if(dispositivos != null)
        {
            ArrayList<String> nombresDispositivo = new ArrayList<>();
            LinearLayout tableRowDispositivos = (LinearLayout)findViewById(R.id.layoutDispositivos);
            tableRowDispositivos.removeAllViews();
            for(int i = 0; i < dispositivos.length(); i++)
            {
                LayoutInflater inflater = LayoutInflater.from(this);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.cargadispositivolayout, null, false);
                JSONObject dispositivo = dispositivos.getJSONObject(i);
                Log.d("Dispositivo => ",dispositivo.toString());
                String nombreDispositivo = dispositivo.getString("nombreDispositivo");
                if(!nombresDispositivo.contains(nombreDispositivo))
                {
                    nombresDispositivo.add(nombreDispositivo);
                    MaterialButton button = (MaterialButton) layout.findViewWithTag("iconButton");
                    button.setOnClickListener(view -> {
                        try {
                            NavegarPantalla(R.layout.activity_dispositivos, PantallaDispositivos.class, "tipoDispositivo", dispositivo.getString("tipoDispositivo"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    if(nombreDispositivo.equals("Sensor de Movimiento"))
                    {
                        nombreDispositivo = "Sensores de Movimiento";
                    }
                    button.setText(nombreDispositivo);
                    tableRowDispositivos.addView(layout);
                }
            }
        }

    }
    private void NavegarPantalla(@LayoutRes int layoutRes, Class<?> clase )
    {
        finish();
        Intent i = new Intent(getApplicationContext(),clase);
        i.putExtra("jwt",userData.getJWT());
        startActivity(i);
    }
    private void NavegarPantalla(@LayoutRes int layoutRes, Class<?> clase,String extraParamKey,String extraParamValue )
    {
        finish();
        Intent i = new Intent(getApplicationContext(),clase);
        i.putExtra("jwt",userData.getJWT());
        i.putExtra(extraParamKey,extraParamValue);
        startActivity(i);
    }
    public boolean servicioNotificacionesCorriendo(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(ServicioNotificaciones.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}