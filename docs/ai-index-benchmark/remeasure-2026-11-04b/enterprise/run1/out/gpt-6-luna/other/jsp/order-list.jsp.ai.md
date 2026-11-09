### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:23:12Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> Lists open orders and lets users search by customer email or status, review totals and overdue status, and cancel an order.

#### Purpose
- Display orders with their number, total, overdue indicator, and cancellation link.
- Search orders by customer email and status.

#### Type
- JSP page; `text/html;charset=UTF-8`.
- Tag libraries: `c` = Jakarta Tags core (`jakarta.tags.core`); `fmt` = Jakarta Tags formatting (`jakarta.tags.fmt`).
- JSP standard actions: `jsp:include`.

#### Data
- Request attribute `orderFilter`: cast to `OrderFilter`; reads `getPageSize()` when non-null.
- Request attribute `orders`: iterated with `c:forEach`, variable `order`; reads `order.number`, `order.total`, `order.overdue`, `order.id`.
- Request parameter `customerEmail`: used as the search input value.
- Request context path: `pageContext.request.contextPath`, used in the search form action.

#### Actions
- Search form: GET `${pageContext.request.contextPath}/orders/search`; fields `customerEmail` (text input) and `status` (select: `OPEN`, `SHIPPED`); submit control labeled `Search`.
- Per-order cancellation link: `orders/${order.id}/cancel`.

#### Structure
- Includes `/WEB-INF/jspf/header.jspf` via JSP include directive.
- Includes `/WEB-INF/jspf/footer.jspf` via `jsp:include`.

#### Server code
- Imports `java.util.List`, `shop.Order`, and `shop.OrderFilter`.
- Reads request attribute `orderFilter` as `OrderFilter`; computes `pageSize` as its page size, or `25` if the filter is null; displays the result.

#### Dependencies
- Tag libraries: `jakarta.tags.core`, `jakarta.tags.fmt`.
- Imported classes: `java.util.List`, `shop.Order`, `shop.OrderFilter`.
- Included files: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- Search URL: `${pageContext.request.contextPath}/orders/search`; cancellation URL pattern: `orders/${order.id}/cancel`.
