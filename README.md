# Workshop Consumer-Driven Contract Testing

This repository contains samples for the workshop Consumer-Driven Contract Testing.

## Setting up a Cluster with KinD

We use KinD to create a local Kubernetes Cluster.

First install [KinD](https://kind.sigs.k8s.io/docs/user/quick-start).

Create a cluster by executing the following command:

```shell
kind create cluster --config=./deployment/cluster-config/kind-config.yml --name=workshop-cdc-cluster
```

## Build the images and push to cluster

```shell
docker compose build
kind load docker-image host.docker.internal:5000/gogs:local -n workshop-cdc-cluster
kind load docker-image host.docker.internal:5000/jenkins:local -n workshop-cdc-cluster
kind load docker-image host.docker.internal:5000/setup:local -n workshop-cdc-cluster
```

## Initialize the cluster with Kustomize

```shell
kubectl apply -k ./deployment/
```

## Accessing the cluster

Once the cluster and the services are established
(this will take a while), you can access the services with the following urls:

* [Pact Broker](http://localhost:30050/)
* [Gogs (Git)](http://localhost:30060/)
* [Jenkins](http://localhost:30070/)

## Removing the cluster

To remove the cluster, execute the following command:

```shell
kind delete cluster -n workshop-cdc-cluster
```
