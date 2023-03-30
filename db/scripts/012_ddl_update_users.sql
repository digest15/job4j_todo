ALTER TABLE users
    ADD COLUMN IF NOT EXISTS
        user_zone varchar;