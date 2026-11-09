### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:07:13Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates parcel shipping charges by order value and weight, including free-shipping eligibility and rejection of overweight parcels.

#### Purpose
- `ShippingRulesSpec` verifies free shipping, weight-tier pricing, and overweight rejection.
- `ShippingRules` calculates shipping cost using order totals and parcel weights.

#### Type
- `ShippingRulesSpec` — class; extends `Specification`; defines `FREE_SHIPPING_THRESHOLD` and initializes `rules`.
- `ShippingRules` — class; annotated `@CompileStatic`; has `carrier` and `maxParcelKg` properties.
- `ShippingRules` has no explicitly declared superclass or interfaces beyond Groovy defaults.

#### Input
- `ShippingRules.costFor(Order order)` reads `order.weightKg` and `order.total`.
- `ShippingRules` receives `carrier` and `maxParcelKg` through property assignment; the spec initializes them as `'DHL'` and `31.5`.
- `ShippingRulesSpec` test cases construct `Order` instances with totals and weights; its private helper accepts `BigDecimal kg`.

#### Output
- `costFor` returns `BigDecimal` shipping charges: `0`, `4.99`, `9.99`, or `19.99`.
- `costFor` throws `ParcelTooHeavyException` when the parcel exceeds `maxParcelKg`.
- `ShippingRulesSpec` produces Spock assertions for free shipping, tier prices, and rejection behavior.

#### Core logic
- `costFor` first rejects weights above `maxParcelKg`.
- Orders whose total is at least `ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` receive free shipping.
- Otherwise, it selects the first weight tier whose `upTo` covers the parcel: up to 2 kg costs `4.99`, up to 10 kg costs `9.99`, and up to 31.5 kg costs `19.99`.
- `ShippingRulesSpec` checks a 75.00 order ships free, tests three tier prices with `@Unroll`, and expects `ParcelTooHeavyException` for a 40 kg parcel.
- The private `heavyOrder(BigDecimal kg)` helper creates an order totaling 10.

#### Public API
- `orders above the threshold ship for free()` -> `def` — verifies free shipping.
- `a parcel of #weight kg costs #expected()` -> `def` — verifies weight-tier prices.
- `an overweight parcel is rejected()` -> `def` — verifies overweight rejection.
- `FREE_SHIPPING_THRESHOLD` — `BigDecimal` constant.
- `rules` — inferred property — configured shipping rules.
- `carrier` — `String` property; `getCarrier()`/`setCarrier(String)` -> carrier value.
- `maxParcelKg` — `BigDecimal` property; `getMaxParcelKg()`/`setMaxParcelKg(BigDecimal)` -> parcel weight limit.
- `costFor(Order order)` -> `BigDecimal` — calculates order shipping charge.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order`
- `ParcelTooHeavyException`

#### Exceptions / Errors
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.
- No explicit null handling.

#### Concurrency
- No concurrency or synchronization behavior.
