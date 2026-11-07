### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:06:50Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Models a web shop catalog and customer orders, with order-line placement that reserves inventory and recalculates order totals.

#### Purpose
- Stores products, customer orders, and their line items.
- `add_order_item` adds or increments an order line, reduces product stock, and updates the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — primary key, `NOT NULL`, `AUTO_INCREMENT`.
  - `sku VARCHAR(40)` — `NOT NULL`, unique key `uk_product_sku`.
  - `name VARCHAR(200)` — `NOT NULL`.
  - `price_cents INT UNSIGNED` — `NOT NULL`.
  - `stock INT` — `NOT NULL`, `DEFAULT 0`.
  - Index: `uk_product_sku` on `product(sku)`; unique.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — primary key, `NOT NULL`, `AUTO_INCREMENT`.
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, `DEFAULT 'NEW'`.
  - `created_at DATETIME` — `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — `NOT NULL`, `DEFAULT 0`.
  - Index: `idx_order_customer` on `customer_order(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, part of primary key.
  - `product_id INT UNSIGNED` — `NOT NULL`, part of primary key.
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`.
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`.
  - Primary key: `(order_id, product_id)`.
  - Constraint `fk_item_order`: foreign key `order_id` references `customer_order(order_id)`, `ON DELETE CASCADE`.
  - Constraint `fk_item_product`: foreign key `product_id` references `product(product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`.
- `order_item`: `order_id`, `quantity`, `unit_price_cents`.
- Procedure inputs: `p_order_id`, `p_sku`, `p_qty`.

#### Writes
- `order_item`: inserts one row; on duplicate key, increments `quantity`.
- `product`: updates `stock` by subtracting `p_qty`.
- `customer_order`: updates `total_cents` from the sum of order-line `quantity * unit_price_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`; cascade delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).
- Procedure lookup: `product.sku` matches `p_sku`; selected `product_id` identifies the item and stock update target.
- Procedure total calculation: `order_item.order_id` matches `customer_order.order_id`.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Locks a matching product row with `FOR UPDATE` when SKU matches and stock is at least `p_qty`.
  - Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is found.
  - Inserts an order line using the selected product price; duplicate order/product lines have quantity incremented.
  - Deducts `p_qty` from product stock and recalculates the order total.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`.
