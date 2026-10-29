### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:18:07Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema and stored procedure for managing a webshop’s product catalog, customer orders, and order line items, including stock reservation and order total calculation.

#### Purpose
- Persist product, order, and order‑item data for a webshop.
- Provide a single stored procedure to add items to orders, reserve stock, and update totals.

#### Responsibilities
- **Data persistence**: tables `product`, `customer_order`, `order_item`.
- **Business logic**: stored procedure `add_order_item` handles validation, stock reservation, and total recalculation.
- **Integrity enforcement**: foreign keys, unique SKU, status enum, and stock constraints.

#### Key units
- **Table `product`** – holds SKU, name, price, and stock.
- **Table `customer_order`** – tracks order status, timestamps, and total amount.
- **Table `order_item`** – links orders to products with quantity and unit price.
- **Procedure `add_order_item(p_order_id, p_sku, p_qty)`** – inserts/updates an order item, decrements stock, and recomputes the order total.

#### Data flow
1. Caller supplies order ID, product SKU, and quantity to `add_order_item`.
2. Procedure locks the matching `product` row, verifies sufficient stock.
3. Inserts or updates the corresponding `order_item` row.
4. Decrements `product.stock` by the ordered quantity.
5. Recalculates `customer_order.total_cents` by summing all line items for the order.

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item`.
- MySQL 8 features: `ENUM`, `AUTO_INCREMENT`, row‑level locking, `CURRENT_TIMESTAMP`.

#### Cross‑cutting
- **Referential integrity**: foreign keys with `ON DELETE CASCADE` on `order_item`.
- **Error handling**: procedure raises an error if SKU not found or stock insufficient.
- **Indexing**: `idx_order_customer` on `customer_order` for efficient customer‑order queries.
