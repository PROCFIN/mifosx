
SET SESSION old_alter_table=1;
ALTER IGNORE TABLE `m_cashiers`
  DROP INDEX `IK_m_cashiers_m_staff`,
  ADD UNIQUE INDEX `IK_m_cashiers_m_staff` (`staff_id`);
