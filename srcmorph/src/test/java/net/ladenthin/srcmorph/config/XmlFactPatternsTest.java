// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the XML fact counters of the current profile ({@code profiles/v3/xml-facts.tsv} for any XML,
 * XSD and XSLT file, {@code profiles/v3/pom-facts.tsv} for Maven POMs), counted the way the facts line
 * counts them: the {@code [ignore]} counters blank comments and CDATA sections first. The run-time tests
 * repeat an unclosed opener or tag over a long input, where the lazy patterns these replaced were
 * quadratic (about 8 s at 100 KB) or worse. The last tests pin the counts of the benchmark's language
 * corpus.
 */
public class XmlFactPatternsTest {

    /** What a long input may take; a linear pattern needs well under a second. */
    private static final Duration LONG_INPUT_LIMIT = Duration.ofSeconds(10);

    /** Lines of the generated inputs of the run-time tests. */
    private static final int LONG_INPUT_LINES = 20_000;

    private static final List<AiFactCounter> XML = ProfileFactPatterns.counters("xml-facts");

    private static final List<AiFactCounter> POM = ProfileFactPatterns.counters("pom-facts");

    private static int count(final List<AiFactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    /** Repeats {@code line} {@value #LONG_INPUT_LINES} times behind {@code head}. */
    private static String headThenLines(final String head, final String line) {
        final StringBuilder source = new StringBuilder(head);
        for (int i = 0; i < LONG_INPUT_LINES; i++) {
            source.append(line).append('\n');
        }
        return source.toString();
    }

    private static int countLong(final List<AiFactCounter> counters, final String label, final String source) {
        return assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count(counters, label, source));
    }

