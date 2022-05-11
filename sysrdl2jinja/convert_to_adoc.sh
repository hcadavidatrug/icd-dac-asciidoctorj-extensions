#This script assumes the python dependencies defined in dependencies.txt are already available
cd $(dirname $0)

python3 sysrdl2jinja/convert.py $1 templates/sysrdl_adoc_table.template $2

cd -
