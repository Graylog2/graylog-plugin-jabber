package org.graylog2.alarmcallbacks.jabber;

import org.graylog2.plugin.PluginModule;

public class JabberAlarmCallbackModule extends PluginModule {
    @Override
    protected void configure() {
        addAlarmCallback(JabberAlarmCallback.class);
    }
}
