### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:35Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> MySQL stored procedure to atomically add line items to customer orders, validate inventory availability, and recalculate order totals.

#### Purpose
- Adds line items to existing orders while validating stock levels and reserving inventory.
- Ensures data consistency by updating product stock counts and recalculating the aggregate order total.

#### Responsibilities
- **Inventory Validation**: Locks and verifies product availability before processing.
- **Order Line Management**: Inserts new items or updates quantities for existing `order_id` + `product_id` pairs.
- **Stock Reservation**: Decrements available stock on the `product` table upon successful addition.
- **Total Recalculation**: Aggregates line item values to update the `customer_order` total.

#### Key units
- `add_order_item`: Stored procedure accepting `p_order_id`, `p_sku`, and `p_qty` to orchestrate the transaction.
- `product`: Master table storing SKU, price, and current stock levels with row-level locking.
- `customer_order`: Header table tracking order status and cumulative `total_cents`.
- `order_item`: Junction table linking orders to products with quantity and unit price details.

#### Data flow
1.  **Input**: Procedure receives `p_order_id`, `p_sku`, and `p_qty`.
2.  **Validation**: Selects `product` row matching `p_sku` using `FOR UPDATE` lock; fails if stock is insufficient.
3.  **Execution**:
    -   Updates `product.stock` (decrement).
    -   Inserts or updates `order_item` record.
    -   Recalculates sum of `quantity * unit_price_cents` from `order_item`.
4.  **Output**: Updates `customer_order.total_cents` to reflect the new sum.

#### Dependencies
- **Internal Tables**: `product`, `customer_order`, `order_item`.
- **Constraints**: Foreign keys enforce `order_item.order_id` -> `customer_order` and `order_item.product_id` -> `product` with cascade deletes.

#### Cross-cutting
- **Concurrency Control**: Uses `SELECT ... FOR UPDATE` to prevent race conditions during stock reservation.
- **Error Handling**: Explicitly signals errors when products are missing or stock is insufficient.
- **Data Types**: Utilizes `INT UNSIGNED` for monetary values (`price_cents`, `total_cents`) to avoid floating-point precision issues.
