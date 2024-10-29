package com.example.demo;

import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class SplunkLogger {

    private static final String SPLUNK_HEC_URL = "http://localhost:8088/services/collector/event";
    private static final String SPLUNK_HEC_TOKEN = "1a611574-1730-44c7-8197-74d5ff652785";


    public static void logToSplunk(String message, String level) {
        try {
            URL url = new URL(SPLUNK_HEC_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Splunk " + SPLUNK_HEC_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = String.format(
                    "{\"event\": {\"message\": \"%s\", \"level\": \"%s\"}, \"sourcetype\": \"_json\"}",
                    message, level);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Log enviado a Splunk correctamente.");
            } else {
                System.err.println("Error al enviar log a Splunk. Código de respuesta: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
