package com.example.alarmaapp;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import Classes.HTTP;

public class MainActivity extends AppCompatActivity {

    private  SharedPreferences sharedPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = this.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String usuario = new String();
            String contrasenia = new String();

            Button botonIngresar = ( Button) findViewById(R.id.botonIngresar);
            TextInputLayout textViewUsuario = ( TextInputLayout) findViewById(R.id.textoUsuario);
            TextInputLayout textViewContrasenia = ( TextInputLayout) findViewById(R.id.textoContrasenia);
            botonIngresar.setOnClickListener((View) -> {
                IniciarSesion(textViewUsuario.getEditText().getText().toString(), textViewContrasenia.getEditText().getText().toString());
            });
            if(sharedPref.contains("Usuario") && sharedPref.contains("Contraseña")) {
                usuario = sharedPref.getString("Usuario", usuario);
                contrasenia = sharedPref.getString("Contraseña", contrasenia);
                IniciarSesion(usuario,contrasenia);
            }
        }
    }

    private void IniciarSesion(String usuario,String contrasenia) {
        try {
            HTTP http = new HTTP();
            String respuesta = http.IniciarSesion(usuario,contrasenia);
            if(respuesta != null)
            {
                JSONObject data = new JSONObject(respuesta);
                if(data.has("jwt"))
                {
                    if (!data.getString("jwt").equals("")) {
                        Log.d("MainActivity", "Iniciando sesión...");
                        GuardarDatosLogin(usuario, contrasenia);
                        Intent i = new Intent(getApplicationContext(), PantallaPrincipal.class);
                        i.putExtra("jwt",data.getString("jwt"));
                        startActivity(i);
                    }
                }
                else Toast.makeText(this, "Usuario/Contraseña incorrectos!", Toast.LENGTH_SHORT).show();
            }
          //  else Toast.makeText(this, "No se puede conectar con el servidor central!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void GuardarDatosLogin(String usuario,String contrasenia)
    {
        if(!sharedPref.contains("Usuario") && !sharedPref.contains("Contraseña"))
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Usuario", usuario);
            editor.putString("Contraseña", contrasenia);
            editor.apply();
        }
    }
}