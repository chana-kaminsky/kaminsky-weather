package kaminsky.openweathermap;

import java.util.Date;
import java.util.List;

public class OpenWeatherMapForecast
{
    List<HourlyForecast> list;

    static class HourlyForecast
    {
        long dt;
        Main main;
        List<Weather> weather;

        static class Main
        {
            double temp;
        }

        static class Weather
        {
            String main;
            String icon;

            public String getIconUrl()
            {
                return "http://openweathermap.org/ing/en" + icon + "@2x.png";
            }
        }

        public Date getDate()
        {
            return new Date(dt * 1000);
        }

        @Override
        public String toString()
        {
            return new Date(dt * 1000) + " " +
                    main.temp + " " +
                    weather.get(0).main + " " +
                    weather.get(0).getIconUrl();
        }
    }
}
