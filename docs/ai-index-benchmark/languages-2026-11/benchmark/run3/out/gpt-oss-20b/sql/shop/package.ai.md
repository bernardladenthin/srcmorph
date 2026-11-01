### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:07:04Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, with automated stock reservation and order total calculation.  

#### Purpose  
- Persist product, order, and order‑item data.  
- Provide a single procedure to add an item to an order while reserving stock and updating the order total.  

#### Responsibilities  
- **Catalog Management** – `product` table holds SKU, name, price, and stock.  
- **Order Lifecycle** – `customer_order` tracks status, timestamps, and total.  
- **Line Item Handling** – `order_item` links orders to products with quantity and unit price.  
- **Business Logic** – `add_order_item` procedure enforces stock checks, updates inventory, and recalculates totals.  

#### Key units  
- `product` (PK `product_id`, unique `sku`, `price_cents`, `stock`).  
- `customer_order` (PK `order_id`, `customer_id`, `status`, `total_cents`).  
- `order_item` (PK `(order_id, product_id)`, `quantity`, `unit_price_cents`).  
- `add_order_item(p_order_id, p_sku, p_qty)` – reserves stock, inserts/updates line item, updates order total.  

#### Data flow  
1. Caller invokes `add_order_item` with order ID, SKU, and quantity.  
2. Procedure locks the matching `product` row (`FOR UPDATE`).  
3. If stock insufficient, error is raised.  
4. `order_item` is inserted or its quantity updated.  
5. `product.stock` is decremented by the quantity.  
6. `customer_order.total_cents` is recomputed from all items of the order.  

#### Dependencies  
- Internal: `product`, `customer_order`, `order_item` tables.  
- External: MySQL 8 transaction and locking mechanisms.  

#### Cross-cutting  
- **Concurrency** – `SELECT … FOR UPDATE` ensures atomic stock reservation.  
- **Integrity** – Foreign keys `fk_item_order` and `fk_item_product` enforce referential consistency.  
- **Error handling** – Procedure raises an error when the product is not found or stock is insufficient.  
- **Performance** – Index `idx_order_customer` on `(customer_id, created_at)` supports order queries by customer.
