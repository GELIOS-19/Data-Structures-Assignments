package music;

import java.util.*;

/**
 * This class represents a library of song playlists.
 * <p>
 * An ArrayList of Playlist objects represents the various playlists within
 * one's library.
 *
 * @author Jeremy Hui
 * @author Vian Miranda
 */
public class PlaylistLibrary {
  private ArrayList<Playlist> songLibrary; // contains various playlists

  /**
   * DO NOT EDIT! Constructor for Library.
   *
   * @param songLibrary passes in ArrayList of playlists
   */
  public PlaylistLibrary(ArrayList<Playlist> songLibrary) {
    this.songLibrary = songLibrary;
  }

  /** DO NOT EDIT! Default constructor for an empty library. */
  public PlaylistLibrary() {
    this(null);
  }

  /**
   * This method reads the songs from an input csv file, and creates a playlist
   * from it. Each song is on a different line.
   * <p>
   * 1. Open the file using StdIn.setFile(filename);
   * <p>
   * 2. Declare a reference that will refer to the last song of the circular
   * linked list.
   * <p>
   * 3. While there are still lines in the input file: 1. read a song from the
   * file 2. create an object of type Song with the song information 3. Create a
   * SongNode object that holds the Song object from step 3.2. 4. insert the
   * Song object at the END of the circular linked list (use the reference from
   * step 2) 5. increase the count of the number of songs
   * <p>
   * 4. Create a Playlist object with the reference from step (2) and the number
   * of songs in the playlist
   * <p>
   * 5. Return the Playlist object
   * <p>
   * Each line of the input file has the following format:
   * songName,artist,year,popularity,link
   * <p>
   * To read a line, use StdIn.readline(), then use .split(",") to extract
   * fields from the line.
   * <p>
   * If the playlist is empty, return a Playlist object with null for its last,
   * and 0 for its size.
   * <p>
   * The input file has Songs in decreasing popularity order.
   * <p>
   * DO NOT implement a sorting method, PRACTICE add to end.
   *
   * @param filename the playlist information input file
   * @return a Playlist object, which contains a reference to the LAST song in
   *         the circular linkedlist playlist and the size of the playlist.
   */
  public Playlist createPlaylist(String filename) {
    Playlist playlist = new Playlist();
    SongNode last = null;
    StdIn.setFile(filename);
    while (StdIn.hasNextLine()) {
      String[] data = StdIn.readLine().split(",");
      String name = data[0];
      String artist = data[1];
      int year = Integer.parseInt(data[2]);
      int pop = Integer.parseInt(data[3]);
      String link = data[4];
      Song song = new Song(name, artist, year, pop, link);
      if (playlist.getSize() == 0) {
        SongNode songNode = last = new SongNode(song, null);
        songNode.setNext(last);
      } else {
        SongNode songNode = new SongNode(song, last.getNext());
        last.setNext(songNode);
        last = songNode;
      }
      playlist.setSize(playlist.getSize() + 1);
    }
    playlist.setLast(last);
    return playlist;
  }

  /**
   * ****DO NOT**** UPDATE THIS METHOD This method is already implemented for
   * you.
   * <p>
   * Adds a new playlist into the song library at a certain index.
   * <p>
   * 1. Calls createPlayList() with a file containing song information. 2. Adds
   * the new playlist created by createPlayList() into the songLibrary.
   * <p>
   * Note: initialize the songLibrary if it is null
   *
   * @param filename      the playlist information input file
   * @param playlistIndex the index of the location where the playlist will be
   *                        added
   */
  public void addPlaylist(String filename, int playlistIndex) {
    /* DO NOT UPDATE THIS METHOD */
    if (songLibrary == null) {
      songLibrary = new ArrayList<Playlist>();
    }
    if (playlistIndex >= songLibrary.size()) {
      songLibrary.add(createPlaylist(filename));
    } else {
      songLibrary.add(playlistIndex, createPlaylist(filename));
    }
  }

  /**
   * ****DO NOT**** UPDATE THIS METHOD This method is already implemented for
   * you.
   * <p>
   * It takes a playlistIndex, and removes the playlist located at that index.
   *
   * @param playlistIndex the index of the playlist to remove
   * @return true if the playlist has been deleted
   */
  public boolean removePlaylist(int playlistIndex) {
    /* DO NOT UPDATE THIS METHOD */
    if (songLibrary == null || playlistIndex >= songLibrary.size()) {
      return false;
    }
    songLibrary.remove(playlistIndex);
    return true;
  }

