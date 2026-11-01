### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:40Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and order lines; provides an operation to add an order line, reserve product stock, and update the order total.

#### Purpose
- Defines product inventory, customer orders, and order items.
- `add_order_item` adds a product to an order while checking and decrementing stock and recalculating the order total.

#### Type
- MySQL 8; tables and procedure.
- `product`, `customer_order`, `order_item` tables; `add_order_item` procedure.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `sku VARCHAR(40)` — `NOT NULL`, unique key `uk_product_sku`.
  - `name VARCHAR(200)` — `NOT NULL`.
  - `price_cents INT UNSIGNED` — `NOT NULL`.
  - `stock INT` — `NOT NULL`, `DEFAULT 0`.
  - Index `PRIMARY` — `product(product_id)`.
  - Index `uk_product_sku` — unique, `product(sku)`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, `DEFAULT 'NEW'`.
  - `created_at DATETIME` — `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — `NOT NULL`, `DEFAULT 0`.
  - Index `PRIMARY` — `customer_order(order_id)`.
  - Index `idx_order_customer` — `customer_order(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, part of primary key; FK `fk_item_order` references `customer_order(order_id)`, `ON DELETE CASCADE`.
  - `product_id INT UNSIGNED` — `NOT NULL`, part of primary key; FK `fk_item_product` references `product(product_id)`.
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`.
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`.
  - Index `PRIMARY` — `order_item(order_id, product_id)`.

#### Reads
- `product`: reads `product_id`, `price_cents`, `sku`, and `stock` to find and lock an eligible product.
- `order_item`: reads `quantity` and `unit_price_cents` for the specified `order_id` to calculate the order total.

#### Writes
- `order_item`: inserts `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, increments `quantity` by `p_qty`.
- `product`: updates `stock` by subtracting `p_qty` for the selected `product_id`.
- `customer_order`: updates `total_cents` for the specified `order_id`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`; cascade delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`.
- Selects and locks a product matching `p_sku` with `stock >= p_qty`; retrieves its ID and price.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is found.
- Inserts the order line or increments the existing line quantity; decrements product stock; recalculates `customer_order.total_cents` as the sum of line quantity times unit price.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
