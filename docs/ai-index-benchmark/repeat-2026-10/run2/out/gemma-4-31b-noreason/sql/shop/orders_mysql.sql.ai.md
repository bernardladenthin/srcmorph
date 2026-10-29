### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:57:34Z
- G: 
- A: 0.0.0
- X: file
---
> Manages e-commerce product inventory, customer orders, and the atomic process of adding items to orders while updating stock and totals.

#### Purpose
* Track product catalog, pricing, and stock levels.
* Manage customer order headers and line-item details.
* Provide a transactional mechanism to reserve stock and calculate order totals.

#### Type
* Tables, Procedure (MySQL 8).

#### Schema
* **product**: `product_id INT UNSIGNED` (PK, AI), `sku VARCHAR(40)` (UNIQUE), `name VARCHAR(200)` NOT NULL, `price_cents INT UNSIGNED` NOT NULL, `stock INT` DEFAULT 0.
* **customer_order**: `order_id BIGINT UNSIGNED` (PK, AI), `customer_id BIGINT UNSIGNED` NOT NULL, `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` DEFAULT 'NEW', `created_at DATETIME` DEFAULT CURRENT_TIMESTAMP, `total_cents INT UNSIGNED` DEFAULT 0. Index: `idx_order_customer` (customer_id, created_at).
* **order_item**: `order_id BIGINT UNSIGNED` (PK, FK), `product_id INT UNSIGNED` (PK, FK), `quantity SMALLINT UNSIGNED` NOT NULL, `unit_price_cents INT UNSIGNED` NOT NULL.

#### Reads
* **add_order_item**: Reads `product` (product_id, price_cents, stock) via `sku`; reads `order_item` (quantity, unit_price_cents) to calculate sum.

#### Writes
* **add_order_item**: Writes to `order_item` (INSERT/UPDATE quantity); updates `product` (stock); updates `customer_order` (total_cents).

#### Relationships
* `order_item.order_id` -> `customer_order.order_id` (ON DELETE CASCADE)
* `order_item.product_id` -> `product.product_id`

#### Routine logic
* **add_order_item**:
    * Parameters: `p_order_id`, `p_sku`, `p_qty`.
    * Locks `product` row `FOR UPDATE` to verify `sku` existence and sufficient `stock`.
    * Throws error '45000' if product is missing or stock is insufficient.
    * Inserts or increments `quantity` in `order_item`.
    * Decrements `product.stock`.
    * Recalculates and updates `customer_order.total_cents` based on the sum of all items for that order.

#### Dependencies
* `product` table
* `customer_order` table
* `order_item` table
