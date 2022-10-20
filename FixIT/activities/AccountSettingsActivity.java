package ipleiria.pdm.maintenanceapppdm.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Image;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;
import ipleiria.pdm.maintenanceapppdm.classes.User;

/**
 * Classe do tipo atividade que permite definir o ecrã de definições de conta
 */
public class AccountSettingsActivity extends AppCompatActivity implements Config {
    private final Image imageRef = new Image();
    private boolean isNameValid = false;

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        TextView name = findViewById(R.id.textViewDisplayAccountName);
        name.setText(Manage.getInstance().getUser().getName());

        TextView email = findViewById(R.id.textViewDisplayAccountEmail);
        email.setText(Manage.getInstance().getUser().getEmail());

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtAccountSettings);
    }

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AccountSettingsActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Método que verifica a inserção de dados e carrega os mesmos para a firebase se corretos
     */
    public void SetValidation() {
        String filename = (FirebaseAuth.getInstance().getUid() + "/UserImage");
        FirebaseStorage.getInstance()
                .getReference()
                .child(filename.concat(".jpg"))
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    imageRef.setImageUri(uri.toString());
                    imageRef.setImageName("UserImage");

                    String str_name = Manage.getInstance().getUser().getName();
                    String str_email = Manage.getInstance().getUser().getEmail();
                    String str_language = Manage.getInstance().getUser().getLanguage();
                    String str_imagemUrl = imageRef.getImageUri();
                    String str_imagemName = imageRef.getImageName();
                    Image im = new Image(str_imagemName, str_imagemUrl);

                    User user = new User(str_name, str_email, str_language, im);

                    FirebaseFirestore.getInstance()
                            .collection("Utilizadores")
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successChangeAccountSetting), Toast.LENGTH_SHORT).show();
                                Manage.getInstance().playSound(AccountSettingsActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);
                                Manage.getInstance().setUser(user);

                                Intent i = new Intent(AccountSettingsActivity.this, MainActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                finish();
                            });
                }).addOnFailureListener(exception -> {
                    String str_name = Manage.getInstance().getUser().getName();
                    String str_email = Manage.getInstance().getUser().getEmail();
                    String str_language = Manage.getInstance().getUser().getLanguage();
                    User user = new User(str_name, str_email, str_language);

                    FirebaseFirestore.getInstance()
                            .collection("Utilizadores")
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successChangeAccountSetting), Toast.LENGTH_SHORT).show();
                                Manage.getInstance().playSound(AccountSettingsActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);
                                Manage.getInstance().setUser(user);

                                Intent i = new Intent(AccountSettingsActivity.this, MainActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                finish();
                            });
                });
    }

    /**
     * Método que verifica se o botão de adicionar imagem foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonAddImage(View view) {
        Intent i = new Intent(AccountSettingsActivity.this, AddImageActivity.class);
        Manage.getInstance().setLastActivity(wasAccountSettings);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /**
     * Método que verifica se o botão de guardar as mudanças feitas para a conta foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickChangeSettings(View view) {
        SetValidation();
    }

    /**
     * Método que verifica se o botão de alteração de nome foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    @SuppressLint("SetTextI18n")
    public void onClickButtonEditName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = AccountSettingsActivity.this.getLayoutInflater().inflate(R.layout.confirm_dialog_box, null);

        builder.setView(alertView);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = alertView.findViewById(R.id.tvConfirmTitle);
        title.setText(alertView.getResources().getString(R.string.txtChangeField)
                + alertView.getResources().getString(R.string.txtChangeName));

        TextView confirmEditYes = alertView.findViewById(R.id.tvConfirmYes);
        confirmEditYes.setText(R.string.txtDisplayChangeField);
        TextView confirmEditNo = alertView.findViewById(R.id.tvConfirmNo);
        confirmEditNo.setText(R.string.txtCancelChangeField);

        ImageView confirmButtonYes = alertView.findViewById(R.id.btConfirmYes);
        ImageView confirmButtonNo = alertView.findViewById(R.id.btConfirmNo);

        confirmButtonYes.setOnClickListener(v -> {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(AccountSettingsActivity.this);
            View alertView2 = AccountSettingsActivity.this.getLayoutInflater().inflate(R.layout.edit_field_dialog_box, null);

            builder2.setView(alertView2);
            final AlertDialog alertDialog2 = builder2.show();
            alertDialog2.setCancelable(false);
            alertDialog2.setCanceledOnTouchOutside(false);
            alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Define o titulo da dialog box
            TextView title1 = alertView2.findViewById(R.id.tvEditTitle);
            title1.setText(alertView2.getResources().getString(R.string.txtChangeField)
                    + alertView2.getResources().getString(R.string.txtChangeName));

            // Mostra o campo antigo a mudar
            TextView oldDisplayField = alertView2.findViewById(R.id.tvDisplayOldField);
            oldDisplayField.setText(alertView2.getResources().getString(R.string.txtPreviousName));
            TextView oldField = alertView2.findViewById(R.id.tvOldField);
            oldField.setText(Manage.getInstance().getUser().getName());

            // Mostra o campo novo a inserir
            TextView newDisplayField = alertView2.findViewById(R.id.tvDisplayNewField);
            newDisplayField.setText(alertView2.getResources().getString(R.string.txtNewName));

            ImageView editButtonYes = alertView2.findViewById(R.id.btEditYes);
            ImageView editButtonNo = alertView2.findViewById(R.id.btEditNo);

            editButtonYes.setOnClickListener(v1 -> {
                EditText name = alertView2.findViewById(R.id.etEditField);

                if (name.getText().toString().isEmpty()) {
                    name.setError(getResources().getString(R.string.eEmptyName));
                    isNameValid = false;
                } else if (name.getText().length() > maxNameChars) {
                    name.setError(getResources().getString(R.string.eNameMaxOver)
                            .concat("| " + name.getText().length() + " chars"));
                    isNameValid = false;
                } else
                    isNameValid = true;

                if (isNameValid) {
                    String str = name.getText().toString();
                    String finalName = str.substring(0, 0) + str.toUpperCase().charAt(0) + str.substring(1);
                    Manage.getInstance().getUser().setName(finalName);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditName), Toast.LENGTH_SHORT).show();
                    Manage.getInstance().playSound(AccountSettingsActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);
                    alertDialog2.dismiss();
                    alertDialog.dismiss();
                }
            });

            editButtonNo.setOnClickListener(v12 -> {
                alertDialog2.dismiss();
                alertDialog.dismiss();
            });
        });

        confirmButtonNo.setOnClickListener(v -> alertDialog.dismiss());
    }
}