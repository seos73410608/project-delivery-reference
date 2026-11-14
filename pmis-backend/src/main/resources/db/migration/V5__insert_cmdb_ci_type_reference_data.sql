-- ============================================================
-- V5: CMDB CI Type Reference Data
-- Description: Insert hierarchical CI type reference data
-- ============================================================

-- 1. Root
INSERT INTO ci_types (
    code,
    name,
    parent_id,
    description,
    assignable,
    enabled,
    sort_order,
    created_at
)
VALUES (
    'INFRASTRUCTURE',
    '인프라',
    NULL,
    '인프라 구성 항목의 최상위 분류',
    FALSE,
    TRUE,
    10,
    CURRENT_TIMESTAMP
)
ON CONFLICT (code) DO NOTHING;


-- 2. First-level categories
INSERT INTO ci_types (
    code, name, parent_id, description,
    assignable, enabled, sort_order, created_at
)
VALUES
(
    'COMPUTE', '컴퓨팅',
    (SELECT id FROM ci_types WHERE code = 'INFRASTRUCTURE'),
    '서버 및 컴퓨팅 자원 분류',
    FALSE, TRUE, 10, CURRENT_TIMESTAMP
),
(
    'SOFTWARE', '소프트웨어',
    (SELECT id FROM ci_types WHERE code = 'INFRASTRUCTURE'),
    '운영체제 및 소프트웨어 구성요소 분류',
    FALSE, TRUE, 20, CURRENT_TIMESTAMP
),
(
    'NETWORK', '네트워크',
    (SELECT id FROM ci_types WHERE code = 'INFRASTRUCTURE'),
    '네트워크 장비 분류',
    FALSE, TRUE, 30, CURRENT_TIMESTAMP
),
(
    'STORAGE', '스토리지',
    (SELECT id FROM ci_types WHERE code = 'INFRASTRUCTURE'),
    '스토리지 장비 및 자원 분류',
    FALSE, TRUE, 40, CURRENT_TIMESTAMP
)
ON CONFLICT (code) DO NOTHING;


-- 3. Assignable CI types
INSERT INTO ci_types (
    code, name, parent_id, description,
    assignable, enabled, sort_order, created_at
)
VALUES
(
    'SERVER', '서버',
    (SELECT id FROM ci_types WHERE code = 'COMPUTE'),
    '물리 및 가상 서버',
    TRUE, TRUE, 10, CURRENT_TIMESTAMP
),
(
    'OPERATING_SYSTEM', '운영체제',
    (SELECT id FROM ci_types WHERE code = 'SOFTWARE'),
    '서버 및 시스템 운영체제',
    TRUE, TRUE, 10, CURRENT_TIMESTAMP
),
(
    'WEB_SERVER', 'WEB 서버',
    (SELECT id FROM ci_types WHERE code = 'SOFTWARE'),
    'WEB 서버 소프트웨어',
    TRUE, TRUE, 20, CURRENT_TIMESTAMP
),
(
    'WAS', 'WAS',
    (SELECT id FROM ci_types WHERE code = 'SOFTWARE'),
    'Web Application Server 소프트웨어',
    TRUE, TRUE, 30, CURRENT_TIMESTAMP
),
(
    'DATABASE', '데이터베이스',
    (SELECT id FROM ci_types WHERE code = 'SOFTWARE'),
    '데이터베이스 관리 시스템',
    TRUE, TRUE, 40, CURRENT_TIMESTAMP
),
(
    'MIDDLEWARE', '미들웨어',
    (SELECT id FROM ci_types WHERE code = 'SOFTWARE'),
    '시스템 간 연계 및 미들웨어 구성요소',
    TRUE, TRUE, 50, CURRENT_TIMESTAMP
),
(
    'SWITCH', '스위치',
    (SELECT id FROM ci_types WHERE code = 'NETWORK'),
    '네트워크 스위치 장비',
    TRUE, TRUE, 10, CURRENT_TIMESTAMP
),
(
    'ROUTER', '라우터',
    (SELECT id FROM ci_types WHERE code = 'NETWORK'),
    '네트워크 라우터 장비',
    TRUE, TRUE, 20, CURRENT_TIMESTAMP
),
(
    'FIREWALL', '방화벽',
    (SELECT id FROM ci_types WHERE code = 'NETWORK'),
    '네트워크 보안 방화벽 장비',
    TRUE, TRUE, 30, CURRENT_TIMESTAMP
),
(
    'STORAGE_DEVICE', '스토리지 장비',
    (SELECT id FROM ci_types WHERE code = 'STORAGE'),
    '스토리지 시스템 및 장비',
    TRUE, TRUE, 10, CURRENT_TIMESTAMP
)
ON CONFLICT (code) DO NOTHING;
