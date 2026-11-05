// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;
import net.ladenthin.srcmorph.config.AiFactCounter;
import org.junit.jupiter.api.Test;

/**
 * Validates the fact counters of the profile for API contracts and operations files
 * ({@code profiles/v3/openapi-facts.tsv} for the prompt {@code file-body-contract};
 * {@code k8s-facts.tsv}, {@code actions-facts.tsv} and {@code dockerfile-facts.tsv} for
 * {@code file-body-ops}), counted the way the facts line counts them: comments, and in an OpenAPI
 * document the body of a block scalar, are blanked first, so a path or a kind quoted in a description
 * does not count.
 */
public class OpsFactPatternsTest {

    private static final List<AiFactCounter> OPENAPI = ProfileFactPatterns.counters("openapi-facts");

    private static final List<AiFactCounter> K8S = ProfileFactPatterns.counters("k8s-facts");

    private static final List<AiFactCounter> ACTIONS = ProfileFactPatterns.counters("actions-facts");

    private static final List<AiFactCounter> DOCKERFILE = ProfileFactPatterns.counters("dockerfile-facts");

    private static final List<AiFactCounter> ASYNCAPI = ProfileFactPatterns.counters("asyncapi-facts");

    private static final List<AiFactCounter> TERRAFORM = ProfileFactPatterns.counters("terraform-facts");

    private static final List<AiFactCounter> ANSIBLE = ProfileFactPatterns.counters("ansible-facts");

    private static final List<AiFactCounter> COMPOSE = ProfileFactPatterns.counters("compose-facts");

    private static final List<AiFactCounter> CAMEL_YAML = ProfileFactPatterns.counters("camel-yaml-facts");

    private static final List<AiFactCounter> GRADLE = ProfileFactPatterns.counters("gradle-facts");

    private static final List<AiFactCounter> PROPERTIES = ProfileFactPatterns.counters("properties-facts");

    private static final List<AiFactCounter> POWERSHELL = ProfileFactPatterns.counters("powershell-facts");

    private static final List<AiFactCounter> BATCH = ProfileFactPatterns.counters("batch-facts");

    private static final List<AiFactCounter> NPM = ProfileFactPatterns.counters("npm-facts");

