// ============================================================
// NAVIGATION
// ============================================================

function showPanel(panelId) {

    document
        .querySelectorAll(".panel")
        .forEach(panel => {
            panel.classList.remove("active");
        });


    const panel =
        document.getElementById(panelId);


    panel.classList.add("active");


    if (panelId === "categories-panel") {
        loadCategories();
    }

    if (panelId === "media-panel") {
        loadMedia();
    }

    if (panelId === "events-panel") {
        loadEvents();
    }
}


// ============================================================
// STATUS MESSAGES
// ============================================================

function showMessage(
    text,
    type = "success"
) {

    const message =
        document.getElementById("message");


    message.textContent = text;

    message.className =
        "message " + type;


    setTimeout(
        () => {
            message.classList.add("hidden");
        },
        4000
    );
}


// ============================================================
// CATEGORY CREATE
// ============================================================

async function createCategory() {

    const name =
        document
            .getElementById(
                "category-create-name"
            )
            .value
            .trim();


    const description =
        document
            .getElementById(
                "category-create-description"
            )
            .value
            .trim();


    if (!name || !description) {

        showMessage(
            "Name and description are required.",
            "error"
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/category",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        name: name,
                        description: description
                    })
                }
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        const category =
            await response.json();


        showMessage(
            "Created category: "
            + category.name
        );


        document
            .getElementById(
                "category-create-name"
            )
            .value = "";


        document
            .getElementById(
                "category-create-description"
            )
            .value = "";


        loadCategories();

    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// CATEGORY READ
// ============================================================

