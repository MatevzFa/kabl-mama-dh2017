import java.util.Scanner;
import java.net.*;
import java.io.*;

import java.util.Date;


public class Weather {
    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(date);

        
        if (isWeather()) {
            System.out.println("Vreme OK");
        } else {
            System.out.println("Vreme ni OK");
        }
    }
    
    public static boolean isWeather() {
        double lat = 46.049881; // Večna pot 113. Približno.
        double lon = 14.468376;
        
        return weatherOK("http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&APPID=f710ee949d8fd218dae00ddfb3989d60");
    }

    public static boolean weatherOK(String url) {
        String json_data = getHTMLfromURL(url);
        Scanner sc = new Scanner(json_data);
        System.out.println("json:\n" + json_data);
        sc.useDelimiter("\"|\r\n");
        int humidity = 100;
        while(sc.hasNext()) {
            String a = sc.next();
            if (a.equals("humidity")) {
                String num_string = sc.next();
                String number = num_string.substring(1, num_string.length() - 1); // Remove ":" and ","
                humidity = Integer.parseInt(number);
                System.out.println("Vlažnost: " + humidity);
                break;
            }
        }
        return humidity < 85;
    }

    public static String getHTMLfromURL(String url) {
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return content;
    }
}
