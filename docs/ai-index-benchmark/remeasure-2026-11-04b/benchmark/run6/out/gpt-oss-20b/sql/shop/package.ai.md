### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:24Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema and stored procedure that model a web shop’s product catalog, customer orders, and order items, ensuring stock reservation and order total consistency.

#### Purpose
- Persist product, order, and order‑item data for an e‑commerce platform.
- Provide a single atomic operation to add an item to an order while updating inventory and totals.

#### Responsibilities
- **Data Modeling** – Define relational tables for products, orders, and items with appropriate keys, indexes, and constraints.
- **Business Logic** – Encapsulate order‑item addition, stock reservation, and total recalculation in a stored procedure.
- **Integrity Enforcement** – Use foreign keys, unique constraints, and transactional locking to maintain consistency.

#### Key units
- **Table `product`** – Stores SKU, name, price, and stock; PK `product_id`, unique `sku`.
- **Table `customer_order`** – Records order metadata, status, timestamps, and running total; PK `order_id`.
- **Table `order_item`** – Links orders to products, tracks quantity and unit price; composite PK `(order_id, product_id)`.
- **Procedure `add_order_item(p_order_id, p_sku, p_qty)`** – Looks up product, reserves stock, inserts/updates `order_item`, and updates `customer_order.total_cents`.

#### Data flow
1. Caller invokes `add_order_item` with order ID, SKU, and quantity.
2. Procedure locks the matching `product` row (`FOR UPDATE`) to check stock.
3. If stock is sufficient, inserts or updates the corresponding `order_item` row.
4. Decrements `product.stock` by the ordered quantity.
5. Recalculates the order total by summing all `order_item` rows for the order and updates `customer_order.total_cents`.
6. Transaction commits, ensuring all changes are atomic.

#### Dependencies
- **Internal** – Tables `product`, `customer_order`, `order_item`; foreign key relationships enforce referential integrity.
- **External** – None; relies solely on MySQL 8 features (ENUM, `FOR UPDATE`, `ON DUPLICATE KEY`).

#### Cross-cutting
- **Concurrency Control** – Uses row‑level locking (`FOR UPDATE`) to prevent race conditions on stock and totals.
- **Error Handling** – Procedure raises a clear error when SKU is unknown or stock is insufficient.
- **Indexing** – `idx_order_customer` on `customer_order` for efficient customer‑order queries; unique SKU index on `product`.