async function readCategory() {

    const id =
        document
            .getElementById(
                "category-id"
            )
            .value
            .trim();


    if (!id) {

        showMessage(
            "Enter a category UUID.",
            "error"
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/category/"
                + encodeURIComponent(id)
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        const category =
            await response.json();


        document
            .getElementById(
                "category-update-name"
            )
            .value =
            category.name;


        document
            .getElementById(
                "category-update-description"
            )
            .value =
            category.description;


        document
            .getElementById(
                "category-result"
            )
            .innerHTML =
            categoryCard(category);


    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// CATEGORY UPDATE
// ============================================================

async function updateCategory() {

    const id =
        document
            .getElementById(
                "category-id"
            )
            .value
            .trim();


    const name =
        document
            .getElementById(
                "category-update-name"
            )
            .value
            .trim();


    const description =
        document
            .getElementById(
                "category-update-description"
            )
            .value
            .trim();


    if (!id || !name || !description) {

        showMessage(
            "UUID, name, and description are required.",
            "error"
        );

        return;
    }


    try {

        const response =
            await fetch(
                "/api/category/"
                + encodeURIComponent(id),
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        name: name,
                        description: description
                    })
                }
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        const category =
            await response.json();


        showMessage(
            "Updated category: "
            + category.name
        );


        document
            .getElementById(
                "category-result"
            )
            .innerHTML =
            categoryCard(category);


        loadCategories();

    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// CATEGORY DELETE
// ============================================================

async function deleteCategory() {

    const id =
        document
            .getElementById(
                "category-id"
            )
            .value
            .trim();


    if (!id) {

        showMessage(
            "Enter a category UUID.",
            "error"
        );

        return;
    }


    const confirmed =
        window.confirm(
            "Delete this category?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await fetch(
                "/api/category/"
                + encodeURIComponent(id),
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        showMessage(
            "Category deleted."
        );


        document
            .getElementById(
                "category-id"
            )
            .value = "";


        document
            .getElementById(
                "category-update-name"
            )
            .value = "";


        document
            .getElementById(
                "category-update-description"
            )
            .value = "";


        document
            .getElementById(
                "category-result"
            )
            .innerHTML = "";


        loadCategories();

    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// CATEGORY LIST
// ============================================================

async function loadCategories() {

    const list =
        document.getElementById(
            "category-list"
        );


    list.innerHTML =
        "Loading...";


    try {

        const response =
            await fetch(
                "/api/category"
            );


        if (!response.ok) {

            list.innerHTML =
                "Unable to load categories.";

            await handleError(response);

            return;
        }


        const categories =
            await response.json();


        if (!Array.isArray(categories)
            || categories.length === 0) {

            list.innerHTML =
                "No categories found.";

            return;
        }


        list.innerHTML =
            categories
                .map(categoryCard)
                .join("");


    } catch (error) {

        list.innerHTML =
            "Unable to connect to FestHub.";

        connectionFailure(error);
    }
}


// ============================================================
// CATEGORY DISPLAY
// ============================================================

function categoryCard(category) {

    return `
        <div class="record-card">

            <h4>
                ${escapeHtml(category.name)}
            </h4>

            <p>
                ${escapeHtml(category.description)}
            </p>

            <small>
                UUID:
                ${escapeHtml(category.id)}
            </small>

        </div>
    `;
}


// ============================================================
// PROMOTIONAL MEDIA
// ============================================================

async function createMedia() {

    const eventId =
        document
            .getElementById("media-create-event-id")
            .value
            .trim();

    const imageUrl =
        document
            .getElementById("media-create-url")
            .value
            .trim();

    if (!eventId || !imageUrl) {
        showMessage("Event UUID and image URL are required.", "error");
        return;
    }

    try {
        const response = await fetch("/api/media", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ eventId, imageUrl })
        });

        if (!response.ok) {
            await handleError(response);
            return;
        }

        const media = await response.json();
        showMessage("Created promotional media.");
        document.getElementById("media-create-event-id").value = "";
        document.getElementById("media-create-url").value = "";
        document.getElementById("media-id").value = media.id;
        fillMediaForm(media);
        document.getElementById("media-result").innerHTML = mediaCard(media);
        loadMedia();
    } catch (error) {
        connectionFailure(error);
    }
}


async function readMedia() {

    const id =
        document
            .getElementById("media-id")
            .value
            .trim();

    if (!id) {
        showMessage("Enter a media UUID.", "error");
        return;
    }

    try {
        const response = await fetch(
            "/api/media/" + encodeURIComponent(id)
        );

        if (!response.ok) {
            await handleError(response);
            return;
        }

        const media = await response.json();
        fillMediaForm(media);
        document.getElementById("media-result").innerHTML = mediaCard(media);
    } catch (error) {
        connectionFailure(error);
    }
}


async function updateMedia() {

    const id =
        document
            .getElementById("media-id")
            .value
            .trim();

    const eventId =
        document
            .getElementById("media-update-event-id")
            .value
            .trim();

    const imageUrl =
        document
            .getElementById("media-update-url")
            .value
            .trim();

    if (!id || !eventId || !imageUrl) {
        showMessage("Media UUID, event UUID, and image URL are required.", "error");
        return;
    }

    try {
        const response = await fetch(
            "/api/media/" + encodeURIComponent(id),
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ eventId, imageUrl })
            }
        );

        if (!response.ok) {
            await handleError(response);
            return;
        }

        const media = await response.json();
        showMessage("Updated promotional media.");
        document.getElementById("media-result").innerHTML = mediaCard(media);
        loadMedia();
    } catch (error) {
        connectionFailure(error);
    }
}


async function deleteMedia() {

    const id =
        document
            .getElementById("media-id")
            .value
            .trim();

    if (!id) {
        showMessage("Enter a media UUID.", "error");
        return;
    }

    if (!window.confirm("Delete this promotional media?")) {
        return;
    }

    try {
        const response = await fetch(
            "/api/media/" + encodeURIComponent(id),
            { method: "DELETE" }
        );

        if (!response.ok) {
            await handleError(response);
            return;
        }

        showMessage("Promotional media deleted.");
        document.getElementById("media-id").value = "";
        document.getElementById("media-update-event-id").value = "";
        document.getElementById("media-update-url").value = "";
        document.getElementById("media-result").innerHTML = "";
        loadMedia();
    } catch (error) {
        connectionFailure(error);
    }
}


async function loadMedia() {

    const list = document.getElementById("media-list");
    list.innerHTML = "Loading...";

    try {
        const response = await fetch("/api/media");

        if (!response.ok) {
            list.innerHTML = "Unable to load promotional media.";
            await handleError(response);
            return;
        }

        const mediaItems = await response.json();

        if (!Array.isArray(mediaItems) || mediaItems.length === 0) {
            list.innerHTML = "No promotional media found.";
            return;
        }

        list.innerHTML = mediaItems.map(mediaCard).join("");
    } catch (error) {
        list.innerHTML = "Unable to connect to FestHub.";
        connectionFailure(error);
    }
}


