### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:22Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Models webshop products, customer orders, and order lines, with a procedure that adds items, reserves inventory, and recalculates order totals.

#### Purpose
- Stores product catalog and stock, customer orders, and product quantities/prices per order.
- Adds order items while checking stock and updating inventory and order totals.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status`: enum values `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` — primary key.
  - `sku VARCHAR(40) NOT NULL`.
  - `name VARCHAR(200) NOT NULL`.
  - `price_cents INT UNSIGNED NOT NULL`.
  - `stock INT NOT NULL DEFAULT 0`.
- `customer_order`
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` — primary key.
  - `customer_id BIGINT UNSIGNED NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'`.
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`.
- `order_item`
  - `order_id BIGINT UNSIGNED NOT NULL`.
  - `product_id INT UNSIGNED NOT NULL`.
  - `quantity SMALLINT UNSIGNED NOT NULL`.
  - `unit_price_cents INT UNSIGNED NOT NULL`.
  - Primary key `(order_id, product_id)`.
  - `fk_item_order`: foreign key `order_id` references `customer_order(order_id)`, `ON DELETE CASCADE`.
  - `fk_item_product`: foreign key `product_id` references `product(product_id)`.
- Indexes:
  - `PRIMARY` on `product`: `(product_id)`.
  - `uk_product_sku` on `product`: unique `(sku)`.
  - `PRIMARY` on `customer_order`: `(order_id)`.
  - `idx_order_customer` on `customer_order`: `(customer_id, created_at)`.
  - `PRIMARY` on `order_item`: `(order_id, product_id)`.

#### Reads
- `add_order_item`: `product.product_id`, `product.price_cents`, `product.sku`, `product.stock`.
- `add_order_item`: `order_item.quantity`, `order_item.unit_price_cents`, filtered by `order_item.order_id`.
- `add_order_item`: `customer_order.order_id` for the update target.

#### Writes
- `add_order_item`: `order_item(order_id, product_id, quantity, unit_price_cents)` — 1 row per call; duplicate key updates `quantity`.
- `add_order_item`: `product.stock` — decrements stock for the selected product.
- `add_order_item`: `customer_order.total_cents` — sets total from the order’s item quantities and unit prices.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, cascade delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).
- Procedure lookup: `product.sku` matched to `p_sku`; stock eligibility checks `product.stock >= p_qty`.
- Procedure total calculation: `order_item.order_id` matched to `p_order_id`.

#### Routine logic
- `add_order_item(p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED)`:
  - Locks the matching product row and loads its ID and price if SKU matches and stock is sufficient.
  - Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is selected.
  - Inserts the order line, or adds the requested quantity to an existing `(order_id, product_id)` line.
  - Decrements product stock and recalculates `customer_order.total_cents` from the order’s items.
