### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:21:57Z
- G: 
- A: 0.0.0
- X: file
---
> Manages web shop operations: products, orders, and order placement in MySQL.

#### Purpose
- Models web shop data: products, orders, and order items
- Handles order placement and stock management

#### Type
Procedure / Table

#### Schema
##### product
- product_id INT UNSIGNED PK, NOT NULL, AUTO_INCREMENT
- sku VARCHAR(40) UK, NOT NULL
- name VARCHAR(200) NOT NULL
- price_cents INT UNSIGNED NOT NULL
- stock INT NOT NULL, DEFAULT 0

##### customer_order
- order_id BIGINT UNSIGNED PK, NOT NULL, AUTO_INCREMENT
- customer_id BIGINT UNSIGNED, NOT NULL
- status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL, DEFAULT 'NEW'
- created_at DATETIME NOT NULL, DEFAULT CURRENT_TIMESTAMP
- total_cents INT UNSIGNED NOT NULL, DEFAULT 0
- idx_order_customer (customer_id, created_at)

##### order_item
- order_id BIGINT UNSIGNED PK, NOT NULL
- product_id INT UNSIGNED PK, NOT NULL
- quantity SMALLINT UNSIGNED NOT NULL
- unit_price_cents INT UNSIGNED NOT NULL
- fk_item_order (order_id) -> customer_order(order_id)
- fk_item_product (product_id) -> product(product_id)

#### Reads
- product (sku, price_cents, stock)
- customer_order (order_id, total_cents)
- order_item (order_id, product_id, quantity, unit_price_cents)

#### Writes
- product (stock)
- customer_order (total_cents)
- order_item (order_id, product_id, quantity, unit_price_cents)

#### Routine logic
- add_order_item:
  - Takes order_id, SKU, and quantity as input
  - Checks product availability and stock
  - Adds or updates order item
  - Updates product stock and order total

#### Dependencies
- product table
- customer_order table
