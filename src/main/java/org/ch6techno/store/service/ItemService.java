package org.ch6techno.store.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ch6techno.store.model.Item;
import org.ch6techno.store.model.ItemDescription;
import org.ch6techno.store.model.ItemSummary;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ItemService {
    private final Gson gson;
    private final List<Item> cache;
    private final Map<String, Item> byId;

    public ItemService(Gson gson) {
        this.gson = gson;
        this.cache = loadFromClasspath();
        this.byId  = cache.stream().collect(Collectors.toMap(Item::getId, i -> i, (a,b)->a, LinkedHashMap::new));
    }

    private List<Item> loadFromClasspath() {
        var path = "/recursos/items.json";
        var in = ItemService.class.getResourceAsStream(path);
        if (in == null) throw new IllegalStateException("items.json not found at classpath:" + path);
        try (var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<Item>>(){}.getType();
            List<Item> items = gson.fromJson(reader, listType);
            return items != null ? items : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load items.json: " + e.getMessage(), e);
        }
    }

    public List<ItemSummary> listSummaries() {
        return cache.stream()
                .map(i -> new ItemSummary(i.getId(), i.getName(), i.getPrice()))
                .collect(Collectors.toList());
    }

    public Optional<ItemDescription> findDescriptionById(String id) {
        var it = byId.get(id);
        if (it == null) return Optional.empty();
        return Optional.of(new ItemDescription(it.getId(), it.getDescription()));
    }
}
