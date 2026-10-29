### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:15Z
- G: 
- A: 0.0.0
- X: file
---
> A web shop schema modeling products, customers, and order placement with stock reservation and total calculation.

#### Purpose
- Defines the data model for a web shop: products, customer orders, and order items.
- Provides a procedure to add items to an order, enforce stock availability, and recalculate order totals.

#### Type
procedure (MySQL 8)

#### Schema
- **product**: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT [PK]`, `sku VARCHAR(40) NOT NULL [UNIQUE]`, `name VARCHAR(200) NOT NULL`, `price_cents INT UNSIGNED NOT NULL`, `stock INT NOT NULL DEFAULT 0`
- **customer_order**: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT [PK]`, `customer_id BIGINT UNSIGNED NOT NULL`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`, `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`, `total_cents INT UNSIGNED NOT NULL DEFAULT 0`, `KEY idx_order_customer (customer_id, created_at)`
- **order_item**: `order_id BIGINT UNSIGNED NOT NULL`, `product_id INT UNSIGNED NOT NULL`, `quantity SMALLINT UNSIGNED NOT NULL`, `unit_price_cents INT UNSIGNED NOT NULL`, `PRIMARY KEY (order_id, product_id)`, `fk_item_order FOREIGN KEY (order_id) REFERENCES customer_order (order_id) ON DELETE CASCADE`, `fk_item_product FOREIGN KEY (product_id) REFERENCES product (product_id)`

#### Reads
- `product` via `sku` and stock check (`WHERE sku = p_sku AND stock >= p_qty`)
- `order_item` via `order_id` for total recalculation (`WHERE order_id = p_order_id`)

#### Writes
- `order_item`: INSERT (or UPDATE via ON DUPLICATE KEY) with `order_id`, `product_id`, `quantity`, `unit_price_cents`
- `product`: UPDATE `stock = stock - p_qty` where `product_id = v_product_id`
- `customer_order`: UPDATE `total_cents` via subquery `SUM(quantity * unit_price_cents)` where `order_id = p_order_id`

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (FK)
- `customer_order.customer_id` references customers (table not defined in file)

#### Routine logic
- Declares local variables `v_product_id` and `v_price` to hold selected product data.
- Selects `product_id` and `price_cents` from `product` where `sku` matches and `stock` is sufficient, using `FOR UPDATE` to lock the row.
- If no product found, raises an error 'unknown SKU or not enough stock'.
- Inserts a new row into `order_item` (or increments `quantity` if duplicate key exists) with the selected `product_id` and `price`.
- Decrements `product.stock` by the ordered quantity.
- Recalculates and updates `customer_order.total_cents` as the sum of `quantity * unit_price_cents` for the given order.

#### Dependencies
- `product` table (referenced by SKU and stock check)
- `order_item` table (inserted into, updated via ON DUPLICATE KEY, read for total)
- `customer_order` table (updated total_cents)
