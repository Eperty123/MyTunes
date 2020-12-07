package DAL.DAO.FILE;

import BE.Playlist;
import BE.Song;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.List;

public class PlaylistDAO {

    protected List<Playlist> playlists;
    protected File inputFile;

    /**
     *
     * @param path
     */
    public void loadPlaylist(String path) {
        File file = new File(path);
        if (file.exists()) {
            inputFile = file;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                var playlists = objectMapper.readValue(file, new TypeReference<List<Playlist>>() {
                });
                this.playlists.addAll(playlists);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean hasPlaylist(String name) {
        return getPlaylist(name) != null;
    }

    /**
     *
     * @param name
     * @return
     */
    public Playlist getPlaylist(String name) {
        for (int i = 0; i < playlists.size(); i++) {
            var playlist = playlists.get(i);
            if (playlists.equals(name)) return playlist;
        }
        return null;
    }

    /**
     *
     * @param name
     */
    public void deletePlaylist(String name) {
        for (int i = 0; i < playlists.size(); i++) {
            var playlist = playlists.get(i);
            if (playlists.equals(name)) playlists.remove(playlist);
        }
    }

    /**
     *
     * @param name
     */
    public void createPlaylist(String name) {
        if (!hasPlaylist(name)) playlists.add(new Playlist(name));
    }

    /**
     *
     * @param name
     * @param songs
     */
    public void createPlaylist(String name, List<Song> songs) {
        if (!hasPlaylist(name)) playlists.add(new Playlist(name, songs));
    }

    /**
     *
     */
    public void save() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(inputFile, playlists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param path
     */
    public void save(String path) {
        var file = new File(path);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, playlists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public List<Playlist> getPlaylistCollection() {
        return playlists;
    }

    /**
     *
     * @return
     */
    public File getInputFile() {
        return inputFile;
    }
}
