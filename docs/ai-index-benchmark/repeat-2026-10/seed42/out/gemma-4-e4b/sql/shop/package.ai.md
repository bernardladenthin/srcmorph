### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:54:06Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> This package manages the persistence layer for e-commerce transactions, handling product inventory, customer order creation, and atomic stock reservation.

#### Purpose
* Defines the relational schema for products, customer orders, and their line items.
* Provides transactional logic via stored procedures to manage order creation and inventory deduction.

#### Responsibilities
* Schema definition for core entities (product, customer_order, order_item).
* Implementing the `add_order_item` stored procedure for atomic order processing.

#### Key units
* product: Stores product details including SKU and current stock.
* customer_order: Tracks the state and total value of a customer's order.
* order_item: Links products to orders, detailing quantity and price at time of order.
* add_order_item: Stored procedure responsible for reserving stock and updating order totals.

#### Data flow
Input (order ID, SKU, quantity) $\rightarrow$ `add_order_item` procedure $\rightarrow$ Reads `product` (to check stock/price) $\rightarrow$ Writes/Updates `order_item` and `product` (stock) $\rightarrow$ Updates `customer_order` (total\_cents).

#### Dependencies
* Depends on the `product`, `customer_order`, and `order_item` tables for all operations.

#### Cross-cutting
* Uses MySQL schema definitions for persistence.
* The `add_order_item` routine enforces transactional integrity by locking rows during stock checks and updates.
