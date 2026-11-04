### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:12:01Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Provides a web interface for viewing, searching, and canceling open orders in the shop system.

#### Purpose
- Display a paginated list of open orders.
- Allow filtering by customer email and order status.
- Offer a cancel link for each order.

#### Responsibilities
- **Order listing**: iterate over `List<Order>` and render order details.
- **Search/filter**: capture `customerEmail` and `status` from query parameters and submit via GET.
- **Pagination**: use `OrderFilter.pageSize` (default 25) to control list length.
- **Order cancellation**: generate a cancel URL per order.

#### Key units
- **`order-list.jsp`** – JSP page rendering the order table, search form, and cancel links.
- **`OrderFilter`** – request attribute holding pagination settings (e.g., `pageSize`).
- **`Order`** – domain model providing `number`, `total`, `overdue`, `id`.

#### Data flow
1. Client requests `/orders/list` → `order-list.jsp` rendered.
2. JSP reads `OrderFilter` and `orders` from request attributes.
3. Search form submits to `/orders/search` with `customerEmail` and `status`.
4. Each order row includes a link to `/orders/{id}/cancel`.

#### Dependencies
- **Java classes**: `java.util.List`, `shop.Order`, `shop.OrderFilter`.
- **Tag libraries**: `jakarta.tags.core` (`c`), `jakarta.tags.fmt` (`fmt`).
- **JSP fragments**: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.

#### Cross-cutting
- Uses EL expressions for dynamic content (`${}`) and JSTL core tags for iteration and conditionals.
- Pagination logic is driven by a request-scoped `OrderFilter` object.
- Standard HTML form with GET method for search parameters.
