package ipleiria.pdm.maintenanceapppdm.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ipleiria.pdm.maintenanceapppdm.R;
import ipleiria.pdm.maintenanceapppdm.classes.Config;
import ipleiria.pdm.maintenanceapppdm.classes.Image;
import ipleiria.pdm.maintenanceapppdm.classes.Manage;

/**
 * Classe do tipo atividade que permite adicionar uma imagem
 */
public class AddImageActivity extends AppCompatActivity implements Config, CropImageView.OnCropImageCompleteListener, CropImageView.OnSetImageUriCompleteListener {
    private String editTextFileName;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private CropImageView cropImageView;
    private Button cropButton;
    private boolean isImageCropped = false;

    /**
     * Método que permite definir o estado da instância da atividade
     *
     * @param savedInstanceState - Estado da atividade
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        //Define o titulo da pagina de adicionar imagem
        TextView textViewAddImage = findViewById(R.id.textViewAddImage);
        if (Manage.getInstance().getLastActivity() == wasAddAcc) {
            editTextFileName = "Acidente";
            textViewAddImage.setText(R.string.txtAddImageAcc);
            editTextFileName = editTextFileName.concat(
                    Integer.toString(Manage.getInstance().getNumAcc() + 1));
        } else if (Manage.getInstance().getLastActivity() == wasAddMan) {
            editTextFileName = "Manutencao";
            textViewAddImage.setText(R.string.txtAddImageMan);
            editTextFileName = editTextFileName.concat(
                    Integer.toString(Manage.getInstance().getNumMan() + 1));
        } else if (Manage.getInstance().getLastActivity() == wasAccountSettings) {
            editTextFileName = "UserImage";
            textViewAddImage.setText(R.string.txtAddImageAccount);
        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchAcc) {
            editTextFileName = "Acidente";
            textViewAddImage.setText(R.string.txtChangeSearchImage);
            editTextFileName = editTextFileName.concat(
                    Integer.toString(Manage.getInstance().getLastID()));
        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchMan) {
            editTextFileName = "Manutencao";
            textViewAddImage.setText(R.string.txtChangeSearchImage);
            editTextFileName = editTextFileName.concat(
                    Integer.toString(Manage.getInstance().getLastID()));
        }

        imageView = findViewById(R.id.addImageView);
        imageView.setVisibility(View.INVISIBLE);
        cropButton = findViewById(R.id.buttonCrop);
        cropButton.setVisibility(View.INVISIBLE);

        cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setAspectRatio(10, 10);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        cropImageView.setAutoZoomEnabled(true);
        cropImageView.setShowProgressBar(true);
        cropImageView.setCropRect(new Rect(0, 0, 500, 500));
        cropImageView.setOnCropImageCompleteListener(this);
        cropImageView.setOnSetImageUriCompleteListener(this);

        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance()
                .getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
    }

    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new
                    ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                if (result.getData() != null && result.getData().getData() != null) {
                                    imageUri = result.getData().getData();
                                    cropImageView.setImageUriAsync(imageUri);
                                    cropButton.setVisibility(View.VISIBLE);

                                    imageView.setImageURI(imageUri);
                                }
                            }
                        }
                    });

    /**
     * Método que permite detetar se o botão de voltar atrás foi pressionado
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = null;
        if (Manage.getInstance().getLastActivity() == wasAddAcc) {
            i = new Intent(AddImageActivity.this, AddEventActivity.class);
            Manage.getInstance().setLastActivity(wasAddImageLocAcc);
        } else if (Manage.getInstance().getLastActivity() == wasAddMan) {
            i = new Intent(AddImageActivity.this, AddEventActivity.class);
            Manage.getInstance().setLastActivity(wasAddImageLocMan);
        } else if (Manage.getInstance().getLastActivity() == wasAccountSettings) {
            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
            Manage.getInstance().setLastActivity(wasAddImageAccount);
        } else if (Manage.getInstance().getLastActivity() == wasAddImageAccount) {
            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
            Manage.getInstance().setLastActivity(wasAddImageAccount);
        } else if (Manage.getInstance().getLastActivity() == wasAddImageLocAcc) {
            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
            Manage.getInstance().setLastActivity(wasAddImageLocAcc);
        } else if (Manage.getInstance().getLastActivity() == wasAddImageLocMan) {
            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
            Manage.getInstance().setLastActivity(wasAddImageLocMan);
        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchAcc) {
            i = new Intent(AddImageActivity.this, SearchEventActivity.class);
        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchMan) {
            i = new Intent(AddImageActivity.this, SearchEventActivity.class);
        }
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Método que verifica se o botão de adicionar imagem pela galeria foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonChooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        openGallery.launch(intent);
    }

    /**
     * Método que verifica se o botão de fazer upload de imagem for pressionada
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonUpload(View view) {
        if (storageTask != null && storageTask.isInProgress()) {
            Toast.makeText(AddImageActivity.this,
                    getResources().getString(R.string.txtUploadingPhoto), Toast.LENGTH_SHORT).show();
        } else {
            if (imageUri == null) {
                Toast.makeText(AddImageActivity.this,
                        getResources().getString(R.string.eNoImageFile), Toast.LENGTH_SHORT).show();
                Manage.getInstance().playSound(AddImageActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
                return;
            }
            uploadFile();
        }
    }

    /**
     * Método que devolve a string do tipo de ficheiro de uma imagem através da Uri da imagem
     *
     * @param uri - Uri da imagem
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Método que faz upload do ficheiro para a firebase
     */
    private void uploadFile() {
        if (isImageCropped) {
            StorageReference fileReference = storageReference.child(editTextFileName + "." +
                    getFileExtension(imageUri));
            storageTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0), 500);
                        Toast.makeText(AddImageActivity.this,
                                getResources().getString(R.string.successImageUpload), Toast.LENGTH_SHORT).show();
                        Task<Uri> task =
                                Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                        task.addOnSuccessListener(uri -> {
                            String photoLink = uri.toString();
                            new Image(editTextFileName, photoLink);
                        });
                        Manage.getInstance().playSound(AddImageActivity.this, R.raw.success_sound, maxVolume, soundVolumeSuccess);