function fillMediaForm(media) {
    document.getElementById("media-update-event-id").value = media.eventId;
    document.getElementById("media-update-url").value = media.imageUrl;
}


function mediaCard(media) {
    return `
        <div class="record-card">
            <h4>Promotional Media</h4>
            <p>Event UUID: ${escapeHtml(media.eventId)}</p>
            <p>Image URL: ${escapeHtml(media.imageUrl)}</p>
            <small>UUID: ${escapeHtml(media.id)}</small>
        </div>
    `;
}


// ============================================================
// API ERRORS
// ============================================================

async function handleError(response) {

    let message =
        "Request failed with HTTP "
        + response.status;


    try {

        const body =
            await response.json();


        if (body.error) {
            message = body.error;
        }

    } catch (error) {

        // Response was not JSON.
    }


    showMessage(
        message,
        "error"
    );
}


function connectionFailure(error) {

    console.error(error);


    showMessage(
        "Could not contact FestHub.",
        "error"
    );
}


// ============================================================
// HTML SAFETY
// ============================================================

function escapeHtml(value) {

    const div =
        document.createElement("div");


    div.textContent =
        value ?? "";


    return div.innerHTML;
}

// ============================================================
// CONCERT EVENTS
// ============================================================
async function createConcertEvent() {
    const value = id =>
        document.getElementById(id).value.trim();

    const parseIds = text =>
        text.split(",").map(id => id.trim()).filter(Boolean);

    const ticketsText = value("event-create-tickets");
    const priceText = value("event-create-price");

    const availableTickets = Number(ticketsText);
    const price = Number(priceText);
    const ticketPriceCents = Math.round(price * 100);

    if (
        !ticketsText ||
        !Number.isSafeInteger(availableTickets) ||
        availableTickets < 0 ||
        !priceText ||
        !Number.isFinite(price) ||
        price <= 0 ||
        !Number.isSafeInteger(ticketPriceCents)
    ) {
        showMessage("Enter valid tickets and a positive price.", "error");
        return;
    }

    try {
        const response = await fetch("/api/events", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: value("event-create-name"),
                description: value("event-create-description"),
                availableTickets,
                ticketPriceCents,
                artistIds: parseIds(value("event-create-artist")),
                categoryIds: parseIds(value("event-create-category"))
            })
        });

        if (!response.ok) {
            await handleError(response);
            return;
        }

        const event = await response.json();

        showMessage("Created event: " + event.name);

        [
            "event-create-name",
            "event-create-description",
            "event-create-tickets",
            "event-create-price",
            "event-create-artist",
            "event-create-category"
        ].forEach(id => {
            document.getElementById(id).value = "";
        });
    } catch (error) {
        connectionFailure(error);
    }
}

function eventCard(event) {
    return `
        <div class="record-card">
            <h4>${escapeHtml(event.name)}</h4>

            <p>${escapeHtml(event.description)}</p>

            <p>
                Available tickets:
                ${escapeHtml(String(event.availableTickets))}
            </p>

            <p>
                Ticket price:
                $${(event.ticketPriceCents / 100).toFixed(2)}
            </p>

            <p>
                Artist UUIDs:
                ${escapeHtml((event.artistIds ?? []).join(", ") || "None")}
            </p>

            <p>
                Category UUIDs:
                ${escapeHtml((event.categoryIds ?? []).join(", ") || "None")}
            </p>

            <small>UUID: ${escapeHtml(event.id)}</small>
        </div>
    `;
}

function displayEvent(event) {
    document.getElementById("event-id").value = event.id;
    document.getElementById("event-update-name").value = event.name;
    document.getElementById("event-update-description").value =
        event.description;
    document.getElementById("event-update-tickets").value =
        event.availableTickets;
    document.getElementById("event-update-price").value =
        (event.ticketPriceCents / 100).toFixed(2);

    document.getElementById("event-update-artists").value =
        (event.artistIds ?? []).join(", ");
    document.getElementById("event-update-categories").value =
        (event.categoryIds ?? []).join(", ");

    const result = document.getElementById("event-result");
    result.style.whiteSpace = "";
    result.innerHTML = eventCard(event);
}

