package view;

import common.Constants;
import common.Direction;
import common.DirtType;
import common.Interfaces;
import common.SimulationState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Sol kontrol paneli. Kullanıcı girişlerini alır ve ISimulationController metodlarına iletir.
// SimulationObserver olarak kayıtlıdır. onStateChanged() ile kendi UI bileşenlerini günceller.
public class ControlPanel extends VBox implements Interfaces.SimulationObserver {

    private final Interfaces.ISimulationController controller;

    private Button            btnStart, btnPause, btnReset, btnReturnStation;
    private ComboBox<String>  comboAlgorithm;
    private Slider            sliderSpeed;
    private Label             lblSpeedValue;
    private Label             lblRobotPos, lblRobotDir, lblRobotBattery;
    private ComboBox<Integer> comboManualBattery;
    private ToggleGroup       toolGroup;
    private RadioButton       rbObstacle, rbDust, rbLiquid, rbStain;

    public ControlPanel(Interfaces.ISimulationController controller) {
        this.controller = controller;
        setPrefWidth(Constants.CONTROL_PANEL_WIDTH);
        setPadding(new Insets(12));
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #E0E0E0; -fx-border-color: #BDBDBD; -fx-border-width: 0 1 0 0;");
        initUI();
    }

    private void initUI() {
        // Simülasyon kontrol butonları
        addLabel("Simülasyon Kontrolleri");

        btnStart = mkBtn("▶ Başlat");
        btnStart.setOnAction(e -> controller.startSimulation());

        btnPause = mkBtn("⏸ Duraklat");
        btnPause.setDisable(true);
        btnPause.setOnAction(e -> controller.pauseSimulation());

        btnReset = mkBtn("↺ Sıfırla");
        btnReset.setOnAction(e -> controller.resetSimulation());

        btnReturnStation = mkBtn("⚡ İstasyona Dön");
        btnReturnStation.setOnAction(e -> controller.returnToStation());

        // Sol tıkta ne ekleneceğini seçen araç grubu
        getChildren().add(new Separator());
        addLabel("Ekleme Aracı (Sol Tık)");

        toolGroup  = new ToggleGroup();
        rbObstacle = mkRadio("Mobilya (Engel)", toolGroup, true);
        rbDust     = mkRadio("Toz Kiri — 1 geçiş",   toolGroup, false);
        rbLiquid   = mkRadio("Sıvı Kiri — 2 geçiş",  toolGroup, false);
        rbStain    = mkRadio("Leke Kiri — 3 geçiş",  toolGroup, false);
        VBox toolBox = new VBox(6, rbObstacle, rbDust, rbLiquid, rbStain);
        toolBox.setPadding(new Insets(0, 0, 0, 10));

        // Robot anlık durum göstergesi
        getChildren().add(new Separator());
        addLabel("Robot Durumu");
        lblRobotPos     = new Label("Konum: (0, 0)");
        lblRobotDir     = new Label("Yön: Doğu (→)");
        lblRobotBattery = new Label("Batarya: %100");
        lblRobotBattery.setStyle("-fx-font-weight:bold;-fx-text-fill:" + Constants.COLOR_BATTERY_HIGH);
        VBox statusBox = new VBox(5, lblRobotPos, lblRobotDir, lblRobotBattery);
        statusBox.setPadding(new Insets(0, 0, 0, 10));

        // Algoritma ve hız ayarları
        getChildren().add(new Separator());
        addLabel("Ayarlar");

        Label lblAlgo = new Label("Algoritma:");
        comboAlgorithm = new ComboBox<>();
        comboAlgorithm.getItems().addAll(
                Constants.ALGO_RANDOM, Constants.ALGO_SPIRAL, Constants.ALGO_WALL_FOLLOW);
        comboAlgorithm.setValue(Constants.ALGO_RANDOM);
        comboAlgorithm.setMaxWidth(Double.MAX_VALUE);
        comboAlgorithm.setOnAction(e -> controller.setAlgorithm(comboAlgorithm.getValue()));

        lblSpeedValue = new Label(Constants.DEFAULT_SPEED + "x");
        lblSpeedValue.setStyle("-fx-font-weight:bold;");
        HBox speedRow = new HBox(6, new Label("Robot Hızı:"), lblSpeedValue);

        sliderSpeed = new Slider(Constants.MIN_SPEED, Constants.MAX_SPEED, Constants.DEFAULT_SPEED);
        sliderSpeed.setShowTickMarks(true);
        sliderSpeed.setMajorTickUnit(0.5);
        sliderSpeed.setBlockIncrement(0.5);
        // Slider değişince 0.5 katlarına yuvarla ve Controller'a bildir
        sliderSpeed.valueProperty().addListener((obs, ov, nv) -> {
            double r = Math.round(nv.doubleValue() * 2) / 2.0;
            sliderSpeed.setValue(r);
            lblSpeedValue.setText(r + "x");
            controller.setSpeed(r);
        });

        // Manuel batarya ayarı: açılır listeden seviye seçilir
        Label lblBatLabel = new Label("Manuel Batarya:");
        comboManualBattery = new ComboBox<>();
        for (int i = 10; i <= 100; i += 10) comboManualBattery.getItems().add(i);
        comboManualBattery.setValue(100);
        Button btnSetBat = new Button("Ayarla");
        btnSetBat.setOnAction(e -> controller.setBattery(comboManualBattery.getValue()));
        HBox batRow = new HBox(6, comboManualBattery, btnSetBat);

        getChildren().addAll(
                btnStart, btnPause, btnReset, btnReturnStation,
                toolBox, statusBox,
                lblAlgo, comboAlgorithm,
                speedRow, sliderSpeed,
                lblBatLabel, batRow
        );
    }

