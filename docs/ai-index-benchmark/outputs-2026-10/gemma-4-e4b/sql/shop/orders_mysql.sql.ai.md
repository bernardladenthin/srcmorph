### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:58:48Z
- G: 
- A: 0.0.0
- X: file
---
> This schema models product inventory, customer orders, and the process of adding items to an order while managing stock levels.

#### Purpose
* Defines core entities: products, customer orders, and order line items.
* Provides a stored procedure to atomically add items to an order, reserve stock, and update the order total.

#### Type
table / procedure (MySQL)

#### Schema
product
product_id INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
sku VARCHAR(40) NOT NULL (UNIQUE)
name VARCHAR(200) NOT NULL
price_cents INT UNSIGNED NOT NULL
stock INT NOT NULL DEFAULT 0

customer_order
order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
customer_id BIGINT UNSIGNED NOT NULL
status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
total_cents INT UNSIGNED NOT NULL DEFAULT 0

order_item
order_id BIGINT UNSIGNED NOT NULL (PK, FK)
product_id INT UNSIGNED NOT NULL (PK, FK)
quantity SMALLINT UNSIGNED NOT NULL
unit_price_cents INT UNSIGNED NOT NULL

#### Reads
product: sku, stock
customer_order: order_id
order_item: order_id, product_id, quantity, unit_price_cents

#### Writes
order_item: order_id, product_id, quantity, unit_price_cents (INSERT/UPDATE)
product: stock (UPDATE)
customer_order: total_cents (UPDATE)

#### Relationships
order_item.order_id -> customer_order.order_id
order_item.product_id -> product.product_id

#### Routine logic
add_order_item(p_order_id, p_sku, p_qty):
* Selects product_id and price_cents from product where sku matches and stock >= p_qty, locking the row.
* If product not found or stock insufficient, signals error.
* Inserts or updates order_item for the given order_id, product_id, quantity, and unit_price_cents.
* Decrements stock in product for the matched product_id by p_qty.
* Updates customer_order.total_cents by recalculating the sum of (quantity * unit_price_cents) for the specified order_id from order_item.

#### Dependencies
product
customer_order
order_item
