# ADD ROLE
INSERT INTO crm_db.role (name) VALUES ('ROLE_ADMIN'),('ROLE_MODERATOR'),('ROLE_SHIPPINGLINE'),('ROLE_FORWARDER'),('ROLE_MERCHANT'),('ROLE_DRIVER');


# USER:
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'admin@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'admin','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'moderator@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'moderator','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'shippingline@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'shippingline','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'forwarder@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'forwarder','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'merchant@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'merchant','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'driver@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098','ACTIVE',CURDATE(),'driver','Ba Dinh, Ha Noi, Vietnam');

# MAP USER - ROLE:
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('1','1');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('2','2');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('3','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('4','4');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('5','5');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('6','6');

# SUPPLIER, DRIVER
INSERT INTO crm_db.supplier (company_address,company_code,description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('210 Doi can, Ha Noi, Viet Nam','APL','Hang tau APL','Pacific Mail Steamship Company','Nguyen Van A','25688','0.2','1232223','apl.com','3');
INSERT INTO crm_db.supplier (company_address,company_code,description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('Bonn, Germany','DHL','DHL is an American-founded German courier, parcel, and express mail service.','DHL Express','Ken Allen','11111','0.5','1232423','dhl.com','4');
INSERT INTO crm_db.supplier (company_address,company_code,description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('200 Quang Trung, Ha Dong, Ha Noi, Viet Nam','TAP','Xuat khau cac thiet bi dien may','Tan An Phat','Nguyen Van A','25688','0.2','1232223','tapvn.com','5');
INSERT INTO crm_db.forwarder (user_id) VALUES ('3');
INSERT INTO crm_db.forwarder (user_id) VALUES ('4');
INSERT INTO crm_db.merchant (user_id) VALUES ('5');
INSERT INTO crm_db.driver (driver_license,fullname,location,user_id,forwarder_id) VALUES ('292883943','Nguyen Van B','21.0245-105.84117','6','4');

# PORT
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Hai Phong, Viet Nam','Port Of Haiphong Joint Stock Company','HAIPHONGPORT',CURDATE(),CURDATE());

# CONTAINER TYPE
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,payload_capacity,tare_weight,created_at,updated_at) VALUES ('67.72','40ft High Cube','2.35','2.22','2.46','12.03','2.28','40HC','24000','5920',CURDATE(),CURDATE());