### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:56:31Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL schema and stored procedure for managing e‑commerce orders, products, and inventory.

#### Purpose
- Define core tables for products, orders, and order items.
- Offer a stored procedure to add an order line, reserve stock, and update order totals.

#### Responsibilities
- **Data modeling** – tables `product`, `customer_order`, `order_item` with keys, indexes, and constraints.
- **Business logic** – procedure `add_order_item` that validates stock, inserts/updates order items, adjusts inventory, and recalculates order totals.
- **Integrity enforcement** – foreign keys, unique constraints, and transactional locking (`FOR UPDATE`).

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item(p_order_id, p_sku, p_qty)`** – encapsulates the add‑to‑order workflow.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.
2. Procedure locks the matching `product` row (`FOR UPDATE`).
3. Validates stock; signals error if insufficient.
4. Inserts or updates an `order_item` row.
5. Decrements `product.stock` by `p_qty`.
6. Recalculates `customer_order.total_cents` from all `order_item` rows for the order.

#### Dependencies
- Relies on tables `product`, `customer_order`, and `order_item`.
- Uses MySQL 8 features: `ENUM`, `FOR UPDATE`, `SIGNAL`.

#### Cross-cutting
- Consistent use of `INT UNSIGNED` for identifiers and monetary values.
- Transactional locking ensures atomicity of stock reservation and order total updates.
- Foreign key cascades (`ON DELETE CASCADE`) maintain referential integrity between orders and items.
