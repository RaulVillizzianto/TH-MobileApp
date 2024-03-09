package com.example.alarmaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class PantallaAgregarDispositivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private HTTP http = null;
    private UserData userData = null;
    private Map<String,String> tipoDispositivos =  new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_dispositivo);
        http = new HTTP();
        userData = new UserData(this.getIntent());

        CargarTipoDispositivos();
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
        Button botonAgregar = (Button) findViewById(R.id.botonAgregar);
        botonAgregar.setOnClickListener((view -> {
            Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
            String nombreDispositivo = spinner.getSelectedItem().toString();

            EditText editTextDescripcion = (EditText)findViewById(R.id.descripcionDispositivo);
            EditText editTextId = (EditText)findViewById(R.id.idDispositivo);

            String descripcion = editTextDescripcion.getText().toString();
            String id =  editTextId.getText().toString();
            if(descripcion.length() != 0)
            {
                http.AgregarDispositivo(nombreDispositivo,descripcion,userData.getJWT(),Integer.toString(spinner.getSelectedItemPosition()),id);
                NavegarPantalla(R.layout.activity_pantalla_principal,PantallaPrincipal.class);
            }
            else Toast.makeText(getBaseContext(), "La descripción no puede estar vacia", Toast.LENGTH_SHORT).show();

        }));
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
        CargarTipoDispositivos();
    }

    private void CargarTipoDispositivos()
    {
        try {
            JSONArray dispositivos = http.ObtenerTipoDispositivos();
            ArrayList<String> arrayDispositivos = new ArrayList<>();

            for(int i = 0; i < dispositivos.length(); i++) {
                tipoDispositivos.putIfAbsent(dispositivos.getJSONObject(i).get("tipo").toString(),dispositivos.getJSONObject(i).get("nombre").toString());
                arrayDispositivos.add(dispositivos.getJSONObject(i).get("nombre").toString());
            }
            Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    arrayDispositivos
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setSelection(0);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
        catch (JSONException exception)
        {

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
