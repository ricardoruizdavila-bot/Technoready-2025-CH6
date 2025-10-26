# 🛒 CH6Techno – Online Store API  
**Sprint 1 – Puntos 1, 2 y 3**

## 📘 Descripción general  
Esta API fue desarrollada en **Java 17**, usando **Spark Framework** como microframework web y **Maven** como gestor de dependencias.  
El proyecto simula una tienda en línea con dos módulos principales:

1. **Products** → recursos CRUD almacenados en memoria.  
2. **Items** → recursos leídos desde el archivo `src/main/resources/recursos/items.json`.

---

## ⚙️ Dependencias principales
- **Spark Core 2.9.4** – manejo de rutas HTTP.  
- **Gson 2.10.1** – serialización/deserialización JSON.  
- **Logback 1.2.13** – registro de eventos.  
- **JDK 17 + Maven 4.0** – entorno de desarrollo.  

---

## 🚀 Ejecución
### Desde IntelliJ
1. Abre el proyecto Maven.  
2. Ejecuta la clase `org.ch6techno.store.App`.  
3. Por defecto el servidor se inicia en **http://localhost:4567**.

---

## 📡 Endpoints principales

### **1️⃣ Products (CRUD en memoria)**
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/products` | Lista todos los productos |
| GET | `/api/v1/products/:id` | Obtiene un producto por ID |
| POST | `/api/v1/products` | Crea un producto nuevo |
| PUT | `/api/v1/products/:id` | Actualiza un producto existente |
| DELETE | `/api/v1/products/:id` | Elimina un producto |

🧪 *Datos iniciales:* se crean dos productos de ejemplo en memoria.

---

### **2️⃣ Items (leídos desde `items.json`)**
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/items` | Retorna la lista completa de artículos (`id`, `name`, `price`) |
| GET | `/api/v1/items/:id/description` | Retorna la descripción del artículo dado |

📂 *Origen de datos:* `src/main/resources/recursos/items.json`

Ejemplo de respuesta:
```json
[
  { "id": "item1", "name": "Gorra autografiada por Peso Pluma", "price": 621.34 },
  { "id": "item2", "name": "Casco autografiado por Rosalía", "price": 734.57 }
]
```

```json
{
  "id": "item4",
  "description": "Una guitarra acústica de alta calidad utilizada por el famoso cantautor Fernando Delgadillo."
}
```

---

## 🧩 Arquitectura básica
```
src/
 ├─ main/java/org/ch6techno/store/
 │   ├─ App.java                     → punto de entrada
 │   ├─ controller/ItemController.java
 │   ├─ controller/ProductController.java
 │   ├─ model/Item.java, Product.java, ErrorResponse.java
 │   ├─ repo/InMemoryProductRepository.java
 │   ├─ service/ItemService.java     → lectura y cacheo de items.json
 │   └─ config/Json.java             → configuración Gson
 └─ resources/recursos/items.json
```

---

## 💬 Explicación del funcionamiento
- Al iniciar, `ItemService` carga `items.json` desde `resources` y mantiene los objetos en memoria.  
- Las rutas `/api/v1/items` y `/api/v1/items/:id/description` devuelven datos reales de ese archivo.  
- Las rutas `/products` sirven para probar operaciones CRUD usando listas en memoria.  
- Toda respuesta se entrega en formato **JSON** y con tipo `application/json`.  
- Los errores globales son manejados por `ErrorResponse`.
# Technoready-2025-CH6
