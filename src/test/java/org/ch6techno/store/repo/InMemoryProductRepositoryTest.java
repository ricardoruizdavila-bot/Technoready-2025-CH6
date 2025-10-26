package org.ch6techno.store.repo;

import org.ch6techno.store.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryProductRepositoryTest {

    @Test
    void saveAndFind() {
        var repo = new InMemoryProductRepository();
        var p = new Product("Mouse", new BigDecimal("250.00"), 5);
        var saved = repo.save(p);

        assertNotNull(saved.getId());
        assertTrue(repo.findById(saved.getId()).isPresent());
    }
}
