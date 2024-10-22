package org.gprojetos.api;

import org.gprojetos.gui.InterfaceGrafica;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class API {

    private static API instance;
    private String usuarioLogado;

    private API() {}

    public static API getInstance() {
        if (instance == null) {
            instance = new API();
        }
        return instance;
    }

    public boolean autenticarUsuario(String usuario, String senha) {
        try {
            String jsonInputString = "{\"username\": \"" + usuario + "\", \"password\": \"" + senha + "\"}";

            URL url = new URL("http://localhost:8080/api/images/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Resposta da API: " + response.toString());
                }

                usuarioLogado = usuario;
                return true;
            } else {
                System.err.println("Erro no login. Código de resposta: " + responseCode);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean registrarUsuario(String usuario, String senha, String nome, String email) {
        try {
            String jsonInputString = "{\"username\": \"" + usuario + "\", " +
                    "\"password\": \"" + senha + "\", " +
                    "\"name\": \"" + nome + "\", " +
                    "\"email\": \"" + email + "\"}";

            URL url = new URL("http://localhost:8080/api/images/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Resposta da API: " + response.toString());
                }
                return true;
            } else {
                System.err.println("Erro ao registrar usuário. Código de resposta: " + responseCode);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}