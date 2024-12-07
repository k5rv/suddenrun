#!/bin/bash
#
# Creates AWS EKS cluster
#
source ./utils.sh

CLUSTER_NAME="suddenrun"
REGION="eu-north-1"
VPC_NAME="suddenrun"
SUBNET_PRIVATE_A_NAME="private-eu-north-1a"
SUBNET_PRIVATE_B_NAME="private-eu-north-1b"
VERSION="1.26"
INSTANCE_TYPE="t3.small"
MANAGED_NODES_GROUP_NAME="suddenrun"
MIN_SIZE=1
MAX_SIZE=2
DESIRED_CAPACITY=1
VOLUME_SIZE=5
CONFIGURATION_FILE="cluster.yaml"

describe_flags() {
  echo "Available flags:"
  echo "-c: create cluster $CLUSTER_NAME using configuration file $CONFIGURATION_FILE"
  echo "-d: delete cluster $CLUSTER_NAME"
}

while getopts "cd" flag; do
  case ${flag} in
  c)
    vpc_id=$(get_vpc_id $VPC_NAME)
    subnet_private_a_id=$(get_subnet_id "$SUBNET_PRIVATE_A_NAME")
    subnet_private_b_id=$(get_subnet_id "$SUBNET_PRIVATE_B_NAME")

    cat >$CONFIGURATION_FILE <<EOF
# nonk8s
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: "$CLUSTER_NAME"
  region: "$REGION"
  version: "$VERSION"

vpc:
  id: "$vpc_id"
  subnets:
    private:
      eu-north-1a: { id: "$subnet_private_a_id" }
      eu-north-1b: { id: "$subnet_private_b_id" }

managedNodeGroups:
  - name: "$MANAGED_NODES_GROUP_NAME"
    labels: { role: worker }
    instanceType: $INSTANCE_TYPE
    desiredCapacity: $DESIRED_CAPACITY
    privateNetworking: true
    minSize: $MIN_SIZE
    maxSize: $MAX_SIZE
    volumeSize: $VOLUME_SIZE
    iam:
      withAddonPolicies:
        autoScaler: true
    propagateASGTags: true
EOF
    create_cluster $CLUSTER_NAME $CONFIGURATION_FILE
    ;;
  d)
    delete_cluster $CLUSTER_NAME
    ;;
  \?)
    describe_flags
    ;;
  esac
done
