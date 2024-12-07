#!/bin/bash
#
# Creates AWS RDS
#
source ./utils.sh

DB_NAME="suddenrun"
DB_INSTANCE_IDENTIFIER="suddenrun"
ENGINE="postgres"
ENGINE_VERSION="15.2"
MASTER_USER_NAME="suddenrun"
MASTER_USER_PASSWORD="suddenrun"
SECURITY_GROUP_NAME="suddenrun"
SUBNET_PRIVATE_A_NAME="private-eu-north-1a"
SUBNET_PRIVATE_B_NAME="private-eu-north-1b"
DB_SUBNET_GROUP_NAME="suddenrunsubnetgroup"
DB_INSTANCE_CLASS="db.t3.micro"
STORAGE_TYPE="gp2"
ALLOCATED_STORAGE=10
BACKUP_RETENTION_PERIOD=0

describe_flags() {
  echo "Available flags:"
  echo "-c: create RDS"
}

while getopts "cd" flag; do
  case ${flag} in
  c)
    echo "Creating RDS instance $DB_INSTANCE_IDENTIFIER $ENGINE $ENGINE_VERSION"
    subnet_group=$(get_db_subnet_group $DB_SUBNET_GROUP_NAME)
    if [[ "$subnet_group" == "" ]]; then
      subnet_private_a_id=$(get_subnet_id "$SUBNET_PRIVATE_A_NAME")
      subnet_private_b_id=$(get_subnet_id "$SUBNET_PRIVATE_B_NAME")
      create_db_subnet_group "$DB_SUBNET_GROUP_NAME" "$DB_INSTANCE_IDENTIFIER db instance subnet group" "$subnet_private_a_id" "$subnet_private_b_id"
    fi

    sg_id=$(get_security_group_id $SECURITY_GROUP_NAME)
    terminate_if_empty "$sg_id"

    db_instance=$(aws rds create-db-instance \
      --db-name $DB_NAME \
      --db-instance-identifier $DB_INSTANCE_IDENTIFIER \
      --master-username $MASTER_USER_NAME \
      --master-user-password $MASTER_USER_PASSWORD \
      --engine $ENGINE \
      --engine-version $ENGINE_VERSION \
      --vpc-security-group-ids "$sg_id" \
      --no-publicly-accessible \
      --db-subnet-group-name $DB_SUBNET_GROUP_NAME \
      --db-instance-class $DB_INSTANCE_CLASS \
      --storage-type $STORAGE_TYPE \
      --allocated-storage $ALLOCATED_STORAGE \
      --no-multi-az \
      --backup-retention-period $BACKUP_RETENTION_PERIOD \
      --no-auto-minor-version-upgrade \
      --no-enable-performance-insights \
      --tags "[{\"Key\":\"Name\",\"Value\":\"$DB_SUBNET_GROUP_NAME\"}]")

    echo "$db_instance"
    poll_db_instance_status $DB_INSTANCE_IDENTIFIER "available"

    ;;
  d)
    delete_db_instance $DB_INSTANCE_IDENTIFIER
    poll_db_instance_status $DB_INSTANCE_IDENTIFIER "deleted"
    delete_db_subnet_group $DB_SUBNET_GROUP_NAME
    ;;
  \?)
    describe_flags
    ;;
  esac
done
