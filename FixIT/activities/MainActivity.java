package ipleiria.pdm.maintenanceapppdm.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;
import ipleiria.pdm.maintenanceapppdm.classes.User;

/**
 * Classe do tipo atividade onde está o ecrã principal da aplicação
 */
public class MainActivity extends AppCompatActivity implements Config, View.OnTouchListener {
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String language = "";
    private ImageView logo;
    private double mCurrAngle = 0;
    private int rotateCount = 0;

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Manage.getInstance().getLastLang() != null) {
            Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Redireciona para LoginActivity se não existir login efetuado
        FirebaseAuth.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            FirebaseFirestore.getInstance().collection("Utilizadores")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        Manage.getInstance().setUser(user);
                        if (user == null) {
                            startActivity(new Intent(this, LoginActivity.class));
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                        }

                        assert user != null;
                        String username = user.getName();

                        // Vai buscar o header da nav drawer
                        NavigationView nav_view = findViewById(R.id.navView);
                        View header = nav_view.getHeaderView(0);
                        TextView textHeader = header.findViewById(R.id.textViewUser);
                        textHeader.setText(username);

                        ImageView imageHeader = header.findViewById(R.id.imageViewUser);
                        if (user.getUserImage().getImageUri().equals("n") && user.getUserImage().getImageName().equals("n")) {
                            Picasso.get()
                                    .load(R.drawable.nav_user_pic_foreground)
                                    .into(imageHeader);
                        } else {
                            Picasso.get()
                                    .load(user.getUserImage().getImageUri())
                                    .into(imageHeader);
                        }
                    });
            // instância drawer com toggle para abrir e fechar o drawer
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
            // pass o toggle para o drawer listener para ir trocando o estado do botão
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            // faz com que o nav drawer icon apareça sempre na action bar
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

            Manage.getInstance().getUserAccFirestore();
            Manage.getInstance().getUserManFirestore();
            getLangFirestore();

            // botão de mudar modo de cor
            NavigationView nav_view = findViewById(R.id.navView);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch s = (Switch) nav_view.getMenu().findItem(R.id.nav_darkswitch).getActionView();
            s.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

            s.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(buttonView.getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int themeID;

                if (isChecked) {
                    themeID = AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    themeID = AppCompatDelegate.MODE_NIGHT_NO;
                }
                Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
                AppCompatDelegate.setDefaultNightMode(themeID); // Change the theme at runtime
                editor.putInt("themeID", themeID); // Save it to be remembered at next launch
                editor.apply();
                if (Manage.getInstance().getLastLang() != null) {
                    overridePendingTransition(0, 0);
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
        logo = findViewById(R.id.ivMain);
        logo.setOnTouchListener(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtTitleMain);
    }

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    /**
     * Método que permite guardar o estado da instância na gestão Manage
     *
     * @param outState - Estado da instância
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("estado", Manage.getInstance());
    }

    /**
     * Método que permite detetar a seleção do item do menu de navegação lateral
     *
     * @param item - Item do menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método que permite rodar uma view, através de um evento de movimento
     *
     * @param v     - View
     * @param event - Evento de movimento
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        final float xc = (float) logo.getWidth() / 2;
        final float yc = (float) logo.getHeight() / 2;

        final float x = event.getX();
        final float y = event.getY();

        double mPrevAngle;
        final double mCurrAngle = Math.toDegrees(Math.atan2(x - xc, yc - y));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                logo.clearAnimation();
                this.mCurrAngle = mCurrAngle;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mPrevAngle = this.mCurrAngle;
                this.mCurrAngle = mCurrAngle;
                animate(mPrevAngle, this.mCurrAngle);
                break;
            }
            case MotionEvent.ACTION_UP: {
                this.mCurrAngle = 0;
                break;
            }
        }
        return true;
    }

    /**
     * Método que permite animar a view rodada, através do angulo de onde a imagem estava
     * para o angulo atual
     *
     * @param fromDegrees - Ângulo anterior da imagem
     * @param toDegrees   - Novo ângulo anterior da imagem
     */
    private void animate(double fromDegrees, double toDegrees) {
        final RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(0);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        logo.startAnimation(rotate);

        easterEgg();
    }

    /**
     * Método que permite aceder a um 'easter egg'
     */
    public void easterEgg() {
        if (mCurrAngle >= 179 && mCurrAngle >= -179){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 250};  // vibra durante 250ms
            v.vibrate(pattern, -1); // -1 significa que só vibra 1 vez

            rotateCount++;
        }

        if (rotateCount >= 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View alertView = getLayoutInflater().inflate(R.layout.ee_dialog_box, null);

            builder.setView(alertView);
            final AlertDialog alertDialog = builder.show();
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            rotateCount = 0;
        }
    }

    /**
     * Método que verifica se o botão de adicionar acidente foi pressionado
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickButtonAcc(View v) {
        Intent i = new Intent(this, AddEventActivity.class);
        Manage.getInstance().setLastActivity(wasMain);
        Manage.getInstance().setLastButtonClick(wasButtonAddAcc);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /**
     * Método que verifica se o botão de adicionar manutenção foi pressionado
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickButtonMan(View v) {
        Intent i = new Intent(this, AddEventActivity.class);
        Manage.getInstance().setLastActivity(wasMain);
        Manage.getInstance().setLastButtonClick(wasButtonAddMan);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /**
     * Método que verifica se o item de procurar acidente no menu de navegação foi pressionado
     *
     * @param item - Item pressionado
     */
    public void onClickSearchAcc(MenuItem item) {
        if (Manage.getInstance().getNumAcc() <= 0) {
            Snackbar.make(findViewById(R.id.navView), getResources().getString(R.string.failNoAccidents), Snackbar.LENGTH_SHORT).show();
            Manage.getInstance().playSound(MainActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        } else {
            Intent i = new Intent(this, SearchEventActivity.class);
            Manage.getInstance().setLastActivity(wasMain);
            Manage.getInstance().setLastButtonClick(wasButtonSearchAcc);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    /**
     * Método que verifica se o item de procurar manutenção no menu de navegação foi pressionado
     *
     * @param item - Item pressionado
     */
    public void onClickSearchMan(MenuItem item) {
        if (Manage.getInstance().getNumMan() <= 0) {
            Snackbar.make(findViewById(R.id.navView), getResources().getString(R.string.failNoMaintenances), Snackbar.LENGTH_SHORT).show();
            Manage.getInstance().playSound(MainActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        } else {
            Intent i = new Intent(MainActivity.this, SearchEventActivity.class);
            Manage.getInstance().setLastActivity(wasMain);
            Manage.getInstance().setLastButtonClick(wasButtonSearchMan);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    /**
     * Método que verifica se o item de abrir localização de eventos no mapa foi pressionado
     *
     * @param item - Item pressionado
     */
    public void onClickMap(MenuItem item) {
        if (Manage.getInstance().getNumAcc() <= 0 && Manage.getInstance().getNumMan() <= 0) {
            Snackbar.make(findViewById(R.id.navView), getResources().getString(R.string.failNoEvents), Snackbar.LENGTH_SHORT).show();
            Manage.getInstance().playSound(MainActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        } else {
            Intent i = new Intent(this, MapsActivity.class);
            Manage.getInstance().setLastActivity(wasMain);
            Manage.getInstance().setLastButtonClick(wasButtonMapa);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    /**
     * Método que verifica se o item de alterar configurações da conta foi pressionado
     *
     * @param item - Item pressionado
     */
    public void onClickButtonAccountSettings(MenuItem item) {
        Intent i = new Intent(this, AccountSettingsActivity.class);
        Manage.getInstance().setLastActivity(wasMain);
        Manage.getInstance().setLastButtonClick(wasButtonAccountSettings);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /**
     * Método que verifica se o item de fazer logout foi pressionado
     *
     * @param item - Item pressionado
     */
    public void onClickLogout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        Manage.getInstance().setLastActivity(wasMain);
        Manage.getInstance().setLastButtonClick(wasButtonLogout);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        finish();
    }

    /**
     * Método que verifica se o item de seleciionar linguagem foi pressionado
     *
     * @param item - Item pressionado
     */
    public void onClickLanguageSelect(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = getLayoutInflater().inflate(R.layout.language_dialog_box, null);

        builder.setView(alertView);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView closeButton = alertView.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem inglês foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageEN(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(EN); // faz update ao Locale
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToEN), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class)); // faz reset à língua
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // muda a transicao
        Manage.getInstance().setLastLang(EN);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem português foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguagePT(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(PT);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToPT), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(PT);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem espanhol foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageES(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(ES);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToES), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(ES);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem francês foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageFR(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(FR);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToFR), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(FR);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem chinês foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageZH(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(ZH);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToZH), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(ZH);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem árabe foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageAR(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(AR);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToAR), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(AR);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem hindi foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageHI(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(HI);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToHI), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(HI);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem indonésia foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageIN(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(IN);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToIN), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(IN);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que verifica se o botão de selecionar a linguagem russo foi pressionado na respetiva
     * AlertDialog
     *
     * @param v - View onde se encontra o botão
     */
    public void onClickLanguageRU(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
        Manage.getInstance().updateLocaleFirestore(RU);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChangeToRU), Toast.LENGTH_SHORT).show();
        Manage.getInstance().playSound(MainActivity.this, R.raw.bubble_switch_sound, maxVolume, soundVolumeBubble);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Manage.getInstance().setLastLang(RU);
        Manage.getInstance().setLocale(MainActivity.this, Manage.getInstance().getLastLang());
        finish();
    }

    /**
     * Método que permite ler o campo 'language' do utilizador
     * através da respetiva secção da firebase
     */
    public void getLangFirestore() {
        FirebaseFirestore.getInstance().collection("Utilizadores")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    language = documentSnapshot.getString("language");
                    assert language != null;
                    if (!language.equals("") && !language.equals(Manage.getInstance().getLastLang())) {
                        Manage.getInstance().setLocale(MainActivity.this, language);
                        Manage.getInstance().setLastLang(language);
                        Toast.makeText(MainActivity.this, R.string.txtLoadingLanguage, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        overridePendingTransition(0, 0);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
    }
}