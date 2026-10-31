### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:14:08Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains webshop product inventory and customer orders, including adding order lines, reserving stock, and recalculating order totals.

#### Purpose
- Stores product catalog and inventory alongside customer order status, totals, and line-item quantities and prices.
- Provides `add_order_item` to add or increment an order line, reduce stock, and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and available stock.
- **Order records:** `customer_order` tracks customer, status, creation time, and total; statuses are `NEW`, `PAID`, `SHIPPED`, and `CANCELLED`.
- **Order lines and fulfillment:** `order_item` stores each product’s quantity and unit price for an order; `add_order_item` manages line changes and stock reduction.

#### Key units
- `product` — product catalog and inventory, with a unique SKU.
- `customer_order` — customer order metadata, status, and total in cents.
- `order_item` — order-to-product line items, keyed by order and product.
- `add_order_item(IN p_order_id, IN p_sku, IN p_qty)` — checks SKU and stock, adds or increments a line, reduces inventory, and updates the order total.

#### Data flow
- `add_order_item` looks up the requested SKU and price while locking the matching product row; insufficient stock or an unknown SKU raises SQLSTATE `45000`.
- It inserts the order line or increments its quantity, then subtracts the requested quantity from product stock.
- It sums the order’s line totals and writes the result to `customer_order.total_cents`.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`.
- `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- Product lookup uses `FOR UPDATE` when checking available stock.
- Invalid SKU or insufficient stock is reported with SQLSTATE `45000` and message `unknown SKU or not enough stock`.
