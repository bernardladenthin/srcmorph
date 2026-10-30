### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:37Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages product inventory and customer orders, reserving stock and updating order totals when products are added to orders.

#### Purpose
- Stores product stock, customer order status and totals, and order-line quantities and prices.
- Adds products to orders while decrementing inventory and recalculating totals.

#### Responsibilities
- **Inventory and catalog:** `product` records SKU, name, price, and stock.
- **Order management:** `customer_order` stores customer, status, creation time, and total.
- **Order lines:** `order_item` records products, quantities, and unit prices.
- **Order-line operation:** `add_order_item` adds or increments a line and updates stock and order totals.

#### Key units
- `product` — product catalog and available inventory.
- `customer_order` — customer order status and aggregate total.
- `order_item` — per-order product quantities and unit prices.
- `add_order_item` — locks an eligible product by SKU, updates its order line, decrements stock, and recalculates the total.

#### Data flow
- `add_order_item` looks up and locks an in-stock product by SKU; if none qualifies, it signals SQLSTATE `45000`.
- It inserts or increments the matching `order_item`, decrements `product.stock`, and sets `customer_order.total_cents` from the order-line aggregate.

#### Dependencies
- Internal tables: `product`, `order_item`, and `customer_order`.
- `order_item` references `customer_order` and `product`; deleting an order cascades to its order lines.

#### Cross-cutting
- Product lookup uses `SELECT ... FOR UPDATE`.
- Foreign keys enforce order-line relationships; the order foreign key uses `ON DELETE CASCADE`.
