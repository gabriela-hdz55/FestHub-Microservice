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
    // CONCERT EVENT MENU
    // ============================================================

    private static void concertEventMenu() {

        while (true) {

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "         CONCERT EVENTS"
            );
            System.out.println(
                    "================================"
            );

            System.out.println("1. Create Event");
            System.out.println("2. Read Event");
            System.out.println("3. Update Event");
            System.out.println("4. Delete Event");
            System.out.println("5. List Events");
            System.out.println("0. Back");

            System.out.println();
            System.out.print("Choose an option: ");

            String choice =
                    SCANNER.nextLine().trim();

            switch (choice) {

                case "1" ->
                        createConcertEvent();

                case "2" ->
                        readConcertEvent();

                case "3" ->
                        updateConcertEvent();

                case "4" ->
                        deleteConcertEvent();

                case "5" ->
                        listConcertEvents();

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
    // CREATE EVENT
    // ============================================================

    private static void createConcertEvent() {

        System.out.println();
        System.out.println(
                "--- Create Concert Event ---"
        );

        try {

            System.out.print(
                    "Event UUID (blank to generate): "
            );
            String id = SCANNER.nextLine().trim();

            System.out.print("Name: ");
            String name = SCANNER.nextLine().trim();

            System.out.print("Description: ");
            String description = SCANNER.nextLine().trim();

            System.out.print("Available tickets: ");
            int availableTickets =
                    Integer.parseInt(SCANNER.nextLine().trim());

            System.out.print("Ticket price in dollars: ");

            /*
             * Convert dollars to cents exactly.
             * Fractional cents are rejected.
             */
            long ticketPriceCents =
                    new BigDecimal(SCANNER.nextLine().trim())
                            .movePointRight(2)
                            .longValueExact();

            System.out.print(
                    "Artist UUIDs (comma-separated; blank for none): "
            );
            String artistInput = SCANNER.nextLine().trim();

            List<String> artistIds =
                    artistInput.isBlank()
                            ? List.of()
                            : Arrays.stream(artistInput.split(",", -1))
                                    .map(String::trim)
                                    .map(UUID::fromString)
                                    .map(UUID::toString)
                                    .toList();

            System.out.print(
                    "Category UUIDs (comma-separated; blank for none): "
            );
            String categoryInput = SCANNER.nextLine().trim();

            List<String> categoryIds =
                    categoryInput.isBlank()
                            ? List.of()
                            : Arrays.stream(categoryInput.split(",", -1))
                                    .map(String::trim)
                                    .map(UUID::fromString)
                                    .map(UUID::toString)
                                    .toList();

            /*
             * Build the JSON fields.
             * Leave out the ID when automatic generation is requested.
             */
            Map<String, Object> fields = new HashMap<>();

            fields.put("name", name);
            fields.put("description", description);
            fields.put("availableTickets", availableTickets);
            fields.put("ticketPriceCents", ticketPriceCents);
            fields.put("artistIds", artistIds);
            fields.put("categoryIds", categoryIds);

            if (!id.isBlank()) {
                fields.put("id", UUID.fromString(id).toString());
            }

            String requestBody =
                    MAPPER.writeValueAsString(fields);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL + "/api/events"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofString(
                                            requestBody
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 201) {

                System.out.println();
                System.out.println(
                        "Event created successfully."
                );

                printConcertEvent(
                        MAPPER.readTree(response.body())
                );

            } else {

                printError(response);
            }

        } catch (IllegalArgumentException | ArithmeticException exception) {

            System.out.println(
                    "Invalid input: " + exception.getMessage()
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            printRequestFailure(exception);

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // READ EVENT
    // ============================================================

    private static void readConcertEvent() {

        System.out.println();
        System.out.println(
                "--- Read Concert Event ---"
        );

        System.out.print("Event ID: ");
        String id = SCANNER.nextLine().trim();

        try {

            id = UUID.fromString(id).toString();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL + "/api/events/" + id
                                    )
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {

                System.out.println();

                printConcertEvent(
                        MAPPER.readTree(response.body())
                );

            } else {

                printError(response);
            }

        } catch (IllegalArgumentException exception) {

            System.out.println(
                    "Enter a valid event UUID."
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            printRequestFailure(exception);

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // UPDATE EVENT
    // ============================================================

    private static void updateConcertEvent() {

        System.out.println();
        System.out.println(
                "--- Update Concert Event ---"
        );

        System.out.println(
                "Enter all replacement values."
        );

        try {

            System.out.print("Event ID: ");
            String id =
                    UUID.fromString(
                            SCANNER.nextLine().trim()
                    ).toString();

            System.out.print("New name: ");
            String name = SCANNER.nextLine().trim();

            System.out.print("New description: ");
            String description = SCANNER.nextLine().trim();

            System.out.print("New available tickets: ");
            int availableTickets =
                    Integer.parseInt(SCANNER.nextLine().trim());

            System.out.print("New ticket price in dollars: ");
            long ticketPriceCents =
                    new BigDecimal(SCANNER.nextLine().trim())
                            .movePointRight(2)
                            .longValueExact();

            System.out.print(
                    "Artist UUIDs (comma-separated; blank for none): "
            );
            String artistInput = SCANNER.nextLine().trim();

            List<String> artistIds =
                    artistInput.isBlank()
                            ? List.of()
                            : Arrays.stream(artistInput.split(",", -1))
                                    .map(String::trim)
                                    .map(UUID::fromString)
                                    .map(UUID::toString)
                                    .toList();

            System.out.print(
                    "Category UUIDs (comma-separated; blank for none): "
            );
            String categoryInput = SCANNER.nextLine().trim();

            List<String> categoryIds =
                    categoryInput.isBlank()
                            ? List.of()
                            : Arrays.stream(categoryInput.split(",", -1))
                                    .map(String::trim)
                                    .map(UUID::fromString)
                                    .map(UUID::toString)
                                    .toList();

            String requestBody =
                    MAPPER.writeValueAsString(
                            Map.of(
                                    "name", name,
                                    "description", description,
                                    "availableTickets", availableTickets,
                                    "ticketPriceCents", ticketPriceCents,
                                    "artistIds", artistIds,
                                    "categoryIds", categoryIds
                            )
                    );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL + "/api/events/" + id
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .PUT(
                                    HttpRequest.BodyPublishers.ofString(
                                            requestBody
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {

                System.out.println();
                System.out.println(
                        "Event updated successfully."
                );

                printConcertEvent(
                        MAPPER.readTree(response.body())
                );

            } else {

                printError(response);
            }

        } catch (IllegalArgumentException | ArithmeticException exception) {

            System.out.println(
                    "Invalid input: " + exception.getMessage()
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            printRequestFailure(exception);

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // DELETE EVENT
    // ============================================================

    private static void deleteConcertEvent() {

        System.out.println();
        System.out.println(
                "--- Delete Concert Event ---"
        );

        System.out.print("Event ID: ");
        String id = SCANNER.nextLine().trim();

        System.out.print("Are you sure? (y/n): ");
        String confirmation = SCANNER.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("y")) {

            System.out.println("Delete cancelled.");
            return;
        }

        try {

            id = UUID.fromString(id).toString();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL + "/api/events/" + id
                                    )
                            )
                            .DELETE()
                            .build();

            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 204) {

                System.out.println();
                System.out.println(
                        "Event deleted successfully."
                );

            } else {

                printError(response);
            }

        } catch (IllegalArgumentException exception) {

            System.out.println(
                    "Enter a valid event UUID."
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            printRequestFailure(exception);

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // LIST EVENTS
    // ============================================================

    private static void listConcertEvents() {

        System.out.println();
        System.out.println(
                "--- Concert Events ---"
        );

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL + "/api/events"
                                    )
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                printError(response);
                return;
            }

            JsonNode events =
                    MAPPER.readTree(response.body());

            if (!events.isArray() || events.isEmpty()) {

                System.out.println(
                        "No concert events found."
                );

                return;
            }

            for (JsonNode event : events) {

                System.out.println();
                printConcertEvent(event);

                System.out.println(
                        "--------------------------------"
                );
            }

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();
            printRequestFailure(exception);

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // EVENT DISPLAY
    // ============================================================

    private static void printConcertEvent(
            JsonNode event) {

        printCategory(event);

        System.out.println(
                "Available tickets: "
                        + event.path("availableTickets").asInt()
        );

        System.out.println(
                "Ticket price:      $"
                        + BigDecimal.valueOf(
                                event.path("ticketPriceCents").asLong(),
                                2
                        ).toPlainString()
        );

        System.out.println(
                "Artist IDs:        "
                        + event.path("artistIds")
        );

        System.out.println(
                "Category IDs:      "
                        + event.path("categoryIds")
        );
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

    // ============================================================
    // ARTIST MENU
    // ============================================================

    private static void artistMenu() {

        while (true) {

            System.out.println();
            System.out.println(
                    "================================"
            );
            System.out.println(
                    "            ARTISTS"
            );
            System.out.println(
                    "================================"
            );

            System.out.println(
                    "1. Create Artist"
            );
            System.out.println(
                    "2. Read Artist"
            );
            System.out.println(
                    "3. Update Artist"
            );
            System.out.println(
                    "4. Delete Artist"
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
                        createArtist();

                case "2" ->
                        readArtist();

                case "3" ->
                        updateArtist();

                case "4" ->
                        deleteArtist();

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
    // CREATE ARTIST
    // ============================================================

    private static void createArtist() {

        System.out.println();
        System.out.println(
                "--- Create Artist ---"
        );

        System.out.print(
                "Name: "
        );

        String name =
                SCANNER.nextLine().trim();

        System.out.print(
                "Booking Contact (Email): "
        );

        String bookingContact =
                SCANNER.nextLine().trim();


        try {

            // Maps to CreateArtistRequest.java
            String requestBody =
                    MAPPER
                            .createObjectNode()
                            .put(
                                    "name",
                                    name
                            )
                            .put(
                                    "bookingContact",
                                    bookingContact
                            )
                            .toString();


            // Routes to @PostMapping("/create") in ArtistController.java
            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/artist/create"
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


            if (response.statusCode() == 200) {

                System.out.println();
                System.out.println(
                        "Artist created successfully."
                );

                JsonNode artist =
                        MAPPER.readTree(
                                response.body()
                        );

                printArtist(artist);

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // READ ARTIST
    // ============================================================

    private static void readArtist() {

        System.out.println();
        System.out.println(
                "--- Read Artist ---"
        );

        System.out.print(
                "Artist ID: "
        );

        String id =
                SCANNER.nextLine().trim();


        try {

            // Routes to @GetMapping("/read") with @RequestParam UUID id
            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/artist/read?id="
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

                JsonNode artist =
                        MAPPER.readTree(
                                response.body()
                        );

                System.out.println();

                printArtist(artist);

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // UPDATE ARTIST
    // ============================================================

    private static void updateArtist() {

        System.out.println();
        System.out.println(
                "--- Update Artist ---"
        );

        System.out.print(
                "Artist ID: "
        );

        String id =
                SCANNER.nextLine().trim();

        System.out.print(
                "New name: "
        );

        String name =
                SCANNER.nextLine().trim();

        System.out.print(
                "New booking contact: "
        );

        String bookingContact =
                SCANNER.nextLine().trim();


        try {

            // Maps to UpdateArtistRequest.java
            String requestBody =
                    MAPPER
                            .createObjectNode()
                            .put(
                                    "id",
                                    id
                            )
                            .put(
                                    "name",
                                    name
                            )
                            .put(
                                    "bookingContact",
                                    bookingContact
                            )
                            .toString();


            // Routes to @PostMapping("/update") in ArtistController.java
            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/artist/update"
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


            if (response.statusCode() == 200) {

                System.out.println();
                System.out.println(
                        "Artist updated successfully."
                );

                JsonNode artist =
                        MAPPER.readTree(
                                response.body()
                        );

                printArtist(artist);

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // DELETE ARTIST
    // ============================================================

    private static void deleteArtist() {

        System.out.println();
        System.out.println(
                "--- Delete Artist ---"
        );

        System.out.print(
                "Artist ID: "
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

            // Routes to @DeleteMapping("/delete") with @RequestParam UUID id
            HttpRequest request =
                    HttpRequest
                            .newBuilder()
                            .uri(
                                    URI.create(
                                            API_URL
                                                    + "/api/artist/delete?id="
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


            if (response.statusCode() == 200) {

                System.out.println();
                System.out.println(
                        "Artist deleted successfully."
                );

            } else {

                printError(response);
            }

        } catch (Exception exception) {

            printRequestFailure(exception);
        }
    }


    // ============================================================
    // DISPLAY HELPER FOR ARTIST
    // ============================================================

    private static void printArtist(
            JsonNode artist) {

        System.out.println(
                "ID:              "
                        + artist
                        .path("id")
                        .asText()
        );

        System.out.println(
                "Name:            "
                        + artist
                        .path("name")
                        .asText()
        );

        System.out.println(
                "Booking Contact: "
                        + artist
                        .path("bookingContact")
                        .asText()
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