    @Test
    public void xmlProfile_hasTheseCountersInThisOrder() {
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(XML, "").keySet()),
                is(Arrays.asList(
                        "elements",
                        "namespace declarations",
                        "id and name attributes",
                        "XSD type definitions",
                        "XSLT templates",
                        "Spring beans")));
    }

    @Test
    public void pomProfile_hasTheseCountersInThisOrder() {
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(POM, "").keySet()),
                is(Arrays.asList(
                        "dependencies",
                        "managed dependency sections",
                        "plugins",
                        "modules",
                        "profiles",
                        "test-scoped dependencies",
                        "managed plugin sections")));
    }

    // <editor-fold defaultstate="collapsed" desc="xml-facts">
    @Test
    public void elements_startTagsCountAndTheDeclarationACommentAndCdataDoNot() {
        assertThat(
                count(XML, "elements", "<?xml version=\"1.0\"?>\n<!-- <fake/> -->\n<a><b/><![CDATA[<c>]]></a>\n"),
                is(2));
    }

    @Test
    public void elements_commentOpenerInsideCdata_opensNothing() {
        assertThat(count(XML, "elements", "<r><![CDATA[ <!-- ]]><b/><c/><!-- c --><d/></r>\n"), is(4));
    }

    @Test
    public void elements_cdataOpenerInsideAComment_opensNothing() {
        assertThat(count(XML, "elements", "<r><!-- <![CDATA[ --><b/><c/><![CDATA[ x ]]><d/></r>\n"), is(4));
    }

    @Test
    public void elements_markupInAProcessingInstruction_doesNotCount() {
        assertThat(count(XML, "elements", "<?php echo \"<b>\"; ?>\n<r/>\n"), is(1));
    }

    @Test
    public void namespaceDeclarations_defaultAndPrefixed_count() {
        assertThat(
                count(
                        XML,
                        "namespace declarations",
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
                                + "    xmlns:xsi = 'http://www.w3.org/2001/XMLSchema-instance'>\n"),
                is(2));
    }

    @Test
    public void idAndNameAttributes_onlyThoseExactAttributeNames() {
        assertThat(
                count(XML, "id and name attributes", "<bean id=\"a\" name=\"b\"/>\n<x pid=\"c\" names=\"d\"/>\n"),
                is(2));
    }

    /** Element text is no tag: a "name = 'a'" in it is no attribute; one over several lines of a tag is. */
    @Test
    public void idAndNameAttributes_inElementText_doNotCount() {
        assertThat(
                count(
                        XML,
                        "id and name attributes",
                        "<p>set name = 'a' and id=\"b\" here</p>\n<bean\n    class=\"x\"\n    id=\"c\"/>\n"),
                is(1));
    }

    @Test
    public void idAndNameAttributes_prefixedOnesCountANamespaceDeclarationDoesNot() {
        assertThat(
                count(
                        XML,
                        "id and name attributes",
                        "<TextView android:id=\"@+id/t\" android:name=\"n\" xml:id=\"i\" xmlns:name=\"urn:x\"/>\n"),
                is(3));
    }

    @Test
    public void xsdTypeDefinitions_namedTypesCountAnonymousOnesDoNot() {
        assertThat(
                count(
                        XML,
                        "XSD type definitions",
                        "<xs:complexType name=\"A\">\n</xs:complexType>\n<xs:simpleType name=\"B\"/>\n"
                                + "<xs:element name=\"c\"><xs:complexType>\n</xs:complexType></xs:element>\n"),
                is(2));
    }

    @Test
    public void xsltTemplates_matchAndNamedCountCallsAndApplyDoNot() {
        assertThat(
                count(
                        XML,
                        "XSLT templates",
                        "<xsl:template match=\"/\">\n<xsl:call-template name=\"x\"/>\n"
                                + "<xsl:apply-templates/>\n</xsl:template>\n<xsl:template name=\"x\"/>\n<templates/>\n"),
                is(2));
    }

    @Test
    public void springBeans_beanElementsCountTheBeansRootDoesNot() {
        assertThat(
                count(
                        XML,
                        "Spring beans",
                        "<beans>\n<bean id=\"a\"/>\n<!-- <bean id=\"old\"/> -->\n<bean-name>x</bean-name>\n</beans>\n"),
                is(1));
    }

    @Test
    public void elements_markupInADoctypeInternalSubset_doesNotCount() {
        assertThat(
                count(
                        XML,
                        "elements",
                        "<?xml version=\"1.0\"?>\n<!DOCTYPE r [\n<!ENTITY e \"<b>x</b>\">\n<!ELEMENT r (#PCDATA)>\n]>\n<r>&e;</r>\n"),
                is(1));
        assertThat(
                count(
                        XML,
                        "elements",
                        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><body/></html>\n"),
                is(2));
    }

    @Test
    public void elements_unclosedComment_isBlankedToTheEnd() {
        assertThat(count(XML, "elements", "<a/>\n<!-- <b/>\n<c/>\n"), is(1));
    }

    @Test
    public void elements_unclosedCdataAndProcessingInstruction_areBlankedToTheEnd() {
        assertThat(count(XML, "elements", "<a/>\n<![CDATA[ <b/>\n<c/>\n"), is(1));
        assertThat(count(XML, "elements", "<a/>\n<?pi <b/>\n<c/>\n"), is(1));
    }

    @Test
    public void elements_unclosedDoctype_isBlankedToTheEnd() {
        assertThat(count(XML, "elements", "<!DOCTYPE r [\n<!ELEMENT r ANY>\n<r/>\n"), is(0));
        assertThat(count(XML, "elements", "<!DOCTYPE r [ <!ELEMENT r ANY> ]\n<r/>\n"), is(0));
    }

    @Test
    public void ignore_manyUnclosedComments_stayLinear() {
        assertThat(countLong(XML, "elements", headThenLines("<r/>\n", "<!-- x")), is(1));
    }

    @Test
    public void ignore_manyUnclosedCdataSections_stayLinear() {
        assertThat(countLong(XML, "elements", headThenLines("<r/>\n", "<![CDATA[ x")), is(1));
    }

    @Test
    public void ignore_manyUnclosedProcessingInstructions_stayLinear() {
        assertThat(countLong(XML, "elements", headThenLines("<r/>\n", "<? x")), is(1));
    }

    @Test
    public void ignore_manyUnclosedDoctypes_stayLinear() {
        assertThat(countLong(XML, "elements", headThenLines("<r/>\n", "<!DOCTYPE r [ x")), is(1));
        assertThat(countLong(XML, "elements", headThenLines("<r/>\n", "<!DOCTYPE r [ ] x")), is(1));
    }

    @Test
    public void xsdTypeDefinitions_manyUnclosedTypeTags_stayLinear() {
        assertThat(countLong(XML, "XSD type definitions", headThenLines("", "<xs:complexType a=\"b\"")), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="pom-facts">
    @Test
    public void dependencies_everyDependencyElementCountsTheListAndManagementTagsDoNot() {
        assertThat(
                count(
                        POM,
                        "dependencies",
                        "<dependencies><dependency/>\n<dependency>\n</dependency></dependencies>\n"
                                + "<dependencyManagement><dependencies><dependency/></dependencies></dependencyManagement>\n"
                                + "<!-- <dependency/> -->\n"),
                is(3));
    }

    @Test
    public void managedDependencySections_counted() {
        assertThat(
                count(POM, "managed dependency sections", "<dependencyManagement>\n</dependencyManagement>\n"), is(1));
    }

    @Test
    public void pluginsModulesAndProfiles_elementsCountTheirListsDoNot() {
        final String pom = "<plugins><plugin/><plugin>\n</plugin></plugins>\n<modules><module>a</module></modules>\n"
                + "<profiles><profile><id>x</id></profile></profiles>\n";
        assertThat(count(POM, "plugins", pom), is(2));
        assertThat(count(POM, "modules", pom), is(1));
        assertThat(count(POM, "profiles", pom), is(1));
    }

    @Test
    public void testScopedDependencies_scopeTestWithOrWithoutBlanks() {
        assertThat(
                count(
                        POM,
                        "test-scoped dependencies",
                        "<scope>test</scope>\n<scope> test </scope>\n<scope>runtime</scope>\n"),
                is(2));
    }

    @Test
    public void dependencies_ofAPlugin_doNotCount() {
        final String pom = "<dependencies><dependency><scope>test</scope></dependency></dependencies>\n"
                + "<build><plugins><plugin>\n    <artifactId>x</artifactId>\n"
                + "    <dependencies><dependency><scope>test</scope></dependency><dependency/></dependencies>\n"
                + "</plugin></plugins></build>\n";
        assertThat(count(POM, "dependencies", pom), is(1));
        assertThat(count(POM, "test-scoped dependencies", pom), is(1));
        assertThat(count(POM, "plugins", pom), is(1));
    }

    @Test
    public void plugins_everyPlaceCountsAndManagedPluginSectionsSaysWhetherThereAreAny() {
        final String pom = "<build><pluginManagement><plugins><plugin/></plugins></pluginManagement>\n"
                + "<plugins><plugin>\n</plugin></plugins></build>\n<reporting><plugins><plugin/></plugins></reporting>\n"
                + "<profiles><profile><build><plugins><plugin/></plugins></build></profile></profiles>\n";
        assertThat(count(POM, "plugins", pom), is(4));
        assertThat(count(POM, "managed plugin sections", pom), is(1));
    }

    @Test
    public void modulesAndProfiles_insideAPluginConfiguration_doNotCount() {
        final String pom = "<plugin><configuration><modules><module>a</module></modules>"
                + "<profiles><profile>x</profile></profiles></configuration></plugin>\n";
        assertThat(count(POM, "modules", pom), is(0));
        assertThat(count(POM, "profiles", pom), is(0));
    }

    @Test
    public void plugins_nestedInASpotBugsConfiguration_areNoPluginsAndItsDependencyIsNone() {
        final String pom = "<plugins><plugin>\n    <artifactId>spotbugs-maven-plugin</artifactId>\n"
                + "    <configuration><plugins>\n        <plugin><artifactId>fb-contrib</artifactId></plugin>\n"
                + "        <plugin><artifactId>findsecbugs-plugin</artifactId></plugin>\n    </plugins></configuration>\n"
                + "    <dependencies><dependency/></dependencies>\n</plugin></plugins>\n"
                + "<dependencies><dependency/></dependencies>\n";
        assertThat(count(POM, "plugins", pom), is(1));
        assertThat(count(POM, "dependencies", pom), is(1));
    }

    @Test
    public void dependencies_inCdataOrAfterACommentedPluginOpener_countOnce() {
        assertThat(
                count(
                        POM,
                        "dependencies",
                        "<description><![CDATA[<dependency/>]]></description>\n<!-- <plugin> -->\n<dependency/>\n"),
                is(1));
    }

    @Test
    public void dependencies_afterAnUnclosedComment_areNone() {
        assertThat(count(POM, "dependencies", "<dependency/>\n<!-- <dependency/>\n<dependency/>\n"), is(1));
    }

    @Test
    public void ignore_manyUnclosedComments_inAPom_stayLinear() {
        assertThat(countLong(POM, "dependencies", headThenLines("<dependency/>\n", "<!-- x")), is(1));
        assertThat(countLong(POM, "dependencies", headThenLines("<dependency/>\n", "<![CDATA[ x")), is(1));
    }

    @Test
    public void ignore_manyUnclosedPlugins_stayLinear() {
        assertThat(countLong(POM, "plugins", headThenLines("", "<plugin>")), is(LONG_INPUT_LINES));
        assertThat(countLong(POM, "plugins", headThenLines("", "<plugin><plugin>x")), is(2 * LONG_INPUT_LINES));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the benchmark's language corpus">
    @Test
    public void corpus_schedulerContextXml() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/xml/scheduler-context.xml");
        assertThat(count(XML, "elements", source), is(19));
        assertThat(count(XML, "namespace declarations", source), is(4));
        assertThat(count(XML, "id and name attributes", source), is(11));
        assertThat(count(XML, "Spring beans", source), is(4));
    }

    @Test
    public void corpus_shipmentXsd() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/xml/shipment.xsd");
        assertThat(count(XML, "elements", source), is(31));
        assertThat(count(XML, "id and name attributes", source), is(20));
        assertThat(count(XML, "XSD type definitions", source), is(5));
        assertThat(count(XML, "namespace declarations", source), is(2));
    }

    @Test
    public void corpus_statementToHtmlXsl() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/xml/statement-to-html.xsl");
        assertThat(count(XML, "elements", source), is(34));
        assertThat(count(XML, "id and name attributes", source), is(8));
        assertThat(count(XML, "XSLT templates", source), is(4));
    }

    @Test
    public void corpus_invoiceServicePom() {
        final String source = ProfileFactPatterns.languageCorpusFile("other/xml/invoice-service-pom.xml");
        assertThat(count(XML, "elements", source), is(59));
        assertThat(count(POM, "dependencies", source), is(6));
        assertThat(count(POM, "managed dependency sections", source), is(0));
        assertThat(count(POM, "plugins", source), is(2));
        assertThat(count(POM, "modules", source), is(0));
        assertThat(count(POM, "profiles", source), is(1));
        assertThat(count(POM, "test-scoped dependencies", source), is(2));
        assertThat(count(POM, "managed plugin sections", source), is(0));
    }
    // </editor-fold>
}
