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
import ipleiria.pdm.maintenanceapppdm.classes.Manage;
import ipleiria.pdm.maintenanceapppdm.classes.User;

/**
 * Classe do tipo atividade que permite um utilizador registado pela firebase fazer
 * login na aplicação
 */
public class LoginActivity extends AppCompatActivity implements Config {
    EditText email, password;
    Button login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    public static final String lastEmail = "email";

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPass);
        login = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.textRegister);

        // se o botão login for pressionado, verifica a validação de dados inseridos
        login.setOnClickListener(v -> SetValidation());

        // se a TextView for pressionada, invoca a register activity
        register.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // guarda o ultimo estado do editText do email
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
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
        } else {
            isPasswordValid = true;
        }

        // Se email e pass okay então autentica-se na firebase e faz login na app
        if (isEmailValid && isPasswordValid) {
            String emailFB = email.getText().toString();
            String passwordFB = password.getText().toString();

            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailFB, passwordFB)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance()
                                    .collection("Utilizadores")
                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
                                        Manage.getInstance().playSound(LoginActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                                        User user = documentSnapshot.toObject(User.class);
                                        Manage.getInstance().setUser(user);

                                        // Login bem sucedido, invoca a main activity
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.failLogin), Toast.LENGTH_SHORT).show();
                            Manage.getInstance().playSound(LoginActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                        }
                    });
        } else if (!isEmailValid) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eInvalidEmail), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(LoginActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        } else {    // password not valid
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.ePassInvalid), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(LoginActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        }
    }
}