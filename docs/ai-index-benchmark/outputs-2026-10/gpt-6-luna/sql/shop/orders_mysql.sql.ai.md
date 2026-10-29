### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:21:36Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and order lines; the order-placement procedure reserves inventory and recalculates order totals.

#### Purpose
- Stores product inventory, customer order status and totals, and per-product order lines.
- `add_order_item` adds or increments an order line, deducts stock, and refreshes the order total.

#### Type
- MySQL 8; InnoDB tables and stored procedure.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — PK, NOT NULL, AUTO_INCREMENT
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE (`uk_product_sku`)
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — PK, NOT NULL, AUTO_INCREMENT
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW','PAID','SHIPPED','CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - Index: `idx_order_customer (customer_id, created_at)`
- `order_item`
  - `order_id BIGINT UNSIGNED` — PK component, NOT NULL, FK `fk_item_order` → `customer_order.order_id`, ON DELETE CASCADE
  - `product_id INT UNSIGNED` — PK component, NOT NULL, FK `fk_item_product` → `product.product_id`
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Composite PK: `(order_id, product_id)`

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — SKU lookup, price retrieval, and stock check with `FOR UPDATE`.
- `order_item`: `quantity`, `unit_price_cents`, `order_id` — summed to recalculate an order total.

#### Writes
- `order_item`: inserts `(order_id, product_id, quantity, unit_price_cents)`; on duplicate key increments `quantity`.
- `product`: decrements `stock`.
- `customer_order`: updates `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` — FK, delete cascades.
- `order_item.product_id -> product.product_id` — FK.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Locks a matching `product` row where `sku = p_sku` and `stock >= p_qty`; retrieves `product_id` and `price_cents`.
- Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is found.
- Inserts the order line or adds `p_qty` to the existing line quantity.
- Deducts `p_qty` from product stock and sets the order total to the sum of line quantity × unit price.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`.
