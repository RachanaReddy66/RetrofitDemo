package com.retrofitdemo;
import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class RetrofitModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RetrofitModule(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
    }

    @Override
    public String getName() {
        return "RetrofitModule";
    }

    @ReactMethod
    public void makeNetworkRequest(String url, String xmlData, final Callback successCallback, final Callback errorCallback) {
        try {
            // Load the certificate from the assets folder
            InputStream certificateInputStream = reactContext.getAssets().open("eposservice_jk_gov_in.crt");

            // Create a KeyStore and load the certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            keyStore.setCertificateEntry("certificate", certificateFactory.generateCertificate(certificateInputStream));

            // Create a TrustManager that trusts the server certificate
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create an SSLContext with the TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Configure the OkHttpClient with the SSLContext
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();

            MyApiService apiService = retrofit.create(MyApiService.class);

            MediaType mediaType = MediaType.parse("application/xml; charset=utf-8");
            RequestBody xmlRequestBody = RequestBody.create(xmlData, mediaType);

            Call<ResponseBody> call = apiService.postXmlData(xmlRequestBody);
            retrofit2.Response<ResponseBody> retrofitResponse = call.execute();

            if (retrofitResponse.isSuccessful()) {
                // Handle successful response
                ResponseBody responseBody = retrofitResponse.body();
                String xmlResponse = responseBody.string();
                successCallback.invoke(xmlResponse);
            } else {
                // Handle error response
                errorCallback.invoke("Error occurred");
            }
        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }
}
