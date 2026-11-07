### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:19:47Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> Customers enter a delivery address, choose a shipping method, review the EUR order total, and place an order.

#### Purpose
- Collect checkout address and shipping details, update the displayed total, and submit the order.

#### Type
- Jakarta Faces Facelets page; XHTML.
- Namespaces: `h` = Jakarta Faces HTML, `f` = Jakarta Faces core, `ui` = Jakarta Faces Facelets.

#### Data
- `checkoutBean.address.street` — street input value.
- `checkoutBean.address.zip` — ZIP input value.
- `checkoutBean.shippingMethod` — selected shipping method.
- `checkoutBean.shippingMethods` — options for the shipping menu.
- `checkoutBean.total` — displayed total.
- Iterations: none.

#### Actions
- `checkoutForm` — Faces form; `Place order` command button invokes `checkoutBean.placeOrder`.
- Inputs: `street` (`h:inputText`, required); `zip` (`h:inputText`, regex `[0-9]{5}`).
- `shipping` (`h:selectOneMenu`) — AJAX listener `checkoutBean.recalculate`; renders `total`.
- `total` — number converter formats the value as currency with currency code `EUR`.

#### Structure
- Uses template `/WEB-INF/templates/shop.xhtml`; defines `content`.
- Includes `/WEB-INF/includes/payment-hints.xhtml` in `content`.

#### Dependencies
- Template: `/WEB-INF/templates/shop.xhtml`.
- Included file: `/WEB-INF/includes/payment-hints.xhtml`.
- Bean: `checkoutBean`.
- Actions: `checkoutBean.recalculate`, `checkoutBean.placeOrder`.
