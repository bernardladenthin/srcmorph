### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:31:18Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Provides staff with a searchable view of customer orders and links to cancel individual orders.

#### Purpose
- Display order numbers, totals, and overdue status for staff review.
- Search orders by customer email and status, and expose order cancellation links.

#### Responsibilities
- Render the order list and search form using Jakarta Tags core and formatting tags.
- Set page size from the `orderFilter` request attribute, defaulting to 25 when absent.

#### Key units
- `order-list.jsp` — JSP page that renders orders and provides search and cancellation actions.
- `shop.Order` — order data referenced by the page.
- `shop.OrderFilter` — filter data used to determine page size.

#### Data flow
- Reads `orderFilter` from the request and computes `pageSize`; reads `customerEmail` from request parameters.
- Iterates over `${orders}` to display each order’s `number`, `total`, and `overdue` values.
- Submits search criteria by GET to `/orders/search`; links each order to `orders/${order.id}/cancel`.

#### Dependencies
- Includes `/WEB-INF/jspf/header.jspf` and `/WEB-INF/jspf/footer.jspf`.
- Uses `shop.Order`, `shop.OrderFilter`, Jakarta Tags core, and Jakarta Tags formatting.
