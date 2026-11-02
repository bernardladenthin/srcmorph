### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:23:02Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and total calculation.

#### Purpose
- Persist product, order, and order‑item data for an online shop.
- Provide a single procedure to add an item to an order while enforcing stock limits and updating order totals.

#### Responsibilities
- **Data persistence**: tables `product`, `customer_order`, `order_item`.
- **Business logic**: stored procedure `add_order_item` that handles stock checks, item insertion/updating, stock decrement, and total recalculation.
- **Integrity enforcement**: foreign keys, unique constraints, and transactional locking (`FOR UPDATE`).

#### Key units
- **Table `product`** – holds SKU, name, price, and available stock.
- **Table `customer_order`** – tracks order status, timestamps, and running total.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item`** – accepts order ID, SKU, and quantity; reserves stock, inserts/updates an order line, and updates the order total.

#### Data flow
1. Caller supplies `p_order_id`, `p_sku`, `p_qty`.
2. Procedure locks the matching `product` row (`FOR UPDATE`) and verifies stock.
3. Inserts or updates an `order_item` row, adding `p_qty` if the product is already in the order.
4. Decrements `product.stock` by `p_qty`.
5. Recalculates `customer_order.total_cents` from all `order_item` line totals and updates the order row.

#### Dependencies
- **Internal**: `product`, `customer_order`, `order_item` tables; foreign key relationships `fk_item_order`, `fk_item_product`.
- **External**: None; all logic is contained within the MySQL schema and stored procedure.

#### Cross-cutting
- **Transactional consistency**: all operations occur within a single transaction; `FOR UPDATE` ensures row‑level locking.
- **Error handling**: procedure signals an error if SKU is unknown or stock is insufficient.
- **Data integrity**: unique SKU constraint, composite primary key on `order_item`, and cascading deletes on order items.
