### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:33:35Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop schema modeling products, customers, orders, and order placement with stock reservation and total calculation.

#### Purpose
- Models product catalog, customer orders, and order line items
- Provides order placement procedure with stock reservation and total update

#### Type
- Tables: product, customer_order, order_item
- Procedure: add_order_item
- Dialect: MySQL 8

#### Schema
- product: product_id INT UNSIGNED PK AUTO_INCREMENT, sku VARCHAR(40) NOT NULL UNIQUE, name VARCHAR(200) NOT NULL, price_cents INT UNSIGNED NOT NULL, stock INT NOT NULL DEFAULT 0
- customer_order: order_id BIGINT UNSIGNED PK AUTO_INCREMENT, customer_id BIGINT UNSIGNED NOT NULL, status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW', created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, total_cents INT UNSIGNED NOT NULL DEFAULT 0, KEY idx_order_customer (customer_id, created_at)
- order_item: order_id BIGINT UNSIGNED NOT NULL, product_id INT UNSIGNED NOT NULL, quantity SMALLINT UNSIGNED NOT NULL, unit_price_cents INT UNSIGNED NOT NULL, PK (order_id, product_id), FK fk_item_order order_id -> customer_order.order_id ON DELETE CASCADE, FK fk_item_product product_id -> product.product_id
- All tables ENGINE=InnoDB DEFAULT CHARSET=utf8mb4

#### Reads
- product: sku, price_cents, stock
- customer_order: order_id, total_cents
- order_item: order_id, product_id, quantity, unit_price_cents

#### Writes
- order_item: order_id, product_id, quantity, unit_price_cents
- product: stock
- customer_order: total_cents

#### Relationships
- order_item.order_id -> customer_order.order_id
- order_item.product_id -> product.product_id

#### Routine logic
- Parameters: p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED
- Selects product_id and price_cents from product where sku matches and stock sufficient FOR UPDATE
- Signals error if product not found or stock insufficient
- Inserts into order_item or updates quantity on duplicate key
- Decrements product stock
- Recalculates customer_order total_cents from order_item sum

#### Dependencies
- product, customer_order, order_item, add_order_item
