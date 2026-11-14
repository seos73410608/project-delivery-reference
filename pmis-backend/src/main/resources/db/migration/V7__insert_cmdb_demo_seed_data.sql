
-- ============================================================================
-- V7: CMDB Demo Seed Data
-- PostgreSQL
--
-- Projects                 : 8
-- Configuration Items     : 50 (Servers 30 + Others 20)
-- Server Details           : 30
-- CI Relationships         : 60
-- Project-CI Relationships : 72
--
-- All records are fictional demonstration data.
-- Existing CI Type / Relationship Type reference data is reused.
-- ============================================================================

-- 1. Safety check
-- Do not seed into a database that already contains business data.

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM public.projects)
       OR EXISTS (SELECT 1 FROM public.configuration_items)
       OR EXISTS (SELECT 1 FROM public.server_details)
       OR EXISTS (SELECT 1 FROM public.ci_relationships)
       OR EXISTS (SELECT 1 FROM public.project_configuration_items)
    THEN
        RAISE EXCEPTION
            'V7 Demo Seed aborted: one or more target tables are not empty.';
    END IF;

    IF (SELECT COUNT(*) FROM public.ci_types WHERE enabled = TRUE) < 15 THEN
        RAISE EXCEPTION
            'V7 Demo Seed aborted: CI type reference data is incomplete.';
    END IF;

    IF (SELECT COUNT(*) FROM public.ci_relationship_types WHERE enabled = TRUE) < 5 THEN
        RAISE EXCEPTION
            'V7 Demo Seed aborted: CI relationship type reference data is incomplete.';
    END IF;
END $$;


-- ============================================================================
-- 2. Projects
-- ============================================================================

INSERT INTO public.projects
(
    created_at,
    updated_at,
    project_code,
    project_name,
    customer_name,
    project_manager,
    description,
    start_date,
    end_date,
    status,
    priority
)
VALUES
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-001',
    '차세대 금융업무 시스템 구축',
    '가상금융그룹',
    '김민준',
    '핵심 금융업무 시스템의 인프라 및 서비스 환경을 구축하는 시연 프로젝트',
    DATE '2026-01-01', DATE '2026-12-31',
    'IN_PROGRESS', 'CRITICAL'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-002',
    '통합 인증 시스템 구축',
    '가상금융그룹',
    '이서연',
    '내부 업무 시스템의 통합 인증 및 접근 관리 환경 구축',
    DATE '2026-02-01', DATE '2026-11-30',
    'IN_PROGRESS', 'HIGH'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-003',
    '데이터 분석 플랫폼 구축',
    '미래데이터 주식회사',
    '박지훈',
    '데이터 수집, 저장 및 분석을 위한 플랫폼 인프라 구축',
    DATE '2027-01-01', DATE '2027-10-31',
    'PLANNING', 'HIGH'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-004',
    '대외계 연계 시스템 구축',
    '가상금융그룹',
    '최유진',
    '외부 기관 및 제휴 시스템과의 연계 환경 구축',
    DATE '2026-03-01', DATE '2027-02-28',
    'IN_PROGRESS', 'HIGH'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-005',
    '통합 모니터링 시스템 구축',
    '미래데이터 주식회사',
    '정도현',
    '서버 및 애플리케이션 통합 모니터링 환경 구축',
    DATE '2026-04-01', DATE '2026-12-31',
    'IN_PROGRESS', 'MEDIUM'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-006',
    '재해복구 시스템 구축',
    '안심페이 주식회사',
    '한지민',
    '서비스 연속성 확보를 위한 재해복구 인프라 구축',
    DATE '2027-01-01', DATE '2027-12-31',
    'PLANNING', 'CRITICAL'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-007',
    '정보보안 관리체계 고도화',
    '안심페이 주식회사',
    '오준호',
    '네트워크 및 보안 인프라 관리체계 개선',
    DATE '2026-05-01', DATE '2027-04-30',
    'ON_HOLD', 'MEDIUM'
),
(
    CURRENT_TIMESTAMP, NULL,
    'DEMO-PRJ-008',
    '업무 포털 고도화',
    '한빛서비스 주식회사',
    '윤하늘',
    '사내 업무 포털의 서비스 환경 개선 및 운영 안정화',
    DATE '2025-01-01', DATE '2025-12-31',
    'COMPLETED', 'LOW'
);


