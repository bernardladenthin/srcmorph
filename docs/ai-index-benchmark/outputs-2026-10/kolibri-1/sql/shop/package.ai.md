### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:42:36Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Models a web shop's product catalog, customer orders, and order placement with stock reservation and total calculation.

#### Purpose
- Models product catalog, customer orders, and order line items
- Provides order placement procedure with stock reservation and total update

#### Responsibilities
- Product catalog management with SKU uniqueness and stock tracking
- Order creation and line item management with quantity and unit price
- Stock reservation and decrement during order placement
- Order total recalculation from line item sum

#### Key units
- product table: product_id, sku, name, price_cents, stock
- customer_order table: order_id, customer_id, status, total_cents
- order_item table: order_id, product_id, quantity, unit_price_cents
- add_order_item procedure: p_order_id, p_sku, p_qty parameters

#### Data flow
- Order placement reads product stock and price, reserves stock FOR UPDATE, inserts order_item, decrements product stock, recalculates customer_order total_cents

#### Dependencies
- product, customer_order, order_item, add_order_item
- MySQL 8 dialect, InnoDB engine, utf8mb4 charset

#### Cross-cutting
- ENUM status values: NEW, PAID, SHIPPED, CANCELLED
- Composite primary key on order_item (order_id, product_id)
- Foreign keys with ON DELETE CASCADE on order_item
- Index on customer_order (customer_id, created_at)
