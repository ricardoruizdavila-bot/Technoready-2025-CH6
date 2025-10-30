package org.ch6techno.store;

import static spark.Spark.*;
import com.google.gson.Gson;

import org.ch6techno.store.config.Json;
import org.ch6techno.store.controller.ItemController;
import org.ch6techno.store.controller.ProductController;
import org.ch6techno.store.controller.OfferController;
import org.ch6techno.store.exception.GlobalExceptionHandler;
import org.ch6techno.store.model.ErrorResponse;
import org.ch6techno.store.repo.InMemoryProductRepository;
import org.ch6techno.store.service.ItemService;
import org.ch6techno.store.service.OfferService;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
// Ofertas de un artículo (vista)
import java.util.Objects;

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
        new GlobalExceptionHandler(gson).register();

        // Services / Controllers
        var productRepo       = new InMemoryProductRepository();
        var productController = new ProductController(productRepo, gson);

        var itemService       = new ItemService(gson);
        var itemController    = new ItemController(itemService, gson);

        var offerService      = new OfferService(gson); // <-- definir UNA sola vez
        var offerController   = new OfferController(offerService, itemService, gson);

        // Estáticos (resources/inicio)
        staticFiles.location("/inicio");

        var views = new MustacheTemplateEngine();

        // Solo API -> JSON
        before("/api/*", (req, res) -> res.type("application/json"));

        // ===================== API =====================
        path("/api/v1", () -> {
            // PRODUCTS
            get ("/products",          productController::list,    gson::toJson);
            get ("/products/:id",      productController::getById, gson::toJson);
            post("/products", "application/json", productController::create, gson::toJson);
            put ("/products/:id", "application/json", productController::update, gson::toJson);
            delete("/products/:id",    productController::delete,  gson::toJson);

            // ITEMS
            path("/items", () -> {
                get("",  itemController::listSummaries, gson::toJson);
                get("/", itemController::listSummaries, gson::toJson);
                get("/:id/description", itemController::getDescription, gson::toJson);
            });

            // OFFERS
            path("/offers", () -> {
                get ("",  offerController::list,   gson::toJson);
                post("",  offerController::create, gson::toJson); // acepta JSON o form
            });

            // DEBUG 500
            get("/debug/error", (req, res) -> { throw new RuntimeException("Simulated internal error"); });
        });
        // ===============================================

        // Redirect raíz
        get("/", (req, res) -> { res.redirect("/ui/items"); return null; });

        // ===================== UI (HTML) =====================
        // Catálogo
        get("/ui/items", (req, res) -> {
            var summaries = itemService.listSummaries();
            var list = new ArrayList<Map<String, Object>>();
            summaries.forEach(s -> {
                var row = new HashMap<String, Object>();
                row.put("id", s.getId());
                row.put("name", s.getName());
                row.put("priceFmt", fmt(s.getPrice()));
                list.add(row);
            });
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Catálogo de artículos");
            model.put("items", list);
            return views.render(new ModelAndView(model, "items.mustache"));
        });

        // Detalle + botón ofertar
        get("/ui/items/:id", (req, res) -> {
            String id = req.params(":id");
            var opt = itemService.findItemById(id);

            Map<String, Object> model = new HashMap<>();
            if (opt.isEmpty()) {
                res.status(404);
                model.put("title", "No encontrado");
                model.put("id", id);
                model.put("name", "Artículo no encontrado");
                model.put("priceFmt", "-");
                model.put("description", "No existe el artículo solicitado.");
                return views.render(new ModelAndView(model, "item_detail.mustache"));
            }

            var it = opt.get();
            model.put("title", "Detalle del artículo");
            model.put("id", it.getId());
            model.put("name", it.getName());
            model.put("priceFmt", fmt(it.getPrice()));
            model.put("description", it.getDescription());
            return views.render(new ModelAndView(model, "item_detail.mustache"));
        });



// Listado de ofertas para un artículo específico
        get("/ui/items/:id/offers", (req, res) -> {
            String id = req.params(":id");
            var offers = offerService.listAll().stream()
                    .filter(o -> Objects.equals(o.getItemId(), id)) // evita NPE
                    .toList();

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Ofertas del artículo");
            model.put("offers", offers);
            model.put("itemId", id);
            return views.render(new ModelAndView(model, "offers.mustache"));
        });


        // Listado general de ofertas (vista)
        get("/ui/offers", (req, res) -> {
            var all = offerService.listAll();
            var rows = new ArrayList<Map<String,Object>>();
            all.forEach(o -> {
                var m = new HashMap<String,Object>();
                m.put("createdAt", o.getCreatedAt());
                m.put("itemId",    o.getItemId());
                m.put("bidder",    o.getBidder());
                m.put("email",     o.getEmail());
                m.put("amountFmt", fmt(o.getAmount()));
                rows.add(m);
            });
            return views.render(new ModelAndView(Map.of("offers", rows), "offers.mustache"));
        });
        // =====================================================

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

    private static String fmt(BigDecimal p) {
        if (p == null) return "-";
        return "$" + new DecimalFormat("#,##0.00").format(p);
    }
}
