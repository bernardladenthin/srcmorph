### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:54:31Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 web‑shop module that manages order line items, reserves inventory, and keeps order totals accurate.

#### Purpose
- Add items to customer orders.
- Reserve product stock.
- Recalculate order totals.

#### Responsibilities
- **Order line management** – inserting/updating `order_item` rows.
- **Stock reservation** – decrementing `product.stock` with row locking.
- **Order total maintenance** – updating `customer_order.total_cents` after each change.

#### Key units
- **Tables**  
  - `product` – SKU, price, stock.  
  - `customer_order` – status, total, timestamps.  
  - `order_item` – line items linking orders to products.  
- **Procedure** `add_order_item(p_order_id, p_sku, p_qty)` – core logic for adding an item, reserving stock, and updating totals.

#### Data flow
1. `add_order_item` receives order ID, SKU, and quantity.  
2. Locks the matching `product` row (`FOR UPDATE`) and checks stock.  
3. Inserts or updates the corresponding `order_item` row.  
4. Decrements `product.stock` by the ordered quantity.  
5. Recalculates `customer_order.total_cents` from all items of that order.

#### Dependencies
- Relies on the three tables: `product`, `customer_order`, `order_item`.  
- Uses MySQL 8 features: `FOR UPDATE`, `SIGNAL`, `ENUM` status.

#### Cross-cutting
- **Row locking** ensures inventory consistency.  
- **Error signaling** for insufficient stock or missing SKU.  
- **Atomicity**: all updates occur within the stored procedure’s transaction scope.
