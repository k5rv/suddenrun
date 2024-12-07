#!/bin/bash
#
# Wrapper functions for AWS commands
#

terminate_if_empty() {
  local value="$1"
  if [[ "$value" == "" ]]; then
    echo "Error: value is empty"
    exit 1
  fi
}

create_vpc() {
  local vpc_name="$1"
  local cluster_name="$2"
  local region="$3"
  local cidr_block="$4"
  vpc_id=$(aws ec2 create-vpc \
    --cidr-block "$cidr_block" \
    --region "$region" \
    --tag-specification "ResourceType=vpc,Tags=[{Key=Name,Value=$vpc_name},{Key=kubernetes.io/cluster/$cluster_name,Value=owned}]" \
    --query "Vpc.{VpcId:VpcId}" \
    --output text)
  terminate_if_empty "$vpc_id"
  echo "$vpc_id"
}

enable_dns_hostnames() {
  local vpc_id="$1"
  aws ec2 modify-vpc-attribute --vpc-id "$vpc_id" --enable-dns-hostnames "{\"Value\":true}"
}

get_vpc_id() {
  local vpc_name=$1
  vpc_id=$(aws ec2 describe-vpcs --filter Name=tag:Name,Values="$vpc_name" --query "Vpcs[].VpcId" --output text)
  terminate_if_empty "$vpc_id"
  echo "$vpc_id"
}

delete_vpc() {
  local vpc_id="$1"
  aws ec2 delete-vpc --vpc-id "$vpc_id"
  printf "Deleted VPC '%s'\n" "$vpc_id"
}

create_internet_gateway() {
  igw_name="$1"
  region="$2"
  igw_id=$(aws ec2 create-internet-gateway \
    --region "$region" \
    --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$igw_name}]" \
    --query "InternetGateway.{InternetGatewayId:InternetGatewayId}" \
    --output text)
  terminate_if_empty "$igw_id"
  echo "$igw_id"
}

get_internet_gateway_id() {
  local project="$1"
  igw_id=$(aws ec2 describe-internet-gateways \
    --filter "Name=tag:Name,Values=$project" \
    --query "InternetGateways[].InternetGatewayId" \
    --output text)
  echo "$igw_id"
}

delete_internet_gateway() {
  local igw_id="$1"
  aws ec2 delete-internet-gateway --internet-gateway-id "$igw_id"
  printf "Deleted Internet Gateway '%s'\n" "$igw_id"
}

attach_internet_gateway() {
  local vpc_id="$1"
  local igw_id="$2"
  aws ec2 attach-internet-gateway --internet-gateway-id "$igw_id" --vpc-id "$vpc_id"
}

detach_internet_gateway() {
  local vpc_id="$1"
  local igw_id="$2"
  aws ec2 detach-internet-gateway --internet-gateway-id "$igw_id" --vpc-id "$vpc_id"
}

create_eip() {
  local eip_name="$1"
  local network_border_group="$2"
  eip_id=$(aws ec2 allocate-address \
    --network-border-group "$network_border_group" \
    --tag-specifications "ResourceType=elastic-ip,Tags=[{Key=Name,Value=$eip_name}]" \
    --query '{AllocationId:AllocationId}' \
    --output text)
  terminate_if_empty "$eip_id"
  echo "$eip_id"
}

get_eip_id() {
  local eip_name="$1"
  eip_id=$(aws ec2 describe-addresses \
    --filter "Name=tag:Name,Values=$eip_name" \
    --query Addresses[].AllocationId \
    --output text)
  echo "$eip_id"
}

delete_eip() {
  local eip_id=$1
  aws ec2 release-address --allocation-id "$eip_id"
  printf "Deleted Elastic IP '%s'\n" "$eip_id"
}

create_subnet() {
  subnet_name="$1"
  cidr="$2"
  availability_zone="$3"
  vpc_id="$4"
  cluster_name="$5"
  elb_tag="$6"
  subnet_id=$(aws ec2 create-subnet \
    --vpc-id "$vpc_id" \
    --cidr-block "$cidr" \
    --availability-zone "$availability_zone" \
    --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$subnet_name}, $elb_tag, {Key=kubernetes.io/cluster/$cluster_name,Value=owned}]" \
    --query "Subnet.{SubnetId:SubnetId}" \
    --output text)
  terminate_if_empty "$subnet_id"
  echo "$subnet_id"
}

