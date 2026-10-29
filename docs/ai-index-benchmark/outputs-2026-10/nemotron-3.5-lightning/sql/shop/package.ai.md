### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:13:00Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL 8 web shop data model with product catalog, order management, and stock control via a transactional stored procedure.

#### Purpose
- Models a web shop data model with products, customers, and orders.
- Provides a stored procedure to add order items and maintain stock and order totals.

#### Responsibilities
- Maintains product stock levels when order items are inserted or updated.
- Computes and updates order total cents from order item aggregates.
- Enforces stock availability and raises errors for unknown SKUs or insufficient quantity.
- Handles duplicate order item quantity accumulation via ON DUPLICATE KEY UPDATE.

#### Key units
- `product` table: stores product SKU, price in cents, and stock quantity.
- `customer_order` table: tracks order status, total cents, and creation timestamp.
- `order_item` table: links orders and products with quantity and unit price, using composite primary key.
- `add_order_item` procedure: transactional routine that validates stock, inserts items, and updates inventory and order totals.

#### Data flow
- Input SKU and quantity trigger a stock check with row-level locking.
- Validated stock leads to order item insertion (or quantity increment) and product stock decrement.
- Order total is recomputed as the sum of quantity times unit price across all items for the order.

#### Dependencies
- `product` table, referenced for SKU lookup and stock management.
- `customer_order` table, referenced for order total updates.
- `order_item` table, the junction table for order-product line items.
- Procedure depends on all three tables for its logic.

#### Cross-cutting
- Uses `FOR UPDATE` row locking on product reads to prevent concurrent stock oversell.
- Signals SQLSTATE '45000' with message when SKU is unknown or stock is insufficient.
- Employs ON DELETE CASCADE for order item removal on order cancellation.
- Applies ON DUPLICATE KEY UPDATE to accumulate quantities for repeated line items.
