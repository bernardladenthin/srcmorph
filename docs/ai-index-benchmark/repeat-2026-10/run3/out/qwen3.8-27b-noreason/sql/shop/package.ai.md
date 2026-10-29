### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:50:56Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Defines the relational schema and transactional logic for a web shop's product catalog, customer orders, and order line items, including stock enforcement and total recalculation.

#### Purpose
- Models the core e-commerce data structures: products, customer orders, and order line items.
- Provides atomic order modification logic to ensure data integrity during item addition.

#### Responsibilities
- **Catalog Management**: Stores product details including SKU, pricing in cents, and current stock levels.
- **Order Lifecycle**: Tracks customer orders with status progression (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`) and aggregate totals.
- **Order Line Items**: Maintains the many-to-many relationship between orders and products, recording quantities and unit prices at the time of purchase.
- **Transactional Logic**: Implements the `add_order_item` procedure to handle concurrent stock checks and order updates.

#### Key units
- **`product`**: Table storing `product_id`, `sku`, `name`, `price_cents`, and `stock`.
- **`customer_order`**: Table storing `order_id`, `customer_id`, `status`, `created_at`, and `total_cents`.
- **`order_item`**: Junction table linking `order_id` and `product_id` with `quantity` and `unit_price_cents`.
- **`add_order_item`**: Stored procedure that atomically adds items to an order, validates stock, and recalculates totals.

#### Data flow
1. Input: `p_order_id`, `p_sku`, `p_qty` passed to `add_order_item`.
2. Validation: Procedure locks the `product` row by `sku` using `FOR UPDATE` and verifies `stock >= p_qty`.
3. Update: Inserts or updates `order_item` records; decrements `product.stock`.
4. Recalculation: Updates `customer_order.total_cents` based on the sum of `quantity * unit_price_cents` for all items in the order.
5. Output: Modified state of `product`, `order_item`, and `customer_order` tables; raises SQLSTATE '45000' on failure.

#### Dependencies
- **Internal**: `product`, `customer_order`, and `order_item` tables are interdependent via foreign keys.
- **External**: MySQL 8 InnoDB engine for transactional support and row-level locking.

#### Cross-cutting
- **Concurrency Control**: Uses `FOR UPDATE` row locking in `add_order_item` to prevent race conditions on stock.
- **Error Handling**: Custom SQLSTATE '45000' raised for insufficient stock or missing products.
- **Data Integrity**: Foreign keys with `ON DELETE CASCADE` for order items; unique constraint on `product.sku`.
