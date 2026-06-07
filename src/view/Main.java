package view;

import common.Constants;
import controller.SimulationController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Robot;
import model.Room;

// JavaFX uygulama giriş noktası. Gerçek Room, Robot ve SimulationController oluşturur.
// Tüm View bileşenlerini Controller üzerinden birbirine bağlar.
public class Main extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        // Model nesneleri oluşturulur
        Room  room  = new Room();
        Robot robot = new Robot(Constants.CHARGING_STATION_X, Constants.CHARGING_STATION_Y);

        // Controller model nesnelerini alır ve simülasyonu yönetir
        SimulationController controller = new SimulationController(room, robot);

        // GridView ile Controller aynı room referansını paylaşır.
        // Farklı referans olursa tıklanan engel/kir ekranda görünmez.
        MainView mainView = new MainView(controller.getRoom(), controller);

        Scene scene = new Scene(mainView, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        primaryStage.setTitle("Robot Süpürge Simülasyonu — BZ 214");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
