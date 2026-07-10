-- Roles del enum com.everywhere.backend.model.enums.Role
INSERT INTO public.role (rol_nam_vc)
VALUES ('GERENTE'),
    ('VENTAS'),
    ('ADMINISTRAR'),
    ('ADMIN'),
    ('OPERACIONES'),
    ('SISTEMAS'),
    ('VENTAS_JUNIOR'),
    ('GERENTE_ARGENTINA');
INSERT INTO public.usuarios (rol_nam_vc, usr_email_vc, usr_pass_vc, rol_id)
SELECT 'Admin User',
    'admin@everywheretravel.online',
    'COGNITO_MANAGED',
    rol_id
FROM public.role
WHERE role.rol_nam_vc = 'ADMIN';