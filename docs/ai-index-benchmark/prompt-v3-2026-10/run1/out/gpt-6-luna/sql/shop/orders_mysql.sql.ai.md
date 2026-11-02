### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:13Z
- G: 
- A: 0.0.0
- X: file
---
> Models web-shop products, customer orders, and order lines, with a routine that adds an item, reserves product stock, and updates the order total.

#### Purpose
- Stores product catalog and inventory, customer orders, and order-line quantities with captured unit prices.
- `add_order_item` adds or increments an order line while reserving stock and recalculating the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- `customer_order.status` uses inline `ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')`; no standalone `CREATE TYPE` or extensions.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — PK, NOT NULL, AUTO_INCREMENT
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE via `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — PK, NOT NULL, AUTO_INCREMENT
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
- `order_item`
  - `order_id BIGINT UNSIGNED` — PK component, NOT NULL, FK `fk_item_order` → `customer_order.order_id`, ON DELETE CASCADE
  - `product_id INT UNSIGNED` — PK component, NOT NULL, FK `fk_item_product` → `product.product_id`
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Composite primary key: `(order_id, product_id)`
- Indexes:
  - `PRIMARY` on `product` (`product_id`)
  - `uk_product_sku` on `product` (`sku`), unique
  - `PRIMARY` on `customer_order` (`order_id`)
  - `idx_order_customer` on `customer_order` (`customer_id`, `created_at`), default method
  - `PRIMARY` on `order_item` (`order_id`, `product_id`)

#### Reads
- `add_order_item` reads `product.product_id`, `product.price_cents`, `product.sku`, `product.stock`; locks the matching product row with `FOR UPDATE`.
- Reads `order_item.quantity`, `order_item.unit_price_cents` to calculate the order total.
- Reads `order_item.order_id` to filter the total calculation.

#### Writes
- `add_order_item` inserts into `order_item` (`order_id`, `product_id`, `quantity`, `unit_price_cents`); on duplicate key, increments `quantity`.
- Updates `product.stock` for the selected `product_id`.
- Updates `customer_order.total_cents` for the supplied `order_id`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (`fk_item_product`)

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Selects and locks the product matching `p_sku` if `stock >= p_qty`; captures product ID and price.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no matching available product is found.
- Inserts an order line or adds `p_qty` to the existing line quantity.
- Decrements product stock by `p_qty`.
- Recalculates `customer_order.total_cents` as the sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