get_subnet_id() {
  local subnet_name="$1"
  subnet_id=$(aws ec2 describe-subnets \
    --filter "Name=tag:Name,Values=$subnet_name" \
    --query 'Subnets[].SubnetId' \
    --output text)
  echo "$subnet_id"
}

delete_subnet() {
  local subnet_id="$1"
  aws ec2 delete-subnet --subnet-id="$subnet_id"
  printf "Deleted Subnet '%s'\n" "$subnet_id"
}

enable_public_ipv4_address_auto_assign() {
  local subnet_id="$1"
  aws ec2 modify-subnet-attribute --subnet-id "$subnet_id" --map-public-ip-on-launch "{\"Value\":true}"
}

enable_resource_name_dns_a_record() {
  local subnet_id="$1"
  aws ec2 modify-subnet-attribute --subnet-id "$subnet_id" \
    --enable-resource-name-dns-a-record-on-launch "{\"Value\":true}"
}

create_route_table() {
  local route_table_name="$1"
  local vpc_id="$2"
  route_table_id=$(aws ec2 create-route-table --vpc-id "$vpc_id" \
    --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=$route_table_name}]" \
    --query "RouteTable.{RouteTableId:RouteTableId}" \
    --output text)
  terminate_if_empty "$route_table_id"
  echo "$route_table_id"
}

get_route_table_id() {
  local vpc_id="$1"
  local route_table_name="$2"
  route_table_id=$(aws ec2 describe-route-tables \
    --filter "Name=vpc-id,Values=$vpc_id" "Name=tag:Name,Values=$route_table_name" \
    --query "RouteTables[].RouteTableId" \
    --output text)
  echo "$route_table_id"
}

delete_route_table() {
  local route_table_id="$1"
  aws ec2 delete-route-table --route-table-id "$route_table_id"
  printf "Deleted Route Table '%s'\n" "$route_table_id"
}

create_internet_gateway_route() {
  local route_table_id="$1"
  local igw_id="$2"
  local destination_cidr_block="$3"
  aws ec2 create-route --route-table-id "$route_table_id" \
    --gateway-id "$igw_id" \
    --destination-cidr-block "$destination_cidr_block" >/dev/null
}

create_natgateway_route() {
  local route_table_id="$1"
  local ngw_id="$2"
  local destination_cidr_block="$3"
  aws ec2 create-route --route-table-id "$route_table_id" \
    --nat-gateway-id "$ngw_id" \
    --destination-cidr-block "$destination_cidr_block" >/dev/null
}

delete_route() {
  local route_table_id="$1"
  local destination_cidr_block="$2"
  aws ec2 delete-route --route-table-id "$route_table_id" --destination-cidr-block "$destination_cidr_block"
}

create_route_table_association() {
  local route_table_id="$1"
  local subnet_id="$2"
  association_id=$(aws ec2 associate-route-table \
    --route-table-id "$route_table_id" \
    --subnet-id "$subnet_id" \
    --query "{AssociationId:AssociationId}" \
    --output text)
  echo "$association_id"
}

get_route_table_association_id() {
  local route_table_id="$1"
  local subnet_id="$2"
  association_id=$(aws ec2 describe-route-tables \
    --filter "Name=association.route-table-id,Values=$route_table_id" "Name=association.subnet-id,Values=$subnet_id" \
    --query "RouteTables[].Associations[?SubnetId=='$subnet_id'].RouteTableAssociationId" \
    --output text)
  echo "$association_id"
}

delete_route_table_association() {
  local route_table_association_id="$1"
  aws ec2 disassociate-route-table --association-id "$route_table_association_id"
}

poll_natgateway_state() {
  local natgateway_id="$1"
  local expected="$2"
  local actual
  actual=$(get_natgateway_state "$natgateway_id")
  if [[ "$actual" != "$expected" ]]; then
    printf "Waiting for NAT Gateway '%s' to become '%s' current state is '%s'\n" "$natgateway_id" "$expected" "$actual"
    while [ "$actual" != "$expected" ]; do
      printf "%s\n" "..."
      sleep 5
      actual=$(get_natgateway_state "$natgateway_id")
    done
  fi
}

