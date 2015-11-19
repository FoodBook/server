package tk.lenkyun.foodbook.foodbook.Parser.rowset;

import org.springframework.jdbc.core.RowMapper;
import tk.lenkyun.foodbook.foodbook.Domain.Data.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lenkyun on 19/11/2558.
 */
public class LocationMapper implements RowMapper<Location> {

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Location(rs.getString("name"), new Location.LatLng(rs.getDouble("lat"), rs.getDouble("lon")), null);
    }
}
