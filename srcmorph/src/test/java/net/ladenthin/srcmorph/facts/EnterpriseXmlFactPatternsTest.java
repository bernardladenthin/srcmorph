// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the fact counters of the profile for the XML of enterprise Java and integration
 * ({@code profiles/v3/jee-facts.tsv}, {@code persistence-facts.tsv}, {@code mybatis-facts.tsv},
 * {@code integration-facts.tsv}, {@code bpmn-facts.tsv}, {@code wsdl-facts.tsv}), counted the way the
 * facts line counts them. All but the MyBatis file include {@code xml-facts}, so its {@code [ignore]}
 * line blanks comments and CDATA first; each sample has a commented-out element that must not count,
 * and the elements whose names begin like a counted one ({@code <servlet-name>}, {@code <flow-ref>}).
 */
public class EnterpriseXmlFactPatternsTest {

    private static final List<FactCounter> XML = ProfileFactPatterns.counters("xml-facts");

    private static final List<FactCounter> JEE = ProfileFactPatterns.counters("jee-facts");

    private static final List<FactCounter> PERSISTENCE = ProfileFactPatterns.counters("persistence-facts");

    private static final List<FactCounter> MYBATIS = ProfileFactPatterns.counters("mybatis-facts");

    private static final List<FactCounter> INTEGRATION = ProfileFactPatterns.counters("integration-facts");

    private static final List<FactCounter> BPMN = ProfileFactPatterns.counters("bpmn-facts");

    private static final List<FactCounter> WSDL = ProfileFactPatterns.counters("wsdl-facts");

