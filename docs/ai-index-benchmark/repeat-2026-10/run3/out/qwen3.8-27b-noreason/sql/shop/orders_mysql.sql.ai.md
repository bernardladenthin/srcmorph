### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:38Z
- G: 
- A: 0.0.0
- X: file
---
> Models a web shop catalog and order lifecycle, providing a procedure to add items to an order while enforcing stock availability and maintaining order totals.

#### Purpose
- Defines the data model for products, customer orders, and order line items.
- Implements the `add_order_item` procedure to atomically add a product to an order, decrement stock, and recalculate the order total.

#### Type
- Tables: `product`, `customer_order`, `order_item` (MySQL 8, InnoDB)
- Procedure: `add_order_item` (MySQL)

#### Schema
- `product`
  - `product_id INT UNSIGNED` PK, AUTO_INCREMENT
  - `sku VARCHAR(40)` NOT NULL, UNIQUE
  - `name VARCHAR(200)` NOT NULL
  - `price_cents INT UNSIGNED` NOT NULL
  - `stock INT` NOT NULL, DEFAULT 0
- `customer_order`
  - `order_id BIGINT UNSIGNED` PK, AUTO_INCREMENT
  - `customer_id BIGINT UNSIGNED` NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` NOT NULL, DEFAULT 'NEW'
  - `created_at DATETIME` NOT NULL, DEFAULT CURRENT_TIMESTAMP
  - `total_cents INT UNSIGNED` NOT NULL, DEFAULT 0
  - Index: `idx_order_customer` on `(customer_id, created_at)`
- `order_item`
  - `order_id BIGINT UNSIGNED` NOT NULL, PK (composite)
  - `product_id INT UNSIGNED` NOT NULL, PK (composite)
  - `quantity SMALLINT UNSIGNED` NOT NULL
  - `unit_price_cents INT UNSIGNED` NOT NULL

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`
- `order_item`: `quantity`, `unit_price_cents`, `order_id`

#### Writes
- `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents` (INSERT/UPDATE)
- `product`: `stock` (UPDATE)
- `customer_order`: `total_cents` (UPDATE)

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (FK)

#### Routine logic
- `add_order_item(p_order_id, p_sku, p_qty)`
  - Locks the matching `product` row by `sku` where `stock >= p_qty` using `FOR UPDATE`.
  - Raises SQLSTATE '45000' if no matching product or insufficient stock.
  - Inserts into `order_item` or increments `quantity` on duplicate key.
  - Decrements `product.stock` by `p_qty`.
  - Recalculates `customer_order.total_cents` as the sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
