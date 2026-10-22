# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Drives .github/verify-classifier-fatjars.sh against fixture jars built here.

srcmorph's own test, not a shared file: the script is srcmorph's alone. Every case of a check the
script makes itself asserts the exit code AND the ::error:: line it promises, so a silent exit cannot
pass for a diagnosed one; the corrupt-jar case asserts unzip's own exit code and that the script does
not pass the file off as a jar without natives.
"""

import os
import shutil
import subprocess
import tempfile
import unittest
import zipfile

from buildcheck.tests.helpers import REPO

SCRIPT = os.path.join(REPO, ".github", "verify-classifier-fatjars.sh")
BASH = shutil.which("bash")
UNZIP = shutil.which("unzip")

PREFIX = "net/ladenthin/llama/"
LINUX_CPU = PREFIX + "Linux/x86_64/cpu/libjllama.so"
WINDOWS_CPU = PREFIX + "Windows/x86_64/cpu/jllama.dll"
LINUX_CUDA = PREFIX + "Linux/x86_64/cuda13/libggml-cuda.so"
NON_NATIVE = "net/ladenthin/srcmorph/cli/Main.class"
DEFAULT = "srcmorph-cli-1.3.0-SNAPSHOT-jar-with-dependencies.jar"


def classifier_jar(classifier):
    return f"srcmorph-cli-1.3.0-SNAPSHOT-jar-with-dependencies-{classifier}.jar"


@unittest.skipUnless(BASH and UNZIP, "needs bash and unzip")
class VerifyClassifierFatjarsTest(unittest.TestCase):

    def setUp(self):
        self.dir = tempfile.mkdtemp()
        self.addCleanup(shutil.rmtree, self.dir)

    def jar(self, name, entries):
        with zipfile.ZipFile(os.path.join(self.dir, name), "w") as z:
            z.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n")
            for entry in entries:
                z.writestr(entry, b"x")

    def run_script(self, *classifiers):
        return subprocess.run([BASH, SCRIPT, self.dir, *classifiers], capture_output=True, text=True)

    def assert_fails_with(self, result, message):
        self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
        self.assertIn("::error::", result.stderr)
        self.assertIn(message, result.stderr)

    def test_a_classifier_jar_with_its_backend_and_the_cpu_natives_passes(self):
        self.jar(DEFAULT, [LINUX_CPU, WINDOWS_CPU, NON_NATIVE])
        self.jar(classifier_jar("cuda13-linux-x86-64"), [LINUX_CPU, WINDOWS_CPU, LINUX_CUDA, NON_NATIVE])
        result = self.run_script("cuda13-linux-x86-64")
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("matches Linux/x86_64/cuda13/", result.stdout)
        self.assertEqual(result.stderr, "")

    def test_a_classifier_jar_holding_only_the_cpu_natives_fails(self):
        self.jar(DEFAULT, [LINUX_CPU, WINDOWS_CPU])
        self.jar(classifier_jar("cuda13-linux-x86-64"), [LINUX_CPU, WINDOWS_CPU])
        self.assert_fails_with(self.run_script("cuda13-linux-x86-64"), "does not contain what its name promises")

    def test_a_classifier_jar_that_lost_a_default_native_fails(self):
        self.jar(DEFAULT, [LINUX_CPU, WINDOWS_CPU])
        self.jar(classifier_jar("cuda13-linux-x86-64"), [LINUX_CPU, LINUX_CUDA])
        self.assert_fails_with(self.run_script("cuda13-linux-x86-64"), "lost natives of the default jar")

    def test_an_unmapped_classifier_shape_fails(self):
        self.jar(DEFAULT, [LINUX_CPU, WINDOWS_CPU])
        self.jar(classifier_jar("cuda13-macos-aarch64"), [LINUX_CPU, WINDOWS_CPU, LINUX_CUDA])
        self.assert_fails_with(self.run_script("cuda13-macos-aarch64"), "has no expected native path")

    def test_a_classifier_jar_without_any_native_fails_naming_it(self):
        self.jar(DEFAULT, [LINUX_CPU, WINDOWS_CPU])
        self.jar(classifier_jar("cuda13-linux-x86-64"), [NON_NATIVE])
        self.assert_fails_with(self.run_script("cuda13-linux-x86-64"),
                               classifier_jar("cuda13-linux-x86-64") + " carries no native library at all")

    def test_a_default_jar_without_any_native_fails_naming_it(self):
        self.jar(DEFAULT, [NON_NATIVE])
        self.jar(classifier_jar("cuda13-linux-x86-64"), [LINUX_CPU, WINDOWS_CPU, LINUX_CUDA])
        self.assert_fails_with(self.run_script("cuda13-linux-x86-64"), DEFAULT + " carries no native library at all")

    def test_a_default_jar_with_natives_of_one_os_fails(self):
        self.jar(DEFAULT, [LINUX_CPU])
        self.jar(classifier_jar("cuda13-linux-x86-64"), [LINUX_CPU, LINUX_CUDA])
        self.assert_fails_with(self.run_script("cuda13-linux-x86-64"), "carries natives for only 1 OS")

    def test_a_corrupt_jar_fails_with_unzip_s_own_error_not_as_a_jar_without_natives(self):
        """A jar unzip cannot read must end the script with unzip's own failure, not read as a jar
        without natives: were the helper's failure masked (a `|| true` on its call, or a lost
        pipefail), the script would report "carries no native library at all" with exit code 1 and
        an ::error:: line, a wrong diagnosis. So the case pins unzip's exit code (9,
        "End-of-central-directory signature not found") and the absence of the script's diagnosis,
        not merely a non-zero exit. (A `|| true` on the grep branch alone is not observable this way:
        pipefail carries unzip's status whatever that branch does; the branch only decides whether
        grep's own error, exit 2, is tolerated, and nothing a fixture can provoke makes grep fail.)"""
        self.jar(DEFAULT, [LINUX_CPU, WINDOWS_CPU])
        with open(os.path.join(self.dir, classifier_jar("cuda13-linux-x86-64")), "wb") as f:
            f.write(b"not a zip")
        result = self.run_script("cuda13-linux-x86-64")
        self.assertEqual(result.returncode, 9, result.stdout + result.stderr)
        self.assertIn("End-of-central-directory signature not found", result.stderr)
        self.assertNotIn("::error::", result.stderr)
        self.assertNotIn("carries no native library", result.stderr)
        self.assertNotIn("ok: ", result.stdout)


if __name__ == "__main__":
    unittest.main()
