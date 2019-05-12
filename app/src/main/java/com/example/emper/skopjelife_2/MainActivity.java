package com.example.emper.skopjelife_2;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Camera;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.WebDialog;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.tasks.OnCompleteListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    LoginButton login_button;
    CallbackManager callbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "MainActivity";
    private static ProfileTracker profileTracker;
    private static AccessTokenTracker accessTokenTracker;
    private static AccessToken currentaccectoken;
    private static LatLng LongsAndLads;
    private GoogleMap mMap;
    private List<String> listaID = new ArrayList<>();
    private List<Marker> lista_markeri=new ArrayList<>();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    //vars
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static String uslov_vreme;
    private static String uslov_den;

    Spinner sp;
    Spinner spinner_right_side;

    String [] data_vo_spinner_right_side={"Today", "Tomorrow"};
    String [] data_vo_spinner= {"After 00:00", "After 01:00", "After 02:00", "After 03:00", "After 04:00", "After 05:00", "After 06:00", "After 07:00", "After 08:00", "After 09:00", "After 10:00",
            "After 11:00","After 12:00","After 13:00","After 14:00","After 15:00","After 16:00","After 17:00","After 18:00","After 19:00","After 20:00","After 21:00","After 22:00","After 23:00"};
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter_right_side;
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        System.out.println("ACCES TOKEN VO MAIN " + accessToken);
        return accessToken != null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.out.println("MAIN ACTIVITY : CURRENT ACCESS TOKEN " + currentaccectoken);
        setContentView(R.layout.activity_main);
        unpack();
        InitControls();
        Log.d(TAG, "ONCREATE");
        //FacebookSdk.sdkInitialize(getApplicationContext());
        isServicesOK();

        //InitControls();
        //loginWithFB();
        getLocationPermission();

        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (newAccessToken == null) {
                    stopTracking();
                    Intent sendtologin = new Intent(MainActivity.this, log_in_screen_activity.class);
                    startActivity(sendtologin);
                }
            }
        };
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
    @Override
    public void onBackPressed() {
       Intent sendtologin_from_back=new Intent(this,log_in_screen_activity.class);
       startActivity(sendtologin_from_back);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ONSTART");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ONSTOP");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ONPAUSE");
    }

    public void onDestroy() {

        super.onDestroy();

        System.out.println("DESTROYING");

    }

    private void loginWithFB() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        System.out.println("LOGIN SUCC\n" + loginResult.getAccessToken().getToken());
                        currentaccectoken = loginResult.getAccessToken();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        System.out.println("LOGIN CANCEL\n");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("ERROR 696969");
                    }
                });
        // LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email"));

    }

    //MOZEBI SE NEPOTREBNI --->
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
       /* Intent i=new Intent(this,log_in_screen_activity.class);
        System.out.println("SE ODLOGIRAVVV");
        startActivity(i);*/
    }


    private void InitControls() {
        callbackManager = CallbackManager.Factory.create();
        login_button = (LoginButton) findViewById(R.id.login_button_vo_main);
    }
