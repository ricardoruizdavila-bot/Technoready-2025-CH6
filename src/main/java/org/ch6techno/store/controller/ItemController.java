package org.ch6techno.store.controller;

import com.google.gson.Gson;
import org.ch6techno.store.model.ErrorResponse;
import org.ch6techno.store.service.ItemService;
import spark.Request;
import spark.Response;

public class ItemController {
    private final ItemService service;
    private final Gson gson;

    public ItemController(ItemService service, Gson gson) {
        this.service = service;
        this.gson = gson;
    }

    // GET /api/v1/items  -> [{id, name, price}]
    public Object listSummaries(Request req, Response res) {
        res.status(200);
        return service.listSummaries();
    }

    // GET /api/v1/items/:id/description -> {id, description}
    public Object getDescription(Request req, Response res) {
        String id = req.params(":id");
        return service.findDescriptionById(id)
                .<Object>map(desc -> { res.status(200); return desc; })
                .orElseGet(() -> { res.status(404); return new ErrorResponse("NOT_FOUND", "Item " + id + " not found"); });
    }
}