    private static int count(final List<FactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    private static List<String> labels(final List<FactCounter> counters) {
        return new ArrayList<>(ProfileFactPatterns.counts(counters, "").keySet());
    }

    // <editor-fold defaultstate="collapsed" desc="xml-facts include">
    @Test
    public void xmlFactsInclude_everyFileButMyBatis_startsWithTheXmlCounters() {
        // arrange
        final List<String> xml = labels(XML);

        // act, assert
        for (final List<FactCounter> counters : List.of(JEE, PERSISTENCE, INTEGRATION, BPMN, WSDL)) {
            assertThat(labels(counters).subList(0, xml.size()), is(xml));
        }
        assertThat(labels(MYBATIS).contains("elements"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="jee-facts">
    private static final String WEB_XML = "<?xml version=\"1.0\"?>\n"
            + "<web-app xmlns=\"https://jakarta.ee/xml/ns/jakartaee\">\n"
            + "<!-- <servlet><servlet-name>Old</servlet-name></servlet> -->\n"
            + "<context-param><param-name>a</param-name></context-param>\n"
            + "<servlet><servlet-name>Front</servlet-name><servlet-class>x.Front</servlet-class></servlet>\n"
            + "<servlet-mapping><servlet-name>Front</servlet-name><url-pattern>/app/*</url-pattern>"
            + "<url-pattern>*.do</url-pattern></servlet-mapping>\n"
            + "<filter><filter-name>F</filter-name></filter><filter-mapping><filter-name>F</filter-name>"
            + "<url-pattern>/*</url-pattern></filter-mapping>\n"
            + "<listener><listener-class>x.L</listener-class></listener>\n"
            + "<session-config><session-timeout>30</session-timeout></session-config>\n"
            + "<security-constraint><web-resource-collection><url-pattern>/admin/*</url-pattern>"
            + "</web-resource-collection></security-constraint>\n"
            + "<security-role><role-name>admin</role-name></security-role>\n"
            + "<ejb-local-ref><ejb-ref-name>ejb/O</ejb-ref-name></ejb-local-ref>"
            + "<resource-ref><res-ref-name>jdbc/DS</res-ref-name></resource-ref>\n"
            + "<env-entry><env-entry-name>max</env-entry-name></env-entry><persistence-context-ref/>\n"
            + "</web-app>\n";

    private static final String EJB_JAR = "<ejb-jar><enterprise-beans>\n"
            + "<session><ejb-name>OrderService</ejb-name><session-type>Stateless</session-type>"
            + "<timer><schedule/></timer></session>\n"
            + "<session>\n <ejb-name>Cart</ejb-name><session-type> Stateful </session-type></session>\n"
            + "<message-driven><ejb-name>OrderListener</ejb-name></message-driven>\n"
            + "<entity><ejb-name>Customer</ejb-name></entity>\n"
            + "</enterprise-beans><interceptors><interceptor><interceptor-class>x.I</interceptor-class>"
            + "</interceptor></interceptors>\n"
            + "<assembly-descriptor><container-transaction/><method-permission><role-name>r</role-name>"
            + "</method-permission><interceptor-binding/></assembly-descriptor></ejb-jar>\n";

    @Test
    public void jee_webXml_countsEveryComponentOnceAndNotItsChildren() {
        // act, assert
        assertThat(count(JEE, "servlets", WEB_XML), is(1));
        assertThat(count(JEE, "servlet mappings", WEB_XML), is(1));
        assertThat(count(JEE, "URL patterns", WEB_XML), is(4));
        assertThat(count(JEE, "filters", WEB_XML), is(1));
        assertThat(count(JEE, "filter mappings", WEB_XML), is(1));
        assertThat(count(JEE, "listeners", WEB_XML), is(1));
        assertThat(count(JEE, "context parameters", WEB_XML), is(1));
        assertThat(count(JEE, "session beans", WEB_XML), is(0));
        assertThat(count(JEE, "security roles", WEB_XML), is(1));
        assertThat(count(JEE, "security constraints", WEB_XML), is(1));
        assertThat(count(JEE, "EJB references", WEB_XML), is(1));
        assertThat(count(JEE, "resource references", WEB_XML), is(1));
        assertThat(count(JEE, "persistence references", WEB_XML), is(1));
        assertThat(count(JEE, "environment entries", WEB_XML), is(1));
    }

    @Test
    public void jee_ejbJar_countsBeansByKind() {
        // act, assert
        assertThat(count(JEE, "session beans", EJB_JAR), is(2));
        assertThat(count(JEE, "stateless session beans", EJB_JAR), is(1));
        assertThat(count(JEE, "message-driven beans", EJB_JAR), is(1));
        assertThat(count(JEE, "entity beans (EJB 2.x)", EJB_JAR), is(1));
        assertThat(count(JEE, "interceptors", EJB_JAR), is(1));
        assertThat(count(JEE, "timers", EJB_JAR), is(1));
        assertThat(count(JEE, "container transactions", EJB_JAR), is(1));
        assertThat(count(JEE, "method permissions", EJB_JAR), is(1));
    }

    @Test
    public void jee_applicationXml_countsItsModules() {
        // act, assert
        assertThat(
                count(
                        JEE,
                        "EAR modules",
                        "<application><module><ejb>a.jar</ejb></module><module><web><web-uri>w.war</web-uri>"
                                + "</web></module><!-- <module/> --></application>\n"),
                is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="persistence-facts">
    private static final String PERSISTENCE_XML =
            "<persistence><persistence-unit name=\"shop\" transaction-type=\"JTA\">"
                    + "<jta-data-source>java:/ShopDS</jta-data-source><mapping-file>META-INF/orm.xml</mapping-file>"
                    + "<class>x.Order</class><class>x.Line</class><properties>"
                    + "<property name=\"hibernate.dialect\" value=\"x\"/></properties></persistence-unit></persistence>\n";

    private static final String ORM_XML = "<entity-mappings><persistence-unit-metadata/>"
            + "<named-query name=\"Order.all\"><query>SELECT o FROM Order o</query></named-query>"
            + "<entity class=\"x.Order\"><table name=\"ORDERS\"/><attributes><one-to-many name=\"lines\"/>"
            + "</attributes></entity><embeddable class=\"x.Addr\"/></entity-mappings>\n";

    private static final String HBM_XML = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" "
            + "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n"
            + "<hibernate-mapping package=\"x\"><class name=\"Order\" table=\"ORDERS\"><id name=\"id\"/>"
            + "<property name=\"total\"/><many-to-one name=\"customer\"/><set name=\"lines\"><key column=\"o\"/>"
            + "<one-to-many class=\"Line\"/></set><joined-subclass name=\"Big\"/></class>\n"
            + "<query name=\"q1\"><![CDATA[from Order o where o.total < 5 <query x>]]></query>"
            + "<sql-query name=\"q2\"><![CDATA[select 1]]></sql-query></hibernate-mapping>\n";

    @Test
    public void persistence_persistenceXml_countsTheUnitAndItsClasses() {
        // act, assert
        assertThat(count(PERSISTENCE, "persistence units", PERSISTENCE_XML), is(1));
        assertThat(count(PERSISTENCE, "JTA data sources", PERSISTENCE_XML), is(1));
        assertThat(count(PERSISTENCE, "non-JTA data sources", PERSISTENCE_XML), is(0));
        assertThat(count(PERSISTENCE, "listed classes", PERSISTENCE_XML), is(2));
        assertThat(count(PERSISTENCE, "entity mappings", PERSISTENCE_XML), is(0));
        assertThat(count(PERSISTENCE, "mapping files", PERSISTENCE_XML), is(1));
        assertThat(count(PERSISTENCE, "properties", PERSISTENCE_XML), is(1));
    }

    @Test
    public void persistence_ormXml_countsEntitiesQueriesAndAssociations() {
        // act, assert
        assertThat(count(PERSISTENCE, "persistence units", ORM_XML), is(0));
        assertThat(count(PERSISTENCE, "named queries", ORM_XML), is(1));
        assertThat(count(PERSISTENCE, "entity mappings", ORM_XML), is(1));
        assertThat(count(PERSISTENCE, "embeddables and mapped superclasses", ORM_XML), is(1));
        assertThat(count(PERSISTENCE, "associations", ORM_XML), is(1));
    }

    @Test
    public void persistence_hbmXml_countsMappingsAndNotTheQueryTextInCdata() {
        // act, assert
        assertThat(count(PERSISTENCE, "entity mappings", HBM_XML), is(2));
        assertThat(count(PERSISTENCE, "named queries", HBM_XML), is(2));
        assertThat(count(PERSISTENCE, "associations", HBM_XML), is(2));
        assertThat(count(PERSISTENCE, "properties", HBM_XML), is(1));
        assertThat(count(PERSISTENCE, "mapping files", HBM_XML), is(0));
        assertThat(count(PERSISTENCE, "listed classes", HBM_XML), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="mybatis-facts">
    private static final String MAPPER = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" "
            + "\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n"
            + "<mapper namespace=\"x.OrderMapper\">\n"
            + "<!-- <select id=\"old\">select ${bad}</select> -->\n"
            + "<resultMap id=\"r\" type=\"x.Order\"/><sql id=\"cols\">id, total</sql>\n"
            + "<select id=\"find\" resultMap=\"r\">select <include refid=\"cols\"/> from orders <where>"
            + "<if test=\"id != null\">id = #{id}</if><![CDATA[ and total < #{max} ]]></where> order by ${sort}"
            + "</select>\n"
            + "<insert id=\"ins\">insert into orders values (#{id,jdbcType=INTEGER})</insert>\n"
            + "<update id=\"up\">update orders <set>total=#{t}</set></update></mapper>\n";

    @Test
    public void mybatis_mapper_countsStatementsAndReadsTheSqlInCdata() {
        // act, assert
        assertThat(count(MYBATIS, "mapper namespaces", MAPPER), is(1));
        assertThat(count(MYBATIS, "select statements", MAPPER), is(1));
        assertThat(count(MYBATIS, "insert, update and delete statements", MAPPER), is(2));
        assertThat(count(MYBATIS, "result maps", MAPPER), is(1));
        assertThat(count(MYBATIS, "SQL fragments", MAPPER), is(1));
        assertThat(count(MYBATIS, "fragment includes", MAPPER), is(1));
        assertThat(count(MYBATIS, "dynamic SQL tags", MAPPER), is(3));
        // #{id}, #{max} in CDATA, #{id,jdbcType=INTEGER}, #{t}
        assertThat(count(MYBATIS, "parameters #{}", MAPPER), is(4));
        // ${sort}; not the commented-out ${bad}
        assertThat(count(MYBATIS, "substitutions ${}", MAPPER), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="integration-facts">
    private static final String CAMEL = "<beans xmlns=\"http://www.springframework.org/schema/beans\">\n"
            + "<!-- <route id=\"old\"><from uri=\"direct:old\"/></route> -->\n"
            + "<bean id=\"orderProcessor\" class=\"shop.OrderProcessor\"/>\n"
            + "<camelContext xmlns=\"http://camel.apache.org/schema/spring\">\n"
            + "<onException><exception>java.io.IOException</exception>"
            + "<redeliveryPolicy maximumRedeliveries=\"3\"/></onException>\n"
            + "<route id=\"orders\">\n"
            + "<from uri=\"jms:queue:{{orders.queue}}\"/>\n"
            + "<choice><when><simple>${body.total} &gt; 100</simple><to uri=\"direct:big\"/></when>"
            + "<otherwise><toD uri=\"log:${header.id}\"/></otherwise></choice>\n"
            + "<wireTap uri=\"seda:audit\"/>\n"
            + "</route>\n"
            + "<rest path=\"/orders\"><get><to uri=\"direct:list\"/></get></rest>\n"
            + "</camelContext>\n"
            + "</beans>\n";

    private static final String MULE = "<mule xmlns=\"http://www.mulesoft.org/schema/mule/core\">\n"
            + "<flow name=\"orderFlow\"><http:listener path=\"/orders\"/><flow-ref name=\"validate\"/>"
            + "<on-error-continue/></flow>\n"
            + "<sub-flow name=\"validate\"><logger/></sub-flow>\n"
            + "</mule>\n";

    @Test
    public void integration_camelRoute_countsRoutesEndpointsAndPlaceholders() {
        // act, assert
        assertThat(count(INTEGRATION, "Camel routes and route templates", CAMEL), is(1));
        assertThat(count(INTEGRATION, "Camel REST services", CAMEL), is(1));
        assertThat(count(INTEGRATION, "Camel consumer endpoints (from)", CAMEL), is(1));
        assertThat(count(INTEGRATION, "Camel producer endpoints", CAMEL), is(4));
        assertThat(count(INTEGRATION, "error handlers", CAMEL), is(1));
        // the one property placeholder of the route, orders.queue; the two Simple expressions are none
        assertThat(count(INTEGRATION, "property placeholders", CAMEL), is(1));
        assertThat(count(INTEGRATION, "Spring beans", CAMEL), is(1));
    }

    @Test
    public void integration_simpleExpressionsAndPropertyPlaceholders_countsOnlyThePlaceholders() {
        // arrange
        final String simple = "<log message=\"${body} ${bodyAs(String)} ${headers} ${in.header.x} ${out.body}\"/>\n"
                + "<setHeader name=\"a\"><simple>${exchangeProperty.k} ${exchangeId} ${exception.message}"
                + " ${date:now:yyyyMMdd} ${routeId} ${camelContext.name} ${env.HOME} ${env:JAVA_HOME}"
                + " ${sysenv.PATH} ${bean:svc.run} ${ref:x} ${type:shop.Kind.A} ${file:name.noext}"
                + " ${originalBody} ${prettyBody} ${bodyType}</simple></setHeader>\n";
        final String placeholders = "<bean id=\"ds\"><property name=\"url\" value=\"${db.url}\"/>"
                + "<property name=\"pool\" value=\"${db.pool:10}\"/><property name=\"i\" value=\"${index}\"/>"
                + "<property name=\"h\" value=\"${header-size}\"/></bean>\n"
                + "<from uri=\"timer:t?period={{period:1000}}\"/>\n";

        // act, assert
        assertThat(count(INTEGRATION, "property placeholders", simple), is(0));
        // the five placeholders: db.url, db.pool with its default, index, header-size and period
        assertThat(count(INTEGRATION, "property placeholders", placeholders), is(5));
    }

    @Test
    public void integration_simpleRootsThatArePropertyNamesToo_countAsPlaceholders() {
        // arrange
        // Simple's ${id} and ${sys.user} have the shape of a property name, which Mule and Spring use too
        final String ambiguous = "<log message=\"${id} ${sys.user} ${exchange} ${hostname} ${random.int}\"/>\n";

        // act, assert
        assertThat(count(INTEGRATION, "property placeholders", ambiguous), is(5));
    }

    @Test
    public void integration_mulePropertyNames_countAsPlaceholders() {
        // arrange
        final String mule = "<configuration-properties file=\"config-${env}.yaml\"/>\n"
                + "<http:listener-config name=\"l\"><http:listener-connection port=\"${http.port}\"/>"
                + "</http:listener-config>\n"
                + "<file:config name=\"f\"><file:connection workingDir=\"${file.workingDir}\"/></file:config>\n";

        // act, assert
        assertThat(count(INTEGRATION, "property placeholders", mule), is(3));
    }

    @Test
    public void integration_springIntegrationPropertyNames_countAsPlaceholders() {
        // arrange
        final String springIntegration = "<int-file:inbound-channel-adapter directory=\"${file.input.dir}\"/>\n"
                + "<int-file:outbound-channel-adapter directory=\"${out.dir}\"/>\n"
                + "<int:service-activator ref=\"${type.handler}\" method=\"${env.method:handle}\"/>\n";

        // act, assert
        assertThat(count(INTEGRATION, "property placeholders", springIntegration), is(4));
    }

    @Test
    public void integration_muleSpringIntegrationAndSynapse_countsFlowsChannelsAndProxies() {
        // act, assert
        assertThat(count(INTEGRATION, "Mule flows and sub-flows", MULE), is(2));
        assertThat(count(INTEGRATION, "Mule flow references", MULE), is(1));
        assertThat(count(INTEGRATION, "error handlers", MULE), is(1));

        // arrange
        final String springIntegration = "<int:channel id=\"in\"/><int:publish-subscribe-channel id=\"out\"/>"
                + "<int-jms:inbound-channel-adapter id=\"a\"/><int:gateway id=\"g\"/>\n";

        // act, assert
        assertThat(count(INTEGRATION, "Spring Integration channels", springIntegration), is(2));
        assertThat(count(INTEGRATION, "channel adapters and gateways", springIntegration), is(2));

        // arrange
        final String synapse = "<proxy name=\"OrderProxy\"><target><endpoint key=\"ep\"/>"
                + "<endpoint name=\"direct\"><address uri=\"x\"/></endpoint></target><faultSequence/></proxy>"
                + "<sequence name=\"main\"/>\n";

        // act, assert
        assertThat(count(INTEGRATION, "Synapse proxies, APIs, sequences and endpoints (named)", synapse), is(3));
        assertThat(count(INTEGRATION, "error handlers", synapse), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="bpmn-facts">
    private static final String DIAGRAM = "<bpmndi:BPMNDiagram id=\"d\"><bpmndi:BPMNPlane>"
            + "<bpmndi:BPMNShape bpmnElement=\"check\"><dc:Bounds x=\"1\"/></bpmndi:BPMNShape>"
            + "<bpmndi:BPMNEdge bpmnElement=\"f1\"/></bpmndi:BPMNPlane></bpmndi:BPMNDiagram>\n";

    private static final String PROCESS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" "
            + "xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\">\n"
            + "<bpmn:message id=\"m1\" name=\"OrderPlaced\"/>\n"
            + "<bpmn:process id=\"order\" name=\"Order\" isExecutable=\"true\">\n"
            + "<bpmn:startEvent id=\"start\"><bpmn:messageEventDefinition messageRef=\"m1\"/></bpmn:startEvent>\n"
            + "<bpmn:sequenceFlow id=\"f1\" sourceRef=\"start\" targetRef=\"check\"/>\n"
            + "<bpmn:serviceTask id=\"check\" camunda:class=\"shop.CheckOrder\"/>\n"
            + "<bpmn:exclusiveGateway id=\"gw\"/>\n"
            + "<bpmn:sequenceFlow id=\"f2\" sourceRef=\"gw\" targetRef=\"ship\">"
            + "<bpmn:conditionExpression>${ok}</bpmn:conditionExpression></bpmn:sequenceFlow>\n"
            + "<bpmn:userTask id=\"approve\"/>\n"
            + "<bpmn:serviceTask id=\"ship\" camunda:type=\"external\" camunda:topic=\"shipping\"/>\n"
            + "<bpmn:callActivity id=\"invoice\" calledElement=\"invoicing\"/>\n"
            + "<bpmn:boundaryEvent id=\"timeout\" attachedToRef=\"approve\"/>\n"
            + "<bpmn:endEvent id=\"end\"/>\n"
            + "<!-- <bpmn:task id=\"old\"/> -->\n"
            + "</bpmn:process>\n"
            + DIAGRAM
            + "</bpmn:definitions>\n";

    @Test
    public void bpmn_processWithDiagram_countsTheProcessAndNotTheDiagram() {
        // act, assert
        assertThat(count(BPMN, "processes", PROCESS), is(1));
        assertThat(count(BPMN, "tasks", PROCESS), is(3));
        assertThat(count(BPMN, "call activities and sub-processes", PROCESS), is(1));
        assertThat(count(BPMN, "gateways", PROCESS), is(1));
        assertThat(count(BPMN, "events", PROCESS), is(3));
        assertThat(count(BPMN, "sequence flows", PROCESS), is(2));
        assertThat(count(BPMN, "messages, signals, errors and escalations (named)", PROCESS), is(1));
        assertThat(count(BPMN, "Java delegate bindings", PROCESS), is(1));
        assertThat(count(BPMN, "external task topics, job types and work items", PROCESS), is(1));
        // the diagram's shapes and edges are blanked, so the elements are those of the process alone
        assertThat(count(BPMN, "elements", PROCESS), is(count(BPMN, "elements", PROCESS.replace(DIAGRAM, ""))));
        assertThat(count(XML, "elements", PROCESS) > count(BPMN, "elements", PROCESS), is(true));
    }

    @Test
    public void bpmn_unclosedDiagram_runsToTheEnd() {
        // act, assert
        assertThat(count(BPMN, "processes", "<process id=\"a\"/>\n<bpmndi:BPMNDiagram>\n<process id=\"b\"/>\n"), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dmn-facts">
    private static final List<FactCounter> DMN = ProfileFactPatterns.counters("dmn-facts");

    /** The diagram interchange of {@link #DECISIONS}: shapes and edges, which must not count. */
    private static final String DMN_DIAGRAM = "<dmndi:DMNDI><dmndi:DMNDiagram>\n"
            + "<dmndi:DMNShape dmnElementRef=\"discount\"><dc:Bounds x=\"1\" y=\"2\"/></dmndi:DMNShape>\n"
            + "<dmndi:DMNEdge dmnElementRef=\"r\"><di:waypoint x=\"1\" y=\"2\"/></dmndi:DMNEdge>\n"
            + "</dmndi:DMNDiagram></dmndi:DMNDI>\n";

    /** A decision requirements graph with two decision tables, one of them FIRST, and a literal decision. */
    private static final String DECISIONS = "<definitions xmlns=\"https://www.omg.org/spec/DMN/20191111/MODEL/\">\n"
            + "<inputData id=\"customer\" name=\"Customer\"/>\n"
            + "<businessKnowledgeModel id=\"rate\" name=\"Rate\"/>\n"
            + "<knowledgeSource id=\"policy\" name=\"Pricing policy\"/>\n"
            + "<decision id=\"discount\" name=\"Discount\">\n"
            + "  <informationRequirement><requiredInput href=\"#customer\"/></informationRequirement>\n"
            + "  <knowledgeRequirement><requiredKnowledge href=\"#rate\"/></knowledgeRequirement>\n"
            + "  <authorityRequirement><requiredAuthority href=\"#policy\"/></authorityRequirement>\n"
            + "  <decisionTable id=\"t1\" hitPolicy=\"FIRST\">\n"
            + "    <input id=\"i1\" label=\"Type\"><inputExpression><text>type</text></inputExpression></input>\n"
            + "    <input id=\"i2\" label=\"Total\"><inputExpression><text>total</text></inputExpression></input>\n"
            + "    <output id=\"o1\" name=\"discount\"/>\n"
            + "    <rule id=\"r1\"><inputEntry><text>\"gold\"</text></inputEntry><inputEntry><text>-</text>"
            + "</inputEntry><outputEntry><text>0.1</text></outputEntry></rule>\n"
            + "    <rule id=\"r2\"><inputEntry><text>-</text></inputEntry><inputEntry><text>&gt; 100</text>"
            + "</inputEntry><outputEntry><text>0.05</text></outputEntry></rule>\n"
            + "    <!-- <rule id=\"old\"/> -->\n"
            + "  </decisionTable>\n"
            + "</decision>\n"
            + "<decision id=\"shipping\" name=\"Shipping\">\n"
            + "  <decisionTable id=\"t2\" hitPolicy=\"UNIQUE\"><input id=\"i3\"/><output id=\"o2\"/>"
            + "<rule id=\"r3\"/></decisionTable>\n"
            + "</decision>\n"
            + "<decision id=\"label\"><literalExpression><text>\"x\"</text></literalExpression></decision>\n"
            + "<decision id=\"boxed\"><context><contextEntry/></context></decision>\n"
            + DMN_DIAGRAM
            + "</definitions>\n";

    @Test
    public void dmn_decisionsWithDiagram_countsTheDecisionsAndNotTheDiagram() {
        // act, assert
        assertThat(count(DMN, "decisions", DECISIONS), is(4));
        assertThat(count(DMN, "decision tables", DECISIONS), is(2));
        assertThat(count(DMN, "decision tables with a hit policy other than UNIQUE", DECISIONS), is(1));
        assertThat(count(DMN, "inputs", DECISIONS), is(3));
        assertThat(count(DMN, "outputs", DECISIONS), is(2));
        assertThat(count(DMN, "rules", DECISIONS), is(3));
        assertThat(count(DMN, "input data", DECISIONS), is(1));
        assertThat(count(DMN, "business knowledge models", DECISIONS), is(1));
        assertThat(count(DMN, "knowledge sources", DECISIONS), is(1));
        assertThat(count(DMN, "requirements", DECISIONS), is(3));
        assertThat(count(DMN, "literal expressions", DECISIONS), is(1));
        assertThat(count(DMN, "boxed contexts, invocations, relations and functions", DECISIONS), is(1));
        // the diagram's shapes and edges are blanked, so the elements are those of the decisions alone
        assertThat(count(DMN, "elements", DECISIONS), is(count(DMN, "elements", DECISIONS.replace(DMN_DIAGRAM, ""))));
    }

    @Test
    public void dmn_hitPolicyWithAPrefixOrSpacesAndAnUnclosedDiagram_countsOnce() {
        // act, assert
        assertThat(
                count(
                        DMN,
                        "decision tables with a hit policy other than UNIQUE",
                        "<dmn:decisionTable\n  id=\"t\" hitPolicy = 'COLLECT' aggregation=\"SUM\">\n"),
                is(1));
        assertThat(count(DMN, "decisions", "<decision id=\"a\"/>\n<dmndi:DMNDI>\n<decision id=\"b\"/>\n"), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="wsdl-facts">
    private static final String WSDL_SOURCE = "<definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\" "
            + "xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">\n"
            + "<types><xs:schema><xs:complexType name=\"Order\"/></xs:schema></types>\n"
            + "<message name=\"getOrderRequest\"/><message name=\"getOrderResponse\"/>\n"
            + "<!-- <message name=\"old\"/> -->\n"
            + "<portType name=\"OrderPort\"><operation name=\"getOrder\"><input message=\"tns:getOrderRequest\"/>"
            + "</operation></portType>\n"
            + "<binding name=\"OrderBinding\" type=\"tns:OrderPort\"><soap:binding style=\"document\"/>"
            + "<operation name=\"getOrder\"><soap:operation soapAction=\"get\"/></operation></binding>\n"
            + "<service name=\"OrderService\"><port name=\"OrderPortSoap\" binding=\"tns:OrderBinding\">"
            + "<soap:address location=\"http://x/orders\"/></port></service>\n"
            + "</definitions>\n";

    @Test
    public void wsdl_serviceDefinition_countsTheServiceAndKeepsSoapElementsApart() {
        // act, assert
        assertThat(count(WSDL, "services", WSDL_SOURCE), is(1));
        assertThat(count(WSDL, "ports and endpoints", WSDL_SOURCE), is(1));
        assertThat(count(WSDL, "port types and interfaces", WSDL_SOURCE), is(1));
        assertThat(count(WSDL, "bindings", WSDL_SOURCE), is(1));
        assertThat(count(WSDL, "operations", WSDL_SOURCE), is(2));
        assertThat(count(WSDL, "messages", WSDL_SOURCE), is(2));
        assertThat(count(WSDL, "SOAP bindings", WSDL_SOURCE), is(1));
        assertThat(count(WSDL, "XSD type definitions", WSDL_SOURCE), is(1));
        assertThat(count(WSDL, "operations", WSDL_SOURCE.replace("<", "<wsdl:")), is(2));
    }
    // </editor-fold>
}