  /**
   * Adds the playlists from many files into the songLibrary
   * <p>
   * 1. Initialize the songLibrary
   * <p>
   * 2. For each of the filenames add the playlist into songLibrary
   * <p>
   * The playlist will have the same index in songLibrary as it has in the
   * filenames array. For example if the playlist is being created from the
   * filename[i] it will be added to songLibrary[i]. Use the addPlaylist()
   * method.
   *
   * @param filenames an array of the filenames of playlists that should be
   *                    added to the library
   */
  public void addAllPlaylists(String[] filenames) {
    if (this.songLibrary == null) {
      this.songLibrary = new ArrayList<Playlist>();
    }
    for (String filename : filenames) {
      this.addPlaylist(filename, this.songLibrary.size());
    }
  }

  /**
   * This method adds a song to a specified playlist at a given position.
   * <p>
   * The first node of the circular linked list is at position 1, the second
   * node is at position 2 and so forth.
   * <p>
   * Return true if the song can be added at the given position within the
   * specified playlist (and thus has been added to the playlist), false
   * otherwise (and the song will not be added).
   * <p>
   * Increment the size of the playlist if the song has been successfully added
   * to the playlist.
   *
   * @param playlistIndex the index where the playlist will be added
   * @param position      the position in the playlist to which the song is to
   *                        be added
   * @param song          the song to add
   * @return true if the song can be added and therefore has been added, false
   *         otherwise.
   */
  public boolean insertSong(int playlistIndex, int position, Song song) {
    Playlist playlist = songLibrary.get(playlistIndex);
    int playlistSize = playlist.getSize();
    if (position <= 0 || position > playlistSize + 1) {
      return false;
    }
    // if the song can be inserted, increment first
    playlist.setSize(playlistSize + 1);
    // insert into empty
    if (playlistSize == 0) {
      SongNode targetSongNode = new SongNode(song, null);
      targetSongNode.setNext(targetSongNode);
      playlist.setLast(targetSongNode);
      return true;
    }
    // insert at the end
    if (position == playlistSize + 1) {
      SongNode targetSongNode = new SongNode(song, null);
      targetSongNode.setNext(playlist.getLast().getNext());
      playlist.getLast().setNext(targetSongNode);
      playlist.setLast(targetSongNode);
      return true;
    }
    // insert in the middle
    SongNode ptr = playlist.getLast();
    for (int i = 1; i < position; i++) {
      ptr = ptr.getNext();
    }
    SongNode targetSongNode = new SongNode(song, null);
    targetSongNode.setNext(ptr.getNext());
    ptr.setNext(targetSongNode);
    return true;
  }

  /**
   * This method removes a song at a specified playlist, if the song exists.
   * <p>
   * Use the .equals() method of the Song class to check if an element of the
   * circular linkedlist matches the specified song.
   * <p>
   * Return true if the song is found in the playlist (and thus has been
   * removed), false otherwise (and thus nothing is removed).
   * <p>
   * Decrease the playlist size by one if the song has been successfully removed
   * from the playlist.
   *
   * @param playlistIndex the playlist index within the songLibrary where the
   *                        song is to be added.
   * @param song          the song to remove.
   * @return true if the song is present in the playlist and therefore has been
   *         removed, false otherwise.
   */
  public boolean removeSong(int playlistIndex, Song song) {
    Playlist playlist = songLibrary.get(playlistIndex);
    int playlistSize = playlist.getSize();
    // empty playlist
    if (playlistSize == 0) {
      return false;
    }
    // delete only song
    if (playlistSize == 1 && playlist.getLast().getSong().equals(song)) {
      playlist.setLast(null);
      playlist.setSize(0);
      return true;
    }
    // find song previous
    SongNode ptr = playlist.getLast().getNext();
    int i = 1;
    boolean found = false;
    while (i <= playlistSize) {
      if (ptr.getNext().getSong().equals(song)) {
        found = true;
        break;
      }
      ptr = ptr.getNext();
      i++;
    }
    // delete song
    if (found) {
      if (ptr.getNext() == playlist.getLast()) {
        playlist.setLast(ptr);
      }
      ptr.setNext(ptr.getNext().getNext());
      playlist.setSize(playlistSize - 1);
    }
    return found;
  }

