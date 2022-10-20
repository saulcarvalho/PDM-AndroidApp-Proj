package ipleiria.pdm.maintenanceapppdm.classes;

/**
 * Interface que permite definir constantes usadas ao longo do programa
 */
public interface Config {
    // Constantes de Validação
    int minDuration = 1;
    int maxDuration = 24;                       // Define o max de cada intervenção como 24h
    int minPasswordChars = 7;
    int maxNameChars = 15;
    int maxDescChars = 500;
    int minCusto = 1;
    int maxCusto = 9999999;

    // Constantes do SplashScreen
    int splashTimeout = 3000;                   // em ms
    int rotateTime = 2000;

    // Constantes MediaPlayer
    int maxVolume = 100;
    int soundVolumeSplashScreen = 50;
    int soundVolumeSuccess = 40;
    int soundVolumeFailure = 40;
    int soundVolumeBubble = 20;

    // Constantes Google Maps
    int camOrientNorth = 0;                     // Orientação para Norte
    int camZoomMainMaps = 14;
    int camZoomRegularLoc = 18;
    int camTilt = 30;                           // em graus
    int intervalTime = 120000;                  // em ms
    int waitTime = 100;                         // em ms
    int camUpdateDurationMainMaps = 3000;       // em ms
    int camUpdateDurationRegularLoc = 1000;     // em ms
    int invalidLocation = -999999;

    // Constantes Navegação entre atividades
    int wasMain = 0;
    int wasAddAcc = 1;
    int wasAddMan = 2;
    int wasSearchAcc = 3;
    int wasSearchMan = 4;
    int wasAddImageLocAcc = 5;
    int wasAddImageLocMan = 6;
    int wasAccountSettings = 7;
    int wasAddImageAccount = 8;

    // Constantes ultimo botao pressionado
    int wasButtonAddAcc                 = 0;
    int wasButtonAddMan                 = 1;
    int wasButtonSearchAcc              = 2;
    int wasButtonSearchMan              = 3;
    int wasButtonAddImageAcc            = 4;
    int wasButtonAddImageMan            = 5;
    int wasButtonMapa                   = 6;
    int wasButtonAccountSettings        = 7;
    int wasButtonLogout                 = 8;
    int wasButtonBackMenu               = 9;

    // Linguagem
    String EN = "en";
    String PT = "pt";
    String ES = "es";
    String FR = "fr";
    String ZH = "zh";
    String AR = "ar";
    String HI = "hi";
    String IN = "in";
    String RU = "ru";
}