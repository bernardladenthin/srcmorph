### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:24:45Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> A JSP page that lists open orders, supports filtering by customer email and status, and lets users cancel individual orders.

#### Purpose
- Render a searchable table of open orders.
- Provide pagination via `OrderFilter`.
- Offer per‑order cancellation links.

#### Responsibilities
- **Display**: Iterate over `List<Order>` and show order number, total, overdue flag, and cancel link.
- **Search**: Render a form that submits to `/orders/search` with `customerEmail` and `status` parameters.
- **Pagination**: Use `OrderFilter` from the request to determine page size (default 25).

#### Key units
- **JSP page**: `order-list.jsp` – main view rendering orders.
- **Tag libraries**: `c` (`jakarta.tags.core`), `fmt` (`jakarta.tags.fmt`).
- **Included fragments**: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.

#### Data flow
1. Controller populates request attributes `orderFilter` (optional) and `orders` (list of `Order`).
2. JSP reads `orderFilter` to set `pageSize`; iterates over `orders` to display each order.
3. User submits search form → controller filters orders and returns updated list.

#### Dependencies
- **Java classes**: `shop.Order`, `shop.OrderFilter`, `java.util.List`.
- **Tag libraries**: `jakarta.tags.core`, `jakarta.tags.fmt`.
- **JSP fragments**: header and footer JSP files.

#### Cross-cutting
- Uses EL expressions for order properties (`${order.number}`, `${order.total}`, `${order.overdue}`, `${order.id}`).
- Standard JSP scriptlet for retrieving `OrderFilter` and computing `pageSize`.
- Form uses GET method for idempotent search.
