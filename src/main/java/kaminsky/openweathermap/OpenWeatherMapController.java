package kaminsky.openweathermap;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapController
{
    @FXML
    TextField locationInput;

    @FXML
    Label currentTemp;

    @FXML
    ImageView currentIcon;

    @FXML
    List<Label> forecastList;

    @FXML
    List<ImageView> iconList;

    @FXML
    ComboBox<String> degrees;

    @FXML
    Label errorLabel;

    OpenWeatherMapServiceFactory factory = new OpenWeatherMapServiceFactory();//test???
    OpenWeatherMapService service;

    public void initialize()
    {
        service = factory.newInstance();
        degrees.getSelectionModel().select(1);
    }

    public void getWeather()
    {
        clearAll();
        String degreeChoice = degrees.getValue().equals("Fahrenheit") ? "imperial" : "metric";

        Disposable disposableFeed = service.getCurrentWeather(locationInput.getText(), degreeChoice)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(this::onOpenWeatherMapFeed, this::onError);

        Disposable disposableForecast = service.getWeatherForecast(locationInput.getText(), degreeChoice)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(this::onOpenWeatherMapForecast, this::onError);
    }

    public void onOpenWeatherMapFeed(OpenWeatherMapFeed feed)
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                onOpenWeatherMapFeedRun(feed);
            }
            });
    }

    public void onOpenWeatherMapFeedRun(OpenWeatherMapFeed feed)
    {
        currentTemp.setText(Double.toString(feed.main.temp) + new String(Character.toChars(0x00B0))
                + degrees.getValue().charAt(0));
        currentIcon.setImage(new Image(feed.weather.get(0).getIconUrl()));
    }

    public void onOpenWeatherMapForecast(OpenWeatherMapForecast forecast)
    {

        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                onOpenWeatherMapForecastRun(forecast);
            }
        });
    }

    public void onOpenWeatherMapForecastRun(OpenWeatherMapForecast forecast)
    {
        for (int day = 0; day < 5; day++)
        {
            OpenWeatherMapForecast.HourlyForecast hourlyForecast = forecast.getForecastFor(day+1);
            String date = (hourlyForecast.getDate().toString()).split(" 11")[0];
            forecastList.get(day).setText(date + ": " + Double.toString(hourlyForecast.main.temp)
                    + new String(Character.toChars(0x00B0)) + degrees.getValue().charAt(0));
            iconList.get(day).setImage(new Image(hourlyForecast.weather.get(0).getIconUrl()));
        }
    }

    public void onError(Throwable throwable)
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                onErrorRun(throwable);
            }
        });

    }

    public void onErrorRun(Throwable throwable)
    {
        errorLabel.setText("Please enter a valid location");
    }

    public void clearAll()
    {
        errorLabel.setText("");
    }
}