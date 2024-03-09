package com.example.alarmaapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Classes.HTTP;
import Classes.UserData;

public class PantallaDispositivos  extends AppCompatActivity {
    private HTTP http = null;
    private UserData userData = null;
    private String tipoDispositivo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos);
        http = new HTTP();
        userData = new UserData(this.getIntent());
        tipoDispositivo = this.getIntent().getStringExtra("tipoDispositivo");

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
        CargarDispositivos(tipoDispositivo);
        CargarNotificaciones();

    }

    public void Sincronizar()
    {
        CargarNotificaciones();
        CargarDispositivos(tipoDispositivo);
    }

    private void CargarDispositivos(String tipoDispositivo)
    {
        try {
            JSONArray dispositivos = http.ObtenerDispositivos(userData.getJWT());
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout linear = (LinearLayout)findViewById(R.id.listaDispositivos);
            linear.removeAllViews();

            for(int i = 0; i < dispositivos.length(); i++)
            {
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.listardispositivolayout, null, false);
                JSONObject dispositivo = dispositivos.getJSONObject(i);
                Log.d("Dispositivo => ",dispositivo.toString());

                if(tipoDispositivo.equals(dispositivo.getString("tipoDispositivo")))
                {
                    String nombreDispositivo = dispositivo.getString("nombreDispositivo");
                    TextView textViewNombre = (TextView) layout.findViewWithTag("textViewNombre");
                    textViewNombre.setText(nombreDispositivo);
                    String descripcionDispositivo = dispositivo.getString("descripcionDispositivo");
                    String estadoDispositivo = dispositivo.getString("estadoDispositivo");
                    String estadoConexion = dispositivo.getString("estadoConexion");

                    TextView textViewDescripcion = (TextView) layout.findViewWithTag("textViewDescripcion");
                    textViewDescripcion.setText(descripcionDispositivo + " | " + estadoDispositivo + " | " + estadoConexion);
                    MaterialButton iconoDispositivo = (MaterialButton)layout.findViewById(R.id.iconoDispositivo);

                    iconoDispositivo.setOnClickListener((view) -> {
                        try {
                            NavegarPantalla(R.layout.activity_dispositivo, PantallaDispositivo.class, "idDispositivo", dispositivo.getString("idDispositivo"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    layout.setOnClickListener((view) -> {
                        try {
                            NavegarPantalla(R.layout.activity_dispositivo, PantallaDispositivo.class, "idDispositivo", dispositivo.getString("idDispositivo"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    Switch switchDispositivo = (Switch) layout.findViewWithTag("switchDispositivo");

                    switchDispositivo.setTag(dispositivo.getString("idDispositivo").toString());
                    if(estadoConexion.equals("DESCONECTADO"))
                    {
                        switchDispositivo.setChecked(false);
                        iconoDispositivo.setIcon(getDrawable(R.drawable.motion_sensor_idle_fill0_wght300_grad0_opsz24));
                    }
                    else if(estadoDispositivo.equals("APAGADO"))
                    {
                        switchDispositivo.setChecked(false);
                    } else switchDispositivo.setChecked(true);

                    switchDispositivo.setOnClickListener((view) -> {
                        if(estadoConexion.equals("DESCONECTADO"))
                        {
                            switchDispositivo.setChecked(false);
                            Toast.makeText(this, "No se puede encender un dispositivo desconectado de la red.", Toast.LENGTH_SHORT).show();
                        }
                        else if(estadoDispositivo.equals("APAGADO"))
                        {
                            try {
                                http.EncenderDispositivo(dispositivo.getString("nombreDispositivo"),userData.getJWT(),dispositivo.getString("idDispositivo"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else
                        {
                            try {
                                http.ApagarDispositivo(dispositivo.getString("nombreDispositivo"),userData.getJWT(),dispositivo.getString("idDispositivo"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
                linear.addView(layout);

            }
        }catch (Exception e)
        {
            throw new RuntimeException(e);
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
