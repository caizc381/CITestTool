update tb_card a set a.from_hospital =
  (SELECT b.from_hospital from
    (SELECT id,from_hospital from tb_card) b where b.id = a.parent_card_id)
where a.manager_id in
    (SELECT tb_account_role.account_id from tb_account_role where tb_account_role.role_id != 4)
      AND a.parent_card_id is not null;

update tb_card a set a.new_company_id =
  (SELECT c.new_company_id from
    (SELECT b.id,tb_channel_company.id as new_company_id from
      (SELECT tb_card.id, tb_card.company_id, tb_manager_channel_relation.channel_id
        from tb_card, tb_manager_channel_relation WHERE tb_card.manager_id = tb_manager_channel_relation.manager_id) b,tb_channel_company
        where b.company_id = tb_channel_company.tb_exam_company_id and b.channel_id = tb_channel_company.organization_id) c where a.id = c.id);

UPDATE tb_manager_card_relation,tb_card SET tb_manager_card_relation.new_company_id = tb_card.new_company_id
  WHERE tb_manager_card_relation.card_id = tb_card.id and tb_card.manager_id in
    (SELECT tb_manager_channel_relation.manager_id from tb_manager_channel_relation);

UPDATE tb_card_batch,tb_card SET tb_card_batch.new_company_id = tb_card.new_company_id
  WHERE tb_card_batch.id = tb_card.batch_id and tb_card.manager_id in
    (SELECT tb_manager_channel_relation.manager_id from tb_manager_channel_relation);

UPDATE tb_card_exam_note,tb_card SET tb_card_exam_note.new_company_id = tb_card.new_company_id
  WHERE tb_card_exam_note.account_id = tb_card.manager_id and tb_card.manager_id in
    (SELECT tb_manager_channel_relation.manager_id from tb_manager_channel_relation);