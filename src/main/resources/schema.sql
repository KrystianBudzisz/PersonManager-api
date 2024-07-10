CREATE TABLE import_status
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    status            VARCHAR(255),
    created_date      TIMESTAMP,
    started_date      TIMESTAMP,
    processed_records INT
);

CREATE TABLE person
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255)       NOT NULL,
    last_name  VARCHAR(255)       NOT NULL,
    pesel      VARCHAR(11) UNIQUE NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL,
    email      VARCHAR(255)       NOT NULL,
    version    BIGINT
);
ALTER TABLE person
    ADD COLUMN type VARCHAR(255);

CREATE TABLE employee
(
    id               BIGINT PRIMARY KEY,
    employment_date  VARCHAR(255),
    current_position VARCHAR(255),
    current_salary DOUBLE,
    FOREIGN KEY (id) REFERENCES person (id)
);

CREATE TABLE position
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id   BIGINT NOT NULL,
    position_name VARCHAR(255),
    salary DOUBLE,
    start_date    DATE,
    end_date      DATE,
    FOREIGN KEY (employee_id) REFERENCES employee (id)
);

CREATE TABLE student
(
    id              BIGINT PRIMARY KEY,
    university_name VARCHAR(255),
    year_of_study   INT,
    field_of_study  VARCHAR(255),
    scholarship DOUBLE,
    FOREIGN KEY (id) REFERENCES person (id)
);

CREATE TABLE retiree
(
    id           BIGINT PRIMARY KEY,
    pension DOUBLE,
    years_worked INT,
    FOREIGN KEY (id) REFERENCES person (id)
);

CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;