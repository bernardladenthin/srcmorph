### scheduler-context.xml
- H: 1.0
- C: 76DD20FB
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:36Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Configures weekday morning delivery of overdue reminders through a Spring-wired database repository and SMTP gateway.

#### Purpose
- Wires the reminder database, repository, mail gateway, and overdue reminder job.
- Schedules `sendDueReminders` to run on weekdays at 07:00.

#### Type
- Spring application context; root element `beans`.
- Namespaces: default `http://www.springframework.org/schema/beans`; `xsi` `http://www.w3.org/2001/XMLSchema-instance`; `context` `http://www.springframework.org/schema/context`; `task` `http://www.springframework.org/schema/task`.

#### Definitions
- Beans:
  - `reminderDataSource`: `com.zaxxer.hikari.HikariDataSource`; destroy method `close`.
  - `reminderRepository`: `org.example.reminder.JdbcReminderRepository`; constructor references `reminderDataSource`.
  - `mailGateway`: `org.example.reminder.SmtpMailGateway`.
  - `overdueReminderJob`: `org.example.reminder.OverdueReminderJob`; constructor references `reminderRepository` and `mailGateway`.
- Component scan: `org.example.reminder.web`.
- Scheduler: `reminderScheduler`.
- Scheduled task: `overdueReminderJob.sendDueReminders`, using `reminderScheduler`, cron `0 0 7 * * MON-FRI`.

#### Configuration
- Property placeholder location: `classpath:reminder.properties`.
- `reminderDataSource`: `jdbcUrl=${reminder.db.url}`; `username=${reminder.db.user}`; `maximumPoolSize=8`.
- `mailGateway`: `host=${reminder.smtp.host}`; `port=587`.
- `overdueReminderJob`: `graceDays=14`.
- `reminderScheduler`: `pool-size=2`.
- Scheduled task cron: `0 0 7 * * MON-FRI`.

#### Dependencies
- Properties file: `classpath:reminder.properties`.
- Classes: `com.zaxxer.hikari.HikariDataSource`, `org.example.reminder.JdbcReminderRepository`, `org.example.reminder.SmtpMailGateway`, `org.example.reminder.OverdueReminderJob`.
- Scanned package: `org.example.reminder.web`.
- External systems: database at `${reminder.db.url}`; SMTP host `${reminder.smtp.host}` on port `587`.
