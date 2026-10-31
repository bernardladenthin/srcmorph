### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:43Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop order placement with product catalog, customer orders, and stock reservation.
#### Purpose
- Models web shop `product`, `customer_order`, `order_item` catalog and ordering
- Places order lines via `add_order_item` with stock check and order total maintenance
#### Responsibilities
- Product catalog: `product` with `sku`, `name`, `price_cents`, `stock`
- Customer orders: `customer_order` with `status`, `created_at`, `total_cents`
- Order line placement: `add_order_item` upsert, `stock` reservation, `total_cents` recalculation
#### Key units
- `product`: catalog item with `product_id`, `uk_product_sku`, `price_cents`, `stock`
- `customer_order`: customer order with `order_id`, `customer_id`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `total_cents`, `idx_order_customer`
- `order_item`: order line with composite `PRIMARY KEY (order_id, product_id)`, `quantity`, `unit_price_cents`, `fk_item_order`, `fk_item_product`
- `add_order_item(p_order_id, p_sku, p_qty)`: locks product, upserts line, decrements stock, updates total
#### Data flow
- Input `p_order_id, p_sku, p_qty` -> `SELECT ... WHERE sku = p_sku AND stock >= p_qty FOR UPDATE` into `v_product_id, v_price`
- Upsert `order_item` via `INSERT ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`
- Decrement `product.stock`, recalc `SUM(quantity * unit_price_cents)` -> `UPDATE customer_order.total_cents`
#### Dependencies
- Internal: `product` <-> `order_item` via `fk_item_product`, `customer_order` <-> `order_item` via `fk_item_order ON DELETE CASCADE`
- Platform: MySQL 8, InnoDB `utf8mb4`, `DELIMITER //` routine
#### Cross-cutting
- Row-level locking with `SELECT ... FOR UPDATE` for stock reservation
- Error handling via `SIGNAL SQLSTATE '45000'` on `unknown SKU or not enough stock`
- Idempotent line add via `ON DUPLICATE KEY UPDATE` on composite key
