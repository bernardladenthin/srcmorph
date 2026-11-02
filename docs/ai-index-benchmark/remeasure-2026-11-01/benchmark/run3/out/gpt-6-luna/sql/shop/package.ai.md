### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:03:16Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains web-shop product inventory and customer orders, including stock reservation and order-total calculation when items are added.

#### Purpose
- Stores product catalog, customer orders, and their line items.
- Adds order items while validating and reserving stock, then recalculates the order total.

#### Responsibilities
- **Catalog and inventory:** Defines products with unique SKUs, prices, and stock quantities.
- **Order storage:** Tracks customer orders, statuses, timestamps, totals, and item quantities and prices.
- **Order placement:** Validates SKU availability, adds or increments an order line, decrements inventory, and updates the order total.

#### Key units
- `product` — catalog entries with SKU, price, and available stock.
- `customer_order` — customer order records with `NEW`, `PAID`, `SHIPPED`, or `CANCELLED` status.
- `order_item` — order lines keyed by order and product, storing quantity and unit price.
- `add_order_item` — locks and validates a product, adds the requested quantity at its current price, reserves stock, and recalculates the order total.

#### Data flow
- `add_order_item` finds and locks the product by SKU, requiring sufficient stock; otherwise it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts an order line or increments its existing quantity, decrements product stock, then updates `customer_order.total_cents` from the order’s line quantities and unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascading deletion; `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- The procedure uses `FOR UPDATE` when checking product availability.
- Monetary values are stored as integer cents; `product.sku` is unique.
