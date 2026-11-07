### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:24:49Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Provides an order-listing interface for searching customer orders, reviewing order totals and overdue status, and initiating cancellation.

#### Purpose
- Display orders with their number, total, overdue indicator, and cancellation link.
- Search orders by customer email and status.

#### Responsibilities
- Render order results from the `orders` request attribute and display the configured page size from `orderFilter`, defaulting to 25.
- Submit a GET search to `/orders/search` with `customerEmail` and `status` (`OPEN` or `SHIPPED`); provide per-order cancellation links.

#### Key units
- `order-list.jsp` — JSP page rendering the search form and order list using Jakarta Tags core and formatting libraries.

#### Data flow
- Reads `orders`, `orderFilter`, and the `customerEmail` request parameter; iterates over orders to show their number, total, overdue state, and ID.
- Sends search criteria to `/orders/search`; cancellation links target `orders/{order.id}/cancel`.

#### Dependencies
- Uses `shop.Order`, `shop.OrderFilter`, and `java.util.List`.
- Uses `jakarta.tags.core` and `jakarta.tags.fmt`; includes `/WEB-INF/jspf/header.jspf` and `/WEB-INF/jspf/footer.jspf`.
