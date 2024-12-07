#!/bin/bash
#
# Removes infrastructure and application
#
sh ./dns_record_alias.sh -d
kubectl delete -f "../k8s/eks/services/suddenrun/"
sh ./eks.sh -d
sh ./rds.sh -d
sh ./vpc.sh -d
