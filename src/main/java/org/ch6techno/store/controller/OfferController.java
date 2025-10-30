package org.ch6techno.store.controller;

import com.google.gson.Gson;
import org.ch6techno.store.model.ErrorResponse;
import org.ch6techno.store.model.Offer;
import org.ch6techno.store.model.OfferIn;
import org.ch6techno.store.service.ItemService;
import org.ch6techno.store.service.OfferService;
import spark.Request;
import spark.Response;

import java.util.List;

public class OfferController {
    private final OfferService offers;
    private final ItemService items;
    private final Gson gson;

    public OfferController(OfferService offers, ItemService items, Gson gson) {
        this.offers = offers; this.items = items; this.gson = gson;
    }

    // GET /api/v1/offers
    public List<Offer> list(Request req, Response res) {
        res.status(200);
        return offers.listAll();
    }

    // POST /api/v1/offers (acepta JSON o form-urlencoded)
    public Object create(Request req, Response res) {
        try {
            String ct = req.contentType() != null ? req.contentType() : "";
            String itemId, bidder, email;
            java.math.BigDecimal amount;

            if (ct.contains("application/json")) {
                OfferIn in = gson.fromJson(req.body(), OfferIn.class);
                if (in == null) throw new IllegalArgumentException("Invalid JSON");
                itemId = in.getItemId(); bidder = in.getBidder(); email = in.getEmail(); amount = in.getAmount();
            } else {
                itemId = req.queryParams("itemId");
                bidder = req.queryParams("name");   // mapeo del script que te dieron
                email  = req.queryParams("email");
                String a = req.queryParams("amount");
                amount = (a == null || a.isBlank()) ? null : new java.math.BigDecimal(a);
            }

            if (itemId == null || itemId.isBlank()) return bad(res, "itemId is required");
            if (items.findItemById(itemId).isEmpty()) return notFound(res, "Item " + itemId + " not found");
            if (bidder == null || bidder.isBlank()) return bad(res, "name/bidder is required");
            if (email == null || email.isBlank()) return bad(res, "email is required");
            if (amount == null || amount.signum() <= 0) return bad(res, "amount must be > 0");



            var saved = offers.add(itemId, bidder, email, amount);
            res.status(201);
            return saved;

        } catch (Exception e) {
            res.status(400);
            return new ErrorResponse("BAD_REQUEST", e.getMessage());
        }
    }

    private ErrorResponse bad(Response res, String msg) { res.status(400); return new ErrorResponse("BAD_REQUEST", msg); }
    private ErrorResponse notFound(Response res, String msg) { res.status(404); return new ErrorResponse("NOT_FOUND", msg); }
}
