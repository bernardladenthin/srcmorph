### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:32:48Z
- G: 
- A: 0.0.0
- X: file
---
> Configures a Spring reminder scheduler with data source, repository, mail gateway, job, and cron trigger.

#### Purpose
- Wire reminder scheduler components.
- Define data source, repository, mail gateway, job, and scheduled task.

#### Type
- Spring XML configuration (`<beans>` root).
- Namespaces:
  - `xmlns="http://www.springframework.org/schema/beans"`
  - `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
  - `xmlns:context="http://www.springframework.org/schema/context"`
  - `xmlns:task="http://www.springframework.org/schema/task"`

#### Definitions
- **Property placeholder**: `location="classpath:reminder.properties"`
- **Component scan**: `base-package="org.example.reminder.web"`
- **Beans**:
  - `id="reminderDataSource"` – class `com.zaxxer.hikari.HikariDataSource`
  - `id="reminderRepository"` – class `org.example.reminder.JdbcReminderRepository`
  - `id="mailGateway"` – class `org.example.reminder.SmtpMailGateway`
  - `id="overdueReminderJob"` – class `org.example.reminder.OverdueReminderJob`
- **Scheduler**: `id="reminderScheduler"` – pool-size `2`
- **Scheduled task**: `ref="overdueReminderJob" method="sendDueReminders" cron="0 0 7 * * MON-FRI"`

#### Configuration
- `reminderDataSource` properties:
  - `jdbcUrl` → `${reminder.db.url}`
  - `username` → `${reminder.db.user}`
  - `maximumPoolSize` → `8`
- `mailGateway` properties:
  - `host` → `${reminder.smtp.host}`
  - `port` → `587`
- `overdueReminderJob` property:
  - `graceDays` → `14`

#### Dependencies
- External classes:
  - `com.zaxxer.hikari.HikariDataSource`
  - `org.example.reminder.JdbcReminderRepository`
  - `org.example.reminder.SmtpMailGateway`
  - `org.example.reminder.OverdueReminderJob`
- External properties file: `classpath:reminder.properties`
- Spring schemas:
  - `spring-beans.xsd`
  - `spring-context.xsd`
  - `spring-task.xsd`
