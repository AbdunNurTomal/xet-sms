/*
MySQL Backup
Source Host:           localhost
Source Server Version: 4.1.14-nt
Source Database:       xsms
Date:                  2006/02/16 10.38.30
*/

SET FOREIGN_KEY_CHECKS=0;
set character_set_client=utf8;
set character_set_connection=utf8;
use xsms;
#----------------------------
# Table structure for groups
#----------------------------
drop table if exists groups;
CREATE TABLE `groups` (
  `id` smallint(5) unsigned zerofill NOT NULL auto_increment,
  `name` tinytext NOT NULL,
  `description` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB';
#----------------------------
# No records for table groups
#----------------------------

#----------------------------
# Table structure for messages
#----------------------------
drop table if exists messages;
CREATE TABLE `messages` (
  `id` smallint(5) unsigned zerofill NOT NULL auto_increment,
  `sender` bigint(13) unsigned NOT NULL default '0',
  `date` date NOT NULL default '0000-00-00',
  `text` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB; InnoDB free: 4096 kB; InnoDB free: 409';
#----------------------------
# No records for table messages
#----------------------------

#----------------------------
# Table structure for users
#----------------------------
drop table if exists users;
CREATE TABLE `users` (
  `id` bigint(20) unsigned zerofill NOT NULL auto_increment,
  `firstname` tinytext,
  `secondname` tinytext,
  `nickname` tinytext,
  `cellnumber` bigint(13) unsigned NOT NULL default '0',
  `miscinfo` text,
  `groups` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
#----------------------------
# Records for table users
#----------------------------


insert  into users values (00000000000000000001, 0x456D616E75656C65, 0x546573736F7265, 0x586574, 3493526168, 0x787472656D656C7920636F6F6C, 0x7573657273) ;

