ALTER TABLE tasks
    ADD COLUMN IF NOT EXISTS
        user_id int;