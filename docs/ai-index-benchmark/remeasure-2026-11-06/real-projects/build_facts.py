# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""C4 (TODO row 36): copies the files of the new kinds out of three open-source projects into a corpus
(other/<project>/<path>) and derives the names a faithful summary states, per kind, from the source.

Usage: python build_c4.py <src dir with the clones> <corpus dir> <facts.json>
"""
import json
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

import yaml

SRC, CORPUS, FACTS = Path(sys.argv[1]), Path(sys.argv[2]), Path(sys.argv[3])


def tracked(project):
    out = subprocess.run(["git", "-c", "core.longpaths=true", "-C", str(SRC / project), "ls-files"],
                         check=True, capture_output=True, text=True).stdout
    return out.split()


def simple(name):
    return re.split(r"[.$]", name.strip())[-1]


def uniq(xs):
    return [x for x in dict.fromkeys(x.strip() for x in xs if x and x.strip())]


def mybatis(text):
    ids = re.findall(r'<(?:select|insert|update|delete|resultMap|sql)\b[^>]*?\bid="([^"]+)"', text)
    ns = re.findall(r'<mapper\b[^>]*namespace="([^"]+)"', text)
    return ids + [simple(n) for n in ns]


def jsp(text):
    includes = [os.path.basename(f) for f in re.findall(r'<%@\s*include\s+file="([^"]+)"', text)]
    includes += [os.path.basename(f) for f in re.findall(r'<jsp:include\s+page="([^"]+)"', text)]
    beans = [simple(b) for b in re.findall(r'beanclass="([^"]+)"', text)]
    events = re.findall(r'\bevent="([^"]+)"', text)
    return includes + beans + events


def web_xml(text):
    names = re.findall(r"<(?:servlet|filter)-name>\s*([^<\s]+)\s*<", text)
    classes = [simple(c) for c in re.findall(r"<(?:servlet|filter|listener)-class>\s*([^<\s]+)\s*<", text)]
    return names + classes


def spring_xml(text):
    ids = re.findall(r'<bean\b[^>]*\bid="([^"]+)"', text)
    classes = [simple(c) for c in re.findall(r'<bean\b[^>]*\bclass="([^"]+)"', text)]
    packages = re.findall(r'base-package="([^"]+)"', text)
    return ids + classes + packages


def pom(text):
    return re.findall(r"<artifactId>\s*([^<\s]+)\s*</artifactId>", text)


BPMN_ELEMENTS = (r"(?:userTask|serviceTask|task|scriptTask|sendTask|receiveTask|businessRuleTask|manualTask|"
                 r"startEvent|endEvent|intermediateCatchEvent|intermediateThrowEvent|boundaryEvent|"
                 r"exclusiveGateway|parallelGateway|inclusiveGateway|eventBasedGateway|subProcess|callActivity)")


def bpmn(text):
    process = re.findall(r'<(?:\w+:)?process\b[^>]*\bid="([^"]+)"', text)
    names = re.findall(r'<(?:\w+:)?' + BPMN_ELEMENTS + r'\b[^>]*\bname="([^"]+)"', text)
    classes = [simple(c) for c in re.findall(r'camunda:class="([^"]+)"', text)]
    topics = re.findall(r'camunda:topic="([^"]+)"', text)
    return process + names + classes + topics


def dmn(text):
    decisions = re.findall(r'<(?:\w+:)?decision\b[^>]*\bid="([^"]+)"', text)
    decisions += re.findall(r'<(?:\w+:)?decision\b[^>]*\bname="([^"]+)"', text)
    inputs = re.findall(r'<(?:\w+:)?input\b[^>]*\blabel="([^"]+)"', text)
    outputs = re.findall(r'<(?:\w+:)?output\b[^>]*\bname="([^"]+)"', text)
    return decisions + inputs + outputs


def image(ref):
    ref = ref.split("@")[0]
    last = ref.split("/")[-1]
    return last.split(":")[0]


def k8s(text):
    out = []
    for doc in yaml.safe_load_all(text):
        if not isinstance(doc, dict):
            continue
        name = (doc.get("metadata") or {}).get("name")
        if name:
            out.append(str(name))
        spec = doc.get("spec") or {}
        pod = ((spec.get("template") or {}).get("spec")) or {}
        for c in (pod.get("containers") or []) + (pod.get("initContainers") or []):
            if isinstance(c, dict):
                out.append(str(c.get("name", "")))
                if c.get("image"):
                    out.append(image(str(c["image"])))
    return out


def kustomization(text):
    doc = yaml.safe_load(text) or {}
    out = []
    for key in ("resources", "components", "bases"):
        for entry in doc.get(key) or []:
            out.append(os.path.basename(str(entry).rstrip("/")))
    return out


def workflow(text):
    doc = yaml.safe_load(text) or {}
    out = [str(doc.get("name", ""))]
    for job_id, job in (doc.get("jobs") or {}).items():
        out.append(str(job_id))
        for step in (job or {}).get("steps") or []:
            if isinstance(step, dict) and step.get("uses"):
                out.append(str(step["uses"]).split("@")[0])
    return out


def dockerfile(text):
    out = []
    for m in re.finditer(r"(?im)^FROM\s+(?:--\S+\s+)*(\S+)(?:\s+AS\s+(\S+))?", text):
        if not m.group(1).startswith("$"):
            out.append(image(m.group(1)))
        if m.group(2):
            out.append(m.group(2))
    return out


def proto(text):
    return re.findall(r"(?m)^\s*(?:message|service|enum|rpc)\s+(\w+)", text)


def terraform(text):
    out = re.findall(r'(?m)^\s*(?:resource|data)\s+"[^"]+"\s+"([^"]+)"', text)
    out += re.findall(r'(?m)^\s*(?:variable|module|output)\s+"([^"]+)"', text)
    return out


def kind_of(path, text):
    name = os.path.basename(path)
    if name == "pom.xml":
        return pom
    if name == "web.xml":
        return web_xml
    if path.endswith(".xml") and "mybatis.org//DTD Mapper" in text:
        return mybatis
    if path.endswith(".xml") and "springframework.org/schema/beans" in text:
        return spring_xml
    if path.endswith(".jsp"):
        return jsp
    if path.endswith(".bpmn"):
        return bpmn
    if path.endswith(".dmn"):
        return dmn
    if name == "Dockerfile":
        return dockerfile
    if path.endswith(".proto"):
        return proto
    if path.endswith(".tf"):
        return terraform
    if re.search(r"(^|/)\.github/workflows/[^/]+\.ya?ml$", path):
        return workflow
    if name in ("kustomization.yaml", "kustomization.yml"):
        return kustomization
    if path.endswith((".yaml", ".yml")) and re.search(r"(?m)^kind:\s*[A-Z]", text):
        return k8s
    return None


SELECT = {
    "jpetstore-6": lambda p: p.startswith("src/main/") or p == "pom.xml",
    "microservices-demo": lambda p: not p.startswith(("helm-chart/", "docs/")) and not p.endswith(".debug"),
    "camunda-bpm-examples": lambda p: True,
}
POM_LIMIT = {"camunda-bpm-examples": 8}

facts = {"_comment": "C4: names derived per kind from jpetstore-6, microservices-demo and camunda-bpm-examples "
                     "(Apache-2.0) at the commits in RESULTS.md"}
counts = {}
if CORPUS.exists():
    shutil.rmtree(CORPUS)
for project, keep in SELECT.items():
    poms = 0
    for rel in tracked(project):
        if not keep(rel):
            continue
        src = SRC / project / rel
        try:
            text = src.read_text(encoding="utf-8")
        except (UnicodeDecodeError, FileNotFoundError):
            continue
        kind = kind_of(rel, text)
        if kind is None:
            continue
        if kind is pom:
            poms += 1
            if poms > POM_LIMIT.get(project, 99):
                continue
        try:
            ids = uniq(kind(text))
        except yaml.YAMLError:
            continue
        ids = [i for i in ids if len(i) > 1 and "{{" not in i and "${" not in i]
        if not ids:
            continue
        target_rel = f"other/{project}/{rel}"
        target = CORPUS / target_rel
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, target)
        words = [i for i in ids if i.isalpha() and i.islower()]
        facts[target_rel] = {"ids": ids, "words": words}
        counts[kind.__name__] = counts.get(kind.__name__, 0) + 1
FACTS.write_text(json.dumps(facts, indent=1) + "\n", encoding="utf-8")
print(counts, sum(counts.values()), "files,", sum(len(v["ids"]) for k, v in facts.items() if k != "_comment"), "names")
