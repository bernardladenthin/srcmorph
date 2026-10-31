### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:52:44Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> The file creates database objects for a web shop's product catalog, customer orders, and order placement logic with stock reservation.

#### Purpose
- Defines tables for products, customer orders, and order items.
- Provides a stored procedure `add_order_item` to insert an item into an order while reserving stock and updating totals.

#### Type
SQL script (MySQL 8) containing tables and a stored procedure.

#### Schema
- `product`:
  - `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PK
  - `sku` VARCHAR(40) NOT NULL UNIQUE
  - `name` VARCHAR(200) NOT NULL
  - `price_cents` INT UNSIGNED NOT NULL
  - `stock` INT NOT NULL DEFAULT 0
- `customer_order`:
  - `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PK
  - `customer_id` BIGINT UNSIGNED NOT NULL
  - `status` ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'
  - `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
  - `total_cents` INT UNSIGNED NOT NULL DEFAULT 0
  - Index `idx_order_customer` on (`customer_id`, `created_at`)
- `order_item`:
  - `order_id` BIGINT UNSIGNED NOT NULL
  - `product_id` INT UNSIGNED NOT NULL
  - `quantity` SMALLINT UNSIGNED NOT NULL
  - `unit_price_cents` INT UNSIGNED NOT NULL
  - Primary key (`order_id`, `product_id`)
  - Foreign key `fk_item_order` → `customer_order(order_id)` ON DELETE CASCADE
  - Foreign key `fk_item_product` → `product(product_id)`

#### Reads
- `product`: read in `add_order_item` procedure (SELECT `product_id`, `price_cents` WHERE `sku` = p_sku AND `stock` >= p_qty)
- `order_item`: read in `add_order_item` procedure (SELECT `SUM(quantity * unit_price_cents)` FROM `order_item` WHERE `order_id` = p_order_id)
- `customer_order`: read in `add_order_item` procedure (UPDATE `customer_order` WHERE `order_id` = p_order_id)

#### Writes
- `order_item`: INSERT (via `INSERT INTO order_item`) and UPDATE (via `ON DUPLICATE KEY UPDATE`) in `add_order_item`
- `product`: UPDATE `stock` = `stock` - p_qty in `add_order_item`
- `customer_order`: UPDATE `total_cents` in `add_order_item`

#### Relationships
- `order_item.order_id` → `customer_order.order_id` (via `fk_item_order`)
- `order_item.product_id` → `product.product_id` (via `fk_item_product`)

#### Routine logic
- `add_order_item(p_order_id, p_sku, p_qty)`:
  - SELECTs `product_id` and `price_cents` from `product` where `sku` matches and `stock` >= `p_qty`, locked for update.
  - If no product found, signals error "unknown SKU or not enough stock".
  - INSERTs into `order_item`; if key exists, adds `p_qty` to existing `quantity` (`ON DUPLICATE KEY UPDATE`).
  - Updates `product.stock` by subtracting `p_qty`.
  - Updates `customer_order.total_cents` by recalculating sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- `product`, `customer_order`, `order_item` (referenced in procedure)
- No external views, functions, or sequences used.
