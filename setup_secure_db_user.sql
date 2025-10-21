-- ==================================================================
-- SETUP SECURE DATABASE USER FOR PHONGTRO247Z
-- ==================================================================
-- 
-- Script này tạo database user mới với quyền hạn chế
-- thay vì dùng SA account (rủi ro bảo mật cao)
--
-- HƯỚNG DẪN:
-- 1. Kết nối SQL Server với SA account
-- 2. Chạy script này
-- 3. Update file .env với username/password mới
-- 4. DISABLE SA account (khuyến nghị cho production)
--
-- Ngày tạo: 21/10/2025
-- ==================================================================

USE master;
GO

PRINT '========================================';
PRINT 'STEP 1: Creating Login';
PRINT '========================================';

-- Kiểm tra xem login đã tồn tại chưa
IF EXISTS (SELECT * FROM sys.server_principals WHERE name = 'phongtro247_user')
BEGIN
    PRINT 'Login phongtro247_user already exists. Dropping...';
    DROP LOGIN phongtro247_user;
END

-- Tạo login mới
-- ⚠️ QUAN TRỌNG: Đổi password này thành password mạnh của bạn!
-- Yêu cầu: Ít nhất 20 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt
CREATE LOGIN phongtro247_user 
WITH PASSWORD = 'Komk@2004';
GO

PRINT 'Login phongtro247_user created successfully.';
PRINT '';

-- ==================================================================

PRINT '========================================';
PRINT 'STEP 2: Creating Database User';
PRINT '========================================';

USE phongtro247_db;
GO

-- Kiểm tra xem user đã tồn tại chưa
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = 'phongtro247_user')
BEGIN
    PRINT 'User phongtro247_user already exists in database. Dropping...';
    DROP USER phongtro247_user;
END

-- Tạo user trong database
CREATE USER phongtro247_user FOR LOGIN phongtro247_user;
GO

PRINT 'User phongtro247_user created in phongtro247_db.';
PRINT '';

-- ==================================================================

PRINT '========================================';
PRINT 'STEP 3: Granting Permissions';
PRINT '========================================';

-- Cấp quyền read data
ALTER ROLE db_datareader ADD MEMBER phongtro247_user;
PRINT 'Granted db_datareader role.';

-- Cấp quyền write data (INSERT, UPDATE, DELETE)
ALTER ROLE db_datawriter ADD MEMBER phongtro247_user;
PRINT 'Granted db_datawriter role.';

-- Cấp quyền execute stored procedures (nếu có)
GRANT EXECUTE TO phongtro247_user;
PRINT 'Granted EXECUTE permission.';

-- Cấp quyền VIEW DEFINITION (để xem cấu trúc bảng)
GRANT VIEW DEFINITION TO phongtro247_user;
PRINT 'Granted VIEW DEFINITION permission.';

PRINT '';

-- ==================================================================

PRINT '========================================';
PRINT 'STEP 4: Testing Permissions';
PRINT '========================================';

-- Test query để verify quyền
EXECUTE AS USER = 'phongtro247_user';

SELECT 'Testing permissions for phongtro247_user...' AS Status;

-- Test SELECT
BEGIN TRY
    SELECT TOP 1 * FROM users;
    PRINT '✓ SELECT permission: OK';
END TRY
BEGIN CATCH
    PRINT '✗ SELECT permission: FAILED - ' + ERROR_MESSAGE();
END CATCH

-- Test INSERT
BEGIN TRY
    BEGIN TRANSACTION;
    INSERT INTO users (username, password, full_name, email, role_id, created_at)
    VALUES ('test_user_temp', 'test_pass', 'Test User', 'test@test.com', 1, GETDATE());
    ROLLBACK TRANSACTION;
    PRINT '✓ INSERT permission: OK';
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT '✗ INSERT permission: FAILED - ' + ERROR_MESSAGE();
END CATCH

-- Test UPDATE
BEGIN TRY
    BEGIN TRANSACTION;
    UPDATE users SET updated_at = GETDATE() WHERE user_id = 1;
    ROLLBACK TRANSACTION;
    PRINT '✓ UPDATE permission: OK';
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT '✗ UPDATE permission: FAILED - ' + ERROR_MESSAGE();
END CATCH

-- Test DELETE
BEGIN TRY
    BEGIN TRANSACTION;
    DELETE FROM users WHERE username = 'non_existent_user';
    ROLLBACK TRANSACTION;
    PRINT '✓ DELETE permission: OK';
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT '✗ DELETE permission: FAILED - ' + ERROR_MESSAGE();
END CATCH

REVERT;
PRINT '';

-- ==================================================================

PRINT '========================================';
PRINT 'STEP 5: Security Recommendations';
PRINT '========================================';

PRINT '';
PRINT '✅ Database user setup completed successfully!';
PRINT '';
PRINT 'NEXT STEPS:';
PRINT '';
PRINT '1. Update your .env file with:';
PRINT '   DB_USERNAME=phongtro247_user';
PRINT '   DB_PASSWORD=your_strong_password';
PRINT '';
PRINT '2. Test connection from your application';
PRINT '';
PRINT '3. FOR PRODUCTION: Disable SA account';
PRINT '   ALTER LOGIN sa DISABLE;';
PRINT '';
PRINT '4. Consider enabling SQL Server Audit';
PRINT '   to track all database activities';
PRINT '';
PRINT '⚠️  IMPORTANT SECURITY NOTES:';
PRINT '- NEVER use SA account in application code';
PRINT '- Use strong password (20+ chars, mixed case, numbers, symbols)';
PRINT '- Store password in .env file (NOT in code)';
PRINT '- Rotate passwords every 90 days';
PRINT '- Monitor failed login attempts';
PRINT '- Enable SQL Server firewall rules';
PRINT '';

-- ==================================================================

PRINT '========================================';
PRINT 'OPTIONAL: Connection Test Command';
PRINT '========================================';

PRINT '';
PRINT 'Test connection in PowerShell:';
PRINT 'sqlcmd -S localhost -U phongtro247_user -P "your_password" -d phongtro247_db -Q "SELECT @@VERSION"';
PRINT '';

-- ==================================================================
-- END OF SCRIPT
-- ==================================================================
