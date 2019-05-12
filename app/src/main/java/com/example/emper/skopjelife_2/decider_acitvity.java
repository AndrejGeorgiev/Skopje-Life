package com.example.emper.skopjelife_2;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.LoginStatusCallback;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import org.w3c.dom.Text;
import java.util.concurrent.Semaphore;

public class decider_acitvity extends AppCompatActivity {
    static Semaphore locationator = new Semaphore(0);
    private static AccessToken accessToken;
    private static String TAG="CHECKER";
    static boolean zavresno=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen_activity);
boolean isloc=isLocationEnabled(this);
boolean isconnected=haveNetworkConnection();
System.out.println("IS LOC E "+isloc+" IS CONNECTED E "+isconnected);
if (isloc==false || isconnected==false)
{
    System.out.println("VLEGOV");
    Intent tofixer=new Intent(this,internet_and_location_fixer.class);
    startActivity(tofixer);
}
/*System.out.println("DALI IMA INTERNET "+isconnected);
if (isconnected==false)
{
   // Toast.makeText(this,"Please enable internet connection",Toast.LENGTH_LONG).show();
    //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));


    while (true) {
        if (haveNetworkConnection() == true) {
            break;
        }
        System.out.println("VRTAM USTE EDNAS INTERNET");
    }
}
if (isloc==false) {
    ActivateLocation();
    while (true) {
        if (isLocationEnabled(this) == true) {
           // areYouOk();

            break;
        }
        System.out.println("VRTAM USTE EDNAS LOCATION");
    }

}

        System.out.println("ISLOC "+isloc);*/
else {
    boolean kadedaodi = isLoggedIn();
    if (kadedaodi == true) {

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("ACCESSTOKEN", accessToken);
        System.out.println("TESTING1: " + accessToken);
        System.out.println("TESTING2: " + accessToken.getToken());
        startActivity(i);
    } else {
        System.out.println("NE E LOGIRAN ZNACI VO LOG IN SCREEN ODI");
        Intent i = new Intent(this, log_in_screen_activity.class);
        startActivity(i);
    }

}
    }
    public boolean isLoggedIn() {
        accessToken = AccessToken.getCurrentAccessToken();
        System.out.println("ACCES TOKEN VO DECIDER " + accessToken);
        return accessToken != null;
    }

    private void buildAlertMessageNoGps() {
/*
        System.out.println("VO BUILDALERT SUM");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        System.out.println("LOCATIONATOR IMA VOLKU PERMITI PRED: "+locationator.availablePermits());

        System.out.println("LOCATIONATOR IMA VOLKU PERMITI POSLE: "+locationator.availablePermits());
*/
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"ONSTART");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"ONSTOP");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"ONPAUSE");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"ONDESTROY");
    }

    @Override
  protected void onResume() {
        super.onResume();
        Log.d(TAG,"ONRESUME");
    }

    private void showalert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                locationator.release();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        System.out.println("TREBA DA SE POKAZE DO TUKA SUM");
        alertDialog.show();
    }


    public void ActivateLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}

