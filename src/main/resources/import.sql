# ADD ROLE
INSERT INTO crm_db.role (name, created_at, updated_at) VALUES ('ROLE_ADMIN',CURDATE(),CURDATE()),('ROLE_MODERATOR',CURDATE(),CURDATE()),('ROLE_SHIPPINGLINE',CURDATE(),CURDATE()),('ROLE_FORWARDER',CURDATE(),CURDATE()),('ROLE_MERCHANT',CURDATE(),CURDATE()),('ROLE_DRIVER',CURDATE(),CURDATE());


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
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('210 Doi can, Ha Noi, Viet Nam','APL','Hang tau APL','Pacific Mail Steamship Company','Nguyen Van A','25688','0.2','1232223','apl.com','3');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('Bonn, Germany','DHL','DHL is an American-founded German courier, parcel, and express mail service.','DHL Express','Ken Allen','11111','0.5','1232423','dhl.com','4');
INSERT INTO crm_db.supplier (company_address,company_code,company_description,company_name,contact_person,fax,rating_value,tin,website,user_id) VALUES ('200 Quang Trung, Ha Dong, Ha Noi, Viet Nam','TAP','Xuat khau cac thiet bi dien may','Tan An Phat','Nguyen Van A','25688','0.2','1232223','tapvn.com','5');
INSERT INTO crm_db.operator (user_id, fullname, is_root) VALUES ('1', 'Nguyen Van A', 1);
INSERT INTO crm_db.operator (user_id, fullname, is_root) VALUES ('2', 'Nguyen Van B', 0);
INSERT INTO crm_db.shipping_line (user_id) VALUES ('3');
INSERT INTO crm_db.forwarder (user_id) VALUES ('4');
INSERT INTO crm_db.merchant (user_id) VALUES ('5');
INSERT INTO crm_db.driver (driver_license,fullname,user_id,forwarder_id) VALUES ('292883943','Nguyen Van B','6','4');

# PORT
INSERT INTO crm_db.port (address,fullname,name_code,created_at,updated_at) VALUES ('Hai Phong, Viet Nam','Port Of Haiphong Joint Stock Company','HAIPHONGPORT',CURDATE(),CURDATE());

# CONTAINER TYPE
INSERT INTO crm_db.container_type(cubic_capacity,description,door_open_height,door_open_width,internal_height,internal_length,internal_width,name,payload_capacity,tare_weight,created_at,updated_at) VALUES ('67.72','40ft High Cube','2.35','2.22','2.46','12.03','2.28','40HC','24000','5920',CURDATE(),CURDATE());

# Outbound AND Inbound
INSERT INTO crm_db.supply VALUES (1,'2020-07-09 00:00:59','2020-07-09 00:00:59',1,3),(2,'2020-07-09 00:01:34','2020-07-09 00:02:17',1,3);
INSERT INTO crm_db.outbound VALUES ('2020-10-10 20:22:00','very good',1111,'DaNang city','2020-10-10 10:22:00','BIDDING','KG',2,5);
INSERT INTO crm_db.booking VALUES (1,'15351','2020-07-09 00:01:34','2020-11-10 20:22:00',_binary '',1,'2020-07-09 00:01:34',2,1);
INSERT INTO crm_db.vehicle VALUES (1,'2020-07-12 00:48:56','112222',2,'2020-07-12 00:48:56',4),(2,'2020-07-12 00:49:22','1122223',2,'2020-07-12 00:49:22',4);
INSERT INTO crm_db.container_semi_trailer VALUES ('T36','FT',2);
INSERT INTO crm_db.container_tractor VALUES (1);
INSERT INTO crm_db.bidding_document VALUES (1,'2020-08-08 23:22:00',2000000,'2020-07-09 00:02:17',5000000,'2020-07-09 00:02:17','VND',_binary '\0',4800000,'COMBINED','2020-07-09 00:03:30',NULL,5,2);
INSERT INTO crm_db.inbound VALUES ('2020-08-11 20:22:00','2020-08-10 20:22:00','Hai phong',1,4);
INSERT INTO crm_db.bill_of_lading VALUES (1,'C16251CGCS','2020-07-09 00:00:59','2020-12-10 20:22:00',1,'2020-07-09 00:00:59',1,1);
INSERT INTO crm_db.container VALUES (1,'124322CCC','2020-07-09 00:00:59','COMBINED','2020-07-09 00:03:30',1,6,1,2);

# Bidding
INSERT INTO crm_db.bid VALUES (1,'2020-07-09 00:03:16',4800000,'2020-07-09 01:03:16','2020-07-09 00:03:16','2020-07-09 00:03:30','ACCEPTED','2020-07-09 00:03:30',4,1);
INSERT INTO crm_db.bid_container VALUES (1,1);
INSERT INTO crm_db.notification VALUES (1,'2020-07-09 00:02:17',_binary '\0','You got a new Bidding Document from merchant','2020-07-09 00:02:17',NULL,'2020-07-09 00:02:17',4),(2,'2020-07-09 00:03:16',_binary '\0','You got a new Bid from forwarder','2020-07-09 00:03:16',NULL,'2020-07-09 00:03:16',5),(3,'2020-07-09 00:03:30',_binary '\0','Your Bid have ACCEPTED from merchant','2020-07-09 00:03:30',NULL,'2020-07-09 00:03:30',4),(4,'2020-07-09 00:03:30',_binary '\0','merchant and forwarder want to borrow 1 container from you','2020-07-09 00:03:30',NULL,'2020-07-09 00:03:30',3);
INSERT INTO crm_db.bidding_notification VALUES ('ADDED',1,1),('ADDED',2,1),('ACCEPTED',3,1),('ACCEPTED',4,1);

# Combined, Contract AND Evidence
INSERT INTO crm_db.combined VALUES (1,'2020-07-09 00:03:30','INFO_RECEIVED','2020-07-09 00:03:30',1);
INSERT INTO crm_db.contract VALUES (1,'2020-07-09 00:03:54',50,_binary '\0','2020-07-09 00:03:54',1);
INSERT INTO crm_db.evidence VALUES (1,'2020-07-09 00:04:04','chan qua',_binary '','2020-07-09 00:13:02',1,4);

# Payment
INSERT INTO crm_db.payment VALUES (1,5000000,'2020-07-09 00:04:14','Tao tra tien do',_binary '\0','2020-07-09 00:04:14','PAYMENT',1,4,4);
