ALTER TABLE `acc_gl_financial_activity_account`
  DROP INDEX `financial_activity_type`,
  ADD CONSTRAINT `financial_activity_type` UNIQUE INDEX(`financial_activity_type`, `currency`);
