package com.webservice;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.app.android.cync.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by ketul.patel on 21/1/16.
 */
public class VolleySetup {
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    public static String ErrorMessage = "";

    private VolleySetup() {
        // no instances
    }

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSocketFactory(context)));
        ErrorMessage = context.getResources().getString(R.string.s_wrong);
    }


    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    public static SSLSocketFactory getSocketFactory(Context context) {

        CertificateFactory cf;
        try {
            cf = CertificateFactory.getInstance("X.509");
            Certificate ca = null;
            try (InputStream caInput = context.getResources().openRawResource(R.raw.server_ssl_cert)) {
                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (CertificateException e) {
                e.printStackTrace();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.e("CipherUsed", "" + session.getCipherSuite());
                    return true;/*hostname.compareTo("192.168.1.10") == 0; //The Hostname of your server*/
                }
            };

            //HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");

            sslContext.init(null, tmf.getTrustManagers(), null);
            //HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            SSLSocketFactory sf = sslContext.getSocketFactory();
            return sf;
        } catch (CertificateException | NoSuchAlgorithmException
                | KeyStoreException | IOException | KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void trustCertificate() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

}
