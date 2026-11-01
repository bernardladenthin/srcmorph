### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:01:26Z
- G: 
- A: 0.0.0
- X: file
---
> Models web-shop products, customer orders, and order lines; the order-placement procedure reserves inventory and recalculates order totals.

#### Purpose
- Stores product catalog, customer orders, and order items.
- Adds an order line while checking and reserving stock, then updates the order total.

#### Type
- MySQL 8 tables and procedure.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — PK `PRIMARY`; NOT NULL; AUTO_INCREMENT.
  - `sku VARCHAR(40)` — NOT NULL; UNIQUE KEY `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL; DEFAULT `0`.
  - Index `PRIMARY` on `(product_id)`.
  - Index `uk_product_sku` on `(sku)`; unique.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — PK `PRIMARY`; NOT NULL; AUTO_INCREMENT.
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL; DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL; DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL; DEFAULT `0`.
  - Index `PRIMARY` on `(order_id)`.
  - Index `idx_order_customer` on `(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL; PK `PRIMARY`; FK `fk_item_order`.
  - `product_id INT UNSIGNED` — NOT NULL; PK `PRIMARY`; FK `fk_item_product`.
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - Index `PRIMARY` on `(order_id, product_id)`.
  - Constraint `fk_item_order`: `order_id` references `customer_order(order_id)`; ON DELETE CASCADE.
  - Constraint `fk_item_product`: `product_id` references `product(product_id)`.

#### Reads
- Procedure `add_order_item`: `product.product_id`, `product.price_cents`, `product.sku`, `product.stock`.
- Procedure `add_order_item`: `order_item.order_id`, `order_item.quantity`, `order_item.unit_price_cents` in total calculation.

#### Writes
- Procedure `add_order_item`:
  - `order_item`: inserts `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key updates `quantity`.
  - `product`: updates `stock`.
  - `customer_order`: updates `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`:
  - Locks matching product row with `FOR UPDATE`, requiring `sku = p_sku` and `stock >= p_qty`; retrieves product ID and price.
  - Signals SQLSTATE `45000` with `unknown SKU or not enough stock` when no matching product is available.
  - Inserts the order line using the current product price; on duplicate `(order_id, product_id)`, adds `p_qty` to existing quantity.
  - Decrements product stock by `p_qty`.
  - Recalculates the order’s `total_cents` as the sum of line quantity times unit price.

#### Dependencies
- `product`
- `order_item`
- `customer_order`
