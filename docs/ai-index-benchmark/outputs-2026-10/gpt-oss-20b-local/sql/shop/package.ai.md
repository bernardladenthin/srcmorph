### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:23:50Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 stored procedure that manages order line items, reserves inventory, and keeps the order total accurate.

#### Purpose
- Handle insertion/updating of order items.
- Reserve product stock.
- Recalculate order totals.

#### Responsibilities
- **Order Item Management** – insert or update `order_item` rows, adjust quantities.
- **Inventory Reservation** – decrement `product.stock` atomically.
- **Order Total Calculation** – recompute `customer_order.total_cents` after each change.

#### Key units
- **Tables**
  - `product` – holds SKU, price, and stock.
  - `customer_order` – tracks order status and total.
  - `order_item` – links orders to products with quantity and unit price.
- **Procedure**
  - `add_order_item(p_order_id, p_sku, p_qty)` – core routine for adding an item, reserving stock, and updating totals.

#### Data flow
1. `add_order_item` receives order ID, SKU, and quantity.  
2. Looks up `product_id` and `price_cents` for the SKU, locking the row (`FOR UPDATE`).  
3. If stock is insufficient, signals an error.  
4. Inserts or updates `order_item`; on duplicate key, increments quantity.  
5. Decrements `product.stock` by the ordered quantity.  
6. Recalculates `customer_order.total_cents` as the sum of all line totals for that order.

#### Dependencies
- **Internal** – `product`, `customer_order`, `order_item` tables.  
- **External** – none; all logic is contained within the procedure.

#### Cross-cutting
- Uses `FOR UPDATE` locking to prevent race conditions on stock.  
- Signals errors when SKU not found or stock insufficient.  
- Maintains referential integrity via foreign keys with cascade delete.
