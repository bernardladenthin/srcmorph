// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Validates the SQL fact counters of the current profile ({@code profiles/v2/sql-facts.tsv}, read
 * through {@link ProfileFactPatterns}, so the shipped file is what is tested) on PostgreSQL, MySQL and
 * T-SQL shapes, in the real {@link java.util.regex} engine {@code AiFactExtractor} uses. Each counter
 * gets the forms it has to see and the near misses it must not count; the approximate one ("VALUES
 * rows (approx)") gets its known over- and undercount pinned, so a change to it is a decision.
 */
public class SqlFactPatternsTest {

    private static final Map<String, String> PATTERNS = ProfileFactPatterns.load("sql-facts");

    private static int count(final String label, final String sql) {
        return ProfileFactPatterns.count(ProfileFactPatterns.pattern(PATTERNS, label), sql);
    }

    @Test
    public void profile_hasTheseCountersInThisOrder() {
        assertThat(
                new ArrayList<>(PATTERNS.keySet()),
                is(Arrays.asList(
                        "INSERT statements",
                        "VALUES rows (approx)",
                        "tables",
                        "views",
                        "indexes",
                        "functions/procedures",
                        "triggers",
                        "types",
                        "ALTER TABLE")));
    }

    // <editor-fold defaultstate="collapsed" desc="INSERT statements">
    @Test
    public void insertStatements_oneSingleAndOneMultiRowInsert_countTwo() {
        assertThat(
                count("INSERT statements", "INSERT INTO a (x) VALUES (1);\ninsert into b (x) values\n  (1),\n  (2);\n"),
                is(2));
    }

    @Test
    public void insertStatements_indentedInsideARoutine_counts() {
        assertThat(count("INSERT statements", "BEGIN\n    INSERT INTO log (msg) VALUES ('x');\nEND;\n"), is(1));
    }

    @Test
    public void insertStatements_mergeInsertBranchAndTheWordInAString_doNotCount() {
        assertThat(
                count(
                        "INSERT statements",
                        "MERGE t USING s ON t.id = s.id\n"
                                + "WHEN NOT MATCHED THEN INSERT (id) VALUES (s.id);\n"
                                + "SELECT 'INSERT INTO x' AS text;\n"),
                is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="VALUES rows (approx)">
    @Test
    public void valuesRows_multiRowInsert_countsEveryRow() {
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO branch (name, city) VALUES\n"
                                + "    ('North', 'A'),\n"
                                + "    ('South', 'B'),\n"
                                + "    ('East', 'C');\n"),
                is(3));
    }

    @Test
    public void valuesRows_rowsOnOneLineAndWithoutSpaces_count() {
        assertThat(count("VALUES rows (approx)", "insert into t values(1),(2), (3);"), is(3));
    }

    @Test
    public void valuesRows_singleRowInsertsWithFunctionCalls_countOnePerInsert() {
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO a (id, at) VALUES (1, now());\n"
                                + "INSERT INTO a (id, at) VALUES (2, coalesce(null, now()));\n"),
                is(2));
    }

    @Test
    public void valuesRows_rowsAnInsertSelectReadsFromAValuesList_count() {
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO kind (name, group_id)\n"
                                + "SELECT name, (SELECT id FROM grp WHERE name = 'IT')\n"
                                + "FROM (VALUES\n    ('Laptop'),\n    ('Monitor')\n) AS t(name);\n"),
                is(2));
    }

    @Test
    public void valuesRows_multiLineSingleRow_countsOne() {
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO users (name, mail)\nVALUES (\n    'admin',\n    'admin@example.org'\n);\n"),
                is(1));
    }

    @Test
    public void valuesRows_insertSelectFromATable_isNotSeen() {
        assertThat(count("VALUES rows (approx)", "INSERT INTO archive (id) SELECT id FROM loan WHERE done;"), is(0));
    }

    @Test
    public void valuesRows_tupleNestedInARow_countsOnceMore() {
        // the accepted overcount: "), (" inside a row starts no row, but is counted as one
        assertThat(count("VALUES rows (approx)", "INSERT INTO t (a, b) VALUES (f(1), (SELECT 2));"), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="tables">
    @Test
    public void tables_plainIfNotExistsQuotedAndSchemaQualified_count() {
        assertThat(
                count(
                        "tables",
                        "CREATE TABLE a (id INT);\n"
                                + "create table if not exists b (id int);\n"
                                + "CREATE TABLE \"C\" (id INT);\n"
                                + "CREATE TABLE dbo.D (Id INT);\n"
                                + "CREATE TABLE `e` (id INT) ENGINE=InnoDB;\n"),
                is(5));
    }

    @Test
    public void tables_temporaryAndUnlogged_count() {
        assertThat(
                count(
                        "tables",
                        "CREATE TEMP TABLE a (id INT);\n"
                                + "CREATE TEMPORARY TABLE b (id INT);\n"
                                + "CREATE GLOBAL TEMPORARY TABLE c (id INT);\n"
                                + "CREATE UNLOGGED TABLE d (id INT);\n"),
                is(4));
    }

    @Test
    public void tables_alterDropAndAComment_doNotCount() {
        assertThat(
                count("tables", "ALTER TABLE a ADD COLUMN b INT;\nDROP TABLE c;\n-- CREATE TABLE d is not run\n"),
                is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="views">
    @Test
    public void views_plainOrReplaceOrAlterAndMaterialized_count() {
        assertThat(
                count(
                        "views",
                        "CREATE VIEW a AS SELECT 1;\n"
                                + "CREATE OR REPLACE VIEW b AS SELECT 1;\n"
                                + "CREATE OR ALTER VIEW dbo.c AS SELECT 1;\n"
                                + "CREATE MATERIALIZED VIEW d AS SELECT 1;\n"),
                is(4));
    }

    @Test
    public void views_refreshAndDrop_doNotCount() {
        assertThat(count("views", "REFRESH MATERIALIZED VIEW d;\nDROP VIEW a;\n"), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="indexes">
    @Test
    public void indexes_plainUniqueIfNotExistsConcurrentlyAndGin_count() {
        assertThat(
                count(
                        "indexes",
                        "CREATE INDEX a ON t (x);\n"
                                + "CREATE UNIQUE INDEX b ON t (y);\n"
                                + "CREATE INDEX IF NOT EXISTS c ON t (z);\n"
                                + "CREATE INDEX CONCURRENTLY d ON t (w);\n"
                                + "CREATE INDEX e ON t USING GIN (name gin_trgm_ops);\n"),
                is(5));
    }

    @Test
    public void indexes_tSqlClusteredAndNonclustered_count() {
        assertThat(
                count(
                        "indexes",
                        "CREATE CLUSTERED INDEX a ON dbo.T (X);\nCREATE UNIQUE NONCLUSTERED INDEX b ON dbo.T (Y);\n"),
                is(2));
    }

    @Test
    public void indexes_inlineKeyOfAMySqlTable_isNotAStatement() {
        assertThat(
                count("indexes", "CREATE TABLE t (\n  id INT,\n  KEY idx_t_id (id),\n  INDEX idx_t_x (id)\n);\n"),
                is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="functions/procedures">
    @Test
    public void functionsProcedures_postgresMySqlAndTSqlForms_count() {
        assertThat(
                count(
                        "functions/procedures",
                        "CREATE FUNCTION a() RETURNS INT AS $$ SELECT 1 $$ LANGUAGE sql;\n"
                                + "CREATE OR REPLACE FUNCTION b() RETURNS INT AS $$ SELECT 1 $$ LANGUAGE sql;\n"
                                + "CREATE PROCEDURE c(IN p INT) BEGIN SELECT p; END;\n"
                                + "CREATE DEFINER=`app`@`%` PROCEDURE d() BEGIN END;\n"
                                + "CREATE PROC dbo.e AS SELECT 1;\n"
                                + "CREATE OR ALTER PROCEDURE dbo.f AS SELECT 1;\n"),
                is(6));
    }

    @Test
    public void functionsProcedures_callsAndDrops_doNotCount() {
        assertThat(count("functions/procedures", "CALL c(1);\nEXEC dbo.e;\nDROP FUNCTION a;\nSELECT b();\n"), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="triggers">
    @Test
    public void triggers_postgresMySqlAndTSqlForms_count() {
        assertThat(
                count(
                        "triggers",
                        "CREATE TRIGGER a AFTER INSERT ON t FOR EACH ROW EXECUTE FUNCTION f();\n"
                                + "CREATE OR REPLACE TRIGGER b BEFORE UPDATE ON t FOR EACH ROW EXECUTE FUNCTION f();\n"
                                + "CREATE CONSTRAINT TRIGGER c AFTER INSERT ON t FOR EACH ROW EXECUTE FUNCTION f();\n"
                                + "CREATE DEFINER=`app`@`%` TRIGGER d BEFORE INSERT ON t FOR EACH ROW SET @x = 1;\n"
                                + "CREATE OR ALTER TRIGGER dbo.e ON dbo.T AFTER INSERT AS SELECT 1;\n"),
                is(5));
    }

    @Test
    public void triggers_theTriggerFunctionItself_isAFunctionNotATrigger() {
        final String sql =
                "CREATE FUNCTION audit_fn() RETURNS trigger AS $$ BEGIN RETURN NEW; END $$ LANGUAGE plpgsql;\n";

        assertThat(count("triggers", sql), is(0));
        assertThat(count("functions/procedures", sql), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="types and ALTER TABLE">
    @Test
    public void types_enumAndCompositeTypes_count() {
        assertThat(
                count(
                        "types",
                        "CREATE TYPE status AS ENUM ('ACTIVE', 'RETIRED');\nCREATE TYPE pair AS (a INT, b INT);\n"),
                is(2));
    }

    @Test
    public void types_aColumnOfAType_doesNotCount() {
        assertThat(count("types", "CREATE TABLE t (s status NOT NULL);\nALTER TYPE status ADD VALUE 'LOST';\n"), is(0));
    }

    @Test
    public void alterTable_everyStatement_counts() {
        assertThat(
                count(
                        "ALTER TABLE",
                        "ALTER TABLE a ADD COLUMN b INT;\n"
                                + "alter table a drop column c;\n"
                                + "ALTER TABLE IF EXISTS a RENAME TO d;\n"
                                + "ALTER TABLE ONLY a ADD CONSTRAINT k UNIQUE (b);\n"),
                is(4));
    }

    @Test
    public void alterTable_alterOfOtherObjects_doesNotCount() {
        assertThat(count("ALTER TABLE", "ALTER INDEX a RENAME TO b;\nALTER VIEW v RENAME TO w;\n"), is(0));
    }
    // </editor-fold>
}
