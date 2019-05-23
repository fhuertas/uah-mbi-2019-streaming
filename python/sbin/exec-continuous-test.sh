#!/usr/bin/env bash
BASEDIR=`dirname $0`/..
source $BASEDIR/bin/var-env.sh



ENV=${ENV:=env}
set -u
echo "[${PACKAGE}]: Executing tests (${TEST}). Environment: \"${ENV}\""
echo "${BASEDIR}/bin/continuous-test.sh --env ${ENV} --src ${PACKAGE} --test ${TEST}"
bash ${BASEDIR}/bin/continuous-test.sh --env ${ENV} --src ${PACKAGE} --test ${TEST}
