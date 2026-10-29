### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:11:14Z
- G: 
- A: 0.0.0
- X: file
---
> This file models a web shop's inventory, customer orders, and the transactional logic for adding items to an order while managing stock levels.

#### Purpose
*   Manages product catalog, customer orders, and order line items.
*   Handles atomic order item insertion, stock deduction, and order total recalculation.

#### Type
Table, Procedure (MySQL 8)

#### Schema
product
*   product_id INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
*   sku VARCHAR(40) NOT NULL (UNIQUE)
*   name VARCHAR(200) NOT NULL
*   price_cents INT UNSIGNED NOT NULL
*   stock INT NOT NULL DEFAULT 0

customer_order
*   order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
*   customer_id BIGINT UNSIGNED NOT NULL
*   status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
*   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
*   total_cents INT UNSIGNED NOT NULL DEFAULT 0
*   Index: idx_order_customer (customer_id, created_at)

order_item
*   order_id BIGINT UNSIGNED NOT NULL (PK, FK)
*   product_id INT UNSIGNED NOT NULL (PK, FK)
*   quantity SMALLINT UNSIGNED NOT NULL
*   unit_price_cents INT UNSIGNED NOT NULL

#### Reads
*   product: product_id, price_cents, stock (filtered by sku)
*   order_item: quantity, unit_price_cents (aggregated for total_cents calculation)

#### Writes
*   order_item: INSERT (order_id, product_id, quantity, unit_price_cents)
*   product: UPDATE stock
*   customer_order: UPDATE total_cents

#### Relationships
*   order_item.order_id -> customer_order.order_id
*   order_item.product_id -> product.product_id

#### Routine logic
*   Procedure: add_order_item
*   Parameters: p_order_id (BIGINT UNSIGNED), p_sku (VARCHAR(40)), p_qty (SMALLINT UNSIGNED)
*   Steps:
    *   Selects product_id and price_cents from product where sku matches and stock is sufficient (FOR UPDATE lock).
    *   Signals error if SKU is invalid or stock is insufficient.
    *   Inserts into order_item; updates quantity if the order_id/product_id pair already exists.
    *   Decrements product stock by p_qty.
    *   Recalculates and updates customer_order.total_cents by summing all order_item values for that order_id.

#### Dependencies
*   product
*   customer_order
*   order_item
