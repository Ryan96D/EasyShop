package org.yearup.data.mysql;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MySqlReceiptDao extends MySqlDaoBase implements ReceiptDao
{
    public MySqlReceiptDao(DataSource dataSource)
    {
        super(dataSource);
    }
