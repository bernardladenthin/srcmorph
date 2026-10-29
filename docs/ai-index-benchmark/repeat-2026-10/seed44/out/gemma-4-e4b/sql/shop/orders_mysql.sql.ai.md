### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:57Z
- G: 
- A: 0.0.0
- X: file
---
> This schema models product inventory, customer orders, and the process of adding items to an order while managing stock levels.

#### Purpose
* Define product catalog and inventory.
* Track customer orders and their status.
* Manage line items within an order, including stock reservation and total calculation.

#### Type
table / procedure (MySQL)

#### Schema
product:
product_id INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
sku VARCHAR(40) NOT NULL (UNIQUE)
name VARCHAR(200) NOT NULL
price_cents INT UNSIGNED NOT NULL
stock INT NOT NULL DEFAULT 0

customer_order:
order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
customer_id BIGINT UNSIGNED NOT NULL
status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
total_cents INT UNSIGNED NOT NULL DEFAULT 0

order_item:
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
* Selects product_id and price_cents from product where sku matches p_sku and stock >= p_qty, locking the row.
* If product not found or stock insufficient, signals error.
* Inserts/updates order_item for p_order_id, p_sku, p_qty using ON DUPLICATE KEY UPDATE.
* Decrements stock in product for the found product_id by p_qty.
* Updates customer_order.total_cents for p_order_id by summing quantities * unit_price_cents from order_item.

#### Dependencies
product
customer_order
order_item
