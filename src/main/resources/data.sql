--
-- Table for User
--

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `user_id` varchar(25) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(100) NOT NULL,
  PRIMARY KEY (`user_id`)
);