### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:22:15Z
- G: 
- A: 0.0.0
- X: file
---
> Models web-shop products, customer orders, order lines, and stock reservation with order-total updates when an item is added.

#### Purpose
- Stores product catalog and inventory, customer orders, and order-item snapshots of quantity and unit price.
- `add_order_item` adds or increments an order line, reserves product stock, and recalculates the order total.

#### Type
- MySQL 8; InnoDB tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.

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
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - Index: `idx_order_customer (customer_id, created_at)`
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, PK component, FK `fk_item_order` to `customer_order.order_id`, `ON DELETE CASCADE`
  - `product_id INT UNSIGNED` — NOT NULL, PK component, FK `fk_item_product` to `product.product_id`
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Composite primary key: `(order_id, product_id)`

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — SKU lookup, price retrieval, stock check; row locked with `FOR UPDATE`.
- `order_item`: `order_id`, `quantity`, `unit_price_cents` — sum used to recalculate order total.

#### Writes
- `order_item`: inserts `(order_id, product_id, quantity, unit_price_cents)`; on duplicate key, increments `quantity`.
- `product`: decrements `stock` for the selected product.
- `customer_order`: updates `total_cents` for the specified order.

#### Relationships
- `order_item.order_id -> customer_order.order_id` — FK; deletion of an order cascades to its items.
- `order_item.product_id -> product.product_id` — FK.
- `customer_order.customer_id` has no declared FK in this source.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Finds and locks a product matching `p_sku` with `stock >= p_qty`; retrieves product ID and price.
  - Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is selected.
  - Inserts the order line or increments its quantity when the `(order_id, product_id)` key already exists.
  - Decrements product stock by `p_qty`.
  - Recalculates `customer_order.total_cents` as the sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
