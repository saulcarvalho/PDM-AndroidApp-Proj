package ipleiria.pdm.maintenanceapppdm.classes;

import java.io.Serializable;

/**
 * Classe localização
 */
public class Event implements Serializable {
    private String date, name, email, description;
    private int ID, duration, cost;
    private Image image;
    private Location location;

    /**
     * Construtor principal da classe utilizador
     */
    public Event(int ID, String date, String name, String email, String description, int duration, int cost, Image image, Location location) {
        this.ID = ID;
        this.date = date;
        this.name = name;
        this.email = email;
        this.description = description;
        this.duration = duration;
        this.cost = cost;
        this.image = image;
        this.location = location;
    }

    /**
     * Construtor vazio
     */
    public Event() {
    }

    /**
     * Método que permite devolver a identificação do evento
     *
     * @return identificação do evento
     */
    public int getID() {
        return ID;
    }

    /**
     * Método que permite definir a identificação do evento
     *
     * @param ID - Identificação do evento
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Método que permite devolver a data do evento
     *
     * @return data do evento
     */
    public String getDate() {
        return date;
    }

    /**
     * Método que permite definir a data do evento
     *
     * @param date - Data do evento
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Método que permite devolver o nome do evento
     *
     * @return nome do evento
     */
    public String getName() {
        return name;
    }

    /**
     * Método que permite definir o nome do evento
     *
     * @param name - Nome do evento
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método que permite devolver o email do utilizador que criou o evento
     *
     * @return email do utilizador que criou o evento
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método que permite definir o email do utilizador que criou o evento
     *
     * @param email - Email do utilizador que criou o evento
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método que permite devolver a descrição do evento
     *
     * @return descrição do evento
     */
    public String getDescription() {
        return description;
    }

    /**
     * Método que permite definir a descrição do evento
     *
     * @param description - Descrição do evento
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Método que permite devolver a duração do evento
     *
     * @return duração do evento
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Método que permite definir a duração do evento
     *
     * @param duration - Duração do evento
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Método que permite devolver o custo do evento
     *
     * @return descrição do custo
     */
    public int getCost() {
        return cost;
    }

    /**
     * Método que permite definir o custo do evento
     *
     * @param cost - Custo do evento
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Método que permite devolver a foto do evento
     *
     * @return foto do evento
     */
    public Image getImage() {
        return image;
    }

    /**
     * Método que permite definir a foto do evento
     *
     * @param image - Foto do evento
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Método que permite devolver a localização do evento
     *
     * @return localização do evento
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Método que permite definir a localização do evento
     *
     * @param location - Localização do evento
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}