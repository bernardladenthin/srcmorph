### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:15:42Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Staff can search and review customer orders, including totals and overdue status, and initiate order cancellation.

#### Purpose
- Presents the order list and a customer-email/status search form.
- Shows each order’s number, total, overdue indicator, and cancellation link.

#### Responsibilities
- **Order search and display:** Submits search criteria by GET and renders the supplied orders.
- **Cancellation access:** Provides a per-order link to the cancellation URL.
- **Page layout:** Includes shared header and footer fragments.

#### Key units
- `order-list.jsp` — renders the order search form and order list; defaults `pageSize` to 25 when `orderFilter` is absent.

#### Data flow
- Reads `orderFilter` and `orders` from request attributes; derives `pageSize` from the filter when present.
- Uses the `customerEmail` request parameter as the search input value.
- Submits `customerEmail` and `status` (`OPEN` or `SHIPPED`) to `/orders/search`; builds cancellation links from each order’s ID.

#### Dependencies
- Jakarta Tags core (`c`) and format (`fmt`) tag libraries.
- `shop.Order`, `shop.OrderFilter`, and `java.util.List`.
- `/WEB-INF/jspf/header.jspf` and `/WEB-INF/jspf/footer.jspf`.
- URL patterns `/orders/search` and `orders/${order.id}/cancel`.
