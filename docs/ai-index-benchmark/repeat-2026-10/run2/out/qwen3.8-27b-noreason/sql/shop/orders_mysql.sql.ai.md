### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:38Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop schema and procedure for managing product inventory, customer orders, and order line items with stock reservation and total recalculation.

#### Purpose
- Defines the core e-commerce data model: `product` catalog, `customer_order` headers, and `order_item` line items.
- Provides the `add_order_item` procedure to atomically add a line item, decrement stock, and update the order total.

#### Type
- Tables: `product`, `customer_order`, `order_item` (MySQL 8, InnoDB, utf8mb4).
- Procedure: `add_order_item` (MySQL 8).

#### Schema
- `product`
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` (PK)
  - `sku VARCHAR(40) NOT NULL` (UNIQUE `uk_product_sku`)
  - `name VARCHAR(200) NOT NULL`
  - `price_cents INT UNSIGNED NOT NULL`
  - `stock INT NOT NULL DEFAULT 0`
- `customer_order`
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` (PK)
  - `customer_id BIGINT UNSIGNED NOT NULL` (Index `idx_order_customer` on `customer_id, created_at`)
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'`
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
- `order_item`
  - `order_id BIGINT UNSIGNED NOT NULL` (PK part 1, FK)
  - `product_id INT UNSIGNED NOT NULL` (PK part 2, FK)
  - `quantity SMALLINT UNSIGNED NOT NULL`
  - `unit_price_cents INT UNSIGNED NOT NULL`

#### Reads
- `add_order_item` reads `product.product_id`, `product.price_cents` filtered by `sku` and `stock`.
- `add_order_item` reads `order_item.quantity`, `order_item.unit_price_cents` for total calculation.

#### Writes
- `add_order_item` inserts into `order_item` (`order_id`, `product_id`, `quantity`, `unit_price_cents`) or updates `quantity` on duplicate.
- `add_order_item` updates `product.stock`.
- `add_order_item` updates `customer_order.total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (CASCADE delete)
- `order_item.product_id -> product.product_id`

#### Routine logic
- `add_order_item(p_order_id, p_sku, p_qty)`:
  - Locks matching `product` row with `FOR UPDATE` where `sku = p_sku` and `stock >= p_qty`.
  - Signals SQLSTATE `45000` if no product found or insufficient stock.
  - Inserts `order_item` row; on duplicate key, increments `quantity` by `p_qty`.
  - Decrements `product.stock` by `p_qty`.
  - Recalculates `customer_order.total_cents` as `SUM(quantity * unit_price_cents)` for the order.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
