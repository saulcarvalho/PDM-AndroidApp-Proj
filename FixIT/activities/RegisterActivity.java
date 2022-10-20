package ipleiria.pdm.maintenanceapppdm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Image;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;
import ipleiria.pdm.maintenanceapppdm.classes.User;

/**
 * Classe do tipo atividade que permite o registo de um utilizador na firebase e aceder depois à
 * aplicação pela atividade login
 */
public class RegisterActivity extends AppCompatActivity implements Config {
    EditText name, email, password, confirm;
    Button register;
    TextView notregister;
    boolean isNameValid, isEmailValid, isPasswordValid, isPassConfirmValid;
    public static final String lastName = "name", lastEmail = "email";

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.editTextRegisterName);
        email = findViewById(R.id.editTextRegisterEmail);
        password = findViewById(R.id.editTextRegisterPass);
        confirm = findViewById(R.id.editTextConfirmPass);
        register = findViewById(R.id.buttonRegister);
        notregister = findViewById(R.id.textNotRegister);

        // se o botão register for pressionado, verifica a validação de dados inseridos
        register.setOnClickListener(v -> SetValidation());

        // se a TextView for pressionada, invoca a login activity
        notregister.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        // guarda o ultimo estado do editText do nome
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        name.setText(pref.getString(lastName, ""));
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(lastName, s.toString()).apply();
            }
        });

        // guarda o ultimo estado do editText do email
        email.setText(pref.getString(lastEmail, ""));
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(lastEmail, s.toString()).apply();
            }
        });
    }

    /**
     * Método que verifica a inserção de dados e carrega os mesmos para a firebase se corretos
     */
    public void SetValidation() {
        // Verifica se existe um nome válido
        if (name.getText().toString().isEmpty()) {
            name.setError(getResources().getString(R.string.eEmptyName));
            isNameValid = false;
        } else if (name.getText().length() > maxNameChars) {
            name.setError(getResources().getString(R.string.eNameMaxOver)
                    .concat("| " + name.getText().length() + " chars"));
            isNameValid = false;
        } else
            isNameValid = true;

        // Verifica se existe um email válido
        if (email.getText().toString().isEmpty()) {
            email.setError(getResources().getString(R.string.eEmptyEmail));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError(getResources().getString(R.string.eFormatEmail));
            isEmailValid = false;
        } else
            isEmailValid = true;

        // Verifica se existe uma pass válida
        if (password.getText().toString().isEmpty()) {
            password.setError(getResources().getString(R.string.eEmptyPass));
            isPasswordValid = false;
        } else if (password.getText().length() < minPasswordChars) {
            password.setError(getResources().getString(R.string.eShortPass));
            isPasswordValid = false;
        } else
            isPasswordValid = true;

        // Confirma a password
        if (confirm.getText().toString().isEmpty()) {
            confirm.setError(getResources().getString(R.string.eEmptyPass));
            isPassConfirmValid = false;
        } else if (confirm.getText().length() < minPasswordChars) {
            confirm.setError(getResources().getString(R.string.eShortPass));
            isPassConfirmValid = false;
        } else if (!(confirm.getText().toString().equals(password.getText().toString()))) {
            confirm.setError(getResources().getString(R.string.ePassConfirm));
            isPassConfirmValid = false;
        } else
            isPassConfirmValid = true;

        // Se validação toda correta, regista utilizador com os dados na firebase
        if (isNameValid && isEmailValid && isPasswordValid && isPassConfirmValid) {
            String str = name.getText().toString();
            String usernameFB = str.substring(0, 0) + str.toUpperCase().charAt(0) + str.substring(1);
            String emailFB = email.getText().toString();
            String passwordFB = password.getText().toString();
            Image im = new Image("n", "n");

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailFB, passwordFB)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            User user = new User(usernameFB, emailFB, "", im);
                            FirebaseFirestore.getInstance()
                                    .collection("Utilizadores")
                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.successRegister), Toast.LENGTH_SHORT).show();
                                        Manage.getInstance().playSound(RegisterActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                                        Manage.getInstance().setUser(user);

                                        // Registo bem sucedido, invoca a login activity
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                        finish();
                                    });

                        } else {
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.failRegister), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(RegisterActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        }
                    });
        } else if (!isNameValid) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidName), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(RegisterActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        } else if (!isEmailValid) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidEmail), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(RegisterActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        } else if (!(isPasswordValid || isPassConfirmValid)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.ePassInvalid), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(RegisterActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        }
    }
}