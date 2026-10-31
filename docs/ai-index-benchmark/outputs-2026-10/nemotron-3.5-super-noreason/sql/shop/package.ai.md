### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:05:06Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Models a MySQL web shop catalog, customer orders, and order line items with stock reservation and order total recalculation when items are added.

#### Purpose
- Defines product catalog, customer orders, and order line items for a MySQL web shop.
- Provides a stored procedure to add an item to an order, reserve stock, and update the order total.

#### Responsibilities
- Product catalog management with SKU uniqueness and stock tracking.
- Customer order lifecycle with status enum and monetary totals in cents.
- Order item line entries with composite primary key on order and product.
- Atomic item addition procedure that locks product stock, validates availability, inserts or updates order items, decrements stock, and recalculates order totals.

#### Key units
- product table: product catalog with sku, name, price_cents, and stock columns.
- customer_order table: customer orders with status, created_at, and total_cents.
- order_item table: order line items with composite PK (order_id, product_id), quantity, and unit_price_cents.
- add_order_item stored procedure: adds an item to an order by SKU, reserves stock, and recomputes the order total.

#### Data flow
- add_order_item receives order_id, sku, and quantity; locks and reads product by SKU; validates stock sufficiency; inserts or updates order_item with quantity and unit price; decrements product.stock; recomputes customer_order.total_cents from order_item line sums.

#### Dependencies
- MySQL 8 InnoDB with utf8mb4.
- Internal table dependencies: product, customer_order, order_item.
- Foreign keys: order_item.order_id references customer_order.order_id ON DELETE CASCADE; order_item.product_id references product.product_id.

#### Cross-cutting
- Stock reservation uses SELECT ... FOR UPDATE row locking to prevent overselling.
- Error handling via SQLSTATE '45000' signals for missing product or insufficient stock.
- Order item upsert uses INSERT ... ON DUPLICATE KEY UPDATE on the composite primary key.
- Monetary values stored as integer cents in product.price_cents, order_item.unit_price_cents, and customer_order.total_cents.
