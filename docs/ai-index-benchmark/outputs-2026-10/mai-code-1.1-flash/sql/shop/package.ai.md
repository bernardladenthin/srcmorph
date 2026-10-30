### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:48:59Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> This package manages the web shop’s catalog, order lifecycle, and inventory reservation for placing and reconciling order items.

#### Purpose
- Defines the relational model for catalog items, customer orders, and order-line fulfillment in the web shop.
- Enforces stock-aware order-item insertion and keeps order totals synchronized with the current line items.

#### Responsibilities
- Catalog and order schema: defines `product`, `customer_order`, and `order_item` with keys, constraints, and indexes.
- Order-item fulfillment logic: implements `add_order_item` to validate SKU/stock, reserve inventory, and recalculate totals.
- Order reconciliation: maintains `customer_order.total_cents` from aggregated `order_item` values and rejects invalid stock requests.

#### Key units
- `product` — catalog item table with `sku`, `name`, `price_cents`, and `stock` inventory.
- `customer_order` — order header table with `status`, `created_at`, and `total_cents` summary.
- `order_item` — join table linking orders to products with `quantity`, `unit_price_cents`, and composite key.
- `add_order_item` — MySQL procedure that validates a SKU, reserves stock, inserts or increments line items, and updates the order total.

#### Data flow
- Input arrives as `p_order_id`, `p_sku`, and `p_qty` to `add_order_item`.
- The procedure selects a matching `product` row by `sku` and checks `stock >= p_qty` with a `FOR UPDATE` lock.
- If no eligible product exists, it raises `SQLSTATE '45000'` with `MESSAGE_TEXT = 'unknown SKU or not enough stock'`.
- On success, it inserts or updates `order_item` for the order/product pair and decrements `product.stock`.
- It recomputes `customer_order.total_cents` from `SUM(quantity * unit_price_cents)` for that order and persists the result.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`
- Internal relationships:
  - `order_item.order_id -> customer_order.order_id`
  - `order_item.product_id -> product.product_id`
- MySQL built-ins: `CURRENT_TIMESTAMP`, `FOR UPDATE`, `SUM()`, `SIGNAL`
- Domain data: `sku`, `stock`, `total_cents`, `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')`

#### Cross-cutting
- Referential integrity: `FK` constraints on `order_item` enforce order and product validity with cascade delete for order removal.
- Transactional locking: `SELECT ... FOR UPDATE` on `product` prevents stock races while reserving quantity.
- Recalculation pattern: `customer_order.total_cents` is derived from `order_item` totals instead of treating the aggregate as a separate source of truth.
- Validation and failure handling: invalid or insufficient stock is rejected with a controlled SQL error instead of allowing incomplete order writes.
- Operational defaults: `created_at` uses `CURRENT_TIMESTAMP`, and new orders default to `status = 'NEW'` and `total_cents = 0`.
