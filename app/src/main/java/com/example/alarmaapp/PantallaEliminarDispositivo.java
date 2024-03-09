package com.example.alarmaapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Classes.HTTP;
import Classes.UserData;

public class PantallaEliminarDispositivo extends AppCompatActivity {
    private HTTP http = null;
    private UserData userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_dispositivo);
        http = new HTTP();
        userData = new UserData(this.getIntent());
        CargarDispositivos();
        CargarNotificaciones();

        View topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setOnClickListener((view -> {
            NavegarPantalla(R.layout.activity_pantalla_principal, PantallaPrincipal.class);
        }));

        MaterialToolbar appBar = findViewById(R.id.topAppBar);
        appBar.setOnMenuItemClickListener(item -> {
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

    public void Sincronizar()
    {
        CargarNotificaciones();
        CargarDispositivos();
    }

    private void CargarDispositivos()
    {
        try {
            JSONArray dispositivos = http.ObtenerDispositivos(userData.getJWT());
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout linear = (LinearLayout)findViewById(R.id.listaDispositivos);
            linear.removeAllViews();
            for(int i = 0; i < dispositivos.length(); i++)
            {
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.eliminarbotonlayout, null, false);
                JSONObject dispositivo = dispositivos.getJSONObject(i);
                Log.d("Dispositivo => ",dispositivo.toString());
                String nombreDispositivo = dispositivo.getString("nombreDispositivo");
                Button botonEliminarDispositivo = (Button) layout.findViewById(R.id.botonEliminarDispositivo);
                TextView textViewNombre = (TextView) layout.findViewById(R.id.textViewNombre);
                String descripcionDispositivo = dispositivo.getString("descripcionDispositivo");
                String estadoConexion = dispositivo.getString("estadoConexion");

                TextView textViewDescripcion = (TextView) layout.findViewById(R.id.textViewDescripcion);
                textViewDescripcion.setText(descripcionDispositivo + " | " + estadoConexion + " | ID: " + dispositivo.getString("idDispositivo"));
                textViewNombre.setText(nombreDispositivo);

                botonEliminarDispositivo.setOnClickListener((view) -> {
                    try {
                        http.EliminarDispositivo(dispositivo.getString("idDispositivo"));
                        NavegarPantalla(R.layout.activity_pantalla_principal,PantallaPrincipal.class);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                });

                linear.addView(layout);
            }
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }


    private void CargarNotificaciones() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        Resources res = getResources();
        JSONArray jArray = http.ObtenerNotificacionesNuevas(userData.getJWT());
        if (jArray != null)
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
}
