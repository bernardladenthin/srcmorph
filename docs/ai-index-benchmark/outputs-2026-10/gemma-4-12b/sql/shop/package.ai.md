### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:41:59Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> This package manages a web shop's inventory and order processing system, handling product cataloging, stock management, and transactional order fulfillment.

#### Purpose
*   Manages product catalog, customer orders, and order line items.
*   Ensures atomic order processing including stock deduction and total recalculation.

#### Responsibilities
*   **Inventory Management**: Tracking product details (SKU, price, stock) and updating stock levels during sales.
*   **Order Processing**: Handling order lifecycle statuses and line item insertion.
*   **Transactional Logic**: Executing atomic updates to ensure data consistency between order totals, item quantities, and available stock.

#### Key units
*   `product`: Stores catalog information including `sku`, `price_cents`, and `stock` levels.
*   `customer_order`: Tracks order status, customer associations, and the aggregate `total_cents`.
*   `order_item`: Represents individual line items linking products to specific orders with `quantity` and `unit_price_cents`.
*   `add_order_item` (Procedure): Orchestrates the logic to validate stock, insert/update items, decrement inventory, and refresh order totals.

#### Data flow
1.  Input `p_order_id`, `p_sku`, and `p_qty` are passed to the `add_order_item` procedure.
2.  The system locks and validates the `product` record based on `sku`.
3.  An `order_item` record is created or updated.
4.  The `product` stock is decremented.
5.  The `customer_order` total is recalculated by summing all associated `order_item` values.

#### Dependencies
*   Internal tables: `product`, `customer_order`, `order_item`.
