### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:58:46Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop schema and procedure for managing product inventory, customer orders, and order line items with stock reservation and total recalculation.

#### Purpose
- Defines the core e-commerce data model: `product` catalog, `customer_order` headers, and `order_item` line items.
- Provides a stored procedure `add_order_item` to atomically add a product to an order, decrement stock, and update the order total.

#### Type
- Tables: `product`, `customer_order`, `order_item` (MySQL 8, InnoDB).
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
- `add_order_item` reads `product.product_id`, `product.price_cents` where `sku = p_sku` and `stock >= p_qty` (with `FOR UPDATE` lock).
- `add_order_item` reads `order_item.quantity`, `order_item.unit_price_cents` to recalculate `total_cents`.

#### Writes
- `add_order_item` writes to `order_item` (INSERT or UPDATE on duplicate key).
- `add_order_item` writes to `product.stock` (decrement).
- `add_order_item` writes to `customer_order.total_cents` (recalculate sum).

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (FK `fk_item_product`)

#### Routine logic
- `add_order_item(p_order_id, p_sku, p_qty)`:
  - Locks matching `product` row by `sku` and checks `stock >= p_qty`.
  - Signals SQLSTATE `45000` if product not found or insufficient stock.
  - Inserts into `order_item` or increments `quantity` if duplicate key.
  - Decrements `product.stock` by `p_qty`.
  - Updates `customer_order.total_cents` with `SUM(quantity * unit_price_cents)` for the order.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