    private static int count(final List<AiFactCounter> counters, final String label, final String source) {
        final Map<String, Integer> counts = ProfileFactPatterns.counts(counters, source);
        final Integer count = counts.get(label);
        if (count == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + counts.keySet());
        }
        return count;
    }

    // <editor-fold defaultstate="collapsed" desc="openapi-facts">
    private static final String OPENAPI_YAML = "openapi: 3.0.3\n"
            + "info:\n"
            + "  title: Orders\n"
            + "  description: |\n"
            + "    Lists orders. Example path:\n"
            + "    /fake/path:\n"
            + "      get:\n"
            + "        type: object\n"
            + "  version: 1.0.0\n"
            + "# /commented:\n"
            + "paths:\n"
            + "  /orders:\n"
            + "    get:\n"
            + "      operationId: listOrders\n"
            + "      parameters:\n"
            + "        - name: limit\n"
            + "          in: query\n"
            + "      responses:\n"
            + "        '200':\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: '#/components/schemas/Order'\n"
            + "    post:\n"
            + "      operationId: createOrder\n"
            + "      deprecated: true\n"
            + "  /orders/{id}:\n"
            + "    get:\n"
            + "      operationId: getOrder\n"
            + "      parameters:\n"
            + "        - in: path\n"
            + "          name: id\n"
            + "components:\n"
            + "  schemas:\n"
            + "    Order:\n"
            + "      type: object\n"
            + "  securitySchemes:\n"
            + "    bearer:\n"
            + "      type: http\n";

    @Test
    public void openapi_yaml_countsPathsOperationsAndSchemasAndNotADescription() {
        assertThat(count(OPENAPI, "paths", OPENAPI_YAML), is(2));
        assertThat(count(OPENAPI, "operations (approx)", OPENAPI_YAML), is(3));
        assertThat(count(OPENAPI, "operationIds", OPENAPI_YAML), is(3));
        assertThat(count(OPENAPI, "parameters", OPENAPI_YAML), is(2));
        assertThat(count(OPENAPI, "$ref references", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "object schemas", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "security schemes", OPENAPI_YAML), is(1));
        assertThat(count(OPENAPI, "deprecated operations and fields", OPENAPI_YAML), is(1));
    }

    @Test
    public void openapi_minifiedJson_countsTheSameKeys() {
        final String json = "{\"openapi\":\"3.0.3\",\"paths\":{\"/orders\":{\"get\":{\"operationId\":\"listOrders\","
                + "\"parameters\":[{\"in\":\"query\",\"name\":\"limit\"}]},\"post\":{\"operationId\":\"createOrder\"}}},"
                + "\"components\":{\"schemas\":{\"Order\":{\"type\":\"object\"}}}}";
        assertThat(count(OPENAPI, "paths", json), is(1));
        assertThat(count(OPENAPI, "operations (approx)", json), is(2));
        assertThat(count(OPENAPI, "operationIds", json), is(2));
        assertThat(count(OPENAPI, "parameters", json), is(1));
        assertThat(count(OPENAPI, "object schemas", json), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="k8s-facts">
    private static final String MANIFEST = "# kind: Commented\n"
            + "apiVersion: apps/v1\n"
            + "kind: Deployment\n"
            + "metadata:\n"
            + "  name: orders\n"
            + "spec:\n"
            + "  replicas: {{ .Values.replicas }}\n"
            + "  template:\n"
            + "    spec:\n"
            + "      containers:\n"
            + "        - name: app\n"
            + "          image: shop/orders:1.4.0\n"
            + "          ports:\n"
            + "            - containerPort: 8080\n"
            + "          livenessProbe:\n"
            + "            httpGet: {path: /health}\n"
            + "          env:\n"
            + "            - name: DB_PASSWORD\n"
            + "              valueFrom:\n"
            + "                secretKeyRef:\n"
            + "                  name: db\n"
            + "{{- /* if .Values.debug */ -}}\n"
            + "{{- if .Values.ingress.enabled }}\n"
            + "---\n"
            + "apiVersion: v1\n"
            + "kind: Service\n"
            + "metadata:\n"
            + "  name: orders\n"
            + "---\n"
            + "kind: ConfigMap\n"
            + "data:\n"
            + "  manifest.yaml: |\n"
            + "    kind: Secret\n"
            + "{{- end }}\n";

    @Test
    public void k8s_countsResourcesAtColumnZeroAndNotACommentOrAQuotedManifest() {
        assertThat(count(K8S, "resources", MANIFEST), is(3));
        assertThat(count(K8S, "workloads", MANIFEST), is(1));
        assertThat(count(K8S, "Services, Ingresses and Routes", MANIFEST), is(1));
        assertThat(count(K8S, "ConfigMaps and Secrets", MANIFEST), is(1));
    }

    @Test
    public void k8s_countsContainersAndHelmActionsAndNotAHelmComment() {
        assertThat(count(K8S, "container images", MANIFEST), is(1));
        assertThat(count(K8S, "container ports", MANIFEST), is(1));
        assertThat(count(K8S, "probes", MANIFEST), is(1));
        assertThat(count(K8S, "secret references", MANIFEST), is(1));
        assertThat(count(K8S, "Helm control actions (approx)", MANIFEST), is(1));
        assertThat(count(K8S, ".Values references", MANIFEST), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="actions-facts">
    private static final String WORKFLOW = "name: CI\n"
            + "on:\n"
            + "  push:\n"
            + "  schedule:\n"
            + "    - cron: '0 3 * * *'\n"
            + "permissions:\n"
            + "  contents: read\n"
            + "jobs:\n"
            + "  build:\n"
            + "    runs-on: ubuntu-latest\n"
            + "    strategy:\n"
            + "      matrix:\n"
            + "        java: [21, 25]\n"
            + "    steps:\n"
            + "      - uses: actions/checkout@v4\n"
            + "      - name: Build\n"
            + "        run: mvn -B verify\n"
            + "        env:\n"
            + "          TOKEN: ${{ secrets.SONAR_TOKEN }}\n"
            + "      # - uses: old/action@v1\n"
            + "  release:\n"
            + "    uses: ./.github/workflows/release.yml\n"
            + "    secrets: inherit\n";

    @Test
    public void actions_countsJobsStepsActionsAndSecretNames() {
        assertThat(count(ACTIONS, "jobs on a runner", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "reusable workflow calls", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "steps (approx)", WORKFLOW), is(2));
        assertThat(count(ACTIONS, "actions used", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "secret references", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "cron schedules", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "matrices", WORKFLOW), is(1));
        assertThat(count(ACTIONS, "permissions blocks", WORKFLOW), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dockerfile-facts">
    private static final String DOCKERFILE_SOURCE = "# syntax=docker/dockerfile:1\n"
            + "FROM maven:3.9-eclipse-temurin-21 AS build\n"
            + "ARG VERSION=1.0\n"
            + "COPY pom.xml .\n"
            + "RUN mvn -B package \\\n"
            + "    && echo FROM done\n"
            + "from eclipse-temurin:21-jre\n"
            + "ENV JAVA_OPTS=-Xmx512m\n"
            + "COPY --from=build /app/target/app.jar /app.jar\n"
            + "EXPOSE 8080\n"
            + "USER app\n"
            + "HEALTHCHECK CMD curl -f http://localhost:8080/health\n"
            + "# RUN rm -rf /\n"
            + "ENTRYPOINT [\"java\", \"-jar\", \"/app.jar\"]\n";

    @Test
    public void dockerfile_countsInstructionsAtTheStartOfALineInAnyCase() {
        assertThat(count(DOCKERFILE, "build stages", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "RUN instructions", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "COPY and ADD instructions", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "environment entries", DOCKERFILE_SOURCE), is(2));
        assertThat(count(DOCKERFILE, "exposed ports", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "users", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "entrypoints and commands", DOCKERFILE_SOURCE), is(1));
        assertThat(count(DOCKERFILE, "healthchecks", DOCKERFILE_SOURCE), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="asyncapi-facts">
    private static final String ASYNCAPI_V2 = "asyncapi: 2.6.0\n"
            + "info:\n"
            + "  title: Orders\n"
            + "  description: |\n"
            + "    Publishes orders.\n"
            + "    publish:\n"
            + "servers:\n"
            + "  prod:\n"
            + "    url: broker:9092\n"
            + "    protocol: kafka\n"
            + "channels:\n"
            + "  order/placed:\n"
            + "    publish:\n"
            + "      operationId: placeOrder\n"
            + "      bindings:\n"
            + "        kafka:\n"
            + "          clientId: shop\n"
            + "      message:\n"
            + "        correlationId:\n"
            + "          location: $message.header#/id\n"
            + "        payload:\n"
            + "          type: object\n"
            + "  # order/old:\n"
            + "  order/shipped:\n"
            + "    subscribe:\n"
            + "      message:\n"
            + "        $ref: '#/components/messages/Shipped'\n"
            + "components:\n"
            + "  securitySchemes:\n"
            + "    sasl:\n"
            + "      type: scramSha512\n";

    @Test
    public void asyncapi_countsVersionTwoOperationsAndNotTheDescription() {
        assertThat(count(ASYNCAPI, "operations", ASYNCAPI_V2), is(2));
        assertThat(count(ASYNCAPI, "operationIds", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "payloads", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "server protocols", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "bindings", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "correlation ids", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "$ref references", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "object schemas", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "security schemes", ASYNCAPI_V2), is(1));
        assertThat(count(ASYNCAPI, "channel addresses (3.x)", ASYNCAPI_V2), is(0));
    }

    @Test
    public void asyncapi_aSchemaPropertyNamedAddress_isNoChannelAddress() {
        final String schema = "asyncapi: 2.6.0\n"
                + "components:\n"
                + "  schemas:\n"
                + "    Customer:\n"
                + "      properties:\n"
                + "        address:\n"
                + "          type: string\n"
                + "        billing: {\"address\": {\"type\": \"string\"}}\n";

        assertThat(count(ASYNCAPI, "channel addresses (3.x)", schema), is(0));
        assertThat(
                count(ASYNCAPI, "channel addresses (3.x)", "{\"channels\": {\"a\": {\"address\": \"order/a\"}}}"),
                is(1));
    }

    @Test
    public void asyncapi_countsVersionThreeChannelsAndActions() {
        final String v3 = "asyncapi: 3.0.0\n"
                + "channels:\n"
                + "  placed:\n"
                + "    address: order/placed\n"
                + "  shipped:\n"
                + "    address: 'order/{id}/shipped'\n"
                + "operations:\n"
                + "  onPlaced:\n"
                + "    action: receive\n"
                + "  sendShipped:\n"
                + "    action: send\n"
                + "    # action: send\n";

        assertThat(count(ASYNCAPI, "channel addresses (3.x)", v3), is(2));
        assertThat(count(ASYNCAPI, "operations", v3), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="terraform-facts">
    private static final String TERRAFORM_SOURCE = "terraform {\n"
            + "  required_providers {\n"
            + "    aws = { source = \"hashicorp/aws\" }\n"
            + "  }\n"
            + "  backend \"s3\" {\n"
            + "    bucket = \"state\"\n"
            + "  }\n"
            + "}\n"
            + "provider \"aws\" {\n"
            + "  region = var.region\n"
            + "}\n"
            + "variable \"region\" {\n"
            + "  default = \"eu-central-1\"\n"
            + "}\n"
            + "variable \"db_password\" {\n"
            + "  sensitive = true\n"
            + "}\n"
            + "locals {\n"
            + "  name = \"shop # not a comment\"\n"
            + "}\n"
            + "# resource \"aws_s3_bucket\" \"old\" {}\n"
            + "// module \"old\" {}\n"
            + "/* data \"aws_ami\" \"old\" {} */\n"
            + "data \"aws_ami\" \"base\" {\n"
            + "  most_recent = true\n"
            + "}\n"
            + "resource \"aws_instance\" \"web\" {\n"
            + "  count    = 2\n"
            + "  provider = aws.west\n"
            + "  user_data = <<-EOF\n"
            + "    resource \"fake\" \"x\" {}\n"
            + "    EOF\n"
            + "  dynamic \"ebs_block_device\" {\n"
            + "    for_each = var.disks\n"
            + "    content {}\n"
            + "  }\n"
            + "}\n"
            + "module \"network\" {\n"
            + "  source = \"./network\"\n"
            + "}\n"
            + "moved {\n"
            + "  from = aws_instance.old\n"
            + "  to   = aws_instance.web\n"
            + "}\n"
            + "output \"ip\" {\n"
            + "  value = aws_instance.web[0].public_ip\n"
            + "}\n";

    @Test
    public void terraform_countsTheBlocksAndNotCommentsHeredocsOrAttributes() {
        assertThat(count(TERRAFORM, "resources", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "data sources", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "modules", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "variables", TERRAFORM_SOURCE), is(2));
        assertThat(count(TERRAFORM, "outputs", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "locals blocks", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "providers", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "backends", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "dynamic blocks", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "count and for_each", TERRAFORM_SOURCE), is(2));
        assertThat(count(TERRAFORM, "refactoring blocks", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "sensitive values", TERRAFORM_SOURCE), is(1));
        assertThat(count(TERRAFORM, "Terragrunt includes and dependencies", TERRAFORM_SOURCE), is(0));
    }

    @Test
    public void terraform_terragruntBlocksAndAnUnclosedHeredoc() {
        final String terragrunt = "include \"root\" {\n  path = find_in_parent_folders()\n}\n"
                + "dependency \"vpc\" {\n  config_path = \"../vpc\"\n}\n";
        final String unclosed = "resource \"a\" \"b\" {\n  x = <<EOT\nresource \"c\" \"d\" {}\n";

        assertThat(count(TERRAFORM, "Terragrunt includes and dependencies", terragrunt), is(2));
        assertThat(count(TERRAFORM, "resources", unclosed), is(1));
    }

    @Test
    public void terraform_referencesOnALineOfTheirOwn_areNoBlocks() {
        final String references = "resource \"aws_instance\" \"web\" {\n"
                + "  depends_on = [\n"
                + "    module.network,\n"
                + "    data.aws_caller_identity.current,\n"
                + "    provider.aws,\n"
                + "  ]\n"
                + "}\n";

        assertThat(count(TERRAFORM, "resources", references), is(1));
        assertThat(count(TERRAFORM, "modules", references), is(0));
        assertThat(count(TERRAFORM, "data sources", references), is(0));
        assertThat(count(TERRAFORM, "providers", references), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ansible-facts">
    private static final String PLAYBOOK = "---\n"
            + "- name: Deploy the shop\n"
            + "  hosts: web\n"
            + "  become: true\n"
            + "  roles:\n"
            + "    - common\n"
            + "  tasks:\n"
            + "    - name: Copy the jar\n"
            + "      ansible.builtin.copy:\n"
            + "        src: shop.jar\n"
            + "        dest: \"/opt/{{ app_dir }}/shop.jar\"\n"
            + "      notify: restart shop\n"
            + "    - name: Run the migration\n"
            + "      ansible.builtin.shell: |\n"
            + "        - name: not a task\n"
            + "        ./migrate.sh\n"
            + "      register: migration\n"
            + "      when: migrate | bool\n"
            + "    # - name: old task\n"
            + "    - name: Open the ports\n"
            + "      community.general.ufw:\n"
            + "        port: \"{{ item }}\"\n"
            + "      loop: [8080, 8443]\n"
            + "    - ansible.builtin.include_tasks: extra.yml\n"
            + "    - name: Users\n"
            + "      ansible.builtin.user:\n"
            + "        name: \"{{ item }}\"\n"
            + "        password: !vault |\n"
            + "          $ANSIBLE_VAULT;1.1;AES256\n"
            + "      with_items: [a, b]\n"
            + "  handlers:\n"
            + "    - name: restart shop\n"
            + "      ansible.builtin.service:\n"
            + "        name: shop\n"
            + "- import_playbook: monitoring.yml\n";

    @Test
    public void ansible_countsPlaysTasksAndModulesAndNotTheScriptBody() {
        assertThat(count(ANSIBLE, "plays", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "named tasks, handlers and plays", PLAYBOOK), is(6));
        assertThat(count(ANSIBLE, "FQCN module calls", PLAYBOOK), is(6));
        assertThat(count(ANSIBLE, "role inclusions", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "task and playbook inclusions", PLAYBOOK), is(2));
        assertThat(count(ANSIBLE, "handler notifications", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "privilege escalation", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "conditionals", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "loops", PLAYBOOK), is(2));
        assertThat(count(ANSIBLE, "registered results", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "vault-encrypted values", PLAYBOOK), is(1));
        assertThat(count(ANSIBLE, "template expressions", PLAYBOOK), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="compose-facts">
    private static final String COMPOSE_SOURCE = "services:\n"
            + "  shop:\n"
            + "    build: .\n"
            + "    image: shop:${TAG:-latest}\n"
            + "    ports:\n"
            + "      - \"8080:8080\"\n"
            + "    environment:\n"
            + "      DB_URL: jdbc:postgresql://db/shop\n"
            + "    env_file: .env\n"
            + "    depends_on:\n"
            + "      - db\n"
            + "    restart: unless-stopped\n"
            + "    deploy:\n"
            + "      replicas: 2\n"
            + "    secrets:\n"
            + "      - db_password\n"
            + "  db:\n"
            + "    image: postgres:16\n"
            + "    command: >\n"
            + "      postgres -c image: x\n"
            + "    healthcheck:\n"
            + "      test: [\"CMD\", \"pg_isready\"]\n"
            + "    volumes:\n"
            + "      - data:/var/lib/postgresql/data\n"
            + "    profiles: [full]\n"
            + "    networks: [back]\n"
            + "  # cache:\n"
            + "  #   image: redis\n"
            + "volumes:\n"
            + "  data: {}\n"
            + "networks:\n"
            + "  back: {}\n"
            + "secrets:\n"
            + "  db_password:\n"
            + "    file: ./db_password.txt\n";

    @Test
    public void compose_countsServicesAndTheirKeysAndNotComments() {
        assertThat(count(COMPOSE, "services with an image", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "services built here", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "port lists", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "dependencies between services", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "healthchecks", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "environment blocks and files", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "volume lists", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "network lists", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "secret lists", COMPOSE_SOURCE), is(2));
        assertThat(count(COMPOSE, "restart policies", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "profiles", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "deploy sections", COMPOSE_SOURCE), is(1));
        assertThat(count(COMPOSE, "variable substitutions", COMPOSE_SOURCE), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="camel-yaml-facts">
    private static final String CAMEL_ROUTES = "- beans:\n"
            + "    - name: pricing\n"
            + "      type: shop.Pricing\n"
            + "- route:\n"
            + "    id: orders\n"
            + "    from:\n"
            + "      uri: jms:queue:orders\n"
            + "      steps:\n"
            + "        - log: received ${body}\n"
            + "        - choice:\n"
            + "            when:\n"
            + "              - simple: ${header.priority} == 'high'\n"
            + "                steps:\n"
            + "                  - to:\n"
            + "                      uri: jms:queue:urgent\n"
            + "            otherwise:\n"
            + "              steps:\n"
            + "                - toD: jms:queue:${header.region}\n"
            + "        - split:\n"
            + "            simple: ${body.lines}\n"
            + "        - setBody:\n"
            + "            groovy: |\n"
            + "              - to: not a step\n"
            + "        - bean:\n"
            + "            ref: pricing\n"
            + "        # - wireTap: jms:queue:audit\n"
            + "- from:\n"
            + "    uri: timer:tick\n"
            + "    steps:\n"
            + "      - doTry:\n"
            + "          steps:\n"
            + "            - to: http://stock/refresh\n"
            + "- routeTemplate:\n"
            + "    id: poller\n"
            + "- rest:\n"
            + "    path: /orders\n"
            + "    get:\n"
            + "      - to: direct:list\n"
            + "    post:\n"
            + "      - to: direct:create\n";

    @Test
    public void camelYaml_countsRoutesAndEipsAndNotTheScripts() {
        assertThat(count(CAMEL_YAML, "routes", CAMEL_ROUTES), is(2));
        assertThat(count(CAMEL_YAML, "route templates", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "consumers (from)", CAMEL_ROUTES), is(2));
        assertThat(count(CAMEL_YAML, "producers (to, toD, wireTap, enrich, pollEnrich)", CAMEL_ROUTES), is(5));
        assertThat(count(CAMEL_YAML, "endpoint URIs", CAMEL_ROUTES), is(3));
        assertThat(count(CAMEL_YAML, "content-based routers (choice)", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "splitters, aggregators and other routing EIPs", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "processors and transformations", CAMEL_ROUTES), is(3));
        assertThat(count(CAMEL_YAML, "error handling", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "REST definitions", CAMEL_ROUTES), is(1));
        assertThat(count(CAMEL_YAML, "REST verb blocks (get, post, put, delete, patch, head)", CAMEL_ROUTES), is(2));
        assertThat(count(CAMEL_YAML, "bean declarations", CAMEL_ROUTES), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="gradle-facts">
    private static final String GROOVY_BUILD = "plugins {\n"
            + "    id 'java-library'\n"
            + "    id \"org.springframework.boot\" version \"3.3.0\"\n"
            + "    alias(libs.plugins.spotless)\n"
            + "}\n"
            + "apply plugin: 'maven-publish'\n"
            + "repositories {\n"
            + "    mavenCentral()\n"
            + "    maven { url 'https://repo.example.org/releases' }\n"
            + "}\n"
            + "dependencies {\n"
            + "    implementation platform(libs.spring.bom)\n"
            + "    implementation 'org.slf4j:slf4j-api:2.0.13'\n"
            + "    api project(':core')\n"
            + "    // implementation 'old:lib:1.0'\n"
            + "    compileOnly libs.lombok\n"
            + "    annotationProcessor libs.lombok\n"
            + "    testImplementation \"org.junit.jupiter:junit-jupiter:5.10.2\"\n"
            + "}\n"
            + "configurations {\n"
            + "    implementation.extendsFrom(custom)\n"
            + "}\n"
            + "tasks.register('fatJar', Jar) {\n"
            + "    description = \"\"\"implementation 'x:y:1' in a string\"\"\"\n"
            + "}\n"
            + "task hello {\n"
            + "}\n";

    private static final String KOTLIN_BUILD = "plugins {\n"
            + "    `java-library`\n"
            + "    kotlin(\"jvm\") version \"2.0.0\"\n"
            + "    id(\"com.diffplug.spotless\")\n"
            + "}\n"
            + "dependencies {\n"
            + "    implementation(enforcedPlatform(libs.boot.bom))\n"
            + "    implementation(projects.core)\n"
            + "    runtimeOnly(\"org.postgresql:postgresql:42.7.3\")\n"
            + "    /* testImplementation(\"old:lib:1.0\") */\n"
            + "}\n"
            + "tasks.named<Test>(\"test\") {\n"
            + "    useJUnitPlatform()\n"
            + "}\n";

    @Test
    public void gradle_countsAGroovyBuildScript() {
        assertThat(count(GRADLE, "plugins", GROOVY_BUILD), is(4));
        assertThat(count(GRADLE, "dependency declarations", GROOVY_BUILD), is(6));
        assertThat(count(GRADLE, "project dependencies", GROOVY_BUILD), is(1));
        assertThat(count(GRADLE, "platforms and BOMs", GROOVY_BUILD), is(1));
        assertThat(count(GRADLE, "repositories", GROOVY_BUILD), is(2));
        assertThat(count(GRADLE, "tasks registered, created or configured", GROOVY_BUILD), is(2));
        assertThat(count(GRADLE, "version catalog references", GROOVY_BUILD), is(4));
    }

    @Test
    public void gradle_countsAKotlinBuildScriptAndASettingsScript() {
        final String settings = "rootProject.name = \"shop\"\n"
                + "include(\"core\", \"web\")\n"
                + "include ':cli'\n"
                + "includeBuild(\"build-logic\")\n";

        assertThat(count(GRADLE, "plugins", KOTLIN_BUILD), is(3));
        assertThat(count(GRADLE, "dependency declarations", KOTLIN_BUILD), is(3));
        assertThat(count(GRADLE, "project dependencies", KOTLIN_BUILD), is(1));
        assertThat(count(GRADLE, "platforms and BOMs", KOTLIN_BUILD), is(1));
        assertThat(count(GRADLE, "tasks registered, created or configured", KOTLIN_BUILD), is(1));
        assertThat(count(GRADLE, "included projects and builds", settings), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="properties-facts">
    @Test
    public void properties_countsKeysAndNotComments() {
        final String properties = "# server.port=1\n"
                + "! legacy=1\n"
                + "server.port=8080\n"
                + "spring.datasource.url = jdbc:postgresql://db:5432/shop\n"
                + "spring.datasource.password=${DB_PASSWORD}\n"
                + "app.api-token: abc\n"
                + "greeting=Hello\n"
                + "\n";

        assertThat(count(PROPERTIES, "keys (approx)", properties), is(5));
        assertThat(count(PROPERTIES, "keys naming a secret", properties), is(2));
        assertThat(count(PROPERTIES, "placeholders", properties), is(1));
        assertThat(count(PROPERTIES, "URLs", properties), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="powershell-facts">
    private static final String POWERSHELL_SCRIPT = "#Requires -Version 7\n"
            + "#requires -Modules Az.Accounts\n"
            + "<#\n"
            + ".SYNOPSIS\n"
            + "function Not-AFunction { }\n"
            + "#>\n"
            + "using module Shop.Tools\n"
            + "Import-Module Az.Storage\n"
            + ". ./common.ps1\n"
            + "class Order { [string] $Id }\n"
            + "function Deploy-Shop {\n"
            + "    [CmdletBinding()]\n"
            + "    param(\n"
            + "        [Parameter(Mandatory)] [string] $Environment\n"
            + "    )\n"
            + "    # function Old-Thing { }\n"
            + "    $note = \"function Fake-Thing in a string\"\n"
            + "    $body = @\"\n"
            + "function Here-String { }\n"
            + "\"@\n"
            + "    try {\n"
            + "        Get-AzStorageAccount -Name $Environment | ForEach-Object { $_ } | ConvertTo-Json\n"
            + "    } catch {\n"
            + "        Write-Error \"failed\"\n"
            + "        throw\n"
            + "    }\n"
            + "}\n"
            + "FUNCTION helper { }\n";

    @Test
    public void powershell_countsFunctionsAndRequirementsAndNotHelpOrStrings() {
        assertThat(count(POWERSHELL, "requirements", POWERSHELL_SCRIPT), is(2));
        assertThat(count(POWERSHELL, "functions", POWERSHELL_SCRIPT), is(2));
        assertThat(count(POWERSHELL, "advanced functions", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "parameter blocks", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "mandatory parameters", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "classes", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "modules imported", POWERSHELL_SCRIPT), is(2));
        assertThat(count(POWERSHELL, "dot-sourced scripts", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "try blocks", POWERSHELL_SCRIPT), is(1));
        assertThat(count(POWERSHELL, "errors raised", POWERSHELL_SCRIPT), is(2));
        // Import-Module, Deploy-Shop, Get-AzStorageAccount, ForEach-Object, ConvertTo-Json, Write-Error
        assertThat(count(POWERSHELL, "Verb-Noun command calls (approx)", POWERSHELL_SCRIPT), is(6));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="batch-facts">
    @Test
    public void batch_countsLabelsCallsAndVariablesAndNotComments() {
        final String script = "@echo off\n"
                + "setlocal enabledelayedexpansion\n"
                + "REM goto :old\n"
                + ":: call old.bat\n"
                + "@rem set OLD=1\n"
                + "set APP_HOME=%~dp0\n"
                + "set /a COUNT=0\n"
                + "if \"%1\"==\"\" goto :usage\n"
                + "call :build\n"
                + "for /f \"tokens=*\" %%a in (list.txt) do set /a COUNT+=1\n"
                + "for /r C:\\src %%f in (*.java) do echo %%f\n"
                + "if errorlevel 1 exit /b 1\n"
                + "goto :eof\n"
                + ":build\n"
                + "call mvn -B package\n"
                + "exit /b %errorlevel%\n"
                + ":usage\n"
                + "echo usage: build.bat target\n"
                + "EXIT /B 2\n";

        assertThat(count(BATCH, "labels", script), is(2));
        assertThat(count(BATCH, "calls", script), is(2));
        assertThat(count(BATCH, "gotos", script), is(2));
        assertThat(count(BATCH, "variables set", script), is(3));
        assertThat(count(BATCH, "conditionals (approx)", script), is(2));
        assertThat(count(BATCH, "for loops", script), is(2));
        assertThat(count(BATCH, "local scopes", script), is(1));
        assertThat(count(BATCH, "exits", script), is(3));
        assertThat(count(BATCH, "error level checks", script), is(2));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="npm-facts">
    /**
     * A {@code package.json} with every counted object, a nested {@code scripts} key that is no npm
     * script, an escaped quote and an empty object.
     */
    private static final String PACKAGE_JSON = "{\n"
            + "  \"name\": \"demo\",\n"
            + "  \"workspaces\": [\"packages/*\", \"tools\"],\n"
            + "  \"scripts\": {\n"
            + "    \"build\": \"tsc -p .\",\n"
            + "    \"test\": \"jest --config \\\"jest.json\\\"\",\n"
            + "    \"lint\" : \"eslint .\"\n"
            + "  },\n"
            + "  \"dependencies\": {\"express\": \"^4.19.2\", \"zod\": \"3.23.8\"},\n"
            + "  \"devDependencies\": {\n"
            + "    \"typescript\": \"5.6.3\",\n"
            + "    \"jest\": \"29.7.0\",\n"
            + "    \"eslint\": \"9.12.0\"\n"
            + "  },\n"
            + "  \"peerDependencies\": {\"react\": \">=18\"},\n"
            + "  \"optionalDependencies\": {},\n"
            + "  \"engines\": {\"node\": \">=20\", \"npm\": \">=10\"},\n"
            + "  \"tool\": {\"scripts\": {\"nested\": \"x\"}, \"after\": \"y\"}\n"
            + "}\n";

    @Test
    public void npm_countsTheEntriesOfEachObject() {
        // act + assert
        assertThat(count(NPM, "scripts", PACKAGE_JSON), is(4));
        assertThat(count(NPM, "dependencies", PACKAGE_JSON), is(2));
        assertThat(count(NPM, "dev dependencies", PACKAGE_JSON), is(3));
        assertThat(count(NPM, "peer dependencies", PACKAGE_JSON), is(1));
        assertThat(count(NPM, "optional dependencies", PACKAGE_JSON), is(0));
        assertThat(count(NPM, "engines", PACKAGE_JSON), is(2));
        assertThat(count(NPM, "workspaces", PACKAGE_JSON), is(2));
    }

    @Test
    public void npm_theCountStopsAtTheObjectsEnd() {
        // arrange: the entry after the scripts object follows its }, not a comma between its entries
        final String nested = "{\"tool\": {\"scripts\": {\"a\": \"x\"}, \"after\": \"y\"}}";

        // act + assert
        assertThat(count(NPM, "scripts", nested), is(1));
    }
    // </editor-fold>
}