                        Intent i = null;
                        if (Manage.getInstance().getLastActivity() == wasAddAcc) {
                            i = new Intent(AddImageActivity.this, AddEventActivity.class);
                            Manage.getInstance().setLastActivity(wasAddImageLocAcc);
                        } else if (Manage.getInstance().getLastActivity() == wasAddMan) {
                            i = new Intent(AddImageActivity.this, AddEventActivity.class);
                            Manage.getInstance().setLastActivity(wasAddImageLocMan);
                        } else if (Manage.getInstance().getLastActivity() == wasAccountSettings) {
                            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
                            Manage.getInstance().setLastActivity(wasAddImageAccount);
                        } else if (Manage.getInstance().getLastActivity() == wasAddImageAccount) {
                            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
                            Manage.getInstance().setLastActivity(wasAddImageAccount);
                        } else if (Manage.getInstance().getLastActivity() == wasAddImageLocAcc) {
                            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
                            Manage.getInstance().setLastActivity(wasAddImageLocAcc);
                        } else if (Manage.getInstance().getLastActivity() == wasAddImageLocMan) {
                            i = new Intent(AddImageActivity.this, AccountSettingsActivity.class);
                            Manage.getInstance().setLastActivity(wasAddImageLocMan);
                        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchAcc) {
                            String filename = (FirebaseAuth.getInstance().getUid() + "/" + editTextFileName);
                            // Atualiza a imagem no respetivo acidente na storage
                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child(filename.concat(".jpg"))
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Map<String, Object> updateImage = new HashMap<>();
                                        updateImage.put("imageName", editTextFileName);
                                        updateImage.put("imageUri", uri.toString());

                                        // Atualiza a imagem no respetivo acidente na firestore
                                        FirebaseFirestore.getInstance().collection("Utilizadores")
                                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                .collection("Acidentes")
                                                .whereEqualTo("ID", Manage.getInstance().getLastID())
                                                .get()
                                                .addOnCompleteListener(task1 -> task1.getResult()
                                                        .getDocuments()
                                                        .get(0)
                                                        .getReference()
                                                        .update(updateImage));
                                    });
                            i = new Intent(AddImageActivity.this, MainActivity.class);
                        } else if (Manage.getInstance().getLastActivity() == wasMain && Manage.getInstance().getLastButtonClick() == wasButtonSearchMan) {
                            String filename = (FirebaseAuth.getInstance().getUid() + "/" + editTextFileName);
                            // Atualiza a imagem no respetivo acidente na storage
                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child(filename.concat(".jpg"))
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Map<String, Object> updateImage = new HashMap<>();
                                        updateImage.put("imageName", editTextFileName);
                                        updateImage.put("imageUri", uri.toString());

                                        // Atualiza a imagem no respetivo acidente na firestore
                                        FirebaseFirestore.getInstance().collection("Utilizadores")
                                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                .collection("Manutencoes")
                                                .whereEqualTo("ID", Manage.getInstance().getLastID())
                                                .get()
                                                .addOnCompleteListener(task12 -> task12.getResult()
                                                        .getDocuments()
                                                        .get(0)
                                                        .getReference()
                                                        .update(updateImage));
                                    });
                            i = new Intent(AddImageActivity.this, MainActivity.class);
                        }
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddImageActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() /
                                taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(AddImageActivity.this,
                    getResources().getString(R.string.eNoImageCrop), Toast.LENGTH_SHORT).show();
            Manage.getInstance().playSound(AddImageActivity.this, R.raw.failure_sound, maxVolume, soundVolumeFailure);
        }
    }

    ActivityResultLauncher<Intent> openCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                                cropImageView.setImageUriAsync(imageUri);
                                cropButton.setVisibility(View.VISIBLE);

                                imageView.setImageURI(imageUri);
                        }
                    }
            });

    /**
     * Método para aceder ao telemóvel e tirar foto
     */
    public void accessCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (i.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "ipleiria.pdm.maintenanceapppdm",
                        photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                openCamera.launch(i);
            }
        }
    }

    /**
     * Método que verifica a permissão de acesso à câmera
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    accessCamera();
                }
            });

    /**
     * Método que verifica se o botão de tirar foto com cãmara foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonTakePhoto(View view) {
        if (ContextCompat.checkSelfPermission(AddImageActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            accessCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Método que cria ficheiro de imagem
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                editTextFileName,      // prefix
                ".jpg",       // suffix
                storageDir          // directory
        );
    }

    /**
     * Método que verifica se o botão de recortar a imagem foi pressionado
     *
     * @param view - View onde se encontra o botão
     */
    public void onClickButtonCrop(View view) {
        cropImageView.setOnCropImageCompleteListener(this);
        cropImageView.setOnSetImageUriCompleteListener(this);
        cropImageView.getCroppedImageAsync();
        isImageCropped = true;
    }

    /**
     * Método de callback do Listener que ocorre quando o recorte da imagem foi concluído
     *
     * @param view - View da imagem
     * @param result - Resultdado do crop da imagem
     */
    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        view.setImageBitmap(result.getBitmap());
        view.saveCroppedImageAsync(imageUri);

        cropImageView.setOnCropImageCompleteListener(null);
    }

    /**
     * Método de callback do Listener que ocorre quando a URI da imagem está disponível
     *
     * @param view  - View da imagem
     * @param uri   - Uri da imagem
     * @param error - Erro de excepção do listener
     */
    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        imageUri = uri;
        imageView.setImageURI(uri);
        view.setImageUriAsync(uri);

        cropImageView.setOnSetImageUriCompleteListener(null);
    }
}