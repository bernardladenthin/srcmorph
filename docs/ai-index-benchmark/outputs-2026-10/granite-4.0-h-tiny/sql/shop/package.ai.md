### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:10:33Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL-based web‑shop domain model for managing products, customers, and order placement, including order items and stock reservation.

#### Purpose
- Models a web‑shop domain: products, customers, and order placement, including order items and stock reservation.

#### Responsibilities
- Provides stored procedures and schema objects for order processing.
- Handles product lookup, stock checking, and reservation.
- Updates order totals and order items atomically.
- Manages error conditions such as missing SKUs or insufficient stock.

#### Key units
- **product** (`product_id INT UNSIGNED PK`, `sku VARCHAR(40) UNQ`, `name VARCHAR(200) UNQ`, `price_cents INT UNSIGNED`, `stock INT`)
- **customer_order** (`order_id BIGINT UNSIGNED PK`, `customer_id BIGINT UNSIGNED`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `created_at DATETIME`, `total_cents INT UNSIGNED`)
- **order_item** (`order_id BIGINT UNSIGNED FK→customer_order(order_id)`, `product_id INT UNSIGNED FK→product(product_id)`, `quantity SMALLINT UNSIGNED`, `unit_price_cents INT UNSIGNED`)
- `add_order_item` (`order_id BIGINT UNSIGNED`, `sku VARCHAR(40)`, `quantity SMALLINT`)

#### Data flow
Inputs from application code call `add_order_item` with an order ID, SKU, and quantity. The procedure looks up the product, verifies stock, inserts an order item, decrements stock, recomputes the order total, and signals errors on failure.

#### Dependencies
- MySQL 8 stored procedures and schema objects.
- No external dependencies.

#### Cross-cutting
- Uses explicit transaction control for atomic updates.
- Error handling via SQLSTATE codes for SKU not found or insufficient stock.
