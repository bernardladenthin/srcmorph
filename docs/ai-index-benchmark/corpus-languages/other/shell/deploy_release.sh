#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# Rolls one release tarball out to every host of an environment, one host at a time, and restarts
# the service there; stops at the first host whose health check fails.
# Usage: deploy_release.sh -e <staging|production> -t <tag> [-n] [-p <parallel>]
set -euo pipefail

readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

DEPLOY_USER="${DEPLOY_USER:-deploy}"
SSH_KEY="${SSH_KEY:-$HOME/.ssh/deploy_ed25519}"
export RELEASE_ROOT="/opt/releases"
SERVICE_NAME="orderd"
HEALTH_TIMEOUT=30
ENVIRONMENT=""
TAG=""
DRY_RUN=0
declare -a HOSTS=()
declare -A HOST_FILES=([staging]="hosts.staging" [production]="hosts.production")

log() {
  printf '%s %s\n' "$(date -u +%H:%M:%S)" "$*" >&2
}

die() {
  log "ERROR: $*"
  exit 1
}

function usage {
  sed -n 's/^# Usage: //p' "$0"
}

check_prereqs() {
  local tool
  for tool in rsync ssh curl; do
    command -v "$tool" >/dev/null || die "missing tool: $tool"
  done
  [[ -r "$SSH_KEY" ]] || die "cannot read SSH key $SSH_KEY"
}

load_hosts() {
  local file="$SCRIPT_DIR/${HOST_FILES[$ENVIRONMENT]:?unknown environment: $ENVIRONMENT}"
  mapfile -t HOSTS < <(grep -v '^#' "$file")
  (( ${#HOSTS[@]} > 0 )) || die "no hosts in $file"
}

deploy_host() {
  local host="$1"
  local target="$RELEASE_ROOT/$TAG"
  if (( DRY_RUN )); then
    log "would deploy $TAG to $host"
    return 0
  fi
  rsync -az -e "ssh -i $SSH_KEY" "build/$TAG.tar.gz" "$DEPLOY_USER@$host:/tmp/"
  ssh -i "$SSH_KEY" "$DEPLOY_USER@$host" bash -s <<EOF
set -e
mkdir -p "$target"
tar -xzf "/tmp/$TAG.tar.gz" -C "$target"
ln -sfn "$target" "$RELEASE_ROOT/current"
sudo systemctl restart "$SERVICE_NAME"
EOF
  wait_healthy "$host"
}

wait_healthy() {
  local host="$1" waited=0
  until curl -fsS "http://$host:8080/health" >/dev/null; do
    (( waited += 2 ))
    (( waited < HEALTH_TIMEOUT )) || die "$host not healthy after ${HEALTH_TIMEOUT}s"
    sleep 2
  done
}

cleanup() {
  rm -f "/tmp/deploy_release.$$.lock"
}
trap cleanup EXIT

while getopts ":e:t:np:h" opt; do
  case "$opt" in
    e) ENVIRONMENT="$OPTARG" ;;
    t) TAG="$OPTARG" ;;
    n) DRY_RUN=1 ;;
    p) log "parallel deploys are not supported, ignoring -p $OPTARG" ;;
    h) usage; exit 0 ;;
    \?) die "unknown option -$OPTARG" ;;
    :) die "option -$OPTARG needs a value" ;;
  esac
done
shift $((OPTIND - 1))

[[ -n "$ENVIRONMENT" && -n "$TAG" ]] || { usage; exit 2; }
check_prereqs
load_hosts
for host in "${HOSTS[@]}"; do
  deploy_host "$host"
  log "deployed $TAG to $host"
done
