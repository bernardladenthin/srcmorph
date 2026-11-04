### other/persistence
- H: 1.0
- C: 2F8025B8
- D: 2026-11-04T06:47:45Z
- T: 2026-11-04T08:25:06Z
- G: 
- A: 0.0.0
- X: package
- F: [Shipment.hbm.xml](Shipment.hbm.xml.ai.md)
- F: [persistence.xml](persistence.xml.ai.md)
---
> Provides Hibernate/JPA configuration for the shop shipping domain, mapping entities to PostgreSQL tables and defining persistence units for transactional and reporting data access.  

#### Purpose  
- Configure Hibernate mappings for `Shipment` and its subclass `ExpressShipment`.  
- Define JPA persistence units for shop and reporting data sources.  

#### Responsibilities  
- **Entity mapping**: `Shipment.hbm.xml` maps `shop.shipping.Shipment` and `ExpressShipment` to tables, sets relationships, and declares queries.  
- **Persistence unit definition**: `persistence.xml` declares two units (`shop` and `reporting`) with data source, provider, and properties.  

#### Key units  
- **`Shipment` entity** – table `SHIPMENT`, fields `id`, `trackingCode`, `carrier`, `shippedAt`; many‑to‑one `order`, one‑to‑many `parcels`.  
- **`ExpressShipment` subclass** – table `EXPRESS_SHIPMENT`, field `deliveryWindow`.  
- **Named queries** – `Shipment.undelivered` (HQL), `Shipment.countByCarrier` (SQL).  
- **Persistence unit `shop`** – JTA, Hibernate provider, PostgreSQL dialect, batch size 50, mapped classes `Order`, `OrderLine`, `Customer`.  
- **Persistence unit `reporting`** – RESOURCE_LOCAL, excludes unlisted classes, maps `DailyRevenue`.  

#### Data flow  
1. Application obtains an `EntityManager` from the `shop` unit.  
2. `Shipment` instances are persisted via Hibernate, using the sequence `SHIPMENT_SEQ`.  
3. Queries `Shipment.undelivered` and `Shipment.countByCarrier` are executed against the `SHIPMENT` table.  
4. Reporting data is accessed through the `reporting` unit, reading `DailyRevenue` from the reporting data source.  

#### Dependencies  
- Hibernate JPA provider `org.hibernate.jpa.HibernatePersistenceProvider`.  
- PostgreSQL dialect `org.hibernate.dialect.PostgreSQLDialect`.  
- Data sources `java:/jdbc/ShopDS` (JTA) and `java:/jdbc/ReportingDS` (RESOURCE_LOCAL).  
- Mapping file `META-INF/order-queries.xml` referenced by the `shop` unit.  

#### Cross-cutting  
- Sequence generator `SHIPMENT_SEQ` for `Shipment.id`.  
- Cascade `all-delete-orphan` on `Shipment.parcels`.  
- Shared Hibernate properties: dialect, batch size, connection credentials.  
- Named queries defined in the mapping file for reuse across the application.
