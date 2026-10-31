### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:47Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, including stock reservation and order total calculation.

#### Purpose
- Persist product, order, and order‑item data for an e‑commerce platform.
- Provide a single procedure to add items to an order while enforcing stock limits and updating totals.

#### Responsibilities
- **Data storage**: tables `product`, `customer_order`, `order_item`.
- **Business logic**: stored procedure `add_order_item` that handles inventory checks, line‑item updates, stock decrement, and order total recalculation.

#### Key units
- **`product`** – catalog table with `product_id`, `sku`, `name`, `price_cents`, `stock`.
- **`customer_order`** – order header with `order_id`, `customer_id`, `status`, `created_at`, `total_cents`.
- **`order_item`** – line‑item table linking orders to products, with `quantity` and `unit_price_cents`.
- **`add_order_item`** – procedure that accepts `p_order_id`, `p_sku`, `p_qty`; locks the product row, validates stock, inserts/updates the line item, decrements stock, and recomputes the order total.

#### Data flow
1. Caller supplies order ID, product SKU, and quantity to `add_order_item`.
2. Procedure selects the product row by SKU with `FOR UPDATE` to lock stock.
3. Validates existence and sufficient stock; otherwise raises an error.
4. Inserts or updates the corresponding `order_item` row.
5. Decrements `product.stock` by the ordered quantity.
6. Recalculates `customer_order.total_cents` by summing all line items for the order.

#### Dependencies
- Internal: tables `product`, `customer_order`, `order_item`.
- No external functions or sequences; relies solely on MySQL 8 features (ENUM, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`).

#### Cross‑cutting
- **Referential integrity**: foreign keys `fk_item_order` and `fk_item_product` enforce consistency between orders, items, and products.
- **Concurrency control**: `SELECT … FOR UPDATE` ensures atomic stock reservation.
- **Error handling**: procedure raises errors when product not found or stock insufficient, preventing inconsistent state.
