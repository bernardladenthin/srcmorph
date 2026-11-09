### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:21:54Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models shop products, customer orders, and order lines, with a procedure that adds items, reserves inventory, and refreshes order totals.

#### Purpose
- Stores product catalog and stock, customer orders, and per-order product quantities and prices.
- `add_order_item` adds or increments an order line, decrements stock, and recalculates the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — primary key `PRIMARY`, `NOT NULL`, `AUTO_INCREMENT`.
  - `sku VARCHAR(40)` — `NOT NULL`, unique key `uk_product_sku`.
  - `name VARCHAR(200)` — `NOT NULL`.
  - `price_cents INT UNSIGNED` — `NOT NULL`.
  - `stock INT` — `NOT NULL`, `DEFAULT 0`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — primary key `PRIMARY`, `NOT NULL`, `AUTO_INCREMENT`.
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, `DEFAULT 'NEW'`.
  - `created_at DATETIME` — `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — `NOT NULL`, `DEFAULT 0`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — primary-key component `PRIMARY`, `NOT NULL`; foreign key `fk_item_order`.
  - `product_id INT UNSIGNED` — primary-key component `PRIMARY`, `NOT NULL`; foreign key `fk_item_product`.
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`.
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`.
- Indexes:
  - `PRIMARY` on `product (product_id)`.
  - `uk_product_sku` on `product (sku)` — unique.
  - `PRIMARY` on `customer_order (order_id)`.
  - `idx_order_customer` on `customer_order (customer_id, created_at)`.
  - `PRIMARY` on `order_item (order_id, product_id)`.
  - `fk_item_order` on `order_item (order_id)` — foreign-key index.
  - `fk_item_product` on `order_item (product_id)` — foreign-key index.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — SKU lookup and stock eligibility check.
- `order_item`: `order_id`, `quantity`, `unit_price_cents` — sums line totals for an order.

#### Writes
- `order_item`: inserts one line per procedure call; on duplicate `(order_id, product_id)`, increments `quantity`.
- `product`: updates `stock` by subtracting `p_qty` for the selected `product_id`.
- `customer_order`: updates `total_cents` for `order_id` using the sum of its order-item line totals.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, `ON DELETE CASCADE`).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Selects and locks the matching product by SKU when stock is sufficient.
- Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is selected.
- Inserts an order line or increments its quantity on duplicate key; decrements product stock; recalculates the order total.
- No result set; effects are persisted in `order_item`, `product`, and `customer_order`.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