create_natgateway() {
  local natgateway_name="$1"
  local subnet_id="$2"
  local eip_id="$3"
  natgateway_id=$(aws ec2 create-nat-gateway \
    --connectivity-type "public" \
    --subnet-id "$subnet_id" \
    --allocation-id "$eip_id" \
    --tag-specifications "ResourceType=natgateway,Tags=[{Key=Name,Value=$natgateway_name}]" \
    --query "NatGateway.{NatGatewayId:NatGatewayId}" \
    --output text)
  terminate_if_empty "$natgateway_id"
  echo "$natgateway_id"
}

get_natgateway_id() {
  local natgateway_name="$1"
  natgateway_id=$(aws ec2 describe-nat-gateways \
    --filter "Name=tag:Name,Values=$natgateway_name" "Name=state,Values=available,pending" \
    --query "NatGateways[].NatGatewayId" \
    --output text)
  echo "$natgateway_id"
}

get_natgateway_state() {
  local natgateway_id="$1"
  natgateway_state=$(aws ec2 describe-nat-gateways \
    --filter "Name=nat-gateway-id,Values=$natgateway_id" \
    --query "NatGateways[].State" \
    --output text)
  echo "$natgateway_state"
}

delete_natgateway() {
  local natgateway_id="$1"
  aws ec2 delete-nat-gateway --nat-gateway-id "$natgateway_id" >/dev/null
  printf "Initiated deletion of Nat Gateway '%s'\n" "$natgateway_id"
}

create_security_group() {
  local sg_name="$1"
  local vpc_id="$2"
  sg_id=$(aws ec2 create-security-group --group-name "$sg_name" \
    --description "cluster security group" \
    --vpc-id "$vpc_id" \
    --tag-specifications "ResourceType=security-group,Tags=[{Key=Name,Value=$sg_name}, {Key=kubernetes.io/cluster/$cluster_name,Value=owned}]" \
    --query "{GroupId:GroupId}" \
    --output text)
  terminate_if_empty "$sg_id"
  echo "$sg_id"
}

get_security_group_id() {
  local sg_name="$1"
  sg_id=$(aws ec2 describe-security-groups \
    --filter "Name=tag:Name,Values=$sg_name" \
    --query "SecurityGroups[].GroupId" \
    --output text)
  echo "$sg_id"
}

delete_security_group() {
  local sg_id="$1"
  aws ec2 delete-security-group --group-id "$sg_id" >/dev/null
  printf "Deleted Security Group '%s'\n" "$sg_id"
}

create_rule() {
  local sg_id="$1"
  local protocol="$2"
  local port="$3"
  local cidr="$4"
  aws ec2 authorize-security-group-ingress --group-id "$sg_id" \
    --protocol "$protocol" \
    --port "$port" \
    --cidr "$cidr" >/dev/null
}

create_db_subnet_group() {
  local name="$1"
  local description="$2"
  local subnet_a_id="$3"
  local subnet_b_id="$4"
  subnet_group=$(aws rds create-db-subnet-group \
    --db-subnet-group-name "$name" \
    --db-subnet-group-description "$description" \
    --subnet-ids "[\"$subnet_a_id\",\"$subnet_b_id\"]")
  printf "Created database subnet group '%s'\n" "$name"
}

get_db_subnet_group() {
  local subnet_group_name="$1"
  subnet_group=$(aws rds describe-db-subnet-groups --db-subnet-group-name "$subnet_group_name")
  echo "$subnet_group"
}

get_db_instance_status() {
  local identifier="$1"
  status=$(aws rds describe-db-instances \
    --db-instance-identifier "$identifier" \
    --query "DBInstances[?DBInstanceIdentifier=='$identifier'].{DBInstanceStatus:DBInstanceStatus}" \
    --output text)
  echo "$status"
}

delete_db_subnet_group() {
  local subnet_group_name="$1"
  subnet_group=$(aws rds delete-db-subnet-group --db-subnet-group-name "$subnet_group_name")
  printf "Deleted database subnet group '%s'\n" "$subnet_group_name"
}

