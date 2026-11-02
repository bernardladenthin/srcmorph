### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:12Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and order lines, with a procedure that adds an order item, reserves product stock, and recalculates the order total.

#### Purpose
- Stores product catalog and inventory, customer orders, and order-item quantities with unit-price snapshots.
- `add_order_item` adds or increments an order line, decrements stock, and updates the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status`: `ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, PK
  - `sku VARCHAR(40)` — `NOT NULL`, unique key `uk_product_sku`
  - `name VARCHAR(200)` — `NOT NULL`
  - `price_cents INT UNSIGNED` — `NOT NULL`
  - `stock INT` — `NOT NULL`, `DEFAULT 0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, PK
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, `DEFAULT 'NEW'`
  - `created_at DATETIME` — `NOT NULL`, `DEFAULT CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — `NOT NULL`, `DEFAULT 0`
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, composite PK
  - `product_id INT UNSIGNED` — `NOT NULL`, composite PK
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`
  - `fk_item_order`: FK `order_id` → `customer_order.order_id`, `ON DELETE CASCADE`
  - `fk_item_product`: FK `product_id` → `product.product_id`
- Indexes:
  - `product`: `PRIMARY` on `(product_id)`; `uk_product_sku` unique on `(sku)`
  - `customer_order`: `PRIMARY` on `(order_id)`; `idx_order_customer` on `(customer_id, created_at)`
  - `order_item`: `PRIMARY` on `(order_id, product_id)`; FK-supporting indexes are not explicitly named in the source

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — SKU lookup, availability check, and row lock.
- `order_item`: `quantity`, `unit_price_cents`, `order_id` — total calculation.

#### Writes
- `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents` — one row per procedure call; duplicate key updates `quantity` instead.
- `product`: `stock` — decremented by `p_qty`.
- `customer_order`: `total_cents` — set to the sum of order-line quantity × unit price.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, cascade delete)
- `order_item.product_id -> product.product_id` (`fk_item_product`)

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Locks a matching product row where `sku = p_sku` and `stock >= p_qty`; retrieves its ID and price.
- Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is found.
- Inserts the order line or increments its existing quantity; decrements product stock and recalculates the order total.
- No explicit transaction handling is defined.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
