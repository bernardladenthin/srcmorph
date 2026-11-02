### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:24:14Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Configures the reminder service’s database access, SMTP delivery, and weekday morning overdue-reminder schedule.

#### Purpose
- Wires the reminder scheduler’s data source, repository, mail gateway, and job.
- Loads reminder settings and scans web components.

#### Type
- Spring application context XML; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` → `http://www.w3.org/2001/XMLSchema-instance`; `context` → `http://www.springframework.org/schema/context`; `task` → `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource` — `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
  - `reminderRepository` — `org.example.reminder.JdbcReminderRepository`; constructor reference `reminderDataSource`.
  - `mailGateway` — `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob` — `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository`, `mailGateway`.
- Component scan: `org.example.reminder.web`.
- Scheduler: `reminderScheduler`; scheduled task invokes `overdueReminderJob.sendDueReminders`.

#### Configuration
- Property file: `classpath:reminder.properties`.
- `reminderDataSource`: `jdbcUrl` = `${reminder.db.url}`; `username` = `${reminder.db.user}`; `maximumPoolSize` = `8`.
- `mailGateway`: `host` = `${reminder.smtp.host}`; `port` = `587`.
- `overdueReminderJob`: `graceDays` = `14`.
- `reminderScheduler`: `pool-size` = `2`.
- Scheduled task: `cron` = `0 0 7 * * MON-FRI`.

#### Dependencies
- Referenced classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Referenced file: `classpath:reminder.properties`.
- External systems: JDBC database (`${reminder.db.url}`); SMTP host (`${reminder.smtp.host}`), port `587`.
- Spring schemas: `spring-beans.xsd`, `spring-context.xsd`, `spring-task.xsd`.
