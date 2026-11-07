### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:10Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Models shop products, customer orders, and order lines, with a procedure that adds order items while reserving inventory and updating order totals.

#### Purpose
- Stores product catalog and stock, customer orders, and per-product order lines.
- `add_order_item` adds or increments an order line, decrements stock, and recalculates the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- Enum type `customer_order.status`: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL, DEFAULT `0`.
  - Index: `PRIMARY` on `product(product_id)`.
  - Index: `uk_product_sku` on `product(sku)`; UNIQUE.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`.
  - Index: `PRIMARY` on `customer_order(order_id)`.
  - Index: `idx_order_customer` on `customer_order(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY; FK `fk_item_order`, ON DELETE CASCADE.
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY; FK `fk_item_product`.
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - Index: `PRIMARY` on `order_item(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` (procedure lookup filtered by `sku` and stock, with `FOR UPDATE`).
- `order_item`: `quantity`, `unit_price_cents`, `order_id` (procedure total calculation).

#### Writes
- `order_item`: procedure INSERT with duplicate-key UPDATE; inserts one line per call, or increments existing `quantity`.
- `product`: UPDATE `stock` by subtracting requested quantity.
- `customer_order`: UPDATE `total_cents` from the order’s summed line amounts.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`.
- Selects and locks a matching in-stock product; signals SQLSTATE `45000` with `unknown SKU or not enough stock` if none is found.
- Inserts an order line with the current product price, or adds `p_qty` to an existing line’s quantity.
- Decrements product stock and recalculates the order’s `total_cents` as the sum of `quantity * unit_price_cents` for that order.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
