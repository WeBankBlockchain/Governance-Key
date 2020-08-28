-- Create syntax for TABLE 'encrypt_keys_info'
drop table if exists encrypt_keys_info;
CREATE TABLE `encrypt_keys_info` (
  `pk_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key_address` varchar(255) NOT NULL,
  `key_name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  `encrypt_key` text NOT NULL,
  `parent_address` varchar(255) DEFAULT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY (`key_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'key_pwds_info'
drop table if exists key_pwds_info;
CREATE TABLE `key_pwds_info` (
  `pk_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key_address` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `key_pwd` varchar(255) NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY (`key_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- Create syntax for TABLE 'cert_keys_info'
drop table if exists cert_keys_info;
CREATE TABLE `cert_keys_info` (
  `pk_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `key_alg` varchar(8) NOT NULL,
  `key_pem` longtext NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'cert_info'
drop table if exists cert_info;
CREATE TABLE `cert_info` (
  `pk_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `subject_pub_key` longtext NOT NULL,
  `cert_content` longtext NOT NULL,
  `issuer_key_id` bigint(20) NOT NULL,
  `subject_key_id` bigint(20) NOT NULL,
  `parent_cert_id` bigint(20),
  `serial_number` varchar(255) NOT NULL,
  `issuer_org` varchar(255) NOT NULL,
  `issuer_cn` varchar(255) NOT NULL,
  `subject_org` varchar(255) NOT NULL,
  `subject_cn` varchar(255) NOT NULL,
  `is_ca_cert` int(4) NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY (`parent_cert_id`,`serial_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'cert_request_info'
drop table if exists cert_request_info;
CREATE TABLE `cert_request_info` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cert_request_content` longtext,
  `parent_cert_id` bigint(20) DEFAULT NULL,
  `subject_cn` varchar(255) DEFAULT NULL,
  `subject_key_id` bigint(20) DEFAULT NULL,
  `subject_org` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `parent_cert_userId` varchar(255) DEFAULT NULL,
  `issue` bit(1) DEFAULT b'0',
  PRIMARY KEY (`pk_id`),
  KEY `user_id` (`user_id`),
  KEY `subject_key_id` (`subject_key_id`),
  KEY `parent_cert_id` (`parent_cert_id`),
  KEY `subject_org` (`subject_org`),
  KEY `subject_cn` (`subject_cn`),
  KEY `parent_cert_userId` (`parent_cert_userId`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;