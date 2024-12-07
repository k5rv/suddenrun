#!/bin/bash
#
# Creates infrastructure and deploys application
#
sh ./vpc.sh -c
sh ./rds.sh -c
sh ./eks.sh -c
sh ./lb-controller.sh -c
kubectl apply -f "../k8s/eks/services/suddenrun/"
sh ./alb.sh -w
sh ./dns_record_alias.sh -c
