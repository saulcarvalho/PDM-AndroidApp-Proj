package ipleiria.pdm.maintenanceapppdm.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Image;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;

/**
 * Classe do tipo atividade que permite a adição de um evento do tipo acidente ou manutenção
 */
public class AddEventActivity extends AppCompatActivity implements Config {
    private TextView ID;
    private TextView date;
    private EditText description, duration, cost;
    private final Image imageRef = new Image();
    private boolean isDescriptionValid = false, isDurationValid = false, isCostValid = false, isLocationValid = false;
    private static final String lastDescAcc = "descAddAcc", lastDurationAcc = "durationAddAcc", lastCostAcc = "costAddAcc";
    private static final String lastDescMan = "descAddMan", lastDurationMan = "durationAddMan", lastCostMan = "costAddMan";

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Define o titulo do evento
        TextView title = findViewById(R.id.textViewAddEvent);
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            title.setText(R.string.txtTitleAddAcc);
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            title.setText(R.string.txtTitleAddMan);
        }

        ID = findViewById(R.id.textViewDisplayID);
        date = findViewById(R.id.textViewDisplayData);
        description = findViewById(R.id.editTextDesc);
        description.setMovementMethod(new ScrollingMovementMethod());
        duration = findViewById(R.id.editTextTime);
        cost = findViewById(R.id.editTextCost);
        Button addButton = findViewById(R.id.buttonAddEvent);


        addButton.setOnClickListener(new OnClickListener() {
            /**
             * Método que permite verificasr se o botão para validar os
             * dados inseridos para adição de evento foi pressionado
             *
             * @param v - View do botão que foi pressionado
             */
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });

        // Mostra o ID do acidente
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            ID.setText(Integer.toString(Manage.getInstance().getNumAcc() + 1));
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtTitleAddAcc);
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            ID.setText(Integer.toString(Manage.getInstance().getNumMan() + 1));
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtTitleAddMan);
        }

        // Mostra a data atual
        Calendar dataTemp = new GregorianCalendar();
        StringBuilder str = new StringBuilder();
        dataTemp.setTime(Calendar.getInstance().getTime());

        str.append(dataTemp.get(Calendar.DATE)).append("/")
                .append(dataTemp.get(Calendar.MONTH) + 1).append("/")
                .append(dataTemp.get(Calendar.YEAR));
        date.setText(str);

        // guarda o ultimo estado do editText da descricao
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            description.setText(pref.getString(lastDescAcc, ""));
            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    pref.edit().putString(lastDescAcc, s.toString()).apply();
                }
            });
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            description.setText(pref.getString(lastDescMan, ""));
            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    pref.edit().putString(lastDescMan, s.toString()).apply();
                }
            });
        }

        // guarda o ultimo estado do editText da duração
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            duration.setText(pref.getString(lastDurationAcc, ""));
            duration.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    pref.edit().putString(lastDurationAcc, s.toString()).apply();
                }
            });
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            duration.setText(pref.getString(lastDurationMan, ""));
            duration.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    pref.edit().putString(lastDurationMan, s.toString()).apply();
                }
            });
        }

        // guarda o ultimo estado do editText do custo
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            cost.setText(pref.getString(lastCostAcc, ""));
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    pref.edit().putString(lastCostAcc, s.toString()).apply();
                }
            });
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            cost.setText(pref.getString(lastCostMan, ""));
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    pref.edit().putString(lastCostMan, s.toString()).apply();
                }
            });
        }
    }

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AddEventActivity.this, MainActivity.class);
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            Manage.getInstance().setLastActivity(wasAddAcc);
            Manage.getInstance().setLastButtonClick(wasButtonBackMenu);
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            Manage.getInstance().setLastActivity(wasAddMan);
            Manage.getInstance().setLastButtonClick(wasButtonBackMenu);
        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Método que verifica a validação dos dados introduzidos para a criação do evento
     */
    public void SetValidation() {
        // Verifica se existe uma descrição válida
        if (description.getText().toString().isEmpty()) {
            description.setError(getResources().getString(R.string.eEmptyDesc));
            isDescriptionValid = false;
        } else if (description.getText().length() > maxDescChars) {
            description.setError(getResources().getString(R.string.eDescMaxOver)
                    .concat("| " + description.getText().length() + " chars"));
            isDescriptionValid = false;
        } else
            isDescriptionValid = true;

        // Verifica se existe uma duração válida
        if (duration.getText().toString().isEmpty()) {
            duration.setError(getResources().getString(R.string.eEmptyDuration));
            isDurationValid = false;
        } else if (Integer.parseInt(duration.getText().toString()) < minDuration) {
            duration.setError(getResources().getString(R.string.eInvalidDurationMin));
            isDurationValid = false;
        } else if (Integer.parseInt(duration.getText().toString()) > maxDuration) {
            duration.setError(getResources().getString(R.string.eInvalidDurationMax));
            isDurationValid = false;
        } else
            isDurationValid = true;

        // Verifica se existe um custo válido
        if (cost.getText().toString().isEmpty()) {
            cost.setError(getResources().getString(R.string.eEmptyCost));
            isCostValid = false;
        } else if (Integer.parseInt(cost.getText().toString()) < minCusto) {
            cost.setError(getResources().getString(R.string.eInvalidCostMin));
            isCostValid = false;
        } else if (Integer.parseInt(cost.getText().toString()) > maxCusto) {
            cost.setError(getResources().getString(R.string.eInvalidCostMax));
            isCostValid = false;
        } else
            isCostValid = true;

        if (Manage.getInstance().getLastLatitude() != invalidLocation && Manage.getInstance().getLastLongitude() != invalidLocation)
            isLocationValid = true;
        else {
            isLocationValid = false;
        }

        // Verifica se a imagem já foi carregada para a Firebase Storage e restantes validações
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
           String filename = (FirebaseAuth.getInstance().getUid() + "/Acidente");

            FirebaseStorage.getInstance()
                    .getReference()
                    .child(filename.concat(
                            Integer.toString(Manage.getInstance().getNumAcc() + 1)) + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        imageRef.setImageUri(uri.toString());
                        imageRef.setImageName("Acidente" + (Manage.getInstance().getNumAcc() + 1));

                        if (isDescriptionValid && isDurationValid && isCostValid && isLocationValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.successAddAcc), Toast.LENGTH_SHORT).show();

                            int str_ID = Integer.parseInt(ID.getText().toString());
                            String str_date = date.getText().toString();
                            String str_desc = description.getText().toString();
                            int str_duration = Integer.parseInt(duration.getText().toString());
                            int str_cost = Integer.parseInt(cost.getText().toString());
                            String str_imagemUrl = imageRef.getImageUri();
                            String str_imagemName = imageRef.getImageName();
                            double str_lat = Manage.getInstance().getLastLatitude();
                            double str_lng = Manage.getInstance().getLastLongitude();

                            Map<String, Object> acidente = new HashMap<>();
                            acidente.put("ID", str_ID);
                            acidente.put("date", str_date);
                            acidente.put("email", Manage.getInstance().getUser().getEmail());
                            acidente.put("description", str_desc);
                            acidente.put("duration", str_duration);
                            acidente.put("cost", str_cost);
                            acidente.put("imageUri", str_imagemUrl);
                            acidente.put("imageName", str_imagemName);
                            acidente.put("locationLatitude", str_lat);
                            acidente.put("locationLongitude", str_lng);

                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                            FirebaseFirestore.getInstance()
                                    .collection("Utilizadores")
                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .collection("Acidentes")
                                    .document(str_imagemName)
                                    .set(acidente)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Intent i = new Intent(AddEventActivity.this, MainActivity.class);
                                            Manage.getInstance().setLastActivity(wasAddAcc);
                                            //Reset à memória de ultima localização
                                            Manage.getInstance().setLastLatitude(invalidLocation);
                                            Manage.getInstance().setLastLongitude(invalidLocation);
                                            // Reset aos campos de texto
                                            description.setText("");
                                            duration.setText("");
                                            cost.setText("");

                                            startActivity(i);
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                            finish();
                                        }
                                    });
                        } else if (!isDescriptionValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidDesc), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else if (!isDurationValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidDurationMin), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else if (!isCostValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidCostMin), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else if (!isLocationValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidLocation), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eNoImageFile), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.eImage), Toast.LENGTH_SHORT).show();
                        Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                    });
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            String filename = (FirebaseAuth.getInstance().getUid() + "/Manutencao");
            // se imagem inválida
            FirebaseStorage.getInstance()
                    .getReference()
                    .child(filename.concat(
                            Integer.toString(Manage.getInstance().getNumMan() + 1)) + ".jpg")
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        imageRef.setImageUri(uri.toString());
                        imageRef.setImageName("Manutencao" + (Manage.getInstance().getNumMan() + 1));

                        if (isDescriptionValid && isDurationValid && isCostValid  && isLocationValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.successAddMan), Toast.LENGTH_SHORT).show();

                            int str_ID = Integer.parseInt(ID.getText().toString());
                            String str_date = date.getText().toString();
                            String str_desc = description.getText().toString();
                            int str_duration = Integer.parseInt(duration.getText().toString());
                            int str_cost = Integer.parseInt(cost.getText().toString());
                            String str_imagemUrl = imageRef.getImageUri();
                            String str_imagemName = imageRef.getImageName();
                            double str_lat = Manage.getInstance().getLastLatitude();
                            double str_lng = Manage.getInstance().getLastLongitude();

                            Map<String, Object> manutencao = new HashMap<>();
                            manutencao.put("ID", str_ID);
                            manutencao.put("date", str_date);
                            manutencao.put("email", Manage.getInstance().getUser().getEmail());
                            manutencao.put("description", str_desc);
                            manutencao.put("duration", str_duration);
                            manutencao.put("cost", str_cost);
                            manutencao.put("imageUri", str_imagemUrl);
                            manutencao.put("imageName", str_imagemName);
                            manutencao.put("locationLatitude", str_lat);
                            manutencao.put("locationLongitude", str_lng);

                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                            FirebaseFirestore.getInstance()
                                    .collection("Utilizadores")
                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .collection("Manutencoes")
                                    .document(str_imagemName)
                                    .set(manutencao)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Intent i = new Intent(AddEventActivity.this, MainActivity.class);
                                            Manage.getInstance().setLastActivity(wasAddMan);
                                            // Reset à memória de ultima localização
                                            Manage.getInstance().setLastLatitude(invalidLocation);
                                            Manage.getInstance().setLastLongitude(invalidLocation);
                                            // Reset aos campos de texto
                                            description.setText("");
                                            duration.setText("");
                                            cost.setText("");

                                            startActivity(i);
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                            finish();
                                        }
                                    });
                        } else if (!isDescriptionValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidDesc), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else if (!isDurationValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidDurationMin), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else if (!isCostValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidCostMin), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else if (!isLocationValid) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidLocation), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eNoImageFile), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.eImage), Toast.LENGTH_SHORT).show();
                        Manage.getInstance().playSound(AddEventActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                    });
        }
    }

    /**
     * Método que verifica se o botão de adicionar imagem foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonAddImage(View view) {
        Intent i = new Intent(AddEventActivity.this, AddImageActivity.class);
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            Manage.getInstance().setLastActivity(wasAddAcc);
            Manage.getInstance().setLastButtonClick(wasButtonAddImageAcc);
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            Manage.getInstance().setLastActivity(wasAddMan);
            Manage.getInstance().setLastButtonClick(wasButtonAddImageMan);
        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /**
     * Método que verifica se o botão de adicionar localização foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonAddLoc(View view) {
        Intent i = new Intent(AddEventActivity.this, AddLocationActivity.class);
        if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocAcc)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddAcc | Manage.getInstance().getLastButtonClick() == wasButtonAddImageAcc)) {
            Manage.getInstance().setLastActivity(wasAddImageLocAcc);
        } else if ((Manage.getInstance().getLastActivity() == wasMain | Manage.getInstance().getLastActivity() == wasAddImageLocMan)
                && (Manage.getInstance().getLastButtonClick() == wasButtonAddMan | Manage.getInstance().getLastButtonClick() == wasButtonAddImageMan)) {
            Manage.getInstance().setLastActivity(wasAddImageLocMan);
        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}