# ADD ROLE
INSERT INTO crm_db.role (name) VALUES ('ROLE_ADMIN'),('ROLE_MODERATOR'),('ROLE_SHIPPINGLINE'),('ROLE_FORWARDER'),('ROLE_MERCHANT'),('ROLE_DRIVER');

# ADD ADDRESS
INSERT INTO crm_db.address(city,country,county,postal_code,street) VALUES ('Ha Noi','Viet Nam','Ba Dinh','100000','So 218');

# ADD USER
INSERT INTO crm_db.user (created_at,email,password,phone,status,updated_at,username,address_id) VALUES (CURDATE(),'admin@crm.com','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390098',1,CURDATE(),'admin','1');

# MAP USER - ROLE
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('1','1');

# ICD
INSERT INTO crm_db.icd (address,fullname,name_code) VALUES ('TS 10, Dong Nguyen, Tu Son, Bac Ninh','ICD Tien Son','TIENSON');

# PORT
INSERT INTO crm_db.port (address,fullname,name_code) VALUES ('Hai Phong, Viet Nam','Port Of Haiphong Joint Stock Company','HAIPHONGPORT');

# CONTAINER TYPE
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,payload_capacity,tare_weight) VALUES ('67.72','40ft High Cube','2.35','2.22','2.46','12.03','2.28','40HC','24000','5920');

