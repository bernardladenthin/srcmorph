### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:30Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Stores shop products and customer orders, reserving inventory and recalculating order totals when items are added.

#### Purpose
- Defines the MySQL schema for products, customer orders, and order lines.
- Provides `add_order_item` to add or increment an order line while reducing stock and updating the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock; `uk_product_sku` enforces unique SKUs.
- **Orders and line items:** `customer_order` tracks customer, status, creation time, and total; `order_item` stores product quantities and unit prices.
- **Order-item placement:** `add_order_item` looks up and locks an eligible product, writes or increments its order line, deducts stock, and recalculates the order total.

#### Key units
- `product` — catalog record and inventory level keyed by `product_id`.
- `customer_order` — customer order with `NEW`, `PAID`, `SHIPPED`, or `CANCELLED` status.
- `order_item` — order/product line keyed by `(order_id, product_id)`, with quantity and unit price.
- `add_order_item` — procedure accepting `p_order_id`, `p_sku`, and `p_qty` to place an item on an order.

#### Data flow
- `add_order_item` matches `p_sku` to `product.sku` and locks the row when stock is at least `p_qty`.
- If no eligible product is found, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- The procedure inserts an `order_item` at the selected product price, or increments the existing line quantity; it then subtracts `p_qty` from stock and recomputes `customer_order.total_cents` from its lines.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade delete; `order_item.product_id` references `product.product_id`.
- The procedure reads `product` and `order_item`, and writes `order_item`, `product`, and `customer_order`.

#### Cross-cutting
- Monetary values are stored in cents; order totals derive from line quantity multiplied by unit price.
- Product selection uses `FOR UPDATE`; order lines use a composite primary key to identify duplicate product entries per order.
