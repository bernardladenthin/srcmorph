### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:00:20Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a shop catalog and customer orders, including order-line creation, inventory reservation, and order-total calculation.

#### Purpose
- Stores products and stock, customer orders and statuses, and per-product order lines.
- Provides `add_order_item` to add or increase a line while reducing stock and recalculating the order total.

#### Responsibilities
- **Catalog and inventory:** `product` records SKU, name, price, and stock; SKU is unique.
- **Order storage:** `customer_order` tracks customer, status, creation time, and total; statuses are `NEW`, `PAID`, `SHIPPED`, and `CANCELLED`.
- **Order lines and updates:** `order_item` links orders to products, stores quantity and unit price, and cascades deletion when its order is deleted.
- **Order-item routine:** `add_order_item` validates SKU and available stock, inserts or increments a line, decrements stock, and updates the order total.

#### Key units
- `product` — product catalog and inventory table keyed by `product_id`.
- `customer_order` — customer order header and status/total table keyed by `order_id`.
- `order_item` — order-to-product line table keyed by `(order_id, product_id)`.
- `add_order_item(p_order_id, p_sku, p_qty)` — adds requested quantity to an order after checking product eligibility and stock.

#### Data flow
- `add_order_item` finds a `product` by SKU, locks the matching row, and checks that stock covers the requested quantity.
- On success, it inserts an `order_item` or increments its existing quantity, then subtracts the requested quantity from `product.stock`.
- It recalculates `customer_order.total_cents` from the order’s line quantities and unit prices; an unknown SKU or insufficient stock signals SQLSTATE `45000`.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade delete; `order_item.product_id` references `product.product_id`.
- `add_order_item` coordinates reads and writes across `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- Uses primary keys, foreign keys, SKU uniqueness, and an `(order_id, product_id)` composite key to enforce identity and relationships.
- The routine uses `FOR UPDATE` for the matching product row and reports eligibility failures through a SQL exception.
