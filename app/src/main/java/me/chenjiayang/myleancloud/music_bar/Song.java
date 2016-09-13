package me.chenjiayang.myleancloud.music_bar;

public class Song {

    private long id;
    private String title;
    private String artist;
    private String length;

    public Song(long songID, String songTitle, String songArtist, String songLength){
        id=songID;
        title=songTitle;
        artist=songArtist;
        length=songLength;
    }

    public long getID(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getLength(){
        return length;
    }
}
