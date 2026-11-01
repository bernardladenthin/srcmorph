### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:15:44Z
- G: 
- A: 0.0.0
- X: file
---
> Models a web shop’s product inventory, customer orders, and order lines, with a procedure that adds an item, reserves stock, and recalculates the order total.

#### Purpose
- Stores products, customer orders, and order-item quantities and prices.
- `add_order_item` adds or increments an order line while updating inventory and order totals.

#### Type
- Tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- MySQL 8, InnoDB, `utf8mb4`.
- `customer_order.status` enum values: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE via `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
  - Index: `PRIMARY` on `product (product_id)`
  - Index: `uk_product_sku` on `product (sku)`, unique
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - Index: `PRIMARY` on `customer_order (order_id)`
  - Index: `idx_order_customer` on `customer_order (customer_id, created_at)`
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of composite PRIMARY KEY
  - `product_id INT UNSIGNED` — NOT NULL
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Index: `PRIMARY` on `order_item (order_id, product_id)`
  - Constraint: `fk_item_order`, FOREIGN KEY (`order_id`) REFERENCES `customer_order (order_id)` ON DELETE CASCADE
  - Constraint: `fk_item_product`, FOREIGN KEY (`product_id`) REFERENCES `product (product_id)`

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` (`SELECT ... FOR UPDATE`).
- `order_item`: `quantity`, `unit_price_cents` for the specified `order_id` in the total calculation.

#### Writes
- `order_item`: inserts one line per procedure call; on duplicate key, increments `quantity`.
- `product`: updates `stock` by subtracting `p_qty` for the selected product.
- `customer_order`: updates `total_cents` to the sum of its order-item quantity × unit price.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, cascading delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Locks a matching product row where `sku = p_sku` and `stock >= p_qty`; retrieves its ID and price.
- Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is selected.
- Inserts an order line or increments its existing quantity, subtracts requested quantity from stock, and recalculates the order total from its lines.
