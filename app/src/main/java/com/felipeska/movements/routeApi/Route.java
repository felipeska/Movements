package com.felipeska.movements.routeApi;

import com.felipeska.movements.db.Location;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 *@author felipeska
 */
public class Route {

  public interface CallbackRoute{
    public void onSuccess(String response);
    public void onFailed();
  }

  public void download(String url, CallbackRoute callbackRoute) throws Exception {
    final OkHttpClient client = new OkHttpClient();
    final CallbackRoute callback = callbackRoute;
    Request request = new Request.Builder()
            .url(url)
            .build();
    System.out.println("Url: "+url);
    client.newCall(request).enqueue(new Callback() {
      @Override public void onFailure(Request request, IOException e) {
        e.printStackTrace();
        callback.onFailed();
      }

      @Override public void onResponse(Response response) throws IOException {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        callback.onSuccess(response.body().string());
      }
    });
  }

  public static final class UrlBuilder {
    private final StringBuilder url = new StringBuilder();
    private String endpoint;
    private String output = "json";
    private Location origin;
    private Location destination;
    private boolean sensor = false;

    public UrlBuilder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
    }

    public UrlBuilder output(String output) {
      this.output = output;
      return this;
    }

    public UrlBuilder origin(Location origin) {
      this.origin = origin;
      return this;
    }

    public UrlBuilder destination(Location destination) {
      this.destination = destination;
      return this;
    }

    public String build() {
      url.append(endpoint);
      url.append(output);
      url.append("?");
      if (origin != null) {
        url.append("origin=");
        url.append(origin.latitude() + "," + origin.longitude());
      }
      url.append("&");
      if (destination != null) {

      }
      url.append("destination=");
      url.append("4.6573371,-74.0580698");
      url.append("&");
      url.append("sensor=");
      url.append(sensor);
      return url.toString();
    }
  }
}
