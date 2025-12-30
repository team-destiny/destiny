-- Stock
CREATE UNIQUE INDEX ux_stock_product_id
    ON stock(product_id);

-- StockReservation
CREATE INDEX idx_stock_reservation_order_id
    ON stock_reservation(order_id);

CREATE INDEX idx_stock_reservation_product_id
    ON stock_reservation(product_id);

CREATE INDEX idx_stock_reservation_status
    ON stock_reservation(status);