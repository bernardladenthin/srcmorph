### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:23:14Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Tests shipping cost calculation, free‑shipping threshold, and overweight rejection for DHL parcels.

#### Purpose
- Verify `ShippingRules.costFor` behavior for various weights and totals.
- Ensure free shipping applies above threshold.
- Confirm overweight parcels throw `ParcelTooHeavyException`.

#### Type
- `class ShippingRulesSpec` (extends `Specification`)  
  - Modifiers: none  
  - Imports: `groovy.transform.CompileStatic`, `spock.lang.Specification`, `spock.lang.Unroll`  
- `class ShippingRules` (annotated `@CompileStatic`)  
  - Modifiers: none  
  - Fields: `String carrier`, `BigDecimal maxParcelKg`  
  - Method: `BigDecimal costFor(Order order)`  

#### Input
- `ShippingRulesSpec` constructor: none.  
- `ShippingRulesSpec` fields: `static final BigDecimal FREE_SHIPPING_THRESHOLD = 50.00`, `def rules = new ShippingRules(carrier: 'DHL', maxParcelKg: 31.5)`  
- `ShippingRules.costFor` parameters: `Order order` (properties `total: BigDecimal`, `weightKg: BigDecimal`).  
- `ShippingRulesSpec` test methods create `Order` instances with `total` and `weightKg`.

#### Output
- `ShippingRules.costFor` returns `BigDecimal` cost or `0` for free shipping.  
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.  
- Test methods assert returned cost or exception.

#### Core logic
- In `costFor`:  
  1. If `order.weightKg > maxParcelKg` → throw `ParcelTooHeavyException`.  
  2. If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` → return `0`.  
  3. Define tiers: `[[upTo: 2, price: 4.99], [upTo: 10, price: 9.99], [upTo: 31.5, price: 19.99]]`.  
  4. Find first tier where `order.weightKg <= tier.upTo` and return `tier.price` as `BigDecimal`.

#### Public API
- `ShippingRulesSpec."orders above the threshold ship for free"() -> void` – test free shipping.  
- `ShippingRulesSpec."a parcel of #weight kg costs #expected"() -> void` – parameterized cost test.  
- `ShippingRulesSpec."an overweight parcel is rejected"() -> void` – test overweight exception.  
- `ShippingRulesSpec.heavyOrder(BigDecimal kg) -> Order` – helper.  
- `ShippingRules.costFor(Order order) -> BigDecimal` – shipping cost calculation.

#### Dependencies
- `groovy.transform.CompileStatic`  
- `spock.lang.Specification`  
- `spock.lang.Unroll`  
- `Order` (assumed defined elsewhere)  
- `ParcelTooHeavyException` (assumed defined elsewhere)

#### Exceptions / Errors
- `ParcelTooHeavyException` thrown when parcel weight exceeds `maxParcelKg`.  
- No other explicit exceptions.

#### Concurrency
- No concurrency constructs; all operations are single‑threaded.
