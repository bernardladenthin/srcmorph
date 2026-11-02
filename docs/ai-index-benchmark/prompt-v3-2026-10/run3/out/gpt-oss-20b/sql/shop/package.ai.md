### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:47:15Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides the core data model and stored procedure for a web shop’s product catalog, orders, and order items, including stock reservation and total calculation.

#### Purpose
- Define e‑commerce tables: `product`, `customer_order`, `order_item`.
- Offer `add_order_item` procedure to add items, reserve stock, and update order totals.

#### Responsibilities
- **Data persistence** – tables for products, orders, and items.
- **Business logic** – stored procedure handling stock checks, item insertion/updating, stock decrement, and total recalculation.

#### Key units
- `product` table – holds SKU, name, price, and stock.
- `customer_order` table – tracks order status, timestamps, and total.
- `order_item` table – links orders to products with quantity and unit price.
- `add_order_item` procedure – orchestrates item addition, stock reservation, and total update.

#### Data flow
1. `add_order_item` receives `p_order_id`, `p_sku`, `p_qty`.
2. Locks the matching `product` row (`FOR UPDATE`).
3. Validates stock and SKU existence.
4. Inserts or updates `order_item` (increments quantity if duplicate).
5. Decrements `product.stock` by `p_qty`.
6. Recalculates `customer_order.total_cents` from all items of the order.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item` tables.
- External: MySQL 8 features (`FOR UPDATE`, ENUM, auto‑increment).

#### Cross-cutting
- Consistent use of `UNSIGNED` numeric types and `INT`/`SMALLINT` sizing.
- Referential integrity via foreign keys (`fk_item_order`, `fk_item_product`).
- Transactional safety implied by row locking and updates within the procedure.
