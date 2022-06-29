package client.websocket.jabyl.xyz;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.concurrent.Future;

public class EventClient
{

    private static final String KEYSTORE_PATH = "";
    private static final String PASSWORD = "";

    public static void main(String[] args)
    {
        EventClient client = new EventClient();
        URI uri = URI.create("wss://localhost:8080/events/");
        try
        {
            client.run(uri);
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

    public void run(URI uri) throws Exception
    {
        KeyStore keystore = KeyStore.getInstance("JKS");
        try (InputStream in = new FileInputStream(KEYSTORE_PATH)) {
            keystore.load(in, PASSWORD.toCharArray());
        }
        SslContextFactory contextFactory = new SslContextFactory.Client(true);
        contextFactory.setKeyStore(keystore);

        HttpClient httpClient = new HttpClient(contextFactory);
        httpClient.start();

        WebSocketClient client = new WebSocketClient(httpClient);

        try
        {
            client.start();
            // The socket that receives events
            EventSocket socket = new EventSocket();
            // Attempt Connect
            Future<Session> fut = client.connect(socket, uri);
            // Wait for Connect
            Session session = fut.get();

            // Send a message
            session.getRemote().sendString("Hello");

            // Send another message
            session.getRemote().sendString("Goodbye");

            // Wait for other side to close
            socket.awaitClosure();

            // Close session
            session.close();
        }
        finally
        {
            client.stop();
        }
    }

//    private SSLContext getSslContext(String trustStoreFile, String keystoreFile, String password)
//            throws GeneralSecurityException, IOException {
//        KeyStore keystore = KeyStore.getInstance("JKS");
//        try (InputStream in = new FileInputStream(keystoreFile)) {
//            keystore.load(in, password.toCharArray());
//        }
//        try (InputStream in = new FileInputStream(trustStoreFile)) {
//            keystore.load(in, password.toCharArray());
//        }
//
//        KeyManagerFactory keyManagerFactory =
//                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        keyManagerFactory.init(keystore, password.toCharArray());
//
//        TrustManagerFactory tmf = TrustManagerFactory
//                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmf.init(keystore);
//
//        // Get hold of the default trust manager
//        X509TrustManager x509Tm = null;
//        for (TrustManager tm : tmf.getTrustManagers()) {
//            if (tm instanceof X509TrustManager) {
//                x509Tm = (X509TrustManager) tm;
//                break;
//            }
//        }
//
//        // Wrap it in your own class.
//        final X509TrustManager finalTm = x509Tm;
//        X509ExtendedTrustManager customTm = new X509ExtendedTrustManager() {
//            @Override
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            @Override
//            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//            }
//
//            @Override
//            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//            }
//
//            @Override
//            public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {
//
//            }
//        };
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(
//                keyManagerFactory.getKeyManagers(),
//                new TrustManager[]{customTm},
//                new SecureRandom());
//
//        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//
//        // Create all-trusting host name verifier
//        HostnameVerifier allHostsValid = new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        };
//        // Install the all-trusting host verifier
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        return sslContext;
//    }
}