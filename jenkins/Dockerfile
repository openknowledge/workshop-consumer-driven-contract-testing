FROM jenkins/jenkins:2.414.1-lts-jdk11

RUN jenkins-plugin-cli -p git workflow-aggregator pipeline-utility-steps generic-webhook-trigger pipeline-stage-view

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
