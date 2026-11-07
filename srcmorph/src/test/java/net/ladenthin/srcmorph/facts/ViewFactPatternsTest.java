// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the fact counters of the profile for server-side views, which share the prompt
 * {@code file-body-view} ({@code profiles/v3/jsp-facts.tsv}, {@code facelets-facts.tsv}), counted the
 * way the facts line counts them. A JSP comment is blanked; an HTML comment is not, since the server
 * still runs the scriptlet in it. A Facelets view includes {@code xml-facts}, whose {@code [ignore]}
 * line blanks XML comments.
 */
public class ViewFactPatternsTest {

    private static final List<FactCounter> JSP = ProfileFactPatterns.counters("jsp-facts");

    private static final List<FactCounter> JSPX = ProfileFactPatterns.counters("jspx-facts");

    private static final List<FactCounter> FACELETS = ProfileFactPatterns.counters("facelets-facts");

    private static int count(final List<FactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="jsp-facts">
    private static final String PAGE = "<%@ page contentType=\"text/html\" import=\"java.util.*,x.Order\" %>\n"
            + "<%@ taglib prefix=\"c\" uri=\"jakarta.tags.core\" %>\n"
            + "<%@ include file=\"/WEB-INF/jspf/header.jspf\" %>\n"
            + "<%-- <% oldCode(); %> ${old} --%>\n"
            + "<!-- <%= request.getParameter(\"debug\") %> -->\n"
            + "<% List<Order> os = (List<Order>) request.getAttribute(\"orders\"); %>\n"
            + "<%! int counter; %>\n"
            + "<form action=\"order.do\" method=\"post\"><input type=\"text\" name=\"qty\"/><select name=\"s\">"
            + "</select></form>\n"
            + "<c:forEach items=\"${orders}\" var=\"o\"><c:out value=\"${o.id}\"/></c:forEach> ${param.q} "
            + "<jsp:include page=\"footer.jsp\"/>\n"
            + "<form:form modelAttribute=\"x\"><form:input path=\"n\"/></form:form>\n";

    @Test
    public void jsp_directivesAndServerCode_countAndAJspCommentDoesNot() {
        // act, assert
        assertThat(count(JSP, "page directives", PAGE), is(1));
        assertThat(count(JSP, "taglib directives", PAGE), is(1));
        assertThat(count(JSP, "includes and forwards", PAGE), is(2));
        assertThat(count(JSP, "tag file attributes", PAGE), is(0));
        assertThat(count(JSP, "scriptlets", PAGE), is(1));
        // the one in the HTML comment: the server runs it
        assertThat(count(JSP, "expressions", PAGE), is(1));
        assertThat(count(JSP, "declarations", PAGE), is(1));
    }

    @Test
    public void jsp_dataTagsAndForms_countEach() {
        // act, assert
        assertThat(count(JSP, "EL expressions", PAGE), is(3));
        assertThat(count(JSP, "prefixed tags", PAGE), is(5));
        assertThat(count(JSP, "forms", PAGE), is(2));
        assertThat(count(JSP, "input fields", PAGE), is(3));
        assertThat(count(JSP, "request parameters read", PAGE), is(2));
    }

    @Test
    public void jsp_springFormChoiceAndHiddenTags_countAsInputFields() {
        // arrange
        final String page = "<form:checkboxes path=\"roles\" items=\"${roles}\"/>\n"
                + "<form:radiobutton path=\"g\" value=\"m\"/>\n"
                + "<form:radiobuttons path=\"size\" items=\"${sizes}\"/>\n"
                + "<form:hidden path=\"id\"/>\n"
                + "<form:checkbox path=\"ok\"/>\n";

        // act, assert
        assertThat(count(JSP, "input fields", page), is(5));
    }

    @Test
    public void jsp_tagFileAttributesAndAnUnclosedComment_countTheAttributeAndRunToTheEnd() {
        // act, assert
        assertThat(count(JSP, "tag file attributes", "<%@ attribute name=\"total\" required=\"true\" %>\n"), is(1));
        assertThat(count(JSP, "scriptlets", "<% a(); %>\n<%-- <% b(); %>\n<% c(); %>\n"), is(1));
    }

    /** A JSP document (.jspx, .tagx) writes its directives as elements; its XML comments are never sent. */
    private static final String DOCUMENT = "<jsp:root xmlns:jsp=\"http://java.sun.com/JSP/Page\" version=\"2.0\">\n"
            + "<jsp:directive.page contentType=\"text/html\"/>\n"
            + "<jsp:directive.include file=\"header.jspf\"/>\n"
            + "<!-- <jsp:scriptlet>old();</jsp:scriptlet> ${old} -->\n"
            + "<p>${order.id}</p>\n"
            + "</jsp:root>\n";

    @Test
    public void jspx_directiveElementsAndAnXmlComment_countTheDirectivesAndNotTheComment() {
        // act, assert
        assertThat(count(JSPX, "page directives", DOCUMENT), is(1));
        assertThat(count(JSPX, "includes and forwards", DOCUMENT), is(1));
        assertThat(count(JSPX, "scriptlets", DOCUMENT), is(0));
        assertThat(count(JSPX, "EL expressions", DOCUMENT), is(1));
        assertThat(count(JSPX, "tag file attributes", "<jsp:directive.attribute name=\"total\"/>\n"), is(1));
    }

    @Test
    public void jsp_directiveElements_countInAPageToo() {
        // act, assert
        assertThat(count(JSP, "page directives", DOCUMENT), is(1));
        assertThat(count(JSP, "includes and forwards", DOCUMENT), is(1));
        // an HTML comment of a page is sent, and its server code runs
        assertThat(count(JSP, "scriptlets", DOCUMENT), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="facelets-facts">
    private static final String VIEW = "<ui:composition template=\"/t.xhtml\" xmlns:h=\"jakarta.faces.html\" "
            + "xmlns:ui=\"jakarta.faces.facelets\">\n"
            + "<!-- <h:form/> #{old} -->\n"
            + "<h:form><h:inputText value=\"#{orderBean.qty}\"/><h:selectOneMenu value=\"#{orderBean.s}\"/>"
            + "<h:commandButton action=\"#{orderBean.save}\"/><f:ajax listener='#{b.l}'/></h:form>\n"
            + "<ui:include src=\"footer.xhtml\"/>\n"
            + "</ui:composition>\n";

    @Test
    public void facelets_viewWithComment_countsBindingsFormsAndTemplatesAndNotAComment() {
        // act, assert
        assertThat(count(FACELETS, "EL expressions", VIEW), is(4));
        assertThat(count(FACELETS, "forms", VIEW), is(1));
        assertThat(count(FACELETS, "input fields", VIEW), is(2));
        assertThat(count(FACELETS, "EL method bindings", VIEW), is(2));
        assertThat(count(FACELETS, "templates and includes", VIEW), is(2));
        assertThat(count(FACELETS, "composite component interfaces", VIEW), is(0));
        assertThat(
                count(FACELETS, "composite component interfaces", "<cc:interface><cc:attribute/></cc:interface>"),
                is(1));
    }

    @Test
    public void facelets_selectWithItsOptions_countsTheSelectAndNotTheOptions() {
        // arrange
        final String view = "<h:selectOneMenu value=\"#{b.kind}\"><f:selectItem itemValue=\"a\"/>"
                + "<f:selectItems value=\"#{b.kinds}\"/></h:selectOneMenu>\n"
                + "<h:selectManyCheckbox value=\"#{b.tags}\"><f:selectItems value=\"#{b.all}\" /></h:selectManyCheckbox>\n";

        // act, assert
        assertThat(count(FACELETS, "input fields", view), is(2));
    }
    // </editor-fold>
}
