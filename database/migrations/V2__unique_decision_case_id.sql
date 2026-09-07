ALTER TABLE decisions
    ADD CONSTRAINT uq_decisions_case_id UNIQUE (case_id);
