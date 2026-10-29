### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:58:51Z
- G: 
- A: 0.0.0
- X: file
---
> This schema models a web shop system managing products, customer orders, and the items within those orders.

#### Purpose
* Defines core entities: products, customer orders, and order line items.
* Provides a stored procedure to atomically add items to an order, reserve stock, and update the order total.

#### Type
table, procedure (MySQL)

#### Schema
product
product_id INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
sku VARCHAR(40) NOT NULL (UNIQUE KEY uk_product_sku)
name VARCHAR(200) NOT NULL
price_cents INT UNSIGNED NOT NULL
stock INT NOT NULL DEFAULT 0

customer_order
order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
customer_id BIGINT UNSIGNED NOT NULL
status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
total_cents INT UNSIGNED NOT NULL DEFAULT 0
(Index idx_order_customer on customer_id, created_at)

order_item
order_id BIGINT UNSIGNED NOT NULL (PK, FK)
product_id INT UNSIGNED NOT NULL (PK, FK)
quantity SMALLINT UNSIGNED NOT NULL
unit_price_cents INT UNSIGNED NOT NULL
(Primary Key: order_id, product_id)

#### Reads
product: sku, product_id, price_cents, stock (within add_order_item)
order_item: quantity, unit_price_cents (within add_order_item, for total calculation)
customer_order: order_id (within add_order_item, for total update)

#### Writes
order_item: order_id, product_id, quantity, unit_price_cents (INSERT/UPDATE via ON DUPLICATE KEY)
product: stock (UPDATE via add_order_item)
customer_order: total_cents (UPDATE via add_order_item)

#### Relationships
order_item.order_id -> customer_order.order_id
order_item.product_id -> product.product_id

#### Routine logic
add_order_item(p_order_id, p_sku, p_qty):
* Selects product_id and price_cents from product where sku matches and stock >= p_qty, locking the row.
* Signals error if product not found or stock insufficient.
* Inserts/updates order_item for the given order_id, product_id, quantity, and unit_price_cents.
* Decrements stock in product for the matched product_id by p_qty.
* Updates customer_order.total_cents by summing (quantity * unit_price_cents) from order_item for p_order_id.

#### Dependencies
product
customer_order
order_item
