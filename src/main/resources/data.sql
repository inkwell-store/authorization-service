INSERT INTO ROLE_TBL (id, name)
VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_CUSTOMER');

INSERT INTO CREDENTIAL_TBL (id, email, password, created_at)
VALUES ('02f2ad05-5707-4f25-b48f-6d36143011c1', 'wilsondbarbosa@gmail.com', '$2a$10$XhNSaT8.n/Dm98UBcrAGZONu/8elZc8oRQnb8unw9sNSuYAqJO/WS', '2026-08-10 19:33:09.94351+00');

INSERT INTO CREDENTIAL_ROLE_TBL (id, role_id, credential_id)
VALUES (1, 2, '02f2ad05-5707-4f25-b48f-6d36143011c1');