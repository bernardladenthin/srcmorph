# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""The kinds the self-test does not route yet, on open-source projects: copies their files into a corpus
(other/<project>/<path>) and derives the names a faithful summary states, per kind, from the source.

Usage: python build_facts.py <src dir with the clones> <corpus dir> <facts.json>
"""
import json
import os
import re
import shutil
import subprocess
import sys
import tomllib
from pathlib import Path

import yaml

SRC, CORPUS, FACTS = Path(sys.argv[1]), Path(sys.argv[2]), Path(sys.argv[3])


def tracked(project):
    if not (SRC / project / ".git").exists():
        return sorted(str(p.relative_to(SRC / project)).replace("\\", "/") for p in (SRC / project).rglob("*")
                      if p.is_file())
    out = subprocess.run(["git", "-c", "core.longpaths=true", "-C", str(SRC / project), "ls-files"],
                         check=True, capture_output=True, text=True).stdout
    return sorted(out.split())


def simple(name):
    return re.split(r"[.$]", name.strip())[-1]


def uniq(xs):
    return [x for x in dict.fromkeys(str(x).strip() for x in xs if x is not None and str(x).strip())]


def endpoint(uri):
    """A Camel endpoint as a summary names it: scheme and path, without the options."""
    uri = uri.strip().split("?")[0]
    return uri if ":" in uri and "{{" not in uri and "${" not in uri else None


# --- XML kinds ---------------------------------------------------------------------------------------

def persistence(text):
    units = re.findall(r'<persistence-unit\b[^>]*\bname="([^"]+)"', text)
    classes = [simple(c) for c in re.findall(r"<class>\s*([^<\s]+)\s*</class>", text)]
    sources = re.findall(r"<(?:jta-|non-jta-)data-source>\s*([^<\s]+)\s*<", text)
    providers = [simple(p) for p in re.findall(r"<provider>\s*([^<\s]+)\s*</provider>", text)]
    return units + classes + sources + providers


def integration(text):
    ids = re.findall(r'<route\b[^>]*\bid="([^"]+)"', text)
    beans = re.findall(r'<bean\b[^>]*\bid="([^"]+)"', text)
    classes = [simple(c) for c in re.findall(r'<bean\b[^>]*\bclass="([^"]+)"', text)]
    uris = [endpoint(u) for u in re.findall(r'<(?:from|to|toD|wireTap|enrich)\b[^>]*\buri="([^"]+)"', text)]
    return ids + beans + classes + uris


def wsdl(text):
    names = []
    for element in ("service", "portType", "binding", "operation"):
        names += re.findall(r"<(?:\w+:)?" + element + r'\b[^>]*\bname="([^"]+)"', text)
    return names


EL_KEYWORDS = {"empty", "not", "and", "or", "eq", "ne", "lt", "gt", "le", "ge", "true", "false", "null",
               "param", "facesContext", "request", "session", "flash", "cc", "component", "resource", "view",
               "requestScope", "sessionScope", "applicationScope", "viewScope", "flowScope", "header", "headerValues",
               "cookie", "initParam", "paramValues", "externalContext"}


def facelets(text):
    pages = [os.path.basename(p) for p in re.findall(r'<ui:(?:composition|decorate)\b[^>]*\btemplate="([^"]+)"', text)]
    pages += [os.path.basename(p) for p in re.findall(r'<ui:include\b[^>]*\bsrc="([^"]+)"', text)]
    # the first name of an expression, not a function prefix (of:, fn:) or an implicit object
    beans = [b for b in re.findall(r"#\{\s*!?\s*([A-Za-z_]\w*)(?!\w|\s*:)", text) if b not in EL_KEYWORDS]
    return pages + beans


# --- contracts -----------------------------------------------------------------------------------------

HTTP = ("get", "put", "post", "delete", "patch", "head", "options", "trace")


def openapi(text, path):
    doc = json.loads(text) if path.endswith(".json") else yaml.safe_load(text)
    out = []
    for route, item in (doc.get("paths") or {}).items():
        out.append(route)
        for method, operation in (item or {}).items():
            if method in HTTP and isinstance(operation, dict) and operation.get("operationId"):
                out.append(operation["operationId"])
    out += list(((doc.get("components") or {}).get("schemas") or {}).keys())
    out += list((doc.get("definitions") or {}).keys())
    return out


def asyncapi(text, path):
    doc = json.loads(text) if path.endswith(".json") else yaml.safe_load(text)
    out = []
    for name, channel in (doc.get("channels") or {}).items():
        out.append(name)
        if isinstance(channel, dict):
            if channel.get("address"):
                out.append(channel["address"])
            for verb in ("publish", "subscribe"):
                if isinstance(channel.get(verb), dict) and channel[verb].get("operationId"):
                    out.append(channel[verb]["operationId"])
    out += list((doc.get("operations") or {}).keys())
    out += list(((doc.get("components") or {}).get("messages") or {}).keys())
    out += list(((doc.get("components") or {}).get("schemas") or {}).keys())
    return out


def avro(text):
    doc = json.loads(text)
    out = []

    def walk(node, top):
        if isinstance(node, list):
            for item in node:
                walk(item, top)
        elif isinstance(node, dict):
            if node.get("type") in ("record", "error", "enum", "fixed") and node.get("name"):
                out.append(simple(node["name"]))
            for field in node.get("fields") or []:
                if top and isinstance(field, dict) and field.get("name"):
                    out.append(field["name"])
                walk(field.get("type"), False)
            if "items" in node:
                walk(node["items"], False)
            if "values" in node:
                walk(node["values"], False)
    walk(doc, True)
    return out


def graphql(text):
    text = re.sub(r'"""[\s\S]*?"""|#[^\n]*', "", text)
    out = re.findall(r"(?m)^\s*(?:extend\s+)?(?:type|input|enum|interface|union|scalar)\s+(\w+)", text)
    for root in re.finditer(r"(?:type|extend\s+type)\s+(?:Query|Mutation|Subscription)\b[^{]*\{([^}]*)\}", text):
        out += re.findall(r"(?m)^\s*(\w+)\s*[(:]", root.group(1))
    return out


# --- ops -------------------------------------------------------------------------------------------------

def camel_yaml(text):
    out = []

    def uris(node):
        if isinstance(node, dict):
            for key, value in node.items():
                if key in ("uri",) and isinstance(value, str):
                    out.append(endpoint(value))
                elif key in ("to", "toD", "wireTap") and isinstance(value, str):
                    out.append(endpoint(value))
                else:
                    uris(value)
        elif isinstance(node, list):
            for item in node:
                uris(item)
    for doc in yaml.safe_load_all(text):
        for entry in doc or []:
            if not isinstance(entry, dict):
                continue
            for kind in ("route", "routeTemplate", "templatedRoute", "rest"):
                if isinstance(entry.get(kind), dict) and entry[kind].get("id"):
                    out.append(entry[kind]["id"])
            uris(entry)
    return out


def image(ref):
    ref = ref.split("@")[0]
    return ref.split("/")[-1].split(":")[0]


def compose(text):
    doc = yaml.safe_load(text) or {}
    out = []
    for name, service in (doc.get("services") or {}).items():
        out.append(name)
        if isinstance(service, dict) and isinstance(service.get("image"), str) and "$" not in service["image"]:
            out.append(image(service["image"]))
    out += list((doc.get("volumes") or {}).keys()) + list((doc.get("networks") or {}).keys())
    return out


ANSIBLE_META = {"name", "become", "become_user", "when", "with_items", "loop", "register", "notify", "tags",
                "vars", "ignore_errors", "changed_when", "failed_when", "delegate_to", "run_once", "args",
                "environment", "no_log", "until", "retries", "delay", "loop_control", "with_dict", "listen",
                "block", "rescue", "always", "check_mode", "async", "poll", "connection", "local_action"}


def ansible(text):
    out = []

    def tasks(items):
        for task in items or []:
            if not isinstance(task, dict):
                continue
            for key in task:
                if key in ("block", "rescue", "always"):
                    tasks(task[key])
                elif key in ("include_tasks", "import_tasks", "include", "import_playbook") and isinstance(task[key], str):
                    out.append(os.path.basename(task[key]))
                elif key not in ANSIBLE_META:
                    out.append(key)
                    break
    for doc in yaml.safe_load_all(text):
        if not isinstance(doc, list):
            continue
        for play in doc:
            if not isinstance(play, dict):
                continue
            if "hosts" in play:
                if isinstance(play["hosts"], str):
                    out.append(play["hosts"])
                for role in play.get("roles") or []:
                    out.append(role if isinstance(role, str) else (role or {}).get("role", ""))
                out += list((play.get("vars") or {}).keys())
                for section in ("pre_tasks", "tasks", "post_tasks", "handlers"):
                    tasks(play.get(section))
            else:
                tasks([play])
    return out


def gradle(text):
    plugins = re.findall(r"""\bid\s*\(?\s*["']([\w.\-]+)["']""", text)
    plugins += re.findall(r"""apply\s+plugin\s*:\s*["']([\w.\-]+)["']""", text)
    plugins += re.findall(r"""^\s*(?:`)?([a-z][\w\-]*)(?:`)?\s*$""", "\n".join(
        m.group(1) for m in re.finditer(r"plugins\s*\{([^}]*)\}", text)), flags=re.M)
    deps = re.findall(r"""["'][\w.\-]+:([\w.\-]+)(?::[^"']*)?["']""", text)
    tasks = re.findall(r"""tasks\.(?:register|named|create)\s*(?:<[^>]*>)?\s*\(\s*["'](\w+)["']""", text)
    tasks += re.findall(r"(?m)^\s*task\s+(\w+)", text)
    return plugins + deps + tasks


def properties(text):
    keys = []
    continued = False
    for line in text.splitlines():
        stripped = line.strip()
        if continued:
            continued = line.endswith("\\")
            continue
        if not stripped or stripped[0] in "#!":
            continue
        m = re.match(r"\s*((?:\\.|[^\s:=\\])+)", line)
        if m:
            keys.append(m.group(1).replace("\\", ""))
        continued = line.endswith("\\")
    return keys[:40]


def powershell(text):
    text = re.sub(r"<#[\s\S]*?#>", "", text)
    functions = re.findall(r"(?im)^\s*function\s+([\w\-]+)", text)
    params = []
    for block in re.finditer(r"(?is)\bparam\s*\((.*?)\)\s*(?:$|\n)", text):
        params += re.findall(r"\$(\w+)\s*(?:=|,|\)|$)", block.group(1), flags=re.M)
    return [name for name in functions + params if name.lower() not in ("true", "false", "null")]


def batch(text):
    labels = [label for label in re.findall(r"(?m)^\s*:(\w+)", text) if label.lower() != "eof"]
    variables = re.findall(r'(?im)^\s*@?set\s+(?:/[ap]\s+)?"?(\w+)=', text)
    calls = [os.path.basename(c) for c in re.findall(r'(?im)\bcall\s+"?(?:%~dp0)?([\w.\\/\-]+\.(?:bat|cmd))', text)]
    return labels + variables + calls


# --- class-based languages -------------------------------------------------------------------------------

def strip_comments(text):
    return re.sub(r"/\*[\s\S]*?\*/|//[^\n]*", "", text)


def kotlin(text):
    text = strip_comments(text)
    types = re.findall(r"\b(?:class|interface|object|enum\s+class)\s+(\w+)", text)
    functions = re.findall(r"\bfun\s+(?:<[^>]*>\s*)?(?:[\w.]+\.)?(\w+)\s*\(", text)
    return types + functions


def groovy(text):
    text = strip_comments(text)
    types = re.findall(r"\b(?:class|interface|trait|enum)\s+(\w+)", text)
    methods = re.findall(r"(?m)^\s*(?:(?:public|protected|private|static|final|abstract|synchronized)\s+)*"
                         r"(?:def|void|[A-Z]\w*(?:<[^>]*>)?)\s+([a-z]\w*)\s*\(", text)
    return types + methods


def scala(text):
    text = strip_comments(text)
    types = re.findall(r"\b(?:class|trait|object|enum)\s+(\w+)", text)
    defs = re.findall(r"\bdef\s+(\w+)", text)
    return types + defs


def java_ee(text):
    text = strip_comments(text)
    types = re.findall(r"\b(?:class|interface|enum|record)\s+(\w+)", text)
    methods = re.findall(r"(?m)^\s*public\s+(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?(?:<[^>]*>\s*)?"
                         r"[\w.<>\[\], ?]+\s+(\w+)\s*\(", text)
    paths = re.findall(r'@Path\(\s*"([^"]+)"', text)
    return types + methods + paths


EE_IMPORTS = (r"(?m)(?:^import[ \t]+(?:static[ \t]+)?|@)(?:javax|jakarta)\.(?:ejb|persistence|ws\.rs|servlet|jws"
              r"|xml\.ws|inject|transaction|enterprise|annotation\.security)\.")


def pyproject(text):
    doc = tomllib.loads(text)
    project = doc.get("project") or {}
    out = [project.get("name"), (doc.get("build-system") or {}).get("build-backend")]
    for dep in (project.get("dependencies") or []):
        out.append(re.split(r"[\s<>=!~;\[(]", dep, maxsplit=1)[0])
    out += list((project.get("scripts") or {}).keys())
    out += list((project.get("optional-dependencies") or {}).keys())
    out += list((doc.get("tool") or {}).keys())
    poetry = (doc.get("tool") or {}).get("poetry") or {}
    out += [d for d in (poetry.get("dependencies") or {}) if d != "python"]
    return out


SECRET = re.compile(r"(?i)(secret|credential|vault|\.env)")


def kind_of(path, text):
    name = os.path.basename(path)
    lower = name.lower()
    if SECRET.search(name):
        return None
    if name in ("persistence.xml", "orm.xml") or lower.endswith(".hbm.xml"):
        return persistence
    if lower.endswith(".xml") and re.search(r"camel\.apache\.org/schema|springframework\.org/schema/integration",
                                             text):
        return integration
    if lower.endswith(".wsdl"):
        return wsdl
    if lower.endswith(".xhtml"):
        return facelets
    if lower.endswith((".yml", ".yaml")):
        if re.search(r"""(?m)^["']?(?:openapi|swagger)["']?[ \t]*:""", text):
            return openapi
        if re.search(r"""(?m)^["']?asyncapi["']?[ \t]*:""", text):
            return asyncapi
        if re.match(r"(?:docker-)?compose[^/]*\.ya?ml$", lower):
            return compose
        if re.search(r"(?m)^-[ \t]+(?:route|routeTemplate|templatedRoute|from|rest)[ \t]*:", text):
            return camel_yaml
        if (re.search(r"(^|/)roles/[^/]+/(tasks|handlers|defaults|vars|meta)/[^/]+\.ya?ml$", path)
                or re.search(r"(?m)^-[ \t]+(?:name[ \t]*:[^\n]*\n[ \t]+)?hosts[ \t]*:", text)):
            if re.search(r"(?m)^kind:[ \t]*[\"']?[A-Z]", text) or "/.github/" in "/" + path:
                return None
            return ansible
        return None
    if re.match(r"(?:openapi|swagger).*\.json$", lower):
        return openapi
    if re.match(r"asyncapi.*\.json$", lower):
        return asyncapi
    if lower.endswith(".avsc"):
        return avro
    if lower.endswith((".graphql", ".graphqls", ".gql")):
        return graphql
    if lower.endswith((".gradle", ".gradle.kts")):
        return gradle
    if lower.endswith(".properties"):
        return properties
    if lower.endswith((".ps1", ".psm1")):
        return powershell
    if lower.endswith((".bat", ".cmd")):
        return batch
    if lower.endswith((".kt", ".kts")):
        return kotlin
    if lower.endswith((".groovy", ".gvy")):
        return groovy
    if lower.endswith(".scala"):
        return scala
    if lower.endswith(".java") and re.search(EE_IMPORTS, text):
        return java_ee
    if name == "pyproject.toml":
        return pyproject
    return None


# which files of a project, and how many of a kind at most (the first ones in path order)
SELECT = {
    "tomee": lambda p: p.startswith("examples/") and "/src/test/" not in p,
    "camel-examples": lambda p: "/src/test/" not in p,
    "asyncapi-spec": lambda p: p.startswith("examples/"),
    "learn.openapis.org": lambda p: p.startswith("examples/v3"),
    "avro": lambda p: p.startswith(("share/test/schemas/", "share/schemas/", "lang/java/tools/src/test/compiler/")),
    "jakartaee-examples": lambda p: "/src/test/" not in p,
    "cxf": lambda p: p.startswith("distribution/src/main/release/samples/"),
    "spring-graphql": lambda p: p.endswith((".graphqls", ".graphql", ".gql", ".gradle", ".gradle.kts")),
    "dgs-examples-kotlin": lambda p: "/test/" not in p,
    "awesome-compose": lambda p: True,
    "ansible-for-devops": lambda p: True,
    "spock-example": lambda p: True,
    "scala3-example-project": lambda p: True,
    "spring-petclinic": lambda p: p.endswith((".gradle", ".gradle.kts", ".properties")),
    "tomcat": lambda p: p.startswith("bin/"),
    "PowerShell": lambda p: p.startswith("tools/"),
    "pyprojects": lambda p: True,
}
LIMIT = {"java_ee": 30, "properties": 20, "integration": 20, "camel_yaml": 20, "compose": 20, "ansible": 24,
         "facelets": 20, "persistence": 20, "kotlin": 20, "avro": 20, "asyncapi": 16, "openapi": 16,
         "powershell": 16, "batch": 16, "groovy": 20, "scala": 20, "gradle": 16, "wsdl": 16, "graphql": 16,
         "pyproject": 16}
PER_PROJECT = 12  # at most this many files of one kind from one project, so no project fills a kind

facts = {"_comment": "Kinds the self-test does not route yet: names derived per kind from open-source projects "
                     "(their licenses and commits in the writeup)"}
counts = {}
if CORPUS.exists():
    shutil.rmtree(CORPUS)
for project, keep in SELECT.items():
    if not (SRC / project).exists():
        continue
    per_kind = {}
    for rel in tracked(project):
        if not keep(rel):
            continue
        src = SRC / project / rel
        try:
            text = src.read_text(encoding="utf-8")
        except (UnicodeDecodeError, FileNotFoundError, IsADirectoryError):
            continue
        kind = kind_of(rel, text)
        if kind is None:
            continue
        name = kind.__name__
        if counts.get(name, 0) >= LIMIT.get(name, 20) or per_kind.get(name, 0) >= PER_PROJECT:
            continue
        try:
            ids = kind(text, rel) if kind in (openapi, asyncapi) else kind(text)
        except (yaml.YAMLError, json.JSONDecodeError, tomllib.TOMLDecodeError, AttributeError, TypeError):
            continue
        ids = [i for i in uniq(ids) if len(i) > 1 and "{{" not in i and "${" not in i]
        if len(ids) < 2 or len(text) > 60_000:
            continue
        target_rel = f"other/{project}/{rel}"
        target = CORPUS / target_rel
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, target)
        words = [i for i in ids if i.isalpha() and i.islower()]
        facts[target_rel] = {"ids": ids, "words": words}
        counts[name] = counts.get(name, 0) + 1
        per_kind[name] = per_kind.get(name, 0) + 1
FACTS.write_text(json.dumps(facts, indent=1) + "\n", encoding="utf-8")
print(counts, sum(counts.values()), "files,", sum(len(v["ids"]) for k, v in facts.items() if k != "_comment"), "names")
