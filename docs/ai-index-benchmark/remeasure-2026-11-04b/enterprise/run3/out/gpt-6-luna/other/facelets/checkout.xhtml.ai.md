### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:37:49Z
- G: 
- A: 0.0.0
- X: file
- P: C9F70393
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> The checkout page collects a delivery address and shipping method, updates the order total, and submits the order.

#### Purpose
- Enter delivery details and select shipping before placing an order.
- Display the total in euros and include payment hints.

#### Type
- Facelets page; XHTML.
- `h` = Jakarta Faces HTML; `f` = Jakarta Faces core; `ui` = Jakarta Faces Facelets.

#### Data
- `checkoutBean.address.street` — delivery street.
- `checkoutBean.address.zip` — delivery ZIP code.
- `checkoutBean.shippingMethod` — selected shipping method.
- `checkoutBean.shippingMethods` — options for the shipping method menu.
- `checkoutBean.total` — order total.

#### Actions
- `checkoutForm` submits through `checkoutBean.placeOrder` via the “Place order” command button.
- Changing `shipping` invokes `checkoutBean.recalculate` via Ajax and rerenders `total`.
- `zip` uses `f:validateRegex` with pattern `[0-9]{5}`.
- `total` uses `f:convertNumber` with currency type and `EUR`.
- Commented-out “Save cart” button references `checkoutBean.saveCart`; it is not active.

#### Structure
- Uses template `/WEB-INF/templates/shop.xhtml`; defines `content`.
- Includes `/WEB-INF/includes/payment-hints.xhtml`.

#### Dependencies
- `checkoutBean`
- `/WEB-INF/templates/shop.xhtml`
- `/WEB-INF/includes/payment-hints.xhtml`
- `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`
