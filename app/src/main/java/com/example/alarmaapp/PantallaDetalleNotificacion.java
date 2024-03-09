package com.example.alarmaapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import Classes.HTTP;
import Classes.UserData;

public class PantallaDetalleNotificacion extends AppCompatActivity {
    private HTTP http = null;
    private UserData userData = null;
    private String idNotificacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_notificacion);
        http = new HTTP();
        userData = new UserData(this.getIntent());
        idNotificacion = this.getIntent().getStringExtra("id");
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnClickListener((view -> {
            NavegarPantalla(R.layout.activity_notificaciones, PantallaNotificaciones.class);
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
        CargarDetalleNotificacion(idNotificacion);
    }

    public void Sincronizar()
    {
        CargarDetalleNotificacion(idNotificacion);
    }
    private void CargarDetalleNotificacion(String id)
    {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        Resources res = getResources();
        JSONArray jArray = http.ObtenerNotificacionesNuevas(userData.getJWT());

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
        try {
            JSONObject notificacion = http.ObtenerNotificacion(userData.getJWT(),idNotificacion);
            Log.d("TAG", notificacion.toString());
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            LinearLayout linear = (LinearLayout)findViewById(R.id.detalleNotificacion);
            linear.removeAllViews();

            Iterator<String> keys = notificacion.keys();
            for(int i = 0; i<notificacion.names().length(); i++){
                String key = notificacion.names().getString(i);
                String value = notificacion.get(notificacion.names().getString(i)).toString();

                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.propiedadesdispositivolayout, null, false);
                TextView textViewKey = (TextView) layout.findViewById(R.id.Key);
                TextView textViewValue = (TextView) layout.findViewById(R.id.Value);

                textViewKey.setText(key);
                textViewValue.setText(value);
                linear.addView(layout);
            }
            http.MarcarNotificacionComoVista(id);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
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
