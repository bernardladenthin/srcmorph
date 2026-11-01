# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Runs the model-card benchmark for one or more models through the srcmorph CLI.

The corpus is 12 Java files of srcmorph itself (taken from a fixed commit with `git archive`) plus
the SQL and other fixtures under ../corpus. Every model gets the same three phases with the same
prompts: GenerateFileIndex (prompt routed by extension: .java, .sql, everything else to the
fallback), AggregatePackages, AggregateProject. The prompts are the files of one profile under the
repository's profiles/ directory (--profile, default v2, the current one), passed to srcmorph as its
profileDirectory, so the benchmark runs exactly the files the plugin's self-test runs. The
.ai.md tree goes to <out>/<model key>/, timings and the OpenRouter cost to <meta>/<model key>.json.

API keys are never read from a file here: the caller puts OPENROUTER_API_KEY / HF_TOKEN into the
environment, and a model only receives the variable its backend names.

Usage:
  python run.py --cp <classpath file> --java <java executable> --models gpt-6-luna,gemma-4-31b
  python run.py ... --backend openrouter        (every model of one backend)
Options: --profile <profile under profiles/> (default v2; it needs the five prompt keys' files),
         --profile-dir <directory holding the profiles> (default the repository's profiles/; an
         earlier prompt set is restored from the history into a directory of its own, see
         profiles/README.md),
         --corpus <directory with java/, sql/, other/> (default: the srcmorph Java snapshot plus
         ../corpus; another corpus of the same layout is scored with score.py --facts),
         --model-dir <GGUF dir> (local models), --prepend <jars> (the CLI jar, the CUDA natives),
         --commit <git rev of the Java snapshot>, --seed <n> (replaces the local backend's seed,
         for repeat runs), --out, --meta, --work.
"""
import argparse
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
EXTENSIONS = [".java", ".sql", ".h", ".c", ".cl", ".py", ".sh"]
PHASES = ("GenerateFileIndex", "AggregatePackages", "AggregateProject")


def prepare_work(work, commit, corpus=None):
    """Java snapshot under work/java/{config,provider}, fixtures under work/sql and work/other; with a
    corpus directory, its java/, sql/ and other/ trees instead."""
    if work.exists():
        shutil.rmtree(work)
    if corpus is not None:
        for kind in ("java", "sql", "other"):
            shutil.copytree(Path(corpus) / kind, work / kind)
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


def ai_definition(models, model, model_dir, seed=None):
    backend = models["backends"][model["backend"]]
    definition = {"key": model["key"]}
    definition.update(models["common"])
    definition.update(backend)
    definition.update(model["settings"])
    if seed is not None and "seed" in definition:
        definition["seed"] = seed
    if model["backend"] == "local":
        definition["modelPath"] = str(Path(model_dir) / definition["modelPath"])
    return definition


def phase_config(phase, work, out_dir, profile, definition):
    key = definition["key"]
    if phase == "GenerateFileIndex":
        rules = [
            {"id": "java", "promptKey": "file-body-java", "aiDefinitionKey": key,
             "condition": {"extensions": [".java"]}},
            {"id": "sql", "promptKey": "file-body-sql", "aiDefinitionKey": key,
             "condition": {"extensions": [".sql"]}},
            {"id": "fallback", "promptKey": "file-body-fallback", "aiDefinitionKey": key, "fallback": True},
        ]
    else:
        prompt = "package-body" if phase == "AggregatePackages" else "project-body"
        rules = [{"promptKey": prompt, "aiDefinitionKey": key}]
    return {
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
    definition = ai_definition(models, model, args.model_dir, args.seed)
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
            "profile": args.profile, "phases": {}}
    usage_before = openrouter_usage() if model["backend"] == "openrouter" else None
    for phase in PHASES:
        config_path = log_dir / f"config_{phase}.json"
        config_path.write_text(json.dumps(phase_config(phase, Path(args.work), out_dir, profile, definition),
                                          indent=1), encoding="utf-8")
        start = time.time()
        with open(log_dir / f"{phase}.log", "w", encoding="utf-8") as log:
            rc = subprocess.run([args.java, "-cp", classpath, "net.ladenthin.srcmorph.cli.Main", str(config_path)],
                                stdout=log, stderr=subprocess.STDOUT, env=env).returncode
        meta["phases"][phase] = {"exit": rc, "seconds": round(time.time() - start, 1),
                                 "cutOff": cut_off_files(log_dir / f"{phase}.log", Path(args.work), out_dir)}
        print(f"{key} {phase} exit {rc} in {meta['phases'][phase]['seconds']} s", flush=True)
        if rc != 0:
            break
    if usage_before is not None:
        meta["costUsd"] = round(openrouter_usage() - usage_before, 6)
    meta["files"] = sum(1 for _ in out_dir.rglob("*.ai.md")) if out_dir.exists() else 0
    Path(args.meta).mkdir(parents=True, exist_ok=True)
    (Path(args.meta) / f"{key}.json").write_text(json.dumps(meta, indent=1) + "\n", encoding="utf-8")
    return meta


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
    parser.add_argument("--profile", default="v2")
    parser.add_argument("--profile-dir", default=str(PROFILES))
    parser.add_argument("--corpus", default="")
    args = parser.parse_args()

    models = json.loads((HERE / "models.json").read_text(encoding="utf-8"))
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
    prepare_work(Path(args.work), args.commit, args.corpus or None)
    for model in wanted:
        run_model(args, models, profile, model)


if __name__ == "__main__":
    main()
