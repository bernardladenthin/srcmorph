### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:13:39Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates order shipping charges from parcel weight and waives the fee for orders meeting the free-shipping threshold.

#### Purpose
- `ShippingRulesSpec` verifies free shipping, weight-tier pricing, and rejection of overweight parcels.
- `ShippingRules` selects a shipping price using order total and weight.

#### Type
- `ShippingRulesSpec` — class; extends `Specification`.
- `ShippingRules` — class; annotated `@CompileStatic`; no explicit superclass or interfaces.

#### Input
- `ShippingRulesSpec.rules` initializes `ShippingRules` with carrier `DHL` and maximum parcel weight `31.5`.
- `ShippingRules.costFor(Order order)` reads `order.weightKg` and `order.total`.
- `ShippingRules` properties: `carrier: String`, `maxParcelKg: BigDecimal`.
- `heavyOrder(BigDecimal kg)` is a private helper that creates an `Order`; it is not called in the shown tests.

#### Output
- `costFor(Order)` returns a `BigDecimal` shipping charge: `0`, `4.99`, `9.99`, or `19.99` for matching cases.
- `costFor` throws `ParcelTooHeavyException` when parcel weight exceeds `maxParcelKg`.
- No persistent state or external resources are written.

#### Core logic
- `ShippingRulesSpec` defines `FREE_SHIPPING_THRESHOLD = 50.00`.
- Tests that an order totaling `75.00` with weight `2` has zero shipping cost.
- Unrolled examples verify prices of `4.99` at `1 kg`, `9.99` at `10 kg`, and `19.99` at `30 kg`.
- Verifies a `40 kg` parcel is rejected.
- `ShippingRules.costFor` checks maximum weight first; excess weight throws with actual and maximum weights in the message.
- For acceptable weight, orders at or above the threshold receive free shipping.
- Otherwise, searches ascending tiers (`2 kg`, `10 kg`, `31.5 kg`) and returns the first matching tier’s price.

#### Public API
- `FREE_SHIPPING_THRESHOLD: BigDecimal` — free-shipping cutoff.
- `getRules()`/`setRules(Object)` — access test rules property.
- `"orders above the threshold ship for free"()` -> `Object` — verifies free shipping.
- `"a parcel of #weight kg costs #expected"()` -> `Object` — verifies tier prices.
- `"an overweight parcel is rejected"()` -> `Object` — verifies overweight exception.
- `getCarrier()`/`setCarrier(String)` — access carrier property.
- `getMaxParcelKg()`/`setMaxParcelKg(BigDecimal)` — access maximum parcel weight.
- `costFor(Order order)` -> `BigDecimal` — calculate shipping charge.

#### Dependencies
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `groovy.transform.CompileStatic`
- `Order`
- `ParcelTooHeavyException`

#### Exceptions / Errors
- `ParcelTooHeavyException` is thrown when `order.weightKg > maxParcelKg`.
- If weight passes `maxParcelKg` but exceeds the final `31.5 kg` tier, no tier matches and `.price` is accessed on a null result.

#### Concurrency
- No concurrency or synchronization; shipping calculation has no state mutation.
