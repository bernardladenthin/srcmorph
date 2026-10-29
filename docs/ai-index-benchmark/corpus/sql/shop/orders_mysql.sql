-- SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
--
-- SPDX-License-Identifier: Apache-2.0
--
-- Web shop: products, orders and order placement (MySQL 8).

CREATE TABLE product (
    product_id  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    sku         VARCHAR(40) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    price_cents INT UNSIGNED NOT NULL,
    stock       INT NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id),
    UNIQUE KEY uk_product_sku (sku)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_order (
    order_id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    customer_id BIGINT UNSIGNED NOT NULL,
    status      ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_cents INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (order_id),
    KEY idx_order_customer (customer_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_item (
    order_id         BIGINT UNSIGNED NOT NULL,
    product_id       INT UNSIGNED NOT NULL,
    quantity         SMALLINT UNSIGNED NOT NULL,
    unit_price_cents INT UNSIGNED NOT NULL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES customer_order (order_id) ON DELETE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER //

-- Adds one line to an order, reserves stock and keeps the order total current.
CREATE PROCEDURE add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)
BEGIN
    DECLARE v_product_id INT UNSIGNED;
    DECLARE v_price INT UNSIGNED;

    SELECT product_id, price_cents INTO v_product_id, v_price
      FROM product
     WHERE sku = p_sku AND stock >= p_qty
     FOR UPDATE;

    IF v_product_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'unknown SKU or not enough stock';
    END IF;

    INSERT INTO order_item (order_id, product_id, quantity, unit_price_cents)
    VALUES (p_order_id, v_product_id, p_qty, v_price)
    ON DUPLICATE KEY UPDATE quantity = quantity + p_qty;

    UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id;
    UPDATE customer_order
       SET total_cents = (SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id)
     WHERE order_id = p_order_id;
END //

DELIMITER ;
