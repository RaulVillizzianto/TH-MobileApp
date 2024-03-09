package com.example.alarmaapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

import Classes.HTTP;
import Classes.UserData;

public class PantallaUsuario  extends AppCompatActivity {
    private HTTP http = null;
    private UserData userData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
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
        Sincronizar();
    }
    private void CargarInformacionUsuario()
    {
        try {
            JSONObject usuario = http.ObtenerUsuario(userData.getJWT());
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            LinearLayout linear = (LinearLayout)findViewById(R.id.propiedadesUsuario);
            linear.removeAllViews();

            Iterator<String> keys = usuario.keys();
            for(int i = 0; i<usuario.names().length(); i++){
                String key = usuario.names().getString(i);
                String value = usuario.get(usuario.names().getString(i)).toString();

                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.propiedadesdispositivolayout, null, false);
                TextView textViewKey = (TextView) layout.findViewById(R.id.Key);
                TextView textViewValue = (TextView) layout.findViewById(R.id.Value);

                textViewKey.setText(key);
                textViewValue.setText(value);
                linear.addView(layout);


            }
        }
        catch (JSONException e) {

        }
    }
    public void Sincronizar()
    {
        CargarNotificaciones();
        CargarInformacionUsuario();
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
