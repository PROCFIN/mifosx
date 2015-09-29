ALTER TABLE `acc_gl_journal_entry`
  ADD COLUMN `exchange_rate_id` BIGINT(20) NULL DEFAULT NULL,
  ADD CONSTRAINT `FK_acc_gl_journal_entry_m_exchange_rate` FOREIGN KEY (`exchange_rate_id`) REFERENCES `m_exchange_rate` (`id`);
