package org.ch6techno.store;

import static spark.Spark.*;
import com.google.gson.Gson;

import org.ch6techno.store.config.Json;
import org.ch6techno.store.controller.ItemController;
import org.ch6techno.store.controller.ProductController;
import org.ch6techno.store.model.ErrorResponse;
import org.ch6techno.store.repo.InMemoryProductRepository;
import org.ch6techno.store.service.ItemService;

public class App {
    public static void main(String[] args) {
        port(getPort());
        threadPool(8, 2, 30_000);

        initExceptionHandler(e -> {
            System.err.println("Spark init FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(100);
        });

        Gson gson = Json.gson();

        // Products (CRUD en memoria: Sprint 1 punto 1)
        var productRepo = new InMemoryProductRepository();
        var productController = new ProductController(productRepo, gson);

        // Items (lectura real desde resources/recursos/items.json: Sprint 1 punto 3)
        var itemService = new ItemService(gson);
        var itemController = new ItemController(itemService, gson);

        before((req, res) -> res.type("application/json"));

        path("/api/v1", () -> {
            // PRODUCTS
            get("/products", productController::list, gson::toJson);
            get("/products/:id", productController::getById, gson::toJson);
            post("/products", "application/json", productController::create, gson::toJson);
            put("/products/:id", "application/json", productController::update, gson::toJson);
            delete("/products/:id", productController::delete, gson::toJson);

            // ITEMS
            path("/items", () -> {
                get("",  itemController::listSummaries, gson::toJson);   // /api/v1/items
                get("/", itemController::listSummaries, gson::toJson);   // /api/v1/items/
                get("/:id/description", itemController::getDescription, gson::toJson);
            });
        });

        init();
        awaitInitialization();

        exception(Exception.class, (ex, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body(gson.toJson(new ErrorResponse("INTERNAL_ERROR", ex.getMessage())));
        });

        after((req, res) -> res.header("Server", "ch6techno-api-store"));
    }

    private static int getPort() {
        String p = System.getenv("PORT");
        return (p != null && !p.isBlank()) ? Integer.parseInt(p) : 4567;
    }
}