async function readConcertEvent() {
    const id = document.getElementById("event-id").value.trim();

    if (!id) {
        showMessage("Enter an event UUID.", "error");
        return;
    }

    try {
        const response = await fetch(
            "/api/events/" + encodeURIComponent(id)
        );

        if (!response.ok) {
            await handleError(response);
            return;
        }

        displayEvent(await response.json());
    } catch (error) {
        connectionFailure(error);
    }
}

async function updateConcertEvent() {
    const value = id =>
        document.getElementById(id).value.trim();

    const parseIds = text =>
        text.split(",").map(id => id.trim()).filter(Boolean);

    const id = value("event-id");
    const name = value("event-update-name");
    const description = value("event-update-description");
    const ticketsText = value("event-update-tickets");
    const priceText = value("event-update-price");

    const availableTickets = Number(ticketsText);
    const price = Number(priceText);
    const ticketPriceCents = Math.round(price * 100);

    if (!id || !name || !description) {
        showMessage(
            "Event UUID, name, and description are required.",
            "error"
        );
        return;
    }

    if (
        !ticketsText ||
        !Number.isSafeInteger(availableTickets) ||
        availableTickets < 0 ||
        !priceText ||
        !Number.isFinite(price) ||
        price <= 0 ||
        !Number.isSafeInteger(ticketPriceCents)
    ) {
        showMessage(
            "Enter valid tickets and a positive price.",
            "error"
        );
        return;
    }

    try {
        const response = await fetch(
            "/api/events/" + encodeURIComponent(id),
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name,
                    description,
                    availableTickets,
                    ticketPriceCents,
                    artistIds: parseIds(
                        value("event-update-artists")
                    ),
                    categoryIds: parseIds(
                        value("event-update-categories")
                    )
                })
            }
        );

        if (!response.ok) {
            await handleError(response);
            return;
        }

        displayEvent(await response.json());
        showMessage("Event updated.");
    } catch (error) {
        connectionFailure(error);
    }
}

async function deleteConcertEvent() {
    const id = document.getElementById("event-id").value.trim();

    if (!id) {
        showMessage("Enter an event UUID.", "error");
        return;
    }

    if (!window.confirm("Delete this event?")) {
        return;
    }

    try {
        const response = await fetch(
            "/api/events/" + encodeURIComponent(id),
            { method: "DELETE" }
        );

        if (!response.ok) {
            await handleError(response);
            return;
        }

        [
            "event-id",
            "event-update-name",
            "event-update-description",
            "event-update-tickets",
            "event-update-price",
            "event-update-artists",
            "event-update-categories"
        ].forEach(fieldId => {
            document.getElementById(fieldId).value = "";
        });

        document.getElementById("event-result").textContent = "";
        showMessage("Event deleted.");
    } catch (error) {
        connectionFailure(error);
    }
}

async function loadEvents() {
    const list = document.getElementById("event-list");
    list.textContent = "Loading...";

    try {
        const response = await fetch("/api/events");

        if (!response.ok) {
            list.textContent = "Unable to load events.";
            await handleError(response);
            return;
        }

        const events = await response.json();

        if (events.length === 0) {
            list.textContent = "No events found.";
            return;
        }

        list.innerHTML = events.map(eventCard).join("");
    } catch (error) {
        list.textContent = "Unable to connect to FestHub.";
        connectionFailure(error);
    }
}

// ============================================================
// ARTIST CREATE
// ============================================================

async function createArtist() {
    const name = document.getElementById("artist-create-name").value.trim();
    const bookingContact = document.getElementById("artist-create-contact").value.trim();

    if (!name || !bookingContact) {
        showMessage("Name and booking contact are required.", "error");
        return;
    }

    try {
        const response = await fetch("/api/artist/create", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: name, bookingContact: bookingContact })
        });

        if (!response.ok) {
            await handleError(response);
            return;
        }

        const artist = await response.json();
        showMessage("Created artist: " + artist.name);

        // Immediately displays the UUID on screen after creation
        document.getElementById("artist-result").innerHTML = artistCard(artist);
        document.getElementById("artist-create-name").value = "";
        document.getElementById("artist-create-contact").value = "";
        
        // Auto-refreshes the list at the bottom
        if (typeof loadArtists === 'function') {
            loadArtists();
        }
    } catch (error) {
        connectionFailure(error);
    }
}

