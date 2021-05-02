package kaminsky.openweathermap;

import java.util.Date;

public class OpenWeatherMapFeed
{
    Main main;
    String name;
    long dt;

    public static class Main
    {
        double temp;

        public double getTemp()
        {
            return temp;
        }
    }

    // write javadoc?
    public Date getTime()
    {
        return new Date(dt * 1000);
    }
}
