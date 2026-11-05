# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Tests of run.py, search.py and large.py that need no network and no model.

Run from the repository root: python -m unittest discover -s docs/ai-index-benchmark/modelcard/tests
"""
import argparse
import io
import json
import subprocess
import sys
import tempfile
import unittest
import urllib.error
from pathlib import Path
from unittest import mock

HERE = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(HERE))
import run  # noqa: E402
import search  # noqa: E402

MODELS = json.loads((HERE / "models.json").read_text(encoding="utf-8"))


def args(**overrides):
    values = {"models": "gpt-oss-20b", "backend": "", "oversize": "", "max_chunks": None, "chars_per_token": None}
    values.update(overrides)
    return argparse.Namespace(**values)


class ValidateTest(unittest.TestCase):

    def assert_rejected(self, **overrides):
        parser = argparse.ArgumentParser()
        with mock.patch.object(sys, "stderr", io.StringIO()), self.assertRaises(SystemExit):
            run.validate(parser, args(**overrides), MODELS)

    def test_valid(self):
        run.validate(argparse.ArgumentParser(), args(oversize="sampleChunks", max_chunks=6, chars_per_token=2.5),
                     MODELS)

    def test_rejected(self):
        self.assert_rejected(max_chunks=6)
        self.assert_rejected(oversize="mapReduce", max_chunks=6)
        self.assert_rejected(oversize="sampleChunks")
        self.assert_rejected(oversize="sampleChunks", max_chunks=0)
        self.assert_rejected(chars_per_token=0)
        self.assert_rejected(models="gpt-oss-20b,gpt-6-lunna")
        self.assert_rejected(models="", backend="nowhere")


class RulesTest(unittest.TestCase):

    def test_maxChunks_isSetWheneverGiven(self):
        rules = run.file_rules(run.PROFILES / "v3", "m", oversize="sampleChunks", max_chunks=1)

        self.assertTrue(all(r["onOversize"] == "sampleChunks" and r["maxChunks"] == 1 for r in rules))

    def test_theRunRecord_onlyInTheFilePhase(self):
        definition = {"key": "m", "provider": "mock"}
        files = run.phase_config("GenerateFileIndex", Path("w"), Path("o"), run.PROFILES / "v3", definition,
                                 run_record=Path("r.json"))
        packages = run.phase_config("AggregatePackages", Path("w"), Path("o"), run.PROFILES / "v3", definition,
                                    run_record=Path("r.json"))

        self.assertEqual(files["srcMorph"]["runRecordFile"], "r.json")
        self.assertNotIn("runRecordFile", packages["srcMorph"])

    def test_theAggregationPhases_takeTheirRuleWhereTheCliReadsIt(self):
        definition = {"key": "m", "provider": "mock"}
        packages = run.phase_config("AggregatePackages", Path("w"), Path("o"), run.PROFILES / "v3", definition)
        project = run.phase_config("AggregateProject", Path("w"), Path("o"), run.PROFILES / "v3", definition)

        chunked = {"mapPromptKey": "map-chunk", "reducePromptKey": "reduce-parts"}
        self.assertEqual(packages["packageFieldGenerations"],
                         [{"promptKey": "package-body", "aiDefinitionKey": "m", **chunked}])
        self.assertNotIn("projectFieldGenerations", packages)
        self.assertEqual(project["projectFieldGenerations"],
                         [{"promptKey": "project-body", "aiDefinitionKey": "m", **chunked}])

    def test_chunkedRules_nameTheMapAndReducePrompts_otherRulesNot(self):
        profile = run.PROFILES / "v3"
        chunked = run.file_rules(profile, "m", oversize="mapReduce")
        sampled = run.file_rules(profile, "m", oversize="sample")

        self.assertTrue(all(r["mapPromptKey"] == "map-chunk" and r["reducePromptKey"] == "reduce-parts"
                            for r in chunked))
        self.assertTrue(all("mapPromptKey" not in r and "reducePromptKey" not in r for r in sampled))

    def test_javaPrompt_namesTheJavaAndTheJavaEeRule(self):
        profile = run.PROFILES / "v3"
        default = {r["id"]: r for r in run.file_rules(profile, "m")}
        with tempfile.TemporaryDirectory() as tmp:
            # a profile of the history before 2026-11-04, with its Java prompt
            (Path(tmp) / "file-body-java.md").write_text("Java.", encoding="utf-8")
            older = {r["id"]: r for r in run.file_rules(Path(tmp), "m", java_prompt="file-body-java")}

        self.assertEqual(default["java"]["promptKey"], "file-body-oop")
        self.assertEqual(default["java-ee"]["promptKey"], "file-body-oop")
        self.assertEqual(default["java-ee"]["priority"], 10)
        self.assertEqual(older["java"]["promptKey"], "file-body-java")
        self.assertEqual(older["java-ee"]["promptKey"], "file-body-java")

    def test_enterpriseKinds_takeTheirSharedPromptAndTheirOwnFacts(self):
        rules = {r["id"]: r for r in run.file_rules(run.PROFILES / "v3", "m", facts=True)}

        self.assertEqual((rules["kotlin"]["promptKey"], rules["kotlin"]["factsKey"]), ("file-body-oop", "kotlin-facts"))
        self.assertEqual((rules["jee"]["promptKey"], rules["jee"]["factsKey"]), ("file-body-xml", "jee-facts"))
        self.assertEqual((rules["jsp"]["promptKey"], rules["jsp"]["factsKey"]), ("file-body-view", "jsp-facts"))
        self.assertEqual(rules["openapi"]["promptKey"], "file-body-contract")
        self.assertEqual(rules["dockerfile"]["factsKey"], "dockerfile-facts")
        self.assertEqual((rules["scala"]["promptKey"], rules["scala"]["factsKey"]), ("file-body-oop", "scala-facts"))
        self.assertEqual((rules["proto"]["promptKey"], rules["proto"]["factsKey"]), ("file-body-contract", "proto-facts"))
        self.assertEqual((rules["gradle"]["promptKey"], rules["gradle"]["priority"]), ("file-body-ops", 10))
        # a kind without a facts file of its own counts nothing
        self.assertNotIn("factsKey", rules["ops"])

    def test_typeScript_takesTheOopPromptWithoutFacts(self):
        rules = {r["id"]: r for r in run.file_rules(run.PROFILES / "v3", "m", facts=True)}

        self.assertEqual(rules["typescript"]["promptKey"], "file-body-oop")
        self.assertEqual(rules["typescript"]["condition"], {"extensions": [".ts", ".mts", ".cts"]})
        self.assertNotIn("factsKey", rules["typescript"])
        self.assertTrue({".ts", ".mts", ".cts"} <= set(run.EXTENSIONS))

    def test_aProfileWithoutAKindsPromptOrFacts_hasNoRuleForIt(self):
        with tempfile.TemporaryDirectory() as tmp:
            rules = {r["id"] for r in run.file_rules(Path(tmp), "m", facts=True)}

        self.assertEqual(rules, {"java", "sql", "fallback"})

    def test_aPhaseConfig_indexesFilesWithoutAnExtensionByName(self):
        definition = {"key": "m", "provider": "mock"}
        config = run.phase_config("GenerateFileIndex", Path("w"), Path("o"), run.PROFILES / "v3", definition)

        self.assertEqual(config["srcMorph"]["fileNames"], ["Dockerfile", "Containerfile"])

    def test_chunkPrompts_onlyThoseTheProfileHas(self):
        self.assertEqual(run.chunk_prompts(Path("no-such-profile")), {})

    def test_profileHashes_ofTheFilesTheRulesName(self):
        profile = run.PROFILES / "v3"
        hashes = run.profile_hashes(profile, run.file_rules(profile, "m", facts=True))

        self.assertIn("file-body-oop.md", hashes)
        self.assertIn("sql-facts.tsv", hashes)
        self.assertIn("package-body.md", hashes)
        self.assertIn("map-chunk.md", hashes)
        self.assertIn("reduce-parts.md", hashes)
        self.assertEqual(hashes["file-body-oop.md"], run.sha256_of(profile / "file-body-oop.md"))


class CostTest(unittest.TestCase):

    def test_cachedPromptTokens_atTheCacheReadPrice(self):
        totals = {"promptTokens": 1000, "cachedPromptTokens": 600, "completionTokens": 100}

        self.assertEqual(run.token_cost(totals, (1e-6, 2e-6, 1e-7)), round(400e-6 + 600e-7 + 200e-6, 6))
        self.assertEqual(run.token_cost({"promptTokens": 10, "completionTokens": 0}, (1e-3, 0, 0)), 0.01)


class ScrubTest(unittest.TestCase):

    def test_theRunDirectory_inEitherSlashForm_becomesAPlaceholder(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp) / "run1"
            lines = [f"failed: {root / 'work' / 'A.java'}", "failed: " + str(root).replace("\\", "/") + "/work/B.java",
                     "no path here"]

            out = run.scrubbed(lines, root)

        self.assertEqual(out[0].replace("\\", "/"), "failed: <run>/work/A.java")
        self.assertEqual(out[1], "failed: <run>/work/B.java")
        self.assertEqual(out[2], "no path here")


class WorkTest(unittest.TestCase):

    def test_aWorkDirectoryRunPyDidNotCreate_isRefused(self):
        with tempfile.TemporaryDirectory() as tmp:
            work = Path(tmp) / "work"
            (work / "keep").mkdir(parents=True)

            with self.assertRaises(SystemExit):
                run.prepare_work(work, run.JAVA_COMMIT, corpus=str(Path(tmp) / "corpus"))
            self.assertTrue((work / "keep").is_dir())

    def test_anOutputDirectoryOfSummaries_isDeleted(self):
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "out" / "m"
            (out / "java").mkdir(parents=True)
            (out / "java" / "A.java.ai.md").write_text("summary", encoding="utf-8")
            (out / "package.ai.md").write_text("package", encoding="utf-8")

            run.clear_out(out)

            self.assertFalse(out.exists())

    def test_anOutputDirectoryHoldingOtherFiles_isRefused(self):
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "src"
            (out / "main").mkdir(parents=True)
            (out / "main" / "A.java").write_text("class A {}", encoding="utf-8")
            (out / "A.java.ai.md").write_text("summary", encoding="utf-8")

            with self.assertRaises(SystemExit):
                run.clear_out(out)
            self.assertTrue((out / "main" / "A.java").is_file())

    def test_itsOwnWorkDirectory_isReplaced(self):
        with tempfile.TemporaryDirectory() as tmp:
            corpus, work = Path(tmp) / "corpus", Path(tmp) / "work"
            (corpus / "other").mkdir(parents=True)
            (corpus / "other" / "a.sh").write_text("echo", encoding="utf-8")

            run.prepare_work(work, run.JAVA_COMMIT, corpus=str(corpus))
            (work / "stale").write_text("", encoding="utf-8")
            run.prepare_work(work, run.JAVA_COMMIT, corpus=str(corpus))

            self.assertEqual(sorted(p.relative_to(work).as_posix() for p in work.rglob("*") if p.is_file()),
                             ["other/a.sh"])


class SearchTest(unittest.TestCase):

    def test_bm25_ranksOnlyFilesSharingAWord(self):
        ranked = search.bm25_rank({"b/x.java": "", "a/y.java": "checkout audit", "c/z.sql": ""}, "audit trail")

        self.assertEqual(ranked, ["a/y.java"])
        self.assertEqual(search.bm25_rank({"a": "", "b": ""}, "anything"), [])

    def test_cacheKey_changesWithTheSystemPrompt(self):
        key = search.cache_key("m", "ctx", "q")
        with mock.patch.object(search, "SYSTEM", search.SYSTEM + " Be brief."):
            self.assertNotEqual(search.cache_key("m", "ctx", "q"), key)
        self.assertEqual(search.cache_key("m", "ctx", "q"), key)

    def test_backoff_retriesARateLimit_failsABadRequestAtOnce(self):
        def http(code):
            return urllib.error.HTTPError("u", code, "x", {}, io.BytesIO(b""))

        answer = mock.MagicMock()
        answer.__enter__.return_value = io.StringIO('{"choices": []}')
        sleeps = []
        with mock.patch.object(search.urllib.request, "urlopen", side_effect=[http(429), http(503), answer]):
            self.assertEqual(search.post_with_backoff(object(), sleeps.append), {"choices": []})
        self.assertEqual(sleeps, [2, 4])

        with mock.patch.object(search.urllib.request, "urlopen", side_effect=[http(400)]):
            with self.assertRaises(urllib.error.HTTPError):
                search.post_with_backoff(object(), sleeps.append)

        error = mock.MagicMock()
        error.__enter__.return_value = io.StringIO('{"error": {"message": "no"}}')
        with mock.patch.object(search.urllib.request, "urlopen", side_effect=[error]):
            with self.assertRaises(IOError):
                search.post_with_backoff(object(), sleeps.append)


class LargeTest(unittest.TestCase):

    def test_factsBesideTheCorpus_namesInTheMiddle(self):
        with tempfile.TemporaryDirectory() as tmp:
            out = Path(tmp) / "large"
            subprocess.run([sys.executable, "-I", str(HERE / "large.py"), "--out", str(out), "--scale", "0.05"],
                           check=True, capture_output=True)
            facts = json.loads((Path(tmp) / "large-facts.json").read_text(encoding="utf-8"))

            self.assertFalse(any(p.suffix == ".json" for p in out.rglob("*")))
            for rel, fact in facts.items():
                if rel.startswith("_"):
                    continue
                text = (out / rel).read_text(encoding="utf-8")
                for name in fact["middle"]:
                    position = text.index(name.split(":")[-1]) / len(text)
                    self.assertTrue(0.2 < position < 0.8, (rel, name, position))
                    self.assertIn(name, fact["ids"])

    def test_aScaleOfZero_isRefused(self):
        result = subprocess.run([sys.executable, "-I", str(HERE / "large.py"), "--out", "unused", "--scale", "0"],
                                capture_output=True, text=True)

        self.assertNotEqual(result.returncode, 0)
        self.assertIn("--scale must be above 0", result.stderr)



class CutOffTest(unittest.TestCase):

    def log(self, text):
        path = Path(tempfile.mkdtemp()) / "phase.log"
        path.write_text(text, encoding="utf-8")
        return path

    def test_anAnswerNotWritten_andOneKept_areCutOff_aKeptPartIsNot(self):
        work, out = Path("W"), Path("O")
        log = self.log(
            "WARN AiFieldGenerationSupport - Incomplete answer for file '" + str(work / "sql" / "a.sql")
            + "' (field 'file-body-sql', TRUNCATED): The answer for x was cut off at the 4096-token output"
            " budget; not written, left for the next run\n"
            "WARN AiFieldGenerationSupport - Incomplete answer for " + str(work / "java" / "B.java")
            + ": The answer for x was cut off at the 4096-token output budget; kept as it is (onIncomplete: keep)\n"
            "WARN AiFieldGenerationSupport - Incomplete answer for a part of " + str(work / "other" / "c.xml")
            + ": The answer for x was cut off at the 4096-token output budget; kept, the reduce reads it\n"
            "WARN AiFieldGenerationSupport - Incomplete answer for file '" + str(work / "sql" / "e.sql")
            + "' (field 'file-body-sql', EMPTY): The model answered nothing\n")

        self.assertEqual(run.cut_off_files(log, work, out), ["java/B.java", "sql/a.sql"])

    def test_filesNotWritten_isTheOnlyFailureTheNextPhasesRunAfter(self):
        self.assertTrue(run.files_not_written(self.log("SrcMorphException: 2 file(s) were not written (an incomplete")))
        self.assertFalse(run.files_not_written(self.log("SrcMorphException: Invalid fieldGenerations: two fallbacks")))

if __name__ == "__main__":
    unittest.main()