//<---- MOZEBI SE NEPOTREBNI

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK:Checking google services");

        int avilable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (avilable == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK:Google play servies is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avilable)) {
            //an error occured but is resolvable
            Log.d(TAG, "isServicesOK:FIXABLE ERROR");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, avilable, ERROR_DIALOG_REQUEST);
            dialog.show();
            return false;
        } else {
            Toast.makeText(this, "YOU CANT MAKE MAP REQUEST", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    //ON CLICKS
    public void graphOnClick(View view) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        System.out.println("JSON OBJECT: " + object);
                        System.out.println("GRAPH RESPONSE: " + response);
                        // Application code
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void GraphPage() {
        System.out.println("IMA NESTO VO LISTAA=====GRAPH PAGE CALLLED");
        if(lista_markeri.size()==0) {
            //listaID.add("611579419233539");
            listaID.add("176391869880710");
            GraphRequestBatch batch = new GraphRequestBatch();
            for (int i = 0; i < listaID.size(); i++) {
                GraphRequest req = GraphRequest.newGraphPathRequest(currentaccectoken,
                        "/" + listaID.get(i) + "/events",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONObject jobj = new JSONObject(response.getRawResponse());
                                    System.out.println("RESPONSOT " + response);
                                    System.out.println("ERROR NA RESPONSOT "+response.getError());
                                    JSONArray data = jobj.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++) {
                                        String desc = data.getJSONObject(i).getString("description");
                                        String attending_count = data.getJSONObject(i).getString("attending_count");
                                        int boja = getMarkerColor(Integer.parseInt(attending_count));
                                        JSONObject indi = data.getJSONObject(i).getJSONObject("place");
                                        String Long = indi.getJSONObject("location").getString("longitude");
                                        String Lat = indi.getJSONObject("location").getString("latitude");
                                        System.out.println("Long: " + Long + " Lat: " + Lat);
                                        MarkerOptions tmp_marker = new MarkerOptions().position(new LatLng(Float.parseFloat(Lat), Float.parseFloat(Long))).icon(BitmapDescriptorFactory.defaultMarker(boja)).title(desc);
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(Float.parseFloat(Lat), Float.parseFloat(Long))).icon(BitmapDescriptorFactory.defaultMarker(boja)).title(desc));
                                        Marker tmp=mMap.addMarker(tmp_marker);

                                        lista_markeri.add(tmp);
                                        /// /KOGA KE SE PRAVI SO SLAJDERI, ZIMAJ GI LONGS I LADS I SVE INFO ZA MARKERITE I CUVAJ GI
                                        //VO LISTA, POSLE KOGA KE SE MENJAT SLAJDERITE SAMO IZMINUVAJ JA LISTATA
                                        //NAMESTO DA ZIMAME INFO OD FACEBOOK STRANITE PAK ODNOSNO SAMO PRVIO PAT NEKA ZIMA OD STRANITE

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,event_times,description,attending_count,category,place,parent_group");
                req.setParameters(parameters);
                batch.add(req);
            }
            batch.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch batch) {
                    System.out.println("BATCH DONE " + batch);
                }
            });
            batch.executeAsync();
        }
        else
        {
            System.out.println("IMA NESTO VO LISTATA_MARKERI");
        }
    }

    //IMPLEMENTIRAJ GO KAKO BATCH :https://developers.facebook.com/docs/android/graph
   /* public void GraphPage() {
        listaID.add("611579419233539");
        listaID.add("176391869880710");
        for (int i = 0; i < listaID.size(); i++) {
            final GraphRequest request = GraphRequest.newGraphPathRequest(currentaccectoken
                    ,
                    "/" + listaID.get(i) + "/events",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONObject jobj = new JSONObject(response.getRawResponse());
                                System.out.println("RESPONSOT " + response);
                                JSONArray data = jobj.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    String desc = data.getJSONObject(i).getString("description");
                                    String attending_count = data.getJSONObject(i).getString("attending_count");
                                    int boja = getMarkerColor(Integer.parseInt(attending_count));
                                    JSONObject indi = data.getJSONObject(i).getJSONObject("place");
                                    String Long = indi.getJSONObject("location").getString("longitude");
                                    String Lat = indi.getJSONObject("location").getString("latitude");
                                    System.out.println("Long: " + Long + " Lat: " + Lat);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(Float.parseFloat(Lat), Float.parseFloat(Long))).icon(BitmapDescriptorFactory.defaultMarker(boja)).title(desc));
                                    //KOGA KE SE PRAVI SO SLAJDERI, ZIMAJ GI LONGS I LADS I SVE INFO ZA MARKERITE I CUVAJ GI
                                    //VO LISTA, POSLE KOGA KE SE MENJAT SLAJDERITE SAMO IZMINUVAJ JA LISTATA
                                    //NAMESTO DA ZIMAME INFO OD FACEBOOK STRANITE PAK ODNOSNO SAMO PRVIO PAT NEKA ZIMA OD STRANITE

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,event_times,description,attending_count,category,place,parent_group");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }*/

    public int getMarkerColor(int attending) {
        if (attending < 10) ;
        return 240;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");


        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                Bundle test=new Bundle();
                test.putString("IME",marker.getTitle());
               mFirebaseAnalytics.logEvent(FirebaseAnalytics.Param.ACHIEVEMENT_ID,test);
                return true;
            }
        });
        //HERE WE SET THE MAP, MAP ZOOM RESTRICTIONS, MAP BOUNDS ///
        LatLng Skopje = new LatLng(41, 21);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(Skopje));
        float minZoomLevel = mMap.getMinZoomLevel();
        LatLngBounds SKOPJE = new LatLngBounds(
                new LatLng(41.994050, 21.366788), new LatLng(42.009513, 21.492331));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.9954809, 21.426928), 13));
        mMap.setLatLngBoundsForCameraTarget(SKOPJE);
        mMap.setMinZoomPreference(14);
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);


        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
        GraphPage();
        sp = (Spinner) findViewById(R.id.list);
        spinner_right_side=(Spinner) findViewById(R.id.list_RIGHT_SIDE);
         adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data_vo_spinner);
         adapter.setDropDownViewResource(R.layout.spin_dropdown);
        adapter_right_side = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data_vo_spinner_right_side);
        adapter_right_side.setDropDownViewResource(R.layout.spin_dropdown);
        sp.setAdapter(adapter);
        spinner_right_side.setAdapter(adapter_right_side);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
System.out.println("SELECTED ITEM VO SPINNER E "+adapterView.getSelectedItem());
uslov_vreme=adapterView.getSelectedItem().toString().split(" ")[1];
//filter_the_map(uslov_vreme,uslov_den);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_right_side.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                uslov_den=adapterView.getSelectedItem().toString();
                System.out.println("USLOV VREME I USLOV DEN "+uslov_vreme+" "+uslov_den);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


       /* sp=(Spinner)findViewById(R.id.Spinner);
        adapt=new ArrayAdapter<>(this,R.layout.activity_main,data_vo_spinner);
        sp.setAdapter(adapt);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/




    }

    private void unpack() {
        Bundle extras = getIntent().getExtras();
        System.out.println("BUNDLOT " + extras);
        currentaccectoken = (AccessToken) extras.get("ACCESSTOKEN");
        System.out.println("TK E " + currentaccectoken.getToken());
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            initMap();
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
    private void getDeviceLocation() {
            Log.d(TAG, "getDeviceLocation: getting the devices current location");
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            try {
                if (mLocationPermissionsGranted) {
                        System.out.println("SPINNING");
                        final Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful() && task.getResult()!=null) {
                                    Log.d(TAG, "onComplete: found location!");
                                    Location currentLocation = (Location) task.getResult();
                                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                                DEFAULT_ZOOM);
                                } else {
                                    Log.d(TAG, "onComplete: current location is null");
                                    Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch(SecurityException e){
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
                }

    }
        private void moveCamera (LatLng latLng,float zoom){
            Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
      /* public void filter_the_map(Time uslov_vreme, Date uslov_den)
        {

            for (Marker mrk : lista_markeri)
            {
                    if (!zadovoluva_uslov) {

                            if (mrk.isVisible()) {
                                mrk.setVisible(false);
                            }

                    }
                    else
                    {
                        if (mrk.isVisible()) {
                            mrk.setVisible(false);
                        }
                    }
            }
        }
*/

    }

