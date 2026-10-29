### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:59Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> This package models a web shop's product catalog and order lifecycle, providing the database schema and a stored procedure to manage item addition, stock validation, and order total calculation.

#### Purpose
- Defines the relational schema for a web shop, including product inventory, customer orders, and order line items.
- Encapsulates order item addition logic within a MySQL stored procedure that ensures data integrity through stock validation and total recalculation.

#### Responsibilities
- **Schema management**: Creates and maintains tables for `product`, `customer_order`, and `order_item` with appropriate keys and constraints.
- **Order processing**: Implements the `add_order_item` procedure to handle item insertion, stock deduction, and order total updates atomically.
- **Data integrity**: Enforces referential integrity via foreign keys, unique constraints on SKU, and stock level checks before modification.

#### Key units
- **product**: Table storing catalog items with `product_id`, `sku`, `name`, `price_cents`, and `stock`; referenced by order items and the ordering procedure.
- **customer_order**: Table tracking individual orders with `order_id`, `customer_id`, `status`, and `total_cents`; serves as the parent for order items.
- **order_item**: Junction table linking orders and products, storing `quantity` and `unit_price_cents`; its primary key is composite on (`order_id`, `product_id`).
- **add_order_item**: Stored procedure accepting `p_order_id`, `p_sku`, and `p_qty`; responsible for stock validation, item insertion/update, and order total recalculation.

#### Data flow
- Procedure `add_order_item` receives an order ID, SKU, and quantity; it locks the target `product` row, validates sufficient stock, inserts or updates the corresponding `order_item` row, decrements `product.stock`, and recalculates `customer_order.total_cents` by summing `quantity * unit_price_cents` across all items for that order.

#### Dependencies
- **product**: Table accessed via `SELECT ... FOR UPDATE` to read `sku`, `stock`, and `price_cents`; written to decrement `stock`.
- **customer_order**: Table accessed via `UPDATE` to recalculate and set `total_cents`; referenced by the foreign key on `order_item`.
- **order_item**: Table written to via `INSERT ... ON DUPLICATE KEY UPDATE` and read via `SUM` aggregate to compute order totals.

#### Cross-cutting
- Uses MySQL 8 `ENUM` for order `status` with a default of `'NEW'`.
- Employs `ON DELETE CASCADE` on the `order_item` foreign key to `customer_order` to maintain consistency on order deletion.
- Relies on `UNIQUE KEY uk_product_sku` to guarantee SKU uniqueness and enable the duplicate-key logic within the procedure.
