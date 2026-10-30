### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:01Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop schema in MySQL 8 for products, customer orders, order line items, and a stored procedure that adds an item to an order while reserving stock and recalculating the order total.

#### Purpose
- Models products with stock, customer orders with status and total, and order line items.
- Provides `add_order_item` to add a SKU to an order, decrement `product.stock`, and refresh `customer_order.total_cents`.

#### Type
- Multiple objects: 3 tables (`product`, `customer_order`, `order_item`) and 1 stored procedure (`add_order_item`).
- Dialect: MySQL 8 (InnoDB, utf8mb4, `DELIMITER //`, `SIGNAL SQLSTATE`).

#### Schema
- `product`
  - `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY
  - `sku` VARCHAR(40) NOT NULL, UNIQUE KEY `uk_product_sku`
  - `name` VARCHAR(200) NOT NULL
  - `price_cents` INT UNSIGNED NOT NULL
  - `stock` INT NOT NULL DEFAULT 0
- `customer_order`
  - `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY
  - `customer_id` BIGINT UNSIGNED NOT NULL, indexed via `idx_order_customer (customer_id, created_at)`
  - `status` ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
  - `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
  - `total_cents` INT UNSIGNED NOT NULL DEFAULT 0
- `order_item`
  - `order_id` BIGINT UNSIGNED NOT NULL, part of composite PRIMARY KEY (`order_id`, `product_id`)
  - `product_id` INT UNSIGNED NOT NULL, part of composite PRIMARY KEY
  - `quantity` SMALLINT UNSIGNED NOT NULL
  - `unit_price_cents` INT UNSIGNED NOT NULL
- Indexes: `uk_product_sku`, `idx_order_customer`, PK on `order_item`.

#### Reads
- `product.product_id`, `product.price_cents`, `product.sku`, `product.stock` (in `add_order_item`, locked via FOR UPDATE)
- `order_item.quantity`, `order_item.unit_price_cents`, `order_item.order_id` (subquery SUM in procedure)

#### Writes
- `order_item` INSERT with ON DUPLICATE KEY UPDATE on `quantity`
- `product.stock` UPDATE (decrement by `p_qty`)
- `customer_order.total_cents` UPDATE

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (`fk_item_product`)

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Selects `product_id` and `price_cents` by `sku` where `stock >= p_qty`, with row lock (FOR UPDATE).
- Raises SQLSTATE '45000' if no product matches (unknown SKU or insufficient stock).
- Inserts into `order_item`; on duplicate key adds `p_qty` to `quantity`.
- Decrements `product.stock` by `p_qty`.
- Recomputes `customer_order.total_cents` as SUM(`quantity` * `unit_price_cents`) over `order_item` for the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- Procedure: `add_order_item`
- Constraints: `uk_product_sku`, `idx_order_customer`, `fk_item_order`, `fk_item_product`
