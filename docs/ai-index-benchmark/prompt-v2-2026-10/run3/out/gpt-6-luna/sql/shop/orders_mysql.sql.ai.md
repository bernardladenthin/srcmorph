### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:30Z
- G: 
- A: 0.0.0
- X: file
---
> Models web-shop products, customer orders, and order lines; adds order items while reserving stock and recalculating order totals.

#### Purpose
- Stores product catalog, order headers, and order line items.
- Provides `add_order_item` to add or increment an order line, decrement stock, and update the order total.

#### Type
- Tables and procedure; MySQL 8.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, primary key (unnamed).
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL, DEFAULT `0`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, primary key (unnamed).
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`.
  - KEY `idx_order_customer` (`customer_id`, `created_at`).
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of primary key (unnamed).
  - `product_id INT UNSIGNED` — NOT NULL, part of primary key (unnamed).
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - CONSTRAINT `fk_item_order`: FK `order_id` references `customer_order(order_id)` ON DELETE CASCADE.
  - CONSTRAINT `fk_item_product`: FK `product_id` references `product(product_id)`.

#### Reads
- `add_order_item` reads `product.product_id`, `product.price_cents`, `product.sku`, and `product.stock`; locks the matching product row with `FOR UPDATE`.
- Reads `order_item.quantity` and `order_item.unit_price_cents` in the `customer_order.total_cents` sum subquery.

#### Writes
- `add_order_item` inserts or upserts `order_item(order_id, product_id, quantity, unit_price_cents)`; on duplicate key, increments `quantity`.
- Updates `product.stock` by subtracting `p_qty`.
- Updates `customer_order.total_cents` from the sum of its order-item quantities times unit prices.
- No static `INSERT ... VALUES` data rows.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`.
- Selects and locks the product matching `p_sku` if its `stock >= p_qty`.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no matching eligible product is found.
- Inserts the line or increments its existing quantity, decrements product stock, and recalculates the order total.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
