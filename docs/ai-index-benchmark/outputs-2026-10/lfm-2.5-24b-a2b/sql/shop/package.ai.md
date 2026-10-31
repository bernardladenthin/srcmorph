### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:55:22Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides e-commerce data modeling and MySQL stored procedures for managing a product catalog, customer orders, and order placement with stock reservation.

#### Purpose
- Define core e-commerce schema for products, orders, and order items.
- Provide a stored procedure to add order lines while reserving stock and updating order totals.

#### Responsibilities
- Schema definition: product, customer_order, and order_item tables with constraints and keys.
- Procedure logic: add_order_item reads product stock/price, inserts/updates order items, decrements product stock, and recalculates customer order totals.
- Stock reservation: reserves stock by decrementing stock on insert and checking availability before adding items.
- Order total maintenance: updates customer_order.total_cents via subquery over order items.

#### Key units
- product table: product_id, sku, name, price_cents, stock
- customer_order table: order_id, customer_id, status, created_at, total_cents
- order_item table: order_id, product_id, quantity, unit_price_cents
- add_order_item stored procedure: parameters p_order_id, p_sku, p_qty
- Stock update: product stock = stock - p_qty
- Order total update: customer_order total_cents = SUM(quantity * unit_price_cents)

#### Data flow
- Input: order placement request containing order_id, sku, and quantity.
- Read: product (sku, stock, price_cents), order_item (order_id, product_id, quantity, unit_price_cents), customer_order (order_id, total_cents).
- Write: INSERT/UPDATE order_item; UPDATE product stock; UPDATE customer_order total_cents.
- Output: updated stock, updated order quantities, updated order total.

#### Dependencies
- product table (sku, price_cents, stock)
- customer_order table (order_id, total_cents)
- order_item table (order_id, product_id, quantity, unit_price_cents)
- MySQL 8 syntax: ENUM, AUTO_INCREMENT, DELIMITER

#### Cross-cutting
- Row-level locking with SELECT ... FOR UPDATE during product lookup.
- Error handling: signals SQLSTATE '45000' with message 'unknown SKU or not enough stock' if no matching product.
- Foreign keys: order_item references customer_order and product with ON DELETE CASCADE.
- Stored procedure parameters: p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED.
