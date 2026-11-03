// ============================================================
// NAVIGATION
// ============================================================

function hideAllSections() {

    document
        .querySelectorAll("main > section")
        .forEach(section => {
            section.classList.add("hidden");
        });
}


function showMainMenu() {

    hideAllSections();

    document
        .getElementById("main-menu")
        .classList.remove("hidden");
}


function showSection(sectionId) {

    hideAllSections();

    document
        .getElementById(sectionId)
        .classList.remove("hidden");


    if (sectionId === "categories-section") {
        loadCategories();
    }
}


// ============================================================
// MESSAGE DISPLAY
// ============================================================

function showMessage(text, type = "success") {

    const message =
        document.getElementById("message");

    message.textContent = text;

    message.className =
        "message " + type;


    setTimeout(() => {

        message.classList.add("hidden");

    }, 4000);
}


// ============================================================
// CREATE CATEGORY
// ============================================================

async function createCategory() {

    const name =
        document
            .getElementById(
                "create-category-name"
            )
            .value
            .trim();


    const description =
        document
            .getElementById(
                "create-category-description"
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
            "Category created: "
            + category.name
        );


        document
            .getElementById(
                "create-category-name"
            )
            .value = "";


        document
            .getElementById(
                "create-category-description"
            )
            .value = "";


        loadCategories();

    } catch (error) {

        showMessage(
            "Could not contact FestHub.",
            "error"
        );

        console.error(error);
    }
}


// ============================================================
// READ CATEGORY
// ============================================================

async function readCategory() {

    const id =
        document
            .getElementById(
                "read-category-id"
            )
            .value
            .trim();


    const result =
        document.getElementById(
            "read-category-result"
        );


    if (!id) {

        showMessage(
            "Enter a category ID.",
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

            result.innerHTML = "";

            await handleError(response);

            return;
        }


        const category =
            await response.json();


        result.innerHTML =
            categoryCard(category);


    } catch (error) {

        showMessage(
            "Could not contact FestHub.",
            "error"
        );

        console.error(error);
    }
}


// ============================================================
// UPDATE CATEGORY
// ============================================================

async function updateCategory() {

    const id =
        document
            .getElementById(
                "update-category-id"
            )
            .value
            .trim();


    const name =
        document
            .getElementById(
                "update-category-name"
            )
            .value
            .trim();


    const description =
        document
            .getElementById(
                "update-category-description"
            )
            .value
            .trim();


    if (!id || !name || !description) {

        showMessage(
            "ID, name, and description are required.",
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
            "Category updated: "
            + category.name
        );


        loadCategories();

    } catch (error) {

        showMessage(
            "Could not contact FestHub.",
            "error"
        );

        console.error(error);
    }
}


// ============================================================
// DELETE CATEGORY
// ============================================================

async function deleteCategory() {

    const id =
        document
            .getElementById(
                "delete-category-id"
            )
            .value
            .trim();


    if (!id) {

        showMessage(
            "Enter a category ID.",
            "error"
        );

        return;
    }


    const confirmed =
        window.confirm(
            "Delete this festival category?"
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
            "Category deleted successfully."
        );


        document
            .getElementById(
                "delete-category-id"
            )
            .value = "";


        loadCategories();

    } catch (error) {

        showMessage(
            "Could not contact FestHub.",
            "error"
        );

        console.error(error);
    }
}


// ============================================================
// LIST CATEGORIES
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


        if (categories.length === 0) {

            list.innerHTML =
                "<p>No festival categories found.</p>";

            return;
        }


        list.innerHTML =
            categories
                .map(categoryCard)
                .join("");


    } catch (error) {

        list.innerHTML =
            "Unable to connect to FestHub.";

        showMessage(
            "Could not contact FestHub.",
            "error"
        );

        console.error(error);
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
                ID:
                ${escapeHtml(category.id)}
            </small>

        </div>
    `;
}


// ============================================================
// API ERROR HANDLING
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

        // Response may not contain JSON.
    }


    showMessage(
        message,
        "error"
    );
}


// ============================================================
// BASIC HTML ESCAPING
// ============================================================

function escapeHtml(value) {

    const element =
        document.createElement("div");

    element.textContent =
        value ?? "";

    return element.innerHTML;
}