poll_db_instance_status() {
  local identifier="$1"
  local expected="$2"
  local actual
  actual=$(get_db_instance_status "$identifier")
  if [[ "$actual" != "$expected" ]]; then
    printf "Waiting for DB instance '%s' to become '%s' current status is '%s'\n" "$identifier" "$expected" "$actual"
    while [ "$actual" != "$expected" ]; do
      if [[ "$actual" == "" ]]; then
        return
      fi
      printf "%s\n" "..."
      sleep 10
      actual=$(get_db_instance_status "$identifier")
    done
  fi
}

delete_db_instance() {
  local identifier="$1"
  db_instance=$(aws rds delete-db-instance --db-instance-identifier "$identifier" --skip-final-snapshot)
  printf "Deleted DB instance '%s'\n" "$identifier"
}

create_cluster() {
  local name="$1"
  local config="$2"
  printf "Creating cluster '%s' with configuration file '%s'\n" "$name" "$config"
  cat "$config"
  eksctl create cluster --config-file "$config"
}

delete_cluster() {
  local name="$1"
  printf "Deleting cluster '%s'\n" "$name"
  eksctl delete cluster --name="$name"
}

create_oidc_provider() {
  local region="$1"
  local cluster_name="$2"
  printf "Creating oidc provider for cluster '%s' in region '%s'\n" "$cluster_name" "$region"
  eksctl utils associate-iam-oidc-provider --region="$region" --cluster="$cluster_name" --approve
}

get_oidc_id() {
  local cluster_name="$1"
  printf "Looking for oidc provider id for cluster '%s'\n" "$cluster_name"
  oidc_id=$(aws eks describe-cluster --name "$cluster_name" --query "cluster.identity.oidc.issuer" --output text | cut -d '/' -f 5)
  echo "$oidc_id"
}

get_iam_policy() {
  local aws_account_id="$1"
  local aws_policy_name="$2"
  iam_policy=$(aws iam get-policy --policy-arn arn:aws:iam::"$aws_account_id":policy/"$aws_policy_name")
  echo "$iam_policy"
}

download_aws_load_balancer_controller_iam_policy() {
  echo "Downloading AWS Load Balancer Controller policy"
  curl -O https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.4.7/docs/install/iam_policy.json
}

create_iam_policy() {
  local policy_name="$1"
  printf "Creating policy '%s'\n" "$policy_name"
  aws iam create-policy --policy-name "$policy_name" --policy-document file://iam_policy.json
}

get_iam_role() {
  local name="$1"
  iam_role=$(aws iam get-role --role-name "$name")
  echo "$iam_role"
}

create_aws_load_balancer_controller_service_account() {
  local aws_account_id="$1"
  local cluster_name="$2"
  local role_name="$3"
  local policy_name="$4"
  printf "Creating aws-load-balancer-controller service account with role '%s'\n" "$role_name"
  eksctl create iamserviceaccount \
    --cluster="$cluster_name" \
    --namespace=kube-system \
    --name=aws-load-balancer-controller \
    --role-name "$role_name" \
    --attach-policy-arn=arn:aws:iam::"$aws_account_id":policy/"$policy_name" \
    --approve
}

install_aws_load_balancer_controller() {
  local service_account_name="$1"
  local cluster_name="$2"
  echo "Installing AWS Load Balancer Controller"
  helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
    -n kube-system \
    --set clusterName="$cluster_name" \
    --set serviceAccount.create=false \
    --set serviceAccount.name="$service_account_name"
}

get_target_group_alb_arn() {
  alb_arn=$(aws elbv2 describe-target-groups --query "TargetGroups[].LoadBalancerArns[]" \
    --output text)
  echo "$alb_arn"
}

get_target_group_arn() {
  local target_group_name="$1"
  target_group_arn=$(aws elbv2 describe-target-groups --names "$target_group_name" \
    --query "TargetGroups[].TargetGroupArn" \
    --output text)
  echo "$target_group_arn"
}

delete_target_group() {
  local target_group_arn="$1"
  aws elbv2 delete-target-group --target-group-arn "$target_group_arn"
  printf "Deleted Target Group '%s'\n" "$target_group_arn"
}

create_nlb() {
  local nlb_name="$1"
  local subnet_a_id="$2"
  local subnet_b_id="$3"
  nlb_arn=$(aws elbv2 create-load-balancer \
    --name "$nlb_name" \
    --scheme internet-facing \
    --subnets "$subnet_a_id" "$subnet_b_id" \
    --tags "[{\"Key\":\"Name\",\"Value\":\"$nlb_name\"}]" \
    --type "network" \
    --query "LoadBalancers[].LoadBalancerArn" \
    --output text)
  echo "$nlb_arn"
}

