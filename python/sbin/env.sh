#!/bin/bash -e

BASEDIR=`dirname $0`/..
source "${BASEDIR}/sbin/var-env.sh"


PYTHON_VERSION=${PYTHON_VERSION:=${DEVELOP_PYTHON_VERSION}}

echo "Making env for python ${PYTHON_VERSION}"

virtualenv -p python${DEVELOP_PYTHON_VERSION} -q ${BASEDIR}/env

source $BASEDIR/env/bin/activate

pip install -r $BASEDIR/requirements.txt
pip install -e $BASEDIR
