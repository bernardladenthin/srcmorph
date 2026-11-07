### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:12:07Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL schema and stored procedure for managing a web‑shop’s product catalog, customer orders, and order line items, including stock reservation and total calculation.

#### Purpose
- Persist product, order, and order‑item data for an online shop.
- Provide a single stored procedure to add items to an order, reserve stock, and update the order total.

#### Responsibilities
- **Catalog Management** – `product` table stores SKU, name, price, and stock.
- **Order Lifecycle** – `customer_order` tracks status, timestamps, and total amount.
- **Line‑Item Handling** – `order_item` links orders to products with quantity and unit price.
- **Business Logic** – `add_order_item` procedure encapsulates validation, stock locking, quantity adjustment, stock decrement, and total recalculation.

#### Key units
- `product` (PK: `product_id`; unique SKU; price in cents; stock count)
- `customer_order` (PK: `order_id`; status enum; total in cents)
- `order_item` (composite PK: `order_id`, `product_id`; quantity; unit price)
- `add_order_item(p_order_id BIGINT, p_sku VARCHAR, p_qty SMALLINT)` – reserves stock, updates or inserts an order item, and refreshes the order total.

#### Data flow
1. Caller invokes `add_order_item` with order ID, SKU, and quantity.
2. Procedure locks the matching `product` row (`FOR UPDATE`) after verifying sufficient stock.
3. Inserts or updates the corresponding `order_item` row.
4. Decrements `product.stock` by the requested quantity.
5. Recalculates `customer_order.total_cents` by summing all line items for that order.
6. Commits the transaction, ensuring atomicity of stock reservation and total update.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.
- No external functions or sequences; relies on MySQL 8 features (ENUM, `FOR UPDATE`, auto‑increment).

#### Cross-cutting
- **Concurrency control** – `SELECT … FOR UPDATE` on `product` ensures safe stock decrement.
- **Error handling** – procedure raises an error if SKU not found or stock insufficient.
- **Data integrity** – foreign keys enforce referential consistency; cascading delete from `customer_order` to `order_item`.
- **Performance** – indexes on `product.sku`, `customer_order.customer_id`, and composite PKs support efficient lookups.
