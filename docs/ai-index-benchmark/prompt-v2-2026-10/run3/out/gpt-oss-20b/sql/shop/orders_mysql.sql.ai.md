### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:09:10Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a MySQL 8 schema for a web shop with products, orders, and order items, and a stored procedure to add items to an order while reserving stock and updating totals.

#### Purpose
- Stores product catalog, customer orders, and line items.
- Provides `add_order_item` procedure to add items, reserve stock, and recalculate order totals.

#### Type
- Tables: `product`, `customer_order`, `order_item`
- Procedure: `add_order_item`
- Dialect: MySQL 8

#### Schema
**product**
- `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` PK
- `sku VARCHAR(40) NOT NULL` UNIQUE `uk_product_sku`
- `name VARCHAR(200) NOT NULL`
- `price_cents INT UNSIGNED NOT NULL`
- `stock INT NOT NULL DEFAULT 0`

**customer_order**
- `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` PK
- `customer_id BIGINT UNSIGNED NOT NULL`
- `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
- `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
- `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
- Index `idx_order_customer (customer_id, created_at)`

**order_item**
- `order_id BIGINT UNSIGNED NOT NULL`
- `product_id INT UNSIGNED NOT NULL`
- `quantity SMALLINT UNSIGNED NOT NULL`
- `unit_price_cents INT UNSIGNED NOT NULL`
- PK `(order_id, product_id)`
- FK `fk_item_order` → `customer_order(order_id)` ON DELETE CASCADE
- FK `fk_item_product` → `product(product_id)`

#### Reads
- `product` (SELECT `product_id`, `price_cents` WHERE `sku` AND `stock` >= `p_qty` FOR UPDATE)
- `order_item` (SELECT SUM(quantity * unit_price_cents) WHERE `order_id` = `p_order_id`)

#### Writes
- `order_item` (INSERT ... ON DUPLICATE KEY UPDATE)
- `product` (UPDATE `stock` = `stock` - `p_qty`)
- `customer_order` (UPDATE `total_cents` = SUM of line items)

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`)
- `order_item.product_id -> product.product_id` (FK `fk_item_product`)

#### Routine logic
- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`
- Lookup product by `sku` with stock check; lock row.
- If not found or insufficient stock, raise error.
- Insert or update `order_item` quantity.
- Decrement `product.stock` by `p_qty`.
- Recalculate `customer_order.total_cents` from current `order_item` rows.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- No external functions or sequences.
