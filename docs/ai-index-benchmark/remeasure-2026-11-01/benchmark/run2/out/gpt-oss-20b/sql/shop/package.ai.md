### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:39Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema for a web shop that stores products, orders, and line items, and provides a stored procedure to add items to an order while reserving stock and updating totals.

#### Purpose
- Persist product catalog, customer orders, and order line items.
- Offer a single procedure to add an item to an order, adjust inventory, and recalculate the order total.

#### Responsibilities
- **Data storage** – tables for `product`, `customer_order`, and `order_item`.
- **Business logic** – stored procedure `add_order_item` that handles stock reservation, item insertion/updating, and total recalculation.

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item`** – accepts order ID, SKU, and quantity; validates stock, inserts/updates line items, decrements stock, and updates the order total.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty` to `add_order_item`.
2. Procedure selects `product_id` and `price_cents` for the SKU with a `FOR UPDATE` lock.
3. If stock is insufficient, an error is signaled.
4. The line item is inserted or its quantity incremented.
5. `product.stock` is decremented by `p_qty`.
6. `customer_order.total_cents` is recalculated as the sum of all line items for that order.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item` tables.
- No external functions or sequences; relies solely on MySQL 8 features.

#### Cross-cutting
- **Concurrency control** – `SELECT … FOR UPDATE` ensures safe stock updates.
- **Error handling** – procedure signals a generic error when SKU is unknown or stock is insufficient.
- **Indexing** – primary keys on each table, foreign keys with cascade delete, and an index on `(customer_id, created_at)` for order queries.
