package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        try (var httpClient = HttpClient.newHttpClient()) {
            while (true) {
                var scanner = new Scanner(System.in);

                System.out.print("add/subtract/multiply/divide num1,num2: ");
                var input = scanner.nextLine();

                try {
                    String[] tokens = input.split(" ");
                    if (tokens.length != 3) {
                        System.out.println("Introducere gresita.");
                        continue;
                    }

                    String operation = tokens[0];
                    Integer num1 = Integer.parseInt(tokens[1]);
                    Integer num2 = Integer.parseInt(tokens[2]);

                    var request = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:7000/calculate"))
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(operation + " " + num1 + " " + num2))
                            .build();

                    var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        System.out.println(response.body());
                    } else if (response.statusCode() == 400) {
                        System.out.println("Fail: " + response.body());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Date incorecte.");
                }
            }
        }
    }
}
