package com.example.alarmaapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Classes.HTTP;
import Classes.UserData;

public class PantallaNotificaciones  extends AppCompatActivity {
    private HTTP http = null;
    private UserData userData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        http = new HTTP();
        userData = new UserData(this.getIntent());
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnClickListener((view -> {
            NavegarPantalla(R.layout.activity_pantalla_principal, PantallaPrincipal.class);
        }));
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
        CargarNotificaciones();
    }

    public void Sincronizar()
    {
        CargarNotificaciones();
    }
    private void CargarNotificaciones()
    {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        Resources res = getResources();
        JSONArray jArray = http.ObtenerNotificacionesNuevas(userData.getJWT());
        JSONArray notificaciones = http.ObtenerNotificaciones(userData.getJWT());
        LinearLayout layoutNotificaciones = (LinearLayout) findViewById(R.id.layoutNotificaciones);
        layoutNotificaciones.removeAllViews();

        if(jArray != null){
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
        if(notificaciones != null)
        {
            try {
                for(int i = 0; i < notificaciones.length(); i++)
                {
                    JSONObject notificacion = notificaciones.getJSONObject(i);
                    Log.d("TAG", notificacion.toString());
                    LayoutInflater inflater = LayoutInflater.from(this);
                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.carga_notificaciones_layout, null, false);

                    TextView tituloNotificacion = (TextView)layout.findViewWithTag("tituloNotificacion");
                    TextView descripcionNotificacion = (TextView)layout.findViewWithTag("descripcionNotificacion");

                    tituloNotificacion.setText(notificacion.getString("titulo"));
                    String descripcion = notificacion.getString("texto");
                    descripcionNotificacion.setText(descripcion);

                    MaterialButton botonVerNotificacion = (MaterialButton)layout.findViewById(R.id.botonVerNotificacion);
                    botonVerNotificacion.setOnClickListener((view) -> {
                        try {
                            NavegarPantalla(R.layout.activity_detalle_notificacion, PantallaDetalleNotificacion.class,  "id",notificacion.getString("id") );
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    MaterialButton iconoDispositivo = (MaterialButton)layout.findViewById(R.id.iconoDispositivo);
                    iconoDispositivo.setOnClickListener((view) -> {
                        try {
                            NavegarPantalla(R.layout.activity_detalle_notificacion, PantallaDetalleNotificacion.class,  "id",notificacion.getString("id") );
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    layout.setOnClickListener((view) -> {
                        try {
                            NavegarPantalla(R.layout.activity_detalle_notificacion, PantallaDetalleNotificacion.class,  "id",notificacion.getString("id") );
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    if(notificacion.getBoolean("visto") == false)
                    {
                        layout.setBackgroundColor(Color.parseColor("#494444"));
                    }
                    layoutNotificaciones.addView(layout);
                }
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
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
}
