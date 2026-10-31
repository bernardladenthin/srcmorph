### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:56:23Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides a MySQL 8 schema and stored procedure for managing products, orders, and inventory in an online shop.

#### Purpose
- Persist product catalog, customer orders, and order line items.  
- Offer a stored procedure to add items to an order, reserve stock, and recalculate the order total.

#### Responsibilities
- **Catalog persistence** – `product` table stores SKU, name, price, and stock.  
- **Order lifecycle** – `customer_order` tracks status, timestamps, and total.  
- **Line‑item handling** – `order_item` links orders to products with quantity and unit price.  
- **Business logic** – `add_order_item` procedure enforces stock checks, updates inventory, and maintains order totals.

#### Key units
- `product` (PK: `product_id`, unique `sku`, `price_cents`, `stock`).  
- `customer_order` (PK: `order_id`, `status` ENUM, `total_cents`).  
- `order_item` (PK: (`order_id`, `product_id`), FK to `customer_order` and `product`).  
- `add_order_item` (MySQL 8 procedure: parameters `p_order_id`, `p_sku`, `p_qty`; locks product row, updates inventory, inserts/updates line item, recalculates order total).

#### Data flow
1. `add_order_item` receives `p_order_id`, `p_sku`, `p_qty`.  
2. SELECT `product_id`, `price_cents` FROM `product` WHERE `sku` = `p_sku` AND `stock` ≥ `p_qty` FOR UPDATE.  
3. If not found → SIGNAL error.  
4. INSERT or UPDATE `order_item` with `quantity` and `unit_price_cents`.  
5. UPDATE `product.stock` = `stock` – `p_qty`.  
6. Recalculate `customer_order.total_cents` = SUM(`quantity` × `unit_price_cents`) for the order.

#### Dependencies
- Internal: `product`, `customer_order`, `order_item`.  
- No external functions or sequences; relies on MySQL 8 features (FOR UPDATE, ENUM, ON DELETE CASCADE).

#### Cross-cutting
- **Referential integrity** – FK constraints enforce order‑item and product relationships.  
- **Concurrency control** – `FOR UPDATE` locks product rows during stock reservation.  
- **Default values & indexes** – `status` defaults to `NEW`; `idx_order_customer` indexes `customer_id` and `created_at`.  
- **Error handling** – procedure signals error when SKU not found or insufficient stock.
