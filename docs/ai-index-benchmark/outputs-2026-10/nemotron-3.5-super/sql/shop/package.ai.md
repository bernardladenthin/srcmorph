### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:58:26Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Models a web shop catalog and order lifecycle by reserving product stock and maintaining order totals when order lines are added.

#### Purpose
- Defines product, customer_order, and order_item tables for a MySQL web shop.
- Provides the add_order_item stored procedure to place an order line, reserve stock, and recalculate the order total.

#### Responsibilities
- Product catalog: SKU uniqueness, pricing in cents, and stock levels.
- Order lifecycle: customer orders with status enum (NEW, PAID, SHIPPED, CANCELLED) and running total in cents.
- Order items: line items linking orders to products with quantity and unit price.
- Stock reservation and total recalculation via add_order_item.

#### Key units
- product table: holds SKU, name, price_cents, and stock for the catalog.
- customer_order table: tracks order status and total_cents per customer, indexed by customer and creation time.
- order_item table: composite primary key on order_id and product_id with quantity and unit_price_cents.
- add_order_item procedure: locks product stock, inserts or updates order_item, decrements stock, and refreshes order total.

#### Data flow
- add_order_item receives p_order_id, p_sku, and p_qty.
- Reads and locks the matching product row with FOR UPDATE where stock is sufficient.
- Aborts with SQLSTATE 45000 if no product is found.
- Inserts into order_item or increments quantity on duplicate key.
- Decrements product.stock by p_qty.
- Recomputes customer_order.total_cents from the sum of order_item.quantity times unit_price_cents for that order.

#### Dependencies
- Internal tables: product, customer_order, order_item.
- Internal constraints: uk_product_sku, idx_order_customer, fk_item_order (ON DELETE CASCADE), fk_item_product.
- External platform: MySQL 8 / InnoDB.

#### Cross-cutting
- Row-level locking via SELECT ... FOR UPDATE on product during stock reservation.
- Error handling using SQLSTATE 45000 for missing or insufficient stock.
- Foreign key cascade delete from customer_order to order_item.
- Unique key enforcement on product.sku.
