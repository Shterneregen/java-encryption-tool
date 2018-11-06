package com.fastorx;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

class Utils {

    static void saveToFile(String str) throws IOException {
        String[] lines = str.split(",");
        File file = new File(getCurrentTime() + ".txt");
        file.createNewFile();
//        Files.write(file.toPath(), str.getBytes("UTF-8"));
        Files.write(file.toPath(), Arrays.asList(lines));
    }

    static String getStringFromReader(Reader reader) throws IOException {
        final int BUFFER_SIZE = 4096;
        char[] buffer = new char[BUFFER_SIZE];
        Reader bufferedReader = new BufferedReader(reader, BUFFER_SIZE);
        StringBuilder builder = new StringBuilder();
        int length = 0;
        while ((length = bufferedReader.read(buffer, 0, BUFFER_SIZE)) != -1) {
            builder.append(buffer, 0, length);
        }
        reader.close();

        return builder.toString();
    }

    private static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("dd-MM-yy-HH-mm-ss");
        return currentTime.format(cal.getTime());
    }

    private static void get(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
    }
}
