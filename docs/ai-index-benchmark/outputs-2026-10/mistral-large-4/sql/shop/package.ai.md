### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:51:37Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> E-commerce persistence layer for product catalog with stock control, customer order lifecycle management, and transactional order-item insertion that reserves inventory and maintains order totals.

#### Purpose
- Defines the core web shop schema: product catalog with SKU/price/stock, customer orders with status workflow, and per-order line items
- Provides the `add_order_item` stored procedure to atomically merge a line item, decrement inventory, and recompute the order total

#### Responsibilities
- Product catalog: `product` table stores unique SKU, name, price in cents, and mutable stock level
- Order header management: `customer_order` table tracks customer, status ENUM (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`), creation timestamp, and aggregate total
- Line item persistence: `order_item` table links orders to products via composite primary key with quantity and unit-price snapshot, cascading on order deletion
- Inventory reservation and pricing: `add_order_item` locks product rows, validates stock sufficiency, upserts line items, decrements stock, and refreshes the order total

#### Key units
- `product` — catalog entity with `product_id` PK, unique `uk_product_sku`, `price_cents`, and `stock` counter
- `customer_order` — order header with `order_id` PK, `customer_id` indexed via `idx_order_customer`, `status` ENUM, `created_at`, and `total_cents`
- `order_item` — line-item junction with composite PK (`order_id`, `product_id`), FKs `fk_item_order` (ON DELETE CASCADE) and `fk_item_product`, plus `quantity` and `unit_price_cents`
- `add_order_item` — stored procedure taking `p_order_id`, `p_sku`, `p_qty`; performs `SELECT ... FOR UPDATE` stock check, `INSERT ... ON DUPLICATE KEY UPDATE` upsert, stock decrement, and total recalculation via `SUM` subquery

#### Data flow
- Inputs `p_order_id`, `p_sku`, `p_qty` enter `add_order_item`
- Validation reads `product` with `FOR UPDATE` row lock where `stock >= p_qty`; signals `SQLSTATE '45000'` if SKU unknown or stock insufficient
- Mutations write `order_item` (merging quantity on duplicate key), decrement `product.stock`, and update `customer_order.total_cents` from the recomputed `SUM(quantity * unit_price_cents)`
- Outputs are the persisted line item, reduced inventory, and refreshed order total within one transaction

#### Dependencies
- Internal tables: `product`, `customer_order`, `order_item` with foreign keys `fk_item_order` and `fk_item_product`
- MySQL 8 features: InnoDB engine, utf8mb4 charset, `ENUM`, `SIGNAL SQLSTATE '45000'`, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`, `CURRENT_TIMESTAMP`
- Indexes: `uk_product_sku` on `product.sku`; `idx_order_customer` on `customer_order(customer_id, created_at)`

#### Cross-cutting
- Monetary amounts stored as integer cents (`price_cents`, `total_cents`, `unit_price_cents`)
- Concurrency control via row-level locking (`FOR UPDATE`) and conditional stock check in the `WHERE` clause
- Aggregate maintenance by recomputing `customer_order.total_cents` from line items rather than incremental updates
- Domain error handling through `SIGNAL SQLSTATE '45000'` with custom `MESSAGE_TEXT`
