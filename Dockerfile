FROM node:fermium-alpine AS node
FROM maven:3.6.1-jdk-8-alpine

USER root

# Node.js-based required dependencies 
# Copying binaries from the official Node image (required by netlify-cli) 
#COPY --from=node /usr/lib /usr/lib
#COPY --from=node /usr/local/share /usr/local/share
#COPY --from=node /usr/local/lib /usr/local/lib
#COPY --from=node /usr/local/include /usr/local/include
#COPY --from=node /usr/local/bin /usr/local/bin

RUN apk add git

# Regular dependencies installation approach (doesn't work for netlify-cli)
RUN apk add --update nodejs nodejs-npm
#ENV ALPINE_MIRROR "http://dl-cdn.alpinelinux.org/alpine"
#RUN echo "${ALPINE_MIRROR}/v3.12/main/" >> /etc/apk/repositories
#RUN apk add nodejs --repository="http://dl-cdn.alpinelinux.org/alpine/v3.12/main/"
#RUN node --version
#RUN npm install -g bytefield-svg
#RUN npm install @mermaid-js/mermaid-cli

#RUN npm install -g netlify-cli --unsafe-perm=true


RUN apk add ttf-dejavu
RUN apk add graphviz
RUN apk add tree


RUN addgroup -S appgroup && adduser -S user -G appgroup



# Python/PIP (to install nwdiag, sysrd2jinja)
ENV PYTHONUNBUFFERED=1
RUN apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python
RUN python3 -m ensurepip
RUN pip3 install --no-cache --upgrade pip setuptools


#RUN pip3 install nwdiag

# Source / Output folders 
RUN mkdir /vale
RUN mkdir /adocsrc
RUN mkdir /adocout
RUN mkdir /public
COPY ./ /asciidocext/
COPY ./.vale.ini /asciidocext/
WORKDIR /asciidocext

# Switch folder owners from root to user 
RUN chown -R user /asciidocext
RUN chown -R user /adocsrc
RUN chown -R user /adocout
RUN chown -R user /public
RUN chown -R user /vale

# Build asciidoctor extensions
USER user
ENV PATH="${PATH}:/node_modules/.bin/"
RUN mvn compile
RUN chmod +x build_docs.sh

# Install sysrdl2jinja dependencies
#RUN sh sysrdl2jinja/install.sh
RUN pip install -r sysrdl2jinja/requirements.txt


RUN wget -P /tmp https://github.com/errata-ai/vale/releases/download/v2.15.2/vale_2.15.2_Linux_64-bit.tar.gz
RUN tar -xf /tmp/vale_2.15.2_Linux_64-bit.tar.gz -C /vale