  /**
   * This method reverses the playlist located at playlistIndex
   * <p>
   * Each node in the circular linkedlist will point to the element that came
   * before it.
   * <p>
   * After the list is reversed, the playlist located at playlistIndex will
   * reference the first SongNode in the original playlist (new last).
   *
   * @param playlistIndex the playlist to reverse
   */
  public void reversePlaylist(int playlistIndex) {
    Playlist playlist = this.songLibrary.get(playlistIndex);
    int playlistSize = playlist.getSize();
    // if playlist is empty or size 1, do nothing
    if (playlistSize <= 1) {
      return;
    }
    SongNode ptr = playlist.getLast().getNext();
    for (int i = 1; i <= playlistSize; i++) {
      SongNode next = ptr.getNext();
      ptr.setNext(playlist.getLast());
      playlist.setLast(ptr);
      ptr = next;
    }
    playlist.setLast(ptr);
  }

  /**
   * This method merges two playlists.
   * <p>
   * Both playlists have songs in decreasing popularity order. The resulting
   * playlist will also be in decreasing popularity order.
   * <p>
   * You may assume both playlists are already in decreasing popularity order.
   * If the songs have the same popularity, add the song from the playlist with
   * the lower playlistIndex first.
   * <p>
   * After the lists have been merged: - store the merged playlist at the lower
   * playlistIndex - remove playlist at the higher playlistIndex
   *
   * @param playlistIndex1 the first playlist to merge into one playlist
   * @param playlistIndex2 the second playlist to merge into one playlist
   */
  public void mergePlaylists(int playlistIndex1, int playlistIndex2) {
    Playlist lowPlaylist = this.songLibrary
        .get(Integer.min(playlistIndex1, playlistIndex2));
    Playlist highPlaylist = this.songLibrary
        .get(Integer.max(playlistIndex1, playlistIndex2));
    int lowPlaylistSize = lowPlaylist.getSize();
    int highPlaylistSize = highPlaylist.getSize();
    // if both playlists are empty, just remove the high and keep
    // the low
    if (lowPlaylistSize == 0 && highPlaylistSize == 0) {
      // this is the high playlist index
      this.removePlaylist(Integer.max(playlistIndex1, playlistIndex2));
      return;
    }
    SongNode mergedLast = null;
    while (lowPlaylist.getSize() != 0 && highPlaylist.getSize() != 0) {
      SongNode lowHead = lowPlaylist.getLast().getNext();
      SongNode highHead = highPlaylist.getLast().getNext();
      Playlist targetPlaylist = lowHead.getSong().getPopularity() >= highHead
          .getSong().getPopularity() ? lowPlaylist : highPlaylist;
      SongNode targetHead = new SongNode(
          targetPlaylist.getLast().getNext().getSong(), null);
      // Now add targetHead at the end of the merged playlist
      // (last node
      // is mergedLast)
      if (mergedLast == null) {
        mergedLast = targetHead;
        mergedLast.setNext(mergedLast);
      } else {
        targetHead.setNext(mergedLast.getNext());
        mergedLast.setNext(targetHead);
        mergedLast = targetHead;
      }
      // Now delete the song from target
      this.removeSong(this.songLibrary.indexOf(targetPlaylist),
          targetHead.getSong());
    }
    Playlist nonEmptyPlaylist = lowPlaylist.getSize() == 0 ? highPlaylist
        : lowPlaylist;
    SongNode nonEmptyPtr = nonEmptyPlaylist.getLast().getNext();
    for (int i = 0; i < nonEmptyPlaylist.getSize(); i++) {
      SongNode targetSongNode = new SongNode(nonEmptyPtr.getSong(), null);
      if (mergedLast == null) {
        mergedLast = targetSongNode;
        mergedLast.setNext(mergedLast);
      } else {
        targetSongNode.setNext(mergedLast.getNext());
        mergedLast.setNext(targetSongNode);
        mergedLast = targetSongNode;
      }
      nonEmptyPtr = nonEmptyPtr.getNext();
    }
    // this is the high playlist index
    this.removePlaylist(Integer.max(playlistIndex1, playlistIndex2));
    lowPlaylist.setLast(mergedLast);
    lowPlaylist.setSize(lowPlaylistSize + highPlaylistSize);
  }

