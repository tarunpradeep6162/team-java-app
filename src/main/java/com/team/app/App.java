package com.team.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        String version =
                System.getenv().getOrDefault("APP_VERSION", "development");

        HttpServer server =
                HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/", exchange -> {

            String response =
                    "Team Java Application\n" +
                    "Version: " + version + "\n" +
                    "Status: Running successfully\n";

            sendResponse(exchange, response, 200);
        });

        server.createContext("/health", exchange -> {
            sendResponse(
                    exchange,
                    "UP\n",
                    200
            );
        });

        server.setExecutor(null);
        server.start();

        System.out.println(
                "Team Java App " + version +
                " started on port " + port
        );
    }

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            int statusCode
    ) throws IOException {

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(bytes);
        }
    }
}
