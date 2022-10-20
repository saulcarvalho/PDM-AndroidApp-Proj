package ipleiria.pdm.maintenanceapppdm.activities;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;

/**
 * Classe do tipo atividade que permite adicionar uma localização
 */
public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback, Config {
    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView txtLat, txtLng;
    private double lat, lng;

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        txtLat = findViewById(R.id.textViewDisplayLat);
        txtLng = findViewById(R.id.textViewDisplayLng);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);
    }

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AddLocationActivity.this, AddEventActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Método para parar as atividades quando atividade está parada
     */
    @Override
    public void onPause() {
        super.onPause();
        // Para as atualizacoes de localizacao se a atividade estiver parada
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Método que prepara o mapa e permite adquirir uma localização
     *
     * @param  googleMap - Mapa do google
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(MAP_TYPE_HYBRID);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setTrafficEnabled(true);

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(intervalTime)
                .setFastestInterval(intervalTime)
                .setMaxWaitTime(waitTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permissao de localizacao ja garantida
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission(); // Pede a permissao de localizacao
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                // A ultima localizacao na lista é a mais recente
                Location location = locationList.get(locationList.size() - 1);
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                // Coloca o marcador da localizacao atual no mapa
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (Manage.getInstance().getLastActivity() == wasAddAcc | Manage.getInstance().getLastActivity() == wasAddImageLocAcc) {
                    markerOptions.title(getResources().getString(R.string.txtCurrentAccLoc));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else if (Manage.getInstance().getLastActivity() == wasAddMan | Manage.getInstance().getLastActivity() == wasAddImageLocMan) {
                    markerOptions.title(getResources().getString(R.string.txtCurrentManLoc));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }

                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                // Constroi uma CameraPosition com foco em latLng e anima a camara para essa posicao
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Centra o mapa na localizacao latLng
                        .zoom(camZoomRegularLoc)              // Define o zoom da camara
                        .bearing(camOrientNorth)    // Define a orientacao da camara
                        .tilt(camTilt)              // Define o angulo da camara
                        .build();                   // Cria a CameraPosition através do construtor
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), camUpdateDurationRegularLoc, null);

                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    txtLat.setText(Double.toString(lat));
                    txtLng.setText(Double.toString(lng));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Método que permite verificar a permissão de acesso à localização
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Mostra explicacao?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Pergunta ao utilizador quando a explicacao aparecer
                            ActivityCompat.requestPermissions(AddLocationActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                // Sem explicacao, pede a permissao ao utilizador
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    /**
     * Método que faz o pedido para obter localizações
     *
     * @param requestCode  - código de pedido de permissão
     * @param permissions  - permissões
     * @param grantResults - resultados da permissão
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// Se o pedido for cancelado, os arrays ficam vazios
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permissao garantida - faz a tarefa de localizacao
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                }
            } else {
                // permissao negada
                Toast.makeText(this, getResources().getString(R.string.failLocPermission), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método que verifica se o botão de adicionar localização foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonAddLocation(View view) {
        if (lat != invalidLocation && lng != invalidLocation){
            Manage.getInstance().setLastLatitude(lat);
            Manage.getInstance().setLastLongitude(lng);
            Intent i = new Intent(AddLocationActivity.this, AddEventActivity.class);

            Toast.makeText(AddLocationActivity.this, getResources().getString(R.string.successAddLocation), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(AddLocationActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        } else {
            Toast.makeText(AddLocationActivity.this, getResources().getString(R.string.failAddLocation), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(AddLocationActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
            Manage.getInstance().setLastLatitude(invalidLocation);
            Manage.getInstance().setLastLongitude(invalidLocation);
        }
    }
}