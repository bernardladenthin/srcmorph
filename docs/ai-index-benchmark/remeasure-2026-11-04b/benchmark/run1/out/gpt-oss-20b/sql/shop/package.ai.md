### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:39Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema and stored procedure that manage a web shop’s product catalog, customer orders, and order line items, ensuring stock reservation and accurate order totals.

#### Purpose
- Persist product catalog, orders, and order items.
- Provide a transactional routine to add items to an order, reserve stock, and update the order total.

#### Responsibilities
- **Data persistence**: tables for `product`, `customer_order`, `order_item`.
- **Business logic**: stored procedure `add_order_item` that validates stock, inserts/updates line items, decrements inventory, and recalculates order totals.

#### Key units
- **Tables**
  - `product`: product catalog with SKU, price, and stock.
  - `customer_order`: order header with status and total.
  - `order_item`: line items linking orders to products.
- **Procedure**
  - `add_order_item(p_order_id BIGINT UNSIGNED, p_sku VARCHAR(40), p_qty SMALLINT UNSIGNED)`: transactional routine for adding an item to an order.

#### Data flow
1. Caller supplies order ID, product SKU, and quantity.
2. Procedure selects product ID and price with `FOR UPDATE` to lock stock.
3. If product exists and stock sufficient, inserts or updates `order_item`.
4. Decrements `product.stock` by the quantity.
5. Recalculates `customer_order.total_cents` as the sum of all line items for that order.

#### Dependencies
- **Internal**: `product`, `customer_order`, `order_item` tables.
- **External**: None; relies solely on MySQL 8 features (InnoDB, ENUM, SIGNAL).

#### Cross-cutting
- **Referential integrity**: foreign keys `fk_item_order` and `fk_item_product` enforce consistency.
- **Concurrency control**: `SELECT … FOR UPDATE` ensures atomic stock reservation.
- **Error handling**: procedure signals an error if the SKU is not found or stock is insufficient.
