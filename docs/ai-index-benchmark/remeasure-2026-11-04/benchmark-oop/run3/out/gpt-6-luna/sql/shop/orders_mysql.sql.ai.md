### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:23Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models web-shop products, customer orders, and line items, with order placement reserving inventory and recalculating totals.

#### Purpose
- Stores product catalog and stock, customer orders, and priced order lines.
- `add_order_item` adds or increments an order line, reserves product stock, and updates the order total.

#### Type
- MySQL 8 tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PK.
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL, DEFAULT `0`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PK.
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`.
  - Index `idx_order_customer`: `(customer_id, created_at)`; method not specified.
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, PK component, FK `fk_item_order` → `customer_order.order_id`, ON DELETE CASCADE.
  - `product_id INT UNSIGNED` — NOT NULL, PK component, FK `fk_item_product` → `product.product_id`.
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - Composite PK: `(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`; selects matching SKU with sufficient stock and locks the row.
- `order_item`: `quantity`, `unit_price_cents`, `order_id`; aggregates line totals for the specified order.
- `customer_order`: `order_id` used to select the row to update.

#### Writes
- `order_item`: inserts `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, increments `quantity` by `p_qty`.
- `product`: updates `stock` by subtracting `p_qty` for the selected product.
- `customer_order`: updates `total_cents` to the sum of `quantity * unit_price_cents` for the specified order.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE).
- `order_item.product_id -> product.product_id` (`fk_item_product`).
- `add_order_item` selects `product.product_id` by `product.sku`, then uses it for the order-item write and stock update.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Locks a matching product row if its stock is at least `p_qty`; captures its ID and price.
  - Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is selected.
  - Inserts an order line or increments its existing quantity.
  - Decrements product stock and recalculates the order total.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
