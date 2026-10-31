### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:55:12Z
- G: 
- A: 0.0.0
- X: file
---
> Models a web shop's inventory and order fulfillment system, including product management, customer orders, and order line items.

#### Purpose
* Manages product catalog, stock levels, and customer order lifecycle.
* Provides transactional logic for adding items to orders while maintaining stock integrity and order totals.

#### Type
Table, Procedure (MySQL)

#### Schema
product
* product_id INT UNSIGNED (PK, AUTO_INCREMENT)
* sku VARCHAR(40) (UNIQUE, NOT NULL)
* name VARCHAR(200) (NOT NULL)
* price_cents INT UNSIGNED (NOT NULL)
* stock INT (NOT NULL, DEFAULT 0)

customer_order
* order_id BIGINT UNSIGNED (PK, AUTO_INCREMENT)
* customer_id BIGINT UNSIGNED (NOT NULL)
* status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') (NOT NULL, DEFAULT 'NEW')
* created_at DATETIME (NOT NULL, DEFAULT CURRENT_TIMESTAMP)
* total_cents INT UNSIGNED (NOT NULL, DEFAULT 0)
* idx_order_customer (INDEX on customer_id, created_at)

order_item
* order_id BIGINT UNSIGNED (PK, FK)
* product_id INT UNSIGNED (PK, FK)
* quantity SMALLINT UNSIGNED (NOT NULL)
* unit_price_cents INT UNSIGNED (NOT NULL)

#### Reads
* product: product_id, price_cents, sku, stock
* order_item: quantity, unit_price_cents
* customer_order: order_id

#### Writes
* product: stock
* order_item: order_id, product_id, quantity, unit_price_cents
* customer_order: total_cents

#### Relationships
* order_item.order_id -> customer_order.order_id
* order_item.product_id -> product.product_id

#### Routine logic
add_order_item
* Parameters: p_order_id (BIGINT), p_sku (VARCHAR), p_qty (SMALLINT)
* Validates product existence and sufficient stock via `product.sku` and `product.stock`.
* Inserts a new line into `order_item` or increments `quantity` if the product already exists in the order.
* Decrements `product.stock` by the ordered quantity.
* Recalculates and updates `customer_order.total_cents` using the sum of all items in the order.
* Throws error if SKU is unknown or stock is insufficient.

#### Dependencies
* product
* customer_order
* order_item
