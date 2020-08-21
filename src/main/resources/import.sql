# ADD ROLE
INSERT INTO crm_db.role (name, created_at, updated_at) VALUES ('ROLE_ADMIN',CURDATE(),CURDATE()),('ROLE_MODERATOR',CURDATE(),CURDATE()),('ROLE_SHIPPINGLINE',CURDATE(),CURDATE()),('ROLE_FORWARDER',CURDATE(),CURDATE()),('ROLE_MERCHANT',CURDATE(),CURDATE()),('ROLE_DRIVER',CURDATE(),CURDATE());


# USER:
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'admin@crm.com','Nguyen Van A','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390091','ACTIVE',CURDATE(),'admin','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'moderator@crm.com','Nguyen Van B','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390092','ACTIVE',CURDATE(),'moderator','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'shippingline@crm.com','Nguyen Van C','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390093','ACTIVE',CURDATE(),'shippingline','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'forwarder@crm.com','Nguyen Van D','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390094','ACTIVE',CURDATE(),'forwarder','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'merchant@crm.com','Nguyen Van E','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390095','ACTIVE',CURDATE(),'merchant','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'driver@crm.com','Nguyen Van F','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390096','ACTIVE',CURDATE(),'driver','Ba Dinh, Ha Noi, Vietnam');

