### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:19Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.

#### Purpose
- Handle adding or updating a single order line.
- Reserve product stock atomically.
- Recompute the order’s total amount.

#### Responsibilities
- **Order line management** – insert or update `order_item` rows.
- **Inventory control** – decrement `product.stock` with row locking.
- **Financial consistency** – recalculate `customer_order.total_cents` after each change.

#### Key units
- **Tables**
  - `product` – SKU, price, stock.
  - `customer_order` – order status, total.
  - `order_item` – line items linking orders to products.
- **Procedure**
  - `add_order_item(p_order_id BIGINT, p_sku VARCHAR, p_qty SMALLINT)` – core logic for line addition, stock reservation, and total recalculation.

#### Data flow
1. `add_order_item` receives order ID, SKU, and quantity.  
2. Locks the `product` row by SKU (`SELECT … FOR UPDATE`).  
3. Validates existence and stock sufficiency; errors if not.  
4. Inserts or updates the `order_item` row (`ON DUPLICATE KEY UPDATE`).  
5. Decrements `product.stock` by the ordered quantity.  
6. Recalculates `customer_order.total_cents` from all items of that order and updates the order row.

#### Dependencies
- **Internal** – `product`, `customer_order`, `order_item` tables.  
- **External** – MySQL 8 features: `SELECT … FOR UPDATE`, `ON DUPLICATE KEY UPDATE`.

#### Cross-cutting
- Uses row‑level locking to prevent race conditions on stock.  
- All updates occur within a single transaction implied by the procedure.  
- Error handling via MySQL error codes when stock is insufficient or product not found.
