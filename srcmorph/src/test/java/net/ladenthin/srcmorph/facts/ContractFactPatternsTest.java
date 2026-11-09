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
 * Validates the fact counters of the profile for interface contracts besides OpenAPI
 * ({@code profiles/v3/proto-facts.tsv}, {@code avro-facts.tsv}, {@code graphql-facts.tsv}; AsyncAPI is
 * in {@link OpsFactPatternsTest} next to OpenAPI, whose ignore line it shares), counted the way the
 * facts line counts them: each sample has a commented-out or quoted declaration that must not count.
 */
public class ContractFactPatternsTest {

    private static final List<FactCounter> PROTO = ProfileFactPatterns.counters("proto-facts");

    private static final List<FactCounter> AVRO = ProfileFactPatterns.counters("avro-facts");

    private static final List<FactCounter> GRAPHQL = ProfileFactPatterns.counters("graphql-facts");

    private static int count(final List<FactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="proto-facts">
    private static final String PROTO_SOURCE = "syntax = \"proto3\";\n"
            + "package shop.v1;\n"
            + "import \"google/protobuf/timestamp.proto\";\n"
            + "option java_package = \"shop.message Fake\";\n"
            + "// message Old { int32 id = 1; }\n"
            + "/* service Legacy {\n   rpc Gone(A) returns (B);\n} */\n"
            + "message Order {\n"
            + "  reserved 2, 15;\n"
            + "  string id = 1;\n"
            + "  repeated Line lines = 3;\n"
            + "  map<string, int32> counts = 4;\n"
            + "  optional google.protobuf.Timestamp created = 5;\n"
            + "  oneof payment {\n"
            + "    string card = 6;\n"
            + "    string iban = 7;\n"
            + "  }\n"
            + "  message Line { int32 qty = 1; }\n"
            + "}\n"
            + "enum Status {\n"
            + "  STATUS_UNSPECIFIED = 0;\n"
            + "  STATUS_PAID = 1;\n"
            + "}\n"
            + "service Orders {\n"
            + "  rpc Get(GetRequest) returns (Order);\n"
            + "  rpc Watch(WatchRequest) returns (stream Order);\n"
            + "  rpc Chat(stream Note) returns (stream Note);\n"
            + "}\n";

    @Test
    public void proto_declarationsAndComments_countsTheDeclarationsAndNotTheComments() {
        // act, assert
        assertThat(count(PROTO, "messages", PROTO_SOURCE), is(2));
        assertThat(count(PROTO, "enums", PROTO_SOURCE), is(1));
        assertThat(count(PROTO, "services", PROTO_SOURCE), is(1));
        assertThat(count(PROTO, "rpcs", PROTO_SOURCE), is(3));
        assertThat(count(PROTO, "streaming rpcs", PROTO_SOURCE), is(2));
        // id, lines, counts, created, card, iban; qty follows "{" on its line
        assertThat(count(PROTO, "fields (approx)", PROTO_SOURCE), is(6));
        assertThat(count(PROTO, "enum values", PROTO_SOURCE), is(2));
        assertThat(count(PROTO, "oneofs", PROTO_SOURCE), is(1));
        assertThat(count(PROTO, "reserved ranges and names", PROTO_SOURCE), is(1));
        assertThat(count(PROTO, "imports", PROTO_SOURCE), is(1));
    }

    @Test
    public void proto_streamingRpcWithReturnsOnTheNextLine_countsWithEitherLineEnding() {
        // arrange
        final String source = "service S {\n  rpc Watch(WatchRequest)\n      returns (stream Order);\n"
                + "  rpc Tail(TailRequest)\r\n      returns (stream Line);\r\n"
                + "  rpc Get(GetRequest) returns (Order);\n}\n";

        // act, assert
        assertThat(count(PROTO, "streaming rpcs", source), is(2));
    }

    @Test
    public void proto_unclosedBlockComment_runsToTheEnd() {
        // act, assert
        assertThat(count(PROTO, "messages", "message A {}\n/* message B {}\nmessage C {}\n"), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="avro-facts">
    private static final String AVRO_SOURCE = "{\n"
            + "  \"type\": \"record\", \"name\": \"Order\", \"namespace\": \"shop.avro\",\n"
            + "  \"fields\": [\n"
            + "    {\"name\": \"id\", \"type\": {\"type\": \"string\", \"logicalType\": \"uuid\"}},\n"
            + "    {\"name\": \"status\", \"type\": {\"type\": \"enum\", \"name\": \"Status\","
            + " \"symbols\": [\"NEW\", \"PAID\"]}},\n"
            + "    {\"name\": \"lines\", \"type\": {\"type\": \"array\", \"items\":\n"
            + "      {\"type\" : \"record\", \"name\": \"Line\", \"fields\": [{\"name\": \"qty\", \"type\": \"int\"}]}}},\n"
            + "    {\"name\": \"tags\", \"type\": {\"type\": \"map\", \"values\": \"string\"}},\n"
            + "    {\"name\": \"hash\", \"type\": {\"type\": \"fixed\", \"name\": \"Md5\", \"size\": 16}},\n"
            + "    {\"name\": \"note\", \"type\": [\"null\", \"string\"], \"default\": null}\n"
            + "  ]\n"
            + "}\n";

    @Test
    public void avro_recordSchema_countsTheTypesAndTheNamedElements() {
        // act, assert
        assertThat(count(AVRO, "records", AVRO_SOURCE), is(2));
        assertThat(count(AVRO, "enums", AVRO_SOURCE), is(1));
        assertThat(count(AVRO, "fixed types", AVRO_SOURCE), is(1));
        assertThat(count(AVRO, "arrays", AVRO_SOURCE), is(1));
        assertThat(count(AVRO, "maps", AVRO_SOURCE), is(1));
        assertThat(count(AVRO, "unions", AVRO_SOURCE), is(1));
        // Order, id, status, Status, lines, Line, qty, tags, hash, Md5, note
        assertThat(count(AVRO, "named elements (types and fields)", AVRO_SOURCE), is(11));
        assertThat(count(AVRO, "field lists", AVRO_SOURCE), is(2));
        assertThat(count(AVRO, "defaults", AVRO_SOURCE), is(1));
        assertThat(count(AVRO, "logical types", AVRO_SOURCE), is(1));
        assertThat(count(AVRO, "namespaces", AVRO_SOURCE), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="graphql-facts">
    private static final String SCHEMA = "\"\"\"\n"
            + "The shop. type Fake { x: Int }\n"
            + "\"\"\"\n"
            + "schema { query: Query mutation: Mutation }\n"
            + "# type Old { id: ID }\n"
            + "directive @auth(role: String) on FIELD_DEFINITION\n"
            + "scalar DateTime\n"
            + "interface Node {\n"
            + "  id: ID!\n"
            + "}\n"
            + "type Order implements Node @key(fields: \"id\") {\n"
            + "  id: ID!\n"
            + "  \"the lines; total: Int\"\n"
            + "  lines(first: Int = 10, after: String): [Line!]!\n"
            + "  total: Int @deprecated(reason: \"use amount\")\n"
            + "}\n"
            + "type Line { qty: Int }\n"
            + "union SearchResult = Order | Line\n"
            + "enum Status {\n"
            + "  NEW\n"
            + "  PAID @deprecated\n"
            + "}\n"
            + "input OrderInput {\n"
            + "  lines: [Int!]\n"
            + "}\n"
            + "type Query {\n"
            + "  order(id: ID!): Order @auth(role: \"user\")\n"
            + "}\n"
            + "extend type Query {\n"
            + "  search(text: String): [SearchResult]\n"
            + "}\n"
            + "type Mutation {\n"
            + "  placeOrder(input: OrderInput!): Order\n"
            + "}\n";

    @Test
    public void graphql_schemaWithDescriptions_countsTheSchemaAndNotTheDescriptions() {
        // act, assert
        assertThat(count(GRAPHQL, "object types", SCHEMA), is(2));
        assertThat(count(GRAPHQL, "root operation types", SCHEMA), is(3));
        assertThat(count(GRAPHQL, "interfaces", SCHEMA), is(1));
        assertThat(count(GRAPHQL, "unions", SCHEMA), is(1));
        assertThat(count(GRAPHQL, "enums", SCHEMA), is(1));
        assertThat(count(GRAPHQL, "input types", SCHEMA), is(1));
        assertThat(count(GRAPHQL, "scalars", SCHEMA), is(1));
        // id (Node), id, lines, total, lines (input), order, search, placeOrder; qty is on the type's line
        assertThat(count(GRAPHQL, "fields (approx)", SCHEMA), is(8));
        assertThat(count(GRAPHQL, "directive definitions", SCHEMA), is(1));
        // @key, @deprecated twice, @auth; not the definition's own @auth
        assertThat(count(GRAPHQL, "directive uses", SCHEMA), is(4));
        assertThat(count(GRAPHQL, "deprecated fields and values", SCHEMA), is(2));
        assertThat(count(GRAPHQL, "operations", SCHEMA), is(0));
    }

    @Test
    public void graphql_executableDocument_countsTheOperationsAndFragments() {
        // arrange
        final String operations = "query Orders($first: Int) {\n"
                + "  orders(first: $first) { ...OrderParts }\n"
                + "}\n"
                + "mutation Pay { pay(id: 1) { ok } }\n"
                + "# subscription Old { x }\n"
                + "fragment OrderParts on Order { id total }\n";

        // act, assert
        assertThat(count(GRAPHQL, "operations", operations), is(2));
        assertThat(count(GRAPHQL, "fragments", operations), is(1));
    }

    @Test
    public void graphql_schemaDefinitionAndNestedArgumentParentheses_countOnlyTheFields() {
        // arrange
        final String schema = "schema @link(url: \"https://specs.apollo.dev/federation/v2.3\") {\n"
                + "  query: Query\n"
                + "  mutation: Mutation\n"
                + "}\n"
                + "type Query {\n"
                + "  orders(filter: OrderFilter @deprecated(reason: \"x\"), first: Int): [Order]\n"
                + "  query: String\n"
                + "}\n";

        // act, assert
        // orders and the field named query; not the root operations of the schema definition
        assertThat(count(GRAPHQL, "fields (approx)", schema), is(2));
        assertThat(count(GRAPHQL, "directive uses", schema), is(2));
    }

    @Test
    public void graphql_unclosedBlockString_runsToTheEnd() {
        // act, assert
        assertThat(count(GRAPHQL, "object types", "type A { x: Int }\n\"\"\"\ntype B { y: Int }\n"), is(1));
    }
    // </editor-fold>
}
