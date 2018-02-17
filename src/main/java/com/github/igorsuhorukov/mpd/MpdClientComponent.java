package com.github.igorsuhorukov.mpd;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.net.URLDecoder;
import java.util.Map;

/**
 * dsasdg
 */
public class MpdClientComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        MpdEndpoint mpdEndpoint = new MpdEndpoint(uri, this);
        setProperties(mpdEndpoint, parameters);
        if(remaining!=null && !remaining.isEmpty()) {
            mpdEndpoint.setCommand(URLDecoder.decode(remaining, "UTF-8"));
        }
        return mpdEndpoint;
    }
}
