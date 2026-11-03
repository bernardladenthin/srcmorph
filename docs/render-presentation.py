# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Renders local-inference-presentation.md to local-inference-presentation.pdf.

The deck is a Marp file whose slides are raw HTML and SVG, so it needs no Markdown renderer and no
Marp CLI: this script builds one HTML page with a 1280 x 720 px <section> per slide (Marp's 16:9
size), applies the style of the front matter, drops the speaker notes (HTML comments), wraps each
SVG in a paragraph as Marp's Markdown does, and lets headless Chrome or Edge print it to PDF. The
chart images resolve relative to docs/, as in the deck. Text stays text in the PDF.

Render again whenever the deck or a deck chart (ai-index-benchmark/modelcard/plot.py) changes.

Usage: python render-presentation.py [--browser PATH] [--html OUT.html]
"""
import argparse
import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

HERE = Path(__file__).resolve().parent
DECK = HERE / "local-inference-presentation.md"
PDF = HERE / "local-inference-presentation.pdf"

BROWSERS = [
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    "google-chrome",
    "google-chrome-stable",
    "chromium",
    "chromium-browser",
    "microsoft-edge",
]


def find_browser(explicit):
    for candidate in [explicit] if explicit else BROWSERS:
        path = candidate if os.path.isfile(candidate) else shutil.which(candidate)
        if path:
            return path
    sys.exit("no Chrome, Chromium or Edge found; pass --browser PATH")


def deck_html(text):
    match = re.match(r"---\n(.*?)\n---\n(.*)", text, re.S)
    if not match:
        sys.exit(f"{DECK.name}: no front matter")
    front, body = match.groups()
    title = re.search(r"^title: (.*)$", front, re.M).group(1).strip()
    style = re.search(r"^style: \|\n((?:  .*\n?)+)", front, re.M).group(1)
    style = "\n".join(line[2:] for line in style.splitlines())

    sections = []
    for slide in body.split("\n---\n"):
        slide = re.sub(r"<!--.*?-->", "", slide, flags=re.S).strip()
        slide = re.sub(r"(<svg .*?</svg>)", r"<p>\1</p>", slide, flags=re.S)
        sections.append(f"<section>{slide}</section>")

    return f"""<!DOCTYPE html>
<html><head><meta charset="utf-8"><title>{title}</title><base href="{HERE.as_uri()}/">
<style>
@page {{ size: 1280px 720px; margin: 0; }}
html, body {{ margin: 0; padding: 0; print-color-adjust: exact; -webkit-print-color-adjust: exact; }}
section {{ width: 1280px; height: 720px; overflow: hidden; box-sizing: border-box;
          position: relative; break-after: page; }}
section:last-child {{ break-after: auto; }}
{style}
</style></head><body>
{chr(10).join(sections)}
</body></html>
""", len(sections)


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--browser", help="path or name of Chrome, Chromium or Edge")
    parser.add_argument("--html", type=Path, help="also keep the intermediate HTML here")
    args = parser.parse_args()

    html, slides = deck_html(DECK.read_text(encoding="utf-8").replace("\r\n", "\n"))
    browser = find_browser(args.browser)
    # A stale PDF must not pass the page check when the browser cannot write, e.g. while a viewer
    # holds the file open.
    try:
        PDF.unlink(missing_ok=True)
    except OSError as error:
        sys.exit(f"{PDF.name}: cannot replace ({error.strerror}); is it open in a viewer?")
    with tempfile.TemporaryDirectory() as tmp:
        page = Path(tmp) / "deck.html"
        page.write_text(html, encoding="utf-8")
        if args.html:
            args.html.write_text(html, encoding="utf-8")
        subprocess.run(
            [browser, "--headless=new", "--disable-gpu", "--allow-file-access-from-files",
             "--no-pdf-header-footer", f"--user-data-dir={Path(tmp) / 'profile'}",
             f"--print-to-pdf={PDF}", page.as_uri()],
            check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    if not PDF.is_file():
        sys.exit(f"{PDF.name}: the browser wrote no PDF")
    pages = len(re.findall(rb"/Type\s*/Page[^s]", PDF.read_bytes()))
    if pages != slides:
        sys.exit(f"{PDF.name}: {pages} pages for {slides} slides")
    print(f"{PDF.name}: {slides} slides")


if __name__ == "__main__":
    main()
