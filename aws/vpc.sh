#!/bin/bash
#
# Creates AWS resources: VPC, internet gateway, public and private subnets, natgateway, route tables, security group
#
source ./utils.sh

REGION="eu-north-1"
CLUSTER_NAME="suddenrun"
VPC_NAME="suddenrun"
VPC_CIDR_BLOCK="10.0.0.0/16"
INTERNET_GATEWAY_NAME="suddenrun"
NATGATEWAY_A_NAME="public-eu-north-1a"
NATGATEWAY_B_NAME="public-eu-north-1b"
AVAILABILITY_ZONE_A="eu-north-1a"
AVAILABILITY_ZONE_B="eu-north-1b"
SUBNET_PUBLIC_A_NAME="public-eu-north-1a"
SUBNET_PUBLIC_B_NAME="public-eu-north-1b"
SUBNET_PRIVATE_A_NAME="private-eu-north-1a"
SUBNET_PRIVATE_B_NAME="private-eu-north-1b"
SUBNET_PUBLIC_A_CIDR="10.0.1.0/24"
SUBNET_PUBLIC_B_CIDR="10.0.2.0/24"
SUBNET_PRIVATE_A_CIDR="10.0.3.0/24"
SUBNET_PRIVATE_B_CIDR="10.0.4.0/24"
SUBNET_PUBLIC_ELB_TAG="{Key=kubernetes.io/role/elb,Value=1}"
SUBNET_PRIVATE_ELB_TAG="{Key=kubernetes.io/role/internal-elb,Value=1}"
ROUTE_TABLE_PUBLIC_NAME="public-eu-north-1"
ROUTE_TABLE_PRIVATE_A_NAME="private-eu-north-1a"
ROUTE_TABLE_PRIVATE_B_NAME="private-eu-north-1b"
EIP_A_NAME="eu-north-1a"
EIP_B_NAME="eu-north-1b"
EIP_NETWORK_BORDER_GROUP="eu-north-1"
SECURITY_GROUP_NAME="suddenrun"

describe_flags() {
  echo "Available flags:"
  echo "-d: delete VPC and its dependencies"
  echo "-c: create VPC and its dependencies"
}

