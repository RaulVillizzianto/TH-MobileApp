package Classes;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;

public class UserData {
    private Intent intent = null;

    public UserData(Intent intent)
    {
        this.intent = intent;

    }
    public String getJWT()
    {
        if(intent != null)
        {
            if(intent.hasExtra("jwt"))
            {
                return intent.getStringExtra("jwt");
            }
        }

        return null;
    }
}
