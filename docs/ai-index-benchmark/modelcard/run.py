# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Runs the model-card benchmark for one or more models through the srcmorph CLI.

The corpus is 12 Java files of srcmorph itself (taken from a fixed commit with `git archive`) plus
the SQL and other fixtures under ../corpus. Every model gets the same three phases with the same
prompts: GenerateFileIndex, AggregatePackages, AggregateProject. The file phase routes a file to the
Java rule (--java-prompt), the SQL rule, a rule per kind of LANGUAGES whose prompt the profile has (for
profile v3: shell, Perl, the XML kinds, the class-based languages, the views, the contracts and the ops
files, some by name or content at a higher priority), and everything else to the fallback; a file the
walk collects by name (FILE_NAMES, globs) takes its rule the same way. The prompts are the files of one profile under the
repository's profiles/ directory (--profile, default v3, the current one), passed to srcmorph as its
profileDirectory, so the benchmark runs exactly the files the plugin's self-test runs. The
.ai.md tree goes to <out>/<model key>/, timings and the OpenRouter cost to <meta>/<model key>.json.

Needs Python 3.11 or later: the rules' Java regexes, which the tools evaluate with re, use
possessive quantifiers.

API keys are never read from a file here: the caller puts OPENROUTER_API_KEY / HF_TOKEN /
COPILOT_GATEWAY_API_KEY into the environment, and a model only receives the variable its own
definition names (every other apiKeyEnv of models.json is dropped from the CLI's environment).

Usage:
  python run.py --cp <classpath file> --java <java executable> --models gpt-6-luna,gemma-4-31b
  python run.py ... --backend openrouter        (every model of one backend)
Options: --profile <profile under profiles/> (default v3; it needs the five prompt keys' files),
         --java-prompt <key> (the Java rule's prompt, default file-body-oop, the one Java, Kotlin,
         Groovy and Scala share; the Java EE rule takes it too; file-body-java for a profile of the
         history before 2026-11-04),
         --profile-dir <directory holding the profiles> (default the repository's profiles/; an
         earlier prompt set is restored from the history into a directory of its own, see
         profiles/README.md),
         --corpus <directory with java/, sql/, other/> (default: the srcmorph Java snapshot plus
         ../corpus; another corpus of the same layout is scored with score.py --facts; a missing
         kind stays empty),
         --facts (the Java and SQL rules name java-facts and sql-facts, every rule of LANGUAGES
         the <name>-facts.tsv of the profile when it has one, e.g. kotlin-facts, pom-facts, k8s-facts;
         a kind with counters but no prompt of its own gets a rule with them and the fallback
         prompt; the card ran without them, so such a run is not comparable to it),
         --oversize <fail|sample|mapReduce|sampleChunks|deterministic> and --max-chunks <n> (the
         strategy for a
         file larger than the model's window, on every file rule; the default fails such a file),
         --chars-per-token <n> (replaces every model's estimate: a data file such as a SQL dump has
         fewer characters per token than code, and a window filled at 4 is refused by the provider),
         --model-dir <GGUF dir> (local models), --prepend <jars> (the CLI jar, the CUDA natives),
         --parts <dir> (keeps every map and reduce answer of a chunked file below <dir>/<model key>/,
         srcmorph's partsDirectory), --commit <git rev of the Java snapshot>, --seed <n> (every model's seed, for repeat runs;
         the OpenAI-compatible provider sends it too), --out (a model's earlier summaries there are
         deleted, a directory holding anything but .ai.md files is refused), --meta (the card's runs/
         only with the card's --out, another --out names a directory of its own), --work (created
         by run.py and marked so; an existing directory without the marker is refused, never deleted).

<meta>/<model key>.json holds the model definition as sent (without the key variable), the corpus,
profile and options, the SHA-256 of every prompt and fact file the rules name, the srcmorph-cli jar
and Java version, start and end (UTC), per phase its exit code, seconds, cut-off paths and on a
failure the log's last lines, and from srcmorph's run record (runRecordFile) the file phase's calls,
prompt / cached / completion tokens and finish reasons; for an OpenRouter model also the cost of
the file phase's tokens at the model's list prices, cached prompt tokens at the cache-read price
(tokenCostUsd: an upper bound, the provider OpenRouter routes to may charge less, and the package and
project phases are not in it), next to the account's usage delta (costUsd, all three phases, which
also books whatever else used the key meanwhile, and late). The meta is written before the costs are
looked up and again with them; a lookup that fails (no network, a timeout) leaves its cost null, with
the reason under costErrors, and the next model still runs.
"""
import argparse
import hashlib
import http.client
import json
import math
import os
import platform
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
PROMPT_KEYS = ("file-body-sql", "file-body-fallback", "package-body", "project-body")
# The prompt of the Java rule (--java-prompt): the one class-based languages share. Up to 2026-11-04 it
# was file-body-java, which an earlier profile restored from the history still has.
JAVA_PROMPT = "file-body-oop"
# The prompts appended to a rule's prompt in a map and a reduce call, when the profile has them.
CHUNK_PROMPT_KEYS = {"mapPromptKey": "map-chunk", "reducePromptKey": "reduce-parts"}
# What every rule does with an incomplete answer: ask once more. Since 2026-11-05; the runs before it
# failed such a file at once (gpt-oss-20b ran away to the output budget on one big file in six, and
# summarized the same file in the next run).
ON_INCOMPLETE = "retryOnce"

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
EXTENSIONS = [".java", ".sql", ".h", ".c", ".cl", ".py", ".sh", ".bash", ".ksh", ".pl", ".pm", ".xml", ".xsd", ".xsl", ".xslt",
              ".wsdl", ".kt", ".kts", ".groovy", ".gvy", ".scala", ".jsp", ".jspf", ".tag", ".xhtml", ".bpmn", ".dmn",
              ".yml", ".yaml", ".proto", ".avsc", ".graphql", ".graphqls", ".gql", ".tf", ".hcl", ".gradle",
              ".properties", ".ps1", ".psm1", ".bat", ".cmd", ".ts", ".mts", ".cts"]
# Files the runs index by name: without an extension, or a manifest no extension of theirs selects.
# srcmorph reads them as globs over the file name, in any case (the dockerfile rule's Dockerfile.*).
FILE_NAMES = ["Dockerfile", "Dockerfile.*", "Containerfile", "package.json", "pyproject.toml", "openapi*.json",
              "swagger*.json", "asyncapi*.json"]
# as the self-test, its CLI twin and the Luna example: BuildKit's Dockerfile.dockerignore, which
# Dockerfile.* takes, is no source
EXCLUDES = ["**/*.dockerignore"]


def name_glob(glob):
    """A fileNames glob as srcmorph compiles it (ConditionEvaluator.compileFileName): over the file name,
    in any case; `*` and `?` stay inside the name, `{a,b}` is either."""
    out, depth = "", 0
    for char in glob:
        if char == "*":
            out += "[^/]*"
        elif char == "?":
            out += "[^/]"
        elif char == "{":
            out, depth = out + "(?:", depth + 1
        elif char == "}" and depth:
            out, depth = out + ")", depth - 1
        elif char == "," and depth:
            out += "|"
        else:
            out += re.escape(char)
    return re.compile(out, re.I)


def indexes(file_name):
    """Whether the runs' walk collects a file of this name: by an extension of EXTENSIONS or a glob of
    FILE_NAMES, both in any case, as srcmorph's SourceFileIndexer selects it, and no glob of EXCLUDES."""
    lower = file_name.lower()
    return ((any(lower.endswith(extension.lower()) for extension in EXTENSIONS)
             or any(name_glob(name).fullmatch(file_name) for name in FILE_NAMES))
            and not any(path_glob(glob).fullmatch(file_name) for glob in EXCLUDES))


def path_glob(glob):
    """A pathGlob as srcmorph compiles it (support.Glob): over the path below the base directory, "/" as
    the separator, in its case; `**/` matches no directory too, `*` and `?` stay inside a segment, `{a,b}`
    is either."""
    out, depth, i = "", 0, 0
    while i < len(glob):
        char = glob[i]
        if glob.startswith("**/", i):
            out, i = out + "(?:.*/)?", i + 3
            continue
        if glob.startswith("**", i):
            out, i = out + ".*", i + 2
            continue
        if char == "*":
            out += "[^/]*"
        elif char == "?":
            out += "[^/]"
        elif char == "{":
            out, depth = out + "(?:", depth + 1
        elif char == "}" and depth:
            out, depth = out + ")", depth - 1
        elif char == "," and depth:
            out += "|"
        else:
            out += re.escape(char)
        i += 1
    return re.compile(out)


# The characters of a file's head a contentMatches condition reads (SrcMorphConfiguration's
# DEFAULT_CONTENT_HEAD_CHARS).
CONTENT_HEAD_CHARS = 16_384


def condition_matches(condition, rel_path, head):
    """Whether a file matches a rule's condition as srcmorph's ConditionEvaluator reads it: and, or and
    not groups over extensions and fileNames (the file name, in any case), pathGlob (the path below the
    base directory) and contentMatches (found in the head). Another condition is refused, so a rule that
    starts to use one is not read wrong."""
    if "and" in condition:
        return all(condition_matches(c, rel_path, head) for c in condition["and"]["conditions"])
    if "or" in condition:
        return any(condition_matches(c, rel_path, head) for c in condition["or"]["conditions"])
    if "not" in condition:
        return not condition_matches(condition["not"], rel_path, head)
    name = rel_path.rsplit("/", 1)[-1]
    if "extensions" in condition:
        return any(name.lower().endswith(extension.lower()) for extension in condition["extensions"])
    if "fileNames" in condition:
        return any(name_glob(glob).fullmatch(name) for glob in condition["fileNames"])
    if "pathGlob" in condition:
        return path_glob(condition["pathGlob"]).fullmatch(rel_path) is not None
    if "contentMatches" in condition:
        # ASCII, as java.util.regex reads \w, \s, \d and (?i) without UNICODE_CHARACTER_CLASS
        return re.search(condition["contentMatches"], head, re.ASCII) is not None
    raise ValueError(f"a condition run.py does not read: {condition}")


def skipped(rel_path, text):
    """Whether the secrets rule (SECRETS_RULE) keeps a file from every model, so the run writes no summary
    for it: rel_path below the corpus, with "/", and the file's text."""
    return condition_matches(SECRETS_RULE["condition"], rel_path, text[:CONTENT_HEAD_CHARS])


# A language gets a rule of its own when the profile has its prompt (file-body-<prompt>.md) or when
# --facts asks for its counters (<name>-facts.tsv); otherwise the fallback rule summarizes it. A POM
# matches pom and xml; its priority gives it the Maven counters, with the XML prompt.
XML_EXTENSIONS = [".xml", ".xsd", ".xsl", ".xslt"]
# The enterprise kinds (corpus-enterprise/) take a prompt a kind shares (None: the Java rule's), the more
# specific ones by name or by content at priority 10 over their extension's rule.
EE_IMPORTS = (r"(?m)(?:^import[ \t]+(?:static[ \t]+)?|@)(?:javax|jakarta)\.(?:ejb|persistence|ws\.rs|servlet|jws"
              r"|xml\.ws|inject|transaction|enterprise|annotation\.security)\.")
YAML_EXTENSIONS = [".yml", ".yaml"]


def _and(*conditions):
    return {"and": {"conditions": list(conditions)}}


def _or(*conditions):
    return {"or": {"conditions": list(conditions)}}


LANGUAGES = (("shell", {"extensions": [".sh", ".bash", ".ksh"]}, "shell", 0),
             ("perl", {"extensions": [".pl", ".pm"]}, "perl", 0),
             ("pom", {"pathGlob": "**/*pom.xml"}, "xml", 10),
             ("xml", {"extensions": XML_EXTENSIONS + [".wsdl"]}, "xml", 0),
             ("java-ee", _and({"extensions": [".java"]}, {"contentMatches": EE_IMPORTS}), None, 10),
             ("kotlin", {"extensions": [".kt", ".kts"]}, "oop", 0),
             ("groovy", {"extensions": [".groovy", ".gvy"]}, "oop", 0),
             ("scala", {"extensions": [".scala"]}, "oop", 0),
             # measured 2026-11-05 on a TypeScript repository: the oop prompt names more of the API than the fallback
             ("typescript", {"extensions": [".ts", ".mts", ".cts"]}, "oop", 0),
             ("jee", {"fileNames": ["web.xml", "web-fragment.xml", "ejb-jar.xml", "application.xml"]}, "xml", 10),
             ("persistence", {"fileNames": ["persistence.xml", "orm.xml", "*.hbm.xml"]}, "xml", 10),
             ("mybatis", _and({"extensions": [".xml"]}, {"contentMatches": r"mybatis\.org//DTD Mapper"}), "xml", 10),
             ("integration", _and({"extensions": [".xml"]}, {"contentMatches": r"camel\.apache\.org/schema|"
                                  r"springframework\.org/schema/integration|mulesoft\.org/schema/mule|"
                                  r"ws\.apache\.org/ns/synapse"}), "xml", 11),  # over jee's application.xml
             ("bpmn", _or({"extensions": [".bpmn"]}, {"fileNames": ["*.bpmn20.xml"]}), "xml", 10),
             ("dmn", {"extensions": [".dmn"]}, "xml", 10),
             ("wsdl", {"extensions": [".wsdl"]}, "xml", 10),
             ("jsp", {"extensions": [".jsp", ".jspf", ".tag"]}, "view", 0),
             ("facelets", {"extensions": [".xhtml"]}, "view", 0),
             ("openapi", _or(_and({"extensions": YAML_EXTENSIONS},
                                  {"contentMatches": r"""(?m)^["']?(?:openapi|swagger)["']?[ \t]*:[ \t]*["']?\d"""}),
                             {"fileNames": ["openapi*.json", "swagger*.json"]}), "contract", 10),
             ("asyncapi", _or(_and({"extensions": YAML_EXTENSIONS}, {"contentMatches": r"""(?m)^["']?asyncapi["']?[ \t]*:"""}),
                              {"fileNames": ["asyncapi*.json"]}), "contract", 10),
             ("proto", {"extensions": [".proto"]}, "contract", 0),
             ("avro", {"extensions": [".avsc"]}, "contract", 0),
             ("graphql", {"extensions": [".graphql", ".graphqls", ".gql"]}, "contract", 0),
             ("actions", {"pathGlob": "**/.github/{workflows,actions}/**/*.{yml,yaml}"}, "ops", 10),
             # the kind as k8s-facts.tsv counts it: quoted or not
             ("k8s", _and({"extensions": YAML_EXTENSIONS}, {"contentMatches": r"""(?m)^kind:[ \t]*["']?[A-Z]"""}), "ops", 5),
             ("dockerfile", {"fileNames": ["Dockerfile", "Dockerfile.*", "Containerfile"]}, "ops", 0),
             ("camel-yaml", _and({"extensions": YAML_EXTENSIONS},
                                {"contentMatches": r"(?m)^-[ \t]+(?:(?:route|routeTemplate|templatedRoute|rest)[ \t]*:|from[ \t]*:[ \t]*"
                                                 r"""(?:["']?(?!https?:)[a-z][\w+.-]*:|(?:#[^\n]*)?\r?\n"""
                                                 r"(?:[ \t]*+(?:#[^\n]*+)?\r?\n|[ \t]++(?:id|description)[ \t]*+:[^\n]*+\n)*+"
                                                 r"[ \t]+(?:uri|steps|parameters)[ \t]*:))"}),
              "ops", 10),
             ("compose", {"fileNames": ["docker-compose*.{yml,yaml}", "compose*.{yml,yaml}"]}, "ops", 10),
             ("ansible", _or({"pathGlob": "**/roles/*/{tasks,handlers,defaults,vars,meta}/*.{yml,yaml}"},
                             _and({"extensions": YAML_EXTENSIONS},
                                  {"contentMatches": r"(?m)^-[ \t]++(?:name[ \t]*+:[^\n]*+\n[ \t]++)?hosts[ \t]*+:"})),
              "ops", 5),
             ("terraform", {"extensions": [".tf", ".hcl"]}, "ops", 0),
             # build scripts of both DSLs: a .gradle.kts over the kotlin rule
             ("gradle", _or({"extensions": [".gradle"]}, {"fileNames": ["*.gradle.kts"]}), "ops", 10),
             ("properties", {"extensions": [".properties"]}, "ops", 0),
             # no prompt of their own: the fallback prompt, with their counters under --facts
             ("powershell", {"extensions": [".ps1", ".psm1"]}, "fallback", 0),
             ("batch", {"extensions": [".bat", ".cmd"]}, "fallback", 0),
             # measured 2026-11-06 on an extension manifest: with the ops prompt's npm line gpt-oss-20b names more
             # than with the fallback (mean 36 of 45 against 25), GPT-6 Luna all of them
             ("npm", {"fileNames": ["package.json"]}, "ops", 10),
             ("pyproject", {"fileNames": ["pyproject.toml"]}, "ops", 10),
             ("ops", {"extensions": YAML_EXTENSIONS}, "ops", 0))
# A Kubernetes manifest of the kind Secret, quoted or not, as the secrets rule reads it.
K8S_SECRET = r"""(?m)^kind:[ \t]*["']?Secret\b"""
# A Helm chart's template: it names the values it renders, it holds none.
HELM_TEMPLATE = _and({"pathGlob": "**/templates/**"}, {"contentMatches": r"\{\{"})
# Files that hold credentials never reach a model: the secrets rule of the self-test and the examples,
# at the highest priority, so no other rule takes them.
SECRETS_RULE = {"id": "secrets", "skip": True, "priority": 1000, "condition": _or(
    {"fileNames": [".env", ".env.*", "*.tfvars", "*.pkrvars.hcl", "*.tfstate", "*.tfstate.backup",
                   "settings-security.xml", "*.pem", "*.key", "id_rsa*", "id_ed25519*"]},
    # Maven's settings.xml (server passwords) by its root element, not an Android res/xml/settings.xml
    _and({"fileNames": ["settings.xml"]}, {"contentMatches": r"<settings[\s>]"}),
    # a properties file that gives a password, secret, token, API key, private key or credentials, a
    # secret key too, a value after =, : or a blank (not a whole ${NAME}, @NAME@ or #{...} placeholder,
    # a flag, a number or a log level, nor a key that only starts so)
    _and({"extensions": [".properties"]},
         {"contentMatches": r"(?im)^[ \t]*+[\w.\-]*?(?:password|passwd|pwd|passphrase|(?<![^._\s-])pass|secret"
                            r"|token|api[._-]?key|private[._-]?key|credentials?)(?:[._-]?(?:access[._-]?)?key)?"
                            r"(?:[ \t]*+[=:]|[ \t]++(?![=:]))[ \t]*+"
                            r"""(?!["']?\$\{[\w.\-]+\}["']?\s*$|@[\w.\-]+@\s*$|#\{[^}\n]*\}\s*$"""
                            r"|(?:true|false|yes|no|on|off|\d+|trace|debug|info|warn|error|fatal|all)\s*$)\S"}),
    # a Kubernetes Secret, whatever its name (deploy/postgres.yaml), but not a Helm chart's template
    _and({"extensions": YAML_EXTENSIONS}, {"contentMatches": K8S_SECRET}, {"not": HELM_TEMPLATE}),
    # named for a vault, secrets or credentials, but not a CI workflow or action, a Helm chart's template
    # or a Kubernetes manifest of another kind than Secret
    _and({"fileNames": ["vault*.{yml,yaml}",
                        "*{secret,secrets,credential,credentials}*.{yml,yaml,properties,json,xml,conf,env,hcl}"]},
         {"not": _or({"pathGlob": "**/.github/**"},
                     HELM_TEMPLATE,
                     _and({"contentMatches": r"""(?m)^kind:[ \t]*["']?[A-Z]"""},
                          {"not": {"contentMatches": K8S_SECRET}}))}))}
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


def chunk_prompts(profile):
    """The map and reduce prompt keys a chunked rule names: those whose file the profile has."""
    return {field: key for field, key in CHUNK_PROMPT_KEYS.items() if (Path(profile) / f"{key}.md").is_file()}


def file_rules(profile, key, facts=False, oversize=None, max_chunks=None, java_prompt=JAVA_PROMPT):
    """The GenerateFileIndex rules: java (with java_prompt), sql, a rule per language that has a prompt
    or (with facts) counters of its own in the profile, and the fallback. With oversize every rule takes
    that strategy, and with mapReduce or sampleChunks the profile's map and reduce prompts."""
    rules = [{"id": "java", "promptKey": java_prompt, "condition": {"extensions": [".java"]}},
             {"id": "sql", "promptKey": "file-body-sql", "condition": {"extensions": [".sql"]}}]
    if facts:
        rules[0]["factsKey"], rules[1]["factsKey"] = "java-facts", "sql-facts"
    for name, condition, prompt, priority in LANGUAGES:
        prompt_key = java_prompt if prompt is None else f"file-body-{prompt}"
        own_prompt = (Path(profile) / f"{prompt_key}.md").is_file()
        own_facts = facts and (Path(profile) / f"{name}-facts.tsv").is_file()
        if own_prompt or own_facts:
            rule = {"id": name, "promptKey": prompt_key if own_prompt else "file-body-fallback",
                    "condition": json.loads(json.dumps(condition))}
            if priority:
                rule["priority"] = priority
            if own_facts:
                rule["factsKey"] = f"{name}-facts"
            rules.append(rule)
    rules.append({"id": "fallback", "promptKey": "file-body-fallback", "fallback": True})
    for rule in rules:
        rule["aiDefinitionKey"] = key
        rule["onIncomplete"] = ON_INCOMPLETE
        if oversize:
            rule["onOversize"] = oversize
            if max_chunks is not None:
                rule["maxChunks"] = max_chunks
            if oversize in ("mapReduce", "sampleChunks"):
                rule.update(chunk_prompts(profile))
    # a skip rule needs no prompt, model or strategy
    rules.insert(0, json.loads(json.dumps(SECRETS_RULE)))
    return rules


def phase_config(phase, work, out_dir, profile, definition, facts=False, oversize=None, max_chunks=None,
                 run_record=None, java_prompt=JAVA_PROMPT, parts=None):
    """The CLI configuration of one phase. The file phase's rules are srcMorph's rules; the
    package and project phases take their one rule from packageRules and
    projectRules, as the CLI reads them."""
    key = definition["key"]
    rules = file_rules(profile, key, facts, oversize, max_chunks, java_prompt)
    config = {
        "command": phase,
        "srcMorph": {
            "baseDirectory": str(work),
            "outputDirectory": str(out_dir),
            "subtrees": ["java", "sql", "other"],
            "fileExtensions": EXTENSIONS,
            "fileNames": FILE_NAMES,
            "excludes": EXCLUDES,
            "generationProvider": definition["provider"],
            "projectName": "srcmorph-modelcard",
            "force": True,
            "profileDirectory": str(profile),
            "aiDefinitions": [definition],
            "rules": rules,
        },
    }
    if phase == "AggregatePackages":
        config["packageRules"] = [
            {"promptKey": "package-body", "aiDefinitionKey": key, "onIncomplete": ON_INCOMPLETE,
             **chunk_prompts(profile)}]
    elif phase == "AggregateProject":
        config["projectRules"] = [
            {"promptKey": "project-body", "aiDefinitionKey": key, "onIncomplete": ON_INCOMPLETE,
             **chunk_prompts(profile)}]
    if run_record is not None and phase == "GenerateFileIndex":
        config["srcMorph"]["runRecordFile"] = str(run_record)
    if parts is not None and phase == "GenerateFileIndex":
        config["srcMorph"]["partsDirectory"] = str(parts)
    return config


def sha256_of(path):
    return hashlib.sha256(Path(path).read_bytes()).hexdigest()


def profile_hashes(profile, rules):
    """The SHA-256 of every prompt and fact file a rule names (profile/<key>.md, profile/<key>.tsv)."""
    hashes = {}
    for rule in rules:
        if rule.get("skip"):
            continue
        named = [rule["promptKey"] + ".md", rule.get("factsKey", "") and rule["factsKey"] + ".tsv"]
        named += [rule[field] + ".md" for field in CHUNK_PROMPT_KEYS if field in rule]
        for name in named:
            if name and (Path(profile) / name).is_file():
                hashes[name] = sha256_of(Path(profile) / name)
    for name in ("package-body.md", "project-body.md"):
        hashes[name] = sha256_of(Path(profile) / name)
    # the phase rules name the map and reduce prompts whenever the profile has them
    for key in chunk_prompts(profile).values():
        hashes[key + ".md"] = sha256_of(Path(profile) / (key + ".md"))
    return dict(sorted(hashes.items()))


def cli_jar(classpath):
    """The srcmorph-cli jar of a classpath: its file name and SHA-256 (the path is machine data)."""
    for entry in classpath.split(os.pathsep):
        if re.search(r"srcmorph-cli-[^" + re.escape(os.sep) + r"/]*\.jar$", entry) and Path(entry).is_file():
            return {"jar": Path(entry).name, "sha256": sha256_of(entry)}
    return None


def srcmorph_commit():
    """The commit of the repository the run is measured from, with "+dirty" when its tree has changes
    (the profile, the tools and the corpus are read from it); None outside a git checkout."""
    head = subprocess.run(["git", "-C", str(REPO), "rev-parse", "HEAD"], capture_output=True, text=True)
    if head.returncode != 0:
        return None
    dirty = subprocess.run(["git", "-C", str(REPO), "status", "--porcelain"], capture_output=True, text=True)
    return head.stdout.strip() + ("+dirty" if dirty.stdout.strip() else "")


def tree_hash(directory):
    """One SHA-256 over every file of a directory, by its path below it and its bytes, in path order:
    two runs over the same corpus have the same hash."""
    digest = hashlib.sha256()
    for path in sorted((p for p in Path(directory).rglob("*") if p.is_file()),
                       key=lambda p: p.relative_to(directory).as_posix()):
        digest.update(path.relative_to(directory).as_posix().encode("utf-8") + b"\0")
        digest.update(hashlib.sha256(path.read_bytes()).digest())
    return digest.hexdigest()


def java_version(java):
    result = subprocess.run([java, "-version"], capture_output=True, text=True)
    return (result.stderr or result.stdout).splitlines()[0] if (result.stderr or result.stdout) else None


def openrouter_price(remote_model):
    """USD per prompt, completion and cache-read token of an OpenRouter model, from its public model
    list (a model without a cache-read price reads its cache at the prompt price)."""
    with urllib.request.urlopen("https://openrouter.ai/api/v1/models", timeout=30) as response:
        for model in json.load(response)["data"]:
            if model["id"] == remote_model:
                pricing = model["pricing"]
                prompt = float(pricing["prompt"])
                return prompt, float(pricing["completion"]), float(pricing.get("input_cache_read", prompt))
    return None


def token_cost(totals, price):
    """The cost of a run record's tokens: uncached prompt, cached prompt and completion tokens."""
    prompt, completion, cache_read = price
    cached = totals.get("cachedPromptTokens", 0)
    return round((totals["promptTokens"] - cached) * prompt + cached * cache_read
                 + totals["completionTokens"] * completion, 6)


def last_lines(path, count=20):
    return path.read_text(encoding="utf-8", errors="replace").splitlines()[-count:]


def scrubbed(lines, root):
    """The lines with the run's directory (work, output and logs below it) written as <run>: where the
    benchmark ran is machine data, not part of the result."""
    forms = {str(Path(root).resolve()), str(Path(root))}
    forms |= {f.replace("\\", "/") for f in forms} | {f.replace("/", "\\") for f in forms}
    out = []
    for line in lines:
        for form in sorted(forms, key=len, reverse=True):
            line = line.replace(form, "<run>")
        out.append(line)
    return out


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


# What srcmorph logs for an answer cut off at maxOutputTokens: the file it does not write ("... (field
# 'x', TRUNCATED)", with the kind of the call), the answer it keeps under onIncomplete: keep, and the older
# provider line (neither of the two with the kind).
CUT_OFF_LINES = (
    re.compile(r"Incomplete answer for (?P<kind>file|package|project) '(?P<path>.+?)' \(field '[^']*', TRUNCATED\)"),
    re.compile(r"Incomplete answer for (?!a part of )(?P<path>.+?): The answer for .+? was cut off at the .+?; "
               r"kept as it is"),
    re.compile(r"Generated text for (?P<path>.+?) was cut off at the"),
)
# The summary srcmorph's package phase writes into every package directory (score.PACKAGE_FILE).
PACKAGE_FILE = "package.ai.md"


def package_summary(call_name, out_dir):
    """The package.ai.md a package call wrote, below the model's output tree, from the name srcmorph's
    PackageIndexer gives the call: the package's path below the output root (`java/config` is
    `java/config/package.ai.md`), the root package by the output directory's name (`package.ai.md`). A
    log of srcmorph before 2026-11 named the absolute package.ai.md, which reads as it is."""
    if call_name == PACKAGE_FILE or call_name.endswith("/" + PACKAGE_FILE):
        return call_name
    if call_name in ("", out_dir.name):
        return PACKAGE_FILE
    return call_name + "/" + PACKAGE_FILE


def cut_off_files(log_path, work, out_dir, phase=None):
    """Paths whose final answer hit the maxOutputTokens budget (srcmorph's WARN lines), relative to the
    corpus for file summaries and to the model's output tree for package and project summaries, as
    score.py names them. srcmorph names a file call by its path below the corpus (an older log by the
    absolute path), a package call by the package's path below the output root (package_summary) and
    the project call by the absolute project.ai.md. A call is a package call when its line says so, and
    in the AggregatePackages phase (a kept answer's line names no kind). A part summary of a map-reduce
    that was cut and kept is not one: the file's own answer was complete."""
    prefixes = [str(work) + os.sep, str(out_dir) + os.sep, out_dir.as_posix() + "/"]
    files = []
    for line in log_path.read_text(encoding="utf-8", errors="replace").splitlines():
        for pattern in CUT_OFF_LINES:
            match = pattern.search(line)
            if match:
                path = match.group("path")
                for prefix in prefixes:
                    path = path.replace(prefix, "")
                path = path.replace(os.sep, "/")
                kind = match.groupdict().get("kind")
                if kind == "package" or (kind is None and phase == "AggregatePackages"):
                    path = package_summary(path, out_dir)
                files.append(path)
                break
    return sorted(set(files))


# srcmorph's message when a file or package phase ends with summaries it did not write (an incomplete
# answer or a failed request); the phases after it still run
NOT_WRITTEN = {"GenerateFileIndex": "file(s) were not written", "AggregatePackages": "package(s) were not written"}


def files_not_written(log_path, phase="GenerateFileIndex"):
    """Whether a phase ended only because some of its summaries were not written (not a configuration
    error): the file phase's files, the package phase's packages. The project phase reads the
    package.ai.md files that were written, as a run whose packages are all written does."""
    return phase in NOT_WRITTEN and NOT_WRITTEN[phase] in log_path.read_text(encoding="utf-8", errors="replace")


def clear_out(out_dir, suffix=".ai.md"):
    """Deletes a model's earlier output, refusing a directory that holds anything but the files srcmorph
    writes there (summaries; for --parts the .parts.md files): a mistyped --out never deletes a source
    tree, and a repeat run never mixes its parts with an earlier run's."""
    if not out_dir.exists():
        return
    foreign = sorted(p for p in out_dir.rglob("*") if p.is_file() and not p.name.endswith(suffix))
    if foreign:
        sys.exit(f"{out_dir} holds files srcmorph did not write ({foreign[0]}); refusing to delete it")
    shutil.rmtree(out_dir)


def cli_environment(environ, models, definition):
    """The CLI's environment: every API key variable models.json names (common, backends, a model's
    settings) is dropped but the one the model's own definition names."""
    keys = {"OPENROUTER_API_KEY", "HF_TOKEN", models.get("common", {}).get("apiKeyEnv")}
    keys |= {backend.get("apiKeyEnv") for backend in models["backends"].values()}
    keys |= {m.get("settings", {}).get("apiKeyEnv") for m in models["models"]}
    env = {k: v for k, v in environ.items() if k not in keys}
    if definition.get("apiKeyEnv"):
        env[definition["apiKeyEnv"]] = environ.get(definition["apiKeyEnv"], "")
    return env


def run_model(args, models, profile, model):
    key = model["key"]
    definition = ai_definition(models, model, args.model_dir, args.seed, args.chars_per_token)
    rules = file_rules(profile, key, args.facts, args.oversize or None, args.max_chunks, args.java_prompt)
    out_dir = Path(args.out) / key
    clear_out(out_dir)
    if args.parts:
        clear_out(Path(args.parts) / key, ".parts.md")
    log_dir = Path(args.work).parent / "logs" / key
    log_dir.mkdir(parents=True, exist_ok=True)
    classpath = Path(args.cp).read_text(encoding="utf-8").strip()
    if args.prepend:
        classpath = args.prepend + os.pathsep + classpath
    env = cli_environment(os.environ, models, definition)

    meta = {"key": key, "name": model["name"], "backend": model["backend"],
            # The GGUF by file name only: where the models sit on the benchmark machine is not data.
            "definition": {k: (Path(v).name if k == "modelPath" else v)
                           for k, v in definition.items() if k != "apiKeyEnv"},
            "javaCommit": None if args.corpus else args.commit,
            "corpus": Path(args.corpus).name if args.corpus else "srcmorph",
            "profile": args.profile, "javaPrompt": args.java_prompt, "facts": args.facts,
            "oversize": args.oversize or None, "onIncomplete": ON_INCOMPLETE,
            "maxChunks": args.max_chunks, "profileHashes": profile_hashes(profile, rules),
            "cli": cli_jar(classpath), "java": java_version(args.java), "python": platform.python_version(),
            "srcmorphCommit": srcmorph_commit(), "corpusHash": tree_hash(Path(args.work)), "startedAt": utc_now(),
            "phases": {}}
    run_record = log_dir / "run-record.json"
    if run_record.exists():
        run_record.unlink()
    usage_before = None
    if model["backend"] == "openrouter":
        try:
            usage_before = openrouter_usage()
        except COST_ERRORS as error:
            cost_failed(meta, "costUsd", "the usage before the run: " + error_text(error))
    for phase in PHASES:
        config_path = log_dir / f"config_{phase}.json"
        config_path.write_text(json.dumps(phase_config(phase, Path(args.work), out_dir, profile, definition,
                                                       args.facts, args.oversize or None, args.max_chunks,
                                                       run_record, args.java_prompt,
                                                       Path(args.parts) / model["key"] if args.parts else None),
                                          indent=1), encoding="utf-8")
        start = time.time()
        with open(log_dir / f"{phase}.log", "w", encoding="utf-8") as log:
            rc = subprocess.run([args.java, "-cp", classpath, "net.ladenthin.srcmorph.cli.Main", str(config_path)],
                                stdout=log, stderr=subprocess.STDOUT, env=env).returncode
        meta["phases"][phase] = {"exit": rc, "seconds": round(time.time() - start, 1),
                                 "cutOff": cut_off_files(log_dir / f"{phase}.log", Path(args.work), out_dir, phase)}
        print(f"{key} {phase} exit {rc} in {meta['phases'][phase]['seconds']} s", flush=True)
        if rc != 0:
            meta["phases"][phase]["errorTail"] = scrubbed(last_lines(log_dir / f"{phase}.log"),
                                                          Path(args.work).parent)
            # a file phase that wrote all but some files still feeds the package and project phases, and a
            # package phase that wrote all but some packages the project phase, so a model's score covers
            # the same summaries as any other run's; any other failure ends the model
            if not files_not_written(log_dir / f"{phase}.log", phase):
                break
    meta["finishedAt"] = utc_now()
    totals = None
    if run_record.exists():
        record = json.loads(run_record.read_text(encoding="utf-8"))
        totals = meta["fileTokens"] = record["totals"]
        meta["finishReasons"] = dict(sorted(
            (reason, sum(1 for f in record["files"] if f["finishReason"] == reason))
            for reason in {f["finishReason"] for f in record["files"] if f["calls"]}))
        # the record knows every file whose last call stopped at the budget, whatever the log said
        file_phase = meta["phases"].get("GenerateFileIndex")
        if file_phase is not None:
            file_phase["cutOff"] = sorted(set(file_phase["cutOff"]) | {
                f["file"] for f in record["files"] if f["finishReason"] == "length" and f["outcome"] != "written"})
    meta["files"] = sum(1 for _ in out_dir.rglob("*.ai.md")) if out_dir.exists() else 0
    # the meta of a paid run is written before its cost is looked up, and again with it: a lookup that
    # fails loses neither the meta nor the models after this one
    write_meta(args.meta, meta)
    if model["backend"] == "openrouter":
        add_costs(meta, totals, definition["remoteModel"], usage_before)
        write_meta(args.meta, meta)
    return meta


# What a cost lookup over the network raises: no connection or a timeout (URLError and socket.timeout are
# OSErrors), an answer cut short (http.client.IncompleteRead), one that is no JSON (ValueError) or lacks a
# field (KeyError, TypeError).
COST_ERRORS = (OSError, http.client.HTTPException, ValueError, KeyError, TypeError)


def error_text(error):
    return f"{type(error).__name__}: {error}"


def cost_failed(meta, field, note):
    """Records a cost that could not be looked up: null, with the reason under costErrors."""
    meta[field] = None
    meta.setdefault("costErrors", {})[field] = note


def add_costs(meta, totals, remote_model, usage_before):
    """The OpenRouter costs of a finished run, into its meta: tokenCostUsd from the file phase's token
    totals at the model's list prices, costUsd from the account's usage since usage_before. A lookup that
    fails records null and its error (cost_failed) instead of ending the run."""
    if totals is not None:
        try:
            price = openrouter_price(remote_model)
            if price is not None:
                meta["tokenCostUsd"] = token_cost(totals, price)
        except COST_ERRORS as error:
            cost_failed(meta, "tokenCostUsd", "the model's prices: " + error_text(error))
    if usage_before is not None:
        try:
            meta["costUsd"] = round(openrouter_usage() - usage_before, 6)
        except COST_ERRORS as error:
            cost_failed(meta, "costUsd", "the usage after the run: " + error_text(error))


def write_meta(meta_dir, meta):
    """Writes <meta>/<model key>.json."""
    Path(meta_dir).mkdir(parents=True, exist_ok=True)
    (Path(meta_dir) / f"{meta['key']}.json").write_text(json.dumps(meta, indent=1) + "\n", encoding="utf-8")


def validate(parser, args, models):
    """The option combinations srcmorph would reject late or ignore silently."""
    if args.max_chunks is not None and args.oversize != "sampleChunks":
        parser.error("--max-chunks applies only to --oversize sampleChunks (mapReduce reads every chunk)")
    if args.oversize == "sampleChunks" and (args.max_chunks is None or args.max_chunks < 1):
        parser.error("--oversize sampleChunks needs --max-chunks of at least 1")
    if args.chars_per_token is not None and not (math.isfinite(args.chars_per_token) and args.chars_per_token > 0):
        parser.error("--chars-per-token must be above 0 (0 would switch srcmorph to the static maxInputChars)")
    known = {m["key"] for m in models["models"]}
    unknown = [k for k in args.models.split(",") if k and k not in known]
    if unknown:
        parser.error(f"unknown model key(s): {', '.join(unknown)}")
    if args.backend and args.backend not in models["backends"]:
        parser.error(f"unknown backend: {args.backend}")


# The options whose path reaches a phase configuration: srcmorph resolves a relative path against the
# configuration file's directory (the logs below --work), not the working one (--model-dir as the
# directory of a local model's modelPath).
PATH_OPTIONS = ("work", "out", "meta", "parts", "model_dir")
# The card's outputs and run records: report.py renders the card from them, score.py scores them.
CARD_OUT = HERE.parent / "outputs-2026-10"
CARD_META = HERE / "runs"


def meta_directory(out, meta):
    """Where the runs' meta files of an --out go: --meta, or for the card's own outputs the card's runs/;
    another output tree names a directory of its own, so the card's run records are not overwritten."""
    card = Path(out).resolve() == CARD_OUT.resolve()
    if meta is None:
        if card:
            return str(CARD_META)
        raise ValueError(f"--out {out} is not the card's outputs: --meta names a directory of its own")
    if not card and Path(meta).resolve() == CARD_META.resolve():
        raise ValueError(f"--meta {meta} is the card's: the meta of --out {out} goes to a directory of its own")
    return meta


def absolute_paths(args):
    """The parsed options with every PATH_OPTIONS path that is given made absolute, against the working
    directory."""
    for name in PATH_OPTIONS:
        if getattr(args, name, None):
            setattr(args, name, str(Path(getattr(args, name)).resolve()))
    return args


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
    parser.add_argument("--out", default=str(CARD_OUT))
    parser.add_argument("--meta", help="where <model key>.json goes (default: the card's runs/, only with"
                        " the card's --out)")
    parser.add_argument("--work", required=True)
    parser.add_argument("--profile", default="v3")
    parser.add_argument("--profile-dir", default=str(PROFILES))
    parser.add_argument("--corpus", default="")
    parser.add_argument("--facts", action="store_true")
    parser.add_argument("--oversize", default="", choices=("",) + OVERSIZE)
    parser.add_argument("--max-chunks", type=int, default=None)
    parser.add_argument("--chars-per-token", type=float, default=None)
    parser.add_argument("--java-prompt", default=JAVA_PROMPT)
    parser.add_argument("--parts", default="")
    args = absolute_paths(parser.parse_args())
    try:
        args.meta = meta_directory(args.out, args.meta)
    except ValueError as e:
        parser.error(str(e))
    models = json.loads((HERE / "models.json").read_text(encoding="utf-8"))
    validate(parser, args, models)

    profile = Path(args.profile_dir).resolve() / args.profile
    for prompt_key in PROMPT_KEYS + (args.java_prompt,):
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
