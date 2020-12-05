package DAL.DAO.DB;

import BE.Song;
import BLL.SongManager;
import DAL.DAO.FILE.SongLocalDAO;
import DAL.DAO.SongDAOInterface;
import DAL.DB.DbConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongDBDAO implements SongDAOInterface {
    protected DbConnectionHandler database;
    protected SongManager songManager;

    @Override
    public void setSongManager(SongManager songManager) {
        this.songManager = songManager;
    }

    public SongDBDAO() throws SQLException {
        database = DbConnectionHandler.getInstance();
        if(database.getConnection()==null){
            songManager.setSongDAO(new SongLocalDAO());
            throw new SQLException("could not connect to database");
        }
    }

    @Override
    public List<Song> loadSongs() {
        var temp = new ArrayList<Song>();
        try (var con = database.getConnection();
             Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM song;");
            while (rs.next()) {
                int id = rs.getInt("song_id");
                String name = rs.getString("song_title");
                String path = rs.getString("song_filepath");
                int category = rs.getInt("category_id");
                temp.add(new Song(id, name, path, category));
            }
            return temp;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean createSong(String name, String path) {
        var sql = "INSERT INTO song (song_title, song_filepath) VALUES(?,?);";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.setString(2, path);
            st.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createSong(String name, String path, int categoryId) throws SQLException {
        var sql = "INSERT INTO song (song_title, song_filepath, category_id) VALUES(?,?,?);";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.setString(2, path);
            st.setInt(3, categoryId);
            st.executeUpdate();
            return true;
        }
    }

    @Override
    public Song getSong(String name) {
        var sql = "SELECT FROM song WHERE song_name = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
            var resultSet = st.getResultSet();
            var id = resultSet.getInt("song_id");
            var name1 = resultSet.getString("song_name");
            var path = resultSet.getString("song_filepath");
            var song = new Song(id, name1, path);
            return song;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteSong(int id) {
        var sql = "DELETE FROM song WHERE song_id = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, id);
            st.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSong(int id, Song modified) {
        var sql = "UPDATE song SET song_title = ?, song_filepath = ? WHERE song_id = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, modified.getTitle());
            st.setString(2, modified.getFilePath());
            st.setInt(3, id);
            st.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
