### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:07:35Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines the schema and a procedure for a MySQL 8 web shop, modeling products, customers, orders, and order items with stock management.

#### Purpose
- Models a web shop data model with products, customers, and orders.
- Provides a stored procedure to add order items and maintain stock and order totals.

#### Type
Tables and procedure (MySQL 8)

#### Schema
- `product`: `product_id` INT UNSIGNED NOT NULL (PK), `sku` VARCHAR(40) NOT NULL (UNIQUE), `name` VARCHAR(200) NOT NULL, `price_cents` INT UNSIGNED NOT NULL, `stock` INT NOT NULL DEFAULT 0
- `customer_order`: `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK), `customer_id` BIGINT UNSIGNED NOT NULL, `status` ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW', `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, `total_cents` INT UNSIGNED NOT NULL DEFAULT 0, Index `idx_order_customer` on (`customer_id`, `created_at`)
- `order_item`: `order_id` BIGINT UNSIGNED NOT NULL, `product_id` INT UNSIGNED NOT NULL, `quantity` SMALLINT UNSIGNED NOT NULL, `unit_price_cents` INT UNSIGNED NOT NULL, PRIMARY KEY (`order_id`, `product_id`), FK `order_item.order_id` → `customer_order.order_id` ON DELETE CASCADE, FK `order_item.product_id` → `product.product_id`

#### Reads
- `product` table columns `sku`, `stock` for SKU lookup and stock availability check.
- `order_item` table via subquery to compute order total.
- Input parameters `p_order_id`, `p_sku`, `p_qty`.

#### Writes
- INSERT INTO `order_item` (`order_id`, `product_id`, `quantity`, `unit_price_cents`).
- UPDATE `product` SET `stock` = `stock` - p_qty WHERE `product_id` = v_product_id.
- UPDATE `customer_order` SET `total_cents` = (SELECT SUM(`quantity` * `unit_price_cents`) FROM `order_item` WHERE `order_id` = p_order_id) WHERE `order_id` = p_order_id.

#### Relationships
- `order_item.order_id` → `customer_order.order_id` (ON DELETE CASCADE)
- `order_item.product_id` → `product.product_id`

#### Routine logic
- Inputs: p_order_id, p_sku, p_qty.
- SELECT product_id, price_cents INTO v_product_id, v_price FROM product WHERE sku = p_sku AND stock >= p_qty FOR UPDATE.
- IF v_product_id IS NULL THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'unknown SKU or not enough stock'; END IF.
- INSERT INTO order_item ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty.
- UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id.
- UPDATE customer_order SET total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id) WHERE order_id = p_order_id.

#### Dependencies
- product
- customer_order
- order_item
