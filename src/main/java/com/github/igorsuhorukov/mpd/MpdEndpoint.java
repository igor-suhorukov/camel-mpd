package com.github.igorsuhorukov.mpd;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.bff.javampd.server.MPD;
import org.bff.javampd.server.Server;

import java.util.Objects;

/**
 * Music Player Daemon API endpoint
 */
@UriEndpoint(scheme = "mpd", title = "Music Player Daemon", syntax="mpd:command",
        consumerClass = Consumer.class, label = "mpd")
public class MpdEndpoint extends DefaultEndpoint{
    @UriPath(description = "12")
    @Getter
    @Setter
    private String command;
    @UriParam(description = "The host for MPD to listen on.") @Metadata(required = "true")
    @Getter
    @Setter
    private String host;
    @UriParam(description = "The port for MPD to listen on.")
    @Getter
    @Setter
    private Integer port;
    @UriParam(description = "MPD connection timeout.")
    @Getter
    @Setter
    private Integer timeout;

    private Server mpd;

    public MpdEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public org.apache.camel.Producer createProducer() throws Exception {
        return new Producer(this, mpd);
    }

    @Override
    public org.apache.camel.Consumer createConsumer(Processor processor) throws Exception {
        return new Consumer(this, processor, mpd);
    }

    private MPD createMpdClient() {
        MPD.Builder mpdBuilder = new MPD.Builder();
        if(host!=null && !host.isEmpty()){
            mpdBuilder.server(host);
        }
        if(port!=null){
            mpdBuilder.port(port);
        }
        if(timeout!=null){
            mpdBuilder.timeout(timeout);
        }
        return mpdBuilder.build();
    }


    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        mpd = createMpdClient();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        mpd.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MpdEndpoint that = (MpdEndpoint) o;
        return Objects.equals(command, that.command) &&
                Objects.equals(host, that.host) &&
                Objects.equals(port, that.port) &&
                Objects.equals(timeout, that.timeout) &&
                Objects.equals(mpd, that.mpd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), command, host, port, timeout, mpd);
    }
}
