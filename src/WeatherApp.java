//retrieve weather data from API

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.server.ExportException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    //fetch data for given location
    public static JSONObject getWeatherData(String locationName){
        //get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //Build api request url with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
            "latitude=" + latitude + "&longitude=" + longitude +
            "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FFortaleza";

        try{
            //call api and get responde
            HttpURLConnection conn = fetchApiResponse(urlString);
            //Check for response status
            //200 - means that connection was successful
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            //Store result in json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }
            
            //close scanner
            scanner.close();

            //Close url connection
            conn.disconnect();

            //parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //we want to get the current hour's data
            //so we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temperature
            JSONArray  temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("relative_humidity", relativeHumidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Retrieves geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName){
        //replace any whitespace in location name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        try{
            //Call api
            HttpURLConnection conn = fetchApiResponse(urlString);

            //Check response status
            //200 mean successful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connecto to the API");
                return null;
            }
            else{
                //Store the API results
                StringBuilder resultJason = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while(scanner.hasNext()){
                    resultJason.append(scanner.nextLine());
                }

                //Close scanner
                scanner.close();

                //CLose url connection
                conn.disconnect();

                //Parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJason));

                //get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //Couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //Attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }
        //could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        //iterate through the time list and see  which one matches our current time
        for(int i = 0; i < timeList.size();i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)) {
                //return the index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format ate to be year-month-dayThour:minute(this is how the API read)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print the current dat time
        String formattedDataTime = currentDateTime.format(formatter);

        return formattedDataTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition ="";
        if(weatherCode==0L){
            weatherCondition = "Clear";
        }
        else if(weatherCode <=3L && weatherCode>0L){
            weatherCondition = "Cloudy";
        }
        else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode<=99L)){
            weatherCondition="Rain";
        }
        else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";

        }
        return weatherCondition;
    }
}


