# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Runs the model-card benchmark for one or more models through the srcmorph CLI.

The corpus is 12 Java files of srcmorph itself (taken from a fixed commit with `git archive`) plus
the SQL and other fixtures under ../corpus. Every model gets the same three phases with the same
prompts: GenerateFileIndex (prompt routed by extension: .java, .sql, everything else to the
fallback), AggregatePackages, AggregateProject. The prompts are the files of one profile under the
repository's profiles/ directory (--profile, default v3, the current one), passed to srcmorph as its
profileDirectory, so the benchmark runs exactly the files the plugin's self-test runs. The
.ai.md tree goes to <out>/<model key>/, timings and the OpenRouter cost to <meta>/<model key>.json.

API keys are never read from a file here: the caller puts OPENROUTER_API_KEY / HF_TOKEN into the
environment, and a model only receives the variable its backend names.

Usage:
  python run.py --cp <classpath file> --java <java executable> --models gpt-6-luna,gemma-4-31b
  python run.py ... --backend openrouter        (every model of one backend)
Options: --profile <profile under profiles/> (default v3; it needs the five prompt keys' files),
         --profile-dir <directory holding the profiles> (default the repository's profiles/; an
         earlier prompt set is restored from the history into a directory of its own, see
         profiles/README.md),
         --corpus <directory with java/, sql/, other/> (default: the srcmorph Java snapshot plus
         ../corpus; another corpus of the same layout is scored with score.py --facts; a missing
         kind stays empty),
         --facts (every file rule names its counters: java-facts, sql-facts, shell-facts,
         perl-facts, xml-facts; the card ran without them, so such a run is not comparable to it),
         --oversize <fail|sample|mapReduce|sampleChunks|deterministic> and --max-chunks <n> (the
         strategy for a
         file larger than the model's window, on every file rule; the default fails such a file),
         --chars-per-token <n> (replaces every model's estimate: a data file such as a SQL dump has
         fewer characters per token than code, and a window filled at 4 is refused by the provider),
         --model-dir <GGUF dir> (local models), --prepend <jars> (the CLI jar, the CUDA natives),
         --commit <git rev of the Java snapshot>, --seed <n> (every model's seed, for repeat runs;
         the OpenAI-compatible provider sends it too), --out, --meta, --work (created by run.py and
         marked so; an existing directory without the marker is refused, never deleted).

<meta>/<model key>.json holds the model definition as sent (without the key variable), the corpus,
profile and options, the SHA-256 of every prompt and fact file the rules name, the srcmorph-cli jar
and Java version, start and end (UTC), per phase its exit code, seconds, cut-off paths and on a
failure the log's last lines, and from srcmorph's run record (runRecordFile) the file phase's calls,
prompt / cached / completion tokens and finish reasons; for an OpenRouter model also the cost of
those tokens at the model's list price (tokenCostUsd, without cache discounts) next to the account's
usage delta (costUsd, which also books whatever else used the key meanwhile).
"""
import argparse
import hashlib
import json
import os
import re
import shutil
import subprocess
import sys
import tarfile
import time
import urllib.request
from io import BytesIO
from pathlib import Path

HERE = Path(__file__).resolve().parent
REPO = HERE.parents[2]
CORPUS = HERE.parent / "corpus"
PROFILES = REPO / "profiles"
PROMPT_KEYS = ("file-body-java", "file-body-sql", "file-body-fallback", "package-body", "project-body")

JAVA_COMMIT = "1e056c23652992a07cca0056d2783cbf6520f5a1"
JAVA_ROOT = "srcmorph/src/main/java/net/ladenthin/srcmorph/"
JAVA_FILES = [
    "config/AiConditionEvaluator.java",
    "config/AiConditionGroup.java",
    "config/AiFactExtractor.java",
    "config/AiGenerationConfig.java",
    "config/AiOversizeStrategy.java",
    "config/SrcMorphConfiguration.java",
    "provider/AiCompletionParser.java",
    "provider/AiGenerationProvider.java",
    "provider/AiGenerationProviderFactory.java",
    "provider/LlamaCppJniAiGenerationProvider.java",
    "provider/LlamaCppJniConfig.java",
    "provider/OpenAiCompatibleProviderSupport.java",
]
EXTENSIONS = [".java", ".sql", ".h", ".c", ".cl", ".py", ".sh", ".bash", ".ksh", ".pl", ".pm", ".xml", ".xsd", ".xsl", ".xslt"]
# A language gets a rule of its own when the profile has its prompt (file-body-<prompt>.md) or when
# --facts asks for its counters (<name>-facts.tsv); otherwise the fallback rule summarizes it. A POM
# matches pom and xml; its priority gives it the Maven counters, with the XML prompt.
XML_EXTENSIONS = [".xml", ".xsd", ".xsl", ".xslt"]
LANGUAGES = (("shell", {"extensions": [".sh", ".bash", ".ksh"]}, "shell", 0),
             ("perl", {"extensions": [".pl", ".pm"]}, "perl", 0),
             ("pom", {"pathGlob": "**/*pom.xml"}, "xml", 10),
             ("xml", {"extensions": XML_EXTENSIONS}, "xml", 0))
PHASES = ("GenerateFileIndex", "AggregatePackages", "AggregateProject")
WORK_MARKER = ".srcmorph-benchmark-work"
OVERSIZE = ("fail", "sample", "mapReduce", "sampleChunks", "deterministic")


def prepare_work(work, commit, corpus=None):
    """Java snapshot under work/java/{config,provider}, fixtures under work/sql and work/other; with a
    corpus directory, its java/, sql/ and other/ trees instead."""
    if work.exists():
        if not (work.parent / (work.name + WORK_MARKER)).exists():
            sys.exit(f"{work} exists and was not created by run.py; refusing to delete it")
        shutil.rmtree(work)
    work.mkdir(parents=True)
    (work.parent / (work.name + WORK_MARKER)).write_text("", encoding="utf-8")
    if corpus is not None:
        for kind in ("java", "sql", "other"):
            if (Path(corpus) / kind).is_dir():
                shutil.copytree(Path(corpus) / kind, work / kind)
            else:
                (work / kind).mkdir(parents=True)
        return
    paths = [JAVA_ROOT + f for f in JAVA_FILES]
    archive = subprocess.run(["git", "-C", str(REPO), "archive", "--format=tar", commit] + paths,
                             check=True, capture_output=True).stdout
    with tarfile.open(fileobj=BytesIO(archive)) as tar:
        for member in tar.getmembers():
            if member.isfile():
                target = work / "java" / member.name[len(JAVA_ROOT):]
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_bytes(tar.extractfile(member).read())
    for kind in ("sql", "other"):
        shutil.copytree(CORPUS / kind, work / kind)


def ai_definition(models, model, model_dir, seed=None, chars_per_token=None):
    backend = models["backends"][model["backend"]]
    definition = {"key": model["key"]}
    definition.update(models["common"])
    definition.update(backend)
    definition.update(model["settings"])
    if seed is not None:
        definition["seed"] = seed
    if chars_per_token is not None:
        definition["charsPerToken"] = chars_per_token
    if model["backend"] == "local":
        definition["modelPath"] = str(Path(model_dir) / definition["modelPath"])
    return definition


def file_rules(profile, key, facts=False, oversize=None, max_chunks=None):
    """The GenerateFileIndex rules: java, sql, a rule per language that has a prompt or (with facts)
    counters of its own, and the fallback. With oversize every rule takes that strategy."""
    rules = [{"id": "java", "promptKey": "file-body-java", "condition": {"extensions": [".java"]}},
             {"id": "sql", "promptKey": "file-body-sql", "condition": {"extensions": [".sql"]}}]
    if facts:
        rules[0]["factsKey"], rules[1]["factsKey"] = "java-facts", "sql-facts"
    for name, condition, prompt, priority in LANGUAGES:
        own_prompt = (Path(profile) / f"file-body-{prompt}.md").is_file()
        if own_prompt or facts:
            rule = {"id": name, "promptKey": f"file-body-{prompt}" if own_prompt else "file-body-fallback",
                    "condition": dict(condition)}
            if priority:
                rule["priority"] = priority
            if facts:
                rule["factsKey"] = f"{name}-facts"
            rules.append(rule)
    rules.append({"id": "fallback", "promptKey": "file-body-fallback", "fallback": True})
    for rule in rules:
        rule["aiDefinitionKey"] = key
        if oversize:
            rule["onOversize"] = oversize
            if max_chunks is not None:
                rule["maxChunks"] = max_chunks
    return rules


def phase_config(phase, work, out_dir, profile, definition, facts=False, oversize=None, max_chunks=None,
                 run_record=None):
    """The CLI configuration of one phase. The file phase's rules are srcMorph's fieldGenerations; the
    package and project phases take their one rule from packageFieldGenerations and
    projectFieldGenerations, as the CLI reads them."""
    key = definition["key"]
    rules = file_rules(profile, key, facts, oversize, max_chunks)
    config = {
        "command": phase,
        "srcMorph": {
            "baseDirectory": str(work),
            "outputDirectory": str(out_dir),
            "subtrees": ["java", "sql", "other"],
            "fileExtensions": EXTENSIONS,
            "generationProvider": definition["provider"],
            "projectName": "srcmorph-modelcard",
            "force": True,
            "profileDirectory": str(profile),
            "aiDefinitions": [definition],
            "fieldGenerations": rules,
        },
    }
    if phase == "AggregatePackages":
        config["packageFieldGenerations"] = [{"promptKey": "package-body", "aiDefinitionKey": key}]
    elif phase == "AggregateProject":
        config["projectFieldGenerations"] = [{"promptKey": "project-body", "aiDefinitionKey": key}]
    if run_record is not None and phase == "GenerateFileIndex":
        config["srcMorph"]["runRecordFile"] = str(run_record)
    return config


def sha256_of(path):
    return hashlib.sha256(Path(path).read_bytes()).hexdigest()


def profile_hashes(profile, rules):
    """The SHA-256 of every prompt and fact file a rule names (profile/<key>.md, profile/<key>.tsv)."""
    hashes = {}
    for rule in rules:
        for name in (rule["promptKey"] + ".md", rule.get("factsKey", "") and rule["factsKey"] + ".tsv"):
            if name and (Path(profile) / name).is_file():
                hashes[name] = sha256_of(Path(profile) / name)
    for name in ("package-body.md", "project-body.md"):
        hashes[name] = sha256_of(Path(profile) / name)
    return dict(sorted(hashes.items()))


def cli_jar(classpath):
    """The srcmorph-cli jar of a classpath: its file name and SHA-256 (the path is machine data)."""
    for entry in classpath.split(os.pathsep):
        if re.search(r"srcmorph-cli-[^" + re.escape(os.sep) + r"/]*\.jar$", entry) and Path(entry).is_file():
            return {"jar": Path(entry).name, "sha256": sha256_of(entry)}
    return None


def java_version(java):
    result = subprocess.run([java, "-version"], capture_output=True, text=True)
    return (result.stderr or result.stdout).splitlines()[0] if (result.stderr or result.stdout) else None


def openrouter_price(remote_model):
    """USD per prompt and per completion token of an OpenRouter model, from its public model list."""
    with urllib.request.urlopen("https://openrouter.ai/api/v1/models", timeout=30) as response:
        for model in json.load(response)["data"]:
            if model["id"] == remote_model:
                return float(model["pricing"]["prompt"]), float(model["pricing"]["completion"])
    return None


def last_lines(path, count=20):
    return path.read_text(encoding="utf-8", errors="replace").splitlines()[-count:]


def utc_now():
    return time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())


def openrouter_usage():
    key = os.environ.get("OPENROUTER_API_KEY")
    if not key:
        return None
    request = urllib.request.Request("https://openrouter.ai/api/v1/credits",
                                     headers={"Authorization": "Bearer " + key})
    with urllib.request.urlopen(request, timeout=30) as response:
        return json.load(response)["data"]["total_usage"]


def cut_off_files(log_path, work, out_dir):
    """Paths whose generation hit the maxOutputTokens budget (the provider's WARN line), relative to the
    corpus for file summaries and to the model's output tree for package and project summaries."""
    prefixes = [str(work) + os.sep, str(out_dir) + os.sep, out_dir.as_posix() + "/"]
    files = []
    for line in log_path.read_text(encoding="utf-8", errors="replace").splitlines():
        match = re.search(r"Generated text for (.+?) was cut off at the", line)
        if match:
            path = match.group(1)
            for prefix in prefixes:
                path = path.replace(prefix, "")
            files.append(path.replace(os.sep, "/"))
    return files


def run_model(args, models, profile, model):
    key = model["key"]
    definition = ai_definition(models, model, args.model_dir, args.seed, args.chars_per_token)
    rules = file_rules(profile, key, args.facts, args.oversize or None, args.max_chunks)
    out_dir = Path(args.out) / key
    if out_dir.exists():
        shutil.rmtree(out_dir)
    log_dir = Path(args.work).parent / "logs" / key
    log_dir.mkdir(parents=True, exist_ok=True)
    classpath = Path(args.cp).read_text(encoding="utf-8").strip()
    if args.prepend:
        classpath = args.prepend + os.pathsep + classpath
    env = {k: v for k, v in os.environ.items() if k not in ("OPENROUTER_API_KEY", "HF_TOKEN")}
    if definition.get("apiKeyEnv"):
        env[definition["apiKeyEnv"]] = os.environ.get(definition["apiKeyEnv"], "")

    meta = {"key": key, "name": model["name"], "backend": model["backend"],
            # The GGUF by file name only: where the models sit on the benchmark machine is not data.
            "definition": {k: (Path(v).name if k == "modelPath" else v)
                           for k, v in definition.items() if k != "apiKeyEnv"},
            "javaCommit": None if args.corpus else args.commit,
            "corpus": Path(args.corpus).name if args.corpus else "srcmorph",
            "profile": args.profile, "facts": args.facts, "oversize": args.oversize or None,
            "maxChunks": args.max_chunks, "profileHashes": profile_hashes(profile, rules),
            "cli": cli_jar(classpath), "java": java_version(args.java), "startedAt": utc_now(),
            "phases": {}}
    run_record = log_dir / "run-record.json"
    if run_record.exists():
        run_record.unlink()
    usage_before = openrouter_usage() if model["backend"] == "openrouter" else None
    for phase in PHASES:
        config_path = log_dir / f"config_{phase}.json"
        config_path.write_text(json.dumps(phase_config(phase, Path(args.work), out_dir, profile, definition,
                                                       args.facts, args.oversize or None, args.max_chunks,
                                                       run_record),
                                          indent=1), encoding="utf-8")
        start = time.time()
        with open(log_dir / f"{phase}.log", "w", encoding="utf-8") as log:
            rc = subprocess.run([args.java, "-cp", classpath, "net.ladenthin.srcmorph.cli.Main", str(config_path)],
                                stdout=log, stderr=subprocess.STDOUT, env=env).returncode
        meta["phases"][phase] = {"exit": rc, "seconds": round(time.time() - start, 1),
                                 "cutOff": cut_off_files(log_dir / f"{phase}.log", Path(args.work), out_dir)}
        print(f"{key} {phase} exit {rc} in {meta['phases'][phase]['seconds']} s", flush=True)
        if rc != 0:
            meta["phases"][phase]["errorTail"] = last_lines(log_dir / f"{phase}.log")
            break
    meta["finishedAt"] = utc_now()
    if run_record.exists():
        record = json.loads(run_record.read_text(encoding="utf-8"))
        meta["fileTokens"] = record["totals"]
        meta["finishReasons"] = dict(sorted(
            (reason, sum(1 for f in record["files"] if f["finishReason"] == reason))
            for reason in {f["finishReason"] for f in record["files"] if f["calls"]}))
        if model["backend"] == "openrouter":
            price = openrouter_price(definition["remoteModel"])
            if price is not None:
                totals = record["totals"]
                meta["tokenCostUsd"] = round(totals["promptTokens"] * price[0]
                                             + totals["completionTokens"] * price[1], 6)
    if usage_before is not None:
        meta["costUsd"] = round(openrouter_usage() - usage_before, 6)
    meta["files"] = sum(1 for _ in out_dir.rglob("*.ai.md")) if out_dir.exists() else 0
    Path(args.meta).mkdir(parents=True, exist_ok=True)
    (Path(args.meta) / f"{key}.json").write_text(json.dumps(meta, indent=1) + "\n", encoding="utf-8")
    return meta


def validate(parser, args, models):
    """The option combinations srcmorph would reject late or ignore silently."""
    if args.max_chunks is not None and args.oversize != "sampleChunks":
        parser.error("--max-chunks applies only to --oversize sampleChunks (mapReduce reads every chunk)")
    if args.oversize == "sampleChunks" and (args.max_chunks is None or args.max_chunks < 1):
        parser.error("--oversize sampleChunks needs --max-chunks of at least 1")
    if args.chars_per_token is not None and args.chars_per_token <= 0:
        parser.error("--chars-per-token must be above 0 (0 would switch srcmorph to the static maxInputChars)")
    known = {m["key"] for m in models["models"]}
    unknown = [k for k in args.models.split(",") if k and k not in known]
    if unknown:
        parser.error(f"unknown model key(s): {', '.join(unknown)}")
    if args.backend and args.backend not in models["backends"]:
        parser.error(f"unknown backend: {args.backend}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--cp", required=True)
    parser.add_argument("--java", default="java")
    parser.add_argument("--prepend", default="")
    parser.add_argument("--models", default="")
    parser.add_argument("--backend", default="")
    parser.add_argument("--model-dir", default="")
    parser.add_argument("--commit", default=JAVA_COMMIT)
    parser.add_argument("--seed", type=int, default=None)
    parser.add_argument("--out", default=str(HERE.parent / "outputs-2026-10"))
    parser.add_argument("--meta", default=str(HERE / "runs"))
    parser.add_argument("--work", required=True)
    parser.add_argument("--profile", default="v3")
    parser.add_argument("--profile-dir", default=str(PROFILES))
    parser.add_argument("--corpus", default="")
    parser.add_argument("--facts", action="store_true")
    parser.add_argument("--oversize", default="", choices=("",) + OVERSIZE)
    parser.add_argument("--max-chunks", type=int, default=None)
    parser.add_argument("--chars-per-token", type=float, default=None)
    args = parser.parse_args()
    models = json.loads((HERE / "models.json").read_text(encoding="utf-8"))
    validate(parser, args, models)

    profile = Path(args.profile_dir).resolve() / args.profile
    for prompt_key in PROMPT_KEYS:
        if not (profile / f"{prompt_key}.md").is_file():
            sys.exit(f"profile {args.profile}: {profile / prompt_key}.md is missing")
    wanted = [m for m in models["models"]
              if (args.models and m["key"] in args.models.split(",")) or (args.backend and m["backend"] == args.backend)]
    if not wanted:
        sys.exit("no model selected")
    if not args.model_dir and any(m["backend"] == "local" for m in wanted):
        sys.exit("--model-dir is required for the local GGUF models")
    for model in wanted:
        env = models["backends"][model["backend"]].get("apiKeyEnv")
        if env and not os.environ.get(env):
            sys.exit(f"{model['key']}: the environment variable {env} is not set")
    prepare_work(Path(args.work), args.commit, args.corpus or None)
    for model in wanted:
        run_model(args, models, profile, model)


if __name__ == "__main__":
    main()
