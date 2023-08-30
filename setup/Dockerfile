FROM alpine/git:2.40.1

RUN apk --no-cache add curl

COPY setup.sh /

ENTRYPOINT ["sh", "/setup.sh"]
