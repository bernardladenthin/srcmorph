### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:18Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models shop products, customer orders, and order lines, with a procedure that reserves product stock and recalculates order totals.

#### Purpose
- Stores product inventory, customer orders, and order-item quantities and prices.
- Adds an order line by SKU, deducts stock, and refreshes the order total.

#### Type
- MySQL 8 tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `sku VARCHAR(40)` — `NOT NULL`; unique key `uk_product_sku`.
  - `name VARCHAR(200)` — `NOT NULL`.
  - `price_cents INT UNSIGNED` — `NOT NULL`.
  - `stock INT` — `NOT NULL`, `DEFAULT 0`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, `DEFAULT 'NEW'`.
  - `created_at DATETIME` — `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — `NOT NULL`, `DEFAULT 0`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, composite primary key.
  - `product_id INT UNSIGNED` — `NOT NULL`, composite primary key.
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`.
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`.
  - Foreign key `fk_item_order`: `order_id` references `customer_order(order_id)`, `ON DELETE CASCADE`.
  - Foreign key `fk_item_product`: `product_id` references `product(product_id)`.
- Indexes:
  - `uk_product_sku` on `product(sku)` — unique key.
  - `idx_order_customer` on `customer_order(customer_id, created_at)` — key.
  - `order_item` primary key on `(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — lookup and row lock in `add_order_item`.
- `order_item`: `quantity`, `unit_price_cents`, `order_id` — total calculation.

#### Writes
- `order_item`: inserts `order_id`, `product_id`, `quantity`, `unit_price_cents`; duplicate key updates `quantity`.
- `product`: updates `stock`.
- `customer_order`: updates `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- `add_order_item(p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED)`
- Selects and locks a matching product with sufficient stock; signals SQLSTATE `45000` if no product is found.
- Inserts or increments the order-item quantity, deducts `p_qty` from product stock, then sets the order total to the sum of its line quantities times unit prices.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
