### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:07Z
- G: 
- A: 0.0.0
- X: file
---
> Models web-shop products, customer orders, order lines, stock reservations, and order-total updates when adding an item.

#### Purpose
- Stores product catalog and inventory, customer orders, and order-item pricing and quantities.
- `add_order_item` adds or increments an order line, reserves product stock, and recalculates the order total.

#### Type
- MySQL 8; tables and procedure.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, PRIMARY KEY (composite)
  - `product_id INT UNSIGNED` — NOT NULL, PRIMARY KEY (composite)
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - PRIMARY KEY (`order_id`, `product_id`) — unnamed
  - CONSTRAINT `fk_item_order`: `order_id` references `customer_order(order_id)`, ON DELETE CASCADE
  - CONSTRAINT `fk_item_product`: `product_id` references `product(product_id)`
- Indexes:
  - `uk_product_sku` on `product(sku)` — unique
  - `idx_order_customer` on `customer_order(customer_id, created_at)`
  - `order_item` primary key on (`order_id`, `product_id`) — unnamed

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`
- `order_item`: `quantity`, `unit_price_cents`, `order_id`

#### Writes
- `order_item`: INSERT writes `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, updates `quantity`.
- `product`: UPDATE writes `stock`.
- `customer_order`: UPDATE writes `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`
- Procedure lookup: `product.sku` matched to `p_sku`.
- Procedure order-total calculation: `order_item.order_id` matched to `p_order_id`.

#### Routine logic
- `add_order_item(p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED)`:
  - Locks a matching product row with `FOR UPDATE` when its stock is at least `p_qty`; retrieves `product_id` and `price_cents`.
  - Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is found.
  - Inserts an order line, or increments its existing quantity on duplicate key.
  - Decrements product stock by `p_qty`.
  - Recalculates `customer_order.total_cents` as the sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
