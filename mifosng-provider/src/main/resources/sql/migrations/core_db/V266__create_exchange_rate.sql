


-- -----------------------------------------------------
-- Table `m_exchange_rate`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `m_exchange_rate`;
CREATE TABLE IF NOT EXISTS `m_exchange_rate` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `currency` VARCHAR(3) NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `date` DATE NOT NULL,
  `amount` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci;

INSERT INTO  `m_permission` (
`grouping` ,
`code` ,
`entity_name` ,
`action_name` ,
`can_maker_checker`
)
VALUES (
 'organisation',  'READ_EXCHANGERATE',  'EXCHANGERATE',  'READ',  '0'
);

INSERT INTO  `m_permission` (
`grouping` ,
`code` ,
`entity_name` ,
`action_name` ,
`can_maker_checker`
)
VALUES (
 'organisation',  'CREATE_EXCHANGERATE',  'EXCHANGERATE',  'CREATE',  '0'
);


INSERT INTO  `m_permission` (
`grouping` ,
`code` ,
`entity_name` ,
`action_name` ,
`can_maker_checker`
)
VALUES (
 'organisation',  'UPDATE_EXCHANGERATE',  'EXCHANGERATE',  'UPDATE',  '0'
);

INSERT INTO  `m_permission` (
`grouping` ,
`code` ,
`entity_name` ,
`action_name` ,
`can_maker_checker`
)
VALUES (
 'organisation',  'UPDATE_EXCHANGERATE_CHECKER',  'EXCHANGERATE',  'UPDATE_CHECKER',  '0'
);



