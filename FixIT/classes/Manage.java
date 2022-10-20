package ipleiria.pdm.maintenanceapppdm.classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Classe que permite gerir o programa através do recurso a vários métodos transversais ao mesmo
 */
public class Manage implements Serializable, Config {
    private final ArrayList<Event> listAcc;
    private final ArrayList<Event> listMan;
    private static Manage INSTANCE = null;
    private AlertDialog lastItemEdit;
    private User user;
    private int lastActivity, lastButtonClick, oldFieldDuration, oldFieldCost, lastID;
    private double lat = invalidLocation, lng = invalidLocation;
    private String language, oldFieldDesc;

    //----------------------------------------------------------------------------------------------
    //---------------------------------------INSTÂNCIA DE GESTÃO------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Adquire a instância da classe de gestão chamada Manage
     *
     * @return a instância Manage
     */
    public static synchronized Manage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Manage();
        }
        return INSTANCE;
    }

    /**
     * Instância dois arraylists dedicados tipo, Acidente e Manutenção
     */
    private Manage() {
        listAcc = new ArrayList<>();
        listMan = new ArrayList<>();
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------NAVEGAÇÃO ENTRE ATIVIDADES--------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que permite devolver a última atividade em que programa esteve
     *
     * @return última atividade
     */
    public int getLastActivity() {
        return lastActivity;
    }

    /**
     * Método que permite definir a última atividade em que o programa esteve
     *
     * @param lastActivity a última atividade do programa
     */
    public void setLastActivity(int lastActivity) {
        this.lastActivity = lastActivity;
    }

    /**
     * Método que permite devolver o último botão pressionado
     *
     * @return último botão pressionado
     */
    public int getLastButtonClick() {
        return lastButtonClick;
    }

    /**
     * Método que permite definir o último botão pressionado durante o uso do programa
     *
     * @param lastButtonClick o último botão pressionado
     */
    public void setLastButtonClick(int lastButtonClick) {
        this.lastButtonClick = lastButtonClick;
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------------MEDIA PLAYER----------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que permite reproduzir um determinado ficheiro
     *
     * @param context      contexto em que o método é chamado
     * @param sound        ficheiro a ser reproduzido
     * @param volumeMax    constante de volume máximo
     * @param wantedVolume constante de volume desejado
     */
    public void playSound(android.content.Context context, int sound, int volumeMax, int wantedVolume) {
        MediaPlayer mp = MediaPlayer.create(context, sound);
        if (!mp.isPlaying()) {
            final float volume = (float) (1 - (Math.log(volumeMax - wantedVolume)
                    / Math.log(volumeMax)));
            mp.setVolume(volume, volume); // define o volume máximo de som
            mp.start();
            mp.setOnCompletionListener(MediaPlayer::release);
        }
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------------ACIDENTE--------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que adiciona um objeto acidente à lista de acidentes
     *
     * @param a objeto acidente
     */
    public void addAcc(Event a) {
        listAcc.add(a);
    }

    /**
     * Método que devolve a lista de acidentes
     *
     * @return lista de acidentes
     */
    public ArrayList<Event> getListAcc() {
        return listAcc;
    }

    /**
     * Método que devolve o tamanho da lista de acidentes
     *
     * @return tamanho da lista de acidentes
     */
    public int getNumAcc() {
        return listAcc.size();
    }

    /**
     * Método que limpa toda a lista de acidentes
     */
    public void resetListAcc() {
        listAcc.clear();
    }

    //----------------------------------------------------------------------------------------------
    //-----------------------------------------MANUTENCAO-------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que adiciona um objeto manutenção à lista de manutenções
     *
     * @param m objeto manutenção
     */
    public void addMan(Event m) {
        listMan.add(m);
    }

    /**
     * Método que devolve a lista de manutenções
     *
     * @return lista de manutenções
     */
    public ArrayList<Event> getListMan() {
        return listMan;
    }

    /**
     * Método que devolve o tamanho da lista de manutenções
     *
     * @return tamanho da lista de manutenções
     */
    public int getNumMan() {
        return listMan.size();
    }

    /**
     * Método que limpa toda a lista de manutenções
     */
    public void resetListMan() {
        listMan.clear();
    }

    //----------------------------------------------------------------------------------------------
    //-----------------------------------------UTILIZADOR-------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que devolve o utilizador atual
     *
     * @return utilizador atual
     */
    public User getUser() {
        return user;
    }

    /**
     * Método que define o utilizador atual
     *
     * @param user utilizador atual
     */
    public void setUser(User user) {
        this.user = user;
    }

    //----------------------------------------------------------------------------------------------
    //--------------------------------LEITURA DE EVENTOS DA FIRESTORE-------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que permite ir buscar todos os acidentes de um dado utilizador à Firebase Firestore
     */
    public void getUserAccFirestore() {
        resetListAcc();

        FirebaseFirestore.getInstance()
                .collection("Utilizadores")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("Acidentes")
                .orderBy("ID")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int ID = Integer.parseInt(Objects.requireNonNull(document.getData().get("ID")).toString());
                            String date = (String) document.getData().get("date");
                            String name = (String) document.getData().get("name");
                            String email = (String) document.getData().get("email");
                            String description = (String) document.getData().get("description");
                            int duration = Integer.parseInt(Objects.requireNonNull(document.getData().get("duration")).toString());
                            int cost = Integer.parseInt(Objects.requireNonNull(document.getData().get("cost")).toString());
                            String imagemNAME = (String) document.getData().get("imageName");
                            String imagemURL = (String) document.getData().get("imageUri");
                            double lat = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLatitude")).toString());
                            double lng = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLongitude")).toString());

                            Image i = new Image(imagemNAME, imagemURL);
                            Location loc = new Location(lat, lng);
                            Event a = new Event(ID, date, name, email, description, duration, cost, i, loc);
                            addAcc(a);
                        }
                    }
                });
    }

    /**
     * Método que permite atualizar na lista de acidentes, o acidente que foi modificado
     */
    public void getUserUpdateAccFirestore() {
        FirebaseFirestore.getInstance()
                .collection("Utilizadores")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("Acidentes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int ID = Integer.parseInt(Objects.requireNonNull(document.getData().get("ID")).toString());
                            String date = (String) document.getData().get("date");
                            String name = (String) document.getData().get("name");
                            String email = (String) document.getData().get("email");
                            String description = (String) document.getData().get("description");
                            int duration = Integer.parseInt(Objects.requireNonNull(document.getData().get("duration")).toString());
                            int cost = Integer.parseInt(Objects.requireNonNull(document.getData().get("cost")).toString());
                            String imagemNAME = (String) document.getData().get("imageName");
                            String imagemURL = (String) document.getData().get("imageUri");
                            double lat = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLatitude")).toString());
                            double lng = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLongitude")).toString());

                            Image i = new Image(imagemNAME, imagemURL);
                            Location loc = new Location(lat, lng);
                            Event a = new Event(ID, date, name, email, description, duration, cost, i, loc);
                            listAcc.set(ID - 1, a); // faz update do respetivo evento
                        }
                    }
                });
    }

    /**
     * Método que permite ir buscar todas as manutenções de um dado utilizador à Firebase Firestore
     */
    public void getUserManFirestore() {
        resetListMan();

        FirebaseFirestore.getInstance()
                .collection("Utilizadores")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("Manutencoes")
                .orderBy("ID")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int ID = Integer.parseInt(Objects.requireNonNull(document.getData().get("ID")).toString());
                            String date = (String) document.getData().get("date");
                            String name = (String) document.getData().get("name");
                            String email = (String) document.getData().get("email");
                            String description = (String) document.getData().get("description");
                            int duration = Integer.parseInt(Objects.requireNonNull(document.getData().get("duration")).toString());
                            int cost = Integer.parseInt(Objects.requireNonNull(document.getData().get("cost")).toString());
                            String imagemNAME = (String) document.getData().get("imageName");
                            String imagemURL = (String) document.getData().get("imageUri");
                            double lat = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLatitude")).toString());
                            double lng = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLongitude")).toString());

                            Image i = new Image(imagemNAME, imagemURL);
                            Location loc = new Location(lat, lng);
                            Event m = new Event(ID, date, name, email, description, duration, cost, i, loc);
                            addMan(m);
                        }
                    }
                });
    }

    /**
     * Método que permite atualizar na lista de manutenções, a manutenção que foi modificada
     */
    public void getUserUpdateManFirestore() {
        FirebaseFirestore.getInstance()
                .collection("Utilizadores")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("Manutencoes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int ID = Integer.parseInt(Objects.requireNonNull(document.getData().get("ID")).toString());
                            String date = (String) document.getData().get("date");
                            String name = (String) document.getData().get("name");
                            String email = (String) document.getData().get("email");
                            String description = (String) document.getData().get("description");
                            int duration = Integer.parseInt(Objects.requireNonNull(document.getData().get("duration")).toString());
                            int cost = Integer.parseInt(Objects.requireNonNull(document.getData().get("cost")).toString());
                            String imagemNAME = (String) document.getData().get("imageName");
                            String imagemURL = (String) document.getData().get("imageUri");
                            double lat = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLatitude")).toString());
                            double lng = Double.parseDouble(Objects.requireNonNull(document.getData().get("locationLongitude")).toString());

                            Image i = new Image(imagemNAME, imagemURL);
                            Location loc = new Location(lat, lng);
                            Event m = new Event(ID, date, name, email, description, duration, cost, i, loc);
                            listMan.set(ID - 1, m); // faz update do respetivo evento
                        }
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------LINGUAGEM---------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que permite atualizar o codigo de linguagem atual
     *
     * @param language codigo de linguagem atual
     */
    public void setLastLang(String language) {
        this.language = language;
    }

    /**
     * Método que permite devolver o codigo de linguagem atual
     *
     * @return codigo de linguagem atual
     */
    public String getLastLang() {
        return language;
    }

    /**
     * Método que permite mudar o local e efetivamente mudar a linguagem da aplicação
     *
     * @param activity contexto de atividade em que o método é chamado
     * @param language código de linguagem
     */
    public void setLocale(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * Método que permite atualizar na Firebase Firestore a linguagem definida para o utilizador atual
     *
     * @param language código de linguagem
     */
    public void updateLocaleFirestore(String language) {
        Map<String, Object> updateLang = new HashMap<>();
        updateLang.put("language", language);

        // Atualiza o campo language no respetivo utilizador
        FirebaseFirestore.getInstance().collection("Utilizadores")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .update(updateLang);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------LOCALIZACAO-------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que permite devolver a última latitude de localização
     *
     * @return última latitude
     */
    public double getLastLatitude() {
        return lat;
    }

    /**
     * Método que permite definir a última latitude de localização
     *
     * @param lat última latitude
     */
    public void setLastLatitude(double lat) {
        this.lat = lat;
    }

    /**
     * Método que permite devolver a última longitude de localização
     *
     * @return última longitude
     */
    public double getLastLongitude() {
        return lng;
    }

    /**
     * Método que permite definir a última longitude de localização
     *
     * @param lng última longitude
     */
    public void setLastLongitude(double lng) {
        this.lng = lng;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------EDICAO CAMPOS DOS EVENTOS-----------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * Método que permite devolver o último ID usado na edição de eventos
     *
     * @return último ID a ser usado
     */
    public int getLastID() {
        return lastID;
    }

    /**
     * Método que permite devolver o último ID usado na edição de eventos
     *
     * @param lastID último ID a ser usado
     */
    public void setLastID(int lastID) {
        this.lastID = lastID;
    }

    /**
     * Método que permite devolver o último campo de descrição usado na edição de eventos
     *
     * @return último campo de descrição
     */
    public String getOldFieldDesc() {
        return oldFieldDesc;
    }

    /**
     * Método que permite definir o último campo de descrição usado na edição de eventos
     *
     * @param oldFieldDesc último campo de descrição
     */
    public void setOldFieldDesc(String oldFieldDesc) {
        this.oldFieldDesc = oldFieldDesc;
    }

    /**
     * Método que permite devolver o último campo de duração usado na edição de eventos
     *
     * @return último campo de duração
     */
    public int getOldFieldDuration() {
        return oldFieldDuration;
    }

    /**
     * Método que permite definir o último campo de duração usado na edição de eventos
     *
     * @param oldFieldDuration último campo de duração
     */
    public void setOldFieldDuration(int oldFieldDuration) {
        this.oldFieldDuration = oldFieldDuration;
    }

    /**
     * Método que permite devolver o último campo de custo usado na edição de eventos
     *
     * @return último campo de custo
     */
    public int getOldFieldCost() {
        return oldFieldCost;
    }

    /**
     * Método que permite definir o último campo de custo usado na edição de eventos
     *
     * @param oldFieldCost último campo de custo
     */
    public void setOldFieldCost(int oldFieldCost) {
        this.oldFieldCost = oldFieldCost;
    }

    /**
     * Método que permite devolver o contexto da última Dialog Box a ser editada
     *
     * @return última Dialog Box
     */
    public AlertDialog getLastItemEdit() {
        return lastItemEdit;
    }

    /**
     * Método que permite definir o contexto da última Dialog Box a ser editada
     *
     * @param lastItemEdit última Dialog Box
     */
    public void setLastItemEdit(AlertDialog lastItemEdit) {
        this.lastItemEdit = lastItemEdit;
    }
}