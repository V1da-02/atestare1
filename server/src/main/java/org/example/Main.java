package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        var server = HttpServer.create(new InetSocketAddress(7000), 0);

        server.createContext("/calculate", exchange -> {
            handleOperation(exchange);
        });

        server.start();
    }

    private static void handleOperation(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            // Citirea corpului cererii JSON
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            try {
                String[] tokens = requestBody.toString().split(" ");
                if (tokens.length != 3) {
                    sendResponse(exchange, 400, "Introduceți corect operația și cele două numere.");
                    return;
                }

                String operation = tokens[0];
                int num1 = Integer.parseInt(tokens[1]);
                int num2 = Integer.parseInt(tokens[2]);
                int result;

                switch (operation) {
                    case "add":
                        result = num1 + num2;
                        break;
                    case "subtract":
                        result = num1 - num2;
                        break;
                    case "multiply":
                        result = num1 * num2;
                        break;
                    case "divide":
                        if (num2 != 0) {
                            result = num1 / num2;
                        } else {
                            sendResponse(exchange, 400, " Impartirea la 0 is bad.");
                            return;
                        }
                        break;
                    default:
                        sendResponse(exchange, 400, "Operatie necunoscuta.");
                        return;
                }

                sendResponse(exchange, 200, Double.toString(result));
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Date incorecte.");
            }
        } else {
            methodNotAllowed(exchange, "Acest endpoint acceptă doar metoda PUT.");
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseText.getBytes().length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(responseText.getBytes());
        }
    }

    private static void methodNotAllowed(HttpExchange exchange, String responseText) throws IOException {
        sendResponse(exchange, 405, responseText);
    }
}
