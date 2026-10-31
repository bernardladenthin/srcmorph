### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:25:59Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop order placement with product catalog, customer orders, and stock reservation.
#### Purpose
- Models web shop inventory and customer ordering with `product`, `customer_order`, `order_item`
- Places order lines with stock check and total recalculation via `add_order_item`
#### Responsibilities
- Product inventory: `product` with `sku`, `price_cents`, `stock`
- Order headers: `customer_order` with `status`, `total_cents`, `customer_id`
- Order lines and stock reservation: `order_item` insert/upsert, `product.stock` decrement, `total_cents` recompute
#### Key units
- `product` table: catalog item with `product_id`, `sku UNIQUE`, `name`, `price_cents`, `stock`
- `customer_order` table: order header with `order_id`, `customer_id`, `status ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `total_cents`
- `order_item` table: order line with `order_id`, `product_id`, `quantity`, `unit_price_cents`
- `add_order_item(p_order_id, p_sku, p_qty)` procedure: SKU lookup with row lock, stock validation, line upsert, stock decrement, total update
#### Data flow
- Input `p_order_id, p_sku, p_qty` -> `SELECT product ... WHERE sku = p_sku AND stock >= p_qty FOR UPDATE` -> `INSERT INTO order_item ... ON DUPLICATE KEY UPDATE quantity` -> `UPDATE product.stock = stock - p_qty` -> `SUM(quantity * unit_price_cents)` -> `UPDATE customer_order.total_cents`
- Errors signaled via `SIGNAL SQLSTATE '45000'` on unknown SKU or insufficient `stock`
#### Dependencies
- Internal: `order_item.order_id -> customer_order.order_id ON DELETE CASCADE`, `order_item.product_id -> product.product_id`
- External: MySQL 8, InnoDB `utf8mb4`, `AUTO_INCREMENT`, `ENUM`, `DELIMITER //`, `SIGNAL SQLSTATE`
#### Cross-cutting
- Row-level locking with `FOR UPDATE` for stock reservation concurrency
- Denormalized `total_cents` maintained from `order_item` sum
- Indexes: `PRIMARY KEY(product_id)`, `PRIMARY KEY(order_id)`, `PRIMARY KEY(order_id, product_id)`, `UNIQUE KEY uk_product_sku(sku)`, `KEY idx_order_customer(customer_id, created_at)`
