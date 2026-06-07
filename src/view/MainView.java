package view;

import common.Interfaces.IRoom;
import common.Interfaces.ISimulationController;
import javafx.scene.layout.BorderPane;

// Tüm View bileşenlerini BorderPane üzerinde düzenler.
// Sol=ControlPanel, Merkez=GridView, Alt=StatusBar yerleşimi kullanılır.
public class MainView extends BorderPane {

    public MainView(IRoom room, ISimulationController controller) {
        // ControlPanel önce oluşturulur. GridView, seçili aracı buradan okur.
        ControlPanel controlPanel = new ControlPanel(controller);
        GridView     gridView     = new GridView(room, controller, controlPanel);
        StatusBar    statusBar    = new StatusBar();

        // Üç View bileşeni Controller'a Observer olarak kaydedilir
        controller.addObserver(gridView);
        controller.addObserver(controlPanel);
        controller.addObserver(statusBar);

        setLeft(controlPanel);
        setCenter(gridView);
        setBottom(statusBar);
    }
}