while getopts "cd" flag; do
  case "${flag}" in
  c)
    # Create VPC
    vpc_id=$(create_vpc "$VPC_NAME" "$CLUSTER_NAME" "$REGION" $VPC_CIDR_BLOCK)
    printf "Created VPC '%s'\n" "$vpc_id"

    # Enable VPC DNS hostname
    enable_dns_hostnames "$vpc_id"

    # Create IGW
    igw_id=$(create_internet_gateway "$INTERNET_GATEWAY_NAME" "$REGION")
    printf "Created Internet Gateway '%s'\n" "$igw_id"

    # Attach IGW to VPC
    attach_internet_gateway "$vpc_id" "$igw_id"

    # Create public subnet A
    subnet_public_a_id=$(create_subnet "$SUBNET_PUBLIC_A_NAME" "$SUBNET_PUBLIC_A_CIDR" "$AVAILABILITY_ZONE_A" "$vpc_id" "$CLUSTER_NAME" "$SUBNET_PUBLIC_ELB_TAG")
    printf "Created Subnet '%s'\n" "$subnet_public_a_id"

    # Create public subnet B
    subnet_public_b_id=$(create_subnet "$SUBNET_PUBLIC_B_NAME" "$SUBNET_PUBLIC_B_CIDR" "$AVAILABILITY_ZONE_B" "$vpc_id" "$CLUSTER_NAME" "$SUBNET_PUBLIC_ELB_TAG")
    printf "Created Subnet '%s'\n" "$subnet_public_b_id"

    # Create private subnet A
    subnet_private_a_id=$(create_subnet "$SUBNET_PRIVATE_A_NAME" "$SUBNET_PRIVATE_A_CIDR" "$AVAILABILITY_ZONE_A" "$vpc_id" "$CLUSTER_NAME" "$SUBNET_PRIVATE_ELB_TAG")
    printf "Created Subnet '%s'\n" "$subnet_private_a_id"

    # Create private subnet B
    subnet_private_b_id=$(create_subnet "$SUBNET_PRIVATE_B_NAME" "$SUBNET_PRIVATE_B_CIDR" "$AVAILABILITY_ZONE_B" "$vpc_id" "$CLUSTER_NAME" "$SUBNET_PRIVATE_ELB_TAG")
    printf "Created Subnet '%s'\n" "$subnet_private_b_id"

    # Enable public subnet A IPv4 auto-assigning
    enable_public_ipv4_address_auto_assign "$subnet_public_a_id"

    # Enable public subnet B IPv4 auto-assigning
    enable_public_ipv4_address_auto_assign "$subnet_public_b_id"

    # Create route table public
    route_table_public_id=$(create_route_table $ROUTE_TABLE_PUBLIC_NAME "$vpc_id")
    printf "Created Route Table '%s'\n" "$route_table_public_id"

    # Create route from any IP address to IGW
    create_internet_gateway_route "$route_table_public_id" "$igw_id" "0.0.0.0/0"

    # Create association between public route table and subnet A
    route_association_public_a_id=$(create_route_table_association "$route_table_public_id" "$subnet_public_a_id")

    # Create association between public route table and subnet B
    route_association_public_b_id=$(create_route_table_association "$route_table_public_id" "$subnet_public_b_id")

    # Allocate EIP A address
    eip_a_id=$(create_eip $EIP_A_NAME $EIP_NETWORK_BORDER_GROUP)
    printf "Created Elastic IP '%s'\n" "$eip_a_id"

    # Allocate EIP B address
    eip_b_id=$(create_eip $EIP_B_NAME $EIP_NETWORK_BORDER_GROUP)
    printf "Created Elastic IP '%s'\n" "$eip_b_id"

    # Create NGW A
    ngw_a_id=$(create_natgateway "$NATGATEWAY_A_NAME" "$subnet_public_a_id" "$eip_a_id")
    printf "Initiated creation of NAT Gateway '%s'\n" "$ngw_a_id"

    # Create NGW B
    ngw_b_id=$(create_natgateway "$NATGATEWAY_B_NAME" "$subnet_public_b_id" "$eip_b_id")
    printf "Initiated creation of NAT Gateway '%s'\n" "$ngw_b_id"

    # Waiting for NGW A
    poll_natgateway_state "$ngw_a_id" "available"

    # Waiting for NGW B
    poll_natgateway_state "$ngw_b_id" "available"

    # Create route table private A
    route_table_private_a_id=$(create_route_table $ROUTE_TABLE_PRIVATE_A_NAME "$vpc_id")
    printf "Created Route Table '%s'\n" "$route_table_private_a_id"

    # Create route from any IP address to NGW in route table A
    create_natgateway_route "$route_table_private_a_id" "$ngw_a_id" "0.0.0.0/0"

    # Create association between private route table A and private subnet A
    route_association_private_a_id=$(create_route_table_association "$route_table_private_a_id" "$subnet_private_a_id")

    # Create route table private B
    route_table_private_b_id=$(create_route_table $ROUTE_TABLE_PRIVATE_B_NAME "$vpc_id")
    printf "Created Route Table '%s'\n" "$route_table_private_b_id"

    # Create route from any IP address to NGW in route table B
    create_natgateway_route "$route_table_private_b_id" "$ngw_b_id" "0.0.0.0/0"

    # Create association between private route table B and private subnet B
    route_association_private_b_id=$(create_route_table_association "$route_table_private_b_id" "$subnet_private_b_id")

    # Create security group
    sg_id=$(create_security_group "$SECURITY_GROUP_NAME" "$vpc_id")
    printf "Created Security Group '%s'\n" "$sg_id"

    # Create security group rules
    create_rule "$sg_id" "tcp" "80" "0.0.0.0/0"
    create_rule "$sg_id" "tcp" "443" "0.0.0.0/0"
    create_rule "$sg_id" "tcp" "22" "0.0.0.0/0"
    create_rule "$sg_id" "tcp" "5432" "0.0.0.0/0"
    create_rule "$sg_id" "icmp" "all" "0.0.0.0/0"
    ;;

  d)
    # Find VPC
    vpc_id=$(get_vpc_id "$VPC_NAME")

    # Find IGW
    igw_id=$(get_internet_gateway_id "$INTERNET_GATEWAY_NAME")

    # Find public route table
    route_table_public_id=$(get_route_table_id "$vpc_id" $ROUTE_TABLE_PUBLIC_NAME)

    # Delete route to IGW
    delete_route "$route_table_public_id" "0.0.0.0/0"

    # Find public subnet A
    subnet_public_a_id=$(get_subnet_id $SUBNET_PUBLIC_A_NAME)

    # Find public subnet A and public route table association
    route_association_public_a_id=$(get_route_table_association_id "$route_table_public_id" "$subnet_public_a_id")

    # Delete subnet a and public route table association
    delete_route_table_association "$route_association_public_a_id"

    # Find public subnet B
    subnet_public_b_id=$(get_subnet_id $SUBNET_PUBLIC_B_NAME)

    # Find public subnet B and public route table association
    route_association_public_b_id=$(get_route_table_association_id "$route_table_public_id" "$subnet_public_b_id")

    # Delete subnet B and public route table association
    delete_route_table_association "$route_association_public_b_id"

    # Delete public route table
    delete_route_table "$route_table_public_id"

    # Find NGW A
    ngw_a_id=$(get_natgateway_id "$NATGATEWAY_A_NAME")

    # Find NGW B
    ngw_b_id=$(get_natgateway_id "$NATGATEWAY_B_NAME")

    # Find private route table A
    route_table_private_a_id=$(get_route_table_id "$vpc_id" $ROUTE_TABLE_PRIVATE_A_NAME)

    # Delete route to NGW for route table private A
    delete_route "$route_table_private_a_id" "0.0.0.0/0"

    # Find private subnet A
    subnet_private_a_id=$(get_subnet_id $SUBNET_PRIVATE_A_NAME)

    # Find private subnet A and private route A table association
    route_association_private_a_id=$(get_route_table_association_id "$route_table_private_a_id" "$subnet_private_a_id")

    # Delete private subnet A and private route A table association
    delete_route_table_association "$route_association_private_a_id"

    # Delete private route A table
    delete_route_table "$route_table_private_a_id"

    # Find private route table B
    route_table_private_b_id=$(get_route_table_id "$vpc_id" $ROUTE_TABLE_PRIVATE_B_NAME)

    # Delete route to NGW for route table private B
    delete_route "$route_table_private_b_id" "0.0.0.0/0"

    # Find private subnet B
    subnet_private_b_id=$(get_subnet_id $SUBNET_PRIVATE_B_NAME)

    # Find private subnet B and private route B table association
    route_association_private_b_id=$(get_route_table_association_id "$route_table_private_b_id" "$subnet_private_b_id")

    # Delete private subnet B and private route B table association
    delete_route_table_association "$route_association_private_b_id"

    # Delete private route B table
    delete_route_table "$route_table_private_b_id"

    # Delete NGW A
    delete_natgateway "$ngw_a_id"

    # Delete NGW B
    delete_natgateway "$ngw_b_id"

    # Waiting for NGW A
    poll_natgateway_state "$ngw_a_id" "deleted"

    # Waiting for NGW B
    poll_natgateway_state "$ngw_b_id" "deleted"

    # Delete public subnet A
    delete_subnet "$subnet_public_a_id"

    # Delete public subnet B
    delete_subnet "$subnet_public_b_id"

    # Delete private subnet A
    delete_subnet "$subnet_private_a_id"

    # Delete private subnet B
    delete_subnet "$subnet_private_b_id"

    # Detach IGW
    detach_internet_gateway "$vpc_id" "$igw_id"

    # Delete IGW
    delete_internet_gateway "$igw_id"

    # Find security group
    sg_id=$(get_security_group_id "$SECURITY_GROUP_NAME")

    # Delete security group
    delete_security_group "$sg_id"

    # Delete VPC
    delete_vpc "$vpc_id"

    # Find EIP A
    eip_a_id=$(get_eip_id $EIP_A_NAME)

    # Release EIP A
    delete_eip "$eip_a_id"

    # Find EIP B
    eip_b_id=$(get_eip_id $EIP_B_NAME)

    # Release EIP B
    delete_eip "$eip_b_id"
    ;;
  \?)
    describe_flags
    ;;
  esac
done
