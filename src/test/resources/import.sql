# ADD ROLE
INSERT INTO crm_db_test.role (name, created_at, updated_at) VALUES ('ROLE_ADMIN',CURDATE(),CURDATE()),('ROLE_MODERATOR',CURDATE(),CURDATE()),('ROLE_SHIPPINGLINE',CURDATE(),CURDATE()),('ROLE_FORWARDER',CURDATE(),CURDATE()),('ROLE_MERCHANT',CURDATE(),CURDATE()),('ROLE_DRIVER',CURDATE(),CURDATE());


# USER:
INSERT INTO crm_db_test.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'admin@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'admin','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db_test.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'moderator@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'moderator','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db_test.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'shippingline@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'shippingline','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db_test.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'forwarder@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'forwarder','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db_test.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'merchant@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'merchant','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db_test.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'driver@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'driver','Ba Dinh, Ha Noi, Vietnam');

# MAP USER - ROLE:
INSERT INTO crm_db_test.user_role (user_id,role_id) VALUES ('1','1');
INSERT INTO crm_db_test.user_role (user_id,role_id) VALUES ('2','2');
INSERT INTO crm_db_test.user_role (user_id,role_id) VALUES ('3','3');
INSERT INTO crm_db_test.user_role (user_id,role_id) VALUES ('4','4');
INSERT INTO crm_db_test.user_role (user_id,role_id) VALUES ('5','5');
INSERT INTO crm_db_test.user_role (user_id,role_id) VALUES ('6','6');

# SUPPLIER, DRIVER
INSERT INTO crm_db_test.supplier (company_address,company_code,company_description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('210 Doi can, Ha Noi, Viet Nam','APL','Hang tau APL','Pacific Mail Steamship Company','Nguyen Van A','25688','0.2','1232223','apl.com','3');
INSERT INTO crm_db_test.supplier (company_address,company_code,company_description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('Bonn, Germany','DHL','DHL is an American-founded German courier, parcel, and express mail service.','DHL Express','Ken Allen','11111','0.5','1232423','dhl.com','4');
INSERT INTO crm_db_test.supplier (company_address,company_code,company_description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('200 Quang Trung, Ha Dong, Ha Noi, Viet Nam','TAP','Xuat khau cac thiet bi dien may','Tan An Phat','Nguyen Van A','25688','0.2','1232223','tapvn.com','5');
INSERT INTO crm_db_test.operator (user_id, fullname, is_root) VALUES ('1', 'Nguyen Van A', 1);
INSERT INTO crm_db_test.operator (user_id, fullname, is_root) VALUES ('2', 'Nguyen Van B', 0);
INSERT INTO crm_db_test.shipping_line (user_id) VALUES ('3');
INSERT INTO crm_db_test.forwarder (user_id) VALUES ('4');
INSERT INTO crm_db_test.merchant (user_id) VALUES ('5');
INSERT INTO crm_db_test.driver (driver_license,fullname,user_id,forwarder_id) VALUES ('292883943','Nguyen Van B','6','4');

# PORT
INSERT INTO crm_db_test.port (address,fullname,name_code,created_at,updated_at) VALUES ('Hai Phong, Viet Nam','Port Of Haiphong Joint Stock Company','HAIPHONGPORT',CURDATE(),CURDATE());

# CONTAINER TYPE
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('9.95','8ft Container Dimensions','1.95','2.11','2.06','2.29','2.11','8CD','6000','950','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('15.95','10ft Container Dimensions','2.28','2.34','2.39','2.84','2.35','10CD','10000','1000','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('33.2','20ft Container Dimensions','2.28','2.34','2.39','5.9','2.35','20CD','30480','2000','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.6','40ft Container Dimensions','2.28','2.34','2.39','12.03','2.35','40CD','30480','3470','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('32.8','20ft Tunnel Dimensions','2.28','2.34','2.39','5.84','2.35','20TD','30480','2180','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('31','20ft Open-sider Dimensions','2.19','2.22','2.30','5.9','2.29','20OSD','30480','3170','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('76.4','40ft High Cube Dimensions','2.58','2.34','2.69','12.03','2.35','40HCD','30480','3660','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('33','20ft Open-top Dimensions','2.28','2.34','2.28','5.96','2.35','20OTD','30480','3000','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('66.8','40ft Open-top Dimensions','2.28','2.34','2.39','12.03','2.35','40OTD','32500','4050','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.4','40ft Tunnel Dimensions','2.28','2.34','2.39','11.98','2.35','40TD','30480','3680','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.72','40ft High Cube Open Sider Dimensions','2.35','2.22','2.46','12.03','2.28','40HCOSD','24000','5920','KG',CURDATE(),CURDATE());
INSERT INTO crm_db_test.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('37.4','20ft High Cube Dimensions','2.58','2.34','2.69','5.96','2.35','20HCD','30480','2100','KG',CURDATE(),CURDATE());

# TRAILER & TRACTOR
INSERT INTO crm_db_test.vehicle VALUES (1,'2020-07-12 00:48:56','112222',2,'2020-07-12 00:48:56',4),(2,'2020-07-12 00:49:22','1122223',2,'2020-07-12 00:49:22',4);
INSERT INTO crm_db_test.container_semi_trailer VALUES ('T36','FT',2);
INSERT INTO crm_db_test.container_tractor VALUES (1);
