package org.ftp4che.proxy;

import java.util.Properties;

import org.ftp4che.exception.ConfigurationException;

public class ProxyConnectionFactory {

    public static final String HTTP = "HTTP";

    public static final String SOCKS4 = "SOCKS4";

    public static final String SOCKS4A = "SOCKS4A";

    public static final String SOCKS5 = "SOCKS5";

    public static Proxy getInstance(String host, int port, String user,
            String pass, String type) throws ConfigurationException {
        if (type.equalsIgnoreCase(SOCKS4))
            return new Socks4(host, port, user);

        throw new ConfigurationException("Unkown proxy type.");
    }

    public static Proxy getInstance(Properties config)
            throws ConfigurationException {
        return ProxyConnectionFactory.getInstance(config
                .getProperty("proxy.host"), Integer.parseInt(config
                .getProperty("proxy.port")), config.getProperty("proxy.user"),
                config.getProperty("proxy.password"), config
                        .getProperty("proxy.type"));
    }
}
