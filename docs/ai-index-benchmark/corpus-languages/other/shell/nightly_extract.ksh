#!/bin/ksh93
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# Nightly export of the settled payments of one business day from Oracle into a CSV file, gzipped,
# with a row-count control file next to it.
# Usage: nightly_extract.ksh [-d YYYYMMDD] [-o <dir>]

typeset -r ORACLE_SID_DEFAULT=PAYPRD
typeset -r EXPORT_DIR_DEFAULT=/data/export/payments
typeset -i MAX_RETRIES=3
typeset -i attempt=0
typeset business_day=$(date +%Y%m%d)
typeset export_dir=$EXPORT_DIR_DEFAULT

export ORACLE_SID=${ORACLE_SID:-$ORACLE_SID_DEFAULT}
export NLS_LANG=AMERICAN_AMERICA.AL32UTF8

function log {
    print -u2 -- "$(date '+%Y-%m-%d %H:%M:%S') nightly_extract: $*"
}

function fail {
    log "FAILED: $1"
    exit ${2:-1}
}

function require_tools {
    typeset tool
    for tool in sqlplus gzip; do
        whence -q $tool || fail "missing $tool" 3
    done
}

function run_export {
    typeset outfile=$1
    sqlplus -s /nolog <<-EOSQL > "$outfile"
	CONNECT / AS SYSDBA
	SET HEADING OFF FEEDBACK OFF PAGESIZE 0 LINESIZE 400 TRIMSPOOL ON
	SELECT payment_id || ';' || account_no || ';' || amount || ';' || currency
	  FROM settled_payment
	 WHERE business_day = TO_DATE('$business_day', 'YYYYMMDD');
	EXIT
	EOSQL
}

function write_control {
    typeset csv=$1
    typeset -i rows
    rows=$(wc -l < "$csv")
    print "rows=$rows business_day=$business_day" > "${csv%.csv}.ctl"
}

while getopts "d:o:" opt; do
    case $opt in
        d) business_day=$OPTARG ;;
        o) export_dir=$OPTARG ;;
        *) fail "usage: nightly_extract.ksh [-d YYYYMMDD] [-o dir]" 2 ;;
    esac
done
shift $((OPTIND - 1))

require_tools
[[ -d $export_dir ]] || mkdir -p "$export_dir" || fail "cannot create $export_dir"
csv=$export_dir/payments_$business_day.csv

until (( attempt >= MAX_RETRIES )); do
    (( attempt++ ))
    if run_export "$csv"; then
        write_control "$csv"
        gzip -f "$csv"
        log "exported business day $business_day (attempt $attempt)"
        exit 0
    fi
    log "attempt $attempt failed, retrying"
    sleep 60
done
fail "giving up after $MAX_RETRIES attempts" 4
