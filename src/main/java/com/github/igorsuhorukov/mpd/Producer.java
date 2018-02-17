package com.github.igorsuhorukov.mpd;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.impl.DefaultProducer;
import org.bff.javampd.server.Server;
import org.bff.javampd.song.MPDSong;

import java.util.List;

public class Producer extends DefaultProducer {

    private Server mpd;
    private MpdEndpoint mpdEndpoint;

    Producer(MpdEndpoint mpdEndpoint, Server mpd) {
        super(mpdEndpoint);
        this.mpdEndpoint = mpdEndpoint;
        this.mpd = mpd;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String command = mpdEndpoint.getCommand();
        switch (command.toLowerCase()){
            case "play":
                mpd.getPlayer().play();
                break;
            case "stop":
                mpd.getPlayer().stop();
                break;
            case "next":
                mpd.getPlayer().playNext();
                break;
            case "previous":
                mpd.getPlayer().playPrevious();
                break;
            case "randomize":
                mpd.getPlayer().randomizePlay();
                break;
            case "unrandomize":
                mpd.getPlayer().unRandomizePlay();
                break;
            case "volume":
                mpd.getPlayer().setVolume(getVolumeLevel(exchange));
                break;
            case "song":
                addSongs(exchange);
                break;
            default:
                throw new IllegalArgumentException("Unknown command: "+command);
        }
    }

    private int getVolumeLevel(Exchange exchange) {
        int volumeLevel = Integer.parseInt(exchange.getIn().getBody(String.class));
        if(volumeLevel<0 || volumeLevel>100){
            throw new IllegalArgumentException("volume value should be in interval [0,100]");
        }
        return volumeLevel;
    }

    private void addSongs(Exchange exchange) throws InvalidPayloadException {
        Object song = exchange.getIn().getMandatoryBody();
        if(song instanceof String){
            mpd.getPlaylist().addSong((String) song);
        } else if(song instanceof MPDSong){
            mpd.getPlaylist().addSong((MPDSong) song);
        } else if(song instanceof List){
            List songs = (List) song;
            if(!songs.isEmpty()){
                if(isItemsMpd(songs)) {
                    addMpdSongList(songs);
                } else if(isItemsString(songs)) {
                    addStringSongList(songs);
                } else {
                    throw new IllegalArgumentException("mixed or unknown song items type");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isItemsString(List songs) {
        return songs.stream().filter(songItem -> songItem instanceof String).count() == songs.size();
    }

    @SuppressWarnings("unchecked")
    private boolean isItemsMpd(List songs) {
        return songs.stream().filter(songItem -> songItem instanceof MPDSong).count() == songs.size();
    }

    @SuppressWarnings("unchecked")
    private void addStringSongList(List songs) {
        songs.forEach(mpdSong -> mpd.getPlaylist().addSong((String) mpdSong));
    }

    @SuppressWarnings("unchecked")
    private void addMpdSongList(List songs) {
        mpd.getPlaylist().addSongs(songs);
    }
}
