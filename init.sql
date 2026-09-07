USE lynqo;

CREATE TABLE IF NOT EXISTS role
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- Create table user
CREATE TABLE IF NOT EXISTS user
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    username     NVARCHAR(50),
    password     VARCHAR(100),
    email        VARCHAR(50),
    role_id      INT,
    avatar_url   VARCHAR(255),
    phone_number VARCHAR(10),
    birthdate    DATE,
    is_active    TINYINT(1)    DEFAULT 0 ,
    is_blocked   TINYINT(1)   DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role (id)
);

-- create table token
CREATE TABLE IF NOT EXISTS token
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    token   VARCHAR(36) NOT NULL UNIQUE,
    token_type VARCHAR(16) NOT NULL,
    revoked TINYINT(1) NOT NULL DEFAULT 0,
    expired TINYINT(1) NOT NULL DEFAULT 0,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES user (id)
);

-- Create table message
CREATE TABLE IF NOT EXISTS message
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    sender_id   int NOT NULL,
    receiver_id int NOT NULL,
    content     TEXT,
    status      NVARCHAR(10),
    media_type  ENUM ('text', 'image', 'video', 'file', 'call') DEFAULT 'text',
    media_url   VARCHAR(255),
    timestamp   TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES user (id),
    FOREIGN KEY (receiver_id) REFERENCES user (id)
);

-- Create table friendship
CREATE TABLE IF NOT EXISTS friendship
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    user_id   int                                     NOT NULL,
    friend_id int                                     NOT NULL,
    status    ENUM ('pending', 'accepted', 'blocked') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (friend_id) REFERENCES user (id)
);

-- Create table video call
CREATE TABLE IF NOT EXISTS video_call
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    caller_id   INT                                     NOT NULL,
    receiver_id INT                                     NOT NULL,
    start_time  DATETIME                                NOT NULL,
    end_time    DATETIME,
    status      ENUM ('ongoing', 'completed', 'missed') NOT NULL,
    FOREIGN KEY (caller_id) REFERENCES user (id),
    FOREIGN KEY (receiver_id) REFERENCES user (id)
);

-- Insert new roles
INSERT INTO role (name)
VALUES ('user'),
       ('admin');

-- Insert new users
INSERT INTO user (id, username, password, email, avatar_url, phone_number, birthdate, is_active, role_id)
VALUES (1, 'alice123', '$2a$10$gYVZi4f7QuXdFFVaRPHyq.QgyuFiLdu1B6vQi0pifFn0RwdBwObEq', 'alice@example.com',
        'https://example.com/alice_avatar.jpg', '1234567890', '1990-05-15', TRUE, 1);
INSERT INTO user (id, username, password, email, avatar_url, phone_number, birthdate, is_active, role_id)
VALUES (2, 'bob456', '$2a$10$gYVZi4f7QuXdFFVaRPHyq.QgyuFiLdu1B6vQi0pifFn0RwdBwObEq', 'bob@example.com',
        'https://example.com/bob_avatar.jpg', '9876543210', '1985-08-20', TRUE, 1);
INSERT INTO user (id, username, password, email, avatar_url, phone_number, birthdate, is_active, role_id)
VALUES (3, 'user3', '$2a$10$gYVZi4f7QuXdFFVaRPHyq.QgyuFiLdu1B6vQi0pifFn0RwdBwObEq', 'sth@gmail.com',
        'https://example.com/bob_avatar.jpg', '9876543210', '1985-08-20', TRUE, 1);

-- Insert a friendship
INSERT INTO friendship (user_id, friend_id, status) VALUES (1, 2, 'accepted');
INSERT INTO friendship (user_id, friend_id, status) VALUES (3, 2, 'accepted');

-- Insert messages
INSERT INTO message (id, sender_id, receiver_id, content, media_type)
VALUES (1, 2, 3, 'Hello', 'text');
INSERT INTO message (id, sender_id, receiver_id, content, media_type)
VALUES (2, 3, 2, 'Hi', 'text');
INSERT INTO message (id, sender_id, receiver_id, content, media_type, media_url)
VALUES (3, 2, 3, '', 'video', 'http://res.cloudinary.com/dannocqhr/video/upload/v1732720592/g9bsoel00zqvxpkbr1lo.mp4');
INSERT INTO message (id, sender_id, receiver_id, content, media_type, media_url)
VALUES (4, 3, 2, '', 'image', 'http://res.cloudinary.com/dannocqhr/image/upload/v1732645319/mngcgrknjeraucazfuf2.jpg');
