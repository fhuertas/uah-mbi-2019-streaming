PYTHON_VERSION=
PYTHON_ENV_TEST=env-test$(PYTHON_VERSION)
all: clean env test-versions package

env: env/bin/activate

env/bin/activate:
	sbin/env.sh

test:
	sbin/tests.sh $(PYTHON_VERSION)

test-clean:
	rm -Rf env-*

clean:
	sbin/clean.sh

package: clean env
	sbin/package.sh

continuous-test:
	bash sbin/exec-continuous-test.sh
