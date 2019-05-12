package com.example.emper.skopjelife_2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;

import java.util.concurrent.Semaphore;

import static com.example.emper.skopjelife_2.decider_acitvity.isLocationEnabled;
import static com.example.emper.skopjelife_2.decider_acitvity.locationator;

public class internet_and_location_fixer extends AppCompatActivity {

    private static AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_and_location_fixer);
        buildAlertMessageNoGps();
        }




    private void buildAlertMessageNoGps() {
                System.out.println("VO BUILDALERT SUM");
                final AlertDialog.Builder builder = new AlertDialog.Builder(internet_and_location_fixer.this);
                builder.setMessage("Your GPS and/or internet connection seems to be disabled, would you like to fix this?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                if (isLocationEnabled(internet_and_location_fixer.this)==false) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    while (true) {
                                        if (isLocationEnabled(internet_and_location_fixer.this) == true) {
                                            break;
                                        }
                                        System.out.println("Vrtam");
                                    }
                                    System.out.println("FREEDOM");
                                }
                                if (!haveNetworkConnection())
                                {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    while (true) {
                                        if (haveNetworkConnection() == true) {
                                            break;
                                        }
                                        System.out.println("VRTAM USTE EDNAS INTERNET");
                                    }



                                }
                                boolean kadedaodi = isLoggedIn();
                                if (kadedaodi == true) {

                                    Intent i = new Intent(internet_and_location_fixer.this, MainActivity.class);
                                    i.putExtra("ACCESSTOKEN", accessToken);
                                    System.out.println("TESTING1: " + accessToken);
                                    System.out.println("TESTING2: " + accessToken.getToken());
                                    startActivity(i);
                                } else {
                                    System.out.println("NE E LOGIRAN ZNACI VO LOG IN SCREEN ODI");
                                    Intent i = new Intent(internet_and_location_fixer.this, log_in_screen_activity.class);
                                    startActivity(i);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory( Intent.CATEGORY_HOME );
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                            }
                        });
                System.out.println("PRI KRAJ NA BUILDALERT SUM");
                final AlertDialog alert = builder.create();
                alert.show();
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
    public boolean isLoggedIn() {
        accessToken = AccessToken.getCurrentAccessToken();
        System.out.println("ACCES TOKEN VO DECIDER " + accessToken);
        return accessToken != null;
    }


    }


