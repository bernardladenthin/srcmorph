### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:58:52Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Models webshop products, customer orders, and order lines, with a procedure that adds an item, reserves inventory, and recalculates the order total.

#### Purpose
- Stores product catalog, customer orders, and per-product order lines.
- `add_order_item` adds or increments an order line while reserving stock and updating the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status`: enum values `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — primary key, `NOT NULL`, `AUTO_INCREMENT`
  - `sku VARCHAR(40)` — `NOT NULL`, unique key `uk_product_sku`
  - `name VARCHAR(200)` — `NOT NULL`
  - `price_cents INT UNSIGNED` — `NOT NULL`
  - `stock INT` — `NOT NULL`, default `0`
  - Index `PRIMARY`: `product(product_id)`
  - Index `uk_product_sku`: `product(sku)`, unique
- `customer_order`
  - `order_id BIGINT UNSIGNED` — primary key, `NOT NULL`, `AUTO_INCREMENT`
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, default `'NEW'`
  - `created_at DATETIME` — `NOT NULL`, default `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — `NOT NULL`, default `0`
  - Index `PRIMARY`: `customer_order(order_id)`
  - Index `idx_order_customer`: `customer_order(customer_id, created_at)`
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, primary key component
  - `product_id INT UNSIGNED` — `NOT NULL`, primary key component
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`
  - Index `PRIMARY`: `order_item(order_id, product_id)`
  - Constraint `fk_item_order`: foreign key `order_id` references `customer_order(order_id)`, `ON DELETE CASCADE`
  - Constraint `fk_item_product`: foreign key `product_id` references `product(product_id)`

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` (`add_order_item` lookup and row lock).
- `order_item`: `quantity`, `unit_price_cents` (`add_order_item` order-total calculation).

#### Writes
- `order_item`: inserts `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, increments `quantity`.
- `product`: updates `stock`.
- `customer_order`: updates `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`; cascading delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).
- `add_order_item` matches `product.sku`, then uses `product.product_id` for `order_item.product_id`.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Locks a matching product row when SKU matches and stock is sufficient; retrieves product ID and price.
  - Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is found.
  - Inserts an order line or adds `p_qty` to the existing line quantity.
  - Decrements product stock by `p_qty`.
  - Recalculates `customer_order.total_cents` as the sum of line quantity times unit price for the order.
  - No result set.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
