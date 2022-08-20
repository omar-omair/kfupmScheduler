package kfupm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application {
    public void start(Stage stage) throws Exception {
        AnchorPane loader = (AnchorPane) FXMLLoader.load(getClass().getResource("page.fxml"));
        Scene scene = new Scene(loader);
        stage.setScene(scene);
        stage.setTitle(" KFUPM scheduler");
        stage.sizeToScene();
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon.png")));
        stage.show();
    }
    

    public static void main(String[] args) throws Exception {
        launch();
        

}
}