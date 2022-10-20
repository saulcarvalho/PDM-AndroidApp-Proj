package ipleiria.pdm.maintenanceapppdm.classes;

import java.io.Serializable;

/**
 * Classe localização
 */
public class Location implements Serializable {
    private double latitude, longitude;

    /**
     * Construtor principal da classe utilizador
     */
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Construtor vazio
     */
    public Location() {
    }

    /**
     * Método que permite definir a latitude de uma localização
     *
     * @param latitude - Latitude de uma localização
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Método que permite devolver a latitude de uma localização
     *
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Método que permite definir a longitude de uma localização
     *
     * @param longitude - Longitude de uma localização
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Método que permite devolver a longitude de uma localização
     *
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }
}