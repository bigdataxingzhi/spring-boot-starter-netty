CREATE TABLE IF NOT EXISTS user
(
    `id` int AUTO_INCREMENT,
    `name` varchar(255),
    `email` varchar(255),
    `age` varchar(255),
    `passwd` varchar(255),
    PRIMARY KEY (`id`),
    UNIQUE KEY (`name`),
    UNIQUE KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
---插入若干数据
INSERT INTO user (`name`, `email`, `age`, `passwd`)
VALUES ('user01', 'user01@163.com', '20', '123');

INSERT INTO user (`name`, `email`, `age`, `passwd`)
VALUES ('user02', 'user02@163.com', '20', '456');

INSERT INTO user (`name`, `email`, `age`, `passwd`)
VALUES ('用户03', 'user03@163.com', '20', '456');