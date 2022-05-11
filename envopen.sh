echo docker build -t "hectorcadavid/asciidoctor-icd-experimental:v0.1"  .
docker run --rm -it   --mount type=bind,source=/home/hcadavid/RUG-local/icd-pipeline-projects/DaC-asciidoctor-extensions-baseline,target=/adocsrc hectorcadavid/asciidoctor-icd-experimental:v0.1  /bin/bash