-- ============================================================================
-- 3. Configuration Items
--    30 Servers + 20 Other CIs
--    CI codes are issued through the existing sequence.
-- ============================================================================

WITH server_seed AS
(
    SELECT
        format('DEMO-SRV-%s', lpad(n::TEXT, 2, '0')) AS ci_name,
        'SERVER' AS type_code,
        CASE
            WHEN n BETWEEN 25 AND 30 THEN 'STAGING'
            WHEN n BETWEEN 17 AND 24 THEN 'DEV'
            WHEN n BETWEEN 9 AND 16 THEN 'TEST'
            ELSE 'PROD'
        END AS environment,
        CASE
            WHEN n IN (8, 16, 24) THEN 'MAINTENANCE'
            ELSE 'ACTIVE'
        END AS status,
        format('시연용 서버 구성 항목 %s', n) AS description,
        n AS server_no
    FROM generate_series(1, 30) AS g(n)
),
other_seed AS
(
    SELECT *
    FROM
    (
        VALUES
        ('DEMO-OS-RHEL9',       'OPERATING_SYSTEM', 'PROD',    'ACTIVE', 'Red Hat Enterprise Linux 9'),
        ('DEMO-OS-RHEL8',       'OPERATING_SYSTEM', 'TEST',    'ACTIVE', 'Red Hat Enterprise Linux 8'),
        ('DEMO-OS-UBUNTU22',    'OPERATING_SYSTEM', 'DEV',     'ACTIVE', 'Ubuntu Server 22.04'),
        ('DEMO-OS-AIX72',       'OPERATING_SYSTEM', 'PROD',    'ACTIVE', 'IBM AIX 7.2'),
        ('DEMO-OS-WINDOWS2022', 'OPERATING_SYSTEM', 'STAGING', 'ACTIVE', 'Windows Server 2022'),

        ('DEMO-WEB-01', 'WEB_SERVER', 'PROD', 'ACTIVE', 'WEB 서버 1'),
        ('DEMO-WEB-02', 'WEB_SERVER', 'TEST', 'ACTIVE', 'WEB 서버 2'),
        ('DEMO-WEB-03', 'WEB_SERVER', 'DEV',  'ACTIVE', 'WEB 서버 3'),

        ('DEMO-WAS-01', 'WAS', 'PROD', 'ACTIVE', 'WAS 서버 1'),
        ('DEMO-WAS-02', 'WAS', 'TEST', 'ACTIVE', 'WAS 서버 2'),
        ('DEMO-WAS-03', 'WAS', 'DEV',  'ACTIVE', 'WAS 서버 3'),

        ('DEMO-DB-01', 'DATABASE', 'PROD', 'ACTIVE', '운영 데이터베이스 1'),
        ('DEMO-DB-02', 'DATABASE', 'TEST', 'ACTIVE', '테스트 데이터베이스 2'),
        ('DEMO-DB-03', 'DATABASE', 'DEV',  'ACTIVE', '개발 데이터베이스 3'),

        ('DEMO-MW-01', 'MIDDLEWARE', 'PROD', 'ACTIVE', '공통 미들웨어 1'),
        ('DEMO-MW-02', 'MIDDLEWARE', 'TEST', 'ACTIVE', '공통 미들웨어 2'),

        ('DEMO-SW-01', 'SWITCH', 'PROD', 'ACTIVE', '내부 네트워크 스위치'),
        ('DEMO-RT-01', 'ROUTER', 'PROD', 'ACTIVE', '외부 연계 라우터'),
        ('DEMO-FW-01', 'FIREWALL', 'PROD', 'ACTIVE', '경계 방화벽'),
        ('DEMO-STG-01', 'STORAGE_DEVICE', 'PROD', 'ACTIVE', '공유 스토리지 장비')
    ) AS v(ci_name, type_code, environment, status, description)
),
all_seed AS
(
    SELECT
        ci_name, type_code, environment, status, description
    FROM server_seed

    UNION ALL

    SELECT
        ci_name, type_code, environment, status, description
    FROM other_seed
)
INSERT INTO public.configuration_items
(
    created_at,
    updated_at,
    ci_code,
    ci_name,
    ci_type_id,
    status,
    environment,
    manager_id,
    location,
    description
)
SELECT
    CURRENT_TIMESTAMP,
    NULL,
    'CI-' || lpad(
        nextval('public.configuration_item_code_seq')::TEXT,
        6,
        '0'
    ),
    s.ci_name,
    t.id,
    s.status,
    s.environment,
    NULL,
    CASE
        WHEN s.environment = 'PROD' THEN '가상 데이터센터 A'
        WHEN s.environment = 'TEST' THEN '가상 데이터센터 B'
        WHEN s.environment = 'DEV' THEN '가상 개발센터'
        ELSE '가상 검증센터'
    END,
    s.description
