package kaminsky.openweathermap;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapService
{
    @GET("/data/2.5/weather?APPID=93016f15c0b2aa6077a9d6f512c86958")
    Single<OpenWeatherMapFeed> getCurrentWeather(@Query("q") String location);
}
