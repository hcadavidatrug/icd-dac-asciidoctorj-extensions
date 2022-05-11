cd $(dirname $0)
PYTHON_ENV_INTERPRETER=bin/python3

if test -f "$PYTHON_ENV_INTERPRETER"; then
	echo "Python local environment with dependencies installed"
	source bin/activate
else
	echo "Setting up local environment and dependencies"
	python3 -m venv .
	source bin/activate
	pip install -r requirements.txt
fi

python3 sysrdl2jinja/convert.py $1 templates/sysrdl_adoc_table.template $2

cd -