# MAP USER - ROLE:
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('1','1');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('2','2');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('3','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('4','4');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('5','5');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('6','6');

# SUPPLIER, DRIVER
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('210 Doi can, Ha Noi, Viet Nam','APL','Hang tau APL','Pacific Mail Steamship Company','25688','0.2','1232223','apl.com','3');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Bonn, Germany','DHL','DHL is an American-founded German courier, parcel, and express mail service.','DHL Express','11111','0.5','1232423','dhl.com','4');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('200 Quang Trung, Ha Dong, Ha Noi, Viet Nam','TAP','Xuat khau cac thiet bi dien may','Tan An Phat','25688','0.2','1232223','tapvn.com','5');
INSERT INTO crm_db.operator (user_id, is_root) VALUES ('1', 1);
INSERT INTO crm_db.operator (user_id, is_root) VALUES ('2', 0);
INSERT INTO crm_db.shipping_line (user_id) VALUES ('3');
INSERT INTO crm_db.forwarder (user_id) VALUES ('4');
INSERT INTO crm_db.merchant (user_id) VALUES ('5');
INSERT INTO crm_db.driver (driver_license,user_id,forwarder_id) VALUES ('292883943','6','4');

# PORT
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Số 1A Minh Khai, Hồng Bàng, Hải Phòng','Port Of Haiphong Joint Stock Company','HAIPHONGPORT',CURDATE(),CURDATE());

# CONTAINER TYPE
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('9.95','8ft Container Dimensions','1.95','2.11','2.06','2.29','2.11','8CD','6000','950','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('15.95','10ft Container Dimensions','2.28','2.34','2.39','2.84','2.35','10CD','10000','1000','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('33.2','20ft Container Dimensions','2.28','2.34','2.39','5.9','2.35','20CD','30480','2000','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.6','40ft Container Dimensions','2.28','2.34','2.39','12.03','2.35','40CD','30480','3470','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('32.8','20ft Tunnel Dimensions','2.28','2.34','2.39','5.84','2.35','20TD','30480','2180','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('31','20ft Open-sider Dimensions','2.19','2.22','2.30','5.9','2.29','20OSD','30480','3170','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('76.4','40ft High Cube Dimensions','2.58','2.34','2.69','12.03','2.35','40HCD','30480','3660','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('33','20ft Open-top Dimensions','2.28','2.34','2.28','5.96','2.35','20OTD','30480','3000','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('66.8','40ft Open-top Dimensions','2.28','2.34','2.39','12.03','2.35','40OTD','32500','4050','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.4','40ft Tunnel Dimensions','2.28','2.34','2.39','11.98','2.35','40TD','30480','3680','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.72','40ft High Cube Open Sider Dimensions','2.35','2.22','2.46','12.03','2.28','40HCOSD','24000','5920','KG',CURDATE(),CURDATE());
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('37.4','20ft High Cube Dimensions','2.58','2.34','2.69','5.96','2.35','20HCD','30480','2100','KG',CURDATE(),CURDATE());

# TRAILER & TRACTOR
INSERT INTO crm_db.vehicle(id,created_at,license_plate,number_of_axles,updated_at,user_id) VALUES (1,'2020-07-12 00:48:56','112222',2,'2020-07-12 00:48:56',4),(2,'2020-07-12 00:49:22','1122223',2,'2020-07-12 00:49:22',4);
INSERT INTO crm_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',2);
INSERT INTO crm_db.container_tractor(vehicle_id) VALUES (1);

# SHIPPINGLINE :
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'wanhai@crm.com','Joseph Lin','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','8211297','ACTIVE',CURDATE(),'wanhai','194 Nguyễn Công Trứ, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'saigonship@saigonshipvn.com','Morten Bruehl','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38296320','ACTIVE',CURDATE(),'saigonship','Lầu 7, Landmark Building, 5B Tôn Đức Thắng, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vinatrans.mngt@vinatrans.com.vn','David Chew','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39414919','ACTIVE',CURDATE(),'vinatrans','406 Nguyễn Tất Thành ,Phường 18,Quận 4,TP.Hồ Chí Minh');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'phuc-ct@vietfracht-hcm.com','Y.S.Chung','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','3811793','ACTIVE',CURDATE(),'vietfracht','Lầu 1, Saigon Port Building, 3 Nguyễn Tất Thành, Quận 4');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'sgncs@apl.com','Ong Toi Kin','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39331250','ACTIVE',CURDATE(),'sgncs','lầu 8, Diamond plaza, 34 Lê Duẩn, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'heung-a@vietfracht-hcm.com','Jae Seon Hong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38210806','ACTIVE',CURDATE(),'heunga','lầu 2, 11 Nguyễn Công Trứ, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'thuan-pham@evergreen-shipping.com.vn','Daniel Wu','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39111026','ACTIVE',CURDATE(),'evergreen','54 Nguyễn Đình Chiểu, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@viconship.com','Trần Hòa Bình','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','3836705','ACTIVE',CURDATE(),'viconship','Số 11 – Võ Thị Sáu – Ngô Quyền - Hải Phòng.');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'ponl@vitranschart.com','Michael Dam','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39404271','ACTIVE',CURDATE(),'vitranschart','428 Nguyễn Tất Thành, P.18, Q.4, TP.HCM, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'pal@hcm.vnn.vn','Jimmy Wong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','9141102','ACTIVE',CURDATE(),'samudera','16 Phó Đức Chính, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'operations@continentalshipping.com','Jose O. Busto','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','9141102','ACTIVE',CURDATE(),'continentalshipping','phòng 810, lầu 8, Sunwah Tower Building, 115 Nguyễn Huệ, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info.sgn@safi.com.vn','Bui Xuan Bach','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38232187','ACTIVE',CURDATE(),'shippingsafi','lầu 12, 37 Tôn Đức Thắng, Quận 1, TP. HCM');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vosagroup@vosagroup.com','Pham Manh Cuong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','54161820','ACTIVE',CURDATE(),'vosagroup','lầu 2, Vosa Building, 7 Nguyễn Huệ, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'agency@vitamas.com.vn','Nguyễn Hồng Trung','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39402382','ACTIVE',CURDATE(),'vitamas','Số 44 - 46 Nguyễn Tất Thành,Phường 12, Quận 4, Thành Phố Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@maersksealand.com.vn','Peter Berendsen Svarrer','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38238566','ACTIVE',CURDATE(),'maersk','Zen Plaza, 54-56 Nguyễn Trãi, Quận 1, 700000 Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'headoffice@vinalinklogistics.com','Nguyễn Nam Tiến','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39919259','ACTIVE',CURDATE(),'vinalink','226/2 Lê Văn Sỹ, Phường 1, Quận Tân Bình, TP Hồ Chí Minh');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@transimex.com.vn','Trần Hồng Đởm','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','22202888','ACTIVE',CURDATE(),'transimek','172 (Lầu 9-10) Hai Bà Trưng, Phường Đakao, Quận 1, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'office@hlcargo.com','Su Han Kim','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','54107108','ACTIVE',CURDATE(),'hlcargo','Tầng 5 Thiên Sơn Plaza - 800 Nguyễn Văn Linh, Phường Tân Phú, Quận 7, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'cs.hq@coscon.com','Yang Zenglian','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','35124888','ACTIVE',CURDATE(),'cosco','lầu 1, 47 Phó Đức Chính, Quận 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'cs.hq@cosfi.com','Vũ Văn Trực','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38238172','ACTIVE',CURDATE(),'cosfi','209 Nguyễn Văn Thủ, Phường Đa Kao, Quận 1, TP Hồ Chí Minh');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@sunnytrans.com.vn','Bùi Quốc Hùng','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39402741','ACTIVE',CURDATE(),'sunnytrans','146 Khánh Hội, Phường 6, Quận 4, TP.Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'mngt@vinafreight.com.vn','Nguyễn Bích Lân','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38446409','ACTIVE',CURDATE(),'vinafreight','Lầu 1, Block C, Tòa nhà văn phòng WASECO, số 10 Phổ Quang, Phường 2, Quận Tân Bình, Tp.HCM, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vnm-info@msc.com','Hòang Trọng Giang','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','36363939','ACTIVE',CURDATE(),'mschcm','Tầng 27, Tòa nhà Etown Central 11 Đoàn Văn Bơ, Quận 4 - TP HỒ CHÍ MINH');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'bkghcmnvo@oocl.com','Albert Chen','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39116088','ACTIVE',CURDATE(),'ooclvn','Lầu 13, Trung tâm Thương mại Sài Gòn 37 Tôn Đức Thắng, Quận 1, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vtb.agent-hcm@vinafco.com.vn','Vũ Hòang Bảo','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38268770','ACTIVE',CURDATE(),'vinafco','Số 53-55, Đường 41, Phường 6, Quận. 4 - Thành phố Hồ Chí Minh');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vnl@vinalines.com.vn','Lê Anh Sơn','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39973818','ACTIVE',CURDATE(),'shippinglines','Số 163, Nguyễn Văn Trỗi, Phường 11, Quận Phú Nhuận, HCM');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'infovnw@vinashinnewworld.vn','Ngô Duy Thạch','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39118646','ACTIVE',CURDATE(),'vinashin','6/16 Nguyễn Văn Thủ, P. Đa Kao, Q. 1, Tp. 1');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'mary@vietnam-ship.com','Mary Phuong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','8375568','ACTIVE',CURDATE(),'vietnamship','396 Trường Sa, Phường 02, Quận Phú Nhuận, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'kevin.bach@asw-vietnam.com','Kevin Bach','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','2969858','ACTIVE',CURDATE(),'airseaworldvn','50 Cửu Long, Phường 2, Quận Tân Bình, TP.HCM, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'povn-hcm@philiorient.com.vn','Phillip Thuan','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38215637','ACTIVE',CURDATE(),'philiorienthcm','153 Nguyễn Tất Thành, Quận 4, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'mtdao@safi.com.vn','Nguyen Hoang Dung','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','9745575','ACTIVE',CURDATE(),'seasafihn','Số 51, Lê Đại Hành, Q. Hai Bà Trưng, Hà Nội, Việt Nam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'giangthuyco@hn.vnn.vn','Nguyen Thanh Thuy','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','3852372','ACTIVE',CURDATE(),'giangthuy','11/51 Đông Khê, Tp Hải Phòng, Việt Nam');

INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('194 Nguyễn Công Trứ, Quận 1','WLL','Hang tau WLL','WANHAI LINES LTD','8243219','0','8251883','www.wanhai.com','7');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 7, Landmark Building, 5B Tôn Đức Thắng, Quận 1','ASSC','Hang tau ASSC','SAIGON SHIPPING CO.LTD','8230013','0','8230014','www.apmss.com','8');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('406 Nguyễn Tất Thành ,Phường 18,Quận 4,TP.Hồ Chí Minh','VINATRANS','Công ty Giao Nhận Kho Vận Ngoại Thương VINATRANS','VINATRANS','8259561','0','8259560','www.vinatrans.com','9');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 1, Saigon Port Building, 3 Nguyễn Tất Thành, Quận 4','VIETFRACHT','Công ty cổ phần Vận tải và Thuê tàu','VIETFRACHT','8267442','0','8267438','vietfracht.vn','10');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 8, Diamond plaza, 34 Lê Duẩn, Quận 1','APLS','Hãng tàu APL','APL','8227880','0','8227881','www.apl.com','11');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 2, 11 Nguyễn Công Trứ, Quận 1','HEUNGA','Hãng Tàu Heung-A Shipping','HEUNG A','38211050','0','38210810','www.heunga-co.kr','12');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('54 Nguyễn Đình Chiểu, Quận 1','EVERGREEN','Hãng Tàu Evergreen Line','Evergreen Line','9103527','0','9103525','www.evergreen-marine.com','13');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 11 – Võ Thị Sáu – Ngô Quyền - Hải Phòng.','VICONSHIP','Công ty cổ phần Container Việt Nam','VICONSHIP','3836105','0','3836104','www.viconship.com','14');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('428 Nguyễn Tất Thành, P.18, Q.4, TP.HCM, Việt Nam','VCJSC','CÔNG TY CỔ PHẦN VẬN TẢI VÀ THUÊ TÀU BIỂN VIỆT NAM','VITRANSCHART JSC','39404712','0','39404711','www.viconship.com','15');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('16 Phó Đức Chính, Quận 1','SAMUDERA','Hãng Tàu Heung-A Shipping Samudera Shipping Line','Samudera Shipping Line','9141105','0','9141104','ssl.samudera.id','16');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('phòng 810, lầu 8, Sunwah Tower Building, 115 Nguyễn Huệ, Quận 1','CSAC','CÔNG TY CỔ PHẦN ĐẠI LÝ VẬN CHUYỂN CONTINENTAL SHIPPING','CONTINENTAL SHIPPING AGENCY CORP','8219349','0','8219259','www.continentalshipping.com','17');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 12, 37 Tôn Đức Thắng, Quận 1, TP. HCM','SAFI','CTCP Đại lý Vận tải SAFI','SAFI','8253610','0','8254371','www.safi.com.vn','18');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 2, Vosa Building, 7 Nguyễn Huệ, Quận 1','VOSASG','VOSA CORPORATION ex VOSA GROUP OF COMPANIES','VOSA CORPORATION','8212799','0','8212795','www.vosa.com.vn','19');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 44 - 46 Nguyễn Tất Thành,Phường 12, Quận 4, Thành Phố Hồ Chí Minh, Việt Nam','VITAMAS','Đại lý Thương Mại và Dịch Vụ Hàng hải','VITAMAS','8235614','0','8235618','www.nykline.com','20');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Zen Plaza, 54-56 Nguyễn Trãi, Quận 1, 700000 Hồ Chí Minh, Việt Nam','MAERSK','Tập đoàn A.P. Moller-Maersk','MAERSK SEALAND','8231398','0','8231395','www.maersk.com','21');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('226/2 Lê Văn Sỹ, Phường 1, Quận Tân Bình, TP Hồ Chí Minh','VINALINK','Công Ty Cổ Phần Logistics Vinalink','VINALINK LOGISTICS','8255340','0','8255342','vina-link.com.vn','22');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('172 (Lầu 9-10) Hai Bà Trưng, Phường Đakao, Quận 1, Thành phố Hồ Chí Minh, Việt Nam','TRANSIMEX','Công Ty Cổ Phần Transimex','TRANSIMEX SG, I.T.L.','8228566','0','8296011','transimex.com.vn','23');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Tầng 5 Thiên Sơn Plaza - 800 Nguyễn Văn Linh, Phường Tân Phú, Quận 7, Thành phố Hồ Chí Minh, Việt Nam','HLCARGO','CÔNG TY TNHH HL CARGO','HL CARGO','8451582','0','9406328','www.hlcargo.com','24');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 1, 47 Phó Đức Chính, Quận 1, Thành phố Hồ Chí Minh, Việt Nam','COSCO','HÃNG TÀU COSCO','COSCO SHIPPING LINES CO.,LTD','3552776','0','38238391','lines.coscoshipping.com','25');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('209 Nguyễn Văn Thủ, Phường Đa Kao, Quận 1, TP Hồ Chí Minh','COSFI','CÔNG TY TNHH ĐẠI LÝ VẬN TẢI COSFI','COSFI','84838238','0','84838232','www.cosfivn.com','26');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('146 Khánh Hội, Phường 6, Quận 4, TP.Hồ Chí Minh, Việt Nam','SUNNYTRANS','Công ty TNHH Vận tải Sunny','SUNNY TRANSPORTATION CO.,LTD','9402740','0','9402734','sunnytrans.com.vn','27');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 1, Block C, Tòa nhà văn phòng WASECO, số 10 Phổ Quang, Phường 2, Quận Tân Bình, Tp.HCM, Việt Nam','VNFREIGHT','Công ty Cổ phần VINAFREIGHT','VINAFREIGHT','8336409','0','8326401','www.vinafreight.com','28');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Tầng 27, Tòa nhà Etown Central 11 Đoàn Văn Bơ, Quận 4 - TP HỒ CHÍ MINH','MSC','CÔNG TY ĐẠI LÝ MSC','MSC','8276216','0','8277216','www.msc.com','29');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 13, Trung tâm Thương mại Sài Gòn 37 Tôn Đức Thắng, Quận 1, Thành phố Hồ Chí Minh, Việt Nam','OOCL','HÃNG TÀU OOCL','OOCL','8242522','0','8242523','www.oocl.com','30');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 53-55, Đường 41, Phường 6, Quận. 4 - Thành phố Hồ Chí Minh','VSC','CÔNG TY CỔ PHẦN VẬN TẢI BIỂN VINAFCO','VINAFCO SHIPPING COMPANY','3550603','0','37686820','vinafcoship.com.vn','31');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 163, Nguyễn Văn Trỗi, Phường 11, Quận Phú Nhuận, HCM','VINALINES','TỔNG CÔNG TY HÀNG HẢI VIỆT NAM','VINALINES','5770860','0','5770850','www.vinalines.com.vn','32');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('6/16 Nguyễn Văn Thủ, P. Đa Kao, Q. 1, Tp. 1','VNWLJSC','CTCP Logistics Tân Thế Giới Vinashin','VINASHIN NEW WORLD LOGISTIC JOINT STOCK COMPANY','877634','0','877638','www.vinashinnewworld.com','33');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('396 Trường Sa, Phường 02, Quận Phú Nhuận, Thành phố Hồ Chí Minh, Việt Nam','VSSC','CÔNG TY CỔ PHẦN DỊCH VỤ HÀNG HẢI VIẾT NAM','VIETNAM SHIPPING SERVICE CORPORATION','8375580','0','8375558','www.vietnam-ship.com','34');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('50 Cửu Long, Phường 2, Quận Tân Bình, TP.HCM, Việt Nam','ASWVL','Công Ty TNHH Logistics Hàng Không Biển Toàn Cầu Việt Nam','Air Sea Worldwide Vietnam Ltd','8421504','0','8421503','www.airseaworldwide.com','35');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('153 Nguyễn Tất Thành, Quận 4, Thành phố Hồ Chí Minh, Việt Nam','POVJ','Công ty cổ phần kinh doanh vận tải hàng hóa quốc tế Phili Orient Vietnam JSC','PHILI ORIENT VIETNAM JSC','9404378','0','9404370','www.philiorientlines.com','36');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 51, Lê Đại Hành, Q. Hai Bà Trưng, Hà Nội, Việt Nam','SAFIH','SEA & AIR FREIGHT INTERNATIONAL HANOI','SEA & AIR FREIGHT INTERNATIONAL HANOI','9745581','0','9745580','www.safi.com.vn','37');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('11/51 Đông Khê, Tp Hải Phòng, Việt Nam','GTSCL','CÔNG TY TNHH THƯƠNG MẠI-SẢN XUẤT VÀ VẬN TẢI BIỂN GIANG THUỶ','GIANG THUY Shipping and Manufacturing Trading Co., Ltd','3852787','0','3852788','www.giangthuyshipping.com','38');

