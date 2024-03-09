package com.example.alarmaapp;

import android.content.Intent;
import android.content.res.Resources;
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

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

import Classes.HTTP;
import Classes.UserData;

public class PantallaDispositivo extends AppCompatActivity {
    private HTTP http = null;
    private UserData userData = null;
    private String idDispositivo;
    private JSONObject dispositivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivo);
        http = new HTTP();
        userData = new UserData(this.getIntent());

        try {
            idDispositivo = this.getIntent().getStringExtra("idDispositivo");
            CargarDispositivo(idDispositivo);
            CargarNotificaciones();

            MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

            topAppBar.setOnClickListener((view -> {
                try {
                    NavegarPantalla(R.layout.activity_dispositivos, PantallaDispositivos.class, "tipoDispositivo", dispositivo.getString("tipoDispositivo"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void Sincronizar()
    {
        try {
            CargarNotificaciones();
            CargarDispositivo(idDispositivo);
        }
        catch (JSONException e)
        {

        }
    }

    private void CargarNotificaciones()
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
    }
    private void CargarDispositivo(String idDispositivo) throws JSONException {


        dispositivo = http.ObtenerDispositivo(userData.getJWT(),idDispositivo);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        LinearLayout linear = (LinearLayout)findViewById(R.id.propiedadesDispositivo);
        linear.removeAllViews();

        Iterator<String> keys = dispositivo.keys();
        for(int i = 0; i<dispositivo.names().length(); i++){
            String key = dispositivo.names().getString(i);
            String value = dispositivo.get(dispositivo.names().getString(i)).toString();

            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.propiedadesdispositivolayout, null, false);
            TextView textViewKey = (TextView) layout.findViewById(R.id.Key);
            TextView textViewValue = (TextView) layout.findViewById(R.id.Value);

            textViewKey.setText(key);
            textViewValue.setText(value);
            linear.addView(layout);

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
