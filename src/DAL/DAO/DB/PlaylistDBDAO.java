package DAL.DAO.DB;

import BE.Playlist;
import BE.Song;
import BLL.PlaylistManager;
import DAL.DAO.PlaylistDAOInterface;
import DAL.DB.DbConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDBDAO implements PlaylistDAOInterface {
    protected DbConnectionHandler database;
    protected PlaylistManager playlistManager;

    /**
     *  Sets the manager.
     * @param playlistManager the current instance of the manager.
     */
    @Override
    public void setPlaylistManager(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

    /**
     * Tries to connect to the database.
     */
    public PlaylistDBDAO() {
        database = DbConnectionHandler.getInstance();
        try {
            if(database.getConnection().isClosed())
                playlistManager.goLocal();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Tries to load the songs from the database.
     * @return A list of the songs in the database or a empty list if the database has no songs.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public List<Playlist> loadPlaylist() throws SQLException {
        var temp = new ArrayList<Playlist>();
        try (var con = database.getConnection();
             Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM playlist;");
            while (rs.next()) {
                int id = rs.getInt("playlist_id");
                String name = rs.getString("playlist_name");
                temp.add(new Playlist(id, name));
            }
            return temp;
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
            return temp;
        }
    }

    /**
     * Tries to create a playlist on the database
     * @param name the name of the playlist.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public void createPlaylist(String name) throws SQLException {
        var sql = "INSERT INTO playlist (playlist_name) VALUES(?);";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
        }
    }

    /**
     * Searches for a playlist on the database
     * @param name the name of the playlist you are looking for
     * @return a playlist with the name
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public Playlist getPlaylist(String name) throws SQLException {
        var sql = "SELECT FROM playlist WHERE playlist_name = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
            var resultSet = st.getResultSet();
            var id = resultSet.getInt("playlist_id");
            var name1 = resultSet.getString("playlist_name");
            var playlist = new Playlist(id, name1);
            return playlist;
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
            return null;
        }
    }

    /**
     * Tries to delete a playlist from the database, does nothing if a playlist with name doesnt exist.
     * @param name the name of the playlist.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void deletePlaylist(String name) throws SQLException {
        var sql = "DELETE FROM playlist WHERE playlist_name = ?;";
        try (var con = database.getConnection(); PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
            return;
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
        }
    }

    /**
     * Tries to load songs from a playlist, by looking for id matches
     * @param playlist_id the id of the playlist whose songs you are looking for.
     * @return a list of songs if theres a positive match for the playlist, an empty playlist otherwise.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public List<Song> loadSongsFromPlaylist(int playlist_id) throws SQLException {
        var temp = new ArrayList<Song>();
        var sql = "SELECT song.song_id,song.song_title,song.song_filepath FROM playlist_song,song WHERE playlist_song.playlist_id=? AND playlist_song.song_id=song.song_id;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1,playlist_id);
            st.execute();
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                int song_id = rs.getInt("song_id");
                String song_title = rs.getString("song_title");
                String song_filepath = rs.getString("song_filepath");
                temp.add(new Song(song_id,song_title,song_filepath));
            }
            return temp;
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
            return temp;
        }
    }


    /**
     * Tries to add a song to a playlist
     * @param playlist_id the id of the playlist you want to add a song to.
     * @param song_id the id of the song you want to add to the playlist.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void AddSongToPlaylist(int playlist_id,int song_id) throws SQLException {
        var sql = "INSERT INTO playlist_song (playlist_id,song_id) VALUES (?,?);";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1,playlist_id);
            st.setInt(2,song_id);
            st.executeUpdate();
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
        }
    }

    /**
     * Tries to delete a song with song_id from a playlist in the database, does nothing if no match found.
     * @param playlist_id the id of the playlist you want to remove a song from.
     * @param song_id the id of the song you want to remove from the playlist.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void deleteFromPlaylist(int playlist_id,int song_id) throws SQLException {
        var sql = "DELETE FROM playlist_song WHERE playlist_id=? AND song_id=?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, playlist_id);
            st.setInt(2,song_id);
            st.executeUpdate();
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
        }
    }


    /**
     * Changes the name of the playlist if a match is found.
     * @param playlist a Playlist with the new name, and the original id.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void updatePlaylist(Playlist playlist) throws SQLException {

        String sql = "UPDATE playlist SET playlist_name=?  WHERE playlist_id=?;";
        try (var con = database.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, playlist.getPlayListName());
            preparedStatement.setInt(2, playlist.getPlaylistId());
            if (preparedStatement.executeUpdate() != 1) {
                System.out.println("Could not update Movie: " + playlist.toString());
            }
        }catch (SQLNonTransientConnectionException e){
            playlistManager.goLocal();
        }
    }
}