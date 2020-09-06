# ADD ROLE
INSERT INTO crum_db.role (name, created_at, updated_at) VALUES ('ROLE_ADMIN',CURDATE(),CURDATE()),('ROLE_MODERATOR',CURDATE(),CURDATE()),('ROLE_SHIPPINGLINE',CURDATE(),CURDATE()),('ROLE_FORWARDER',CURDATE(),CURDATE()),('ROLE_MERCHANT',CURDATE(),CURDATE()),('ROLE_DRIVER',CURDATE(),CURDATE());


# USER:
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'admin@crm.com','Nguyen Van A','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390091','ACTIVE',CURDATE(),'admin','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'moderator@crm.com','Nguyen Van B','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390092','ACTIVE',CURDATE(),'moderator','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'shippingline@crm.com','Nguyen Van C','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390093','ACTIVE',CURDATE(),'shippingline','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'forwarder@crm.com','Nguyen Van D','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390094','ACTIVE',CURDATE(),'forwarder','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'merchant@crm.com','Nguyen Van E','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390095','ACTIVE',CURDATE(),'merchant','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'driver@crm.com','Nguyen Van F','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0967390096','ACTIVE',CURDATE(),'driver','Ba Dinh, Ha Noi, Vietnam');

# MAP USER - ROLE:
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('1','1');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('2','2');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('3','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('4','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('5','5');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('6','6');

# SUPPLIER, DRIVER
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('210 Doi can, Ha Noi, Viet Nam','APL','Hang tau APL','Pacific Mail Steamship Company','25688','0','1232223','apl.com','3');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Bonn, Germany','DHL','DHL is an American-founded German courier, parcel, and express mail service.','DHL Express','11111','0','1232423','dhl.com','4');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('200 Quang Trung, Ha Dong, Ha Noi, Viet Nam','TAP','Xuat khau cac thiet bi dien may','Tan An Phat','25688','0','1232223','tapvn.com','5');
INSERT INTO crum_db.operator (user_id, is_root) VALUES ('1', 1);
INSERT INTO crum_db.operator (user_id, is_root) VALUES ('2', 0);
INSERT INTO crum_db.shipping_line (user_id) VALUES ('3');
INSERT INTO crum_db.forwarder (user_id) VALUES ('4');
INSERT INTO crum_db.merchant (user_id) VALUES ('5');
INSERT INTO crum_db.driver (driver_license,user_id,forwarder_id) VALUES ('292883943002','6','4');

#GEOLOCATION
INSERT INTO crum_db.geolocation(id,created_at,latitude,longitude,updated_at,user_id) VALUES (1,CURDATE(),'','',CURDATE(),6);

# PORT
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Số 1A Minh Khai, Hồng Bàng, Hải Phòng','Cảng Vụ Hàng hải Hải Phòng','HAIPHONGPORT',CURDATE(),CURDATE());

# CONTAINER TYPE
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('9.95','8ft Container Dimensions','1.95','2.11','2.06','2.29','2.11','8CD','6000','950','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('15.95','10ft Container Dimensions','2.28','2.34','2.39','2.84','2.35','10CD','10000','1000','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('33.2','20ft Container Dimensions','2.28','2.34','2.39','5.9','2.35','20CD','30480','2000','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.6','40ft Container Dimensions','2.28','2.34','2.39','12.03','2.35','40CD','30480','3470','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('32.8','20ft Tunnel Dimensions','2.28','2.34','2.39','5.84','2.35','20TD','30480','2180','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('31','20ft Open-sider Dimensions','2.19','2.22','2.30','5.9','2.29','20OSD','30480','3170','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('76.4','40ft High Cube Dimensions','2.58','2.34','2.69','12.03','2.35','40HCD','30480','3660','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('33','20ft Open-top Dimensions','2.28','2.34','2.28','5.96','2.35','20OTD','30480','3000','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('66.8','40ft Open-top Dimensions','2.28','2.34','2.39','12.03','2.35','40OTD','32500','4050','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.4','40ft Tunnel Dimensions','2.28','2.34','2.39','11.98','2.35','40TD','30480','3680','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('67.72','40ft High Cube Open Sider Dimensions','2.35','2.22','2.46','12.03','2.28','40HCOSD','24000','5920','KG',CURDATE(),CURDATE());
INSERT INTO crum_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,gross_weight,tare_weight,unit_of_measurement,created_at,updated_at) VALUES ('37.4','20ft High Cube Dimensions','2.58','2.34','2.69','5.96','2.35','20HCD','30480','2100','KG',CURDATE(),CURDATE());

# TRAILER & TRACTOR
INSERT INTO crum_db.vehicle(id,created_at,license_plate,number_of_axles,updated_at,user_id) VALUES (1,CURDATE(),'71C-4056',2,CURDATE(),4),(2,CURDATE(),'71C-4092',2,CURDATE(),4),(3,CURDATE(),'71C-2032',2,CURDATE(),4),(4,CURDATE(),'71C-5698',2,CURDATE(),4),(5,CURDATE(),'20A-4092',2,CURDATE(),4),(6,CURDATE(),'20A-6692',2,CURDATE(),4),(7,CURDATE(),'20A-5032',2,CURDATE(),4),(8,CURDATE(),'20A-3214',2,CURDATE(),4);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',2);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',3);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',4);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',5);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (1);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (6);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (7);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (8);

# SHIPPINGLINE :
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'wanhai@crm.com','Joseph Lin','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','8211297','ACTIVE',CURDATE(),'wanhai','194 Nguyễn Công Trứ, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'saigonship@saigonshipvn.com','Morten Bruehl','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38296320','ACTIVE',CURDATE(),'saigonship','Lầu 7, Landmark Building, 5B Tôn Đức Thắng, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vinatrans.mngt@vinatrans.com.vn','David Chew','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39414919','ACTIVE',CURDATE(),'vinatrans','406 Nguyễn Tất Thành ,Phường 18,Quận 4,TP.Hồ Chí Minh');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'phuc-ct@vietfracht-hcm.com','Y.S.Chung','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','3811793','ACTIVE',CURDATE(),'vietfracht','Lầu 1, Saigon Port Building, 3 Nguyễn Tất Thành, Quận 4');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'sgncs@apl.com','Ong Toi Kin','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39331250','ACTIVE',CURDATE(),'sgncs','lầu 8, Diamond plaza, 34 Lê Duẩn, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'heung-a@vietfracht-hcm.com','Jae Seon Hong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38210806','ACTIVE',CURDATE(),'heunga','lầu 2, 11 Nguyễn Công Trứ, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'thuan-pham@evergreen-shipping.com.vn','Daniel Wu','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39111026','ACTIVE',CURDATE(),'evergreen','54 Nguyễn Đình Chiểu, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@viconship.com','Trần Hòa Bình','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','3836705','ACTIVE',CURDATE(),'viconship','Số 11 – Võ Thị Sáu – Ngô Quyền - Hải Phòng.');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'ponl@vitranschart.com','Michael Dam','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39404271','ACTIVE',CURDATE(),'vitranschart','428 Nguyễn Tất Thành, P.18, Q.4, TP.HCM, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'pal@hcm.vnn.vn','Jimmy Wong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','9141102','ACTIVE',CURDATE(),'samudera','16 Phó Đức Chính, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'operations@continentalshipping.com','Jose O. Busto','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','9141102','ACTIVE',CURDATE(),'continentalshipping','phòng 810, lầu 8, Sunwah Tower Building, 115 Nguyễn Huệ, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info.sgn@safi.com.vn','Bui Xuan Bach','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38232187','ACTIVE',CURDATE(),'shippingsafi','lầu 12, 37 Tôn Đức Thắng, Quận 1, TP. HCM');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vosagroup@vosagroup.com','Pham Manh Cuong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','54161820','ACTIVE',CURDATE(),'vosagroup','lầu 2, Vosa Building, 7 Nguyễn Huệ, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'agency@vitamas.com.vn','Nguyễn Hồng Trung','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39402382','ACTIVE',CURDATE(),'vitamas','Số 44 - 46 Nguyễn Tất Thành,Phường 12, Quận 4, Thành Phố Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@maersksealand.com.vn','Peter Berendsen Svarrer','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38238566','ACTIVE',CURDATE(),'maersk','Zen Plaza, 54-56 Nguyễn Trãi, Quận 1, 700000 Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'headoffice@vinalinklogistics.com','Nguyễn Nam Tiến','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39919259','ACTIVE',CURDATE(),'vinalink','226/2 Lê Văn Sỹ, Phường 1, Quận Tân Bình, TP Hồ Chí Minh');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@transimex.com.vn','Trần Hồng Đởm','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','22202888','ACTIVE',CURDATE(),'transimek','172 (Lầu 9-10) Hai Bà Trưng, Phường Đakao, Quận 1, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'office@hlcargo.com','Su Han Kim','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','54107108','ACTIVE',CURDATE(),'hlcargo','Tầng 5 Thiên Sơn Plaza - 800 Nguyễn Văn Linh, Phường Tân Phú, Quận 7, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'cs.hq@coscon.com','Yang Zenglian','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','35124888','ACTIVE',CURDATE(),'cosco','lầu 1, 47 Phó Đức Chính, Quận 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'cs.hq@cosfi.com','Vũ Văn Trực','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38238172','ACTIVE',CURDATE(),'cosfi','209 Nguyễn Văn Thủ, Phường Đa Kao, Quận 1, TP Hồ Chí Minh');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@sunnytrans.com.vn','Bùi Quốc Hùng','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39402741','ACTIVE',CURDATE(),'sunnytrans','146 Khánh Hội, Phường 6, Quận 4, TP.Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'mngt@vinafreight.com.vn','Nguyễn Bích Lân','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38446409','ACTIVE',CURDATE(),'vinafreight','Lầu 1, Block C, Tòa nhà văn phòng WASECO, số 10 Phổ Quang, Phường 2, Quận Tân Bình, Tp.HCM, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vnm-info@msc.com','Hòang Trọng Giang','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','36363939','ACTIVE',CURDATE(),'mschcm','Tầng 27, Tòa nhà Etown Central 11 Đoàn Văn Bơ, Quận 4 - TP HỒ CHÍ MINH');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'bkghcmnvo@oocl.com','Albert Chen','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39116088','ACTIVE',CURDATE(),'ooclvn','Lầu 13, Trung tâm Thương mại Sài Gòn 37 Tôn Đức Thắng, Quận 1, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vtb.agent-hcm@vinafco.com.vn','Vũ Hòang Bảo','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38268770','ACTIVE',CURDATE(),'vinafco','Số 53-55, Đường 41, Phường 6, Quận. 4 - Thành phố Hồ Chí Minh');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vnl@vinalines.com.vn','Lê Anh Sơn','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39973818','ACTIVE',CURDATE(),'shippinglines','Số 163, Nguyễn Văn Trỗi, Phường 11, Quận Phú Nhuận, HCM');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'infovnw@vinashinnewworld.vn','Ngô Duy Thạch','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','39118646','ACTIVE',CURDATE(),'vinashin','6/16 Nguyễn Văn Thủ, P. Đa Kao, Q. 1, Tp. 1');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'mary@vietnam-ship.com','Mary Phuong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','8375568','ACTIVE',CURDATE(),'vietnamship','396 Trường Sa, Phường 02, Quận Phú Nhuận, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'kevin.bach@asw-vietnam.com','Kevin Bach','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','2969858','ACTIVE',CURDATE(),'airseaworldvn','50 Cửu Long, Phường 2, Quận Tân Bình, TP.HCM, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'povn-hcm@philiorient.com.vn','Phillip Thuan','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','38215637','ACTIVE',CURDATE(),'philiorienthcm','153 Nguyễn Tất Thành, Quận 4, Thành phố Hồ Chí Minh, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'mtdao@safi.com.vn','Nguyen Hoang Dung','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','9745575','ACTIVE',CURDATE(),'seasafihn','Số 51, Lê Đại Hành, Q. Hai Bà Trưng, Hà Nội, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'giangthuyco@hn.vnn.vn','Nguyen Thanh Thuy','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','3852372','ACTIVE',CURDATE(),'giangthuy','11/51 Đông Khê, Tp Hải Phòng, Việt Nam');

INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('194 Nguyễn Công Trứ, Quận 1','WLL','Hang tau WLL','WANHAI LINES LTD','8243219','0','8251883','www.wanhai.com','7');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 7, Landmark Building, 5B Tôn Đức Thắng, Quận 1','ASSC','Hang tau ASSC','SAIGON SHIPPING CO.LTD','8230013','0','8230014','www.apmss.com','8');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('406 Nguyễn Tất Thành ,Phường 18,Quận 4,TP.Hồ Chí Minh','VINATRANS','Công ty Giao Nhận Kho Vận Ngoại Thương VINATRANS','VINATRANS','8259561','0','8259560','www.vinatrans.com','9');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 1, Saigon Port Building, 3 Nguyễn Tất Thành, Quận 4','VIETFRACHT','Công ty cổ phần Vận tải và Thuê tàu','VIETFRACHT','8267442','0','8267438','vietfracht.vn','10');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 8, Diamond plaza, 34 Lê Duẩn, Quận 1','APLS','Hãng tàu APL','APL','8227880','0','8227881','www.apl.com','11');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 2, 11 Nguyễn Công Trứ, Quận 1','HEUNGA','Hãng Tàu Heung-A Shipping','HEUNG A','38211050','0','38210810','www.heunga-co.kr','12');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('54 Nguyễn Đình Chiểu, Quận 1','EVERGREEN','Hãng Tàu Evergreen Line','Evergreen Line','9103527','0','9103525','www.evergreen-marine.com','13');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 11 – Võ Thị Sáu – Ngô Quyền - Hải Phòng.','VICONSHIP','Công ty cổ phần Container Việt Nam','VICONSHIP','3836105','0','3836104','www.viconship.com','14');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('428 Nguyễn Tất Thành, P.18, Q.4, TP.HCM, Việt Nam','VCJSC','CÔNG TY CỔ PHẦN VẬN TẢI VÀ THUÊ TÀU BIỂN VIỆT NAM','VITRANSCHART JSC','39404712','0','39404711','www.viconship.com','15');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('16 Phó Đức Chính, Quận 1','SAMUDERA','Hãng Tàu Heung-A Shipping Samudera Shipping Line','Samudera Shipping Line','9141105','0','9141104','ssl.samudera.id','16');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('phòng 810, lầu 8, Sunwah Tower Building, 115 Nguyễn Huệ, Quận 1','CSAC','CÔNG TY CỔ PHẦN ĐẠI LÝ VẬN CHUYỂN CONTINENTAL SHIPPING','CONTINENTAL SHIPPING AGENCY CORP','8219349','0','8219259','www.continentalshipping.com','17');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 12, 37 Tôn Đức Thắng, Quận 1, TP. HCM','SAFI','CTCP Đại lý Vận tải SAFI','SAFI','8253610','0','8254371','www.safi.com.vn','18');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 2, Vosa Building, 7 Nguyễn Huệ, Quận 1','VOSASG','VOSA CORPORATION ex VOSA GROUP OF COMPANIES','VOSA CORPORATION','8212799','0','8212795','www.vosa.com.vn','19');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 44 - 46 Nguyễn Tất Thành,Phường 12, Quận 4, Thành Phố Hồ Chí Minh, Việt Nam','VITAMAS','Đại lý Thương Mại và Dịch Vụ Hàng hải','VITAMAS','8235614','0','8235618','www.nykline.com','20');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Zen Plaza, 54-56 Nguyễn Trãi, Quận 1, 700000 Hồ Chí Minh, Việt Nam','MAERSK','Tập đoàn A.P. Moller-Maersk','MAERSK SEALAND','8231398','0','8231395','www.maersk.com','21');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('226/2 Lê Văn Sỹ, Phường 1, Quận Tân Bình, TP Hồ Chí Minh','VINALINK','Công Ty Cổ Phần Logistics Vinalink','VINALINK LOGISTICS','8255340','0','8255342','vina-link.com.vn','22');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('172 (Lầu 9-10) Hai Bà Trưng, Phường Đakao, Quận 1, Thành phố Hồ Chí Minh, Việt Nam','TRANSIMEX','Công Ty Cổ Phần Transimex','TRANSIMEX SG, I.T.L.','8228566','0','8296011','transimex.com.vn','23');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Tầng 5 Thiên Sơn Plaza - 800 Nguyễn Văn Linh, Phường Tân Phú, Quận 7, Thành phố Hồ Chí Minh, Việt Nam','HLCARGO','CÔNG TY TNHH HL CARGO','HL CARGO','8451582','0','9406328','www.hlcargo.com','24');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('lầu 1, 47 Phó Đức Chính, Quận 1, Thành phố Hồ Chí Minh, Việt Nam','COSCO','HÃNG TÀU COSCO','COSCO SHIPPING LINES CO.,LTD','3552776','0','38238391','lines.coscoshipping.com','25');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('209 Nguyễn Văn Thủ, Phường Đa Kao, Quận 1, TP Hồ Chí Minh','COSFI','CÔNG TY TNHH ĐẠI LÝ VẬN TẢI COSFI','COSFI','84838238','0','84838232','www.cosfivn.com','26');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('146 Khánh Hội, Phường 6, Quận 4, TP.Hồ Chí Minh, Việt Nam','SUNNYTRANS','Công ty TNHH Vận tải Sunny','SUNNY TRANSPORTATION CO.,LTD','9402740','0','9402734','sunnytrans.com.vn','27');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 1, Block C, Tòa nhà văn phòng WASECO, số 10 Phổ Quang, Phường 2, Quận Tân Bình, Tp.HCM, Việt Nam','VNFREIGHT','Công ty Cổ phần VINAFREIGHT','VINAFREIGHT','8336409','0','8326401','www.vinafreight.com','28');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Tầng 27, Tòa nhà Etown Central 11 Đoàn Văn Bơ, Quận 4 - TP HỒ CHÍ MINH','MSC','CÔNG TY ĐẠI LÝ MSC','MSC','8276216','0','8277216','www.msc.com','29');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 13, Trung tâm Thương mại Sài Gòn 37 Tôn Đức Thắng, Quận 1, Thành phố Hồ Chí Minh, Việt Nam','OOCL','HÃNG TÀU OOCL','OOCL','8242522','0','8242523','www.oocl.com','30');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 53-55, Đường 41, Phường 6, Quận. 4 - Thành phố Hồ Chí Minh','VSC','CÔNG TY CỔ PHẦN VẬN TẢI BIỂN VINAFCO','VINAFCO SHIPPING COMPANY','3550603','0','37686820','vinafcoship.com.vn','31');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 163, Nguyễn Văn Trỗi, Phường 11, Quận Phú Nhuận, HCM','VINALINES','TỔNG CÔNG TY HÀNG HẢI VIỆT NAM','VINALINES','5770860','0','5770850','www.vinalines.com.vn','32');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('6/16 Nguyễn Văn Thủ, P. Đa Kao, Q. 1, Tp. 1','VNWLJSC','CTCP Logistics Tân Thế Giới Vinashin','VINASHIN NEW WORLD LOGISTIC JOINT STOCK COMPANY','877634','0','877638','www.vinashinnewworld.com','33');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('396 Trường Sa, Phường 02, Quận Phú Nhuận, Thành phố Hồ Chí Minh, Việt Nam','VSSC','CÔNG TY CỔ PHẦN DỊCH VỤ HÀNG HẢI VIẾT NAM','VIETNAM SHIPPING SERVICE CORPORATION','8375580','0','8375558','www.vietnam-ship.com','34');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('50 Cửu Long, Phường 2, Quận Tân Bình, TP.HCM, Việt Nam','ASWVL','Công Ty TNHH Logistics Hàng Không Biển Toàn Cầu Việt Nam','Air Sea Worldwide Vietnam Ltd','8421504','0','8421503','www.airseaworldwide.com','35');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('153 Nguyễn Tất Thành, Quận 4, Thành phố Hồ Chí Minh, Việt Nam','POVJ','Công ty cổ phần kinh doanh vận tải hàng hóa quốc tế Phili Orient Vietnam JSC','PHILI ORIENT VIETNAM JSC','9404378','0','9404370','www.philiorientlines.com','36');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 51, Lê Đại Hành, Q. Hai Bà Trưng, Hà Nội, Việt Nam','SAFIH','SEA & AIR FREIGHT INTERNATIONAL HANOI','SEA & AIR FREIGHT INTERNATIONAL HANOI','9745581','0','9745580','www.safi.com.vn','37');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('11/51 Đông Khê, Tp Hải Phòng, Việt Nam','GTSCL','CÔNG TY TNHH THƯƠNG MẠI-SẢN XUẤT VÀ VẬN TẢI BIỂN GIANG THUỶ','GIANG THUY Shipping and Manufacturing Trading Co., Ltd','3852787','0','3852788','www.giangthuyshipping.com','38');

