package com.aluracursos.ScreenMatch.service;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class ConsultaGemini {

    private static final String API_KEY = System.getenv("API_KEY_GEMINI");
    public static String obtenerTraduccion(String texto) {
        String modelo = "gemini-2.0-flash-lite";
        String prompt = "Traduce el siguiente texto al español: " + texto;

        // Verifica si la API Key está configurada
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("Error: La variable de entorno API_KEY_GEMINI no está configurada.");
            return null;
        }

        Client cliente = new Client.Builder().apiKey(API_KEY).build();

        try {
            GenerateContentResponse respuesta = cliente.models.generateContent(
                    modelo,
                    prompt,
                    null // Parámetro para configuraciones adicionales
            );

            if (!respuesta.text().isEmpty()) {
                return respuesta.text();
            }
        } catch (Exception e) {
            System.err.println("Error al llamar a la API de Gemini para traducción: " + e.getMessage());
        }

        return null;
    }
}