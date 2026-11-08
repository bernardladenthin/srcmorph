<!--
SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->
# Enterprise corpus

Seventeen source files of the kinds an enterprise Java code base holds besides Java and SQL, one or
two per kind that profile v3 has a prompt or fact counters for since November 2026. Like the
[language corpus](../corpus-languages/README.md) they are written for this benchmark (Apache-2.0),
around one shop backoffice, and each holds the traps its counters and its prompt have to get past:
a commented-out servlet, route or step, a KDoc and a string that mention `fun`, a password in a
persistence unit that must not reach a summary.

| File | Kind | Rule (run.py) |
|---|---|---|
| [`java/shop/OrderResource.java`](java/shop/OrderResource.java) | a stateless EJB that is a JAX-RS resource, with a timer | `java-ee` |
| [`other/kotlin/InvoiceService.kt`](other/kotlin/InvoiceService.kt) | Kotlin: a value class, data and sealed types, a coroutine, an extension function | `kotlin` |
| [`other/groovy/ShippingRulesSpec.groovy`](other/groovy/ShippingRulesSpec.groovy) | a Spock specification and the Groovy class it tests | `groovy` |
| [`other/jee/web.xml`](other/jee/web.xml) | servlets, a filter, a listener, security constraints, a resource reference | `jee` |
| [`other/jee/ejb-jar.xml`](other/jee/ejb-jar.xml) | session and message-driven beans, an interceptor, transaction attributes | `jee` |
| [`other/persistence/persistence.xml`](other/persistence/persistence.xml) | two persistence units, one with a password | `persistence` |
| [`other/persistence/Shipment.hbm.xml`](other/persistence/Shipment.hbm.xml) | a Hibernate mapping with a subclass, associations and named queries | `persistence` |
| [`other/mybatis/CustomerMapper.xml`](other/mybatis/CustomerMapper.xml) | a MyBatis mapper: statements, dynamic SQL, a `${}` substitution | `mybatis` |
| [`other/integration/order-routes.xml`](other/integration/order-routes.xml) | Apache Camel routes in Spring XML | `integration` |
| [`other/bpmn/order-fulfilment.bpmn`](other/bpmn/order-fulfilment.bpmn) | a Camunda 7 process with a diagram | `bpmn` |
| [`other/wsdl/StockService.wsdl`](other/wsdl/StockService.wsdl) | a SOAP service with an embedded schema | `wsdl` |
| [`other/jsp/order-list.jsp`](other/jsp/order-list.jsp) | a JSP page: directives, a scriptlet, JSTL, a form | `jsp` |
| [`other/facelets/checkout.xhtml`](other/facelets/checkout.xhtml) | a Facelets view bound to a bean | `facelets` |
| [`other/openapi/orders-api.yaml`](other/openapi/orders-api.yaml) | an OpenAPI 3.0 contract | `openapi` |
| [`other/k8s/orders-deployment.yaml`](other/k8s/orders-deployment.yaml) | a Deployment, a Service and an Ingress | `k8s` |
| [`other/actions/.github/workflows/release.yml`](other/actions/.github/workflows/release.yml) | a GitHub Actions release workflow | `actions` |
| [`other/docker/Dockerfile`](other/docker/Dockerfile) | a two-stage Dockerfile | `dockerfile` |

What a faithful summary names is in [`../modelcard/facts-enterprise.json`](../modelcard/facts-enterprise.json);
its `secret` entry is the password `score.py` checks no summary copies (`secretsLeaked`). The
counts the fact files give are pinned on samples of each kind by `JvmFactPatternsTest`,
`EnterpriseXmlFactPatternsTest`, `ViewFactPatternsTest` and `OpsFactPatternsTest` (`srcmorph`
module). To measure a profile on it:

```bash
python run.py --cp <classpath file> --models gpt-oss-20b,gpt-6-luna --work <dir> \
    --corpus ../corpus-enterprise --out <dir> --meta <dir> --facts [--java-prompt file-body-oop]
python score.py --work <dir> --out <dir> --meta <dir> --facts facts-enterprise.json
```
