### order-list.jsp
- H: 1.0
- C: 939CAC41
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:17:21Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: F786E780
---
**Facts (exact, whole file):** page directives: 1; taglib directives: 2; includes and forwards: 2; tag file attributes: 0; scriptlets: 1; expressions: 1; declarations: 0; EL expressions: 7; prefixed tags: 5; forms: 1; input fields: 3; request parameters read: 1

> Displays a searchable list of open orders, allowing filtering by customer email and status, and providing a cancel link for each order.

#### Purpose
- Show open orders with total and overdue status.
- Enable search by customer email and status.
- Offer cancel action per order.

#### Type
- JSP page
- Content type: `text/html;charset=UTF-8`
- Tag libraries: `c` = `jakarta.tags.core`, `fmt` = `jakarta.tags.fmt`

#### Data
- Request attribute: `orderFilter` → `OrderFilter filter`
- Request attribute: `orders` (iterated with `c:forEach`, var `order`)
  - `order.number`
  - `order.total`
  - `order.overdue`
  - `order.id`
- Request parameters:
  - `param.customerEmail`
  - `param.status`

#### Actions
- Form: `action="${pageContext.request.contextPath}/orders/search"`, method `get`
  - Input `name="customerEmail"`
  - Select `name="status"` with options `OPEN`, `SHIPPED`
  - Submit button
- Link: `href="orders/${order.id}/cancel"` (per order)

#### Structure
- Includes header fragment: `/WEB-INF/jspf/header.jspf`
- Includes footer fragment: `/WEB-INF/jspf/footer.jspf`

#### Server code
- Scriptlet imports: `java.util.List`, `shop.Order`, `shop.OrderFilter`
- Scriptlet retrieves `OrderFilter` from request, determines `pageSize` (default 25)
- Scriptlet outputs `pageSize` in paragraph

#### Dependencies
- Tag libraries: `jakarta.tags.core`, `jakarta.tags.fmt`
- Included files: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`
- Beans/Classes: `shop.Order`, `shop.OrderFilter`