get_nlb_arn() {
  local nlb_name="$1"
  nlb_arn=$(aws elb describe-load-balancers \
    --load-balancer-names "$nlb_name" \
    --query "LoadBalancers[].LoadBalancerArn" \
    --output text)
  echo "$nlb_arn"
}

get_elbv2_arn() {
  local elb_name="$1"
  nlb_arn=$(aws elbv2 describe-load-balancers \
    --query "LoadBalancers[?LoadBalancerName=='$elb_name'].LoadBalancerArn" \
    --output text)
  echo "$nlb_arn"
}

get_listener_arn() {
  local alb_arn="$1"
  alb_arn=$(aws elbv2 describe-listeners \
    --load-balancer-arn "$alb_arn" \
    --query "Listeners[].ListenerArn" \
    --output text)
  echo "$alb_arn"
}

delete_listener() {
  local listener_arn="$1"
  aws elbv2 delete-listener --listener-arn "$listener_arn"
  printf "Deleted Listener '%s'\n" "$listener_arn"
}

create_lb_listener() {
  local nlb_listener_name="$1"
  local target_group_arn="$2"
  local protocol="$3"
  local port="$4"
  nlb_listener_arn=$(aws elbv2 create-listener \
    --load-balancer-arn "$nlb_arn" \
    --protocol "$protocol" \
    --port "$port" \
    --default-actions Type=forward,TargetGroupArn="$target_group_arn" \
    --tags "[{\"Key\":\"Name\",\"Value\":\"$nlb_listener_name\"}]" \
    --query "Listeners[].ListenerArn" \
    --output text)
  echo "$nlb_listener_arn"
}

delete_elbv2() {
  local elb_arn="$1"
  aws elbv2 delete-load-balancer --load-balancer-arn "$elb_arn"
  printf "Deleted Load Balancer '%s'\n" "$elb_arn"
}

get_elbv2_state() {
  local elb_arn="$1"
  state=$(aws elbv2 describe-load-balancers \
    --load-balancer-arns "$elb_arn" \
    --query "LoadBalancers[].State" --output text)
  echo "$state"
}

get_load_balancer_hosted_zone_id() {
  local nlb_name="$1"
  hosted_zone_id=$(aws elbv2 describe-load-balancers \
    --names "$nlb_name" \
    --query "LoadBalancers[].CanonicalHostedZoneId" \
    --output text)
  echo "$hosted_zone_id"
}

get_route_53_hosted_zone_id() {
  local hosted_zone_name="$1"
  hosted_zone_id=$(aws route53 list-hosted-zones-by-name \
    --dns-name "$hosted_zone_name" \
    --query "HostedZones[].Id" \
    --output text)
  echo "$hosted_zone_id"
}

get_load_balancer_dns_name() {
  local lb_name="$1"
  dns_name=$(aws elbv2 describe-load-balancers \
    --names "$lb_name" \
    --query "LoadBalancers[].DNSName" \
    --output text)
  echo "$dns_name"
}

poll_elb_instance_state() {
  local elb_arn="$1"
  local expected="$2"
  local actual
  actual=$(get_elbv2_state "$elb_arn")
  if [[ "$actual" != "$expected" ]]; then
    printf "Waiting for Load Balancer '%s' to become '%s' current state is '%s'\n" "$elb_arn" "$expected" "$actual"
    while [ "$actual" != "$expected" ]; do
      printf "%s\n" "..."
      sleep 5
      actual=$(get_elbv2_state "$elb_arn")
    done
  fi
}

poll_elb_creation() {
  local actual
  actual=$(get_target_group_alb_arn)
  if [[ "$actual" == "" ]]; then
    echo "Waiting for Load Balancer to become available"
    while [ "$actual" == "" ]; do
      printf "%s\n" "..."
      sleep 5
      actual=$(get_target_group_alb_arn)
    done
  fi
}

change_resource_record_sets() {
  local hosted_zone_id="$1"
  local file_name="$2"
  aws route53 change-resource-record-sets --hosted-zone-id "$hosted_zone_id" --change-batch file://"$file_name"
}
