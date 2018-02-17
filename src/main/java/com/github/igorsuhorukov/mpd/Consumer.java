package com.github.igorsuhorukov.mpd;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.bff.javampd.server.Server;

public class Consumer extends ScheduledPollConsumer {

    private Server mpd;
    private MpdEndpoint mpdEndpoint;

    public Consumer(MpdEndpoint mpdEndpoint, Processor processor, Server mpd) {
        super(mpdEndpoint, processor);
        this.mpdEndpoint = mpdEndpoint;
        this.mpd = mpd;
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = getEndpoint().createExchange();

        try {
            String command = mpdEndpoint.getCommand();
            Object exchangeBody;
            switch (command.toLowerCase()){
                case "current":
                    exchangeBody = mpd.getPlayer().getCurrentSong();//endpoint.getCommand())
                    break;
                case "playlist":
                    exchangeBody = mpd.getPlaylist().getSongList();
                    break;
                case "state":
                    exchangeBody = mpd.getServerStatus().getState();
                    break;
                case "status":
                    exchangeBody = mpd.getServerStatus().getStatus();
                    break;
                case "volume":
                    exchangeBody = mpd.getServerStatus().getVolume();
                    break;
                case "total":
                    exchangeBody = mpd.getServerStatus().getTotalTime();
                    break;
                case "elapsed":
                    exchangeBody = mpd.getServerStatus().getElapsedTime();
                    break;
                case "error":
                    exchangeBody = mpd.getServerStatus().getError();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command: "+command);

            }
            exchange.getIn().setBody(exchangeBody);
        } catch (Exception e) {
            handleException(e);
        }
        try {
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                handleException(exchange.getException());
            }
        }
    }
}
