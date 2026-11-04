### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:13:57Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop inventory and customer order lines, reserving product stock and maintaining order totals when items are added.

#### Purpose
- Stores products, customer orders, and order-item quantities and prices.
- Adds items by SKU while deducting inventory and recalculating the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and available stock.
- **Order records:** `customer_order` tracks customer, status, creation time, and total; `order_item` records quantities and unit prices.
- **Item addition and stock reservation:** `add_order_item` locks a matching product, inserts or increments its order line, deducts stock, and updates the order total.

#### Key units
- `product` — product catalog and stock inventory, with unique SKU lookup.
- `customer_order` — customer order header with `NEW`, `PAID`, `SHIPPED`, or `CANCELLED` status.
- `order_item` — order lines keyed by `(order_id, product_id)`, linked to orders and products.
- `add_order_item(p_order_id, p_sku, p_qty)` — adds a requested quantity by SKU and refreshes stock and order total.

#### Data flow
- `add_order_item` finds and locks a `product` by SKU, requiring sufficient stock; signals SQLSTATE `45000` if no matching product is found.
- It inserts an `order_item` or increments the existing line quantity, then deducts the requested quantity from `product.stock`.
- It recalculates `customer_order.total_cents` from the order’s line quantities and unit prices.

#### Dependencies
- Internal tables: `product`, `order_item`, and `customer_order`.
- `order_item.order_id` references `customer_order.order_id` with cascade deletion; `order_item.product_id` references `product.product_id`.

#### Cross-cutting
- Uses MySQL 8 tables and a stored procedure.
- Product lookup uses a row lock; `order_item` has a composite primary key to support duplicate-key quantity updates.
