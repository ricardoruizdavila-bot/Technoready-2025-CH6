package org.ch6techno.store.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class Json {
    public static Gson gson() {
        return new GsonBuilder()
                .serializeNulls()
                // Un solo TypeAdapter que SERIALIZA y DESERIALIZA BigDecimal
                .registerTypeAdapter(BigDecimal.class, new TypeAdapter<BigDecimal>() {
                    @Override
                    public void write(JsonWriter out, BigDecimal value) throws IOException {
                        if (value == null) { out.nullValue(); return; }
                        out.value(value); // sale como número JSON
                    }

                    @Override
                    public BigDecimal read(JsonReader in) throws IOException {
                        JsonToken t = in.peek();
                        if (t == JsonToken.NULL) { in.nextNull(); return null; }
                        if (t == JsonToken.NUMBER) {
                            // números puros del JSON
                            return new BigDecimal(in.nextString());
                        }
                        if (t == JsonToken.STRING) {
                            String raw = in.nextString();
                            if (raw == null || raw.isBlank()) return null;

                            // Normaliza: acepta "$1,299.00 MXN", "1.299,00", "1299", etc.
                            String norm = raw.replace(',', '.')        // coma decimal -> punto
                                    .replaceAll("[^0-9.\\-]", ""); // quita símbolos/espacios

                            // Si quedaron múltiples puntos (miles + decimal), conserva sólo el último como decimal
                            int lastDot = norm.lastIndexOf('.');
                            if (lastDot > -1) {
                                norm = norm.substring(0, lastDot).replace(".", "") + norm.substring(lastDot);
                            }
                            if (norm.isBlank()) return null;

                            try {
                                return new BigDecimal(norm);
                            } catch (NumberFormatException e) {
                                throw new JsonParseException("No se pudo parsear BigDecimal desde: '" + raw + "'", e);
                            }
                        }
                        throw new JsonParseException("Formato JSON no soportado para BigDecimal: " + t);
                    }
                })
                .create();
    }
}
