### persistence.xml
- H: 1.0
- C: 24ACE2B2
- D: 2026-11-04T06:47:44Z
- T: 2026-11-04T08:23:51Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
- N: 13E13638
---
**Facts (exact, whole file):** elements: 17; namespace declarations: 1; id and name attributes: 6; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0; persistence units: 2; JTA data sources: 1; non-JTA data sources: 1; listed classes: 4; entity mappings: 0; embeddables and mapped superclasses: 0; named queries: 0; associations: 0; mapping files: 1; properties: 4

> Configures two Jakarta Persistence units: `shop` for JTA‑managed Hibernate with PostgreSQL, and `reporting` for RESOURCE_LOCAL access to a reporting data source.

#### Purpose
- Define persistence units for JPA entities.
- Configure Hibernate provider and data sources.

#### Type
- Jakarta Persistence XML (`persistence.xml`).
- Root element: `persistence`.
- Namespace: `https://jakarta.ee/xml/ns/persistence`.

#### Definitions
- **Persistence Unit `shop`**
  - `transaction-type="JTA"`.
  - `provider`: `org.hibernate.jpa.HibernatePersistenceProvider`.
  - `jta-data-source`: `java:/jdbc/ShopDS`.
  - `mapping-file`: `META-INF/order-queries.xml`.
  - Entity classes:
    - `shop.Order`
    - `shop.OrderLine`
    - `shop.Customer`
  - Properties:
    - `hibernate.dialect`: `org.hibernate.dialect.PostgreSQLDialect`
    - `hibernate.jdbc.batch_size`: `50`
    - `hibernate.connection.username`: `shop_app`
    - `hibernate.connection.password`: `***`
- **Persistence Unit `reporting`**
  - `transaction-type="RESOURCE_LOCAL"`.
  - `non-jta-data-source`: `java:/jdbc/ReportingDS`.
  - Entity class:
    - `shop.reporting.DailyRevenue`
  - `exclude-unlisted-classes`: `true`

#### Configuration
- `hibernate.dialect`: `org.hibernate.dialect.PostgreSQLDialect`
- `hibernate.jdbc.batch_size`: `50`
- `hibernate.connection.username`: `shop_app`
- `hibernate.connection.password`: `***`

#### Dependencies
- Hibernate JPA provider: `org.hibernate.jpa.HibernatePersistenceProvider`.
- PostgreSQL dialect class: `org.hibernate.dialect.PostgreSQLDialect`.
- Data sources:
  - `java:/jdbc/ShopDS`
  - `java:/jdbc/ReportingDS`
- Mapping file: `META-INF/order-queries.xml`.