  /**
   * This method shuffles a specified playlist using the following procedure:
   * <p>
   * 1. Create a new playlist to store the shuffled playlist in.
   * <p>
   * 2. While the size of the original playlist is not 0, randomly generate a
   * number using StdRandom.uniformInt(1, size+1). Size contains the current
   * number of items in the original playlist.
   * <p>
   * 3. Remove the corresponding node from the original playlist and insert it
   * into the END of the new playlist (1 being the first node, 2 being the
   * second, etc).
   * <p>
   * 4. Update the old playlist with the new shuffled playlist.
   *
   * @param index the playlist to shuffle in songLibrary
   */
  public void shufflePlaylist(int playlistIndex) {
    Playlist playlist = this.songLibrary.get(playlistIndex);
    int playlistSize = playlist.getSize();
    SongNode shuffledLast = null;
    while (playlist.getSize() != 0) {
      int randomNumber = StdRandom.uniformInt(playlist.getSize() + 1);
      SongNode ptr = playlist.getLast().getNext();
      for (int i = 1; i <= randomNumber; i++) {
        ptr = ptr.getNext();
      }
      SongNode targetSongNode = new SongNode(ptr.getSong(), null);
      if (shuffledLast == null) {
        shuffledLast = targetSongNode;
        shuffledLast.setNext(shuffledLast);
      } else {
        targetSongNode.setNext(shuffledLast.getNext());
        shuffledLast.setNext(targetSongNode);
        shuffledLast = targetSongNode;
      }
      // Delete targetSongNode
      this.removeSong(playlistIndex, ptr.getSong());
    }
    playlist.setLast(shuffledLast);
    playlist.setSize(playlistSize);
  }

  /**
   * This method sorts a specified playlist using linearithmic sort.
   * <p>
   * Set the playlist located at the corresponding playlistIndex in decreasing
   * popularity index order.
   * <p>
   * This method should use a sort that has O(nlogn), such as with merge sort.
   *
   * @param playlistIndex the playlist to shuffle
   */
  public void sortPlaylist(int playlistIndex) {
    // WRITE YOUR CODE HERE
  }

  /**
   * ****DO NOT**** UPDATE THIS METHOD Plays playlist by index; can use this
   * method to debug.
   *
   * @param playlistIndex the playlist to print
   * @param repeats       number of times to repeat playlist
   * @throws InterruptedException
   */
  public void playPlaylist(int playlistIndex, int repeats) {
    /* DO NOT UPDATE THIS METHOD */
    final String NO_SONG_MSG = " has no link to a song! Playing next...";
    if (songLibrary.get(playlistIndex).getLast() == null) {
      StdOut.println("Nothing to play.");
      return;
    }
    SongNode ptr = songLibrary.get(playlistIndex).getLast().getNext(),
        first = ptr;
    do {
      StdOut.print("\r" + ptr.getSong().toString());
      if (ptr.getSong().getLink() != null) {
        StdAudio.play(ptr.getSong().getLink());
        for (int ii = 0; ii < ptr.getSong().toString().length(); ii++)
          StdOut.print("\b \b");
      } else {
        StdOut.print(NO_SONG_MSG);
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
        for (int ii = 0; ii < NO_SONG_MSG.length(); ii++)
          StdOut.print("\b \b");
      }
      ptr = ptr.getNext();
      if (ptr == first)
        repeats--;
    } while (ptr != first || repeats > 0);
  }

  /**
   * ****DO NOT**** UPDATE THIS METHOD Prints playlist by index; can use this
   * method to debug.
   *
   * @param playlistIndex the playlist to print
   */
  public void printPlaylist(int playlistIndex) {
    StdOut.printf("%nPlaylist at index %d (%d song(s)):%n", playlistIndex,
        songLibrary.get(playlistIndex).getSize());
    if (songLibrary.get(playlistIndex).getLast() == null) {
      StdOut.println("EMPTY");
      return;
    }
    SongNode ptr;
    for (ptr = songLibrary.get(playlistIndex).getLast()
        .getNext(); ptr != songLibrary.get(playlistIndex)
            .getLast(); ptr = ptr.getNext()) {
      StdOut.print(ptr.getSong().toString() + " -> ");
    }
    if (ptr == songLibrary.get(playlistIndex).getLast()) {
      StdOut.print(songLibrary.get(playlistIndex).getLast().getSong().toString()
          + " - POINTS TO FRONT");
    }
    StdOut.println();
  }

  public void printLibrary() {
    if (songLibrary.size() == 0) {
      StdOut.println("\nYour library is empty!");
    } else {
      for (int ii = 0; ii < songLibrary.size(); ii++) {
        printPlaylist(ii);
      }
    }
  }

  /*
   * Used to get and set objects. DO NOT edit.
   */
  public ArrayList<Playlist> getPlaylists() {
    return songLibrary;
  }

  public void setPlaylists(ArrayList<Playlist> p) {
    songLibrary = p;
  }
}
