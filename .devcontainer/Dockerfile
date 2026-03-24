FROM mcr.microsoft.com/playwright:v1.50.0-noble
RUN apt-get update && apt-get install -y wget apt-transport-https gnupg\
    && wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add -\
    && echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list\
    && apt-get update && apt-get install -y temurin-21-jdk