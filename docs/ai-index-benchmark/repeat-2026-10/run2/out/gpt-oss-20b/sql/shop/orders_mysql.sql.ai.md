### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:14:37Z
- G: 
- A: 0.0.0
- X: file
---
> Manages product catalog, customer orders, and order line items in a MySQL 8 webshop, including stock reservation and order total calculation.

#### Purpose
- Stores product catalog, customer orders, and order line items.
- Provides a stored procedure to add an item to an order, reserve stock, and recalculate the order total.

#### Type
- Table: `product`, `customer_order`, `order_item`
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
- Index: `idx_order_customer (customer_id, created_at)`

**order_item**
- `order_id BIGINT UNSIGNED NOT NULL PK`
- `product_id INT UNSIGNED NOT NULL PK`
- `quantity SMALLINT UNSIGNED NOT NULL`
- `unit_price_cents INT UNSIGNED NOT NULL`
- FK `fk_item_order` → `customer_order(order_id)` ON DELETE CASCADE
- FK `fk_item_product` → `product(product_id)`

#### Reads
- `product` (sku, stock, price_cents)
- `order_item` (order_id, quantity, unit_price_cents) for total calculation
- `customer_order` (order_id) for total update

#### Writes
- `order_item` (INSERT/UPDATE)
- `product` (UPDATE stock)
- `customer_order` (UPDATE total_cents)

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- **Parameters**: `p_order_id`, `p_sku`, `p_qty`
- Select product ID and price where SKU matches and stock ≥ qty, locking row.
- If not found, raise error.
- Insert or update `order_item` with quantity and unit price.
- Decrement `product.stock` by qty.
- Recalculate `customer_order.total_cents` as sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- No external functions or sequences.
