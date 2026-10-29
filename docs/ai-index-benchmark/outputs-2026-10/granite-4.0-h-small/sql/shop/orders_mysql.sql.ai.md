### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:09:19Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop: products, orders and order placement (MySQL 8).

#### Purpose
- Models a simple e‑commerce domain: products, customer orders, and order items.  
- Provides a stored procedure to add an item to an order while reserving stock and updating totals.

#### Type
- **Tables**: `product`, `customer_order`, `order_item`  
- **Procedure**: `add_order_item`

#### Schema
**product**
- `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)  
- `sku` VARCHAR(40) NOT NULL (UK)  
- `name` VARCHAR(200) NOT NULL  
- `price_cents` INT UNSIGNED NOT NULL  
- `stock` INT NOT NULL DEFAULT 0  

**customer_order**
- `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)  
- `customer_id` BIGINT UNSIGNED NOT NULL  
- `status` ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'  
- `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP  
- `total_cents` INT UNSIGNED NOT NULL DEFAULT 0  
- Index: `idx_order_customer(customer_id, created_at)`

**order_item**
- `order_id` BIGINT UNSIGNED NOT NULL (FK → `customer_order.order_id` ON DELETE CASCADE)  
- `product_id` INT UNSIGNED NOT NULL (FK → `product.product_id`)  
- `quantity` SMALLINT UNSIGNED NOT NULL  
- `unit_price_cents` INT UNSIGNED NOT NULL  
- PK: `(order_id, product_id)`

#### Reads
- `add_order_item` reads `product` (sku, product_id, price_cents, stock) and `customer_order` (total_cents) for validation and update.

#### Writes
- `add_order_item` writes `order_item` (insert/ON DUPLICATE KEY UPDATE), `product` (stock decrement), `customer_order` (total_cents recalculation).

#### Relationships
- `order_item.order_id → customer_order.order_id` (FK, cascade delete)  
- `order_item.product_id → product.product_id` (FK)  
- `customer_order.customer_id` (FK not shown, but referenced by `customer_id` column)

#### Routine logic
- **Parameters**: `p_order_id` (BIGINT UNSIGNED), `p_sku` (VARCHAR(40)), `p_qty` (SMALLINT UNSIGNED)  
- **Logic**:
  1. Look up `product` by `sku` with `stock >= p_qty` under `FOR UPDATE`.  
  2. If no product or insufficient stock → `SIGNAL` error.  
  3. Insert into `order_item` (or update quantity if row exists).  
  4. Decrement `product.stock` by `p_qty`.  
  5. Recompute `customer_order.total_cents` as `SUM(quantity * unit_price_cents)` for the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`  
- No external sequences or other objects referenced.
