### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:39Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Adds an item to an existing order, reserves product stock, and updates the order total.

#### Purpose
- Insert or update an order line item.
- Reserve product stock.
- Recalculate the order total.

#### Type
- Table: `product`, `customer_order`, `order_item`.
- Procedure: `add_order_item` (MySQL 8).

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
  - `order_id BIGINT UNSIGNED NOT NULL`
  - `product_id INT UNSIGNED NOT NULL`
  - `quantity SMALLINT UNSIGNED NOT NULL`
  - `unit_price_cents INT UNSIGNED NOT NULL`
  - PK `(order_id, product_id)`
  - FK `fk_item_order` references `customer_order(order_id)` ON DELETE CASCADE
  - FK `fk_item_product` references `product(product_id)`

#### Reads
- `product` (sku, stock, price_cents) – SELECT FOR UPDATE.
- `order_item` (order_id, quantity, unit_price_cents) – subquery for total.
- `customer_order` (order_id) – subquery for total.

#### Writes
- `order_item` – 1 INSERT (or UPDATE via ON DUPLICATE KEY).
- `product` – 1 UPDATE (stock decrement).
- `customer_order` – 1 UPDATE (total_cents recalculation).

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`).
- `order_item.product_id -> product.product_id` (FK `fk_item_product`).

#### Routine logic
- Parameters: `p_order_id`, `p_sku`, `p_qty`.
- Lookup `product_id` and `price_cents` where `sku` matches and `stock >= p_qty`; lock row.
- If not found, SIGNAL error.
- INSERT into `order_item`; if duplicate, increment `quantity`.
- UPDATE `product.stock` subtracting `p_qty`.
- Recalculate `customer_order.total_cents` as sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`.
- No external functions or sequences.
