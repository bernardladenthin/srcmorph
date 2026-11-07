### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:50:58Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a shop catalog and inventory alongside customer orders, and adds order items while reserving stock and updating order totals.

#### Purpose
- Defines the product, customer-order, and order-item data model.
- Adds order items by SKU, tracking inventory changes and recalculating the order total.

#### Responsibilities
- **Catalog and orders:** Store product SKUs, prices, and stock; track customer orders, their statuses, creation times, and totals.
- **Order lines and inventory:** Associate products with orders, recording quantities and unit prices; cascade order deletion to its items.
- **Order-item routine:** Lock and validate a requested product, add or increment its order line, decrement stock, and recalculate the order total.

#### Key units
- `product` — product catalog and available stock, with unique SKU lookup.
- `customer_order` — customer order status and aggregate total.
- `order_item` — per-order product quantities and unit prices, keyed by order and product.
- `add_order_item` — MySQL procedure that adds an item by SKU and updates stock and order totals.

#### Data flow
- `add_order_item` finds and locks an in-stock `product` by SKU; if none is available, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- The procedure inserts an `order_item` at the current product price or increments an existing line’s quantity, then subtracts the requested quantity from product stock.
- It sums each order line’s `quantity * unit_price_cents` and writes the result to `customer_order.total_cents`.

#### Dependencies
- Internal tables: `product`, `customer_order`, and `order_item`.
- Foreign keys link order items to their order and product; deleting an order cascades to its items.

#### Cross-cutting
- Uses MySQL 8 tables and a stored procedure.
- Product lookup uses `FOR UPDATE` to lock the matching in-stock product.
- Monetary values are stored in integer cents; order status is `NEW`, `PAID`, `SHIPPED`, or `CANCELLED`.
