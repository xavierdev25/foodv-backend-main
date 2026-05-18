DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_users_budget_range'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_budget_range
            CHECK (budget_range IN ('BAJO', 'MEDIO', 'ALTO'));
    END IF;
END $$;
