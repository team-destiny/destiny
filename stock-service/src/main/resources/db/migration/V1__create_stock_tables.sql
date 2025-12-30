CREATE TABLE stock (
                       id UUID PRIMARY KEY,
                       product_id UUID NOT NULL,
                       total_quantity INTEGER NOT NULL,
                       reserved_quantity INTEGER NOT NULL,
                       version BIGINT NOT NULL,

    -- BaseEntity
                       created_at TIMESTAMP,
                       created_by UUID,
                       updated_at TIMESTAMP,
                       updated_by UUID,
                       deleted_at TIMESTAMP,
                       deleted_by UUID
);
ALTER TABLE stock
    ADD CONSTRAINT chk_stock_quantity
        CHECK (total_quantity >= 0 AND reserved_quantity >= 0);

CREATE TABLE stock_reservation (
                                   reservation_id UUID PRIMARY KEY,
                                   order_id UUID NOT NULL,
                                   product_id UUID NOT NULL,
                                   reserved_quantity INTEGER NOT NULL,
                                   status VARCHAR(50) NOT NULL,
                                   version BIGINT NOT NULL
);

ALTER TABLE stock_reservation
    ADD CONSTRAINT chk_stock_reservation_quantity
        CHECK (reserved_quantity > 0);