### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:10:19Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages a web shop's inventory, product catalog, and the transactional lifecycle of customer orders and fulfillment.

#### Purpose
* Maintains product availability and stock integrity.
* Orchestrates order creation, line item management, and total price calculations.

#### Responsibilities
* **Inventory Management**: Tracking product details, SKUs, pricing, and real-time stock levels via the `product` table.
* **Order Processing**: Managing the lifecycle of `customer_order` records from creation through various statuses.
* **Transaction Logic**: Handling the atomic addition of items to orders, ensuring stock is decremented and order totals are updated.

#### Key units
* `product`: Stores catalog information including `sku`, `price_cents`, and `stock`.
* `customer_order`: Tracks order metadata such as `customer_id`, `status`, and `total_cents`.
* `order_item`: Represents individual line items linking `product` and `customer_order`.
* `add_order_item`: A stored procedure that validates stock, updates `order_item` quantities, decrements `product.stock`, and recalculates `customer_order.total_cents`.

#### Data flow
* Product availability is verified via `sku` and `stock` levels.
* Upon adding an item, `order_item` records are inserted or updated, `product.stock` is reduced, and the `customer_order.total_cents` is recalculated based on the new sum of items.

#### Dependencies
* Relies on relational integrity between `order_item`, `customer_order`, and `product`.
