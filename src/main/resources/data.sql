# USE lynqo;
#
# -- Insert new roles
# INSERT INTO role (name)
# VALUES ('user'),
#        ('admin');
#
# -- Insert new users
# INSERT INTO user (id, username, password, email, avatar_url, phone_number, birthdate, is_active, role_id)
# VALUES (1, 'alice123', '$2a$10$gYVZi4f7QuXdFFVaRPHyq.QgyuFiLdu1B6vQi0pifFn0RwdBwObEq', 'alice@example.com',
#         'https://example.com/alice_avatar.jpg', '1234567890', '1990-05-15', TRUE, 1);
# INSERT INTO user (id, username, password, email, avatar_url, phone_number, birthdate, is_active, role_id)
# VALUES (2, 'bob456', '$2a$10$gYVZi4f7QuXdFFVaRPHyq.QgyuFiLdu1B6vQi0pifFn0RwdBwObEq', 'bob@example.com',
#         'https://example.com/bob_avatar.jpg', '9876543210', '1985-08-20', TRUE, 1);
# INSERT INTO user (id, username, password, email, avatar_url, phone_number, birthdate, is_active, role_id)
# VALUES (3, 'user3', '$2a$10$gYVZi4f7QuXdFFVaRPHyq.QgyuFiLdu1B6vQi0pifFn0RwdBwObEq', 'sth@gmail.com',
#         'https://example.com/bob_avatar.jpg', '9876543210', '1985-08-20', TRUE, 1);
#
# -- Insert a friendship
# INSERT INTO friendship (user_id, friend_id, status) VALUES (1, 2, 'accepted');
# INSERT INTO friendship (user_id, friend_id, status) VALUES (3, 2, 'accepted');
#
# -- Insert messages
# INSERT INTO message (id, sender_id, receiver_id, content, media_type, media_url)
# VALUES (3, 1, 2, 'Watch this video!', 'video', 'https://example.com/video.mp4');
# INSERT INTO message (id, sender_id, receiver_id, content, media_type)
# VALUES (4, 2, 1, '😊', 'emoji');
