INSERT INTO Employee(id, email, password_hash, display_name) VALUES
    (NEXT VALUE FOR employee_seq, 'alice.johnson@example.com', 'hashed_password_1', 'Alice Johnson'),
    (NEXT VALUE FOR employee_seq, 'bob.smith@example.com',     'hashed_password_2', 'Bob Smith'),
    (NEXT VALUE FOR employee_seq, 'carol.davis@example.com',   'hashed_password_3', 'Carol Davis');

INSERT INTO team(id, name) VALUES
    (NEXT VALUE FOR team_seq, 'Platform'),
    (NEXT VALUE FOR team_seq, 'Mobile'),
    (NEXT VALUE FOR team_seq, 'Data');

INSERT INTO team_membership(id, role, employee_id, team_id) VALUES
    (NEXT VALUE FOR team_membership_seq, 'LEAD',   (SELECT id FROM employee WHERE email = 'alice.johnson@example.com'), (SELECT id FROM team WHERE name = 'Platform')),
    (NEXT VALUE FOR team_membership_seq, 'MEMBER', (SELECT id FROM employee WHERE email = 'bob.smith@example.com'),     (SELECT id FROM team WHERE name = 'Platform')),
    (NEXT VALUE FOR team_membership_seq, 'LEAD',   (SELECT id FROM employee WHERE email = 'bob.smith@example.com'),     (SELECT id FROM team WHERE name = 'Mobile')),
    (NEXT VALUE FOR team_membership_seq, 'MEMBER', (SELECT id FROM employee WHERE email = 'carol.davis@example.com'),   (SELECT id FROM team WHERE name = 'Mobile')),
    (NEXT VALUE FOR team_membership_seq, 'LEAD',   (SELECT id FROM employee WHERE email = 'carol.davis@example.com'),   (SELECT id FROM team WHERE name = 'Data')),
    (NEXT VALUE FOR team_membership_seq, 'MEMBER', (SELECT id FROM employee WHERE email = 'alice.johnson@example.com'), (SELECT id FROM team WHERE name = 'Data'));

INSERT INTO project(id, name, description, team_id) VALUES
    (NEXT VALUE FOR project_seq, 'API Gateway',       'Central gateway for routing and auth',    (SELECT id FROM team WHERE name = 'Platform')),
    (NEXT VALUE FOR project_seq, 'CI/CD Pipeline',    'Automated build and deployment pipeline', (SELECT id FROM team WHERE name = 'Platform')),
    (NEXT VALUE FOR project_seq, 'iOS App Redesign',  'Refresh of the iOS app UI',               (SELECT id FROM team WHERE name = 'Mobile')),
    (NEXT VALUE FOR project_seq, 'Reporting Warehouse', 'Data warehouse for business reporting', (SELECT id FROM team WHERE name = 'Data'));

INSERT INTO task(id, title, status, due_date, project_id, employee_id) VALUES
    (NEXT VALUE FOR task_seq, 'Set up rate limiting',       'IN_PROGRESS', '2026-10-15 17:00:00', (SELECT id FROM project WHERE name = 'API Gateway'),         (SELECT id FROM employee WHERE email = 'alice.johnson@example.com')),
    (NEXT VALUE FOR task_seq, 'Add JWT validation',         'TODO',        '2026-10-30 17:00:00', (SELECT id FROM project WHERE name = 'API Gateway'),         (SELECT id FROM employee WHERE email = 'bob.smith@example.com')),
    (NEXT VALUE FOR task_seq, 'Configure build caching',    'DONE',        '2026-09-30 17:00:00', (SELECT id FROM project WHERE name = 'CI/CD Pipeline'),      (SELECT id FROM employee WHERE email = 'bob.smith@example.com')),
    (NEXT VALUE FOR task_seq, 'Design new onboarding flow', 'IN_PROGRESS', '2026-10-20 17:00:00', (SELECT id FROM project WHERE name = 'iOS App Redesign'),    (SELECT id FROM employee WHERE email = 'carol.davis@example.com')),
    (NEXT VALUE FOR task_seq, 'Migrate to SwiftUI',         'TODO',        '2026-11-15 17:00:00', (SELECT id FROM project WHERE name = 'iOS App Redesign'),    (SELECT id FROM employee WHERE email = 'bob.smith@example.com')),
    (NEXT VALUE FOR task_seq, 'Build sales fact table',     'TODO',        '2026-11-01 17:00:00', (SELECT id FROM project WHERE name = 'Reporting Warehouse'), (SELECT id FROM employee WHERE email = 'alice.johnson@example.com'));

INSERT INTO comment(id, body, created_at, task_id, employee_id) VALUES
    (NEXT VALUE FOR comment_seq, 'Using a token bucket per client key.',      '2026-09-18 10:15:00+00', (SELECT id FROM task WHERE title = 'Set up rate limiting'),       (SELECT id FROM employee WHERE email = 'alice.johnson@example.com')),
    (NEXT VALUE FOR comment_seq, 'Should limits be configurable per route?',  '2026-09-18 14:02:00+00', (SELECT id FROM task WHERE title = 'Set up rate limiting'),       (SELECT id FROM employee WHERE email = 'bob.smith@example.com')),
    (NEXT VALUE FOR comment_seq, 'Build times dropped from 12 to 4 minutes.', '2026-09-20 09:30:00+00', (SELECT id FROM task WHERE title = 'Configure build caching'),    (SELECT id FROM employee WHERE email = 'bob.smith@example.com')),
    (NEXT VALUE FOR comment_seq, 'First mockups are in the design folder.',   '2026-09-19 16:45:00+00', (SELECT id FROM task WHERE title = 'Design new onboarding flow'), (SELECT id FROM employee WHERE email = 'carol.davis@example.com')),
    (NEXT VALUE FOR comment_seq, 'Need the source tables list before starting.', '2026-09-21 11:00:00+00', (SELECT id FROM task WHERE title = 'Build sales fact table'), (SELECT id FROM employee WHERE email = 'alice.johnson@example.com'));
