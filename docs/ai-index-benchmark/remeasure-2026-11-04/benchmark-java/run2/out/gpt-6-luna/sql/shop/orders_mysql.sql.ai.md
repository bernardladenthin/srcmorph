### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:12Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models web-shop products, customer orders, and order lines, with a procedure that adds an item, reserves stock, and refreshes the order total.

#### Purpose
- Defines product inventory, customer orders, and order-item records.
- Adds `add_order_item` to add or increase an order line while decrementing stock and recalculating the order total.

#### Type
- MySQL 8 tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `sku VARCHAR(40)` — `NOT NULL`, unique key `uk_product_sku`.
  - `name VARCHAR(200)` — `NOT NULL`.
  - `price_cents INT UNSIGNED` — `NOT NULL`.
  - `stock INT` — `NOT NULL`, `DEFAULT 0`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, `DEFAULT 'NEW'`.
  - `created_at DATETIME` — `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — `NOT NULL`, `DEFAULT 0`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, part of primary key; FK `fk_item_order` references `customer_order.order_id`, `ON DELETE CASCADE`.
  - `product_id INT UNSIGNED` — `NOT NULL`, part of primary key; FK `fk_item_product` references `product.product_id`.
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`.
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`.
- Indexes:
  - Primary key on `product.product_id`.
  - `uk_product_sku` — unique index on `product(sku)`.
  - Primary key on `customer_order.order_id`.
  - `idx_order_customer` — index on `customer_order(customer_id, created_at)`.
  - Primary key on `order_item(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`.
- `order_item`: `order_id`, `quantity`, `unit_price_cents`.

#### Writes
- `order_item`: insert `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, increment `quantity`.
- `product`: update `stock` by subtracting `p_qty`.
- `customer_order`: update `total_cents` from the sum of `quantity * unit_price_cents` for the specified order.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, cascade on delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED)`; all parameters are `IN`.
- Locks the matching product row where `sku = p_sku` and `stock >= p_qty`; retrieves its ID and price.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is found.
- Inserts an order line or increments its existing quantity; decrements product stock; recalculates the order total.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
