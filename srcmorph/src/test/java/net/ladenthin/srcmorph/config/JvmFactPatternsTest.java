// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the fact counters of the profile for the JVM languages besides Java, which share the
 * prompt {@code file-body-oop} ({@code profiles/v3/kotlin-facts.tsv}, {@code groovy-facts.tsv}), and
 * the Jakarta EE counters over Java ({@code java-ee-facts.tsv}, which includes {@code java-facts}),
 * counted the way the facts line counts them: the {@code [ignore]} lines blank comments and strings
 * first. Each sample holds the traps of its language: a keyword in a comment, a doc comment, a string
 * or a raw string.
 */
public class JvmFactPatternsTest {

    private static final List<AiFactCounter> KOTLIN = ProfileFactPatterns.counters("kotlin-facts");

    private static final List<AiFactCounter> GROOVY = ProfileFactPatterns.counters("groovy-facts");

    private static final List<AiFactCounter> JAVA_EE = ProfileFactPatterns.counters("java-ee-facts");

    private static final List<AiFactCounter> JAVA = ProfileFactPatterns.counters("java-facts");

    private static final List<AiFactCounter> SCALA = ProfileFactPatterns.counters("scala-facts");

    private static int count(final List<AiFactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="kotlin-facts">
    private static final String KOTLIN_SOURCE = "package shop.orders\n"
            + "\n"
            + "import kotlinx.coroutines.flow.Flow\n"
            + "import shop.Money\n"
            + "\n"
            + "/** A fun class in KDoc: fun x() */\n"
            + "// fun commented() {}\n"
            + "/* outer /* nested fun inner() */ class StillComment */\n"
            + "@JvmInline\n"
            + "value class OrderId(val raw: String)\n"
            + "\n"
            + "data class Order(\n"
            + "    val id: OrderId,\n"
            + "    private var total: Money,\n"
            + "    val note: String = \"fun inString() class X ${id}\",\n"
            + ")\n"
            + "\n"
            + "sealed interface Event\n"
            + "object Registry {\n"
            + "    const val LIMIT = 10\n"
            + "    lateinit var cache: String\n"
            + "    fun size(): Int = 0\n"
            + "}\n"
            + "\n"
            + "class OrderService(private val repo: Repo) : Service {\n"
            + "    companion object {\n"
            + "        private const val TAG = \"svc\"\n"
            + "    }\n"
            + "    constructor() : this(Repo())\n"
            + "    suspend fun load(id: OrderId): Order = repo.find(id)\n"
            + "    override fun toString() = \"\"\"raw fun in string\n"
            + "        class Fake\"\"\"\n"
            + "    private fun String.normalized(): String = trim()\n"
            + "}\n"
            + "\n"
            + "fun Order.total(): Money = total\n"
            + "internal suspend inline fun <T> retry(block: () -> T): T = block()\n"
            + "fun interface Handler { fun handle() }\n";

    @Test
    public void kotlinProfile_hasTheseCountersInThisOrder() {
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(KOTLIN, "").keySet()),
                is(Arrays.asList(
                        "type declarations",
                        "companion objects",
                        "data classes",
                        "sealed types",
                        "functions (approx)",
                        "extension functions",
                        "suspend functions",
                        "secondary constructors",
                        "constructor properties (approx)",
                        "const vals",
                        "lateinit properties",
                        "imports")));
    }

    @Test
    public void kotlin_typesCountAndCommentsKdocAndStringsDoNot() {
        // value class, data class, sealed interface, object, class, fun interface
        assertThat(count(KOTLIN, "type declarations", KOTLIN_SOURCE), is(6));
        assertThat(count(KOTLIN, "companion objects", KOTLIN_SOURCE), is(1));
        assertThat(count(KOTLIN, "data classes", KOTLIN_SOURCE), is(1));
        assertThat(count(KOTLIN, "sealed types", KOTLIN_SOURCE), is(1));
    }

    @Test
    public void kotlin_functionsCountAtTheStartOfALineAndAFunInterfaceDoesNot() {
        // size, load, toString, normalized, total, retry; not "fun interface", not a fun mid-line
        assertThat(count(KOTLIN, "functions (approx)", KOTLIN_SOURCE), is(6));
        assertThat(count(KOTLIN, "extension functions", KOTLIN_SOURCE), is(2));
        assertThat(count(KOTLIN, "suspend functions", KOTLIN_SOURCE), is(2));
    }

    @Test
    public void kotlin_constructorsAndProperties() {
        assertThat(count(KOTLIN, "secondary constructors", KOTLIN_SOURCE), is(1));
        // raw, id, total, note, repo
        assertThat(count(KOTLIN, "constructor properties (approx)", KOTLIN_SOURCE), is(5));
        assertThat(count(KOTLIN, "const vals", KOTLIN_SOURCE), is(2));
        assertThat(count(KOTLIN, "lateinit properties", KOTLIN_SOURCE), is(1));
        assertThat(count(KOTLIN, "imports", KOTLIN_SOURCE), is(2));
    }

    @Test
    public void kotlin_unclosedRawStringAndBlockComment_areBlankedToTheEnd() {
        assertThat(count(KOTLIN, "functions (approx)", "fun a() = 1\nval s = \"\"\"\nfun b() = 2\n"), is(1));
        assertThat(count(KOTLIN, "functions (approx)", "fun a() = 1\n/* /* */\nfun b() = 2\n"), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="groovy-facts">
    private static final String GROOVY_SOURCE = "#!/usr/bin/env groovy\n"
            + "package shop\n"
            + "\n"
            + "import groovy.transform.CompileStatic\n"
            + "import spock.lang.Specification\n"
            + "\n"
            + "/* class Commented {} */\n"
            + "@CompileStatic\n"
            + "class OrderSpec extends Specification {\n"
            + "    def service = new OrderService()\n"
            + "\n"
            + "    def \"places an order\"() {\n"
            + "        given:\n"
            + "        def order = new Order(total: 5)\n"
            + "        when:\n"
            + "        service.place(order)\n"
            + "        then:\n"
            + "        order.placed\n"
            + "    }\n"
            + "\n"
            + "    void 'rejects an empty order'() {\n"
            + "        expect:\n"
            + "        service.validate([]) == false\n"
            + "        where:\n"
            + "        x << [1, 2]\n"
            + "    }\n"
            + "\n"
            + "    private int helper(String s) {\n"
            + "        return s.size()\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "trait Audited {\n"
            + "    def audit(String who) { println \"class Fake ${who}\" }\n"
            + "}\n"
            + "\n"
            + "def total = items.collect { item -> item.price }.sum()\n"
            + "def pattern = /class\\s+\\w+/\n"
            + "String gstring = \"\"\"\n"
            + "def fake() { }\n"
            + "\"\"\"\n";

    @Test
    public void groovyProfile_hasTheseCountersInThisOrder() {
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(GROOVY, "").keySet()),
                is(Arrays.asList(
                        "type declarations",
                        "methods (approx)",
                        "Spock feature methods",
                        "Spock blocks",
                        "closures with parameters (approx)",
                        "static type checking",
                        "imports")));
    }

    @Test
    public void groovy_typesAndMethodsCountAndStringsCommentsAndRegexesDoNot() {
        assertThat(count(GROOVY, "type declarations", GROOVY_SOURCE), is(2));
        // helper and the def method audit; not the feature methods, not a call, not the GString's def
        assertThat(count(GROOVY, "methods (approx)", GROOVY_SOURCE), is(2));
    }

    @Test
    public void groovy_spockFeaturesNamedByStringsAndTheirBlocks() {
        assertThat(count(GROOVY, "Spock feature methods", GROOVY_SOURCE), is(2));
        assertThat(count(GROOVY, "Spock blocks", GROOVY_SOURCE), is(5));
    }

    @Test
    public void groovy_closuresStaticCheckingAndImports() {
        assertThat(count(GROOVY, "closures with parameters (approx)", GROOVY_SOURCE), is(1));
        assertThat(count(GROOVY, "static type checking", GROOVY_SOURCE), is(1));
        assertThat(count(GROOVY, "imports", GROOVY_SOURCE), is(2));
    }

    @Test
    public void groovy_shebangAndDollarSlashyString_areBlanked() {
        assertThat(count(GROOVY, "type declarations", "#!/bin/class X\nclass A {}\n"), is(1));
        assertThat(count(GROOVY, "type declarations", "def s = $/\nclass B {}\n/$\nclass A {}\n"), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="java-ee-facts">
    private static final String JAVA_EE_SOURCE = "package shop;\n"
            + "\n"
            + "import jakarta.ejb.Stateless;\n"
            + "\n"
            + "/**\n"
            + " * Uses @Inject and\n"
            + " * @Stateless in prose.\n"
            + " */\n"
            + "@Stateless\n"
            + "@TransactionAttribute(TransactionAttributeType.REQUIRED)\n"
            + "@RolesAllowed(\"admin\") @Path(\"/orders\")\n"
            + "public class OrderResource {\n"
            + "    // @Inject Old old;\n"
            + "    @Inject private Foo foo;\n"
            + "    @Named(\"x\") @Inject Bar bar;\n"
            + "    @PersistenceContext(unitName = \"shop\")\n"
            + "    private EntityManager em;\n"
            + "    @EJB(lookup = \"java:global/x\") Baz baz;\n"
            + "    @GET @Path(\"{id}\") @Produces(\"application/json\")\n"
            + "    public Order get(@PathParam(\"id\") long id) { return null; }\n"
            + "    @Schedule(hour = \"*\") void tick() {}\n"
            + "}\n"
            + "@jakarta.persistence.Entity\n"
            + "@NamedQueries({@NamedQuery(name = \"a\", query = \"x\"), @NamedQuery(name = \"b\", query = \"y\")})\n"
            + "class Order {}\n"
            + "@MessageDriven class Listener {}\n"
            + "@WebServlet(\"/x\") class Front {}\n"
            + "@javax.jws.WebService class Soap {}\n"
            + "@javax.inject.Singleton class S {}\n";

    @Test
    public void javaEeProfile_startsWithTheJavaCountersAndAddsItsOwn() {
        final List<String> labels =
                new ArrayList<>(ProfileFactPatterns.counts(JAVA_EE, "").keySet());
        final List<String> java =
                new ArrayList<>(ProfileFactPatterns.counts(JAVA, "").keySet());
        assertThat(labels.subList(0, java.size()), is(java));
        assertThat(
                labels.subList(java.size(), labels.size()),
                is(Arrays.asList(
                        "EJB session beans",
                        "message-driven beans",
                        "JPA entities and embeddables",
                        "JPA named queries",
                        "injection points",
                        "JAX-RS resource paths",
                        "HTTP method annotations",
                        "servlets, filters and listeners",
                        "SOAP endpoints",
                        "transaction annotations",
                        "EJB timers",
                        "security annotations")));
        assertThat(
                count(JAVA_EE, "type declarations", JAVA_EE_SOURCE),
                is(count(JAVA, "type declarations", JAVA_EE_SOURCE)));
    }

    @Test
    public void javaEe_componentsCountAndProseAndOtherPackagesDoNot() {
        assertThat(count(JAVA_EE, "EJB session beans", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "message-driven beans", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "JPA entities and embeddables", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "JPA named queries", JAVA_EE_SOURCE), is(2));
        assertThat(count(JAVA_EE, "servlets, filters and listeners", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "SOAP endpoints", JAVA_EE_SOURCE), is(1));
    }

    @Test
    public void javaEe_wiringCountsAfterOtherAnnotationsAndNotInAComment() {
        // foo, bar (after @Named), em, baz; not the commented-out @Inject
        assertThat(count(JAVA_EE, "injection points", JAVA_EE_SOURCE), is(4));
        assertThat(count(JAVA_EE, "JAX-RS resource paths", JAVA_EE_SOURCE), is(2));
        assertThat(count(JAVA_EE, "HTTP method annotations", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "transaction annotations", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "EJB timers", JAVA_EE_SOURCE), is(1));
        assertThat(count(JAVA_EE, "security annotations", JAVA_EE_SOURCE), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="scala-facts">
    private static final String SCALA_SOURCE = "package shop\n"
            + "import scala.concurrent.Future\n"
            + "import shop.model.{Order, Line}\n"
            + "/** A class Fake in the Scaladoc; def fake() = 1 */\n"
            + "sealed trait Event\n"
            + "final case class Placed(id: String) extends Event\n"
            + "case object Cancelled extends Event\n"
            + "/* class Old {\n   /* nested */ def gone() = 0\n} */\n"
            + "@deprecated(\"use Orders\") private[shop] class OrderService(repo: Repo) {\n"
            + "  private val Prefix = \"def notAMethod = 1\"\n"
            + "  lazy val cache = Map.empty[String, Order]\n"
            + "  type Id = String\n"
            + "  override def toString: String = s\"OrderService(${repo})\"\n"
            + "  def place(order: Order)(implicit ec: ExecutionContext): Future[Order] = {\n"
            + "    val symbol = 'placed\n"
            + "    val quote = '\"'\n"
            + "    def local(x: Int) = x + 1\n"
            + "    repo.save(order)\n"
            + "  }\n"
            + "  def +(other: OrderService): OrderService = this\n"
            + "}\n"
            + "object OrderService {\n"
            + "  implicit val ordering: Ordering[Order] = Ordering.by(_.id)\n"
            + "  given Conversion[String, Order] = Order(_)\n"
            + "}\n"
            + "enum Status:\n"
            + "  case New, Paid\n"
            + "extension (o: Order)\n"
            + "  def total: BigDecimal = o.lines.map(_.price).sum\n"
            + "opaque type Money = BigDecimal\n";

    @Test
    public void scala_countsDeclarationsAndNotCommentsOrStrings() {
        // Event, Placed, Cancelled, OrderService, OrderService (companion), Status
        assertThat(count(SCALA, "type declarations", SCALA_SOURCE), is(6));
        assertThat(count(SCALA, "case classes and case objects", SCALA_SOURCE), is(2));
        assertThat(count(SCALA, "traits", SCALA_SOURCE), is(1));
        // toString, place, local, +, total
        assertThat(count(SCALA, "methods (approx)", SCALA_SOURCE), is(5));
        assertThat(count(SCALA, "extension methods", SCALA_SOURCE), is(1));
        assertThat(count(SCALA, "givens and implicits", SCALA_SOURCE), is(2));
        // Prefix, cache, symbol, quote, ordering
        assertThat(count(SCALA, "vals and vars at the start of a line (members and locals)", SCALA_SOURCE), is(5));
        assertThat(count(SCALA, "type aliases", SCALA_SOURCE), is(2));
        assertThat(count(SCALA, "imports", SCALA_SOURCE), is(2));
    }

    @Test
    public void scala_anUnclosedNestedCommentRunsToTheEnd() {
        assertThat(count(SCALA, "type declarations", "class A\n/* /* nested */ still open\nclass B\n"), is(1));
    }
    // </editor-fold>
}
