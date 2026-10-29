### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:05Z
- G: 
- A: 0.0.0
- X: file
---
> This MySQL file models a web shop's product catalog and order placement, defining tables for products, customers, and order items, plus a procedure to add items while managing stock and order totals.

#### Purpose
- Defines the schema for a web shop: products, customer orders, and order line items.
- Provides a procedure to add items to an order, validate stock, and update order totals.

#### Type
procedure (MySQL 8)

#### Schema
- **product**: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT`, `sku VARCHAR(40) NOT NULL`, `name VARCHAR(200) NOT NULL`, `price_cents INT UNSIGNED NOT NULL`, `stock INT NOT NULL DEFAULT 0`; PRIMARY KEY (product_id), UNIQUE KEY uk_product_sku (sku)
- **customer_order**: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT`, `customer_id BIGINT UNSIGNED NOT NULL`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`, `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`, `total_cents INT UNSIGNED NOT NULL DEFAULT 0`; PRIMARY KEY (order_id), KEY idx_order_customer (customer_id, created_at)
- **order_item**: `order_id BIGINT UNSIGNED NOT NULL`, `product_id INT UNSIGNED NOT NULL`, `quantity SMALLINT UNSIGNED NOT NULL`, `unit_price_cents INT UNSIGNED NOT NULL`; PRIMARY KEY (order_id, product_id), CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES customer_order (order_id) ON DELETE CASCADE, CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product (product_id)

#### Reads
- product (sku, stock, price_cents) via SELECT ... FOR UPDATE in add_order_item
- customer_order (order_id, total_cents) via UPDATE in add_order_item
- order_item (order_id, product_id, quantity, unit_price_cents) via SELECT SUM(...) in add_order_item

#### Writes
- product: stock decremented by p_qty in add_order_item
- order_item: inserted (or updated via ON DUPLICATE KEY) with order_id, product_id, quantity, unit_price_cents in add_order_item
- customer_order: total_cents recalculated as SUM(quantity * unit_price_cents) for the order in add_order_item

#### Relationships
- order_item.order_id -> customer_order.order_id (fk_item_order, ON DELETE CASCADE)
- order_item.product_id -> product.product_id (fk_item_product)

#### Routine logic
- Procedure add_order_item takes p_order_id, p_sku, p_qty
- Selects product_id and price_cents from product where sku matches and stock >= qty, locking row
- If no product found, raises error 'unknown SKU or not enough stock'
- Inserts a new order_item row (or increments quantity if duplicate key)
- Decrements product stock by p_qty
- Recalculates and updates customer_order total_cents from order_item sum

#### Dependencies
- product (table, referenced by fk_item_product and procedure SELECT)
- customer_order (table, referenced by fk_item_order and procedure UPDATE)
- order_item (table, referenced by procedure INSERT/UPDATE and SUM subquery)
