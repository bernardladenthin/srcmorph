### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:25Z
- G: 
- A: 0.0.0
- X: file
---
> Models webshop products, customer orders, and order lines, with a procedure that adds an order line, reserves inventory, and recalculates the order total.

#### Purpose
- Stores product catalog and stock, customer order status and totals, and order-line quantities and prices.
- `add_order_item` adds or increments a line, decrements product stock, and updates the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE via `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
  - Index `PRIMARY` on `product (product_id)`
  - Index `uk_product_sku` on `product (sku)` — UNIQUE
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - Index `PRIMARY` on `customer_order (order_id)`
  - Index `idx_order_customer` on `customer_order (customer_id, created_at)`
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY; FK `fk_item_order` references `customer_order (order_id) ON DELETE CASCADE`
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY; FK `fk_item_product` references `product (product_id)`
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Index `PRIMARY` on `order_item (order_id, product_id)`
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — looks up matching SKU with sufficient stock under `FOR UPDATE`.
- `order_item`: `quantity`, `unit_price_cents` — sums line totals for `p_order_id`.

#### Writes
- `order_item`: INSERT one line per procedure call; on duplicate key, increments existing `quantity`.
- `product`: UPDATE `stock` by subtracting `p_qty` for the matched product.
- `customer_order`: UPDATE `total_cents` to the sum of its order-line totals.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- `add_order_item` parameters: `p_order_id`, `p_sku`, `p_qty`.
- Locks and retrieves the product ID and price for `p_sku` when stock is at least `p_qty`.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no matching product is found.
- Inserts an order line or increments its quantity on duplicate key; subtracts the requested quantity from stock.
- Recalculates `customer_order.total_cents` from its order lines.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
