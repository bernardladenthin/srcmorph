### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:42:47Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> A JSP page that lists open orders, lets users search by customer email or status, and cancel individual orders.

#### Purpose
- Render a searchable table of open orders.
- Provide pagination controls and cancellation links.

#### Responsibilities
- **Order listing** – iterate over `orders` and display number, total, overdue flag, and ID.
- **Search form** – capture `customerEmail` and `status` and submit to `/orders/search`.
- **Cancellation** – link each order to `orders/{id}/cancel`.

#### Key units
- **`order-list.jsp`** – main view rendering orders, search form, and cancel links.
- **`OrderFilter`** – request attribute holding search criteria and page size.
- **`Order`** – bean representing an individual order (fields: `number`, `total`, `overdue`, `id`).

#### Data flow
1. Controller populates `request` attributes `orderFilter` and `orders`.
2. `order-list.jsp` reads `orderFilter` to set page size and pre‑populate search fields.
3. The page iterates over `orders`, rendering each order row.
4. User submits search form → controller refines `orders` list.
5. User clicks cancel link → controller processes cancellation.

#### Dependencies
- **Tag libraries**: `jakarta.tags.core` (`c`), `jakarta.tags.fmt` (`fmt`).
- **Included JSP fragments**: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- **Java classes**: `shop.Order`, `shop.OrderFilter`.

#### Cross-cutting
- Uses EL expressions for data binding (`${orders}`, `${order}`).
- Relies on standard JSP scriptlet imports for bean access.
- Pagination size derived from `OrderFilter.getPageSize()`.
