package org.ch6techno.store.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ch6techno.store.model.Offer;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class OfferService {
    private final Gson gson;
    private final List<Offer> cache;
    private final Map<String, Offer> byId;

    public OfferService(Gson gson) {
        this.gson = gson;
        this.cache = loadFromClasspath();
        this.byId = cache.stream().collect(Collectors.toMap(
                Offer::getId, o -> o, (a, b) -> a, LinkedHashMap::new));
    }

    private List<Offer> loadFromClasspath() {
        String path = "/recursos/ofertas.json";
        var in = OfferService.class.getResourceAsStream(path);
        if (in == null) return new ArrayList<Offer>(); // sin semilla

        try (var rd = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            var listType = new com.google.gson.reflect.TypeToken<List<Offer>>() {}.getType();
            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(rd);

            List<Offer> list = new ArrayList<Offer>();

            if (root.isJsonArray()) {
                // Caso: [ {...}, {...} ]
                list.addAll((List<Offer>) gson.fromJson(root, listType));
            } else if (root.isJsonObject()) {
                var obj = root.getAsJsonObject();
                if (obj.has("offers") && obj.get("offers").isJsonArray()) {
                    // Caso: { "offers": [ {...} ] }
                    list.addAll((List<Offer>) gson.fromJson(obj.get("offers"), listType));
                } else {
                    // Caso: { ... }  -> lo tratamos como UNA oferta
                    Offer single = gson.fromJson(obj, Offer.class);
                    if (single != null) list.add(single);
                }
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ofertas.json: " + e.getMessage(), e);
        }
    }

    public List<Offer> listAll() {
        // vista solo-lectura de la lista
        return Collections.unmodifiableList(cache);
    }

    public synchronized Offer add(String itemId, String bidder, String email, java.math.BigDecimal amount) {
        String id = "of-" + UUID.randomUUID();
        String now = Instant.now().toString();
        Offer o = new Offer(id, itemId, bidder, email, amount, now);
        cache.add(o);
        byId.put(id, o);
        return o;
    }
}
