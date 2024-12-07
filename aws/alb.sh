#!/bin/bash
#
# Verifies Application Load Balancer state
#
source ./utils.sh

describe_flags() {
  echo "Available flags:"
  echo "-w: wait for Application Load Balancer to become Active"
}

while getopts "w" flag; do
  case ${flag} in
  w)
    poll_elb_creation
    alb_arn=$(get_target_group_alb_arn)
    poll_elb_instance_state "$alb_arn" "active"
    ;;
  \?)
    describe_flags
    ;;
  esac
done
