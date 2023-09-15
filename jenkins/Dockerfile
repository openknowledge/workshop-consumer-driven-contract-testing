FROM jenkins/jenkins:2.414.1-lts-jdk11

RUN jenkins-plugin-cli -p git workflow-aggregator pipeline-utility-steps generic-webhook-trigger pipeline-stage-view

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false


USER root

# increase number of executors
COPY --chown=jenkins:jenkins executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy

RUN apt-get update -qq

# install docker
RUN apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update -qq && apt-get install docker-ce -y
RUN usermod -aG docker jenkins

#install kubectl
RUN curl -fsSL https://packages.cloud.google.com/apt/doc/apt-key.gpg | gpg --dearmor -o /etc/apt/keyrings/kubernetes-archive-keyring.gpg
RUN echo "deb [signed-by=/etc/apt/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | tee /etc/apt/sources.list.d/kubernetes.list
RUN apt-get update && apt-get install -qy kubectl kubelet kubeadm

# install maven
RUN apt-get install -y maven

# install pact client
RUN curl -LO https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v1.69.0/pact-1.69.0-linux-x86_64.tar.gz && tar xzf pact-1.69.0-linux-x86_64.tar.gz -C /usr/local/ && ln -s /usr/local/pact/bin/pact-broker /usr/local/bin/pact-broker

# configure access to cluster
COPY kube-config /root/.kube/config

#USER jenkins
