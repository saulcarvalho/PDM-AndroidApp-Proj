package ipleiria.pdm.maintenanceapppdm.activities;

import static com.google.android.gms.maps.GoogleMap.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;

/**
 * Classe do tipo atividade que permite criar um mapa de ecrã inteiro para ver as localizações
 * de todos os eventos para um dado utilizador
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Config {
    private boolean noLat = false, noLng = false;

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtTitleMaps);
    }

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Método que prepara o mapa e permite adquirir uma localização
     *
     * @param  googleMap - Mapa do google
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapType(MAP_TYPE_HYBRID);
        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setTrafficEnabled(true);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (Manage.getInstance().getNumAcc() != 0) {
            // Vai buscar todas as localizações de acidentes do utilizador
            System.out.println(Manage.getInstance().getListAcc().get(0).toString());
            for (int i = 0; i < Manage.getInstance().getNumAcc(); i++) {
                double lat = Manage.getInstance().getListAcc().get(i).getLocation().getLatitude();
                double lng = Manage.getInstance().getListAcc().get(i).getLocation().getLongitude();

                LatLng loc = new LatLng(lat, lng);
                MarkerOptions marker = new MarkerOptions()
                        .position(loc)
                        .title("Acidente" + (i + 1))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                googleMap.addMarker(marker);
                builder.include(marker.getPosition());
            }
        } else {
            Toast.makeText(MapsActivity.this, getResources().getString(R.string.txtNoAccLocFound), Toast.LENGTH_SHORT).show();
            noLat = true;
        }

        if (Manage.getInstance().getNumMan() != 0) {
            // Vai buscar todas as localizações de manutenções do utilizador
            for (int i = 0; i < Manage.getInstance().getNumMan(); i++) {
                double lat = Manage.getInstance().getListMan().get(i).getLocation().getLatitude();
                double lng = Manage.getInstance().getListMan().get(i).getLocation().getLongitude();

                LatLng loc = new LatLng(lat, lng);
                MarkerOptions marker = new MarkerOptions()
                        .position(loc)
                        .title("Manutencao" + (i + 1))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(marker);
                builder.include(marker.getPosition());
            }
        } else {
            Toast.makeText(MapsActivity.this, getResources().getString(R.string.txtNoManLocFound), Toast.LENGTH_SHORT).show();
            noLng = true;
        }

        if (!(noLat && noLng)) {
            builder.build();
            // Constroi uma CameraPosition com foco em latLng e anima a camara para essa posicao
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(builder.build().getCenter())        // Centra o mapa na localizacao latLng
                    .zoom(camZoomMainMaps)                      // Define o zoom da camara
                    .bearing(camOrientNorth)                    // Define a orientacao da camara
                    .tilt(camTilt)                              // Define o angulo da camara
                    .build();                                   // Cria a CameraPosition através do construtor
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), camUpdateDurationMainMaps, null);
        }
    }
}