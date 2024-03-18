INSERT INTO [dbo].[screens]
(
screen_name
)
VALUES
(
'Create Mapping'
),
(
'Mill Operations'
),
(
'Rotor Reset'
),
(
'View Details'
)
GO;

insert into roles
(role_name)
VALUES
('Maintenance'),
('Operations');

insert into users
(username,password,role_id,is_active)
VALUES
('D1234','D1234',2,'Y');
insert into users
(username,password,role_id,is_active)
VALUES
('36844','36844',2,'Y');

insert into permissions
(role_id,screen_id,permission_type)
VALUES
(1,7,'E'),
(1,8,'E'),
(2,5,'V'),
(2,6,'V');