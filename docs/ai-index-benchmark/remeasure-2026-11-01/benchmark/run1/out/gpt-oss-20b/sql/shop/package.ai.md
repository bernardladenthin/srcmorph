### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:48Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and total calculation.

#### Purpose
- Persist product catalog, orders, and order items.
- Offer a single procedure to add items to an order while handling stock and total updates.

#### Responsibilities
- **Data Model** – Tables for `product`, `customer_order`, and `order_item` with keys, indexes, and constraints.
- **Business Logic** – Stored procedure `add_order_item` that validates stock, inserts or updates line items, adjusts inventory, and recalculates order totals.
- **Integrity** – Foreign keys, unique SKU constraint, and status enum enforce consistency.

#### Key units
- `product` table – holds SKU, name, price, and stock.
- `customer_order` table – tracks order status, timestamps, and total amount.
- `order_item` table – links orders to products with quantity and unit price.
- `add_order_item(p_order_id, p_sku, p_qty)` – reserves stock, updates totals, and signals errors.

#### Data flow
1. Caller supplies order ID, SKU, and quantity to `add_order_item`.
2. Procedure locks the product row, verifies stock, and retrieves price.
3. Inserts or updates the corresponding `order_item`.
4. Decrements `product.stock` by the ordered quantity.
5. Recalculates `customer_order.total_cents` from all line items.

#### Dependencies
- **Tables**: `product`, `customer_order`, `order_item`.
- **Columns**: `product.sku`, `product.stock`, `product.price_cents`, `order_item.order_id`, `order_item.product_id`, `order_item.quantity`, `order_item.unit_price_cents`, `customer_order.total_cents`.
- **Constraints**: FK `fk_item_order`, FK `fk_item_product`, unique SKU, status enum.

#### Cross-cutting
- Row‑level locking (`FOR UPDATE`) ensures concurrent reservations are safe.
- Error handling via `SIGNAL` for missing products or insufficient stock.
- Automatic timestamping and default values for new orders.
