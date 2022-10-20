package ipleiria.pdm.maintenanceapppdm.classes;

import java.io.Serializable;

/**
 * Classe utilizador
 */
public class User implements Serializable {
    private String name, email, language;
    private Image userImage;

    /**
     * Construtor principal da classe utilizador
     */
    public User(String name, String email, String language, Image userImage) {
        this.name = name;
        this.email = email;
        this.language = language;
        this.userImage = userImage;
    }

    /**
     * Construtor vazio
     */
    public User() {
    }

    /**
     * Construtor secundário usado na criação de um utilizador
     */
    public User(String name, String email, String language) {
    }

    /**
     * Método que permite devolver o nome do utilizador
     *
     * @return nome do utilizador
     */
    public String getName() {
        return name;
    }

    /**
     * Método que permite definir o nome do utilizador
     *
     * @param name - Nome do utilizador
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método que permite devolver o email do utilizador
     *
     * @return email do utilizador
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método que permite definir o email do utilizador
     *
     * @param email - Email do utilizador
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método que permite devolver a linguagem definida para o utilizador
     *
     * @return a linguagem definida para o utilizador
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Método que permite definir a linguagem do utilizador
     *
     * @param language - Linguagem do utilizador
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Método que permite devolver a imagem do utilizador
     *
     * @return a imagem do utilizador
     */
    public Image getUserImage() {
        return userImage;
    }

    /**
     * Método que permite definir a linguagem do utilizador
     *
     * @param userImage - Imagem do utilizador
     */
    public void setUserImage(Image userImage) {
        this.userImage = userImage;
    }
}