FROM all_seed s
JOIN public.ci_types t
    ON t.code = s.type_code
   AND t.enabled = TRUE;


-- ============================================================================
-- 4. Server Details
-- ============================================================================

INSERT INTO public.server_details
(
    created_at,
    updated_at,
    ci_id,
    hostname,
    server_category,
    management_ip,
    os_name,
    os_version,
    cpu_cores,
    memory_size_mb,
    virtualization_platform
)
SELECT
    CURRENT_TIMESTAMP,
    NULL,
    ci.id,
    format('demo-srv-%s', lpad(g.n::TEXT, 2, '0')),
    CASE
        WHEN g.n IN (1, 2, 9, 10, 17, 18, 25, 26) THEN 'PHYSICAL'
        ELSE 'VIRTUAL'
    END,
    format('10.20.1.%s', g.n + 10),
    CASE
        WHEN g.n BETWEEN 1 AND 6 THEN 'Red Hat Enterprise Linux'
        WHEN g.n BETWEEN 7 AND 12 THEN 'IBM AIX'
        WHEN g.n BETWEEN 13 AND 18 THEN 'Ubuntu Server'
        WHEN g.n BETWEEN 19 AND 24 THEN 'Windows Server'
        ELSE 'Red Hat Enterprise Linux'
    END,
    CASE
        WHEN g.n BETWEEN 1 AND 6 THEN '9.4'
        WHEN g.n BETWEEN 7 AND 12 THEN '7.2'
        WHEN g.n BETWEEN 13 AND 18 THEN '22.04'
        WHEN g.n BETWEEN 19 AND 24 THEN '2022'
        ELSE '8.10'
    END,
    CASE WHEN g.n IN (1, 2, 9, 10, 17, 18, 25, 26) THEN 32 ELSE 8 END,
    CASE WHEN g.n IN (1, 2, 9, 10, 17, 18, 25, 26)
         THEN 131072 ELSE 32768 END,
    CASE
        WHEN g.n IN (1, 2, 9, 10, 17, 18, 25, 26) THEN NULL
        WHEN g.n % 2 = 0 THEN 'VMware vSphere'
        ELSE 'KVM'
    END
FROM generate_series(1, 30) AS g(n)
JOIN public.configuration_items ci
    ON ci.ci_name = format('DEMO-SRV-%s', lpad(g.n::TEXT, 2, '0'));


-- ============================================================================
-- 5. CI Relationships
--    30 server-to-OS RUNS_ON relationships
--    30 application / infrastructure relationships
-- ============================================================================

