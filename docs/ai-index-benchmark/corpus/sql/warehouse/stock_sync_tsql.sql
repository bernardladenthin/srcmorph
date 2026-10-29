-- SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
--
-- SPDX-License-Identifier: Apache-2.0
--
-- Warehouse: nightly stock synchronisation from a supplier feed (SQL Server, T-SQL).

CREATE TABLE dbo.StockLevel (
    WarehouseId INT          NOT NULL,
    Sku         NVARCHAR(40) NOT NULL,
    OnHand      INT          NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0),
    UpdatedAt   DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT PK_StockLevel PRIMARY KEY (WarehouseId, Sku)
);
GO

CREATE TABLE dbo.SupplierFeed (
    FeedRowId   BIGINT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY,
    WarehouseId INT                   NOT NULL,
    Sku         NVARCHAR(40)          NOT NULL,
    Quantity    INT                   NOT NULL,
    ReceivedAt  DATETIME2(0)          NOT NULL
);
GO

CREATE TABLE dbo.SyncLog (
    SyncLogId    INT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY,
    StartedAt    DATETIME2(0)       NOT NULL,
    RowsInserted INT                NOT NULL,
    RowsUpdated  INT                NOT NULL,
    ErrorMessage NVARCHAR(4000)     NULL
);
GO

-- Merges the supplier feed received since @Since into StockLevel and logs the outcome.
CREATE PROCEDURE dbo.usp_SyncStock
    @Since DATETIME2(0)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Started DATETIME2(0) = SYSUTCDATETIME();
    DECLARE @Changes TABLE (MergeAction NVARCHAR(10));

    BEGIN TRY
        BEGIN TRANSACTION;

        MERGE dbo.StockLevel AS target
        USING (
            SELECT WarehouseId, Sku, SUM(Quantity) AS Quantity
              FROM dbo.SupplierFeed
             WHERE ReceivedAt >= @Since
             GROUP BY WarehouseId, Sku
        ) AS source
           ON target.WarehouseId = source.WarehouseId AND target.Sku = source.Sku
        WHEN MATCHED THEN
            UPDATE SET OnHand = target.OnHand + source.Quantity, UpdatedAt = SYSUTCDATETIME()
        WHEN NOT MATCHED BY TARGET THEN
            INSERT (WarehouseId, Sku, OnHand) VALUES (source.WarehouseId, source.Sku, source.Quantity)
        OUTPUT $action INTO @Changes;

        INSERT INTO dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated)
        SELECT @Started,
               SUM(CASE WHEN MergeAction = 'INSERT' THEN 1 ELSE 0 END),
               SUM(CASE WHEN MergeAction = 'UPDATE' THEN 1 ELSE 0 END)
          FROM @Changes;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        INSERT INTO dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)
        VALUES (@Started, 0, 0, ERROR_MESSAGE());
        THROW;
    END CATCH
END;
GO