INSERT INTO crum_db.shipping_line (user_id) VALUES ('7');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('8');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('9');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('10');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('11');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('12');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('13');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('14');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('15');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('16');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('17');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('18');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('19');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('20');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('21');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('22');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('23');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('24');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('25');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('26');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('27');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('28');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('29');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('30');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('31');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('32');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('33');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('34');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('35');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('36');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('37');
INSERT INTO crum_db.shipping_line (user_id) VALUES ('38');

INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('7','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('8','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('9','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('10','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('11','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('12','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('13','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('14','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('15','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('16','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('17','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('18','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('19','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('20','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('21','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('22','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('23','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('24','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('25','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('26','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('27','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('28','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('29','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('30','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('31','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('32','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('33','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('34','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('35','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('36','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('37','3');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('38','3');

# PORT
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('604 Đường Lý Thường Kiệt, Cửa Ông, Cẩm Phả, Quảng Ninh','CÔNG TY KHO VẬN VÀ CẢNG CẨM PHẢ - VINACOMIN','CAMPHAPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('6A Lê Thánh Tông, Hồng Gai, Thành phố Hạ Long, Quảng Ninh','Cảng biển Hòn Gai','HONGAIPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('xã Nghi Sơn, thị xã Nghi Sơn, tỉnh Thanh Hoá, Việt Nam','Cảng biển Nghi Sơn','NGHISONPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Nghi Thuỷ, Tx. Cửa Lò, Nghệ An','Cảng biển Cửa Lò','CUALOPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Khu kinh tế Vũng Áng, xã Kỳ Lợi, thị xã Kỳ Anh, tỉnh Hà Tĩnh','Cảng biển Vũng Áng','VUNGANGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('130 Nguyễn Huệ, Phú Nhuận, Thành phố Huế, Thừa Thiên Huế','Cảng biển Chân Mây','CHANMAYPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('26 Bạch Đằng, Thạch Thang, Hải Châu, Đà Nẵng 550000','Cảng biển Đà Nẵng','DANANGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Đường Không Tên, Bình Thuận, Bình Sơn, Quảng Ngãi','Cảng biển Dung Quất','DUNGQUATPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('02 Phan Chu Trinh - Phường Hải Cảng - TP. Quy Nhơn - Tỉnh Bình Định','Cảng biển Quy Nhơn','QUYNHONPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('vịnh Vân Phong, khu kinh tế Vân Phong tỉnh Khánh Hòa, Việt Nam','Cảng biển Vân Phong','VANPHONGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('03 Trần Phú, Cầu Đá, Thành phố Nha Trang, Khánh Hòa','Cảng biển Nha Trang','NHATRANGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('vịnh Cam Ranh thuộc tỉnh Khánh Hòa','Cảng biển Cam Ranh','CAMRANHPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('3 Nguyễn Tất Thành, phường 12, quận 4, TP. Hồ Chí Minh','Cảng biển Sài Gòn','SAIGONPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('2 Quang Trung, Phường 1, Thành phố Vũng Tầu, Bà Rịa - Vũng Tàu','Cảng biển Vũng Tàu','VUNGTAUPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('1B Đường D3, Long Bình Tân, Thành phố Biên Hòa, Đồng Nai','Cảng biển Đồng Nai','DONGNAIPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Cụm cảng cái cui Cần thơ, Cần Thơ 870000','Cảng biển Cái Cui','CAICUIPORT',CURDATE(),CURDATE());

INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Khu Mũi Chùa - Tiên Lãng Huyện Tiên Yên, Tiên lãng, Tiên Yên, Quảng Ninh','Cảng biển Mũi Chùa','MUICHUAPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('QL39A, Thái Thượng, Thái Thụy, Thái Bình','Cảng biển Diêm Điền','DIEMDIENPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị trấn Thịnh Long - Huyện Hải Hậu - Tỉnh Nam Định','Cảng biển Nam Định','NAMDINHPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Phường Quảng Hưng, TP Thanh Hóa, Thanh Hóa','Cảng biển Lệ Môn','LEMONPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị xã Cửa Lò, tỉnh Nghệ An','Cảng biển Bến Thủy','BENTHUYPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Kỳ lợi - Kỳ Anh - Hà Tĩnh','Cảng biển Xuân Hải','XUANHAIPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bắc Trạch- Bố Trạch- Quảng Bình','Cảng biển xăng dầu Sông Gianh','SONGGIANHPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Quảng Trạch- Quảng Bình','Cảng biển Hòn La','HONLAPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bắc Trạch- Bố Trạch- Quảng Bình','Cảng biển Thắng Lợi','THANGLOIPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thanh Trạch- Bố Trạch- Quảng Bình','Cảng biển Gianh','GIANHPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị trấn Cửa Việt, Huyện Gio Linh, Quảng Trị','Cảng biển Cửa Việt','CUAVIETPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thị trấn Thuận An, huyện Phú Vang, Thuận An, Phú Vang, Thừa Thiên Huế','Cảng biển Thuận An','THUANANPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Kỳ Hà, Núi Thành, Quảng Nam','Cảng biển Kỳ Hà','KYHAPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Thôn Định Tân, xã Bình Châu, huyện Lý Sơn, tỉnh Quảng Ngãi','Cảng biển Sa Kỳ','SAKYPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Vũng Rô, Đông Hòa, Khánh Hòa','Cảng biển Vũng Rô','VUNGROPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Giai đoạn 1A ( Xã Phước Diêm, huyện Thuận Nam, tỉnh Ninh Thuận)','Cảng biển Cà Ná','CANAPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Xã Tam Thanh, huyện Phú Quý, tỉnh Bình Thuận','Cảng biển Phú Quý','PHUQUYPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('QL1A, Bình An, Dĩ An, Bình Dương','Cảng biển Bình Dương','BINHDUONGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bờ phải sông Tiền, phường 11, thành phố Cao Lãnh, tỉnh Đồng Tháp','Cảng biển Đồng Tháp','DONGTHAPPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Mỹ Thạnh, Tp. Long Xuyên, An Giang','Cảng biển Mỹ Thới','MYTHOIPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('170/2 Phạm Hùng, phường 9, thị xã Vĩnh Long, Phường 9, Vĩnh Long','Cảng biển Vĩnh Long','VINHLONGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Bình Đức, Thành phố Mỹ Tho, Tiền Giang','Cảng biển Mỹ Tho','MYTHOPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Khóm Hàng Vịnh, Thị trấn Năm Căn, Huyện Năm Căn, Tỉnh Cà Mau','Cảng biển Năm Căn','NAMCANPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('tx. Kiên Lương, tỉnh Kiên Giang','Cảng biển Hòn Chông','HONCHONGPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Huyện Kiên Lương-Kiên giang','Cảng biển Bình Trị','BINHTRIPORT',CURDATE(),CURDATE());
INSERT INTO crum_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Côn Đảo, Bà Rịa - Vũng Tàu','Cảng biển Côn Đảo','CONDAOPORT',CURDATE(),CURDATE());

# MERCHANT
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'minhkq@crm.com','Khổng Quang Minh','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0962333456','ACTIVE',CURDATE(),'minhkq','Khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'duongnd@crm.com','Nguyễn Đức Dương','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','054232478','ACTIVE',CURDATE(),'duongnd','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'quyennv@crm.com','Nguyễn Văn Quyền','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0732533498','ACTIVE',CURDATE(),'quyennv','Khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');

# FORWARDER
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'annv@crm.com','Nguyễn Văn An','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0912537496','ACTIVE',CURDATE(),'annv','Khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'duydl@crm.com','Đào Lương Duy','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0914322787','ACTIVE',CURDATE(),'duydl','Khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'phuongnm@crm.com','Nguyễn Mai Phương','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0912755636','ACTIVE',CURDATE(),'phuongnm','Ba Dinh, Ha Noi, Vietnam');

INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','ADD','Công ty TNHH ADD','ADD Company','2231233','0','3342211','minhkq.com','39');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','AUD','Công ty TNHH AUD','AUD Company','2231234','0','3352211','duongnd.com','40');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','AND','Công ty TNHH AND','AND Company','2231235','0','3362211','quyennv.com','41');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','AGD','Công ty TNHH AGD','AGD Company','2231236','0','3372211','annv.com','42');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','ULD','Công ty TNHH ULD','ULD Company','2231237','0','3382211','duydl.com','43');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('khu công nghệ, Thạch Hoà, Thạch Thất, Hà Nội','MNG','Công ty TNHH MNG','MNG Company','2231239','0','3392211','phuongnm.com','44');

INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('39','5');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('40','5');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('41','5');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('42','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('43','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('44','4');

INSERT INTO crum_db.merchant (user_id) VALUES ('39');
INSERT INTO crum_db.merchant (user_id) VALUES ('40');
INSERT INTO crum_db.merchant (user_id) VALUES ('41');

INSERT INTO crum_db.forwarder (user_id) VALUES ('42');
INSERT INTO crum_db.forwarder (user_id) VALUES ('43');
INSERT INTO crum_db.forwarder (user_id) VALUES ('44');

# DRIVER
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'duc@crm.com','Nguyen Minh Duc','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0933458222','ACTIVE',CURDATE(),'ducmn','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'cuong@crm.com','Nguyen Duc Cuong','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0912323096','ACTIVE',CURDATE(),'cuongnd','Ba Dinh, Ha Noi, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'quy@crm.com','Tran Van Quy','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0167322096','ACTIVE',CURDATE(),'quyvt','Ba Dinh, Ha Noi, Vietnam');

INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('45','6');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('46','6');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('47','6');

INSERT INTO crum_db.driver (driver_license,user_id,forwarder_id) VALUES ('801170018870','45','4');
INSERT INTO crum_db.driver (driver_license,user_id,forwarder_id) VALUES ('791170009970','46','4');
INSERT INTO crum_db.driver (driver_license,user_id,forwarder_id) VALUES ('493270009986','47','4');

# TRAILER & TRACTOR
INSERT INTO crum_db.vehicle(id,created_at,license_plate,number_of_axles,updated_at,user_id) VALUES (9,CURDATE(),'20A-4556',2,CURDATE(),4),(10,CURDATE(),'20C-4092',2,CURDATE(),4),(11,CURDATE(),'20C-2032',2,CURDATE(),4),(12,CURDATE(),'20C-5698',2,CURDATE(),4),(13,CURDATE(),'20A-5192',2,CURDATE(),4),(14,CURDATE(),'20A-6092',2,CURDATE(),4);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',9);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',11);
INSERT INTO crum_db.container_semi_trailer(type,unit_of_measurement,vehicle_id) VALUES ('T36','FT',13);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (10);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (12);
INSERT INTO crum_db.container_tractor(vehicle_id) VALUES (14);

#GEOLOCATION
INSERT INTO crum_db.geolocation(id,created_at,latitude,longitude,updated_at,user_id) VALUES (2,CURDATE(),'','',CURDATE(),45);
INSERT INTO crum_db.geolocation(id,created_at,latitude,longitude,updated_at,user_id) VALUES (3,CURDATE(),'','',CURDATE(),46);
INSERT INTO crum_db.geolocation(id,created_at,latitude,longitude,updated_at,user_id) VALUES (4,CURDATE(),'','',CURDATE(),47);

# SHIPPINGLINE :
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'sonlh@360logistics.vn','Mr Sơn','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0982754075','ACTIVE',CURDATE(),'360logs','Số 2-2/17 Phố Định Công Thượng, P Định Công, Q Hoàng Mai, Tp HN');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'phuonglr77@gmail.com','Mr. Phương','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0902042066','ACTIVE',CURDATE(),'phuonglr77','Số 77 Lê Thánh Tông, Phường Máy Tơ, Quận Ngô Quyền, Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'lechan.vantai1@gmail.com','Ms Hường','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0936888060','ACTIVE',CURDATE(),'lechan','Lô CN 21, KCN MP Đình Vũ, P Đông Hải 2, Quận Hải An, TP HP');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'nhatlta@hopnhat.vn','Ms Nhật','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0985989428','ACTIVE',CURDATE(),'nhatlta','Tầng 2, Tòa nhà CT4, Vimeco 2, Phố Nguyễn Chánh, Phường Trung Hòa, Quận Cầu Giấy, Thành phố Hà Nội');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'trucking@omegahanoi.com','Mr Học','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0933368880','ACTIVE',CURDATE(),'omegahanoi','Phòng 16A, tầng 16 tòa nhà HL TOWER lô A2B, đường Duy Tân, p Dịch Vọng Hậu, Q Cầu giấy, HN');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'sales3@voti.com.vn','Ms.Tiên','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0963604867','ACTIVE',CURDATE(),'sales3','Lầu 2 số 316 Lê Văn Sỹ, Phường 1 Quận Tân Bình TP HCM');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'tham@binhminhjschp.com','Ms Thắm','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0906199109','ACTIVE',CURDATE(),'binhminh','Phòng 528, tầng 5, số 452 Lê Thánh Tông, P. Vạn Mỹ,Q. Ngô Quyền, Tp. Hải phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'hanhms@gmail.com','Ms Hạnh','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0904483966','ACTIVE',CURDATE(),'tasa','Số 189 đường đi Đình Vũ, phường Đông Hải 2, quận Hải An, Tp. Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'minhanlc@gmail.com','Mr Thuận','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0969982268','ACTIVE',CURDATE(),'minhanlc','SN 025, tổ 3, Đ. Triệu Quang Phục, P. Phố Mới, TP Lào Cai, Tỉnh Lào Cai');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'dung@voti.com.vn','Nguyễn Chí Dũng','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0902207667','ACTIVE',CURDATE(),'chidung','Số 58 Trần Khánh Dư, Phường Máy Tơ, quận Ngô Quyền, Thành phố Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'vantaitrongthanh.bn@gmail.com','Mr. Thể','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0914774859','ACTIVE',CURDATE(),'trongthanh','Thôn Đông Phù, Xã Phú Lâm, Huyện Tiên Du, Phú Lâm, Tiên Du, Bắc Ninh');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'lengocanh@vanlanjsc.com','Lê Anh Tuấn','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0904346712','ACTIVE',CURDATE(),'vanlan','Khách sạn Dầu Khí mới, Số 441, Đông Hải 1, Hải An, Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'thuyptt@gmail.com','Phạm Thị Thanh Thuý','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0318830697','ACTIVE',CURDATE(),'ngoisaoxanh','441 Đường Đông Hải, Quận Hải An, Hải Phòng, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'hadesigner88@gmail.com','Ngô Thu Hà','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0916583399','ACTIVE',CURDATE(),'thaiduong','69 BT15 Bùi Thị Xuân, ĐVõ Cường, TP. Bắc Ninh,Bắc Ninh');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'saobien@seastarvietnam.com','Nguyễn Văn Đàm','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0983320199','ACTIVE',CURDATE(),'saobien','Số 57 Máy Tơ, Phường Máy Chai, Quận Ngô Quyền, Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'khuongdh@gmail.com','Đinh Hữu Khương','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0225263965','ACTIVE',CURDATE(),'dongdo','Số 21 Võ Thị Sáu, Phường Máy Tơ, Quận Ngô Quyền, Hải Phòng, Vietnam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'hoanvh@gmail.com','Nguyễn Văn Hoàn','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0313540220','ACTIVE',CURDATE(),'hsq','Số 39/73 Lương Khánh Thiện, Phường Lương Khánh Thiện, Quận Ngô Quyền, Thành phố Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'duyen@minhtrung.com.vn','Trần Thị Hồng Duyên','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0984762666','ACTIVE',CURDATE(),'minhtrung','Số 138 Lê Lai, Má Chai, Ngô Quyền, Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'info@glotransvn.com.vn','Trần Anh Giang','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0225355291','ACTIVE',CURDATE(),'glotransvn','Phòng 16, Tầng 3, Toà nhà Thành Đạt, Số 3 Lê Thánh Tông, Quận Ngô Quyền, Thành phố Hải Phòng, Việt Nam');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'andaiphathp@gmail.com','Hứa Đức Duy','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0904229818','ACTIVE',CURDATE(),'andaiphat','Số 1A/47 Phương Lưu, Phường Đông Hải 1, Quận Hải An, Thành phố Hải Phòng');
INSERT INTO crum_db.user (created_at,email,fullname,password,phone,status,updated_at,username,address) VALUES (CURDATE(),'finance.tunglongtraco.hp@gmail.com','Trần Trung Tùng','$2a$10$fKNzue5vXDqZWgzxtuIDWuozd30wiGRXfkzEgnFeUc6MKAIrIyG4i','0167641014','ACTIVE',CURDATE(),'tunglong','Thôn Cái Tắt, Xã An Đồng, Huyện An Dương, Hải Phòng');

INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 2-2/17 Phố Định Công Thượng, P Định Công, Q Hoàng Mai, Tp HN','360LOGS','Công ty cổ phần Vận tải quốc tế 360 độ Logistics','360LOGS','0105161861','0','0105161862','www.360logistics.com','48');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 77 Lê Thánh Tông, Phường Máy Tơ, Quận Ngô Quyền, Hải Phòng','VT3','Công ty TNHH Vận tải và dịch vụ VT3','VT3AP01','0200685167','0','0200685168','www.vinalines.com','49');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lô CN 21, KCN MP Đình Vũ, P Đông Hải 2, Quận Hải An, TP HP','LECHAN','Công ty TNHH Lê Chân','LECHAN01','0200276398','0','0200276397','www.lechanvantai1.com','50');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Tầng 2, Tòa nhà CT4, Vimeco 2, Phố Nguyễn Chánh, Phường Trung Hòa, Quận Cầu Giấy, Thành phố Hà Nội','HNC','Công ty cổ phần Hợp Nhất Quốc Tế','HNC01','0305141894','0','0305141895','www.hnc.com','51');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Phòng 16A, tầng 16 tòa nhà HL TOWER lô A2B, đường Duy Tân, p Dịch Vọng Hậu, Q Cầu giấy, HN','OMEGAHN','Công ty cổ phần OMEGA Hà Nội','OMEGAHN','0106841162','0','0106841161','www.omegahanoi.com','52');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Lầu 2 số 316 Lê Văn Sỹ, Phường 1 Quận Tân Bình TP HCM','VIETOCEAN','Công ty TNHH TM và DV Vận tải quốc tế Đại Dương Việt','VIETOCEAN','0312626137','0','0312626138','www.sales3.com','53');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Phòng 528, tầng 5, số 452 Lê Thánh Tông, P. Vạn Mỹ,Q. Ngô Quyền, Tp. Hải phòng','BINHMINH','Công ty cổ phần Đầu tư và phát triển hàng hải Bình Minh','BINHMINH','0201332856','0','0201332855','www.binhminh.com','54');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 189 đường đi Đình Vũ, phường Đông Hải 2, quận Hải An, Tp. Hải Phòng','TASA','Công ty TNHH Vận tải Đường bộ Duyên Hải','TASA','0200663942','0','0200663943','www.tasa.com','55');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('SN 025, tổ 3, Đ. Triệu Quang Phục, P. Phố Mới, TP Lào Cai, Tỉnh Lào Cai','MINHAN','Công ty TNHH Một thành viên vận tải Minh An','MINHAN','5300713843','0','5300713844','www.minhhan.com','56');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 58 Trần Khánh Dư, Phường Máy Tơ, quận Ngô Quyền, Thành phố Hải Phòng','SAOHP','Công ty TNHH Sao Hải Phòng','SAOHP','0200898246','0','0200898247','','57');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Thôn Đông Phù, Xã Phú Lâm, Huyện Tiên Du, Phú Lâm, Tiên Du, Bắc Ninh','TRONGTHANH','Công ty TNHH Thương mại điện tử và vận tải Trọng Thành','TRONGTHANH','02413734616','0','02413734616','www.trongthanh.com.vn','58');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Khách sạn Dầu Khí mới, Số 441, Đông Hải 1, Hải An, Hải Phòng','VANLAN','Công ty Cổ Phần Vân Lan','VANLAN','0934498167','0','0934498168','','59');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('441 Đường Đông Hải, Quận Hải An, Hải Phòng, Việt Nam','NSX','Công ty TNHH Dịch vụ vận tải Ngôi Sao Xanh','NGOI SAO XANH TRACO., LTD','0201714742','0','0201714741','','60');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('69 BT15 Bùi Thị Xuân, ĐVõ Cường, TP. Bắc Ninh,Bắc Ninh','THAIDUONG','Công Ty TNHH Tiếp Vận Thái Dương','THAIDUONG','02223811716','0','02223811794','','61');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 57 Máy Tơ, Phường Máy Chai, Quận Ngô Quyền, Hải Phòng','SAOBIEN','Công ty cổ phần Thương mại và cung ứng Sao Biển','SAOBIEN','0201148849','0','0201148848','','62');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 21 Võ Thị Sáu, Phường Máy Tơ, Quận Ngô Quyền, Hải Phòng, Vietnam','HHDONGDO','Chi nhánh Công ty Cổ phần Hàng hải Đông Đô tại Hải Phòng','HHDONGDO','0223552577','0','0223551792','','63');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 39/73 Lương Khánh Thiện, Phường Lương Khánh Thiện, Quận Ngô Quyền, Thành phố Hải Phòng','HSQ','Công ty TNHH Thương mại và vận tải HSQ','HSQ','5725660','0','5725661','','64');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 138 Lê Lai, Má Chai, Ngô Quyền, Hải Phòng','MINHTRUNG','Công ty TNHH TM và Giao nhận Minh Trung','MINHTRUNG','02253686725','0','02253686725','www.minhtrung.com.vn','65');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Phòng 16, Tầng 3, Toà nhà Thành Đạt, Số 3 Lê Thánh Tông, Quận Ngô Quyền, Thành phố Hải Phòng, Việt Nam','TOANCAU','Công Ty Tnhh Vận Tải Và Tiếp Vận Toàn Cầu','TOANCAU','842253836866','0','842253836866','glotransvn.com.vn','66');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Số 1A/47 Phương Lưu, Phường Đông Hải 1, Quận Hải An, Thành phố Hải Phòng','ANDAIPHAT','Công ty TNHH Đầu tư vận tải An Đại Phát','AN DAI PHAT TRANICO','0201808928','0','0201808929','','67');
INSERT INTO crum_db.supplier (company_address,company_code,company_description,company_name,fax,rating_value,tin,website,user_id) VALUES ('Thôn Cái Tắt, Xã An Đồng, Huyện An Dương, Hải Phòng','TUNGLONG','Công ty TNHH ĐT - TM vận tải Tùng Long','TUNGLONG','0200895318','0','0200895319','','68');

INSERT INTO crum_db.forwarder (user_id) VALUES ('48');
INSERT INTO crum_db.forwarder (user_id) VALUES ('49');
INSERT INTO crum_db.forwarder (user_id) VALUES ('50');
INSERT INTO crum_db.forwarder (user_id) VALUES ('51');
INSERT INTO crum_db.forwarder (user_id) VALUES ('52');
INSERT INTO crum_db.forwarder (user_id) VALUES ('53');
INSERT INTO crum_db.forwarder (user_id) VALUES ('54');
INSERT INTO crum_db.forwarder (user_id) VALUES ('55');
INSERT INTO crum_db.forwarder (user_id) VALUES ('56');
INSERT INTO crum_db.forwarder (user_id) VALUES ('57');
INSERT INTO crum_db.forwarder (user_id) VALUES ('58');
INSERT INTO crum_db.forwarder (user_id) VALUES ('59');
INSERT INTO crum_db.forwarder (user_id) VALUES ('60');
INSERT INTO crum_db.forwarder (user_id) VALUES ('61');
INSERT INTO crum_db.forwarder (user_id) VALUES ('62');
INSERT INTO crum_db.forwarder (user_id) VALUES ('63');
INSERT INTO crum_db.forwarder (user_id) VALUES ('64');
INSERT INTO crum_db.forwarder (user_id) VALUES ('65');
INSERT INTO crum_db.forwarder (user_id) VALUES ('66');
INSERT INTO crum_db.forwarder (user_id) VALUES ('67');
INSERT INTO crum_db.forwarder (user_id) VALUES ('68');

INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('48','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('49','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('50','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('51','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('52','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('53','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('54','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('55','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('56','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('57','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('58','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('59','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('60','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('61','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('62','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('63','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('64','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('65','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('66','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('67','4');
INSERT INTO crum_db.user_role (user_id,role_id) VALUES ('68','4');