// loading created artists for display
async function loadArtists() {
    const list = document.getElementById("artist-list");
    list.innerHTML = "Loading...";
    
    try {
        const response = await fetch("/api/artist/list");
        
        if (!response.ok) {
            list.innerHTML = "Unable to load artists.";
            await handleError(response);
            return;
        }
        
        const artists = await response.json();
        
        if (!Array.isArray(artists) || artists.length === 0) {
            list.innerHTML = "No artists found.";
            return;
        }
        
        list.innerHTML = artists.map(artistCard).join("");
    } catch (error) {
        list.innerHTML = "Unable to connect to FestHub.";
        connectionFailure(error);
    }
}


// ============================================================
// ARTIST READ
// ============================================================

async function readArtist() {

    const id =
        document
            .getElementById(
                "artist-id"
            )
            .value
            .trim();


    if (!id) {

        showMessage(
            "Enter an artist UUID.",
            "error"
        );

        return;
    }


    try {

        // Maps to @GetMapping("/read?id=...")
        const response =
            await fetch(
                "/api/artist/read?id="
                + encodeURIComponent(id)
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        const artist =
            await response.json();


        document
            .getElementById(
                "artist-update-name"
            )
            .value =
            artist.name;


        document
            .getElementById(
                "artist-update-contact"
            )
            .value =
            artist.bookingContact;


        document
            .getElementById(
                "artist-result"
            )
            .innerHTML =
            artistCard(artist);


    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// ARTIST UPDATE
// ============================================================

async function updateArtist() {

    const id =
        document
            .getElementById(
                "artist-id"
            )
            .value
            .trim();


    const name =
        document
            .getElementById(
                "artist-update-name"
            )
            .value
            .trim();


    const bookingContact =
        document
            .getElementById(
                "artist-update-contact"
            )
            .value
            .trim();


    if (!id || !name || !bookingContact) {

        showMessage(
            "UUID, name, and booking contact are required.",
            "error"
        );

        return;
    }


    try {

        // Maps to @PostMapping("/update") with ID in body
        const response =
            await fetch(
                "/api/artist/update",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        id: id,
                        name: name,
                        bookingContact: bookingContact
                    })
                }
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        const artist =
            await response.json();


        showMessage(
            "Updated artist: "
            + artist.name
        );


        document
            .getElementById(
                "artist-result"
            )
            .innerHTML =
            artistCard(artist);

    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// ARTIST DELETE
// ============================================================

async function deleteArtist() {

    const id =
        document
            .getElementById(
                "artist-id"
            )
            .value
            .trim();


    if (!id) {

        showMessage(
            "Enter an artist UUID.",
            "error"
        );

        return;
    }


    const confirmed =
        window.confirm(
            "Delete this artist?"
        );


    if (!confirmed) {
        return;
    }


    try {

        // Maps to @DeleteMapping("/delete?id=...")
        const response =
            await fetch(
                "/api/artist/delete?id="
                + encodeURIComponent(id),
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            await handleError(response);

            return;
        }


        showMessage(
            "Artist deleted."
        );


        document
            .getElementById(
                "artist-id"
            )
            .value = "";


        document
            .getElementById(
                "artist-update-name"
            )
            .value = "";


        document
            .getElementById(
                "artist-update-contact"
            )
            .value = "";


        document
            .getElementById(
                "artist-result"
            )
            .innerHTML = "";

    } catch (error) {

        connectionFailure(error);
    }
}


// ============================================================
// ARTIST DISPLAY
// ============================================================

function artistCard(artist) {

    return `
        <div class="record-card">

            <h4>
                ${escapeHtml(artist.name)}
            </h4>

            <p>
                Booking Contact: ${escapeHtml(artist.bookingContact)}
            </p>

            <small>
                UUID:
                ${escapeHtml(artist.id)}
            </small>

        </div>
    `;
}