### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:32Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop product inventory and customer orders, including adding order items while reserving stock and recalculating order totals.

#### Purpose
- Defines the product catalog, customer orders, and order-line data model.
- Provides `add_order_item` to place items on orders, decrement inventory, and update totals.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock; SKU uniquely identifies products for order placement.
- **Order records:** `customer_order` stores customer, status, creation time, and total.
- **Order lines:** `order_item` records product quantities and unit prices, linked to orders and products.
- **Order placement:** `add_order_item` validates SKU and available stock, inserts or increases a line, reserves inventory, and recalculates the order total.

#### Key units
- `product` — product catalog and available stock.
- `customer_order` — customer order header with `NEW`, `PAID`, `SHIPPED`, or `CANCELLED` status.
- `order_item` — per-product quantities and unit prices within an order.
- `add_order_item` — adds a SKU and quantity to an order, signaling SQLSTATE `45000` for an unknown SKU or insufficient stock.

#### Data flow
- `add_order_item` locates and locks the matching `product` row, requiring sufficient stock.
- It inserts an `order_item` or adds to the existing line quantity, then decrements `product.stock`.
- It recalculates `customer_order.total_cents` from the order’s lines.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`; `order_item.product_id` references `product.product_id`.
- `product.sku` is uniquely indexed; `customer_order` has an index on `(customer_id, created_at)`.

#### Cross-cutting
- `add_order_item` locks inventory during validation and uses a SQL exception signal for invalid SKU or insufficient stock.
- Monetary values are stored in cents; order lines use `(order_id, product_id)` as their primary key.