WITH server_os AS
(
    SELECT
        format('DEMO-SRV-%s', lpad(n::TEXT, 2, '0')) AS source_name,
        CASE
            WHEN n BETWEEN 1 AND 6 THEN 'DEMO-OS-RHEL9'
            WHEN n BETWEEN 7 AND 12 THEN 'DEMO-OS-AIX72'
            WHEN n BETWEEN 13 AND 18 THEN 'DEMO-OS-UBUNTU22'
            WHEN n BETWEEN 19 AND 24 THEN 'DEMO-OS-WINDOWS2022'
            ELSE 'DEMO-OS-RHEL8'
        END AS target_name,
        'RUNS_ON' AS relationship_code,
        '서버의 운영체제 실행 관계' AS description
    FROM generate_series(1, 30) AS g(n)
),
other_relations AS
(
    SELECT *
    FROM
    (
        VALUES
        ('DEMO-WEB-01', 'DEMO-SRV-01', 'HOSTED_ON', 'WEB 서비스 호스팅'),
        ('DEMO-WEB-02', 'DEMO-SRV-05', 'HOSTED_ON', 'WEB 서비스 호스팅'),
        ('DEMO-WEB-03', 'DEMO-SRV-09', 'HOSTED_ON', 'WEB 서비스 호스팅'),

        ('DEMO-WAS-01', 'DEMO-SRV-02', 'HOSTED_ON', 'WAS 서비스 호스팅'),
        ('DEMO-WAS-02', 'DEMO-SRV-06', 'HOSTED_ON', 'WAS 서비스 호스팅'),
        ('DEMO-WAS-03', 'DEMO-SRV-10', 'HOSTED_ON', 'WAS 서비스 호스팅'),

        ('DEMO-WAS-01', 'DEMO-DB-01', 'DEPENDS_ON', '운영 DB 의존'),
        ('DEMO-WAS-01', 'DEMO-DB-02', 'DEPENDS_ON', '보조 DB 의존'),
        ('DEMO-WAS-01', 'DEMO-DB-03', 'DEPENDS_ON', '개발 DB 의존'),
        ('DEMO-WAS-02', 'DEMO-DB-01', 'DEPENDS_ON', '운영 DB 의존'),
        ('DEMO-WAS-02', 'DEMO-DB-02', 'DEPENDS_ON', '보조 DB 의존'),
        ('DEMO-WAS-02', 'DEMO-DB-03', 'DEPENDS_ON', '개발 DB 의존'),
        ('DEMO-WAS-03', 'DEMO-DB-01', 'DEPENDS_ON', '운영 DB 의존'),
        ('DEMO-WAS-03', 'DEMO-DB-02', 'DEPENDS_ON', '보조 DB 의존'),
        ('DEMO-WAS-03', 'DEMO-DB-03', 'DEPENDS_ON', '개발 DB 의존'),

        ('DEMO-MW-01', 'DEMO-SRV-13', 'HOSTED_ON', '미들웨어 호스팅'),
        ('DEMO-MW-02', 'DEMO-SRV-14', 'HOSTED_ON', '미들웨어 호스팅'),
        ('DEMO-MW-01', 'DEMO-DB-01', 'DEPENDS_ON', '미들웨어 DB 의존'),
        ('DEMO-MW-02', 'DEMO-DB-02', 'DEPENDS_ON', '미들웨어 DB 의존'),

        ('DEMO-FW-01', 'DEMO-RT-01', 'CONNECTS_TO', '방화벽과 라우터 연결'),
        ('DEMO-RT-01', 'DEMO-SW-01', 'CONNECTS_TO', '라우터와 스위치 연결'),
        ('DEMO-SW-01', 'DEMO-STG-01', 'CONNECTS_TO', '스위치와 스토리지 연결'),

        ('DEMO-SRV-01', 'DEMO-SW-01', 'CONNECTS_TO', '서버 네트워크 연결'),
        ('DEMO-SRV-02', 'DEMO-SW-01', 'CONNECTS_TO', '서버 네트워크 연결'),
        ('DEMO-SRV-03', 'DEMO-SW-01', 'CONNECTS_TO', '서버 네트워크 연결'),
        ('DEMO-SRV-04', 'DEMO-SW-01', 'CONNECTS_TO', '서버 네트워크 연결'),
        ('DEMO-SRV-05', 'DEMO-SW-01', 'CONNECTS_TO', '서버 네트워크 연결'),
        ('DEMO-SRV-06', 'DEMO-SW-01', 'CONNECTS_TO', '서버 네트워크 연결'),

        ('DEMO-WEB-01', 'DEMO-FW-01', 'DEPENDS_ON', 'WEB 접근 보안 정책 의존'),
        ('DEMO-WEB-02', 'DEMO-FW-01', 'DEPENDS_ON', 'WEB 접근 보안 정책 의존')
    ) AS v(source_name, target_name, relationship_code, description)
),
all_relations AS
(
    SELECT * FROM server_os
    UNION ALL
    SELECT * FROM other_relations
)
INSERT INTO public.ci_relationships
(
    created_at,
    updated_at,
    source_ci_id,
    target_ci_id,
    relationship_type_id,
    description,
    status,
    deactivated_at,
    deactivated_by,
    deactivation_reason
)
SELECT
    CURRENT_TIMESTAMP,
    NULL,
    src.id,
    tgt.id,
    rt.id,
    r.description,
    'ACTIVE',
    NULL,
    NULL,
    NULL
