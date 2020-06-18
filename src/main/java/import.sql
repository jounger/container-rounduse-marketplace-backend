INSERT INTO role (name) VALUES 
('ROLE_ADMIN'),
('ROLE_MODERATOR'),
('ROLE_SHIPPINGLINE'),
('ROLE_FORWARDER'),
('ROLE_MERCHANT'),
('ROLE_DRIVER');
INSERT INTO address (street, county, city, postal_code, country) VALUES
('Thach That', 'Son Tay', 'Ha Noi', '000000', 'VietNam'),
('Thach That', 'Son Tay', 'Ha Noi', '000200', 'VietNam'),
('Thach That', 'Son Tay', 'Ha Noi', '000300', 'VietNam'),
('Thach That', 'Son Tay', 'Ha Noi', '000400', 'VietNam'),
('Thach That', 'Son Tay', 'Ha Noi', '000100', 'VietNam');
INSERT INTO icd (address, name, name_code) VALUES
('Thach that son tay ha noi', 'icd1', 'icd1'),
('Thach that son tay ha noi', 'icd2', 'icd2'),
('Thach that son tay ha noi', 'icd3', 'icd3'),
('Thach that son tay ha noi', 'icd4', 'icd4'),
('Thach that son tay ha noi', 'icd5', 'icd5');
INSERT INTO port (address, name, name_code) VALUES
('Thach that son tay ha noi', 'port1', 'port1'),
('Thach that son tay ha noi', 'port2', 'port2'),
('Thach that son tay ha noi', 'port3', 'port3'),
('Thach that son tay ha noi', 'port4', 'port4'),
('Thach that son tay ha noi', 'port5', 'port5');
