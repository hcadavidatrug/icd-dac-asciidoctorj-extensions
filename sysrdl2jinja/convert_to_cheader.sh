#This script has as a precondition that the python dependencies defined in dependencies.txt are already available

cd $(dirname $0)

python3 sysrdl2jinja/convert.py $1 templates/sysrdl_c_header.template $2
sha256sum $2 | head -c 64  > $2.sha

cd -
