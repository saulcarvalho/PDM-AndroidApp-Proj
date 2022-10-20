package ipleiria.pdm.maintenanceapppdm.classes;

import java.io.Serializable;

/**
 * Classe imagem
 */
public class Image implements Serializable {
    private String imageName, imageUri;

    /**
     * Construtor principal da classe utilizador
     */
    public Image(String imageName, String imageUri) {
        this.imageName = imageName;
        this.imageUri = imageUri;
    }

    /**
     * Construtor vazio
     */
    public Image() {
    }

    /**
     * Método que permite definir o nome da imagem
     *
     * @param imageName - Nome da imagem
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Método que permite devolver o nome definido para a imagem
     *
     * @return nome definido para a imagem
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Método que permite definir a Uri do evento (caminho HTML)
     *
     * @param imageUri - Uri do evento
     */
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * Método que permite devolver a Uri do evento (caminho HTML)
     *
     * @return Uri do evento
     */
    public String getImageUri() {
        return imageUri;
    }
}