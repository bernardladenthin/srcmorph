### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:24Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and order lines, with a procedure that adds an order item, reserves stock, and recalculates the order total.

#### Purpose
- Store product pricing and stock, customer order status and totals, and per-product order quantities and prices.
- `add_order_item` adds or increments an order line, decrements product stock, and updates the order total.

#### Type
- MySQL 8; tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - KEY `idx_order_customer` (`customer_id`, `created_at`)
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, PRIMARY KEY component
  - `product_id INT UNSIGNED` — NOT NULL, PRIMARY KEY component
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Composite PRIMARY KEY (`order_id`, `product_id`)
  - CONSTRAINT `fk_item_order`: FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`order_id`) ON DELETE CASCADE
  - CONSTRAINT `fk_item_product`: FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`; lookup filters on `sku` and `stock`, with `FOR UPDATE`.
- `order_item`: `quantity`, `unit_price_cents`, `order_id` for the order-total sum.
- Procedure parameters: `p_order_id`, `p_sku`, `p_qty`.

#### Writes
- `order_item`: INSERTs one row per procedure call; duplicate (`order_id`, `product_id`) updates `quantity` by adding `p_qty`.
- `product`: UPDATEs `stock` by subtracting `p_qty`.
- `customer_order`: UPDATEs `total_cents` to the sum of line quantity × unit price for `p_order_id`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, cascade delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Selects and locks a matching product with sufficient stock; signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if none is found.
- Inserts an order line or increments its existing quantity; subtracts the requested quantity from stock.
- Recalculates `customer_order.total_cents` from the order’s lines.