FROM all_relations r
JOIN public.configuration_items src
    ON src.ci_name = r.source_name
JOIN public.configuration_items tgt
    ON tgt.ci_name = r.target_name
JOIN public.ci_relationship_types rt
    ON rt.code = r.relationship_code
   AND rt.enabled = TRUE;


-- ============================================================================
-- 6. Project-CI Relationships
--    72 mappings, with shared infrastructure across projects
-- ============================================================================

WITH mapping_seed AS
(
    SELECT *
    FROM
    (
        VALUES
        -- Project 001
        ('DEMO-PRJ-001', 'DEMO-SRV-01'),
        ('DEMO-PRJ-001', 'DEMO-SRV-02'),
        ('DEMO-PRJ-001', 'DEMO-SRV-03'),
        ('DEMO-PRJ-001', 'DEMO-SRV-04'),
        ('DEMO-PRJ-001', 'DEMO-WEB-01'),
        ('DEMO-PRJ-001', 'DEMO-WAS-01'),
        ('DEMO-PRJ-001', 'DEMO-DB-01'),
        ('DEMO-PRJ-001', 'DEMO-FW-01'),
        ('DEMO-PRJ-001', 'DEMO-SW-01'),

        -- Project 002
        ('DEMO-PRJ-002', 'DEMO-SRV-05'),
        ('DEMO-PRJ-002', 'DEMO-SRV-06'),
        ('DEMO-PRJ-002', 'DEMO-SRV-07'),
        ('DEMO-PRJ-002', 'DEMO-SRV-08'),
        ('DEMO-PRJ-002', 'DEMO-WEB-02'),
        ('DEMO-PRJ-002', 'DEMO-WAS-02'),
        ('DEMO-PRJ-002', 'DEMO-DB-02'),
        ('DEMO-PRJ-002', 'DEMO-FW-01'),
        ('DEMO-PRJ-002', 'DEMO-RT-01'),

        -- Project 003
        ('DEMO-PRJ-003', 'DEMO-SRV-09'),
        ('DEMO-PRJ-003', 'DEMO-SRV-10'),
        ('DEMO-PRJ-003', 'DEMO-SRV-11'),
        ('DEMO-PRJ-003', 'DEMO-SRV-12'),
        ('DEMO-PRJ-003', 'DEMO-WAS-03'),
        ('DEMO-PRJ-003', 'DEMO-DB-03'),
        ('DEMO-PRJ-003', 'DEMO-STG-01'),
        ('DEMO-PRJ-003', 'DEMO-SW-01'),
        ('DEMO-PRJ-003', 'DEMO-MW-01'),

        -- Project 004
        ('DEMO-PRJ-004', 'DEMO-SRV-13'),
        ('DEMO-PRJ-004', 'DEMO-SRV-14'),
        ('DEMO-PRJ-004', 'DEMO-SRV-15'),
        ('DEMO-PRJ-004', 'DEMO-SRV-16'),
        ('DEMO-PRJ-004', 'DEMO-WEB-03'),
        ('DEMO-PRJ-004', 'DEMO-WAS-01'),
        ('DEMO-PRJ-004', 'DEMO-DB-01'),
        ('DEMO-PRJ-004', 'DEMO-RT-01'),
        ('DEMO-PRJ-004', 'DEMO-FW-01'),

        -- Project 005
        ('DEMO-PRJ-005', 'DEMO-SRV-17'),
        ('DEMO-PRJ-005', 'DEMO-SRV-18'),
        ('DEMO-PRJ-005', 'DEMO-SRV-19'),
        ('DEMO-PRJ-005', 'DEMO-SRV-20'),
        ('DEMO-PRJ-005', 'DEMO-MW-01'),
        ('DEMO-PRJ-005', 'DEMO-MW-02'),
        ('DEMO-PRJ-005', 'DEMO-WEB-01'),
        ('DEMO-PRJ-005', 'DEMO-WAS-02'),
        ('DEMO-PRJ-005', 'DEMO-DB-02'),

        -- Project 006
        ('DEMO-PRJ-006', 'DEMO-SRV-21'),
        ('DEMO-PRJ-006', 'DEMO-SRV-22'),
        ('DEMO-PRJ-006', 'DEMO-SRV-23'),
        ('DEMO-PRJ-006', 'DEMO-SRV-24'),
        ('DEMO-PRJ-006', 'DEMO-DB-03'),
        ('DEMO-PRJ-006', 'DEMO-STG-01'),
        ('DEMO-PRJ-006', 'DEMO-RT-01'),
        ('DEMO-PRJ-006', 'DEMO-SW-01'),
        ('DEMO-PRJ-006', 'DEMO-FW-01'),

        -- Project 007
        ('DEMO-PRJ-007', 'DEMO-SRV-25'),
        ('DEMO-PRJ-007', 'DEMO-SRV-26'),
        ('DEMO-PRJ-007', 'DEMO-SRV-27'),
        ('DEMO-PRJ-007', 'DEMO-FW-01'),
        ('DEMO-PRJ-007', 'DEMO-SW-01'),
        ('DEMO-PRJ-007', 'DEMO-RT-01'),
        ('DEMO-PRJ-007', 'DEMO-MW-02'),
        ('DEMO-PRJ-007', 'DEMO-DB-01'),
        ('DEMO-PRJ-007', 'DEMO-WEB-02'),

        -- Project 008
        ('DEMO-PRJ-008', 'DEMO-SRV-28'),
        ('DEMO-PRJ-008', 'DEMO-SRV-29'),
        ('DEMO-PRJ-008', 'DEMO-SRV-30'),
        ('DEMO-PRJ-008', 'DEMO-WEB-01'),
        ('DEMO-PRJ-008', 'DEMO-WAS-03'),
        ('DEMO-PRJ-008', 'DEMO-DB-03'),
        ('DEMO-PRJ-008', 'DEMO-MW-01'),
        ('DEMO-PRJ-008', 'DEMO-STG-01'),
        ('DEMO-PRJ-008', 'DEMO-SW-01')
    ) AS v(project_code, ci_name)
)
INSERT INTO public.project_configuration_items
(
    project_id,
    ci_id,
    created_at
)
SELECT
    p.id,
    ci.id,
    CURRENT_TIMESTAMP
