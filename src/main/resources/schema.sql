CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(128),
    name VARCHAR(64)
);

CREATE TABLE item_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    requestor_id BIGINT,
    created DATETIME,
    FOREIGN KEY (requestor_id) REFERENCES users(id)
);

CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(255),
    owner_id BIGINT,
    request_id BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (request_id) REFERENCES item_requests(id)
);

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    `start` DATETIME,
    `end` DATETIME,
    item_id BIGINT,
    booker_id BIGINT,
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (booker_id) REFERENCES users(id)
);