### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:47Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema for a web shop that stores products, orders, and line items, and provides a stored procedure to add items, reserve stock, and update order totals.

#### Purpose
- Persist product catalog, customer orders, and order line items.
- Offer a single procedure to add an item to an order, reserve inventory, and recalculate the order total.

#### Responsibilities
- **Data definition** – tables `product`, `customer_order`, `order_item` with keys, indexes, and constraints.
- **Business logic** – stored procedure `add_order_item` that validates SKU and stock, updates inventory, inserts or updates order items, and refreshes the order total.

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item`** – encapsulates the transactional flow for adding an item to an order.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.
2. Procedure locks the matching `product` row (FOR UPDATE) and checks stock.
3. If valid, inserts or updates an `order_item` row.
4. Decrements `product.stock` by `p_qty`.
5. Recalculates `customer_order.total_cents` from all items of the order.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item` tables.
- No external packages or sequences; relies on MySQL 8 features (ENUM, FOR UPDATE, ON DUPLICATE KEY UPDATE).

#### Cross-cutting
- **Concurrency control** – uses row locking (`FOR UPDATE`) to prevent overselling.
- **Integrity** – foreign keys enforce referential consistency; cascading deletes keep order items in sync with orders.
- **Error handling** – raises a single error when SKU is unknown or stock is insufficient.
