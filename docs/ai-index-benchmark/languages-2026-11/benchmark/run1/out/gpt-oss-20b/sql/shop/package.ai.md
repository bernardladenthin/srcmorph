### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:22:11Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> A MySQL 8 schema and stored procedure for managing a web shop’s product catalog, customer orders, and order line items, with automated stock reservation and order total calculation.  

#### Purpose  
- Persist product, order, and order‑item data for an e‑commerce platform.  
- Provide a single procedure to add items to an order while handling stock and totals atomically.  

#### Responsibilities  
- **Catalog Management** – `product` table stores SKU, name, price, and stock.  
- **Order Lifecycle** – `customer_order` tracks status, timestamps, and total cost.  
- **Line Item Handling** – `order_item` links orders to products with quantity and unit price.  
- **Business Logic** – `add_order_item` procedure enforces stock availability, updates inventory, and recalculates order totals.  

#### Key units  
- `product` (PK: `product_id`; unique SKU; price & stock fields).  
- `customer_order` (PK: `order_id`; status enum; `total_cents`).  
- `order_item` (PK: `(order_id, product_id)`; FK to order and product).  
- `add_order_item(p_order_id, p_sku, p_qty)` – reserves stock, updates or inserts line item, decrements stock, recomputes order total.  

#### Data flow  
1. Caller invokes `add_order_item` with order ID, SKU, and quantity.  
2. Procedure locks the matching `product` row (FOR UPDATE).  
3. If stock is insufficient, error is raised.  
4. `order_item` is inserted or updated; `product.stock` is decremented.  
5. `customer_order.total_cents` is recalculated from all items of the order.  

#### Dependencies  
- Internal: `product`, `customer_order`, `order_item` tables.  
- No external packages or sequences; relies on MySQL 8 transactional guarantees.  

#### Cross-cutting  
- **Referential Integrity** – FK constraints on `order_item` enforce order and product existence.  
- **Concurrency Control** – `SELECT … FOR UPDATE` ensures safe stock updates under concurrent access.  
- **Error Handling** – procedure raises an error when stock is insufficient, preventing partial updates.  
- **Indexing** – `idx_order_customer` on `(customer_id, created_at)` supports order retrieval by customer.
