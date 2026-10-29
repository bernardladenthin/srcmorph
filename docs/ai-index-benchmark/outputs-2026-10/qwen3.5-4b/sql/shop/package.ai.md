### sql/shop
- H: 1.0
- C: 912A1271
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:06:50Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Provides SQL schema and stored procedures for managing product inventory, customer orders, and order line items in a web shop, including stock reservation and total calculation.

#### Purpose
*   Defines the relational schema for products, orders, and order items.
*   Implements a procedure to handle adding items to orders, reserving stock, and updating totals.

#### Responsibilities
*   Models the core data entities: product catalog, customer orders, and order line items.
*   Enforces data integrity through foreign keys and unique constraints on SKUs.
*   Executes atomic stock reservation and order total recalculation logic.

#### Key units
*   **product**: Stores product catalog data including SKU, name, price, and available stock.
*   **customer_order**: Tracks order status, customer reference, creation time, and calculated total.
*   **order_item**: Links products to orders with quantity and unit price details.
*   **add_order_item**: A MySQL procedure that reserves stock, inserts order items, and updates totals.

#### Data flow
*   Input: SKU, quantity, and order ID passed to the `add_order_item` procedure.
*   Process: Procedure checks stock availability, reserves inventory, inserts order line items, and recalculates order totals.
*   Output: Updated stock levels in `product`, new rows in `order_item`, and refreshed totals in `customer_order`.

#### Dependencies
*   Internal: `product`, `customer_order`, `order_item` tables.
*   External: None specified beyond standard MySQL 8 features.

#### Cross-cutting
*   Uses MySQL 8 stored procedures for transactional integrity.
*   Relies on foreign key constraints for cascade deletion of order items when orders are cancelled.
*   Enforces unique constraints on `sku` to prevent duplicate product entries.
