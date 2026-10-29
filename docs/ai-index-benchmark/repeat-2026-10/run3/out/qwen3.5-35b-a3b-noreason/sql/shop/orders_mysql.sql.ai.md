### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:15Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop procedure to add items to an order, reserve inventory, and recalculate totals.

#### Purpose
- Adds a line item to an existing order while validating stock availability and reserving inventory.
- Maintains data consistency by updating product stock levels and recalculating the order total.

#### Type
MySQL Stored Procedure (`add_order_item`)

#### Schema
**Table: product**
- `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
- `sku` VARCHAR(40) NOT NULL (UNIQUE)
- `name` VARCHAR(200) NOT NULL
- `price_cents` INT UNSIGNED NOT NULL
- `stock` INT NOT NULL DEFAULT 0

**Table: customer_order**
- `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
- `customer_id` BIGINT UNSIGNED NOT NULL
- `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
- `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
- `total_cents` INT UNSIGNED NOT NULL DEFAULT 0
- Index: `idx_order_customer` on (`customer_id`, `created_at`)

**Table: order_item**
- `order_id` BIGINT UNSIGNED NOT NULL (PK, FK)
- `product_id` INT UNSIGNED NOT NULL (PK, FK)
- `quantity` SMALLINT UNSIGNED NOT NULL
- `unit_price_cents` INT UNSIGNED NOT NULL

#### Reads
- `product` (columns: `sku`, `stock`, `product_id`, `price_cents`) via SELECT with `FOR UPDATE` lock.
- `order_item` (columns: `order_id`, `quantity`, `unit_price_cents`) via subquery for total calculation.
- Inputs: `p_order_id`, `p_sku`, `p_qty`.

#### Writes
- `order_item` (columns: `order_id`, `product_id`, `quantity`, `unit_price_cents`) via INSERT or UPDATE.
- `product` (column: `stock`) via UPDATE.
- `customer_order` (column: `total_cents`) via UPDATE.

#### Relationships
- `order_item.order_id` -> `customer_order.order_id` (ON DELETE CASCADE)
- `order_item.product_id` -> `product.product_id`

#### Routine logic
- Declares local variables `v_product_id` and `v_price`.
- Selects `product_id` and `price_cents` from `product` matching `p_sku` with sufficient `stock`, locking rows.
- Signals error if product not found or stock insufficient.
- Inserts new row into `order_item` or updates `quantity` if `order_id` + `product_id` exists.
- Decrements `product.stock` by `p_qty`.
- Recalculates `customer_order.total_cents` as sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
