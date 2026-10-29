### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:57:13Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop procedure to add items to an order while validating stock, reserving inventory, and recalculating order totals.

#### Purpose
- Adds a line item to an existing order, validates SKU existence and stock availability, and updates inventory and totals atomically.
- Handles duplicate line items by increasing quantity rather than creating new rows.

#### Type
MySQL Stored Procedure (`add_order_item`)

#### Schema
**Table: product**
- `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
- `sku` VARCHAR(40) NOT NULL (UNIQUE uk_product_sku)
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
- `order_id` BIGINT UNSIGNED NOT NULL (PK part 1, FK to customer_order)
- `product_id` INT UNSIGNED NOT NULL (PK part 2, FK to product)
- `quantity` SMALLINT UNSIGNED NOT NULL
- `unit_price_cents` INT UNSIGNED NOT NULL
- Constraints: `fk_item_order` (ON DELETE CASCADE), `fk_item_product`

#### Reads
- `product` (columns: `sku`, `stock`, `product_id`, `price_cents`) via SELECT with `FOR UPDATE`
- `order_item` (columns: `order_id`, `quantity`, `unit_price_cents`) via subquery in UPDATE

#### Writes
- `order_item` (columns: `order_id`, `product_id`, `quantity`, `unit_price_cents`) via INSERT ... ON DUPLICATE KEY UPDATE
- `product` (column: `stock`) via UPDATE
- `customer_order` (column: `total_cents`) via UPDATE

#### Relationships
- `order_item.order_id` -> `customer_order.order_id`
- `order_item.product_id` -> `product.product_id`

#### Routine logic
- Input parameters: `p_order_id` (BIGINT), `p_sku` (VARCHAR), `p_qty` (SMALLINT)
- Locks and retrieves `product_id` and `price_cents` for the given `sku` where `stock >= p_qty`
- Raises error if product not found or insufficient stock
- Inserts new row into `order_item` or updates existing row quantity
- Decrements `product.stock` by `p_qty`
- Recalculates `customer_order.total_cents` as sum of `quantity * unit_price_cents` for the order

#### Dependencies
- `product`
- `customer_order`
- `order_item`
