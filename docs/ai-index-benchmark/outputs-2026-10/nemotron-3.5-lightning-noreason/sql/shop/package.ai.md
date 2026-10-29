### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:55:05Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A web shop model for products, orders, and order placement with stock reservation and total calculation.

#### Purpose
- Model shop inventory and order flow.
- Provide a procedure to add items to an order, enforce stock, and recalculate order totals.

#### Responsibilities
- Manage product catalog with SKU uniqueness and stock tracking.
- Handle order creation and item placement with stock enforcement.
- Calculate and maintain order totals dynamically.

#### Key units
- **product**: Table storing catalog items with SKU, price, and stock levels.
- **customer_order**: Table tracking orders per customer with status and total amount.
- **order_item**: Junction table linking orders to products with quantity and unit price.
- **add_order_item**: Procedure that adds items to orders, validates stock, and updates totals.

#### Data flow
- Client calls `add_order_item` with SKU, quantity, and order ID.
- Procedure selects product row for update, checks stock availability.
- Inserts or updates order_item with the ordered quantity.
- Deducts reserved quantity from product stock.
- Recalculates order total by summing item prices.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- Procedure: `add_order_item`
- External: MySQL 8 database with InnoDB support for row-level locking.

#### Cross-cutting
- Uses `SELECT ... FOR UPDATE` pattern for optimistic concurrency and stock race condition prevention.
- Employs `ON DUPLICATE KEY UPDATE` for idempotent item quantity increments.
- Total calculation uses correlated subquery to always reflect current cart state.
