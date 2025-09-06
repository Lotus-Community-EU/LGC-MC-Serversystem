package eu.lotusgc.mc.misc.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import eu.lotusgc.mc.misc.MySQL;

public class RadioInfo {

    String cpName, cpDescription, cpStartedAt, cpEndsAt, npName, npDescription, npStartsAt, npEndsAt, csTitle, csAlbum, csArtist, csStartedAt, csEndsAt;
    List<String> pastSongs;
    int listeners;

    public RadioInfo() {
        try {
            PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM core_radioinfo");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cpName = rs.getString("cp_name");
                cpDescription = rs.getString("cp_desc");
                cpStartedAt = rs.getString("cp_startedAt");
                cpEndsAt = rs.getString("cp_endsAt");
                npName = rs.getString("np_name");
                npDescription = rs.getString("np_desc");
                npStartsAt = rs.getString("np_startsAt");
                npEndsAt = rs.getString("np_endsAt");
                csTitle = rs.getString("cs_title");
                csAlbum = rs.getString("cs_album");
                csArtist = rs.getString("cs_artists");
                csStartedAt = rs.getString("cs_startedAt");
                csEndsAt = rs.getString("cs_endsAt");
                listeners = rs.getInt("listeners");
                pastSongs = Arrays.asList(rs.getString("past_songs").split("-|-"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
