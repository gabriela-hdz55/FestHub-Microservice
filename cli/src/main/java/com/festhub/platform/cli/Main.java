package com.festhub.platform.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    /*
     * The CLI never talks directly to an individual microservice.
     *
     * Default:
     *     CLI -> localhost:8080 -> Gateway
     *
     * API_URL can be overridden with an environment variable.
     */
    private static final String API_URL =
            System.getenv()
                    .getOrDefault(
                            "API_URL",
                            "http://localhost:8080"
                    );

    /*
     * Java's built-in HTTP client.
     *
     * We reuse one client for all requests.
     */
    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    /*
     * Jackson converts between JSON and Java objects/trees.
     */
    private static final ObjectMapper MAPPER =
            new ObjectMapper();

    /*
     * Used for interactive terminal input.
     */
    private static final Scanner SCANNER =
            new Scanner(System.in);


    public static void main(String[] args) {

        runMainMenu();
    }


    // ============================================================
    // MAIN MENU
    // ============================================================

    private static void runMainMenu() {

        while (true) {

            printMainMenu();

            String choice =
                    SCANNER.nextLine().trim();

            switch (choice) {

                case "1" ->
                        concertEventMenu();

                case "2" ->
                        artistMenu();

                case "3" ->
                        bookingCategoryMenu();

                case "4" ->
                        promoMediaMenu();

                case "0" -> {

                    System.out.println();
                    System.out.println(
                            "Exiting FestHub."
                    );

                    return;
                }

                default ->
                        System.out.println(
                                "\nInvalid option."
                        );
            }
        }
    }


    private static void printMainMenu() {

        System.out.println();
        System.out.println(
                "================================"
        );
        System.out.println(
                "            FESTHUB"
        );
        System.out.println(
                "================================"
        );
        System.out.println(
                "1. Concert Events"
        );
        System.out.println(
                "2. Artists"
        );
        System.out.println(
                "3. Festival Categories"
        );
        System.out.println(
                "4. Promotional Media"
        );
        System.out.println(
                "0. Exit"
        );
        System.out.println();

        System.out.print(
                "Choose an option: "
        );
    }


    // ============================================================
    // BOOKING CATEGORY MENU
    // ============================================================

    private static void bookingCategoryMenu() {

        while (true) {

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "      FESTIVAL CATEGORIES"
            );
            System.out.println(
                    "================================"
            );

            System.out.println(
                    "1. Create Category"
            );
            System.out.println(
                    "2. Read Category"
            );
            System.out.println(
                    "3. Update Category"
            );
            System.out.println(
                    "4. Delete Category"
            );
            System.out.println(
                    "5. List Categories"
            );
            System.out.println(
                    "0. Back"
            );

            System.out.println();

            System.out.print(
                    "Choose an option: "
            );

            String choice =
                    SCANNER.nextLine().trim();

            switch (choice) {

                case "1" ->
                        createBookingCategory();

                case "2" ->
                        readBookingCategory();

                case "3" ->
                        updateBookingCategory();

                case "4" ->
                        deleteBookingCategory();

                case "5" ->
                        listBookingCategories();

                case "0" -> {
                    return;
                }

                default ->
                        System.out.println(
                                "\nInvalid option."
                        );
            }
        }
    }


    // ============================================================
    // CREATE CATEGORY
    // ============================================================

    private static void createBookingCategory() {

        System.out.println();
        System.out.println(
                "--- Create Festival Category ---"
        );

        System.out.print(
                "Name: "
        );

        String name =
                SCANNER.nextLine().trim();

        System.out.print(
                "Description: "
        );

        String description =
                SCANNER.nextLine().trim();


        try {

            /*
             * Build JSON:
             *
             * {
             *   "name": "...",
             *   "description": "..."
             * }
             */
            String requestBody =
                    MAPPER
                            .createObjectNode()
                            .put(
                                    "name",
                                    name
                            )
                            .put(
                                    "description",
                                    description
                            )
                            .toString();


            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/category"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest
                                            .BodyPublishers
                                            .ofString(
                                                    requestBody
                                            )
                            )
                            .build();


            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );


            if (response.statusCode() == 201) {

                System.out.println();
                System.out.println(
                        "Category created successfully."
                );

                JsonNode category =
                        MAPPER.readTree(
                                response.body()
                        );

                printCategory(category);

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // READ CATEGORY
    // ============================================================

    private static void readBookingCategory() {

        System.out.println();
        System.out.println(
                "--- Read Festival Category ---"
        );

        System.out.print(
                "Category ID: "
        );

        String id =
                SCANNER.nextLine().trim();


        try {

            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/category/"
                                                    + id
                                    )
                            )
                            .GET()
                            .build();


            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );


            if (response.statusCode() == 200) {

                JsonNode category =
                        MAPPER.readTree(
                                response.body()
                        );

                System.out.println();

                printCategory(category);

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // UPDATE CATEGORY
    // ============================================================

    private static void updateBookingCategory() {

        System.out.println();
        System.out.println(
                "--- Update Festival Category ---"
        );

        System.out.print(
                "Category ID: "
        );

        String id =
                SCANNER.nextLine().trim();

        System.out.print(
                "New name: "
        );

        String name =
                SCANNER.nextLine().trim();

        System.out.print(
                "New description: "
        );

        String description =
                SCANNER.nextLine().trim();


        try {

            String requestBody =
                    MAPPER
                            .createObjectNode()
                            .put(
                                    "name",
                                    name
                            )
                            .put(
                                    "description",
                                    description
                            )
                            .toString();


            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/category/"
                                                    + id
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .PUT(
                                    HttpRequest
                                            .BodyPublishers
                                            .ofString(
                                                    requestBody
                                            )
                            )
                            .build();


            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );


            if (response.statusCode() == 200) {

                System.out.println();
                System.out.println(
                        "Category updated successfully."
                );

                JsonNode category =
                        MAPPER.readTree(
                                response.body()
                        );

                printCategory(category);

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // DELETE CATEGORY
    // ============================================================

    private static void deleteBookingCategory() {

        System.out.println();
        System.out.println(
                "--- Delete Festival Category ---"
        );

        System.out.print(
                "Category ID: "
        );

        String id =
                SCANNER.nextLine().trim();

        System.out.print(
                "Are you sure? (y/n): "
        );

        String confirmation =
                SCANNER.nextLine().trim();


        if (!confirmation.equalsIgnoreCase("y")) {

            System.out.println(
                    "Delete cancelled."
            );

            return;
        }


        try {

            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/category/"
                                                    + id
                                    )
                            )
                            .DELETE()
                            .build();


            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );


            if (response.statusCode() == 204) {

                System.out.println();
                System.out.println(
                        "Category deleted successfully."
                );

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // LIST CATEGORIES
    // ============================================================

    private static void listBookingCategories() {

        System.out.println();
        System.out.println(
                "--- Festival Categories ---"
        );


        try {

            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/category"
                                    )
                            )
                            .GET()
                            .build();


            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse
                                    .BodyHandlers
                                    .ofString()
                    );


            if (response.statusCode() != 200) {

                printError(response);

                return;
            }


            JsonNode categories =
                    MAPPER.readTree(
                            response.body()
                    );


            if (!categories.isArray()
                    || categories.isEmpty()) {

                System.out.println(
                        "No festival categories found."
                );

                return;
            }


            System.out.println();

            for (JsonNode category : categories) {

                printCategory(category);

                System.out.println(
                        "--------------------------------"
                );
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // DISPLAY HELPERS
    // ============================================================

    private static void printCategory(
            JsonNode category) {

        System.out.println(
                "ID:          "
                        + category
                        .path("id")
                        .asText()
        );

        System.out.println(
                "Name:        "
                        + category
                        .path("name")
                        .asText()
        );

        System.out.println(
                "Description: "
                        + category
                        .path("description")
                        .asText()
        );
    }


    private static void printError(
            HttpResponse<String> response) {

        System.out.println();
        System.out.println(
                "Request failed."
        );

        System.out.println(
                "HTTP status: "
                        + response.statusCode()
        );


        if (response.body() == null
                || response.body().isBlank()) {

            return;
        }


        try {

            JsonNode body =
                    MAPPER.readTree(
                            response.body()
                    );


            if (body.has("error")) {

                System.out.println(
                        "Error: "
                                + body
                                .get("error")
                                .asText()
                );

            } else {

                System.out.println(
                        response.body()
                );
            }

        } catch (Exception exception) {

            System.out.println(
                    response.body()
            );
        }
    }


    private static void printRequestFailure(
            Exception exception) {

        System.out.println();
        System.out.println(
                "Could not contact FestHub."
        );

        System.out.println(
                "Make sure the API Gateway is running at:"
        );

        System.out.println(
                API_URL
        );

        System.out.println(
                "Details: "
                        + exception.getMessage()
        );
    }


    // ============================================================
    // OTHER ENTITY MENUS
    //
    // These keep the same overall structure as the original
    // monolithic CLI. We will connect them to their respective
    // microservice APIs once those API contracts are finalized.
    // ============================================================

    private static void concertEventMenu() {

        showPendingMenu(
                "CONCERT EVENTS"
        );
    }


    private static void artistMenu() {

        showPendingMenu(
                "ARTISTS"
        );
    }


    private static void promoMediaMenu() {

        showPendingMenu(
                "PROMOTIONAL MEDIA"
        );
    }


    private static void showPendingMenu(
            String title) {

        while (true) {

            System.out.println();
            System.out.println(
                    "================================"
            );

            System.out.println(
                    "      " + title
            );

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "This API is not connected yet."
            );

            System.out.println();
            System.out.println(
                    "0. Back"
            );

            System.out.print(
                    "\nChoose an option: "
            );


            String choice =
                    SCANNER.nextLine().trim();


            if (choice.equals("0")) {

                return;
            }


            System.out.println(
                    "Invalid option."
            );
        }
    }
}