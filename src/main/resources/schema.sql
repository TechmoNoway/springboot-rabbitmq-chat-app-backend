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
    media_type  ENUM ('text', 'image', 'video', 'emoji') DEFAULT 'text',
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

-- Indexes
# CREATE INDEX idx_username ON user (username);
#
# CREATE INDEX idx_sender_receiver ON message (sender_id, receiver_id);
#
# CREATE INDEX idx_timestamp ON message (timestamp);
#
# CREATE INDEX idx_user_friend ON friendship (user_id, friend_id);


