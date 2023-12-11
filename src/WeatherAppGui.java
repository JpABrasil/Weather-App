import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {

   private JSONObject weatherData;

    public WeatherAppGui(){
      //Setup gui and tittle
      super("Weather App");

      //Configure GUi to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

      //Set the size of our gui
      setSize(450,650);

      //Load our gui at the center
        setLocationRelativeTo(null);

      //Make layout null to manually position our components
      setLayout(null);

      //prevent any resize of our gui
      setResizable(false);

      addGuiComponents();
    }

    private void addGuiComponents(){
        //Search field
        JTextField searchTextField = new JTextField();
        //Set the location and size of our components
        searchTextField.setBounds(15,15,351,45);
        //change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN,24));
        add(searchTextField);

        //Weather Image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //Temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        //Center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //Weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment((SwingConstants.CENTER));
        add(weatherConditionDesc);

        //Humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        //Humidity text
            //We use html here to make only part of the text bold
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%<html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityText);

        //WindSpeed image
        JLabel windSpeed = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeed.setBounds(220,500,75,66);
        add(windSpeed);

        //Wind Speed text
        JLabel windSpeedText = new JLabel("<html><b>Wind Speed<b> 32km/h<html>");
        windSpeedText.setBounds(310,500,85,55);
        windSpeed.setFont(new Font("Dialog",Font.PLAIN,16));
        add(windSpeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //Change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();

                //validate input - remove whitespace to ensure non-empty text
                if(userInput.replaceAll(" \\s", "").length() <= 0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //Depeding on the condition, we update the weather image
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;

                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;

                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;

                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //update humidity text
                long humidity  = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity<b>"+humidity+"%<html");

                //update windspeed text
                double windspeed  = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed<b>"+windspeed+"km/h%<html");
            }
        });
        add(searchButton);

    }
    //Used to create images in our gui components
    private ImageIcon loadImage(String resourcePath){
        try{
            //read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //returns an image icon so that our component can render it
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.print("Could not find resource");
        return null;
    }
}
