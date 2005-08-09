package org.ftp4che.util;

import java.security.*;
import java.security.cert.*;
import javax.net.ssl.*;


public class EasyX509TrustManager implements X509TrustManager
{
    private X509TrustManager standardTrustManager = null;

    public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
        super();
        TrustManagerFactory factory = TrustManagerFactory.getInstance("SunX509");
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("SunX509 trust manager not supported");
        }
        this.standardTrustManager = (X509TrustManager)trustmanagers[0];
    }

    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws  CertificateException{
        this.standardTrustManager.checkClientTrusted(certificates, authType);
    }

    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws  CertificateException{
        if ((certificates != null) && (certificates.length == 1)) {
            X509Certificate certificate = certificates[0];
            try {
                certificate.checkValidity(); 
            }
            catch (CertificateException e) {}
        } else {
            this.standardTrustManager.checkServerTrusted(certificates, authType);
        }
    }
    
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}