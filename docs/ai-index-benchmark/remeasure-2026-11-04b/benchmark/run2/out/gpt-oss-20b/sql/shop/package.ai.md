### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:04:12Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL package that manages order line items, reserves inventory, and keeps order totals accurate.

#### Purpose
- Persist order items and update related order totals.
- Reserve product stock when items are added.

#### Responsibilities
- **Order Item Management** – insert or update line items in `order_item`.
- **Stock Reservation** – decrement `product.stock` upon item addition.
- **Order Total Recalculation** – recompute `customer_order.total_cents` after changes.

#### Key units
- **Tables**
  - `product` – holds SKU, price, and stock.
  - `customer_order` – tracks order status and total.
  - `order_item` – links orders to products with quantity and unit price.
- **Procedure**
  - `add_order_item(p_order_id, p_sku, p_qty)` – core routine for adding items, locking product rows, updating stock, inserting/updating order items, and recalculating totals.

#### Data flow
1. `add_order_item` receives order ID, SKU, and quantity.  
2. Locks the matching `product` row (`SELECT … FOR UPDATE`).  
3. Validates stock availability; signals error if insufficient.  
4. Inserts or updates `order_item` (using `ON DUPLICATE KEY`).  
5. Decrements `product.stock` by the quantity.  
6. Recalculates `customer_order.total_cents` from all items of the order.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.  
- No external functions or sequences; relies on MySQL 8 transactional guarantees.

#### Cross-cutting
- Uses `SELECT … FOR UPDATE` for row‑level locking.  
- Error handling via `SIGNAL`.  
- All monetary values stored in cents (`INT UNSIGNED`).  
- Enumerated order status (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`).
