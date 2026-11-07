// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.FactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the SQL fact counters of the current profile ({@code profiles/v3/sql-facts.tsv}, read
 * through {@link ProfileFactPatterns}, so the shipped file is what is tested) on PostgreSQL, MySQL and
 * T-SQL shapes, in the real {@link java.util.regex} engine {@code FactExtractor} uses. Each counter
 * gets the forms it has to see and the near misses it must not count; the approximate one ("VALUES
 * rows (approx)") gets its known over- and undercount pinned, so a change to it is a decision. The
 * {@code [ignore]} line reads the file as a lexer does; the last tests pin its regions and that a
 * long blanked region stays linear.
 */
public class SqlFactPatternsTest {

    private static final List<FactCounter> COUNTERS = ProfileFactPatterns.counters("sql-facts");

    /** Lines of the long inputs of the run-time tests. */
    private static final int LONG_INPUT_LINES = 40_000;

    /** What a long input may take; the quadratic patterns these tests replaced took about 44 s. */
    private static final Duration LONG_INPUT_LIMIT = Duration.ofSeconds(10);

    private static int count(final String label, final String sql) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(COUNTERS, sql);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="profile">
    @Test
    public void profile_counterLabels_areTheseInThisOrder() {
        // act, assert
        assertThat(
                new ArrayList<>(ProfileFactPatterns.counts(COUNTERS, "").keySet()),
                is(Arrays.asList(
                        "INSERT statements",
                        "VALUES rows (approx)",
                        "tables",
                        "views",
                        "indexes",
                        "functions/procedures",
                        "triggers",
                        "types",
                        "ALTER TABLE",
                        "sequences",
                        "extensions",
                        "schemas and databases",
                        "packages and package bodies")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="INSERT statements">
    @Test
    public void insertStatements_oneSingleAndOneMultiRowInsert_countTwo() {
        // act, assert
        assertThat(
                count("INSERT statements", "INSERT INTO a (x) VALUES (1);\ninsert into b (x) values\n  (1),\n  (2);\n"),
                is(2));
    }

    @Test
    public void insertStatements_indentedInsideARoutine_counts() {
        // act, assert
        assertThat(count("INSERT statements", "BEGIN\n    INSERT INTO log (msg) VALUES ('x');\nEND;\n"), is(1));
    }

    @Test
    public void insertStatements_mergeInsertBranchAndTheWordInAString_doNotCount() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "MERGE t USING s ON t.id = s.id\n"
                                + "WHEN NOT MATCHED THEN INSERT (id) VALUES (s.id);\n"
                                + "SELECT 'INSERT INTO x' AS text;\n"),
                is(0));
    }

    @Test
    public void insertStatements_mySqlIgnoreAndReplaceForms_count() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "INSERT IGNORE INTO t VALUES (1);\nREPLACE INTO t VALUES (1);\n"
                                + "INSERT LOW_PRIORITY INTO t VALUES (1);\n"),
                is(3));
    }

    @Test
    public void insertStatements_insideABlockComment_doNotCount() {
        // act, assert
        assertThat(
                count("INSERT statements", "/*\nINSERT INTO old VALUES (1);\n*/\nINSERT INTO t VALUES (1);\n"), is(1));
    }

    @Test
    public void insertStatements_afterAnInlineBlockComment_count() {
        // act, assert
        assertThat(count("INSERT statements", "/* seed */ INSERT INTO t VALUES (1);\n"), is(1));
    }

    @Test
    public void insertStatements_withoutInto_count() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "INSERT t VALUES (1);\nINSERT IGNORE t VALUES (1);\nINSERT dbo.T (a) VALUES (1);\n"
                                + "INSERT #tmp SELECT 1;\nINSERT @tab VALUES (1);\nINSERT [dbo].[T] VALUES (1);\n"),
                is(6));
    }

    @Test
    public void insertStatements_oracleHintInsertAllAndInsertFirst_count() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "INSERT /*+ APPEND */ INTO t SELECT * FROM s;\n"
                                + "INSERT /*+ APPEND PARALLEL(t, 4) */ INTO t SELECT * FROM s;\n"
                                + "INSERT ALL\n  INTO a VALUES (1)\n  INTO b VALUES (2)\nSELECT * FROM dual;\n"
                                + "INSERT FIRST\n  WHEN x > 1 THEN INTO a VALUES (x)\n  ELSE INTO b VALUES (x)\n"
                                + "SELECT x FROM s;\n"),
                is(4));
    }

    @Test
    public void insertStatements_mergeBranchAndTriggerEventListOnTheirOwnLine_doNotCount() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "MERGE t USING s ON t.id = s.id\nWHEN NOT MATCHED THEN\n    INSERT (id) VALUES (s.id);\n"
                                + "CREATE TRIGGER trg BEFORE\n  INSERT OR UPDATE ON t\n  FOR EACH ROW EXECUTE FUNCTION f();\n"
                                + "CREATE TRIGGER trg2 AFTER\n  INSERT ON t\n  FOR EACH ROW EXECUTE FUNCTION f();\n"),
                is(0));
    }

    @Test
    public void insertStatements_withoutIntoInAStringOrAComment_doNotCount() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "SELECT 'INSERT t VALUES (1)' AS s;\n-- INSERT t VALUES (2)\nINSERT t VALUES (3);\n"),
                is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="comments and string literals">
    @Test
    public void ignore_blockCommentOpenerInAStringLiteral_opensNothing() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "INSERT INTO t VALUES ('/*');\nINSERT INTO t VALUES (2);\nINSERT INTO t VALUES (3);\n"
                                + "/* real */\n"),
                is(3));
    }

    @Test
    public void ignore_blockCommentOpenerInADashComment_opensNothing() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "-- TODO /* fix\nINSERT INTO t VALUES (1);\nINSERT INTO t VALUES (2);\n/* real */\n"),
                is(2));
    }

    @Test
    public void ignore_doubledAndBackslashEscapedQuotes_stayInsideTheLiteral() {
        // arrange
        final String sql = "INSERT INTO t VALUES ('O\\'Brien /* x'),('b');\nINSERT INTO t VALUES ('it''s */ ok');\n";

        // act, assert
        assertThat(count("INSERT statements", sql), is(2));
        assertThat(count("VALUES rows (approx)", sql), is(3));
    }

    /**
     * T-SQL, Oracle and standard PostgreSQL end a literal at its first quote, so {@code '\'} is a whole
     * literal: a backslash before a quote that a delimiter follows escapes nothing, or the rest of the
     * file would be blanked.
     */
    @Test
    public void ignore_aBackslashBeforeAQuoteADelimiterFollows_endsTheLiteral() {
        // arrange
        final String sql = "CREATE TABLE files (id INT, path NVARCHAR(200));\nGO\n"
                + "CREATE VIEW v AS SELECT REPLACE(path, '\\', '/') AS p FROM files;\nGO\n"
                + "CREATE TABLE logs (id INT);\nINSERT INTO logs VALUES (1);\nINSERT INTO logs VALUES (2);\n"
                + "CREATE PROCEDURE p AS SELECT 1;\n";

        // act, assert
        assertThat(count("INSERT statements", sql), is(2));
        assertThat(count("VALUES rows (approx)", sql), is(2));
        assertThat(count("tables", sql), is(2));
        assertThat(count("functions/procedures", sql), is(1));
    }

    @Test
    public void ignore_anEscapeClauseAndADriveRoot_closeTheirLiterals() {
        // arrange
        final String next = "\nINSERT INTO t VALUES ('a');\nINSERT INTO t VALUES (2);\n";

        // act, assert
        assertThat(count("INSERT statements", "SELECT n FROM t WHERE n LIKE 'a\\_%' ESCAPE '\\';" + next), is(2));
        assertThat(count("INSERT statements", "SELECT n FROM t WHERE n LIKE 'a\\_%' ESCAPE '\\'" + next), is(2));
        assertThat(count("INSERT statements", "SELECT 'C:\\', 1;" + next), is(2));
        assertThat(count("INSERT statements", "SET @p = 'C:\\' + @name;" + next), is(2));
        assertThat(count("INSERT statements", "SELECT 'C:\\' || name FROM t;" + next), is(2));
        assertThat(count("INSERT statements", "SELECT f('C:\\' );" + next), is(2));
    }

    /** MySQL's escaped quote stays inside its literal, before a letter and before a blank and a word. */
    @Test
    public void ignore_mySqlEscapedQuotesBeforeALetterOrAWord_stayInsideTheLiteral() {
        // arrange
        final String next = "\nINSERT INTO t VALUES ('a');\nINSERT INTO t VALUES (2);\n";

        // act, assert
        assertThat(count("INSERT statements", "INSERT INTO t VALUES ('it\\'s', 'x');" + next), is(3));
        assertThat(count("VALUES rows (approx)", "INSERT INTO t VALUES ('it\\'s', 'x');" + next), is(3));
        assertThat(count("INSERT statements", "SELECT 'say \\'hi\\' now';" + next), is(2));
    }

    @Test
    public void ignore_dashesInAStringLiteral_areNoComment() {
        // act, assert
        assertThat(
                count("INSERT statements", "INSERT INTO t VALUES ('a -- b'), ('c');\nINSERT INTO t VALUES ('d');\n"),
                is(2));
    }

    /**
     * A string with an apostrophe in another dialect's quoting opens no '...' literal that would run over
     * the next statements: PostgreSQL's dollar quoting on one line, Oracle's q'[...]', a MySQL "..."
     * string and a MySQL # comment.
     */
    @Test
    public void ignore_anApostropheInsideOtherQuotingOrAHashComment_blanksNoStatement() {
        // arrange
        final String next = "\nINSERT INTO t VALUES ('a');\nINSERT INTO t VALUES (2);\n";

        // act, assert
        assertThat(count("INSERT statements", "SELECT $$it's$$;" + next), is(2));
        assertThat(count("INSERT statements", "SELECT $fn$it's$fn$;" + next), is(2));
        assertThat(count("INSERT statements", "SELECT q'[it's]' FROM dual;" + next), is(2));
        assertThat(count("INSERT statements", "SELECT Q'{it's}', nq'!x's!' FROM dual;" + next), is(2));
        assertThat(count("INSERT statements", "SELECT \"it's\";" + next), is(2));
        assertThat(count("INSERT statements", "# it's a comment" + next), is(2));
        assertThat(count("INSERT statements", "SELECT 1; # it's late" + next), is(2));
    }

    /**
     * SQL*Plus reads a REM[ARK] or PRO[MPT] line as a command of its own, not as SQL, so its apostrophe
     * opens no '...' literal over the statements below.
     */
    @Test
    public void ignore_anApostropheInASqlPlusRemarkOrPrompt_blanksNoStatement() {
        // arrange
        final String sql = "REM Creates the shop's schema\n"
                + "PROMPT Don't run this twice\n"
                + "CREATE TABLE a (id NUMBER);\n"
                + "CREATE TABLE b (id NUMBER);\n"
                + "CREATE INDEX a_id ON a (id);\n";
        final String abbreviated = "rem it's\n  pro Don't\nRemark o'clock\nprompt\nCREATE TABLE c (x int);\n";

        // act, assert
        assertThat(count("tables", sql), is(2));
        assertThat(count("indexes", sql), is(1));
        // lower case, indented, abbreviated, spelled out and without text
        assertThat(count("tables", abbreviated), is(1));
    }

    /**
     * psql reads the rows after a COPY ... FROM stdin up to the line {@code \.} as data, so an apostrophe
     * in a pg_dump's rows opens no '...' literal over the constraints and indexes below; without its
     * {@code \.} the data runs to the end of the file, as psql reads it.
     */
    @Test
    public void ignore_anApostropheInTheRowsOfACopyFromStdin_blanksNoStatement() {
        // arrange
        final String sql = "CREATE TABLE public.customer (id integer, name text);\n"
                + "COPY public.customer (id, name) FROM stdin;\n"
                + "1\tO'Brien\n"
                + "2\tSmith\n"
                + "\\.\n"
                + "\n"
                + "ALTER TABLE ONLY public.customer ADD CONSTRAINT customer_pkey PRIMARY KEY (id);\n"
                + "CREATE INDEX customer_name ON public.customer USING btree (name);\n";
        final String fromAFile = "COPY t FROM '/data/it.csv' WITH (FORMAT csv);\nCREATE TABLE a (x int);\n";
        final String unclosed = "COPY t (x) FROM STDIN;\nCREATE TABLE a (x int);\n";

        // act, assert
        assertThat(count("tables", sql), is(1));
        assertThat(count("ALTER TABLE", sql), is(1));
        assertThat(count("indexes", sql), is(1));
        assertThat(count("tables", fromAFile), is(1));
        assertThat(count("tables", unclosed), is(0));
    }

    /**
     * The closing quote of a quoted identifier opens no "..." string: before a PostgreSQL JSON literal it
     * would end at the literal's first {@code "} and leave its apostrophe to open a '...' literal over
     * the rest of the file.
     */
    @Test
    public void ignore_aQuotedIdentifierBeforeAJsonLiteral_blanksNoStatement() {
        // arrange
        final String sql = "INSERT INTO \"cfg\" (\"data\") VALUES ('{\"k\": \"v\"}');\n"
                + "CREATE TABLE audit (id int);\n"
                + "CREATE INDEX audit_id ON audit (id);\n"
                + "INSERT INTO audit VALUES (1);\n";

        // act, assert
        assertThat(count("INSERT statements", sql), is(2));
        assertThat(count("VALUES rows (approx)", sql), is(2));
        assertThat(count("tables", sql), is(1));
        assertThat(count("indexes", sql), is(1));
        // a MySQL "..." string with an apostrophe after an opening parenthesis or a comma still blanks
        assertThat(count("INSERT statements", "SELECT f(\"it's\", \"x's\");\nINSERT INTO t VALUES (1);\n"), is(1));
    }

    /**
     * What the dialect quoting must leave: a quoted identifier names the INSERT's table, a T-SQL temp
     * table starts with #, and a function body in dollar quotes is SQL whose statements count.
     */
    @Test
    public void ignore_quotedIdentifiersTempTablesAndDollarQuotedBodies_stay() {
        // act, assert
        assertThat(count("INSERT statements", "INSERT \"tbl\" VALUES (1);\n"), is(1));
        assertThat(
                count("VALUES rows (approx)", "INSERT INTO #tmp VALUES (1), (2);\nINSERT INTO ##g VALUES (3);\n"),
                is(3));
        assertThat(
                count(
                        "INSERT statements",
                        "CREATE FUNCTION f() RETURNS void AS $$\nBEGIN\n  INSERT INTO log VALUES (1);\nEND;\n$$"
                                + " LANGUAGE plpgsql;\n"),
                is(1));
    }

    /** MySQL's executable comment holds SQL, which counts; a plain comment's does not. */
    @Test
    public void ignore_aMultiLineMySqlVersionComment_counts() {
        // act, assert
        assertThat(
                count("views", "/*!50001\nCREATE VIEW v AS SELECT 1\n*/;\n/*\nCREATE VIEW w AS SELECT 2\n*/\n"), is(1));
    }

    @Test
    public void ignore_unclosedBlockComment_commentsOutTheRest() {
        // act, assert
        assertThat(
                count("INSERT statements", "INSERT INTO t VALUES (1);\n/* never closed\nINSERT INTO t VALUES (2);\n"),
                is(1));
    }

    @Test
    public void ignore_unclosedStringLiteral_commentsOutTheRest() {
        // act, assert
        assertThat(
                count(
                        "INSERT statements",
                        "INSERT INTO t VALUES (1);\nSELECT 'never closed;\nINSERT INTO t VALUES (2);\n"),
                is(1));
    }

    @Test
    public void ignore_manyEscapedQuotes_stayLinear() {
        // arrange
        final String sql = FactPatternLinearity.repeat("\\'s\n", LONG_INPUT_LINES) + "INSERT INTO t VALUES (1);\n";

        // act, assert
        // the first quote opens a literal the escaped ones never close
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("INSERT statements", sql)), is(0));
    }

    @Test
    public void ignore_mySqlVersionCommentAndOracleHint_stay() {
        // arrange
        final String sql = "/*!40101 SET NAMES utf8 */;\n/*!50001 CREATE VIEW v AS SELECT 1 */;\n"
                + "/* CREATE TABLE fake (x int); */\nCREATE TABLE real_t (x int);\n";

        // act, assert
        assertThat(count("tables", sql), is(1));
        // the version comment's view does not start its line, so it is not seen either way
        assertThat(count("views", sql), is(0));
    }

    @Test
    public void ignore_longBlockComment_staysLinear() {
        // arrange
        final StringBuilder sql = new StringBuilder("/*\n");
        for (int i = 0; i < LONG_INPUT_LINES; i++) {
            sql.append("INSERT INTO old VALUES (").append(i).append(");\n");
        }
        sql.append("*/\nINSERT INTO t VALUES (1);\n");

        // act, assert
        assertThat(
                assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("INSERT statements", sql.toString())), is(1));
    }

    @Test
    public void ignore_manyBlockCommentOpenersInStringLiterals_stayLinear() {
        // arrange
        final StringBuilder sql = new StringBuilder();
        for (int i = 0; i < LONG_INPUT_LINES; i++) {
            sql.append("INSERT INTO routes (p) VALUES ('/api/*');\n");
        }

        // act, assert
        assertThat(
                assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("INSERT statements", sql.toString())),
                is(LONG_INPUT_LINES));
    }

    @Test
    public void statements_afterManyBlankLines_stayLinear() {
        // arrange
        final StringBuilder sql = new StringBuilder();
        for (int i = 0; i < LONG_INPUT_LINES; i++) {
            sql.append('\n');
        }
        sql.append("  CREATE TABLE t (x int);\n");

        // act, assert
        assertThat(assertTimeoutPreemptively(LONG_INPUT_LIMIT, () -> count("tables", sql.toString())), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="VALUES rows (approx)">
    @Test
    public void valuesRows_multiRowInsert_countsEveryRow() {
        // act, assert
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
        // act, assert
        assertThat(count("VALUES rows (approx)", "insert into t values(1),(2), (3);"), is(3));
    }

    @Test
    public void valuesRows_singleRowInsertsWithFunctionCalls_countOnePerInsert() {
        // act, assert
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO a (id, at) VALUES (1, now());\n"
                                + "INSERT INTO a (id, at) VALUES (2, coalesce(null, now()));\n"),
                is(2));
    }

    @Test
    public void valuesRows_rowsAnInsertSelectReadsFromAValuesList_count() {
        // act, assert
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
        // act, assert
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO users (name, mail)\nVALUES (\n    'admin',\n    'admin@example.org'\n);\n"),
                is(1));
    }

    @Test
    public void valuesRows_insertSelectFromATable_isNotSeen() {
        // act, assert
        assertThat(count("VALUES rows (approx)", "INSERT INTO archive (id) SELECT id FROM loan WHERE done;"), is(0));
    }

    /** VALUES(col) of MySQL's ON DUPLICATE KEY UPDATE reads the inserted value; it is no row. */
    @Test
    public void valuesRows_theValuesFunctionOfOnDuplicateKeyUpdate_isNoRow() {
        // act, assert
        assertThat(
                count(
                        "VALUES rows (approx)",
                        "INSERT INTO t (a, b) VALUES (1, 2), (3, 4)\nON DUPLICATE KEY UPDATE a = VALUES(a), b=VALUES(b);\n"),
                is(2));
    }

    @Test
    public void valuesRows_tupleNestedInARow_countsOnceMore() {
        // act, assert
        // the accepted overcount: "), (" inside a row starts no row, but is counted as one
        assertThat(count("VALUES rows (approx)", "INSERT INTO t (a, b) VALUES (f(1), (SELECT 2));"), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="tables">
    @Test
    public void tables_plainIfNotExistsQuotedAndSchemaQualified_count() {
        // act, assert
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
    public void tables_mariaDbOrReplaceAndPostgresForeign_count() {
        // act, assert
        assertThat(
                count(
                        "tables",
                        "CREATE OR REPLACE TABLE a (x int);\nCREATE FOREIGN TABLE f (x int) SERVER s;\n"
                                + "CREATE FOREIGN DATA WRAPPER w;\n"),
                is(2));
    }

    @Test
    public void tables_temporaryAndUnlogged_count() {
        // act, assert
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
        // act, assert
        assertThat(
                count("tables", "ALTER TABLE a ADD COLUMN b INT;\nDROP TABLE c;\n-- CREATE TABLE d is not run\n"),
                is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="views">
    @Test
    public void views_plainOrReplaceOrAlterAndMaterialized_count() {
        // act, assert
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
    public void views_oracleForceEditionable_counts() {
        // act, assert
        assertThat(
                count("views", "CREATE OR REPLACE FORCE EDITIONABLE VIEW \"S\".\"V\" AS SELECT 1 FROM dual;\n"), is(1));
    }

    @Test
    public void views_refreshAndDrop_doNotCount() {
        // act, assert
        assertThat(count("views", "REFRESH MATERIALIZED VIEW d;\nDROP VIEW a;\n"), is(0));
    }

    @Test
    public void views_mySqlAlgorithmDefinerAndSqlSecurity_count() {
        // act, assert
        assertThat(
                count(
                        "views",
                        "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW v1 AS SELECT 1;\n"
                                + "CREATE OR REPLACE ALGORITHM = MERGE VIEW v2 AS SELECT 1;\n"
                                + "CREATE DEFINER=`app`@`%` VIEW v3 AS SELECT 1;\n"
                                + "CREATE SQL SECURITY INVOKER VIEW v4 AS SELECT 1;\n"),
                is(4));
    }

    @Test
    public void views_materializedViewLog_isNoView() {
        // act, assert
        assertThat(
                count(
                        "views",
                        "CREATE MATERIALIZED VIEW LOG ON t WITH ROWID;\nCREATE MATERIALIZED VIEW mv AS SELECT 1;\n"),
                is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="indexes">
    @Test
    public void indexes_plainUniqueIfNotExistsConcurrentlyAndGin_count() {
        // act, assert
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
        // act, assert
        assertThat(
                count(
                        "indexes",
                        "CREATE CLUSTERED INDEX a ON dbo.T (X);\nCREATE UNIQUE NONCLUSTERED INDEX b ON dbo.T (Y);\n"),
                is(2));
    }

    @Test
    public void indexes_tSqlColumnstore_count() {
        // act, assert
        assertThat(
                count(
                        "indexes",
                        "CREATE CLUSTERED COLUMNSTORE INDEX cci ON t;\nCREATE NONCLUSTERED COLUMNSTORE INDEX n ON t (a);\n"
                                + "CREATE COLUMNSTORE INDEX c ON t (a);\n"),
                is(3));
    }

    @Test
    public void indexes_fulltextSpatialAndBitmap_count() {
        // act, assert
        assertThat(
                count(
                        "indexes",
                        "CREATE FULLTEXT INDEX a ON t (x);\nCREATE SPATIAL INDEX b ON t (g);\n"
                                + "CREATE BITMAP INDEX c ON t (y);\n"),
                is(3));
    }

    @Test
    public void indexes_inlineKeyOfAMySqlTable_isNotAStatement() {
        // act, assert
        assertThat(
                count("indexes", "CREATE TABLE t (\n  id INT,\n  KEY idx_t_id (id),\n  INDEX idx_t_x (id)\n);\n"),
                is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="functions/procedures">
    @Test
    public void functionsProcedures_postgresMySqlAndTSqlForms_count() {
        // act, assert
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
    public void functionsProcedures_oracleEditionableForms_count() {
        // act, assert
        assertThat(
                count(
                        "functions/procedures",
                        "CREATE OR REPLACE EDITIONABLE PROCEDURE p AS BEGIN NULL; END;\n/\n"
                                + "CREATE OR REPLACE NONEDITIONABLE FUNCTION f RETURN NUMBER AS BEGIN RETURN 1; END;\n/\n"),
                is(2));
    }

    @Test
    public void functionsProcedures_callsAndDrops_doNotCount() {
        // act, assert
        assertThat(count("functions/procedures", "CALL c(1);\nEXEC dbo.e;\nDROP FUNCTION a;\nSELECT b();\n"), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="triggers">
    @Test
    public void triggers_postgresMySqlAndTSqlForms_count() {
        // act, assert
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
        // arrange
        final String sql =
                "CREATE FUNCTION audit_fn() RETURNS trigger AS $$ BEGIN RETURN NEW; END $$ LANGUAGE plpgsql;\n";

        // act, assert
        assertThat(count("triggers", sql), is(0));
        assertThat(count("functions/procedures", sql), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="types and ALTER TABLE">
    @Test
    public void types_enumAndCompositeTypes_count() {
        // act, assert
        assertThat(
                count(
                        "types",
                        "CREATE TYPE status AS ENUM ('ACTIVE', 'RETIRED');\nCREATE TYPE pair AS (a INT, b INT);\n"),
                is(2));
    }

    @Test
    public void types_aColumnOfAType_doesNotCount() {
        // act, assert
        assertThat(count("types", "CREATE TABLE t (s status NOT NULL);\nALTER TYPE status ADD VALUE 'LOST';\n"), is(0));
    }

    @Test
    public void alterTable_everyStatement_counts() {
        // act, assert
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
        // act, assert
        assertThat(count("ALTER TABLE", "ALTER INDEX a RENAME TO b;\nALTER VIEW v RENAME TO w;\n"), is(0));
    }

    @Test
    public void types_oracleOrReplaceAndEditionable_countAndTheTypeBodyDoesNot() {
        // act, assert
        assertThat(
                count(
                        "types",
                        "CREATE OR REPLACE TYPE addr_t AS OBJECT (street VARCHAR2(80));\n/\n"
                                + "CREATE OR REPLACE EDITIONABLE TYPE BODY addr_t AS\nEND;\n/\n"
                                + "CREATE OR REPLACE NONEDITIONABLE TYPE nums_t AS VARRAY(3) OF NUMBER;\n/\n"
                                + "CREATE TYPE status AS ENUM ('A');\n"),
                is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sequences, extensions, schemas and packages">
    @Test
    public void sequences_plainAndTemporary_countAndNextvalDoesNot() {
        // act, assert
        assertThat(
                count("sequences", "CREATE SEQUENCE s START 1;\nCREATE TEMP SEQUENCE t;\nSELECT nextval('s');\n"),
                is(2));
    }

    @Test
    public void extensions_createAndDropExtension_countOnlyTheCreate() {
        // act, assert
        assertThat(count("extensions", "CREATE EXTENSION IF NOT EXISTS pgcrypto;\nDROP EXTENSION hstore;\n"), is(1));
    }

    @Test
    public void schemasAndDatabases_both_count() {
        // act, assert
        assertThat(
                count(
                        "schemas and databases",
                        "CREATE SCHEMA app;\nCREATE DATABASE x;\nCREATE SCHEMA IF NOT EXISTS s AUTHORIZATION u;\n"),
                is(3));
    }

    @Test
    public void packagesAndPackageBodies_oracleSpecAndBody_count() {
        // act, assert
        assertThat(
                count(
                        "packages and package bodies",
                        "CREATE OR REPLACE PACKAGE emp_pkg AS\nEND;\n/\n"
                                + "CREATE OR REPLACE EDITIONABLE PACKAGE BODY emp_pkg AS\nEND;\n/\n"),
                is(2));
    }

    @Test
    public void schemasAndDatabases_linkScopedCredentialAuditAndEncryptionKey_doNotCount() {
        // act, assert
        assertThat(
                count(
                        "schemas and databases",
                        "CREATE DATABASE LINK dl CONNECT TO u IDENTIFIED BY p USING 'x';\n"
                                + "CREATE DATABASE SCOPED CREDENTIAL c WITH IDENTITY = 'x';\n"
                                + "CREATE DATABASE AUDIT SPECIFICATION s FOR SERVER AUDIT a;\n"
                                + "CREATE DATABASE ENCRYPTION KEY WITH ALGORITHM = AES_256 ENCRYPTION BY SERVER CERTIFICATE c;\n"),
                is(0));
    }
    // </editor-fold>
}
