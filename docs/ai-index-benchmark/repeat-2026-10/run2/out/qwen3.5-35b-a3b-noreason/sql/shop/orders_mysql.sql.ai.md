### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:58Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop procedure to add line items to an order while validating stock and updating totals.

#### Purpose
- Adds a product line to an existing order, reserving inventory and recalculating the order total.
- Enforces stock availability and SKU existence before modifying data.

#### Type
MySQL Stored Procedure (`add_order_item`).

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
- `product` (columns: `sku`, `stock`, `product_id`, `price_cents`) via `SELECT ... FOR UPDATE`.
- `order_item` (columns: `order_id`, `quantity`, `unit_price_cents`) via subquery in `UPDATE`.
- Input parameters: `p_order_id`, `p_sku`, `p_qty`.

#### Writes
- `order_item` (columns: `order_id`, `product_id`, `quantity`, `unit_price_cents`) via `INSERT ... ON DUPLICATE KEY UPDATE`.
- `product` (column: `stock`) via `UPDATE`.
- `customer_order` (column: `total_cents`) via `UPDATE`.

#### Relationships
- `order_item.order_id` -> `customer_order.order_id`
- `order_item.product_id` -> `product.product_id`

#### Routine logic
- Selects `product_id` and `price_cents` from `product` matching `p_sku` with sufficient `stock` (row-level lock).
- Signals error if product not found or stock insufficient.
- Inserts new row into `order_item` or increments `quantity` if `order_id` + `product_id` exists.
- Decrements `product.stock` by `p_qty`.
- Recalculates `customer_order.total_cents` by summing `quantity * unit_price_cents` for the order.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
