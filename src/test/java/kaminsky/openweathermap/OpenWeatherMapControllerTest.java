package kaminsky.openweathermap;

import io.reactivex.rxjava3.disposables.Disposable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
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
    private ComboBox<String> degrees;
    private Label errorLabel;
    private TextField locationInput;

    @BeforeClass
    public static void beforeClass() {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
    }

    @Test
    public void initialize()
    {
        // given
        controller = new OpenWeatherMapController();
        OpenWeatherMapService service = mock(OpenWeatherMapService.class);
        controller.service = service;
        OpenWeatherMapServiceFactory factory = mock(OpenWeatherMapServiceFactory.class);
        controller.factory = factory;
        degrees = mock(ComboBox.class);
        controller.degrees = degrees;

        doReturn(service).when(factory).newInstance();
        SingleSelectionModel<String> selectionModel = mock(SingleSelectionModel.class);
        doReturn(selectionModel).when(degrees).getSelectionModel();

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
        controller = new OpenWeatherMapController();
        degrees = mock(ComboBox.class);
        controller.degrees = degrees;
        errorLabel = mock(Label.class);
        controller.errorLabel = errorLabel;
        OpenWeatherMapService service = mock(OpenWeatherMapService.class);
        controller.service = service;
        TextField locationInput = mock(TextField.class);
        controller.locationInput = locationInput;
        Disposable disposable = mock(Disposable.class);
        doReturn("Fahrenheit").when(degrees).getValue();

        // when
        controller.getWeather();

        // then
        Assert.assertEquals("Fahrenheit", degrees.getValue());
        verify(errorLabel).setText("");

    }

    @Test
    public void onOpenWeatherMapFeedRun()
    {
        // given
        controller = new OpenWeatherMapController();
        degrees = mock(ComboBox.class);
        controller.degrees = degrees;
        Label currentTemp = mock(Label.class);
        controller.currentTemp = currentTemp;
        OpenWeatherMapFeed feed = mock(OpenWeatherMapFeed.class);
        feed.main = mock(OpenWeatherMapFeed.Main.class);
        feed.main.temp = 80; // cannot mock double
        ImageView currentIcon = mock(ImageView.class);
        controller.currentIcon = currentIcon;
        feed.weather = Arrays.asList(
                mock(OpenWeatherMapFeed.Weather.class),
                mock(OpenWeatherMapFeed.Weather.class));

        doReturn("Fahrenheit").when(degrees).getValue();
        doReturn("47.55 °F").when(currentTemp).getText();
        doReturn("http://openweathermap.org/img/wn/04n@2x.png").when(feed.weather.get(0)).getIconUrl();
        Image image = new Image(feed.weather.get(0).getIconUrl());

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
        controller = new OpenWeatherMapController();
        degrees = mock(ComboBox.class);
        controller.degrees = degrees;
        List<Label> forecastList = Arrays.asList(
                mock(Label.class),
                mock(Label.class));
        controller.forecastList = forecastList;
        OpenWeatherMapForecast forecast = mock(OpenWeatherMapForecast.class);
        OpenWeatherMapForecast.HourlyForecast hourlyForecast
                = mock(OpenWeatherMapForecast.HourlyForecast.class);
        List<ImageView> iconList = Arrays.asList(
                mock(ImageView.class),
                mock(ImageView.class));
        controller.iconList = iconList;
        hourlyForecast.weather = Arrays.asList(
                mock(OpenWeatherMapForecast.HourlyForecast.Weather.class),
                mock(OpenWeatherMapForecast.HourlyForecast.Weather.class));
        hourlyForecast.main = mock(OpenWeatherMapForecast.HourlyForecast.Main.class);

        doReturn(hourlyForecast).when(forecast).getForecastFor(anyInt());
        doReturn(forecast.getForecastFor(anyInt()).getDate()).when(hourlyForecast).getDate();
        doReturn(new Date()).when(hourlyForecast).getDate();

        doReturn("Wed May 05: 47.55 °F").when(forecastList.get(0)).getText();
        doReturn("Thu May 06: 39.38 °F").when(forecastList.get(1)).getText();
        doReturn("Fahrenheit").when(degrees).getValue();
        String date = (hourlyForecast.getDate().toString()).split(" 11")[0];

        doReturn("http://openweathermap.org/img/wn/04n@2x.png").
                    when(hourlyForecast.weather.get(0)).getIconUrl();

        // when
        controller.onOpenWeatherMapForecastRun(forecast);

        // then
        verify(forecastList.get(0)).setText(date + ": "
                + Double.toString(forecast.getForecastFor(0).main.temp)
                + new String(Character.toChars(0x00B0))
                + degrees.getValue().charAt(0));

        verify(iconList.get(0), times(1)).setImage(any(Image.class));
    }

    @Test
    public void onErrorRun()
    {
        // given
        controller = new OpenWeatherMapController();
        errorLabel = mock(Label.class);
        controller.errorLabel = errorLabel;
        Label currentTemp = mock(Label.class);
        controller.currentTemp = currentTemp;
        ImageView currentIcon = mock(ImageView.class);
        controller.currentIcon = currentIcon;
        List<Label> forecastList = Arrays.asList(
                mock(Label.class),
                mock(Label.class));
        controller.forecastList = forecastList;
        List<ImageView> iconList = Arrays.asList(
                mock(ImageView.class),
                mock(ImageView.class));
        controller.iconList = iconList;

        Throwable throwable = mock(Throwable.class);

        // when
        controller.onErrorRun(throwable);

        // then
        verify(errorLabel).setText("Please enter a valid location");
        verify(currentTemp).setText("");
        verify(currentIcon).setImage(null);
        verify(forecastList.get(0)).setText("");
        verify(iconList.get(0)).setImage(null);
    }
}
