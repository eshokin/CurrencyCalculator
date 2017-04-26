package com.example.calculator.utils;

import android.os.Handler;

import com.example.calculator.models.ValCurs;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RequestExecutor {

    private static final String URL_ADDRESS = "http://www.cbr.ru/scripts/XML_daily.asp";
    private static final int READ_TIMEOUT_MILLIS = 30000;
    private static final int CONNECTION_TIMEOUT_MILLIS = 5000;
 //   private static RequestExecutor instance;
    private final Map<Long, Handler> sHandlerByThreadId = new ConcurrentHashMap<>();
    private ExecutorService mExecutor = Executors.newFixedThreadPool(1);

    public interface ResponseListener {
        void onResponseListener(ValCurs result, String error);
    }

  //  public static RequestExecutor getInstance() {
  //      return instance == null ? instance = new RequestExecutor() : instance;
  //  }

    public void getExchangeRates(final ResponseListener listener) {
        final Handler handler = getCurrentHandler();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                ValCurs result = null;
                String error = null;
                try {
                    result = performCall();
                } catch (Exception e) {
                    error = e.getLocalizedMessage();
                } finally {
                    final ValCurs retResult = result;
                    final String errorResult = error;
                    if (handler != null)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onResponseListener(retResult, errorResult);
                                }
                            }
                        });
                }
            }
        });
    }

    private Handler getCurrentHandler() {
        Handler handler = sHandlerByThreadId.get(Thread.currentThread().getId());
        if (handler == null) {
            synchronized (sHandlerByThreadId) {
                Handler newHandler = sHandlerByThreadId.get(Thread.currentThread().getId());
                if (newHandler == null) {
                    newHandler = new Handler();
                    sHandlerByThreadId.put(Thread.currentThread().getId(), newHandler);
                    handler = newHandler;
                }
            }
        }
        return handler;
    }

    private ValCurs performCall() {
        HttpURLConnection urlConnection;
        try {
            URL mUrl = new URL(URL_ADDRESS);
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
            urlConnection.setReadTimeout(READ_TIMEOUT_MILLIS);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Persister serializer = new Persister();
                    return serializer.read(ValCurs.class, inputStream, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
