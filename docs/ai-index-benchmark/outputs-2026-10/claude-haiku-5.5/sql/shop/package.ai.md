### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:32Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Web shop order management schema in MySQL 8: products with stock, customer orders with totals, and an `add_order_item` procedure that reserves stock and recalculates order totals.

#### Purpose
- Stores products with stock levels, customer orders with status and total, and order line items.
- Provides `add_order_item` to add a SKU to an order, decrement `product.stock`, and refresh `customer_order.total_cents`.

#### Responsibilities
- Catalog and stock: `product` table (`sku`, `price_cents`, `stock`) with unique SKU key `uk_product_sku`.
- Order header: `customer_order` table (`status` ENUM of NEW, PAID, SHIPPED, CANCELLED; `total_cents`), indexed by `idx_order_customer`.
- Order lines: `order_item` table keyed by composite PK (`order_id`, `product_id`), storing `quantity` and `unit_price_cents`.
- Order-entry logic: `add_order_item` stored procedure performs stock reservation and total recalculation in one routine.

#### Key units
- `product`: catalog item with SKU, price in cents, and stock count.
- `customer_order`: customer order header with status and total in cents.
- `order_item`: line item linking an order to a product with quantity and unit price.
- `add_order_item(p_order_id, p_sku, p_qty)`: stored procedure that adds or increments an item, reserves stock, and recomputes the order total.

#### Data flow
- Caller passes order ID, SKU, and quantity to `add_order_item`.
- Procedure locks the matching `product` row (FOR UPDATE) where `stock >= p_qty`.
- Missing or insufficient-stock product raises SQLSTATE '45000'.
- Inserts into `order_item`; on duplicate key adds `p_qty` to `quantity`.
- Decrements `product.stock` by `p_qty`.
- Recomputes `customer_order.total_cents` as SUM(`quantity` × `unit_price_cents`) for the order.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.
- Foreign keys: `fk_item_order` (`order_item.order_id` → `customer_order.order_id`, ON DELETE CASCADE), `fk_item_product` (`order_item.product_id` → `product.product_id`).
- Engine and dialect: MySQL 8, InnoDB, utf8mb4, `DELIMITER //`, `SIGNAL SQLSTATE`.

#### Cross-cutting
- Error handling: business-rule failures signaled via `SIGNAL SQLSTATE '45000'` inside the procedure.
- Concurrency: row-level locking with `SELECT ... FOR UPDATE` on `product` during stock reservation.
- Integrity: unique SKU constraint, composite primary key on `order_item`, cascading delete from orders to items.
