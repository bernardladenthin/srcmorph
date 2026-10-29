### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:35:38Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a web shop's product catalog, customer orders, and the atomic process of adding items to orders while reserving stock.

#### Purpose
* Models product inventory and customer order tracking.
* Provides a transactional mechanism to add items to an order, update stock levels, and recalculate order totals.

#### Type
* Table (MySQL 8)
* Procedure (MySQL 8)

#### Schema
**product**
* `product_id` INT UNSIGNED (PK, AUTO_INCREMENT)
* `sku` VARCHAR(40) (UNIQUE, NOT NULL)
* `name` VARCHAR(200) (NOT NULL)
* `price_cents` INT UNSIGNED (NOT NULL)
* `stock` INT (NOT NULL, DEFAULT 0)

**customer_order**
* `order_id` BIGINT UNSIGNED (PK, AUTO_INCREMENT)
* `customer_id` BIGINT UNSIGNED (NOT NULL)
* `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') (NOT NULL, DEFAULT 'NEW')
* `created_at` DATETIME (NOT NULL, DEFAULT CURRENT_TIMESTAMP)
* `total_cents` INT UNSIGNED (NOT NULL, DEFAULT 0)
* Index: `idx_order_customer` (`customer_id`, `created_at`)

**order_item**
* `order_id` BIGINT UNSIGNED (PK, FK)
* `product_id` INT UNSIGNED (PK, FK)
* `quantity` SMALLINT UNSIGNED (NOT NULL)
* `unit_price_cents` INT UNSIGNED (NOT NULL)

#### Reads
* `product`: `product_id`, `price_cents`, `stock` (via `sku`)
* `order_item`: `quantity`, `unit_price_cents` (via `order_id`)

#### Writes
* `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents`
* `product`: `stock`
* `customer_order`: `total_cents`

#### Relationships
* `order_item.order_id` -> `customer_order.order_id` (ON DELETE CASCADE)
* `order_item.product_id` -> `product.product_id`

#### Routine logic
**add_order_item**
* Parameters: `p_order_id` (BIGINT UNSIGNED), `p_sku` (VARCHAR), `p_qty` (SMALLINT UNSIGNED).
* Steps:
    * Locks and retrieves `product_id` and `price_cents` from `product` where `sku` matches and `stock` is sufficient.
    * Signals error '45000' if product is missing or stock is insufficient.
    * Inserts record into `order_item` or increments `quantity` if the product already exists in the order.
    * Decrements `product.stock` by `p_qty`.
    * Updates `customer_order.total_cents` by summing all `quantity * unit_price_cents` for the order.

#### Dependencies
* `product`
* `customer_order`
* `order_item`
