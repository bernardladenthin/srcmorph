### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:17:57Z
- G: 
- A: 0.0.0
- X: file
---
> Manages web shop orders, products, and order itemization with stock reservation and real-time total calculation.

#### Purpose
- Adds items to customer orders
- Reserves product stock when items are added
- Maintains accurate order totals

#### Type
- table
- procedure

#### Schema

##### product
- product_id int unsigned not null auto_increment
- sku varchar(40) not null
- name varchar(200) not null
- price_cents int unsigned not null
- stock int not null default 0
- primary key (product_id)
- unique key uk_product_sku (sku)

##### customer_order
- order_id bigint unsigned not null auto_increment
- customer_id bigint unsigned not null
- status enum('NEW', 'PAID', 'SHIPPED', 'CANCELLED') not null default 'NEW'
- created_at datetime not null default current_timestamp
- total_cents int unsigned not null default 0
- primary key (order_id)
- key idx_order_customer (customer_id, created_at)

##### order_item
- order_id bigint unsigned not null
- product_id int unsigned not null
- quantity smallint unsigned not null
- unit_price_cents int unsigned not null
- primary key (order_id, product_id)
- foreign key fk_item_order (order_id) references customer_order (order_id) on delete cascade
- foreign key fk_item_product (product_id) references product (product_id)

#### Reads
- product (sku, stock)
- customer_order (order_id)
- order_item (order_id)

#### Writes
- order_item (order_id, product_id, quantity, unit_price_cents)
- product (stock)
- customer_order (total_cents)

#### Relationships
- order_item.order_id -> customer_order.order_id
- order_item.product_id -> product.product_id

#### Routine logic
- accepts order ID, SKU, and quantity as inputs
- validates SKU exists and sufficient stock
- inserts or updates order item with quantity
- reduces product stock
- recalculates and updates order total

#### Dependencies
- product
- customer_order
- order_item
