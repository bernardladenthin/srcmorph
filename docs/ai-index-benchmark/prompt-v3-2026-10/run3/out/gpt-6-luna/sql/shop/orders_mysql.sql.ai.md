### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:08Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and line items, with a procedure that adds order items, reserves inventory, and recalculates order totals.

#### Purpose
- Defines product, customer order, and order item storage.
- Adds `add_order_item` to add a SKU to an order, decrement stock, and update the order total.

#### Type
- MySQL 8; tables and procedure.
- `customer_order.status`: enum `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `sku VARCHAR(40)` — `NOT NULL`, unique.
  - `name VARCHAR(200)` — `NOT NULL`.
  - `price_cents INT UNSIGNED` — `NOT NULL`.
  - `stock INT` — `NOT NULL`, default `0`.
  - Index `PRIMARY` on `product(product_id)`; InnoDB default method.
  - Index `uk_product_sku` on `product(sku)`; unique, InnoDB default method.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, `AUTO_INCREMENT`, primary key.
  - `customer_id BIGINT UNSIGNED` — `NOT NULL`.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — `NOT NULL`, default `'NEW'`.
  - `created_at DATETIME` — `NOT NULL`, default `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — `NOT NULL`, default `0`.
  - Index `PRIMARY` on `customer_order(order_id)`; InnoDB default method.
  - Index `idx_order_customer` on `customer_order(customer_id, created_at)`; InnoDB default method.
- `order_item`
  - `order_id BIGINT UNSIGNED` — `NOT NULL`, primary key component; FK `fk_item_order` references `customer_order(order_id)`, `ON DELETE CASCADE`.
  - `product_id INT UNSIGNED` — `NOT NULL`, primary key component; FK `fk_item_product` references `product(product_id)`.
  - `quantity SMALLINT UNSIGNED` — `NOT NULL`.
  - `unit_price_cents INT UNSIGNED` — `NOT NULL`.
  - Index `PRIMARY` on `order_item(order_id, product_id)`; InnoDB default method.

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — SKU lookup and stock eligibility check in `add_order_item`.
- `order_item`: `quantity`, `unit_price_cents`, `order_id` — subquery sums line totals for the order.

#### Writes
- `order_item`: inserts `(order_id, product_id, quantity, unit_price_cents)`; on duplicate key, updates `quantity`.
- `product`: updates `stock` by subtracting `p_qty` for the selected product.
- `customer_order`: updates `total_cents` from the sum of its order item line totals.

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, cascade delete).
- `order_item.product_id -> product.product_id` (`fk_item_product`).
- `product.sku` is used to locate a product for an order item.

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`.
- Locks the matching `product` row and retrieves its ID and price when SKU matches and stock is sufficient.
- Signals SQLSTATE `45000` with `unknown SKU or not enough stock` if no product is selected.
- Inserts or increments the matching `order_item` quantity, decrements product stock, and recalculates `customer_order.total_cents`.
