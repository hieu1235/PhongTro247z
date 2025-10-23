-- ==================================================================
-- SETUP SECURE DATABASE USER FOR PHONGTRO247Z - PostgreSQL Version
-- ==================================================================
--
-- Script này tạo database user mới với quyền hạn chế cho PostgreSQL
-- Thay vì dùng postgres superuser (rủi ro bảo mật cao)
--
-- HƯỚNG DẪN:
-- 1. Kết nối PostgreSQL với postgres user (trên database postgres)
-- 2. Chạy phần STEP 1 để tạo user
-- 3. Kết nối lại với database phongtro247_db
-- 4. Chạy phần STEP 2 và STEP 3
-- 5. Update file .env với username/password mới
-- 6. DISABLE remote access cho postgres user (khuyến nghị cho production)
--
-- Ngày tạo: 23/10/2025
-- ==================================================================

-- ==================================================================
-- STEP 1: Creating Database User (Chạy trên database postgres)
-- ==================================================================

-- Kiểm tra xem user đã tồn tại chưa
DO $$
BEGIN
    IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'phongtro247_db_user') THEN
        EXECUTE 'DROP USER phongtro247_db_user';
        RAISE NOTICE 'User phongtro247_db_user already exists. Dropping...';
    END IF;
END
$$;

-- Tạo user mới
-- ⚠️ QUAN TRỌNG: Đổi password này thành password mạnh của bạn!
-- Yêu cầu: Ít nhất 20 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt
CREATE USER phongtro247_db_user WITH PASSWORD 'Komk@2004';
GRANT CONNECT ON DATABASE phongtro247_db TO phongtro247_db_user;

-- ==================================================================
-- STEP 2: Granting Permissions on phongtro247_db (Chạy trên database phongtro247_db)
-- ==================================================================

-- Cấp quyền USAGE trên schema public
GRANT USAGE ON SCHEMA public TO phongtro247_db_user;

-- Cấp quyền SELECT, INSERT, UPDATE, DELETE trên tất cả tables hiện tại
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO phongtro247_db_user;

-- Cấp quyền USAGE và SELECT trên tất cả sequences (cho SERIAL columns)
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO phongtro247_db_user;

-- Đảm bảo user mới có quyền trên các objects được tạo sau này
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO phongtro247_db_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO phongtro247_db_user;

-- ==================================================================
-- STEP 3: Testing Permissions (Chạy trên database phongtro247_db)
-- ==================================================================

-- Test query để verify quyền
SET SESSION AUTHORIZATION phongtro247_db_user;

DO $$
DECLARE
    test_count INTEGER;
BEGIN
    RAISE NOTICE 'Testing permissions for phongtro247_db_user...';

    -- Test SELECT
    BEGIN
        SELECT COUNT(*) INTO test_count FROM roles LIMIT 1;
        RAISE NOTICE '✓ SELECT permission: OK';
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE '✗ SELECT permission: FAILED - %', SQLERRM;
    END;

    -- Test INSERT
    BEGIN
        INSERT INTO roles (role_name, description) VALUES ('test_role_temp', 'Test Role');
        RAISE NOTICE '✓ INSERT permission: OK';
        -- Rollback test insert
        DELETE FROM roles WHERE role_name = 'test_role_temp';
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE '✗ INSERT permission: FAILED - %', SQLERRM;
    END;

    -- Test UPDATE
    BEGIN
        UPDATE roles SET description = description WHERE role_id = 1;
        RAISE NOTICE '✓ UPDATE permission: OK';
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE '✗ UPDATE permission: FAILED - %', SQLERRM;
    END;

    -- Test DELETE
    BEGIN
        DELETE FROM roles WHERE role_name = 'non_existent_role';
        RAISE NOTICE '✓ DELETE permission: OK';
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE '✗ DELETE permission: FAILED - %', SQLERRM;
    END;

END
$$;

-- Reset session authorization
RESET SESSION AUTHORIZATION;

-- ==================================================================
-- SECURITY RECOMMENDATIONS
-- ==================================================================
--
-- ✅ Database user setup completed successfully!
--
-- NEXT STEPS:
--
-- 1. Update your .env file with:
--    DB_USERNAME=phongtro247_db_user
--    DB_PASSWORD=your_strong_password
--
-- 2. Test connection from your application
--
-- 3. FOR PRODUCTION: Restrict postgres user access
--    - Disable remote connections for postgres user
--    - Use strong passwords
--    - Enable SSL connections
--
-- 4. Consider enabling PostgreSQL logging
--    to track all database activities
--
-- ⚠️  IMPORTANT SECURITY NOTES:
-- - NEVER use postgres superuser in application code
-- - Use strong password (20+ chars, mixed case, numbers, symbols)
-- - Store password in .env file (NOT in code)
-- - Rotate passwords every 90 days
-- - Monitor failed login attempts
-- - Enable PostgreSQL firewall rules (pg_hba.conf)
-- - Enable SSL for all connections
--

-- ==================================================================
-- OPTIONAL: Connection Test Commands
-- ==================================================================
--
-- Test connection with psql:
-- psql -h your_host -U phongtro247_db_user -d phongtro247_db -c "SELECT version();"
--
-- Test connection with JDBC URL:
-- jdbc:postgresql://your_host:5432/phongtro247_db?user=phongtro247_db_user&password=your_password
--

-- ==================================================================
-- END OF SCRIPT
-- ==================================================================