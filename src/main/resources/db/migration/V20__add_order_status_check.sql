DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_order_status'
    ) THEN
        ALTER TABLE orders
            ADD CONSTRAINT chk_order_status
            CHECK (status IN (
                'PENDIENTE',
                'PREPARANDO',
                'LISTO_PARA_RECOGER',
                'EN_CAMINO',
                'ENTREGADO',
                'CANCELADO'
            ));
    END IF;
END $$;
