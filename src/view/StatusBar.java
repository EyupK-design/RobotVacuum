package view;

import common.Constants;
import common.Direction;
import common.DirtType;
import common.Interfaces;
import common.SimulationState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

// Ekranın altındaki durum çubuğu. Batarya, süre ve alan istatistiklerini gösterir.
// SimulationObserver olarak kayıtlıdır. Ses efektlerini de burada yönetir.
public class StatusBar extends HBox implements Interfaces.SimulationObserver {

    private Label lblStatus;
    private Label lblTime;
    private Label lblBattery;
    private Label lblCleaned;
    private Label lblTotalArea;
    private Label lblRemainingArea;

    // Ses efektleri. Dosya bulunamazsa null kalır ve sessizce atlanır.
    private AudioClip cleanSound;
    private AudioClip chargeSound;
    private AudioClip completeSound;

    // Simülasyon duraklatıldığında ses çalmayı engellemek için bayrak
    private boolean simulationRunning = false;

    // Temizlik sesinin çok sık tekrarlanmasını önlemek için son çalma zamanı
    private long lastCleanSoundMs = 0;
    private static final long CLEAN_SOUND_COOLDOWN_MS = 800;

    public StatusBar() {
        setPrefHeight(Constants.STATUS_BAR_HEIGHT);
        setPadding(new Insets(8, 20, 8, 20));
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #ECEFF1; -fx-border-color: #BDBDBD; -fx-border-width: 1 0 0 0;");
        initUI();
        initSounds();
    }

    private void initUI() {
        lblStatus = new Label("Durum: Hazır");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #37474F;");

        lblTotalArea     = new Label("Toplam: 0 hücre");
        lblTime          = new Label("Süre: 00:00");
        lblBattery       = new Label("Batarya: %100");
        lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");
        lblCleaned       = new Label("Temizlenen: %0.0");
        lblRemainingArea = new Label("Kirli: 0");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(lblStatus, lblTotalArea, spacer,
                lblTime, lblBattery, lblCleaned, lblRemainingArea);
    }

    // Ses dosyalarını resources/sounds/ klasöründen yükler.
    // Dosya yoksa sadece konsola uyarı basar, uygulama çalışmaya devam eder.
    private void initSounds() {
        try {
            cleanSound    = loadClip("/sounds/clean.wav");
            chargeSound   = loadClip("/sounds/charge.wav");
            completeSound = loadClip("/sounds/complete.wav");
        } catch (Exception e) {
            System.err.println("UYARI: Ses dosyaları yüklenemedi. /sounds/ klasörünü kontrol edin.");
        }
    }

    private AudioClip loadClip(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) return null;
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }

    // Sesi sadece simülasyon çalışıyorsa ve cooldown süresi geçtiyse çalar
    private void playClean() {
        if (!simulationRunning) return;
        long now = System.currentTimeMillis();
        if (now - lastCleanSoundMs < CLEAN_SOUND_COOLDOWN_MS) return;
        lastCleanSoundMs = now;
        if (cleanSound != null) cleanSound.play();
    }

    private void playOnce(AudioClip clip) {
        if (clip != null) clip.play();
    }

    // ── Observer metotları ────────────────────────────────────────

    @Override
    public void onStateChanged(SimulationState state) {
        Platform.runLater(() -> {
            lblStatus.setText("Durum: " + state.getStatusMessage());
            lblTime.setText("Süre: " + state.getFormattedTime());
            lblBattery.setText("Batarya: %" + state.getBattery());
            lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + state.getBatteryColor() + ";");
            lblCleaned.setText(String.format("Temizlenen: %.1f%%", state.getCleanedPercentage()));
            lblTotalArea.setText("Toplam: " + state.getTotalCells() + " hücre");
            lblRemainingArea.setText("Kirli: " + state.getDirtyCells());
        });
    }

    @Override
    public void onSimulationStarted() {
        simulationRunning = true;
    }

    @Override
    public void onSimulationPaused() {
        // Duraklatınca ses çalmayı durdur
        simulationRunning = false;
    }

    @Override
    public void onSimulationReset() {
        simulationRunning = false;
        Platform.runLater(() -> {
            lblStatus.setText("Durum: Hazır");
            lblTime.setText("Süre: 00:00");
            lblBattery.setText("Batarya: %100");
            lblBattery.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Constants.COLOR_BATTERY_HIGH + ";");
            lblCleaned.setText("Temizlenen: %0.0");
            lblTotalArea.setText("Toplam: 0 hücre");
            lblRemainingArea.setText("Kirli: 0");
        });
    }

    @Override
    public void onCellCleaned(int x, int y, DirtType dirtType) {
        // Ses çalma Platform.runLater gerekmez, AudioClip thread-safe
        playClean();
    }

    @Override
    public void onChargingComplete() {
        Platform.runLater(() -> {
            lblStatus.setText("Durum: Şarj Tamamlandı");
            playOnce(chargeSound);
        });
    }

    @Override
    public void onCleaningComplete(int totalSeconds) {
        simulationRunning = false;
        Platform.runLater(() -> {
            lblStatus.setText("Durum: Temizlik Bitti!");
            playOnce(completeSound);
        });
    }

    @Override
    public void onBatteryLow(int batteryLevel) {
        Platform.runLater(() -> lblStatus.setText("Durum: Batarya Düşük!"));
    }

    @Override public void onRobotReachedStation() {
        Platform.runLater(() -> lblStatus.setText("Durum: İstasyona Ulaşıldı"));
    }

    @Override public void onRobotMoved(int x, int y, Direction d) {}
}
