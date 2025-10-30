package org.ch6techno.store.exception;

import com.google.gson.Gson;
import org.ch6techno.store.model.ErrorResponse;


public class GlobalExceptionHandler {
    private final Gson gson;

    public GlobalExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    public void register() {
        // 404 - Ruta o recurso no encontrado
        spark.Spark.notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return gson.toJson(new ErrorResponse("NOT_FOUND", "The requested resource was not found"));
        });

        // 500 - Error interno de servidor
        spark.Spark.internalServerError((req, res) -> {
            res.type("application/json");
            res.status(500);
            return gson.toJson(new ErrorResponse("INTERNAL_ERROR", "An unexpected server error occurred"));
        });

        // Captura genérica de excepciones
        spark.Spark.exception(Exception.class, (ex, req, res) -> {
            res.type("application/json");
            res.status(500);
            String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected server error";
            res.body(gson.toJson(new ErrorResponse("INTERNAL_ERROR", message)));
        });
    }
}
