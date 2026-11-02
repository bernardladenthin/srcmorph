### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:05:25Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a MySQL 8 schema for a web shop with products, orders, and order items, plus a stored procedure to add items to an order while reserving stock and updating totals.

#### Purpose
- Store product catalog, customer orders, and line items.
- Provide a procedure to add an item to an order, reserve stock, and recalculate order total.

#### Type
- Table: `product`, `customer_order`, `order_item`
- Procedure: `add_order_item`
- Dialect: MySQL 8

#### Schema
- **product**
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK
  - `sku VARCHAR(40) NOT NULL` UNIQUE `uk_product_sku`
  - `name VARCHAR(200) NOT NULL`
  - `price_cents INT UNSIGNED NOT NULL`
  - `stock INT NOT NULL DEFAULT 0`
- **customer_order**
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK
  - `customer_id BIGINT UNSIGNED NOT NULL`
  - `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
  - Index `idx_order_customer` on `(customer_id, created_at)`
- **order_item**
  - `order_id BIGINT UNSIGNED NOT NULL`
  - `product_id INT UNSIGNED NOT NULL`
  - `quantity SMALLINT UNSIGNED NOT NULL`
  - `unit_price_cents INT UNSIGNED NOT NULL`
  - PK `(order_id, product_id)`
  - FK `fk_item_order` references `customer_order(order_id)` ON DELETE CASCADE
  - FK `fk_item_product` references `product(product_id)`

#### Reads
- `product` (SELECT product_id, price_cents WHERE sku = p_sku AND stock >= p_qty FOR UPDATE)
- `order_item` (SELECT SUM(quantity * unit_price_cents) WHERE order_id = p_order_id)
- `product` (UPDATE stock)
- `customer_order` (UPDATE total_cents)

#### Writes
- `order_item`: 1 row inserted or updated per call
- `product`: 1 row updated (stock decrement)
- `customer_order`: 1 row updated (total_cents recalculated)

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`)
- `order_item.product_id -> product.product_id` (FK `fk_item_product`)

#### Routine logic
- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`
- Steps:
  1. Lock product row with matching `sku` and sufficient `stock`.
  2. If not found, raise error.
  3. Insert or update `order_item` with quantity and unit price.
  4. Decrement `product.stock` by `p_qty`.
  5. Recalculate `customer_order.total_cents` from all items of the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- Columns: `product.sku`, `product.stock`, `product.price_cents`, `order_item.order_id`, `order_item.product_id`, `order_item.quantity`, `order_item.unit_price_cents`, `customer_order.total_cents`
