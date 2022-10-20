package ipleiria.pdm.maintenanceapppdm.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;
import ipleiria.pdm.maintenanceapppdm.classes.ListViewAdapterAcc;
import ipleiria.pdm.maintenanceapppdm.classes.ListViewAdapterMan;


  /**
   * Classe do tipo atividade que permite procurar um dado evento inserido por um utilizador
   */
public class SearchEventActivity extends AppCompatActivity implements Config {
    private boolean isDescriptionValid = false, isDurationValid = false, isCostValid = false;
    private boolean isDescClick = false, isDurationClick = false, isCostClick = false;

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);

        TextView title = findViewById(R.id.textViewSearchEvent);
        if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchAcc) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtSearchAcc);
            title.setText(R.string.txtSearchAcc);
            ListView listaAcc = findViewById(R.id.listView);
            ListViewAdapterAcc adapterAcc = new ListViewAdapterAcc(Manage.getInstance().getListAcc(), SearchEventActivity.this);
            listaAcc.setAdapter(adapterAcc);
            listaAcc.setOnItemClickListener(adapterAcc);
            listaAcc.setOnItemLongClickListener((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchEventActivity.this);
                View alertView = SearchEventActivity.this.getLayoutInflater().inflate(R.layout.confirm_dialog_box, null);

                builder.setView(alertView);
                final AlertDialog alertDialog = builder.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int ID = Manage.getInstance().getListAcc().get(position).getID();
                TextView title1 = alertView.findViewById(R.id.tvConfirmTitle);
                title1.setText(alertView.getResources().getString(R.string.txtRemoveEvent)
                        .concat(alertView.getResources().getString(R.string.txtLabelAcc))
                        .concat(Integer.toString(ID)));

                TextView confirmRemoveYes = alertView.findViewById(R.id.tvConfirmYes);
                confirmRemoveYes.setText(R.string.txtDisplayRemoveAccYes);
                TextView confirmRemoveNo = alertView.findViewById(R.id.tvConfirmNo);
                confirmRemoveNo.setText(R.string.txtDisplayRemoveAccNo);

                ImageView confirmButtonYes = alertView.findViewById(R.id.btConfirmYes);
                ImageView confirmButtonNo = alertView.findViewById(R.id.btConfirmNo);

                confirmButtonYes.setOnClickListener(v -> {
                        // Apaga os dados do evento a remover da firestore
                        FirebaseFirestore.getInstance()
                                .collection("Utilizadores")
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection("Acidentes")
                                .whereEqualTo("ID", ID)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        task.getResult().getDocuments().get(0).getReference().delete();

                                        FirebaseStorage.getInstance()
                                                .getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                .child("Acidente" + ID + ".jpg")
                                                .delete();

                                        // Apaga listaAcidentes da Gestão para dar lugar a uma nova lista atualizada
                                        Manage.getInstance().resetListAcc();
                                        Manage.getInstance().getUserAccFirestore();

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.successAccRemove), Toast.LENGTH_SHORT).show();
                                        Manage.getInstance().playSound(SearchEventActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                                        alertDialog.dismiss();
                                        Intent i = new Intent(SearchEventActivity.this, MainActivity.class);
                                        overridePendingTransition(0, 0);
                                        startActivity(i);
                                        overridePendingTransition(0, 0);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    }
                                });
                    });

                confirmButtonNo.setOnClickListener(v -> alertDialog.dismiss());
                return true;
            });
        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchMan) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.txtSearchMan);
            title.setText(R.string.txtSearchMan);
            ListView listaMan = findViewById(R.id.listView);
            ListViewAdapterMan adapterMan = new ListViewAdapterMan(Manage.getInstance().getListMan(), SearchEventActivity.this);
            listaMan.setAdapter(adapterMan);
            listaMan.setOnItemClickListener(adapterMan);
            listaMan.setOnItemLongClickListener((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchEventActivity.this);
                View alertView = SearchEventActivity.this.getLayoutInflater().inflate(R.layout.confirm_dialog_box, null);

                builder.setView(alertView);
                final AlertDialog alertDialog = builder.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int ID = Manage.getInstance().getListMan().get(position).getID();
                TextView title12 = alertView.findViewById(R.id.tvConfirmTitle);
                title12.setText(alertView.getResources().getString(R.string.txtRemoveEvent)
                        .concat(alertView.getResources().getString(R.string.txtLabelMan))
                        .concat(Integer.toString(ID)));

                TextView confirmRemoveYes = alertView.findViewById(R.id.tvConfirmYes);
                confirmRemoveYes.setText(R.string.txtDisplayRemoveManYes);
                TextView confirmRemoveNo = alertView.findViewById(R.id.tvConfirmNo);
                confirmRemoveNo.setText(R.string.txtDisplayRemoveManNo);

                ImageView confirmButtonYes = alertView.findViewById(R.id.btConfirmYes);
                ImageView confirmButtonNo = alertView.findViewById(R.id.btConfirmNo);

                confirmButtonYes.setOnClickListener(v -> {
                    // Apaga os dados do evento a remover da firestore
                    FirebaseFirestore.getInstance()
                            .collection("Utilizadores")
                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .collection("Manutencoes")
                            .whereEqualTo("ID", ID)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    task.getResult().getDocuments().get(0).getReference().delete();

                                    FirebaseStorage.getInstance()
                                            .getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                            .child("Manutencao" + ID + ".jpg")
                                            .delete();

                                    // Apaga listaManutencoes da Gestão para dar lugar a uma nova lista atualizada
                                    Manage.getInstance().resetListMan();
                                    Manage.getInstance().getUserManFirestore();

                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.successManRemove), Toast.LENGTH_SHORT).show();
                                    Manage.getInstance().playSound(SearchEventActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                                    alertDialog.dismiss();
                                    Intent i = new Intent(SearchEventActivity.this, MainActivity.class);
                                    overridePendingTransition(0, 0);
                                    startActivity(i);
                                    overridePendingTransition(0, 0);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            });

                });

                confirmButtonNo.setOnClickListener(v -> alertDialog.dismiss());
                return true;
            });
        }
    }

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SearchEventActivity.this, MainActivity.class);
        if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchAcc) {
            Manage.getInstance().setLastActivity(wasSearchAcc);
            Manage.getInstance().setLastButtonClick(wasButtonSearchAcc);
        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchMan) {
            Manage.getInstance().setLastActivity(wasSearchMan);
            Manage.getInstance().setLastButtonClick(wasButtonSearchMan);
        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Método que verifica se o botão de editar descrição foi pressionado e define uma flag
     *
     * @param view - View onde se encontra o botão
     */
    @SuppressLint("SetTextI18n")
    public void onClickButtonEditDesc(View view) {
        isDescClick = true;
        whichClick();
    }

    /**
     * Método que verifica se o botão de editar duração foi pressionado e define uma flag
     *
     * @param view - View onde se encontra o botão
     */
    @SuppressLint("SetTextI18n")
    public void onClickButtonEditDuration(View view) {
        isDurationClick = true;
        whichClick();
    }

    /**
     * Método que verifica se o botão de editar custo foi pressionado e define uma flag
     *
     * @param view - View onde se encontra o botão
     */
    @SuppressLint("SetTextI18n")
    public void onClickButtonEditCost(View view) {
        isCostClick = true;
        whichClick();
    }

      /**
       * Método que verifica se o botão de editar imagem foi pressionado e define uma flag
       *
       * @param view - View onde se encontra o botão
       */
    public void onClickButtonEditImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = SearchEventActivity.this.getLayoutInflater().inflate(R.layout.confirm_dialog_box, null);

        builder.setView(alertView);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = alertView.findViewById(R.id.tvConfirmTitle);
        title.setText(alertView.getResources().getString(R.string.txtChangeField)
                .concat(alertView.getResources().getString(R.string.txtChangeImage)));


        TextView confirmEditYes = alertView.findViewById(R.id.tvConfirmYes);
        confirmEditYes.setText(R.string.txtDisplayChangeField);
        TextView confirmEditNo = alertView.findViewById(R.id.tvConfirmNo);
        confirmEditNo.setText(R.string.txtCancelChangeField);

        ImageView confirmButtonYes = alertView.findViewById(R.id.btConfirmYes);
        ImageView confirmButtonNo = alertView.findViewById(R.id.btConfirmNo);

        confirmButtonYes.setOnClickListener(v -> {
            Intent i = new Intent(SearchEventActivity.this, AddImageActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        confirmButtonNo.setOnClickListener(v -> alertDialog.dismiss());
    }

    /**
     * Método que verifica qual botão foi pressionado e permite a edição do respetivo campo
     */
    @SuppressLint("SetTextI18n")
    public void whichClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = SearchEventActivity.this.getLayoutInflater().inflate(R.layout.confirm_dialog_box, null);

        builder.setView(alertView);
        final AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = alertView.findViewById(R.id.tvConfirmTitle);
        if (isDescClick) {
            title.setText(alertView.getResources().getString(R.string.txtChangeField)
                    .concat(alertView.getResources().getString(R.string.txtChangeDesc)));
        } else if (isDurationClick) {
            title.setText(alertView.getResources().getString(R.string.txtChangeField)
                    .concat(alertView.getResources().getString(R.string.txtChangeDuration)));
        } else if (isCostClick) {
            title.setText(alertView.getResources().getString(R.string.txtChangeField)
                    .concat(alertView.getResources().getString(R.string.txtChangeCost)));
        }

        TextView confirmEditYes = alertView.findViewById(R.id.tvConfirmYes);
        confirmEditYes.setText(R.string.txtDisplayChangeField);
        TextView confirmEditNo = alertView.findViewById(R.id.tvConfirmNo);
        confirmEditNo.setText(R.string.txtCancelChangeField);

        ImageView confirmButtonYes = alertView.findViewById(R.id.btConfirmYes);
        ImageView confirmButtonNo = alertView.findViewById(R.id.btConfirmNo);

        confirmButtonYes.setOnClickListener(v -> {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(SearchEventActivity.this);
            View alertView2 = SearchEventActivity.this.getLayoutInflater().inflate(R.layout.edit_field_dialog_box, null);

            builder2.setView(alertView2);
            final AlertDialog alertDialog2 = builder2.show();
            alertDialog2.setCancelable(false);
            alertDialog2.setCanceledOnTouchOutside(false);
            alertDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Define o titulo da dialog box
            TextView title1 = alertView2.findViewById(R.id.tvEditTitle);
            if (isDescClick) {
                title1.setText(alertView2.getResources().getString(R.string.txtChangeField)
                        .concat(alertView2.getResources().getString(R.string.txtChangeDesc)));
            } else if (isDurationClick) {
                title1.setText(alertView2.getResources().getString(R.string.txtChangeField)
                        .concat(alertView2.getResources().getString(R.string.txtChangeDuration)));
            } else if (isCostClick) {
                title1.setText(alertView2.getResources().getString(R.string.txtChangeField)
                        .concat(alertView2.getResources().getString(R.string.txtChangeCost)));
            }

            // Mostra o campo antigo a mudar
            TextView oldDisplayField = alertView2.findViewById(R.id.tvDisplayOldField);
            if (isDescClick) {
                oldDisplayField.setText(alertView2.getResources().getString(R.string.txtPreviousDesc));
            } else if (isDurationClick) {
                oldDisplayField.setText(alertView2.getResources().getString(R.string.txtPreviousDuration));
            } else if (isCostClick) {
                oldDisplayField.setText(alertView2.getResources().getString(R.string.txtPreviousCost));
            }
            TextView oldField = alertView2.findViewById(R.id.tvOldField);
            if (isDescClick) {
                oldField.setText(Manage.getInstance().getOldFieldDesc());
            } else if (isDurationClick) {
                oldField.setText(Integer.toString(Manage.getInstance().getOldFieldDuration()));
            } else if (isCostClick) {
                oldField.setText(Integer.toString(Manage.getInstance().getOldFieldCost()));
            }

            // Mostra o campo novo a inserir
            TextView newDisplayField = alertView2.findViewById(R.id.tvDisplayNewField);
            if (isDescClick) {
                newDisplayField.setText(alertView2.getResources().getString(R.string.txtNewDesc));
            } else if (isDurationClick) {
                newDisplayField.setText(alertView2.getResources().getString(R.string.txtNewDuration));
            } else if (isCostClick) {
                newDisplayField.setText(alertView2.getResources().getString(R.string.txtNewCost));
            }

            ImageView editButtonYes = alertView2.findViewById(R.id.btEditYes);
            ImageView editButtonNo = alertView2.findViewById(R.id.btEditNo);

            EditText field = alertView2.findViewById(R.id.etEditField);
            if (isDescClick) {
                field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                field.setMaxLines(5);
                field.setVerticalScrollBarEnabled(true);
                field.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
                field.setMovementMethod(new ScrollingMovementMethod());
            } else if (isDurationClick) {
                field.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            } else if (isCostClick) {
                field.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                        | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            }

            editButtonYes.setOnClickListener(v1 -> {
                if (isDescClick) {
                    if (field.getText().toString().isEmpty()) {
                        field.setError(getResources().getString(R.string.eEmptyDesc));
                        isDescriptionValid = false;
                    } else if (field.getText().length() > maxDescChars) {
                        field.setError(getResources().getString(R.string.eDescMaxOver)
                                .concat("| " + field.getText().length() + " chars"));
                        isDescriptionValid = false;
                    } else
                        isDescriptionValid = true;
                } else if (isDurationClick) {
                    if (field.getText().toString().isEmpty()) {
                        field.setError(getResources().getString(R.string.eEmptyDuration));
                        isDurationValid = false;
                    } else if (Integer.parseInt(field.getText().toString()) < minDuration) {
                        field.setError(getResources().getString(R.string.eInvalidDurationMin));
                        isDurationValid = false;
                    } else if (Integer.parseInt(field.getText().toString()) > maxDuration) {
                        field.setError(getResources().getString(R.string.eInvalidDurationMax));
                        isDurationValid = false;
                    } else
                        isDurationValid = true;
                } else if (isCostClick) {
                    if (field.getText().toString().isEmpty()) {
                        field.setError(getResources().getString(R.string.eEmptyCost));
                        isCostValid = false;
                    } else if (Integer.parseInt(field.getText().toString()) < minCusto) {
                        field.setError(getResources().getString(R.string.eInvalidCostMin));
                        isCostValid = false;
                    } else if (Integer.parseInt(field.getText().toString()) > maxCusto) {
                        field.setError(getResources().getString(R.string.eInvalidCostMax));
                        isCostValid = false;
                    } else
                        isCostValid = true;
                }

                if (isDescriptionValid || isDurationValid || isCostValid) {
                    int ID = Manage.getInstance().getLastID();

                    if (Manage.getInstance().getLastButtonClick() == wasButtonSearchAcc) {
                        FirebaseFirestore.getInstance()
                                .collection("Utilizadores")
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection("Acidentes")
                                .whereEqualTo("ID", ID)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String f = field.getText().toString();

                                            Map<String, Object> updateField = new HashMap<>();
                                            if (isDescClick) {
                                                updateField.put("description", f);
                                            } else if (isDurationClick) {
                                                updateField.put("duration", f);
                                            } else if (isCostClick) {
                                                updateField.put("cost", f);
                                            }

                                            document.getReference().update(updateField);
                                            Manage.getInstance().getUserUpdateAccFirestore();

                                            if (isDescClick) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditDesc), Toast.LENGTH_SHORT).show();
                                                isDescClick = false;
                                            } else if (isDurationClick) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditDuration), Toast.LENGTH_SHORT).show();
                                                isDurationClick = false;
                                            } else if (isCostClick) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditCost), Toast.LENGTH_SHORT).show();
                                                isCostClick = false;
                                            }
                                        }
                                    }
                                });
                    } else if (Manage.getInstance().getLastButtonClick() == wasButtonSearchMan) {
                        FirebaseFirestore.getInstance()
                                .collection("Utilizadores")
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                .collection("Manutencoes")
                                .whereEqualTo("ID", ID)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String f = field.getText().toString();

                                            Map<String, Object> updateField = new HashMap<>();
                                            if (isDescClick) {
                                                updateField.put("description", f);
                                            } else if (isDurationClick) {
                                                updateField.put("duration", f);
                                            } else if (isCostClick) {
                                                updateField.put("cost", f);
                                            }

                                            document.getReference().update(updateField);
                                            Manage.getInstance().getUserUpdateManFirestore();

                                            if (isDescClick) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditDesc), Toast.LENGTH_SHORT).show();
                                                isDescClick = false;
                                            } else if (isDurationClick) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditDuration), Toast.LENGTH_SHORT).show();
                                                isDurationClick = false;
                                            } else if (isCostClick) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successEditCost), Toast.LENGTH_SHORT).show();
                                                isCostClick = false;
                                            }
                                        }
                                    }
                                });
                    }

                    Manage.getInstance().playSound(SearchEventActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);
                    alertDialog2.dismiss();
                    alertDialog.dismiss();
                    Manage.getInstance().getLastItemEdit().dismiss();
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