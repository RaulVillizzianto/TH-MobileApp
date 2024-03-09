package Classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTP {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build();
    public static final MediaType JSON = MediaType.get("application/json");

    //private final String URL = "http://186.23.84.51:5000/api/";
    private final String URL = "http://192.168.0.47:5000/api/";

    private final String endpoint_dispositivos = "device";
    private final String endpoint_tipo_dispositivos = "/tipoDispositivos";
    private final String endpoint_user = "user";
    private final String endpoint_notificaciones = endpoint_user + "/"+"notifications";
    public String MarcarNotificacionComoEntregada(String id)
    {
        try
        {
            JSONObject data = new JSONObject();
            data.put("id",id);
            return post(URL + endpoint_notificaciones + "/delivered",data);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    public String MarcarNotificacionComoVista(String id)
    {
        try
        {
            JSONObject data = new JSONObject();
            data.put("id",id);
            return post(URL + endpoint_notificaciones + "/seen",data);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public String IniciarSesion(String usuario,String contrasenia)
    {
        try
        {
            JSONObject data = new JSONObject();
            data.put("usuario",usuario);
            data.put("contrasenia",contrasenia);
            return post(URL + endpoint_user,data);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public String AgregarDispositivo(String nombreDispositivo,String descripcion,String jwt, String tipo,String id)
    {
        try
        {
            JSONObject data = new JSONObject();
            data.put("nombreDispositivo",nombreDispositivo);
            data.put("descripcionDispositivo",descripcion);
            data.put("tipoDispositivo",tipo);
            data.put("comandoDispositivo","AGREGAR_DISPOSITIVO");
            data.put("idDispositivo",id);

            data.put("jwt",jwt);
            return post(URL + endpoint_dispositivos,data);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public String EncenderDispositivo(String nombreDispositivo,String jwt,String id)
    {
        try
        {
            JSONObject data = new JSONObject();
            data.put("nombreDispositivo",nombreDispositivo);
            data.put("comandoDispositivo","ENCENDER_DISPOSITIVO");
            data.put("idDispositivo",id);

            data.put("jwt",jwt);
            return post(URL + endpoint_dispositivos,data);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    public String ApagarDispositivo(String nombreDispositivo,String jwt,String id)
    {
        try
        {
            JSONObject data = new JSONObject();
            data.put("nombreDispositivo",nombreDispositivo);
            data.put("comandoDispositivo","APAGAR_DISPOSITIVO");
            data.put("idDispositivo",id);

            data.put("jwt",jwt);
            return post(URL + endpoint_dispositivos,data);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    public String EliminarDispositivo(String id)
    {
        try
        {
            return delete(URL + endpoint_dispositivos + "/" + id);
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public JSONObject ObtenerUsuario(String jwt)
    {
        try
        {
            return new JSONObject(get(URL + endpoint_user + "/" + jwt));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }


    public JSONArray ObtenerNotificaciones(String jwt)
    {
        try
        {
            return new JSONArray(get(URL + endpoint_notificaciones + "/all/" + jwt));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    public JSONObject ObtenerNotificacion(String jwt, String id)
    {
        try
        {
            return new JSONObject(get(URL + endpoint_notificaciones + "/detail/" + jwt + "/" + id));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public JSONArray ObtenerNotificacionesNuevas(String jwt)
    {
        try
        {
            return new JSONArray(get(URL + endpoint_notificaciones + "/new/" + jwt));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public JSONArray ObtenerNotificacionesNoEntregadas(String jwt)
    {
        try
        {
            return new JSONArray(get(URL + endpoint_notificaciones + "/not_delivered/" + jwt));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    public JSONArray ObtenerDispositivos(String jwt)
    {
        try
        {
            return new JSONArray(get(URL + endpoint_dispositivos + "/" + jwt));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    public JSONObject ObtenerDispositivo(String jwt,String id)
    {
        try
        {
            return new JSONObject(get(URL + endpoint_dispositivos + "/" + jwt + "/" + id));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    public JSONArray ObtenerTipoDispositivos()
    {
        try
        {
            return new JSONArray(get(URL + endpoint_dispositivos + endpoint_tipo_dispositivos));
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }
    private String get(String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection","close")
                .build();
        Log.d("GET",url);
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
        catch (Exception e)
        {
            Log.e("error", e.getMessage());
        }
        return null;
    }

    private String post(String url, JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d(request.toString(),json.toString());
        try (Response response = client.newCall(request).execute()) {

            return response.body().string();
        }
    }
    private String delete(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        Log.d("DELETE =>" ,request.toString());
        try (Response response = client.newCall(request).execute()) {

            return response.body().string();
        }
    }
}