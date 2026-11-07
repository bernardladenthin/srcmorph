// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.PromptTemplateLoader;
import net.ladenthin.srcmorph.routing.FileContext;
import net.ladenthin.srcmorph.routing.RuleSelector;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Round-trips every shipped {@code examples/config_*.{json,yaml}} fixture (the public, documented
 * configuration examples referenced from the root and per-module {@code README.md} files) through
 * the CLI's actual Jackson mappers ({@link Main#fromJson(String)} / {@link Main#fromYaml(String)})
 * and asserts a minimally sane {@link CConfiguration}/{@link SrcMorphConfiguration} object graph.
 *
 * <p>Unlike {@code configuration.ConfigBindingTest} (which pins the exact object graph of one
 * ad-hoc private fixture pair), this test is a <em>coverage sweep</em> over the public examples
 * directory: every {@code config_*.json} and {@code config_*.yaml} file under the repository-root
 * {@code examples/} directory must parse without error under the CLI's strict
 * ({@code FAIL_ON_UNKNOWN_PROPERTIES}) mappers, so an example can never silently drift out of sync
 * with {@link SrcMorphConfiguration}'s actual field set. Maven runs tests with the working directory
 * set to the module's own base directory ({@code srcmorph-cli/}), so the examples directory is
 * addressed relative to that as {@code ../examples}.</p>
 */
public class ExamplesConfigBindingTest {

    /**
     * The repository-root {@code examples/} directory, addressed relative to this module's own base
     * directory (Maven Surefire's working directory during a test run).
     */
    private static final Path EXAMPLES_DIRECTORY = Path.of("../examples");

    // <editor-fold defaultstate="collapsed" desc="fromJsonOrFromYaml">
    /**
     * Builds one {@link DynamicTest} per {@code config_*.json}/{@code config_*.yaml} file found under
     * {@link #EXAMPLES_DIRECTORY}, each asserting that the file parses cleanly and yields a sane
     * configuration object graph.
     *
     * @return one dynamic test per example configuration file
     * @throws IOException if {@link #EXAMPLES_DIRECTORY} cannot be listed
     */
    @TestFactory
    public List<DynamicTest> fromJsonOrFromYaml_everyExampleConfig_bindsSuccessfully() throws IOException {
        // arrange
        final List<Path> exampleConfigs = listExampleConfigFiles();

        // pre-assert
        assertThat(
                "expected at least one example config fixture under " + EXAMPLES_DIRECTORY,
                exampleConfigs.size(),
                greaterThan(0));

        // act, assert
        final List<DynamicTest> tests = new ArrayList<>(exampleConfigs.size());
        for (final Path exampleConfig : exampleConfigs) {
            tests.add(DynamicTest.dynamicTest(
                    exampleConfig.getFileName().toString(), () -> assertBindsSuccessfully(exampleConfig)));
        }
        return tests;
    }
    // </editor-fold>

