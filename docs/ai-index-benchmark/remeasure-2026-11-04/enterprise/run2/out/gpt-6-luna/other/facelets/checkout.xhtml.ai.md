### checkout.xhtml
- H: 1.0
- C: A3D4AA3E
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:13:33Z
- G: 
- A: 0.0.0
- X: file
- P: F6DC35F6
- N: 2F819391
---
**Facts (exact, whole file):** elements: 14; namespace declarations: 4; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; EL expressions: 7; forms: 1; input fields: 4; EL method bindings: 2; templates and includes: 2; composite component interfaces: 0

> Customers enter a delivery address, choose shipping, review the EUR total, and place an order.

#### Purpose
- Collect checkout address and shipping details, update the displayed total, and submit the order.

#### Type
- Jakarta Faces Facelets composition; XHTML.
- `h` = Jakarta Faces HTML, `f` = Jakarta Faces core, `ui` = Jakarta Faces Facelets.

#### Data
- `checkoutBean.address.street` — delivery street.
- `checkoutBean.address.zip` — postal code.
- `checkoutBean.shippingMethod` — selected shipping method.
- `checkoutBean.shippingMethods` — options supplied to the shipping selector.
- `checkoutBean.total` — displayed order total.

#### Actions
- `checkoutForm` submits via `h:commandButton` to `checkoutBean.placeOrder`.
- Shipping selector triggers `checkoutBean.recalculate` via `f:ajax`, rendering `total`.
- `zip` uses `f:validateRegex` with pattern `[0-9]{5}`.
- `total` uses `f:convertNumber` with `type="currency"` and `currencyCode="EUR"`.

#### Structure
- Uses template `/WEB-INF/templates/shop.xhtml`; defines `content`.
- Includes `/WEB-INF/includes/payment-hints.xhtml` in `content`.

#### Dependencies
- Template: `/WEB-INF/templates/shop.xhtml`
- Include: `/WEB-INF/includes/payment-hints.xhtml`
- Bean: `checkoutBean`
- Methods: `checkoutBean.recalculate`, `checkoutBean.placeOrder`
