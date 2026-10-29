### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:35Z
- G: 
- A: 0.0.0
- X: file
---
> Adds an item to an order, reserves product stock, and updates the order total in a MySQL 8 webshop.

#### Purpose
- Insert or update an order line item.
- Reserve product stock.
- Recalculate order total.

#### Type
Procedure – MySQL 8.

#### Schema
- **product**  
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT PK`  
  - `sku VARCHAR(40) NOT NULL UNIQUE`  
  - `name VARCHAR(200) NOT NULL`  
  - `price_cents INT UNSIGNED NOT NULL`  
  - `stock INT NOT NULL DEFAULT 0`
- **customer_order**  
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PK`  
  - `customer_id BIGINT UNSIGNED NOT NULL`  
  - `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`  
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`  
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`  
  - Index `idx_order_customer (customer_id, created_at)`
- **order_item**  
  - `order_id BIGINT UNSIGNED NOT NULL`  
  - `product_id INT UNSIGNED NOT NULL`  
  - `quantity SMALLINT UNSIGNED NOT NULL`  
  - `unit_price_cents INT UNSIGNED NOT NULL`  
  - PK `(order_id, product_id)`  
  - FK `fk_item_order (order_id) REFERENCES customer_order(order_id) ON DELETE CASCADE`  
  - FK `fk_item_product (product_id) REFERENCES product(product_id)`

#### Reads
- `product` – `product_id`, `price_cents` where `sku = p_sku AND stock >= p_qty` (FOR UPDATE).
- `order_item` – `quantity`, `unit_price_cents` for `order_id = p_order_id` (subquery in total calculation).

#### Writes
- `order_item` – INSERT or UPDATE `quantity`.
- `product` – UPDATE `stock = stock - p_qty`.
- `customer_order` – UPDATE `total_cents` with sum of line totals.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK, cascade delete).
- `order_item.product_id -> product.product_id` (FK).

#### Routine logic
- **Parameters**: `p_order_id BIGINT UNSIGNED`, `p_sku VARCHAR(40)`, `p_qty SMALLINT UNSIGNED`.
- **Steps**:
  1. SELECT `product_id`, `price_cents` into local vars with `FOR UPDATE` to lock row.
  2. If no product or insufficient stock → SIGNAL error.
  3. INSERT into `order_item`; on duplicate key, increment `quantity`.
  4. Decrement `product.stock` by `p_qty`.
  5. Recalculate `customer_order.total_cents` as sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`.
