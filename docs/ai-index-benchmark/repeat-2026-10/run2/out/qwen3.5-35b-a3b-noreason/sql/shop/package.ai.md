### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:06Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> MySQL stored procedure that atomically adds line items to an order by validating stock, reserving inventory, and recalculating order totals.

#### Purpose
- Adds product line items to an existing order while enforcing stock availability and SKU existence.
- Reserves inventory and updates order totals in a single atomic transaction.

#### Responsibilities
- **Inventory Validation**: Locks and verifies product stock levels before processing.
- **Line Item Management**: Inserts new items or increments quantities for existing items using upsert logic.
- **State Synchronization**: Updates product stock counts and recalculates order totals based on line item data.

#### Key units
- `add_order_item`: MySQL stored procedure handling the core order line item insertion and validation logic.
- `product`: Table storing SKU, price, and current stock levels used for validation and updates.
- `customer_order`: Table tracking order status and aggregate `total_cents` recalculated during the process.
- `order_item`: Junction table linking orders to products, storing quantity and unit price.

#### Data flow
1. **Input**: Procedure receives `p_order_id`, `p_sku`, and `p_qty`.
2. **Validation**: Selects `product` row with `FOR UPDATE` lock to verify SKU existence and sufficient `stock`.
3. **Execution**:
   - Inserts or updates `order_item` (using `ON DUPLICATE KEY UPDATE` for quantity).
   - Decrements `product.stock` by `p_qty`.
   - Recalculates `customer_order.total_cents` by summing `quantity * unit_price_cents`.
4. **Output**: Commits changes or raises error if validation fails.

#### Dependencies
- **Internal**: `product`, `customer_order`, `order_item` tables.
- **External**: None specified beyond standard MySQL storage engine capabilities.

#### Cross-cutting
- **Concurrency**: Uses row-level locking (`SELECT ... FOR UPDATE`) on the `product` table to prevent race conditions during stock reservation.
- **Error Handling**: Explicitly signals errors for missing products or insufficient stock.
- **Constraints**: Relies on foreign keys (`fk_item_order`, `fk_item_product`) and unique indexes (`uk_product_sku`) for data integrity.
