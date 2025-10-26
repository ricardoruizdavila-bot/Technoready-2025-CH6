package org.ch6techno.store.controller;

import com.google.gson.Gson;
import org.ch6techno.store.model.ErrorResponse;
import org.ch6techno.store.model.Product;
import org.ch6techno.store.repo.InMemoryProductRepository;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.List;

public class ProductController {
    private final InMemoryProductRepository repo;
    private final Gson gson;

    public ProductController(InMemoryProductRepository repo, Gson gson) {
        this.repo = repo; this.gson = gson;
    }

    public List<Product> list(Request req, Response res) {
        res.status(200);
        return repo.findAll();
    }

    public Object getById(Request req, Response res) {
        var id = req.params(":id");
        return repo.findById(id)
                .<Object>map(p -> { res.status(200); return p; })
                .orElseGet(() -> {
                    res.status(404);
                    return new ErrorResponse("NOT_FOUND", "Product " + id + " not found");
                });
    }

    public Object create(Request req, Response res) {
        try {
            var incoming = gson.fromJson(req.body(), Product.class);
            if (incoming.getName() == null || incoming.getName().isBlank())
                return bad(res, "name is required");
            if (incoming.getPrice() == null || incoming.getPrice().compareTo(BigDecimal.ZERO) < 0)
                return bad(res, "price must be >= 0");
            if (incoming.getStock() < 0)
                return bad(res, "stock must be >= 0");

            var saved = repo.save(incoming);
            res.status(201);
            return saved;
        } catch (Exception e) {
            res.status(400);
            return new ErrorResponse("BAD_REQUEST", e.getMessage());
        }
    }

    public Object update(Request req, Response res) {
        var id = req.params(":id");
        var existing = repo.findById(id);
        if (existing.isEmpty()) {
            res.status(404);
            return new ErrorResponse("NOT_FOUND", "Product " + id + " not found");
        }

        try {
            var incoming = gson.fromJson(req.body(), Product.class);
            var toUpdate = existing.get();
            if (incoming.getName() != null && !incoming.getName().isBlank())
                toUpdate.setName(incoming.getName());
            if (incoming.getPrice() != null && incoming.getPrice().compareTo(BigDecimal.ZERO) >= 0)
                toUpdate.setPrice(incoming.getPrice());
            if (incoming.getStock() >= 0)
                toUpdate.setStock(incoming.getStock());

            repo.save(toUpdate);
            res.status(200);
            return toUpdate;
        } catch (Exception e) {
            res.status(400);
            return new ErrorResponse("BAD_REQUEST", e.getMessage());
        }
    }

    public Object delete(Request req, Response res) {
        var id = req.params(":id");
        if (repo.delete(id)) {
            res.status(204);
            return "";
        }
        res.status(404);
        return new ErrorResponse("NOT_FOUND", "Product " + id + " not found");
    }

    private ErrorResponse bad(Response res, String msg) {
        res.status(400);
        return new ErrorResponse("BAD_REQUEST", msg);
    }
}
