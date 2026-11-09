### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:19:20Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides the database schema and stored procedure for a web shop’s product catalog, orders, and order line items, handling stock reservation and order total calculation.

#### Purpose
- Persist products, customer orders, and order line items.
- Offer a stored procedure to add items to orders, reserve stock, and update totals.

#### Responsibilities
- **Data storage**: tables for products, orders, and items.
- **Business logic**: `add_order_item` procedure that validates stock, updates quantities, reserves inventory, and recalculates order totals.
- **Integrity enforcement**: foreign keys, unique SKU constraint, and status enumeration.

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item`** – core routine for adding items to an order and managing stock.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.
2. Procedure locks the product row (`FOR UPDATE`), checks stock.
3. Inserts or updates `order_item` with new quantity.
4. Decrements `product.stock` by `p_qty`.
5. Recalculates `customer_order.total_cents` from all items.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item` tables.
- No external functions or sequences; relies on MySQL 8 features (ENUM, AUTO_INCREMENT, `FOR UPDATE`).

#### Cross-cutting
- **Concurrency**: uses row‑level locking (`FOR UPDATE`) to prevent race conditions on stock.
- **Error handling**: raises a single error when SKU is unknown or stock is insufficient.
- **Indexing**: `idx_order_customer` on `(customer_id, created_at)` for order retrieval; primary keys enforce uniqueness.
