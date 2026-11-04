### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:38Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Models shop products, customer orders, and order lines, with a procedure that adds items, reserves inventory, and recalculates order totals.

#### Purpose
- Stores product catalog and stock, customer orders, and per-product order lines.
- `add_order_item` adds or increments an order line while updating stock and the order total.

#### Type
- MySQL 8 tables: `product`, `customer_order`, `order_item`; procedure: `add_order_item`.
- Enum `customer_order.status`: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL, DEFAULT `0`.
  - Index `PRIMARY` on `product(product_id)`.
  - Index `uk_product_sku` on `product(sku)`, unique.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`.
  - Index `PRIMARY` on `customer_order(order_id)`.
  - Index `idx_order_customer` on `customer_order(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY; FK `fk_item_order` references `customer_order(order_id)`, ON DELETE CASCADE.
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY; FK `fk_item_product` references `product(product_id)`.
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - Index `PRIMARY` on `order_item(order_id, product_id)`.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` (selection and stock eligibility check in `add_order_item`).
- `order_item`: `quantity`, `unit_price_cents`, `order_id` (total calculation).

#### Writes
- `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents` — INSERT; duplicate key increments `quantity`.
- `product`: `stock` — UPDATE, subtracts requested quantity.
- `customer_order`: `total_cents` — UPDATE, recalculates from its order lines.

#### Relationships
- `order_item.order_id -> customer_order.order_id` — FK `fk_item_order`; cascade delete.
- `order_item.product_id -> product.product_id` — FK `fk_item_product`.
- `add_order_item` matches `product.sku` to `p_sku` and uses the resulting `product.product_id` for the order line and stock update.
- `add_order_item` uses `p_order_id` with `order_item.order_id` and `customer_order.order_id`.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Locks the matching product row with `FOR UPDATE`, requiring `stock >= p_qty`; retrieves product ID and price.
  - Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no eligible product is found.
  - Inserts an order line or increments its existing quantity.
  - Decrements product stock and updates the order total to the sum of line quantities times unit prices.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
