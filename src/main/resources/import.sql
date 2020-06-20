# ADD ROLE
INSERT INTO crm_db.role (name) VALUES ('ROLE_ADMIN'),('ROLE_MODERATOR'),('ROLE_SHIPPINGLINE'),('ROLE_FORWARDER'),('ROLE_MERCHANT'),('ROLE_DRIVER');

# ADD ADDRESS
INSERT INTO crm_db.address(city,country,county,postal_code,street) VALUES ('Ha Noi','Viet Nam','Ba Dinh','100000','So 218');

# ADD USER: admin, moderator, shippingline, forwarder, merchant, driver
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'admin@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'admin','1');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'moderator@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'moderator','1');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'shippingline@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'shippingline','1');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'forwarder@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'forwarder','1');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'merchant@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'merchant','1');
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'driver@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'driver','1');

# MAP USER - ROLE: admin, moderator, shippingline, forwarder, merchant, driver
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('1','1');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('2','2');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('3','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('4','4');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('5','5');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('6','6');

# SUPPLIER
INSERT INTO crm_db.supplier (company_address,company_code,description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('Bonn, Germany','DHL','DHL is an American-founded German courier, parcel, and express mail service.','DHL Express','Ken Allen','11111','0.5','1232423','dhl.com','4');
INSERT INTO crm_db.supplier (company_address,company_code,description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('200 Quang Trung, Ha Dong, Ha Noi, Viet Nam','TAP','Xuat khau cac thiet bi dien may','Tan An Phat','Nguyen Van A','25688','0.2','1232223','tapvn.com','5');


# shippingline, forwarder, merchant
INSERT INTO crm_db.shipping_line (company_code,company_name,website,user_id) VALUES ('apl','Pacific Mail Steamship Company','apl.com','3');
INSERT INTO crm_db.forwarder (user_id) VALUES ('4');
INSERT INTO crm_db.merchant (user_id) VALUES ('5');

# ICD
INSERT INTO crm_db.icd (address,fullname,name_code) VALUES ('TS 10, Dong Nguyen, Tu Son, Bac Ninh','ICD Tien Son','TIENSON');

# PORT
INSERT INTO crm_db.port (address,fullname,name_code) VALUES ('Hai Phong, Viet Nam','Port Of Haiphong Joint Stock Company','HAIPHONGPORT');

# CONTAINER TYPE
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,payload_capacity,tare_weight) VALUES ('67.72','40ft High Cube','2.35','2.22','2.46','12.03','2.28','40HC','24000','5920');

