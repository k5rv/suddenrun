#!/bin/bash
#
# Attaches ALB to Route53 Hosted Zone
#
source ./utils.sh

RECORD_NAME="suddenrun.com"
ROUTE_53_HOSTED_ZONE_NAME="suddenrun.com."
ALB_NAME="suddenrun-alb"
CREATE_RECORD_FILE_NAME="route53-create-record.json"
DELETE_RECORD_FILE_NAME="route53-delete-record.json"

describe_flags() {
  echo "Available flags:"
  echo "-c: create alias resource record sets in Route 53"
}

while getopts "cd" flag; do
  case ${flag} in
  c)
    lb_hosted_zone_id=$(get_load_balancer_hosted_zone_id $ALB_NAME)
    terminate_if_empty "$lb_hosted_zone_id"

    lb_dns_name=$(get_load_balancer_dns_name $ALB_NAME)
    terminate_if_empty "$lb_dns_name"

    route_53_hosted_zone_id=$(get_route_53_hosted_zone_id $ROUTE_53_HOSTED_ZONE_NAME)
    terminate_if_empty "$route_53_hosted_zone_id"

    cat >$CREATE_RECORD_FILE_NAME <<EOF
{
  "Comment": "Creating Alias resource record sets in Route 53",
  "Changes": [
    {
      "Action": "CREATE",
      "ResourceRecordSet": {
        "Name": "$RECORD_NAME",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "$lb_hosted_zone_id",
          "DNSName": "$lb_dns_name",
          "EvaluateTargetHealth": false
        }
      }
    }
  ]
}
EOF
    change_resource_record_sets "$route_53_hosted_zone_id" "$CREATE_RECORD_FILE_NAME"
    ;;
  d)
    lb_hosted_zone_id=$(get_load_balancer_hosted_zone_id $ALB_NAME)
    terminate_if_empty "$lb_hosted_zone_id"

    lb_dns_name=$(get_load_balancer_dns_name $ALB_NAME)
    terminate_if_empty "$lb_dns_name"

    route_53_hosted_zone_id=$(get_route_53_hosted_zone_id $ROUTE_53_HOSTED_ZONE_NAME)
    terminate_if_empty "$route_53_hosted_zone_id"

    cat >$DELETE_RECORD_FILE_NAME <<EOF
{
  "Comment": "Deleting Alias resource record sets in Route 53",
  "Changes": [
    {
      "Action": "DELETE",
      "ResourceRecordSet": {
        "Name": "$RECORD_NAME",
        "Type": "A",
        "AliasTarget": {
          "HostedZoneId": "$lb_hosted_zone_id",
          "DNSName": "$lb_dns_name",
          "EvaluateTargetHealth": false
        }
      }
    }
  ]
}
EOF

    change_resource_record_sets "$route_53_hosted_zone_id" "$DELETE_RECORD_FILE_NAME"
    ;;
  \?)
    describe_flags
    ;;
  esac
done
