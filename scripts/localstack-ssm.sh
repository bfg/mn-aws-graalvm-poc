#!/usr/bin/env bash

APP_NAME=${APP_NAME:-"my-app"}
export AWS_REGION=${AWS_REGION:-"us-east-1"}

#####################################################

SAFE_STR=0

die() {
  echo "FATAL: $1" >&2
  exit 1
}

create_parameter() {
  local name="$1"
  local value="$2"

  local type="String"
  if [ $SAFE_STR -eq 1 ]; then
    type="SecureString"
    SAFE_STR=0
  else
    type="String"
    SAFE_STR=1
  fi

  local param_name="/${APP_NAME}/${name}"
  #awslocal ssm delete-parameter --name="$param_name" >/dev/null 2>&1
  awslocal ssm put-parameter \
    --name "$param_name" \
    --value "${value}" \
    --description "Parameter description for: ${name}" \
    --type "${type}" \
    --overwrite
}

do_create() {
  local name=""
  local value=""
  for name in a b c d foo/bar bar/baz/kaz ; do
    value="${RANDOM}-val-${name}"
    create_parameter "$name" "$value" || die "failed to create parameter: $name"
  done

}

do_list() {
  local path="$1"
  test -z "$path" && path="/${APP_NAME}"

  awslocal ssm get-parameters-by-path \
    --path="$path" \
    --with-decryption \
    --recursive
}

do_help() {
  cat <<EOF
Usage: $0 <action> [<args>]

ENV:
  APP_NAME           Name of the application (default: "$APP_NAME")

ACTIONS:

- create              Create parameters
- list <path>         List parameters by path
EOF
}

do_run() {
  local action="$1"
  shift

  test -z "$action" -o "$action" = "-h" -o "$action" = "--help" && {
    do_help
    exit 0
  }

  local func_name="do_${action}"

  if [ "$(type -t "$func_name")" != "function" ]; then
    die "Unknown action: $action"
  fi

  ${func_name} "$@"
}

#####################################################

do_run "$@"

# vim:shiftwidth=2 softtabstop=2 expandtab
# EOF
