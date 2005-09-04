package org.ftp4che.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.ftp4che.exception.ProxyConnectionException;

public interface Proxy {

    public Socket connect(String host, int port) throws ProxyConnectionException;
    public void bind(InetSocketAddress isa) throws IOException;
}
