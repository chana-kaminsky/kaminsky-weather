package kaminsky.openweathermap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenWeatherMapApplication extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        OpenWeatherMapService service = new OpenWeatherMapServiceFactory().newInstance();
        OpenWeatherMapController controller = new OpenWeatherMapController(service);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/openweathermap_application.fxml"));
        loader.setController(controller);
        Parent parent = loader.load();
        parent.setStyle("-fx-background-color: WHITE;");
        Scene scene = new Scene(parent, 900, 500);

        stage.setTitle("Weather");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
