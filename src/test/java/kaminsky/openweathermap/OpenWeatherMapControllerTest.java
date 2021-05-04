package kaminsky.openweathermap;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class OpenWeatherMapControllerTest
{
    private OpenWeatherMapController controller;
    private OpenWeatherMapService service;
    private OpenWeatherMapServiceFactory factory;
    private ComboBox<String> degrees;
    private Label errorLabel;
    private TextField locationInput;
    private Label currentTemp;
    private OpenWeatherMapFeed feed;
    private ImageView currentIcon;
    private List<Label> forecastList;
    private OpenWeatherMapForecast forecast;
    private OpenWeatherMapForecast.HourlyForecast hourlyForecast;
    private List<ImageView> iconList;
    private Date date;

    @BeforeClass
    public static void beforeClass() {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
    }


    @Test
    public void initialize()
    {
        // given
        givenControllerTest();
        doReturn(service).when(factory).newInstance();

        // when
        controller.initialize();

        // then
        verify(factory).newInstance();
        Assert.assertEquals(service, factory.newInstance());

    }

    @Test
    public void getWeather()
    {
        // given
        givenControllerTest();

        // when
        controller.getWeather();

        // then
        Assert.assertEquals("Fahrenheit", degrees.getValue());
    }

    @Test
    public void onOpenWeatherMapFeedRun()
    {
        // given
        givenControllerTest();

        doNothing().when(currentTemp).
                setText(Double.toString(feed.main.temp)
                        + new String(Character.toChars(0x00B0))
                        + degrees.getValue().charAt(0));
        doReturn("http://openweathermap.org/img/wn/04n@2x.png").when(feed.weather.get(0)).getIconUrl();
        Image image = new Image(feed.weather.get(0).getIconUrl());
        doNothing().when(currentIcon).setImage(image);

        // when
        controller.onOpenWeatherMapFeedRun(feed);

        // then
        verify(currentTemp).setText(Double.toString(feed.main.temp)
                + new String(Character.toChars(0x00B0))
                + degrees.getValue().charAt(0));
        verify(currentIcon, times(1)).setImage(any(Image.class));

    }

    @Test
    public void onOpenWeatherMapForecastRun()
    {
        // given
        givenControllerTest();
        doReturn(hourlyForecast).when(forecast).getForecastFor(anyInt());
        doReturn(forecast.getForecastFor(anyInt()).getDate()).when(hourlyForecast).getDate();
        doReturn(date).when(hourlyForecast).getDate();

        String strDate = (hourlyForecast.getDate().toString()).split(" 11")[0];

//        doNothing().when(forecastList.get(anyInt())).setText(
//                strDate + ": " + Double.toString(forecast.getForecastFor(0).main.temp)
//                + new String(Character.toChars(0x00B0)) + degrees.getValue().charAt(0));
//
        doReturn("http://openweathermap.org/img/wn/04n@2x.png").
                    when(hourlyForecast.weather.get(0)).getIconUrl();
        Image image = new Image(hourlyForecast.weather.get(0).getIconUrl());
        doNothing().when(iconList.get(0)).setImage(image);
//
//        // when
        controller.onOpenWeatherMapForecastRun(forecast);
//
//        // then
//        verify(forecastList.get(0)).setText(strDate + ": "
//                + Double.toString(forecast.getForecastFor(0).main.temp)
//                + new String(Character.toChars(0x00B0))
//                + degrees.getValue().charAt(0));
//
        verify(currentIcon, times(1)).setImage(any(Image.class));

    }

    @Test
    public void onErrorRun()
    {
        // given
        givenControllerTest();
        doNothing().when(errorLabel).setText("Please enter a valid location");
        Throwable throwable = mock(Throwable.class);

        // when
        controller.onErrorRun(throwable);

        // then
        verify(errorLabel).setText("Please enter a valid location");
    }

    @Test
    public void clearAll()
    {
        // given
        givenControllerTest();
        doNothing().when(errorLabel).setText("");

        // when
        controller.clearAll();

        // then
        verify(errorLabel).setText("");
    }

    private void givenControllerTest()
    {
        controller = new OpenWeatherMapController();
        service = mock(OpenWeatherMapService.class);
        controller.service = service;
        factory = mock(OpenWeatherMapServiceFactory.class);
        controller.factory = factory;
        controller.degrees = degrees;
        errorLabel = mock(Label.class);
        controller.errorLabel = errorLabel;
        degrees = new ComboBox(FXCollections.observableArrayList(
                Arrays.asList("Fahrenheit",
                              "Celsius")
                )); // cannot mock ComboBox or String
        degrees.setValue("Fahrenheit");
        controller.degrees = degrees;
        locationInput = mock(TextField.class);
        controller.locationInput = locationInput;
        currentTemp = mock(Label.class);
        controller.currentTemp = currentTemp;
        feed = mock(OpenWeatherMapFeed.class);
        feed.main = mock(OpenWeatherMapFeed.Main.class);
        feed.main.temp = 80; // cannot mock double
        currentIcon = mock(ImageView.class);
        controller.currentIcon = currentIcon;
        feed.weather = Arrays.asList(
                mock(OpenWeatherMapFeed.Weather.class),
                mock(OpenWeatherMapFeed.Weather.class));
        forecastList = Arrays.asList(
                mock(Label.class),
                mock(Label.class));
        forecast = mock(OpenWeatherMapForecast.class);
        hourlyForecast = mock(OpenWeatherMapForecast.HourlyForecast.class);
        iconList = Arrays.asList(
                mock(ImageView.class),
                mock(ImageView.class));
        hourlyForecast.weather = Arrays.asList(
                mock(OpenWeatherMapForecast.HourlyForecast.Weather.class),
                mock(OpenWeatherMapForecast.HourlyForecast.Weather.class));
        Date date = new Date();


    }
}
