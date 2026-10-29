### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:53:47Z
- G: 
- A: 0.0.0
- X: file
---
> A web shop model for products, orders, and order placement with stock reservation and total calculation.

#### Purpose
- Model shop inventory and order flow.
- Provide a procedure to add items to an order, enforce stock, and recalculate order totals.

#### Type
Procedure (MySQL 8)

#### Schema
- **product**: `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT, sku VARCHAR(40) NOT NULL, name VARCHAR(200) NOT NULL, price_cents INT UNSIGNED NOT NULL, stock INT NOT NULL DEFAULT 0, PRIMARY KEY (product_id), UNIQUE KEY uk_product_sku (sku)`
- **customer_order**: `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, customer_id BIGINT UNSIGNED NOT NULL, status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, total_cents INT UNSIGNED NOT NULL DEFAULT 0, PRIMARY KEY (order_id), KEY idx_order_customer (customer_id, created_at)`
- **order_item**: `order_id BIGINT UNSIGNED NOT NULL, product_id INT UNSIGNED NOT NULL, quantity SMALLINT UNSIGNED NOT NULL, unit_price_cents INT UNSIGNED NOT NULL, PRIMARY KEY (order_id, product_id), CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES customer_order (order_id) ON DELETE CASCADE, CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product (product_id)`

#### Reads
- `product` columns: `sku, price_cents, stock`
- `order_item` columns: `order_id, product_id, quantity, unit_price_cents` (subquery in UPDATE)

#### Writes
- `order_item`: INSERT (order_id, product_id, quantity, unit_price_cents) with ON DUPLICATE KEY UPDATE quantity = quantity + p_qty
- `product`: UPDATE stock = stock - p_qty WHERE product_id = v_product_id
- `customer_order`: UPDATE total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id) WHERE order_id = p_order_id

#### Relationships
- `order_item.fk_item_order`: `order_item.order_id -> customer_order.order_id`
- `order_item.fk_item_product`: `order_item.product_id -> product.product_id`

#### Routine logic
- Declare local variables v_product_id and v_price.
- SELECT product_id, price_cents FROM product WHERE sku = p_sku AND stock >= p_qty FOR UPDATE.
- If no row found, SIGNAL SQLSTATE '45000' 'unknown SKU or not enough stock'.
- INSERT INTO order_item (order_id, product_id, quantity, unit_price_cents) VALUES (p_order_id, v_product_id, p_qty, v_price) ON DUPLICATE KEY UPDATE quantity = quantity + p_qty.
- UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id.
- UPDATE customer_order SET total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id) WHERE order_id = p_order_id.

#### Dependencies
- Tables: product, customer_order, order_item
- Procedure: add_order_item
