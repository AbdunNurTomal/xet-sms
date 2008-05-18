/*
MySQL Backup
Source Host:           localhost
Source Server Version: 5.0.18-nt
Source Database:       xsms
Date:                  2006/03/26 23.02.39
*/

SET FOREIGN_KEY_CHECKS=0;
use xsms;
#----------------------------
# Table structure for config
#----------------------------
CREATE TABLE `config` (
  `id` tinyint(3) unsigned zerofill NOT NULL auto_increment,
  `jdbcurl` tinytext NOT NULL,
  `comport` varchar(5) NOT NULL,
  `baudrate` mediumint(9) NOT NULL,
  `gsmDeviceManufacturer` varchar(64) NOT NULL,
  `gsmDeviceModel` varchar(64) NOT NULL,
  `simpin` varchar(16) NOT NULL,
  `sendMessagesAllowed` tinyint(4) default NULL,
  `demonWait` int(10) unsigned NOT NULL default '10000',
  `isRunning` tinyint(4) default '1',
  `reboot` tinyint(4) default '0',
  `shutdown` tinyint(4) default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB; InnoDB free: 4096 kB; InnoDB free: 409';
#----------------------------
# Records for table config
#----------------------------


insert  into config values 
(1, 'jdbc:mysql://localhost/xsms?user=root&password=texrulez', 'COM5', 9600, 'digicom', 'pocket', '0000', 1, 10000, 0, 0, 1);
#----------------------------
# Table structure for groups
#----------------------------
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
CREATE TABLE `messages` (
  `id` smallint(5) unsigned zerofill NOT NULL auto_increment,
  `sender` bigint(13) unsigned NOT NULL default '0',
  `date` date default NULL,
  `time` time NOT NULL,
  `text` text NOT NULL,
  `status` tinyint(3) unsigned zerofill default NULL COMMENT '0=unread, 1=readOnAir, 2=NA',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB; InnoDB free: 4096 kB; InnoDB free: 409';
#----------------------------
# Records for table messages
#----------------------------


insert  into messages values 
(10, 393357239080, '2006-02-07', '22:59:36', 'Ho chiamato alle 22:59 del 07/02/06. Informazione gratuita del servizio CHIAMAMI di Vodafone.', 1), 
(11, 393338920357, '2006-02-10', '08:56:01', 'Stasera non si suona, eto lavora, xò si potrebbe andare da lui e suonare quando stacca...sentitelo e fatemi sapere...', 1), 
(12, 393338920357, '2006-02-15', '17:36:29', 'Tex che mixer hai su a pino?', 1), 
(13, 393338920357, '2006-02-19', '18:13:21', 'Tex ci becchiamo stasera? Se non 6 impegnato con  una bella topona, ovvio...', 1), 
(15, 393338920357, '2006-02-19', '18:18:46', ' x le 8 in posto risparmio? Dimmi tu...', 0), 
(16, 393338920357, '2006-02-19', '18:39:18', 'Allora dove ci troviamo?', 0), 
(17, 393338920357, '2006-02-19', '20:24:56', 'Siamo già dentro...', 0), 
(18, 393393021697, '2006-02-23', '09:21:52', 'A', 0), 
(19, 393402843972, '2006-03-14', '17:20:58', 'La prima', 0), 
(20, 393487216988, '2006-03-15', '16:22:53', 'FIBI', 0), 
(21, 393939415564, '2006-03-22', '16:22:01', 'almeno il l 580%', 0), 
(22, 393939415564, '2006-03-22', '16:22:53', 'almeno il 58 %', 0), 
(23, 393493526168, '2006-03-25', '21:55:42', 'prova di un messaggio mooooooolto mooooooooooooooooooooooooooolto lungo....talmente lungo che tenta di spaginare ma non spagine o forse si o forse no non lo so che ne so ma si ma no si no boh come sto bah bih boh buh', 1), 
(24, 393498854000, '2006-03-25', '23:51:04', 'SONO TEX.OGGI NON C É.NESSUNO...CI VEDIAMO POI IN SETTIMANA...  ', 0);
#----------------------------
# Table structure for outgoingmessages
#----------------------------
CREATE TABLE `outgoingmessages` (
  `id` int(10) unsigned zerofill NOT NULL auto_increment,
  `recipient` tinytext NOT NULL,
  `isFlash` tinyint(3) unsigned zerofill default '000',
  `text` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB; InnoDB free: 4096 kB';
#----------------------------
# No records for table outgoingmessages
#----------------------------

#----------------------------
# Table structure for sentmessages
#----------------------------
CREATE TABLE `sentmessages` (
  `id` int(10) unsigned zerofill NOT NULL auto_increment,
  `recipient` tinytext NOT NULL,
  `isFlash` tinyint(3) unsigned default NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB; InnoDB free: 4096 kB; InnoDB free: 409';
#----------------------------
# Records for table sentmessages
#----------------------------


insert  into sentmessages values 
(2, '+393493526168', 0, '2006-03-26', '22:55:47', 'Ciao sono Radio Reporter 97! Ti invito ad ascoltarmi fino a notte fonda!!!Ciao!!!'), 
(3, '+393493526168', 0, '2006-03-26', '22:59:31', 'Ciao sono Radio Reporter 97! Ti invito ad ascoltarmi fino a notte fonda!!!\r\nCiao!!!');
#----------------------------
# Table structure for users
#----------------------------
CREATE TABLE `users` (
  `id` smallint(5) unsigned zerofill NOT NULL auto_increment,
  `firstname` tinytext,
  `secondname` tinytext,
  `nickname` tinytext,
  `cellnumber` tinytext NOT NULL,
  `miscinfo` text,
  `groups` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 4096 kB; InnoDB free: 4096 kB';
#----------------------------
# Records for table users
#----------------------------


insert  into users values 
(1, 'null', 'null', 'null', '393939415564', 'null', 'null'), 
(6, 'Rosario', 'Perez', 'djRos', '+393388808518', 'xtremely cool', 'users'), 
(7, 'null', 'null', 'null', '+393498854000', 'null', 'null');

