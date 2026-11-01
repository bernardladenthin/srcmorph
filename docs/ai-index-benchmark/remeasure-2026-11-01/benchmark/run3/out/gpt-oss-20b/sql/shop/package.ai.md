### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:08:51Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and order total calculation.

#### Purpose
- Persist product catalog, customer orders, and order line items.
- Provide a single procedure to add items to an order while reserving stock and updating the order total.

#### Responsibilities
- **Data Model** – tables for products, orders, and order items with appropriate keys, indexes, and constraints.
- **Business Logic** – a stored procedure that validates stock, updates inventory, inserts or updates order items, and recalculates the order total.
- **Integrity Enforcement** – foreign‑key relationships and unique constraints to maintain referential integrity.

#### Key units
- **Table `product`** – stores SKU, name, price, and stock.
- **Table `customer_order`** – records order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item`** – accepts order ID, SKU, and quantity; performs stock check, inserts/updates `order_item`, decrements `product.stock`, and updates `customer_order.total_cents`.

#### Data flow
1. `add_order_item` receives `p_order_id`, `p_sku`, `p_qty`.
2. Locks the matching `product` row, verifies sufficient stock.
3. Inserts or updates the corresponding `order_item` row.
4. Decrements `product.stock` by `p_qty`.
5. Recalculates `customer_order.total_cents` from all items of the order.

#### Dependencies
- **Internal** – `product`, `customer_order`, `order_item` tables.
- **External** – none; relies solely on MySQL 8 features (FOR UPDATE, ON DUPLICATE KEY UPDATE).

#### Cross-cutting
- **Concurrency** – row‑level locking (`FOR UPDATE`) ensures safe stock updates.
- **Error handling** – procedure raises an error if the SKU is missing or stock is insufficient.
- **Indexing** – `idx_order_customer` on `customer_order` for efficient customer‑order queries; unique SKU constraint on `product`.
