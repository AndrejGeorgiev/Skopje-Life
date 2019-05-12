package com.example.emper.skopjelife_2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import static com.example.emper.skopjelife_2.decider_acitvity.isLocationEnabled;

public class log_in_screen_activity extends AppCompatActivity {
    public String TAG="log_in_screen_act";
    LoginButton login_button;
    CallbackManager callbackManager;

    @Override
    protected void onPause() {
        Log.d(TAG,"ONPAUSE");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"ONSTART");
        super.onStart();
        boolean isloc=isLocationEnabled(this);
        boolean isconnected=haveNetworkConnection();
        System.out.println("IS LOC E "+isloc+" IS CONNECTED E "+isconnected);
        if (isloc==false || isconnected==false)
        {
            System.out.println("VLEGOV");
            Intent tofixer=new Intent(this,internet_and_location_fixer.class);
            startActivity(tofixer);
        }
        setContentView(R.layout.activity_log_in_screen_activity);
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"ONSTOP");

        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"ONRESUME");

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"ONCREATE");
        super.onCreate(savedInstanceState);
        InitControls();

    }
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("REQUEST CODE "+requestCode+ " RESULT CODE "+resultCode+" INTENT DATA "+data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
       LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("name"));
      AccessToken accessToken = AccessToken.getCurrentAccessToken();
      try {
          System.out.println("ACCESS TOKEN OD LOG IN SCREEN " + accessToken.getToken());
      }
      catch (NullPointerException e)
      {
          System.out.println("TUKA SUM VO EXC");
          AlertDialog alertDialog = new AlertDialog.Builder(log_in_screen_activity.this).create();
          alertDialog.setTitle("Alert");
          alertDialog.setMessage("Alert message to be shown");
          alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                  new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                      }
                  });
          alertDialog.show();
      }
       Intent i=new Intent(this,MainActivity.class);
       i.putExtra("ACCESSTOKEN",accessToken);
       System.out.println(i.getExtras());
       startActivity(i);
    }
    private void InitControls()
    {
        callbackManager = CallbackManager.Factory.create();
        login_button=(LoginButton) findViewById(R.id.login_button_vo_log_in_screen);
    }
    @Override
    public void onBackPressed() {
        System.out.println("BACK PRESSED IN LOG_IN");
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
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
