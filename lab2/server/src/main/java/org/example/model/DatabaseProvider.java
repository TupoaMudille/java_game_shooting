package org.example.model;

import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProvider
{
    private Connection connection;
    private DataSource dataSource;
    private static DataSource CreateDataSource()
    {
        final String url = "jdbc:postgresql://localhost:5432/JavaNet?user=postgres&password=root";
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }
    public DatabaseProvider()
    {
        try
        {
            this.dataSource = CreateDataSource();
            this.connection = this.dataSource.getConnection();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void UpdateScore(PlayerData _playerData)
    {
        if (_playerData == null)
        {
            return;
        }
        PreparedStatement preparedStatement;
        try
        {
            preparedStatement = connection.prepareStatement("INSERT INTO player_scores (name, hits, aces) VALUES (\'"+ _playerData.name +"\', " + _playerData.hits+ ", "+ _playerData.scoreRes +")");
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    public List<PlayerData> GetScore()
    {
        List<PlayerData> result = new ArrayList<PlayerData>();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try
        {
            preparedStatement = connection.prepareStatement("SELECT * FROM player_scores ORDER BY aces DESC");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                PlayerData temp = new PlayerData(resultSet.getString("name"));
                temp.scoreRes = resultSet.getInt("aces");
                temp.hits = resultSet.getInt("hits");
                result.add(temp);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }
    public List<PlayerData> GetScore(int _n)
    {
        List<PlayerData> result = new ArrayList<PlayerData>();
        List<PlayerData> participants = GetScore();
        for (int i =0; i<_n; i++)
        {
            result.add(participants.get(i));
        }
        return result;
    }

}
