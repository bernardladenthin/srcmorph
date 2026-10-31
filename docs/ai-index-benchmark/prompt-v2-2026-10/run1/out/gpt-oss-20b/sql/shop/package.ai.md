### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:43:28Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema and stored procedure for managing a web shop’s catalog, orders, and inventory.

#### Purpose
- Persist product catalog, customer orders, and order line items.
- Provide transactional logic to add items to an order, reserve stock, and update order totals.

#### Responsibilities
- **Data Model** – tables for `product`, `customer_order`, and `order_item` with appropriate keys, indexes, and foreign‑key constraints.
- **Business Logic** – stored procedure `add_order_item` that:
  - Validates SKU existence and stock availability.
  - Inserts or updates an `order_item` row.
  - Decrements product stock.
  - Recalculates the order total.

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item`** – encapsulates the add‑to‑cart workflow with row‑level locking and total recalculation.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.
2. Procedure locks the matching `product` row (`FOR UPDATE`).
3. Checks SKU existence and stock ≥ `p_qty`; aborts if not.
4. Inserts or updates `order_item` (upsert) with the new quantity.
5. Decrements `product.stock` by `p_qty`.
6. Recomputes `customer_order.total_cents` from all `order_item` rows for that order.
7. Commits transaction.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.
- No external packages or sequences; relies solely on MySQL 8 features (ENUM, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`).

#### Cross-cutting
- **Row‑level locking** (`SELECT … FOR UPDATE`) ensures stock consistency.
- **Upsert pattern** (`INSERT … ON DUPLICATE KEY UPDATE`) keeps `order_item` quantities idempotent.
- **Enum status** (`'NEW','PAID','SHIPPED','CANCELLED'`) standardizes order lifecycle.