FROM mapping_seed m
JOIN public.projects p
    ON p.project_code = m.project_code
JOIN public.configuration_items ci
    ON ci.ci_name = m.ci_name;


-- ============================================================================
-- 7. Post-insert validation
-- ============================================================================

DO $$
DECLARE
    project_count BIGINT;
    ci_count BIGINT;
    server_count BIGINT;
    relationship_count BIGINT;
    mapping_count BIGINT;
BEGIN
    SELECT COUNT(*) INTO project_count
    FROM public.projects
    WHERE project_code LIKE 'DEMO-PRJ-%';

    SELECT COUNT(*) INTO ci_count
    FROM public.configuration_items
    WHERE ci_name LIKE 'DEMO-%';

    SELECT COUNT(*) INTO server_count
    FROM public.server_details sd
    JOIN public.configuration_items ci ON ci.id = sd.ci_id
    WHERE ci.ci_name LIKE 'DEMO-SRV-%';

    SELECT COUNT(*) INTO relationship_count
    FROM public.ci_relationships cr
    JOIN public.configuration_items ci ON ci.id = cr.source_ci_id
    WHERE ci.ci_name LIKE 'DEMO-%';

    SELECT COUNT(*) INTO mapping_count
    FROM public.project_configuration_items pci
    JOIN public.projects p ON p.id = pci.project_id
    WHERE p.project_code LIKE 'DEMO-PRJ-%';

    IF project_count <> 8
       OR ci_count <> 50
       OR server_count <> 30
       OR relationship_count <> 60
       OR mapping_count <> 72
    THEN
        RAISE EXCEPTION
            'V7 validation failed. projects=%, CIs=%, servers=%, relationships=%, mappings=%',
            project_count,
            ci_count,
            server_count,
            relationship_count,
            mapping_count;
    END IF;
END $$;

-- ============================================================================
-- END OF V7
-- ============================================================================
