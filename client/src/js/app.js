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
// UNCONNECTED TEAM SERVICES
// ============================================================

function artistNotConnected() {

    showMessage(
        "Artist service has not been connected to the client yet.",
        "error"
    );
}


function eventNotConnected() {

    showMessage(
        "Concert Event service has not been connected to the client yet.",
        "error"
    );
}


function mediaNotConnected() {

    showMessage(
        "Promotional Media service has not been connected to the client yet.",
        "error"
    );
}