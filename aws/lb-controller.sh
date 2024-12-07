#!/bin/bash
#
# Creates AWS load balancer controller
#
source ./utils.sh

AWS_ACCOUNT_ID="690837617850"
CLUSTER_NAME="suddenrun"
REGION="eu-north-1"
POLICY_NAME="AWSLoadBalancerControllerIAMPolicy"
ROLE_NAME="AmazonEKSLoadBalancerControllerRole"
SERVICE_ACCOUNT_NAME="aws-load-balancer-controller"

describe_flags() {
  echo "Available flags:"
  echo "-c: install AWS Load Balancer Controller add-on"
}

while getopts "c" flag; do
  case ${flag} in
  c)
    create_oidc_provider $REGION $CLUSTER_NAME
    oidc_id=$(get_oidc_id $CLUSTER_NAME)
    terminate_if_empty "$oidc_id"

    printf "Looking for policy '%s'\n" $POLICY_NAME
    iam_policy=$(get_iam_policy "$AWS_ACCOUNT_ID" "$POLICY_NAME")
    if [[ $iam_policy == "" ]]; then
      printf "Policy '%s' not found\n" "$POLICY_NAME"
      download_aws_load_balancer_controller_iam_policy
      create_iam_policy $POLICY_NAME
    fi

    printf "Looking for role '%s'\n" $ROLE_NAME
    iam_role=$(get_iam_role $ROLE_NAME)
    if [[ $iam_role == "" ]]; then
      printf "Role '%s' not found\n" "$ROLE_NAME"
      create_aws_load_balancer_controller_service_account $AWS_ACCOUNT_ID $CLUSTER_NAME $ROLE_NAME $POLICY_NAME
    fi

    echo "Trying to add helm repo"
    helm repo add eks https://aws.github.io/eks-charts
    echo "Trying to update local helm repo"
    helm repo update

    install_aws_load_balancer_controller $SERVICE_ACCOUNT_NAME $CLUSTER_NAME

    sleep 5
    kubectl get deployment -n kube-system aws-load-balancer-controller
    ;;
  \?)
    describe_flags
    ;;
  esac
done
