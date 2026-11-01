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

    def test_anIdentifier_countsAsAWholeWordInProse(self):
        row = score.score_file("", body("It holds the ledger_customer, the SrcMorphConfiguration and malloc."),
                               ["ledger_customer", "SrcMorphConfiguration", "malloc", "ledger"], [])

        # "ledger" is only part of ledger_customer
        self.assertEqual(row["idsFound"], 3)

    def test_aPathLikeId_isASubstring(self):
        row = score.score_file("", body("It uses urn:example:catalog throughout."), ["urn:example:catalog"], [])

        self.assertEqual(row["idsFound"], 1)

    def test_sql_namesWithoutRegardToCase(self):
        row = score.score_file("CREATE TABLE author", body("Table `AUTHOR` and LEDGER_ACCOUNT."),
                               ["author", "ledger_account"], [], case_insensitive=True, everyday=["author"])

        self.assertEqual(row["idsFound"], 2)

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


if __name__ == "__main__":
    unittest.main()
