### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:51:42Z
- G: 
- A: 0.0.0
- X: file
---
> Adds items to a customer order, reserves product stock, and updates the order total in a MySQL 8 web shop.

#### Purpose
- Handles order line addition and stock reservation.
- Keeps `customer_order.total_cents` current.

#### Type
- Table: `product`
- Table: `customer_order`
- Table: `order_item`
- Procedure: `add_order_item`
- Dialect: MySQL 8

#### Schema
**product**
- `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT PK`
- `sku VARCHAR(40) NOT NULL UNIQUE`
- `name VARCHAR(200) NOT NULL`
- `price_cents INT UNSIGNED NOT NULL`
- `stock INT NOT NULL DEFAULT 0`

**customer_order**
- `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PK`
- `customer_id BIGINT UNSIGNED NOT NULL`
- `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`
- `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
- `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
- Index `idx_order_customer (customer_id, created_at)`

**order_item**
- `order_id BIGINT UNSIGNED NOT NULL PK`
- `product_id INT UNSIGNED NOT NULL PK`
- `quantity SMALLINT UNSIGNED NOT NULL`
- `unit_price_cents INT UNSIGNED NOT NULL`
- FK `fk_item_order` → `customer_order(order_id)` ON DELETE CASCADE
- FK `fk_item_product` → `product(product_id)`

#### Reads
- `product` (SELECT `product_id`, `price_cents` WHERE `sku` AND `stock` ≥ `p_qty` FOR UPDATE)
- `order_item` (SELECT SUM(quantity * unit_price_cents) WHERE `order_id` = `p_order_id`)

#### Writes
- `order_item` (INSERT or UPDATE quantity)
- `product` (UPDATE stock)
- `customer_order` (UPDATE total_cents)

#### Relationships
- `order_item.order_id` → `customer_order.order_id`
- `order_item.product_id` → `product.product_id`

#### Routine logic
- Parameters: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`
- Lookup product by SKU with sufficient stock; lock row.
- If not found → SIGNAL error.
- Insert or update `order_item` line; increment quantity if duplicate.
- Decrement `product.stock` by `p_qty`.
- Recalculate `customer_order.total_cents` from all items of the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- No external functions or sequences.
