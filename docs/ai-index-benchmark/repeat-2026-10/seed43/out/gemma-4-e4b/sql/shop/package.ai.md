### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:01:13Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> This package manages the persistence layer for a web shop, defining schemas for products, customer orders, and the transactional logic for placing orders.

#### Purpose
* Defines core entities for a web shop system: products, customer orders, and order line items.
* Provides a stored procedure to atomically manage order creation, stock reservation, and total calculation.

#### Responsibilities
* Schema definition for `product`, `customer_order`, and `order_item` tables.
* Implementing the `add_order_item` stored procedure for transactional order processing.

#### Key units
* `product`: Stores product details including SKU, price, and current stock.
* `customer_order`: Tracks customer orders, including status and total value.
* `order_item`: Links products to orders, detailing quantity and unit price.
* `add_order_item`: Stored procedure handling stock reservation, item insertion, and order total updates.

#### Data flow
Input (order ID, SKU, quantity) flows into `add_order_item`, which reads `product` stock/price, writes to `order_item`, updates `product` stock, and updates `customer_order` total.

#### Dependencies
* Relies on the `product`, `customer_order`, and `order_item` tables for its operations.

#### Cross-cutting
* Uses MySQL schema definitions for all data structures.
* The `add_order_item` procedure enforces transactional integrity by locking rows during stock checks and updates.
