# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Tests of score.py: what counts as a name found, an invented name and a failed summary.

Run from the repository root: python -m unittest discover -s docs/ai-index-benchmark/modelcard/tests
"""
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import score  # noqa: E402

LONG = " It reads the configuration and hands every rule to the engine that runs it."


def body(text):
    return "> Lead line." + LONG + "\n\n" + text


class NamesFoundTest(unittest.TestCase):

    def test_aFenceThatCopiesTheSource_doesNotCount(self):
        source = "public class Foo {\n    void bar() {}\n}\n"
        row = score.score_file(source, body("```java\npublic class Foo {\n    void bar() {}\n}\n```\n"),
                               ["Foo", "bar"], [])

        self.assertEqual(row["idsFound"], 0)
        self.assertEqual(row["fences"], 2)

    def test_aFenceOfTheModelsOwnText_counts(self):
        row = score.score_file("public class Foo {\n    void bar() {}\n}\n",
                               body("```markdown\n#### API\n- Foo: the type\n- bar() -> void\n```\n"), ["Foo", "bar"], [])

        self.assertEqual(row["idsFound"], 2)
        self.assertEqual(row["fences"], 2)

    def test_aTildeFenceThatCopiesTheSource_doesNotCount(self):
        source = "public class Foo {\n    void bar() {}\n}\n"
        row = score.score_file(source, body("~~~java\npublic class Foo {\n    void bar() {}\n}\n~~~\n"),
                               ["Foo", "bar"], [])

        self.assertEqual(row["idsFound"], 0)
        self.assertEqual(row["fences"], 2)

    def test_anIndentedCopyOfTheSource_doesNotCount(self):
        source = "public class Foo {\n    void bar() {}\n}\n"
        row = score.score_file(source, body("The code:\n\n    public class Foo {\n        void bar() {}\n    }\n"),
                               ["Foo", "bar"], [])

        self.assertEqual(row["idsFound"], 0)

    def test_anIndentedBlockOfTheModelsOwnText_counts(self):
        row = score.score_file("public class Foo {\n    void bar() {}\n}\n",
                               body("The API:\n\n    Foo is the type, bar() its one method\n"), ["Foo", "bar"], [])

        self.assertEqual(row["idsFound"], 2)

    def test_aCopyWithItsWhitespaceChanged_isACopy(self):
        source = "public class Foo {\n    void bar( int a ) {}\n}\n"
        row = score.score_file(source, body("```\npublic  class Foo{\nvoid bar(int a) {}\n}\n```\n"), ["Foo", "bar"], [])

        self.assertEqual(row["idsFound"], 0)

    def test_aSignatureList_isNoCopyOfTheDeclarations(self):
        source = "public class Foo {\n    public void bar(int a) {\n        a++;\n    }\n}\n"
        row = score.score_file(source, body("```\npublic void bar(int a)\n```\n"), ["bar"], [])

        self.assertEqual(row["idsFound"], 1)

    def test_anUnclosedCopy_runsToTheEnd(self):
        row = score.score_file("int Foo = 1;", body("```\nint Foo = 1;"), ["Foo"], [])

        self.assertEqual(row["idsFound"], 0)

    def test_anEverydayWord_countsOnlyMarkedAsCode(self):
        words = ["author", "book", "member", "loan", "status", "main"]
        prose = score.score_file("", body("The member borrows a book by an author; the loan has a status."),
                                 words, [], everyday=words)
        marked = score.score_file("", body("- author: `author_id` PK\n- **book**: the copies\n"
                                           "Calls `member` and main() and status (x).\n1. loan: dates\n"),
                                  words, [], everyday=words)

        self.assertEqual(prose["idsFound"], 0)
        # "status (x)" is a call too; the list labels, bold, backticks and calls all count
        self.assertEqual(marked["idsFound"], 6)

    def test_anEverydayWord_qualifiedOrAsAnOptionOrVariable(self):
        words = ["loan", "partial", "currency", "status", "book"]
        row = score.score_file("", body("Joins loan.member_id, removes .partial files, reads --currency and "
                                        "${status}; a book. Then more."), words, [], everyday=words)

        # "a book." ends a sentence
        self.assertEqual(row["idsFound"], 4)

    def test_anEverydayWord_inAnotherWordsBackticks_doesNotCount(self):
        row = score.score_file("", body("Key `author_id` only."), ["author"], [], everyday=["author"])

        self.assertEqual(row["idsFound"], 0)

    def test_anEverydayWord_betweenTwoBacktickSpans_doesNotCount(self):
        # the text between the closing backtick of one span and the opening one of the next is prose
        row = score.score_file("", body("Reads `a` and the author of `b`."), ["author"], [], everyday=["author"])

        self.assertEqual(row["idsFound"], 0)

    def test_anEverydayWord_inAColumnOrFieldListing_counts(self):
        words = ["email", "status", "name"]
        row = score.score_file("", body("- email VARCHAR(254) NOT NULL\n- status: integer, defaults to 0\n"
                                        "Columns name (TEXT) and more."), words, [], case_insensitive=True,
                               everyday=words)

        self.assertEqual(row["idsFound"], 3)

    def test_anIdentifier_countsAsAWholeWordInProse(self):
        row = score.score_file("", body("It holds the ledger_customer, the SrcMorphConfiguration and malloc."),
                               ["ledger_customer", "SrcMorphConfiguration", "malloc", "ledger"], [])

        # "ledger" is only part of ledger_customer
        self.assertEqual(row["idsFound"], 3)

    def test_aPathLikeId_isASubstring(self):
        row = score.score_file("", body("It uses urn:example:catalog throughout."), ["urn:example:catalog"], [])

        self.assertEqual(row["idsFound"], 1)

    def test_aPathLikeId_isAWholeName_notThePrefixOfALongerOne(self):
        longer = score.score_file("", body("Cancels with `/orders/{orderId}/cancel`; looks up java:/jdbc/ShopDSX, "
                                           "the min-revenue-cap, erp.hostname and urn:example:statement:v2."),
                                  ["/orders", "/orders/{orderId}", "/orders/{orderId}/cancel", "jdbc/ShopDS",
                                   "min-revenue", "erp.host", "urn:example:statement"], [])
        whole = score.score_file("", body("Reads orders.incoming.queue. Takes --min-revenue and `java:/jdbc/ShopDS`; "
                                          "GET /orders, then /orders?page=2 and erp.host: the ERP on "
                                          "backoffice.shop.example:8443, with `com.fasterxml.jackson.core:"
                                          "jackson-databind:${jackson.version}`."),
                                 ["orders.incoming.queue", "min-revenue", "jdbc/ShopDS", "/orders", "erp.host",
                                  "backoffice.shop.example", "jackson-databind"], [])

        self.assertEqual(longer["idsFound"], 1)
        self.assertEqual(whole["idsFound"], 7)

    def test_aFenceMarkerInsideAProseLine_opensNoFence(self):
        row = score.score_file("", body("It answers in ```json blocks naming Foo and bar.\nThen Baz.\n"),
                               ["Foo", "bar", "Baz"], [])
        indented = score.score_file("", body("Run it:\n   ```sh\n   # rotate\n   ```\nThen Baz.\n"), ["Baz"], [])

        self.assertEqual(row["idsFound"], 3)
        self.assertEqual(row["fences"], 0)
        # up to three spaces in front still open and close a fence; its '#' line is no heading
        self.assertEqual((indented["fences"], indented["badHeadings"], indented["idsFound"]), (2, 0, 1))

    def test_aReasoningBlock_isNoPartOfTheSummarysMeasures(self):
        source = "class Foo { int bar; int baz; }"
        row = score.score_file(source, "<think>Foo has bar and baz, 61 of them, and `ghostName`." + LONG
                               + "\n# Plan\n```\nx\n```\n</think>\n> A small type", ["Foo", "bar", "baz"], [61])

        self.assertEqual((row["idsFound"], row["numbersStated"], row["ticked"]), (0, 0, 0))
        self.assertEqual((row["badHeadings"], row["fences"]), (0, 0))
        self.assertEqual(row["bodyBytes"], len("> A small type"))
        self.assertEqual(row["failed"], "too-short")
        self.assertTrue(row["lead"])
        # the leaks are counted on the whole body
        self.assertEqual(row["leaks"], 2)

    def test_sql_namesWithoutRegardToCase(self):
        row = score.score_file("CREATE TABLE author", body("Table `AUTHOR` and LEDGER_ACCOUNT."),
                               ["author", "ledger_account"], [], case_insensitive=True, everyday=["author"])

        self.assertEqual(row["idsFound"], 2)

    def test_aSkeletonOfTheModelsOwn_isNoCopyForItsBraces(self):
        # the class line and the closing brace are the source's; the two signatures are the model's own
        source = "public class Foo {\n    public void bar() {\n        run();\n    }\n}\n"
        row = score.score_file(source, body("```java\npublic class Foo {\n    String invent(int a);\n"
                                             "    void other();\n}\n```\n"), ["Foo"], [])

        self.assertEqual(row["idsFound"], 1)

    def test_anEverydayWordBeforeATypeWordOfTheProse_isNoColumn(self):
        words = ["author", "book", "loan", "stock"]
        prose = score.score_file("", body("It keeps the loan date, the book number and the author text."),
                                 words, [], case_insensitive=True, everyday=words)
        columns = score.score_file("", body("Columns loan DATE and author: text, then stock int not null."), words,
                                   [], case_insensitive=True, everyday=words)

        self.assertEqual(prose["idsFound"], 0)
        # a type in capitals, after a colon, or one that is no English word
        self.assertEqual(columns["idsFound"], 3)

    def test_aMiddleArea_countsAsThePrefixOfANameInRecallAndInTheMiddleCheck(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "java").mkdir(parents=True)
            (work / "java" / "E.java").write_text("public enum E { LEDGER_LOCK_02701, AUTH_00001 }", encoding="utf-8")
            (out / "java").mkdir(parents=True)
            (out / "java" / "E.java.ai.md").write_text(
                "h: 1\n---\n" + body("Constants `LEDGER_LOCK_02701..LEDGER_LOCK_03300` and the ledger_x ones."),
                encoding="utf-8")
            facts = {"java/E.java": {"ids": ["LEDGER_LOCK", "ledger"], "middle": ["LEDGER_LOCK"]}}

            row = next(r for r in score.score_model(out, work, facts) if r["file"] == "java/E.java")

        # LEDGER_LOCK names the area; "ledger" is no middle name, so not the prefix of ledger_x
        self.assertEqual((row["idsFound"], row["middleFound"]), (1, 1))

    def test_numbers_notInACopiedFence(self):
        row = score.score_file("VALUES (6000);", body("It inserts 4000 rows.\n```\nVALUES (6000);\n```\n"), [],
                               [4000, 6000])

        self.assertEqual(row["numbersStated"], 1)


class MachineLinesTest(unittest.TestCase):

    def test_theFactsAndCoverageLines_areNotTheModelsText(self):
        text = ("**Facts (exact, whole file):** tables: 3\n\n**Coverage:** 2 of 9 chunks summarized (sampled)\n\n"
                + body("Foo"))

        self.assertTrue(score.model_text(text).lstrip().startswith("> Lead line."))
        row = score.score_file("", text, ["tables"], [3])
        self.assertEqual(row["idsFound"], 0)
        self.assertEqual(row["numbersStated"], 0)
        self.assertTrue(row["lead"])

    def test_aBodyOfOnlyTheFactsLine_isEmpty(self):
        row = score.score_file("", "**Facts (exact, whole file):** tables: 3\n\n", [], [])

        self.assertEqual(row["failed"], "empty")


class InventedTest(unittest.TestCase):

    def test_aQualifiedName_countsAsItsLastPart(self):
        row = score.score_file("throw new NullPointerException(); class Foo", body("`java.lang.Foo` and `x.y.Bar`."),
                               [], [])

        self.assertEqual(row["ticked"], 2)
        self.assertEqual(row["inventedNames"], ["Bar"])

    def test_keywordsLibraryTypesEscapesAndPlaceholders_areNoNames(self):
        row = score.score_file("", body("`extends`, `implements`, `Iterator`, `\\tregion`, `YYYY-MM-DD`, `getX`."),
                               [], [])

        self.assertEqual(row["ticked"], 0)

    def test_aPieceInsideAWordPart_isNoNameOfTheSource(self):
        row = score.score_file("String getName() { return name; }", body("`getName` returns `get` of `ame`."), [], [])

        self.assertEqual(row["ticked"], 3)
        self.assertEqual(row["inventedNames"], ["ame"])

    def test_aPieceInsideAWordPart_isNoNameOfTheSource_inSqlEither(self):
        # matched without regard to case, but a word part still starts at an underscore or a capital
        row = score.score_file("CREATE TABLE customer_order (id INT, price_cents INT);",
                               body("`CUSTOMER_ORDER` holds `price_cents` and `Order`; not `custom`, `tome`, `rder`."),
                               [], [], case_insensitive=True)

        self.assertEqual(row["inventedNames"], ["custom", "rder", "tome"])

    def test_sqlTypesInSmallLetters_areNoNamesEither(self):
        row = score.score_file("CREATE TABLE t (id INT);", body("Columns `varchar` and `timestamp`, `t_x`."), [], [],
                               case_insensitive=True)

        self.assertEqual(row["ticked"], 1)
        self.assertEqual(row["inventedNames"], ["t_x"])

    def test_aFileName_isNoName(self):
        row = score.score_file("class Bar {}", body("Reads `srcmorph.yaml`, `pom.xml` and `docs/notes.md`; "
                                                    "calls `x.y.Bar` and `Ghost`."), [], [])

        self.assertEqual(row["ticked"], 2)
        self.assertEqual(row["inventedNames"], ["Ghost"])

    def test_runsOfWordParts_prefixesAndPlurals_nameTheSource(self):
        source = "size_t ringbuf_free_space(void); int DEFAULT_SIZE; class OrderService {}"
        row = score.score_file(source, body("`free_space`, `ringbuf_`, `DEFAULT_*`, `Service` and the "
                                            "`OrderServices`; not `ree_space`."), [], [])

        self.assertEqual(row["inventedNames"], ["ree_space"])

    def test_namesOfToolsAndProducts_areNoNames(self):
        row = score.score_file("", body("A JavaBean read from PostgreSQL."), [], [])

        self.assertEqual(row["ticked"], 0)

    def test_codeShapedWordsOfTheProse_areNames(self):
        source = "class OrderService { int order_total; }"
        row = score.score_file(source, body("The OrderService keeps order_total and orderTotal; a `ticked_name` "
                                            "counts once."), [], [])

        self.assertEqual(row["inventedNames"], ["orderTotal", "ticked_name"])
        self.assertEqual(row["ticked"], 4)

    def test_aQualifiedCodeShapedWord_countsAsItsLastPart(self):
        row = score.score_file("class OrderService {}", body("It calls app.OrderService and app.missingName."), [], [])

        self.assertEqual(row["inventedNames"], ["missingName"])

    def test_aWordPartAfterAnAcronym_namesTheSource(self):
        row = score.score_file("class XMLParser { HTTPServerConfig config; }",
                               body("`Parser`, `ServerConfig`, `XML`, `HTTP` and `LParser`."), [], [])

        self.assertEqual(row["inventedNames"], ["LParser"])

    def test_aMemberWithAFileExtension_isAName_aFileWithADirectoryOrAHyphenIsNot(self):
        # .tag, .cfg, .c, .ts are member names too (`item.tag`, `self.cfg`): a file only with a directory or a
        # stem no identifier has; a name before a parenthesis is a call, whatever its extension
        row = score.score_file("class Item { String tag; }",
                               body("Sets `item.tag` and `self.cfg`; reads `views/order.tag`, `my-order.tag`, "
                                    "`rotate.sh` and `05_seed.cfg`, and calls `response.json()`."), [], [])

        self.assertEqual(row["ticked"], 3)
        self.assertEqual(row["inventedNames"], ["cfg", "json"])

    def test_namesInACopiedFence_areNotTicked(self):
        row = score.score_file("x = `Invented`", body("```\nx = `Invented`\n```\n"), [], [])

        self.assertEqual(row["ticked"], 0)


class FailureTest(unittest.TestCase):

    def test_reasons(self):
        self.assertEqual(score.score_file("", "", [], [])["failed"], "empty")
        self.assertEqual(score.score_file("", "<think>planning the answer</think>", [], [])["failed"], "leak-only")
        self.assertEqual(score.score_file("", "I'm sorry, but I cannot summarize this file." + LONG, [], [])["failed"],
                         "refusal")
        self.assertEqual(score.score_file("", "> Too short.", [], [])["failed"], "too-short")
        self.assertEqual(score.score_file("", body("Fine."), [], [])["failed"], "")

    def test_aCompleteSummaryAfterAReasoningBlock_isNoFailureButALeak(self):
        row = score.score_file("", "<think>planning the answer</think>\n" + body("Fine."), [], [])

        self.assertEqual(row["failed"], "")
        self.assertTrue(row["lead"])
        self.assertEqual(row["leaks"], 2)

    def test_aCompleteSummaryAfterAHarmonyAnalysisBlock_isNoFailureButALeak(self):
        row = score.score_file("", "<|channel|>analysis<|message|>planning<|end|>"
                               "<|start|>assistant<|channel|>final<|message|>" + body("Fine."), [], [])

        self.assertEqual(row["failed"], "")
        self.assertTrue(row["lead"])
        self.assertEqual(row["leaks"], 6)

    def test_aCompleteSummaryAfterAGemmaThoughtBlock_isNoFailureButALeak(self):
        row = score.score_file("", "<|channel>thought\nplanning<channel|>" + body("Fine."), [], [])

        self.assertEqual(row["failed"], "")
        self.assertTrue(row["lead"])
        self.assertEqual(row["leaks"], 2)

    def test_aHashLineInAFence_isNoHeading(self):
        row = score.score_file("", body("Run it:\n```sh\n# rotate the logs\n## twice\n```\n## A heading\n"), [], [])

        self.assertEqual(row["badHeadings"], 1)
        self.assertEqual(row["fences"], 2)

    def test_aMarkerInBackticks_isTextNotALeak(self):
        row = score.score_file("", body("Strips the `<|channel|>` and `<think>` markers."), [], [])

        self.assertEqual(row["leaks"], 0)

    def test_aQuotedMarker_isTextNotALeak(self):
        row = score.score_file("", body('Reads "<|channel>thought" and "<channel|>", then <channel|> leaks.'), [], [])

        self.assertEqual(row["leaks"], 1)

    def test_scoreModel_missingAndCutOff(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "java").mkdir(parents=True)
            (work / "java" / "A.java").write_text("public class A { public int size() { return 0; } }", encoding="utf-8")
            (work / "java" / "B.java").write_text("public class B {}", encoding="utf-8")
            (out / "java").mkdir(parents=True)
            (out / "java" / "A.java.ai.md").write_text("h: 1\n---\n" + body("`A` has `size`."), encoding="utf-8")
            (out / "java" / "package.ai.md").write_text("h: 1\n---\n" + body("`Unknown` lives here."),
                                                         encoding="utf-8")

            rows = score.score_model(out, work, {}, {"java/A.java"})
            summary = score.summarize(rows)

        by_file = {r["file"]: r for r in rows}
        self.assertEqual(by_file["java/A.java"]["idsFound"], 2)
        self.assertTrue(by_file["java/A.java"]["cutOff"])
        self.assertEqual(by_file["java/B.java"]["failed"], "missing")
        self.assertEqual(by_file["java/package.ai.md"]["inventedNames"], [])
        # A found both names, the missing B scores 0
        self.assertEqual(summary["java"]["recall"], 0.5)
        self.assertEqual(summary["java"]["failed"], {"missing": 1})
        self.assertEqual(summary["java"]["cutOff"], 1)

    def test_scoreModel_aMissingSummary_isCutOffAsTheMetaSaysAndExpectsItsNumbers(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "sql").mkdir(parents=True)
            (work / "sql" / "seed.sql").write_text("INSERT INTO t VALUES (1);", encoding="utf-8")
            out.mkdir()

            rows = score.score_model(out, work, {"sql/seed.sql": {"ids": ["t"], "numbers": [10, 20, 30, 40]}},
                                     {"sql/seed.sql"})
            summary = score.summarize(rows)

        seed = next(r for r in rows if r["file"] == "sql/seed.sql")
        # under retryOnce a cut-off answer is not written: the missing file is the cut-off one
        self.assertTrue(seed["cutOff"])
        self.assertEqual(summary["sql"]["cutOff"], 1)
        self.assertEqual(summary["sql"]["numbers"], "0/4")

    def test_scoreModel_aFileWithNoNameToExpect_countsInRecall(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "java").mkdir(parents=True)
            (work / "other").mkdir(parents=True)
            (work / "java" / "A.java").write_text("public class A { public int size() { return 0; } }", encoding="utf-8")
            (work / "java" / "Hidden.java").write_text("class Hidden { void run() {} }", encoding="utf-8")
            (work / "java" / "Quiet.java").write_text("class Quiet {}", encoding="utf-8")
            (work / "other" / "a.sh").write_text("echo a", encoding="utf-8")
            (out / "java").mkdir(parents=True)
            (out / "other").mkdir(parents=True)
            (out / "java" / "A.java.ai.md").write_text("h: 1\n---\n" + body("`A` has `size`."), encoding="utf-8")
            (out / "java" / "Quiet.java.ai.md").write_text("h: 1\n---\n" + body("Nothing public."), encoding="utf-8")
            (out / "other" / "a.sh.ai.md").write_text("h: 1\n---\n" + body("Echoes."), encoding="utf-8")

            rows = score.score_model(out, work, {})
            summary = score.summarize(rows)

        by_file = {r["file"]: r for r in rows}
        # A names both (1); the missing Hidden has no public name but costs its 0; Quiet's summary had
        # nothing to name and is not measured
        self.assertEqual((by_file["java/Hidden.java"]["recall"], by_file["java/Quiet.java"]["recall"]), (0.0, None))
        self.assertEqual(summary["java"]["recall"], 0.5)
        # a file nobody listed names for is not measured
        self.assertIsNone(by_file["other/a.sh"]["recall"])
        self.assertIsNone(summary["other"]["recall"])

    def test_scoreModel_theExtensionInAnyCase_asSrcmorphSelectsIt(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "java").mkdir(parents=True)
            (work / "java" / "A.JAVA").write_text("public class A {}", encoding="utf-8")
            (work / "other").mkdir(parents=True)
            (work / "other" / "Dockerfile.prod").write_text("FROM x", encoding="utf-8")
            out.mkdir()

            rows = {r["file"]: r for r in score.score_model(out, work, {})}

        self.assertEqual((rows["java/A.JAVA"]["kind"], rows["java/A.JAVA"]["idsExpected"]), ("java", 1))
        self.assertIn("other/Dockerfile.prod", rows)

    def test_scoreModel_theAggregatesOfAFullRun_areExpected(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "java" / "p").mkdir(parents=True)
            (work / "java" / "p" / "A.java").write_text("public class A {}", encoding="utf-8")
            (out / "java" / "p").mkdir(parents=True)
            (out / "java" / "p" / "A.java.ai.md").write_text("h: 1\n---\n" + body("`A`."), encoding="utf-8")
            (out / "java" / "package.ai.md").write_text("h: 1\n---\n" + body("The java tree."), encoding="utf-8")

            rows = score.score_model(out, work, {}, {"project.ai.md"})

        aggregates = {r["file"]: (r["missing"], r["cutOff"]) for r in rows if r["kind"] in ("package", "project")}
        self.assertEqual(aggregates, {"package.ai.md": (True, False), "java/package.ai.md": (False, False),
                                      "java/p/package.ai.md": (True, False), "project.ai.md": (True, True)})
        self.assertEqual([r["file"] for r in rows][1:],
                         ["java/p/package.ai.md", "java/package.ai.md", "package.ai.md", "project.ai.md"])

    def test_scoreModel_aFileSrcmorphDoesNotIndex_isNoMissingSummary(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "other").mkdir(parents=True)
            (work / "other" / "notes.txt").write_text("not indexed", encoding="utf-8")
            (work / "other" / "a.sh").write_text("echo a", encoding="utf-8")
            out.mkdir()

            rows = score.score_model(out, work, {})

        self.assertEqual([r["file"] for r in rows if r["kind"] not in ("package", "project")], ["other/a.sh"])

    def test_sourceText_readsAsSrcmorphDecodes(self):
        with tempfile.TemporaryDirectory() as tmp:
            latin, bom, utf16 = Path(tmp) / "a", Path(tmp) / "b", Path(tmp) / "c"
            latin.write_bytes("Grüße".encode("latin-1"))
            bom.write_bytes(b"\xef\xbb\xbfclass A {}")
            utf16.write_bytes(b"\xff\xfe" + "x = 1".encode("utf-16-le"))

            self.assertEqual(score.source_text(latin), "Gr��e")
            self.assertEqual(score.source_text(bom), "class A {}")
            self.assertEqual(score.source_text(utf16), "x = 1")

    def test_scoreModel_aSecretTheFactsMark_countsWhenCopied(self):
        with tempfile.TemporaryDirectory() as tmp:
            work, out = Path(tmp) / "work", Path(tmp) / "out"
            (work / "other").mkdir(parents=True)
            (work / "other" / "a.xml").write_text("<p name='password' value='s3cret-1'/>", encoding="utf-8")
            (work / "other" / "b.xml").write_text("<p name='password' value='s3cret-2'/>", encoding="utf-8")
            (out / "other").mkdir(parents=True)
            (out / "other" / "a.xml.ai.md").write_text("h: 1\n---\n" + body("`password=***`."), encoding="utf-8")
            (out / "other" / "b.xml.ai.md").write_text("h: 1\n---\n" + body("`password=s3cret-2`."),
                                                       encoding="utf-8")
            facts = {"other/a.xml": {"ids": ["password"], "secret": "s3cret-1"},
                     "other/b.xml": {"ids": ["password"], "secret": "s3cret-2"}}

            rows = score.score_model(out, work, facts)

        by_file = {r["file"]: r for r in rows}
        self.assertFalse(by_file["other/a.xml"]["secretLeaked"])
        self.assertTrue(by_file["other/b.xml"]["secretLeaked"])
        self.assertEqual(score.summarize(rows)["other"]["secretsLeaked"], 1)


class JavaNamesTest(unittest.TestCase):

    def test_annotatedAndMultiLineSignatures(self):
        source = """
            package p;
            /** public void notInAComment() */
            @SuppressWarnings({"a", "b"})
            public final class Config {
                public @Nullable String getName() { return name; }
                @Override
                public String toString() { return "public void notAString() {"; }
                public <T extends Comparable<T>> List<T>
                        sorted(List<T> values) { return values; }
                protected static Map<String, List<Integer>> index() { return null; }
                public Config() {}
                private void hidden() {}
                public static final Pattern P = Pattern.compile("x");
                public static final class Builder {
                    public Builder withName(String name) { return this; }
                }
            }
            """

        self.assertEqual(score.java_names(source),
                         ["Builder", "Config", "getName", "index", "sorted", "toString", "withName"])

    def test_anInterfacesMethods_withoutTheModifier(self):
        source = """
            public interface Provider extends Closeable {
                String generate(Request request) throws IOException;
                default Usage lastUsage() { return Usage.NONE; }
                static Provider of() { class Local { void notPublic() {} } return null; }
                int LIMIT = 3;
            }
            """

        self.assertEqual(score.java_names(source), ["Provider", "generate", "lastUsage", "of"])

    def test_aStaticInterfacesAndAnAnnotationsElements_notAPrivateInterfaceMethod(self):
        source = """
            public class Outer {
                public static interface Listener {
                    void onEvent(String e);
                    private void helper() {}
                    static private int count() { return 0; }
                }
                public abstract static strictfp interface Sink { void accept(int v); }
                public @interface Marker {
                    String value() default "x";
                    int[] order() default {};
                }
            }
            """

        self.assertEqual(score.java_names(source),
                         ["Listener", "Marker", "Outer", "Sink", "accept", "onEvent", "order", "value"])



class NumbersTest(unittest.TestCase):

    def test_aNumber_withOrWithoutThousandsSeparators_counts(self):
        for text in ("20000 rows", "20,000 rows", "20 000 rows", "20\u202f000 rows", "rows: 20000."):
            self.assertTrue(score.number_pattern(20000).search(text), text)

    def test_aNumber_insideALongerNumberOrADecimal_doesNot(self):
        for text in ("120000", "200001", "20000.5", "v1.20000", "20000 000", "1,20000"):
            self.assertFalse(score.number_pattern(20000).search(text), text)

    def test_aSmallNumber_isNotPartOfAGroupedOne(self):
        self.assertTrue(score.number_pattern(61).search("61 INSERT statements"))
        self.assertFalse(score.number_pattern(61).search("1,061 rows"))

    def test_aNumberStuckToALetter_orAPartOfADate_doesNot(self):
        for n, text in ((3, "uses v3 and S3"), (3, "the 3rd"), (100, "a 100k file"), (2026, "on 2026-11-06"),
                        (11, "on 2026-11-06"), (6, "on 2026-11-06"), (12, "error ID-12"), (5, "x_5")):
            self.assertFalse(score.number_pattern(n).search(text), (n, text))
        for n, text in ((4000, "a 4000-row table"), (3, "(3 rows)"), (3, "rows: 3."), (12, "-12 degrees")):
            self.assertTrue(score.number_pattern(n).search(text), (n, text))


class MiddleTest(unittest.TestCase):

    def test_aMiddleName_countsAsANameOrAsThePrefixOfOne(self):
        self.assertEqual(score.middle_found(["LEDGER_LOCK"], "the LEDGER_LOCK_02701 constants"), ["LEDGER_LOCK"])
        self.assertEqual(score.middle_found(["LEDGER_LOCK"], "LEDGER_LOCK errors"), ["LEDGER_LOCK"])
        self.assertEqual(score.middle_found(["LEDGER_LOCK"], "LEDGER_LOCKED"), [])

    def test_aQualifiedMiddleName_countsAsASubstring(self):
        self.assertEqual(score.middle_found(["inv:recall"], "an `inv:recall` element"), ["inv:recall"])


class BoundaryTest(unittest.TestCase):
    """The limits of the measures, each at and beside its edge."""

    def test_aBodyOfExactlyTheMinimum_isNotTooShort(self):
        exact = "> " + "x" * (score.MIN_BODY_BYTES - 2)

        self.assertEqual(score.score_file("", exact, [], [])["failed"], "")
        self.assertEqual(score.score_file("", exact[:-1], [], [])["failed"], "too-short")

    def test_aFenceHalfOfWhoseLinesAreTheSources_isACopy(self):
        source = "int Foo = 1;\n"
        half = score.score_file(source, body("```\nint Foo = 1;\nBar is mine\n```\n"), ["Bar"], [])
        less = score.score_file(source, body("```\nint Foo = 1;\nBar is mine\nand so is this\n```\n"), ["Bar"], [])

        self.assertEqual(half["idsFound"], 0)
        self.assertEqual(less["idsFound"], 1)

    def test_aLongNumber_groupsByThreeFromTheRight(self):
        for n, text in ((1234, "1,234"), (12345, "12,345"), (123456, "123 456"), (1234567, "1,234,567")):
            self.assertTrue(score.number_pattern(n).search(f"has {text} rows"), text)
        self.assertFalse(score.number_pattern(1234567).search("has 12,34,567 rows"))

    def test_theRowFields(self):
        invented = " ".join(f"`name{i:02d}x`" for i in range(10))
        row = score.score_file("", body("# One\n## Two\n### Three\n#### Four\n" + invented), [], [])
        empty = score.score_file("", "", [], [])

        self.assertEqual(row["badHeadings"], 3)
        self.assertEqual(row["invented"], 10)
        self.assertEqual(row["inventedNames"], [f"name{i:02d}x" for i in range(8)])
        self.assertFalse(row["empty"])
        self.assertTrue(empty["empty"])

    def test_onlyTheFirstReasoningBlock_isTheLeadingOne(self):
        row = score.score_file("", "<think>a</think><think>b</think>\n" + body("Fine."), [], [])

        self.assertFalse(row["lead"])

    def test_cutOffOf_noMetaOrNoFile_isNothing(self):
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(score.cut_off_of(None, "m"), set())
            self.assertEqual(score.cut_off_of(tmp, "m"), set())


class MainTest(unittest.TestCase):
    """score.py as the card runs it: two models over a corpus of every kind, into a scores directory
    that does not exist yet."""

    def test_main_writesTheSummaryAndTheRowsOfEveryModel(self):
        import json
        import subprocess

        with tempfile.TemporaryDirectory() as tmp:
            tmp = Path(tmp)
            work, out, meta, scores = tmp / "work", tmp / "out", tmp / "meta", tmp / "a" / "scores"
            for rel, text in (("java/A.java", "public class A { public int size() { return 0; } }"),
                              ("sql/s.sql", "CREATE TABLE loans (id INT);\n"),
                              ("other/x.sh", "MAX=3\n")):
                (work / rel).parent.mkdir(parents=True, exist_ok=True)
                (work / rel).write_text(text, encoding="utf-8")
            facts = tmp / "facts.json"
            facts.write_text(json.dumps({"sql/s.sql": {"ids": ["loans"], "numbers": [3]},
                                         "other/x.sh": {"ids": ["MAX"]}}), encoding="utf-8")
            summaries = {"m1": {"java/A.java": body("`A` has `size`, also `ghost`."),
                                "sql/s.sql": body("Creates LOANS with 3 rows."),
                                "java/package.ai.md": body("`Unknown` lives here."),
                                "project.ai.md": "No lead line here." + LONG},
                         "m2": {"java/A.java": "", "sql/s.sql": "<think>x</think>"}}
            for model, files in summaries.items():
                for rel, text in files.items():
                    target = out / model / (rel if rel.endswith(".ai.md") else rel + ".ai.md")
                    target.parent.mkdir(parents=True, exist_ok=True)
                    target.write_text("h: 1\n---\n" + text, encoding="utf-8")
            meta.mkdir()
            (meta / "m1.json").write_text(json.dumps({"phases": {"GenerateFileIndex": {"cutOff": ["sql/s.sql"]}}}),
                                          encoding="utf-8")

            result = subprocess.run([sys.executable, str(Path(score.__file__)), "--work", str(work), "--out", str(out),
                                     "--scores", str(scores), "--facts", str(facts), "--meta", str(meta)],
                                    capture_output=True, text=True)
            summary_text = (scores / "summary.json").read_text(encoding="utf-8")
            rows = (scores / "files.tsv").read_text(encoding="utf-8").splitlines()
            missing_work = subprocess.run([sys.executable, str(Path(score.__file__)), "--out", str(out)],
                                          capture_output=True, text=True)

        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertTrue(summary_text.startswith('{\n "m1": {\n  "'))
        summary = json.loads(summary_text)
        m1, m2 = summary["m1"], summary["m2"]
        self.assertEqual(m1["java"]["recall"], 1.0)
        self.assertEqual((m1["java"]["ticked"], m1["java"]["invented"], m1["java"]["inventedPer100"]), (2, 1, 50.0))
        source_bytes = len("public class A { public int size() { return 0; } }")
        self.assertEqual(m1["java"]["compaction"],
                         round(source_bytes / len(body("`A` has `size`, also `ghost`.").strip()), 2))
        self.assertEqual((m1["sql"]["recall"], m1["sql"]["numbers"], m1["sql"]["cutOff"]), (1.0, "1/1", 1))
        self.assertEqual(m1["other"]["missing"], 1)
        self.assertEqual(m1["other"]["failed"], {"missing": 1})
        # java/package.ai.md written; other/, sql/ and the root's missing
        self.assertEqual((m1["package"]["files"], m1["package"]["missing"], m1["project"]["noLead"]), (4, 3, 1))
        self.assertEqual(m2["java"]["failed"], {"empty": 1})
        self.assertEqual((m2["sql"]["failed"], m2["sql"]["leaks"], m2["sql"]["compaction"]), ({"leak-only": 1}, 2, None))
        self.assertEqual(m2["java"]["recall"], 0)
        self.assertEqual(rows[0].split("\t"), score.TSV_COLUMNS)
        a = next(r.split("\t") for r in rows if r.startswith("m1\tjava/A.java\t"))
        self.assertEqual(a[score.TSV_COLUMNS.index("inventedNames")], "ghost")
        self.assertEqual(a[score.TSV_COLUMNS.index("idsFound")], "2")
        # the header, then per model three files, four packages (java, other, sql, the root) and the project
        self.assertEqual(len(rows), 1 + 8 + 8)
        self.assertNotEqual(missing_work.returncode, 0)

if __name__ == "__main__":
    unittest.main()
