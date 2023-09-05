FROM alpine/git:2.40.1

RUN apk --no-cache add curl

COPY setup.sh /
COPY jenkins/ /jenkins

ENTRYPOINT ["sh", "/setup.sh"]