INSERT INTO crm_db.shipping_line (user_id) VALUES ('7');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('8');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('9');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('10');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('11');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('12');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('13');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('14');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('15');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('16');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('17');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('18');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('19');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('20');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('21');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('22');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('23');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('24');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('25');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('26');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('27');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('28');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('29');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('30');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('31');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('32');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('33');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('34');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('35');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('36');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('37');
INSERT INTO crm_db.shipping_line (user_id) VALUES ('38');

INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('7','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('8','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('9','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('10','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('11','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('12','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('13','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('14','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('15','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('16','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('17','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('18','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('19','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('20','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('21','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('22','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('23','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('24','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('25','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('26','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('27','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('28','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('29','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('30','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('31','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('32','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('33','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('34','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('35','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('36','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('37','3');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('38','3');

# PORT
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('604 Đường Lý Thường Kiệt, Cửa Ông, Cẩm Phả, Quảng Ninh','CÔNG TY KHO VẬN VÀ CẢNG CẨM PHẢ - VINACOMIN','CAMPHAPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('6A Lê Thánh Tông, Hồng Gai, Thành phố Hạ Long, Quảng Ninh','Cảng biển Hòn Gai','HONGAIPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('xã Nghi Sơn, thị xã Nghi Sơn, tỉnh Thanh Hoá, Việt Nam','Cảng biển Nghi Sơn','NGHISONPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Nghi Thuỷ, Tx. Cửa Lò, Nghệ An','Cảng biển Cửa Lò','CUALOPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Khu kinh tế Vũng Áng, xã Kỳ Lợi, thị xã Kỳ Anh, tỉnh Hà Tĩnh','Cảng biển Vũng Áng','VUNGANGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('130 Nguyễn Huệ, Phú Nhuận, Thành phố Huế, Thừa Thiên Huế','Cảng biển Chân Mây','CHANMAYPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('26 Bạch Đằng, Thạch Thang, Hải Châu, Đà Nẵng 550000','Cảng biển Đà Nẵng','DANANGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Đường Không Tên, Bình Thuận, Bình Sơn, Quảng Ngãi','Cảng biển Dung Quất','DUNGQUATPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('02 Phan Chu Trinh - Phường Hải Cảng - TP. Quy Nhơn - Tỉnh Bình Định','Cảng biển Quy Nhơn','QUYNHONPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('vịnh Vân Phong, khu kinh tế Vân Phong tỉnh Khánh Hòa, Việt Nam','Cảng biển Vân Phong','VANPHONGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('03 Trần Phú, Cầu Đá, Thành phố Nha Trang, Khánh Hòa','Cảng biển Nha Trang','NHATRANGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('vịnh Cam Ranh thuộc tỉnh Khánh Hòa','Cảng biển Cam Ranh','CAMRANHPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('3 Nguyễn Tất Thành, phường 12, quận 4, TP. Hồ Chí Minh','Cảng biển Sài Gòn','SAIGONPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('2 Quang Trung, Phường 1, Thành phố Vũng Tầu, Bà Rịa - Vũng Tàu','Cảng biển Vũng Tàu','VUNGTAUPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('1B Đường D3, Long Bình Tân, Thành phố Biên Hòa, Đồng Nai','Cảng biển Đồng Nai','DONGNAIPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Cụm cảng cái cui Cần thơ, Cần Thơ 870000','Cảng biển Cái Cui','CAICUIPORT',CURDATE(),CURDATE());

INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Khu Mũi Chùa - Tiên Lãng Huyện Tiên Yên, Tiên lãng, Tiên Yên, Quảng Ninh','Cảng biển Mũi Chùa','MUICHUAPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('QL39A, Thái Thượng, Thái Thụy, Thái Bình','Cảng biển Diêm Điền','DIEMDIENPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị trấn Thịnh Long - Huyện Hải Hậu - Tỉnh Nam Định','Cảng biển Nam Định','NAMDINHPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Phường Quảng Hưng, TP Thanh Hóa, Thanh Hóa','Cảng biển Lệ Môn','LEMONPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị xã Cửa Lò, tỉnh Nghệ An','Cảng biển Bến Thủy','BENTHUYPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Kỳ lợi - Kỳ Anh - Hà Tĩnh','Cảng biển Xuân Hải','XUANHAIPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bắc Trạch- Bố Trạch- Quảng Bình','Cảng biển xăng dầu Sông Gianh','SONGGIANHPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Quảng Trạch- Quảng Bình','Cảng biển Hòn La','HONLAPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bắc Trạch- Bố Trạch- Quảng Bình','Cảng biển Thắng Lợi','THANGLOIPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thanh Trạch- Bố Trạch- Quảng Bình','Cảng biển Gianh','GIANHPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị trấn Cửa Việt, Huyện Gio Linh, Quảng Trị','Cảng biển Cửa Việt','CUAVIETPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị trấn Thuận An, huyện Phú Vang, Thuận An, Phú Vang, Thừa Thiên Huế','Cảng biển Thuận An','THUANANPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Kỳ Hà, Núi Thành, Quảng Nam','Cảng biển Kỳ Hà','KYHAPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thôn Định Tân, xã Bình Châu, huyện Lý Sơn, tỉnh Quảng Ngãi','Cảng biển Sa Kỳ','SAKYPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Vũng Rô, Đông Hòa, Khánh Hòa','Cảng biển Vũng Rô','VUNGROPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Giai đoạn 1A ( Xã Phước Diêm, huyện Thuận Nam, tỉnh Ninh Thuận)','Cảng biển Cà Ná','CANAPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Xã Tam Thanh, huyện Phú Quý, tỉnh Bình Thuận','Cảng biển Phú Quý','PHUQUYPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('QL1A, Bình An, Dĩ An, Bình Dương','Cảng biển Bình Dương','BINHDUONGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bờ phải sông Tiền, phường 11, thành phố Cao Lãnh, tỉnh Đồng Tháp','Cảng biển Đồng Tháp','DONGTHAPPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Mỹ Thạnh, Tp. Long Xuyên, An Giang','Cảng biển Mỹ Thới','MYTHOIPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('170/2 Phạm Hùng, phường 9, thị xã Vĩnh Long, Phường 9, Vĩnh Long','Cảng biển Vĩnh Long','VINHLONGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bình Đức, Thành phố Mỹ Tho, Tiền Giang','Cảng biển Mỹ Tho','MYTHOPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Khóm Hàng Vịnh, Thị trấn Năm Căn, Huyện Năm Căn, Tỉnh Cà Mau','Cảng biển Năm Căn','NAMCANPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('tx. Kiên Lương, tỉnh Kiên Giang','Cảng biển Hòn Chông','HONCHONGPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Huyện Kiên Lương-Kiên giang','Cảng biển Bình Trị','BINHTRIPORT',CURDATE(),CURDATE());
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Côn Đảo, Bà Rịa - Vũng Tàu','Cảng biển Côn Đảo','CONDAOPORT',CURDATE(),CURDATE());

# MERCHANT
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'minhkq@crm.com','Khổng Quang Minh','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0962333456','ACTIVE',CURDATE(),'minhkq','khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'duongnd@crm.com','Nguyễn Đức Dương','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','054232478','ACTIVE',CURDATE(),'duongnd','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'quyennv@crm.com','Nguyễn Văn Quyền','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0732533498','ACTIVE',CURDATE(),'quyennv','khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');

# FORWARDER
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'annv@crm.com','Nguyễn Văn An','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0912537496','ACTIVE',CURDATE(),'annv','khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'duydl@crm.com','Đào Lương Duy','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0914322787','ACTIVE',CURDATE(),'duydl','khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');
INSERT INTO crm_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'phuongnm@crm.com','Nguyễn Mai Phương','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0912755636','ACTIVE',CURDATE(),'phuongnm','Ba Dinh, Ha Noi, Vietnam');

INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','ADD','Công ty TNHH ADD','ADD Company','2231233','0','3342211','minhkq.com','39');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','AUD','Công ty TNHH AUD','AUD Company','2231234','0','3352211','duongnd.com','40');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','AND','Công ty TNHH AND','AND Company','2231235','0','3362211','quyennv.com','41');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','AGD','Công ty TNHH AGD','AGD Company','2231236','0','3372211','annv.com','42');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','ULD','Công ty TNHH ULD','ULD Company','2231237','0','3382211','duydl.com','43');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','MNG','Công ty TNHH MNG','MNG Company','2231239','0','3392211','phuongnm.com','44');

INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('39','5');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('40','5');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('41','5');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('42','4');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('43','4');
INSERT INTO crm_db.user_role (user_id,role_id) VALUES ('44','4');

INSERT INTO crm_db.merchant (user_id) VALUES ('39');
INSERT INTO crm_db.merchant (user_id) VALUES ('40');
INSERT INTO crm_db.merchant (user_id) VALUES ('41');

INSERT INTO crm_db.forwarder (user_id) VALUES ('42');
INSERT INTO crm_db.forwarder (user_id) VALUES ('43');
INSERT INTO crm_db.forwarder (user_id) VALUES ('44');