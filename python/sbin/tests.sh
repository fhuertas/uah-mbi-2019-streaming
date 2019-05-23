#!/bin/bash -e

BASEDIR=`dirname $0`/..
source "${BASEDIR}/bin/var-env.sh"

PYTHON_VERSION=${PYTHON_VERSION:=${DEVELOP_PYTHON_VERSION}}
VERBOSITY=${VERBOSITY:=$TEST_VERBOSITY}
TEST_DIR=${BASEDIR}/env-test${PYTHON_VERSION}


echo "Testing on python version: ${PYTHON_VERSION}"
echo "Test environment: $TEST_DIR"

if [ ! -d "$TEST_DIR" ]; then
    virtualenv -p python$1 -q $TEST_DIR
    echo "New virtualenv for UT created."

    source $TEST_DIR/bin/activate
    echo "New virtualenv for UT activated."
    pip3 install -r $BASEDIR/requirements.txt
    pip3 install -e $BASEDIR
fi

export COVERAGE_FILE=.coverage
for PACKAGE in ${PACKAGES[@]}; do
  for TEST in ${TEST_FOLDERS[@]}; do
     ${TEST_DIR}/bin/nosetests --verbosity=${VERBOSITY} --with-coverage --cover-package=${PACKAGE} ${TEST}
  done
done
$TEST_DIR/bin/coverage xml
echo "Total report"
$TEST_DIR/bin/coverage report
