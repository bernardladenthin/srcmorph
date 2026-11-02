### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:02:38Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Adds an item to an order, reserves product stock, and updates the order total in a MySQL 8 web‑shop.

#### Purpose
- Insert or update an order line.
- Reserve product stock.
- Recalculate the order total.

#### Type
- Table: `product`, `customer_order`, `order_item`.
- Procedure: `add_order_item`.
- Dialect: MySQL 8.

#### Schema
- **product**
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK
  - `sku VARCHAR(40) NOT NULL` UNIQUE `uk_product_sku`
  - `name VARCHAR(200) NOT NULL`
  - `price_cents INT UNSIGNED NOT NULL`
  - `stock INT NOT NULL DEFAULT 0`
- **customer_order**
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK
  - `customer_id BIGINT UNSIGNED NOT NULL`
  - `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
  - Index `idx_order_customer` on `(customer_id, created_at)`
- **order_item**
  - `order_id BIGINT UNSIGNED NOT NULL` PK part
  - `product_id INT UNSIGNED NOT NULL` PK part
  - `quantity SMALLINT UNSIGNED NOT NULL`
  - `unit_price_cents INT UNSIGNED NOT NULL`
  - FK `fk_item_order` references `customer_order(order_id)` ON DELETE CASCADE
  - FK `fk_item_product` references `product(product_id)`

#### Reads
- `product` (sku, stock, price_cents) in `SELECT ... FOR UPDATE`.
- `order_item` (quantity, unit_price_cents) in `SELECT SUM(...)` for total.
- `customer_order` (order_id) in `UPDATE` of total.

#### Writes
- `order_item`: `INSERT ... ON DUPLICATE KEY UPDATE` one row per call.
- `product`: `UPDATE stock` one row per call.
- `customer_order`: `UPDATE total_cents` one row per call.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`)
- `order_item.product_id -> product.product_id` (FK `fk_item_product`)

#### Routine logic
- Parameters: `p_order_id`, `p_sku`, `p_qty`.
- Lookup product by `sku` with stock check (`FOR UPDATE`).
- If not found or insufficient stock, raise error.
- Insert or update `order_item` with quantity and unit price.
- Decrement `product.stock` by `p_qty`.
- Recalculate `customer_order.total_cents` as sum of all line totals for that order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`.