    // GridView bu metodu fare tıklamasında çağırır. Seçili araca göre aksiyon alınır.
    public String getSelectedTool() {
        if (rbDust.isSelected())   return "DUST";
        if (rbLiquid.isSelected()) return "LIQUID";
        if (rbStain.isSelected())  return "STAIN";
        return "OBSTACLE";
    }

    // ── Observer metotları ────────────────────────────────────────

    @Override
    public void onStateChanged(SimulationState state) {
        Platform.runLater(() -> {
            boolean run    = state.isRunning();
            boolean paused = state.isPaused();
            btnStart.setDisable(run && !paused);
            btnPause.setDisable(!run || paused);

            lblRobotPos.setText(
                    String.format("Konum: (%d, %d)", state.getRobotX(), state.getRobotY()));
            lblRobotDir.setText("Yön: " + state.getDirection().getDisplayName());
            lblRobotBattery.setText("Batarya: %" + state.getBattery());
            lblRobotBattery.setStyle(
                    "-fx-font-weight:bold;-fx-text-fill:" + state.getBatteryColor());

            String active = state.getActiveAlgorithm();
            if (active != null && !comboAlgorithm.getValue().equals(active))
                comboAlgorithm.setValue(active);
        });
    }

    @Override public void onSimulationStarted() {
        Platform.runLater(() -> { btnStart.setDisable(true); btnPause.setDisable(false); });
    }

    @Override public void onSimulationPaused() {
        Platform.runLater(() -> { btnStart.setDisable(false); btnPause.setDisable(true); });
    }

    @Override public void onSimulationReset() {
        Platform.runLater(() -> {
            btnStart.setDisable(false);
            btnPause.setDisable(true);
            comboAlgorithm.setValue(Constants.ALGO_RANDOM);
            sliderSpeed.setValue(Constants.DEFAULT_SPEED);
            lblSpeedValue.setText(Constants.DEFAULT_SPEED + "x");
            comboManualBattery.setValue(100);
            rbObstacle.setSelected(true);
            lblRobotPos.setText("Konum: (0, 0)");
            lblRobotDir.setText("Yön: Doğu (→)");
            lblRobotBattery.setText("Batarya: %100");
            lblRobotBattery.setStyle(
                    "-fx-font-weight:bold;-fx-text-fill:" + Constants.COLOR_BATTERY_HIGH);
        });
    }

    @Override public void onCleaningComplete(int s) {
        Platform.runLater(() -> { btnStart.setDisable(true); btnPause.setDisable(true); });
    }

    @Override public void onRobotMoved(int x, int y, Direction d)  {}
    @Override public void onCellCleaned(int x, int y, DirtType t)  {}
    @Override public void onBatteryLow(int b)                      {}
    @Override public void onRobotReachedStation()                   {}
    @Override public void onChargingComplete()                      {}

    // ── Yardımcı ─────────────────────────────────────────────────

    private Button mkBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private RadioButton mkRadio(String text, ToggleGroup g, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(g);
        rb.setSelected(selected);
        return rb;
    }

    private void addLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
        getChildren().add(l);
    }
}
