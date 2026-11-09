### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:33Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema for a web shop that defines products, orders, and order items, and provides a stored procedure to add items to an order while reserving stock and updating totals.

#### Purpose
- Persist shop data: products, customer orders, and order items.
- Offer a single procedure to add an item to an order, adjust inventory, and recalculate the order total.

#### Responsibilities
- **Data definition** – tables for `product`, `customer_order`, `order_item`.
- **Business logic** – stored procedure `add_order_item` that:
  - Locks the product row.
  - Validates stock.
  - Inserts/updates the order item.
  - Decrements product stock.
  - Recomputes the order total.

#### Key units
- **Tables**
  - `product`: SKU, name, price, stock.
  - `customer_order`: status, total, timestamps.
  - `order_item`: line items linking orders to products.
- **Procedure**
  - `add_order_item(p_order_id, p_sku, p_qty)`: transactional item addition.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.
2. Procedure locks the matching `product` row (`FOR UPDATE`).
3. If stock insufficient → error.
4. Insert or update `order_item` with quantity and unit price.
5. Decrement `product.stock` by `p_qty`.
6. Recalculate `customer_order.total_cents` from all items of that order.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item` tables.
- No external functions or sequences; relies on MySQL 8 features (ENUM, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`).

#### Cross-cutting
- **Concurrency**: uses row‑level locking (`FOR UPDATE`) to prevent race conditions on stock.
- **Integrity**: foreign keys enforce referential integrity between orders, items, and products.
- **Error handling**: raises an error if the product SKU is not found or stock is insufficient.
