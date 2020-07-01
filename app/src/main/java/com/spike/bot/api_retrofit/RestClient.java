package com.spike.bot.api_retrofit;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.spike.bot.ChatApplication;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    /*dev arp add this abstract class on 23 june 2020*/

    public static final String BASE_URL = ChatApplication.url;
    private static final int TIME = 60 * 15; // 90O milli second
    private static final String TAG = RestClient.class.getSimpleName();
    private static Retrofit retrofit;
    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static RestClient clientInstance;

    //TODO is you use HTTPS then use follow code in place of httpClient
    OkHttpClient httpClient = new OkHttpClient().newBuilder()
            .connectTimeout(TIME, TimeUnit.SECONDS)
            .readTimeout(TIME, TimeUnit.SECONDS)
            .writeTimeout(TIME, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(new UnauthorisedInterceptor()) // if user is not authorised or the token has been changed
            .addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    String Auth_Token = Common.getPrefValue(ChatApplication.getContext(),Constants.AUTHORIZATION_TOKEN);
                    if (Auth_Token != null && Auth_Token.length() > 0) {
                        requestBuilder.addHeader("Authorization", "Bearer "+ Auth_Token);
                        Log.d(TAG + " OkHttp Header -- ", "AuthorizedToken : " + "Bearer "+ Auth_Token);
                    }
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            })
            .build();


    //---------- another solution ----------------
    private ApiInterface apiInterface;

    /*ssl code over*/


    private RestClient() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient);  // dev arp   // for http
//        builder.client(getUnsafeOkHttpClient());  // dev arp   // for https
        Retrofit mretrofit = builder.build();


        apiInterface = mretrofit.create(ApiInterface.class);
    }

    public static RestClient getInstance() {
        if (clientInstance == null) {
            clientInstance = new RestClient();
        }
        return clientInstance;
    }

    //ref url - https://stackoverflow.com/a/36882375/2888952
    public OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = getSSLConfig(ChatApplication.getContext())
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
//                    .sslSocketFactory(sslSocketFactory)
                    .sslSocketFactory(getSSLConfig(ChatApplication.getContext()).getSocketFactory())
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    .connectTimeout(TIME, TimeUnit.SECONDS)
                    .readTimeout(TIME, TimeUnit.SECONDS)
                    .writeTimeout(TIME, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new UnauthorisedInterceptor())
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder();
                            String Auth_Token = Common.getPrefValue(ChatApplication.getContext(),Constants.AUTHORIZATION_TOKEN);
                            if (Auth_Token != null && Auth_Token.length() > 0) {
                                requestBuilder.addHeader("Authorization", "Bearer "+ Auth_Token);
                                Log.d(TAG + " OkHttp Header -- ", "AuthorizedToken : " + "Bearer "+ Auth_Token);
                            }
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
//        try (InputStream cert = context.getResources().openRawResource(R.raw.follofile)) {  // here add your ssl certificate .crt file from your web team
//            ca = cf.generateCertificate(cert);
//        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);  // after add certificate open the comment

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }
}


