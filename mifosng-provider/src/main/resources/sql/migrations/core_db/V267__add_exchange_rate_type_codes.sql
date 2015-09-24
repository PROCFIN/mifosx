ALTER TABLE `m_exchange_rate`
    DROP COLUMN `type`,
    ADD COLUMN `type_cv_id` int(11) NULL DEFAULT NULL,
    ADD CONSTRAINT `FK_m_exchange_rate_mcode_value_type` FOREIGN KEY (`type_cv_id`) REFERENCES `m_code_value` (`id`);

INSERT INTO `m_code` (`code_name`,`is_system_defined`) VALUES('ExchangeRateType',1);

SET @codeId = LAST_INSERT_ID();

INSERT INTO `m_code_value` (`code_id`, `code_value`, `order_position`) VALUES
    (@codeId, 'Selling', 0),
    (@codeId, 'Buying', 0),
    (@codeId, 'Intermediary', 0);
