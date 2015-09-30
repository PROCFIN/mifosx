
ALTER TABLE `m_organisation_currency` ADD KEY (`code`);

DROP TABLE IF EXISTS `m_forex_exchange`;
CREATE TABLE IF NOT EXISTS `m_forex_exchange` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `exchange_rate_id` BIGINT(20) NOT NULL,
  `createdby_id` BIGINT(20) NOT NULL,
  `currency_from_code` varchar(3) NOT NULL,
  `currency_to_code` varchar(3) NOT NULL,
  `client_id` VARCHAR(50) NULL DEFAULT NULL,
  `client_name` VARCHAR(50) NULL DEFAULT NULL,
  `transaction_date` DATETIME NULL DEFAULT NULL,
  `created_date` DATETIME NULL DEFAULT NULL,
  `amount_given` DECIMAL(19,6) NOT NULL,
  `amount_taken` DECIMAL(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_m_forex_exchange_m_exchange_rate` FOREIGN KEY (`exchange_rate_id`) REFERENCES `m_exchange_rate` (`id`),
  CONSTRAINT `FK_m_forex_exchange_m_appuser` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_m_forex_exchange_m_organisation_currency_from` FOREIGN KEY (`currency_from_code`) REFERENCES `m_organisation_currency` (`code`),
  CONSTRAINT `FK_m_forex_exchange_m_organisation_currency_to` FOREIGN KEY (`currency_to_code`) REFERENCES `m_organisation_currency` (`code`)
) ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci;



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('organisation', 'READ_FOREXEXCHANGE', 'FOREXEXCHANGE', 'READ', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('organisation', 'CREATE_FOREXEXCHANGE', 'FOREXEXCHANGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('organisation', 'UPDATE_FOREXEXCHANGE', 'FOREXEXCHANGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('organisation', 'UPDATE_FOREXEXCHANGE_CHECKER', 'FOREXEXCHANGE', 'UPDATE_CHECKER', 0);

ALTER TABLE `m_exchange_rate` CHANGE COLUMN `amount` `amount` DECIMAL(19,6) NOT NULL;