INSERT INTO ROLE_TBL (name)
VALUES ('ROLE_ADMIN'), ('ROLE_CUSTOMER');

INSERT INTO CREDENTIAL_TBL (id, email, password, created_at)
VALUES 
(
    '02f2ad05-5707-4f25-b48f-6d36143011c1', 
    'wilsondbarbosa@gmail.com', 
    '$2a$10$XhNSaT8.n/Dm98UBcrAGZONu/8elZc8oRQnb8unw9sNSuYAqJO/WS', 
    '2026-08-10 19:33:09.94351+00'
),
(
    'e4825462-f160-41db-9cc0-69a7f5279b52', 
    'test@gmail.com', 
    '$2a$10$ccs6079mzJMV5ECXp6LbEumCrbkdILomhkV6nAd0jfXbm1ynDUfyy', 
    '2026-08-10 19:33:09.94351+00'
);

INSERT INTO CREDENTIAL_ROLE_TBL (role_id, credential_id)
VALUES
(2, '02f2ad05-5707-4f25-b48f-6d36143011c1'),
(2, 'e4825462-f160-41db-9cc0-69a7f5279b52');