    private static List<Path> listExampleConfigFiles() throws IOException {
        final List<Path> found = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(EXAMPLES_DIRECTORY, "config_*.{json,yaml}")) {
            for (final Path candidate : stream) {
                found.add(candidate);
            }
        }
        return found;
    }

    // <editor-fold defaultstate="collapsed" desc="fromYaml">
    /**
     * {@code examples/build-your-own-jar/} is how a user builds the runnable jar (docs/windows-intel-core-ultra.md):
     * its {@code srcmorph.yaml} is meant for a real model, so it is not one of the mock fixtures above,
     * but it must still bind under the strict mapper, start with the safe {@code Plan} and find its
     * prompt files; that it matches the plugin's self-test profile is BuildYourOwnJarSelfTestParityTest's
     * job. Its {@code pom.xml} names the CLI's
     * main class by string, so a renamed {@link Main} would otherwise only fail on the user's machine.
     *
     * @throws IOException if an example file cannot be read
     */
    @Test
    public void fromYaml_buildYourOwnJarExample_bindsAndNamesTheMainClass() throws IOException {
        // arrange
        final Path directory = EXAMPLES_DIRECTORY.resolve("build-your-own-jar");

        // act
        final CConfiguration configuration = Main.fromYaml(Main.readString(directory.resolve("srcmorph.yaml")));

        // assert
        assertThat(configuration.command, is(CCommand.Plan));
        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(srcMorph.getGenerationProvider(), is(equalTo("llamacpp-jni")));
        // the rules, facts and models themselves: BuildYourOwnJarSelfTestParityTest
        assertPromptFilesResolve(directory, configuration, "build-your-own-jar/srcmorph.yaml");

        assertThat(
                Main.readString(directory.resolve("pom.xml")),
                containsString("<mainClass>" + Main.class.getName() + "</mainClass>"));
    }

    /**
     * {@code examples/copilot-api-gateway/} is the configuration docs/copilot-api-gateway.md walks
     * through: a remote model, so not one of the mock fixtures above, but it must still bind, start
     * with the safe {@code Plan}, keep the key on a loopback {@code baseUrl} and stream, without which
     * the gateway adds VS Code's tools to the request and runs them.
     *
     * @throws IOException if the example file cannot be read
     */
    @Test
    public void fromYaml_copilotApiGatewayExample_bindsAndStreamsFromLoopback() throws IOException {
        // arrange
        final Path directory = EXAMPLES_DIRECTORY.resolve("copilot-api-gateway");

        // act
        final CConfiguration configuration = Main.fromYaml(Main.readString(directory.resolve("srcmorph.yaml")));

        // assert
        assertThat(configuration.command, is(CCommand.Plan));
        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(srcMorph.getGenerationProvider(), is(equalTo("openai-compatible")));
        assertThat(srcMorph.getAiDefinitions().size(), is(1));
        final ModelDefinition model = srcMorph.getAiDefinitions().get(0);
        assertThat(model.getProvider(), is(equalTo("openai-compatible")));
        assertThat(model.getBaseUrl(), is(equalTo("http://127.0.0.1:3030/v1")));
        assertThat(model.getApiKeyEnv(), is(equalTo("COPILOT_GATEWAY_API_KEY")));
        assertThat(model.isStream(), is(true));
        assertThat(model.getRequestsPerMinute(), is(25));
        assertThat(srcMorph.getRules().size(), is(1));
        assertThat(srcMorph.getRules().get(0).isFallback(), is(true));
        assertPromptFilesResolve(directory, configuration, "copilot-api-gateway/srcmorph.yaml");
    }

    /**
     * {@code examples/copilot-api-gateway/srcmorph-luna.yaml} runs the whole profile through one gateway
     * model: every file, package and the project goes to GPT-6 Luna, every rule but the fallback counts
     * the facts of its kind, and every rule, the package and project rules too, reads an input larger
     * than the window chunk by chunk instead of sampling it or failing. A rule routed elsewhere, a
     * facts or chunk prompt file that moved, or a lost {@code stream} fails here.
     *
     * @throws IOException if the example file cannot be read
     */
    @Test
    public void fromYaml_copilotApiGatewayLunaExample_routesEveryRuleToLunaWithFacts() throws IOException {
        // arrange
        final Path directory = EXAMPLES_DIRECTORY.resolve("copilot-api-gateway");

        // act
        final CConfiguration configuration = Main.fromYaml(Main.readString(directory.resolve("srcmorph-luna.yaml")));

        // assert
        assertThat(configuration.command, is(CCommand.Plan));
        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(srcMorph.getAiDefinitions().size(), is(1));
        final ModelDefinition model = srcMorph.getAiDefinitions().get(0);
        assertThat(model.getRemoteModel(), is(equalTo("gpt-6-luna")));
        assertThat(model.getBaseUrl(), is(equalTo("http://127.0.0.1:3030/v1")));
        assertThat(model.getApiKeyEnv(), is(equalTo("COPILOT_GATEWAY_API_KEY")));
        assertThat(model.isStream(), is(true));
        assertThat(srcMorph.isForce(), is(false));
        assertThat(srcMorph.getRegenerateOn().isSource(), is(true));
        assertThat(srcMorph.getRegenerateOn().isPrompt(), is(true));
        assertThat(srcMorph.getRegenerateOn().isFacts(), is(true));
        assertThat(srcMorph.getRegenerateOn().isModel(), is(true));
        assertThat(srcMorph.getRegenerateOn().isChildren(), is(true));

        final Path profileDirectory = directory.resolve(
                Objects.requireNonNull(srcMorph.getProfileDirectory()).getPath());
        final List<RoutingRule> rules = new ArrayList<>();
        final List<RoutingRule> skipped = new ArrayList<>();
        for (final RoutingRule rule : srcMorph.getRules()) {
            (rule.isSkip() ? skipped : rules).add(rule);
        }
        assertThat("the credentials rule", skipped.size(), is(1));
        assertThat(skipped.get(0).getId(), is(equalTo("secrets")));
        rules.addAll(Objects.requireNonNull(configuration.packageRules));
        rules.addAll(Objects.requireNonNull(configuration.projectRules));
        for (final RoutingRule rule : rules) {
            final String label = "rule " + rule.getPromptKey();
            assertThat(label, rule.getAiDefinitionKey(), is(equalTo(model.getKey())));
            assertThat(label + " reads every chunk", rule.getOnOversize(), is(equalTo("mapReduce")));
            assertThat(label + " asks once more", rule.getOnIncomplete(), is(equalTo("retryOnce")));
            assertThat(label, Files.isRegularFile(profileDirectory.resolve(rule.getMapPromptKey() + ".md")), is(true));
            assertThat(
                    label, Files.isRegularFile(profileDirectory.resolve(rule.getReducePromptKey() + ".md")), is(true));
        }
        final List<String> withoutFacts = new ArrayList<>();
        for (final RoutingRule rule : srcMorph.getRules()) {
            final String label = "rule " + rule.getId();
            if (rule.isSkip()) {
                continue;
            }
            if (rule.getFactsKey() == null) {
                withoutFacts.add(rule.getId());
            } else {
                assertThat(
                        label + " counts the facts of its kind",
                        Files.isRegularFile(profileDirectory.resolve(rule.getFactsKey() + ".tsv")),
                        is(true));
            }
        }
        // the kinds profile v3 has no counters for
        assertThat(withoutFacts, is(List.of("typescript", "pyproject", "ops", "fallback")));
        assertPromptFilesResolve(directory, configuration, "copilot-api-gateway/srcmorph-luna.yaml");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="select">
    @Test
    public void select_credentialsRuleOfEveryProfileConfig_skipsFilesWithSecretsAndNoSource() throws Exception {
        // arrange
        final String[][] secrets = {
            {".env", "TOKEN=x\n"},
            {".env.production", "TOKEN=x\n"},
            {"prod.tfvars", "password = \"x\"\n"},
            {"prod.pkrvars.hcl", "password = \"x\"\n"},
            {"terraform.tfstate", "{}\n"},
            {"settings.xml", "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\">\n"},
            {".m2/settings.xml", "<?xml version=\"1.0\"?>\n<settings>\n"},
            {"settings-security.xml", "<settingsSecurity/>\n"},
            {"server.pem", "-----BEGIN PRIVATE KEY-----\n"},
            {"tls.key", "x\n"},
            {"id_rsa", "x\n"},
            {"vault.yml", "vault_db_password: x\n"},
            {"group_vars/all/vault.yaml", "$ANSIBLE_VAULT;1.1;AES256\n"},
            {"db-credentials.yaml", "password: x\n"},
            {"app-secrets.properties", "password=x\n"},
            {"Secrets.JSON", "{}\n"},
            {"vault/secrets.hcl", "token = \"x\"\n"},
            {"k8s/secret.yaml", "apiVersion: v1\nkind: Secret\ndata:\n  password: eA==\n"},
            {"charts/shop/templates/db-secret.yaml", "apiVersion: v1\nkind: Secret\ndata:\n  password: eA==\n"},
            // a properties file that gives a password, a secret, a token or an API key a value, by its content
            {"gradle.properties", "org.gradle.jvmargs=-Xmx2g\nsigning.password=hunter2\n"},
            {"src/main/resources/application.properties", "spring.datasource.password = s3cret\n"},
            {"ci.properties", "ossrhPassword=x\ngithub.token: ghp_x\n"},
            {"client.properties", "  client-secret=x\n"},
            {"api.properties", "maps.api-key=x\n"},
        };
        final String[][] sources = {
            {"SecretManager.java", "class SecretManager {}\n"},
            {"CredentialsProvider.java", "class CredentialsProvider {}\n"},
            {"PasswordEncoder.java", "class PasswordEncoder {}\n"},
            {"pom.xml", "<project/>\n"},
            {"web.xml", "<web-app/>\n"},
            {"secrets.sh", "#!/bin/sh\n"},
            {"keys.sql", "SELECT 1;\n"},
            {"Settings.java", "class Settings {}\n"},
            // a CI workflow or action that scans for secrets or logs in to a vault holds none
            {".github/workflows/secret-scan.yml", "on: push\n"},
            {".github/workflows/vault.yml", "on: push\n"},
            // a Helm chart's template names the values it renders
            {"charts/shop/templates/secret.yaml", "kind: Secret\ndata:\n  password: {{ .Values.password | b64enc }}\n"},
            // a Kubernetes manifest of another kind
            {"k8s/vault-deployment.yaml", "apiVersion: apps/v1\nkind: Deployment\n"},
            // an Android preference screen
            {"app/src/main/res/xml/settings.xml", "<PreferenceScreen xmlns:android=\"x\"/>\n"},
            // a properties file whose secret is a reference, a comment, empty, or a key that only starts so
            {"application.properties", "spring.datasource.password=${DB_PASSWORD}\n"},
            {"gradle.properties", "# signing.password=x\norg.gradle.jvmargs=-Xmx2g\n"},
            {"empty.properties", "password=\n"},
            {"messages.properties", "password.label=Password\ntoken.expiry=3600\n"},
            {"log4j2.properties", "appender.out.type = Console\n"},
        };

        // act, assert
        for (final Path config : List.of(
                EXAMPLES_DIRECTORY.resolve("copilot-api-gateway/srcmorph-luna.yaml"),
                EXAMPLES_DIRECTORY.resolve("build-your-own-jar/srcmorph.yaml"))) {
            final List<RoutingRule> rules =
                    Main.fromYaml(Main.readString(config)).srcMorph.getRules();
            final RuleSelector selector = new RuleSelector();
            selector.validate(rules);
            for (final String[] secret : secrets) {
                final RoutingRule rule = selector.select(rules, context(secret[0], secret[1]));
                assertThat(config + ": " + secret[0], rule != null && rule.isSkip(), is(true));
            }
            for (final String[] source : sources) {
                final RoutingRule rule = selector.select(rules, context(source[0], source[1]));
                assertThat(config + ": " + source[0], rule != null && !rule.isSkip(), is(true));
            }
        }
    }

    @Test
    public void select_lunaExample_routesEveryKindToItsRule() throws Exception {
        // arrange
        final List<RoutingRule> rules = Main.fromYaml(
                        Main.readString(EXAMPLES_DIRECTORY.resolve("copilot-api-gateway/srcmorph-luna.yaml")))
                .srcMorph
                .getRules();
        final RuleSelector selector = new RuleSelector();
        selector.validate(rules);
        final String[][] cases = {
            {"src/main/java/shop/Order.java", "package shop;\nclass Order {}\n", "java"},
            {"src/main/java/shop/OrderBean.java", "package shop;\nimport jakarta.ejb.Stateless;\n", "java-ee"},
            {"src/main/java/shop/Legacy.java", "import javax.persistence.Entity;\n", "java-ee"},
            {"src/main/java/shop/Audit.java", "import static jakarta.transaction.Transactional.TxType.*;\n", "java-ee"},
            {"src/main/java/shop/Timer.java", "package shop;\n@jakarta.ejb.Singleton\nclass Timer {}\n", "java-ee"},
            {"src/main/java/shop/Notes.java", "// see jakarta.ejb.Stateless\nclass Notes {}\n", "java"},
            {"src/main/kotlin/Invoice.kt", "class Invoice\n", "kotlin"},
            {"build.gradle.kts", "plugins {}\n", "gradle"},
            {"src/main/kotlin/Build.kts", "println(1)\n", "kotlin"},
            {"src/test/groovy/OrderSpec.groovy", "class OrderSpec {}\n", "groovy"},
            {"src/main/scala/Order.scala", "case class Order()\n", "scala"},
            {"src/extension.ts", "export function activate() {}\n", "typescript"},
            {"scripts/release.mts", "export {};\n", "typescript"},
            {"esbuild.js", "require('esbuild');\n", "fallback"},
            {"src/main/webapp/WEB-INF/web.xml", "<web-app/>\n", "jee"},
            {"META-INF/ejb-jar.xml", "<ejb-jar/>\n", "jee"},
            {"META-INF/persistence.xml", "<persistence/>\n", "persistence"},
            {"mapping/Shipment.hbm.xml", "<hibernate-mapping/>\n", "persistence"},
            {"mapper/CustomerMapper.xml", "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\">\n", "mybatis"
            },
            {
                "camel/routes.xml",
                "<beans><camelContext xmlns=\"http://camel.apache.org/schema/spring\"/></beans>\n",
                "integration"
            },
            {"process/order.bpmn", "<definitions/>\n", "bpmn"},
            {"rules/discount.dmn", "<definitions/>\n", "dmn"},
            {"wsdl/Stock.wsdl", "<definitions/>\n", "wsdl"},
            {"pom.xml", "<project/>\n", "pom"},
            {"conf/context.xml", "<beans/>\n", "xml"},
            {"webapp/list.jsp", "<%@ page %>\n", "jsp"},
            {"webapp/order.jspx", "<jsp:root/>\n", "jspx"},
            {"WEB-INF/tags/total.tagx", "<jsp:root/>\n", "jspx"},
            {"webapp/checkout.xhtml", "<ui:composition/>\n", "facelets"},
            {"api/orders.yaml", "openapi: 3.0.3\n", "openapi"},
            {"api/openapi.json", "{\"openapi\":\"3.0.3\"}", "openapi"},
            {"proto/order.proto", "syntax = \"proto3\";\n", "proto"},
            {"avro/order.avsc", "{\"type\": \"record\"}\n", "avro"},
            {"graphql/schema.graphqls", "type Query { x: Int }\n", "graphql"},
            {"api/events.yaml", "asyncapi: 3.0.0\n", "asyncapi"},
            {"api/asyncapi.json", "{\"asyncapi\":\"3.0.0\"}", "asyncapi"},
            {"docker-compose.yml", "services:\n  db:\n    image: postgres\n", "compose"},
            {"deploy/compose.override.yaml", "services: {}\n", "compose"},
            {"ansible/site.yml", "- name: Shop\n  hosts: web\n", "ansible"},
            {"ansible/roles/web/tasks/main.yml", "- name: Copy\n  copy: {}\n", "ansible"},
            {"infra/terragrunt.hcl", "include \"root\" {}\n", "terraform"},
            {"settings.gradle", "include 'core'\n", "gradle"},
            {"src/main/resources/application.properties", "server.port=8080\n", "properties"},
            {"tools/deploy.ps1", "param()\n", "powershell"},
            {"tools/build.cmd", "@echo off\n", "batch"},
            {"bin/deploy", "#!/usr/bin/env bash\nset -e\n", "shell"},
            {"bin/report", "#!/usr/bin/perl -w\n", "perl"},
            {"bin/tool", "#!/usr/bin/env python3\n", "fallback"},
            {".github/workflows/ci.yml", "on: push\n", "actions"},
            {"k8s/deployment.yaml", "apiVersion: apps/v1\nkind: Deployment\n", "k8s"},
            {"camel/orders.yaml", "- route:\n    id: orders\n    from:\n      uri: jms:queue:orders\n", "camel-yaml"},
            {"camel/timer.yaml", "- from:\n    uri: timer:tick\n", "camel-yaml"},
            {"build.gradle", "plugins { id 'java' }\n", "gradle"},
            {"docker/Dockerfile", "FROM alpine\n", "dockerfile"},
            {"Jenkinsfile", "pipeline {}\n", "ops"},
            {"config/application.yml", "server:\n  port: 8080\n", "ops"},
            {"infra/main.tf", "resource \"x\" \"y\" {}\n", "terraform"},
            {"scripts/tool.py", "print(1)\n", "fallback"},
            {"config/.env", "TOKEN=x\n", "secrets"},
        };

        // act, assert
        for (final String[] c : cases) {
            final String path = c[0];
            final String name = path.substring(path.lastIndexOf('/') + 1);
            final RoutingRule rule = selector.select(rules, new FileContext(name, path, 100L, 3, 0L, c[1]));
            assertThat(path, rule == null ? null : rule.getId(), is(equalTo(c[2])));
        }
    }
    // </editor-fold>

    /**
     * A file at a path below the base directory, with its text.
     *
     * @param path    the base-relative path
     * @param content the file's text
     * @return the context a rule reads
     */
    private static FileContext context(final String path, final String content) {
        return new FileContext(path.substring(path.lastIndexOf('/') + 1), path, 100L, 3, 0L, content);
    }

    private static void assertBindsSuccessfully(final Path exampleConfig) throws IOException {
        final String fileName = exampleConfig.getFileName().toString();
        final String content = Main.readString(exampleConfig);

        final CConfiguration configuration =
                fileName.endsWith(".yaml") ? Main.fromYaml(content) : Main.fromJson(content);

        assertThat(fileName + ": command must be set", configuration.command, is(notNullValue()));

        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(fileName + ": srcMorph must be set", srcMorph, is(notNullValue()));
        assertThat(fileName + ": baseDirectory must be set", srcMorph.getBaseDirectory(), is(notNullValue()));
        assertThat(
                fileName + ": generationProvider must be the safe 'mock' default",
                srcMorph.getGenerationProvider(),
                is(equalTo("mock")));

        // Every shipped example configures at least the fallback routing rule, so the run has
        // something to do (a config with an empty <rules> fails GenerateEngine/
        // CalibrateEngine at run time, so an example silently missing it would be useless).
        assertThat(fileName + ": rules must be configured", srcMorph.getRules(), is(notNullValue()));
        assertThat(fileName + ": rules must not be empty", srcMorph.getRules().size(), greaterThan(0));
        assertPromptFilesResolve(EXAMPLES_DIRECTORY, configuration, fileName);
    }

    /**
     * Every example takes its prompts from a profile of the repository's {@code profiles/} directory
     * instead of a copy of its own, by key: it lists no prompt, and every {@code promptKey} its rules
     * use is a file there. The directory is relative to the one the example is run from (its run scripts
     * and the CI smoke start there), so this loads the keys the way srcmorph does from that directory: a
     * moved prompt or a wrong {@code ../} fails here, not on a user's machine.
     *
     * @param runDirectory  the working directory the example is run from
     * @param configuration the bound configuration
     * @param label         the example, for the messages
     * @throws IOException if a prompt file cannot be read
     */
    private static void assertPromptFilesResolve(
            final Path runDirectory, final CConfiguration configuration, final String label) throws IOException {
        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(
                label + ": no promptDefinitions, the profile has them",
                srcMorph.getPromptDefinitions(),
                is(nullValue()));
        assertThat(label + ": profileDirectory must be set", srcMorph.getProfileDirectory(), is(notNullValue()));
        final List<@Nullable String> usedKeys = new ArrayList<>();
        for (final @Nullable List<RoutingRule> rules : Arrays.<@Nullable List<RoutingRule>>asList(
                srcMorph.getRules(), configuration.packageRules, configuration.projectRules)) {
            if (rules != null) {
                for (final RoutingRule rule : rules) {
                    usedKeys.add(rule.getPromptKey());
                }
            }
        }
        final Path profileDirectory = runDirectory.resolve(
                Objects.requireNonNull(srcMorph.getProfileDirectory()).getPath());
        final List<PromptDefinition> loaded = new PromptTemplateLoader(profileDirectory.toFile()).load(null, usedKeys);
        assertThat(label + ": the rules must use at least one prompt", loaded, is(not(empty())));
        for (final PromptDefinition definition : loaded) {
            assertThat(
                    label + ": " + definition.getKey() + " must load one of the measured prompts",
                    definition.getTemplate(),
                    containsString("You are "));
        }
    }
}
