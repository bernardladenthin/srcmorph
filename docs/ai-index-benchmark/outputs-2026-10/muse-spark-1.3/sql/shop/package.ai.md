### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:18:56Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop order placement with product catalog, customer orders, line items, stock reservation and order total maintenance.

#### Purpose
- Model `product` catalog, `customer_order` headers, `order_item` lines for web shop ordering.
- Provide `add_order_item` order-placement operation reserving stock and recalculating order total.

#### Responsibilities
- Product catalog: `product` with `sku`, `name`, `price_cents`, `stock`, `uk_product_sku`.
- Order headers: `customer_order` with `customer_id`, `status`, `created_at`, `total_cents`, `idx_order_customer`.
- Order lines: `order_item` keyed by `(order_id, product_id)` with `quantity`, `unit_price_cents`.
- Stock-checked line upsert + total maintenance via `add_order_item(p_order_id, p_sku, p_qty)`.

#### Key units
- `product`: catalog table, PK `product_id`, UNIQUE `sku`, holds `price_cents` and `stock`.
- `customer_order`: order header table, PK `order_id`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `total_cents`.
- `order_item`: line-item table, PK `(order_id, product_id)`, FKs `fk_item_order`, `fk_item_product`.
- `add_order_item`: procedure locking `product` row `FOR UPDATE`, `SIGNAL SQLSTATE '45000'` on unknown SKU/insufficient stock, upserts `order_item`, decrements `stock`, recomputes `total_cents`.

#### Data flow
- Input `p_order_id, p_sku, p_qty` -> `SELECT ... INTO v_product_id, v_price FROM product WHERE sku = p_sku` + `FOR UPDATE` stock check.
- Validated `v_product_id` -> `INSERT INTO order_item ... ON DUPLICATE KEY UPDATE quantity` with `unit_price_cents = v_price`.
- Upsert -> `UPDATE product SET stock = stock - p_qty` -> `UPDATE customer_order SET total_cents = SUM(quantity * unit_price_cents)`.

#### Dependencies
- Internal: `order_item.order_id -> customer_order.order_id ON DELETE CASCADE`, `order_item.product_id -> product.product_id`.
- Procedure dependencies on `product.sku, stock, price_cents`, `order_item.quantity, unit_price_cents`, `customer_order.total_cents`.
- Platform: MySQL 8 dialect, `ENGINE=InnoDB`, `CHARSET=utf8mb4`, `DELIMITER //`, `AUTO_INCREMENT`, `ENUM`.

#### Cross-cutting
- Row-level locking via `SELECT ... FOR UPDATE` for stock reservation.
- Idempotent line add via `ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`.
- Domain error via `SIGNAL SQLSTATE '45000'` with `unknown SKU or not enough stock`.
