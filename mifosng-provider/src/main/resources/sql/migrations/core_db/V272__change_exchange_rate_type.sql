ALTER TABLE `m_exchange_rate`
  DROP FOREIGN KEY `FK_m_exchange_rate_mcode_value_type`,
  DROP COLUMN `type_cv_id`,
  ADD COLUMN `type` INT(11) NOT NULL;
