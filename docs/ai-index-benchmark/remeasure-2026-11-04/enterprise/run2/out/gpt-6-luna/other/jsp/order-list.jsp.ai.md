### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:14:02Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> Order listing lets staff search orders by customer email or status, review totals and overdue status, and cancel an order.

#### Purpose
- Displays orders with their number, total, overdue indicator, and cancel link.
- Provides a customer-email and status search form.

#### Type
- JSP page; `text/html;charset=UTF-8`.
- `c` = Jakarta Tags core (`jakarta.tags.core`); `fmt` = Jakarta Tags format (`jakarta.tags.fmt`).

#### Data
- Request attribute `orderFilter`; uses `getPageSize()` to set `pageSize`.
- `orders`; iterated as `order`, using `order.number`, `order.total`, `order.overdue`, and `order.id`.
- Request parameter `param.customerEmail`; used as the search input value.

#### Actions
- GET form to `${pageContext.request.contextPath}/orders/search`; fields: `customerEmail` (text), `status` (select: `OPEN`, `SHIPPED`).
- Cancel link to `orders/${order.id}/cancel` for each order.

#### Structure
- Includes `/WEB-INF/jspf/header.jspf` via JSP include directive.
- Includes `/WEB-INF/jspf/footer.jspf` via `jsp:include`.

#### Server code
- Imports `java.util.List`, `shop.Order`, and `shop.OrderFilter`.
- Reads request attribute `orderFilter`; computes `pageSize` as 25 when the filter is absent, otherwise from `filter.getPageSize()`.
- Outputs `pageSize` with a JSP expression.

#### Dependencies
- Tag libraries: `jakarta.tags.core`, `jakarta.tags.fmt`.
- Classes: `java.util.List`, `shop.Order`, `shop.OrderFilter`.
- Included files: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- URL patterns: `/orders/search`, `orders/${order.id}/cancel`.
