package com.azubike.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {
  private final int maxTotal;
  private final int maxRoute;
  private final int requestTimeOut;
  private final int socketTimeOut;

  public BlockingRestTemplateCustomizer(
      @Value("${sfg.client.connection.maxTotal}") int maxTotal,
      @Value("${sfg.client.connection.maxRoute}") int maxRoute,
      @Value("${sfg.client.connection.requestTimeOut}") int requestTimeOut,
      @Value("${sfg.client.connection.socketTimeOut}") int socketTimeOut) {
    this.maxTotal = maxTotal;
    this.maxRoute = maxRoute;
    this.requestTimeOut = requestTimeOut;
    this.socketTimeOut = socketTimeOut;
  }

  public ClientHttpRequestFactory clientHttpRequestFactory() {
    // Set up a connection factory
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(maxTotal); // set the max total connections
    connectionManager.setDefaultMaxPerRoute(
        maxRoute); // set the max connections to a specific route

    // Setup a Request Configurations
    RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectionRequestTimeout(requestTimeOut) // Set request timeout to 3secs ie if the request is taking longer than 3secs
                // it would error and fail
            .setSocketTimeout(socketTimeOut) // Set socket timeout to 3secs
            .build();

    // Implement a closable HttpClient
    CloseableHttpClient closeableHttpClient =
        HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
            .setDefaultRequestConfig(requestConfig)
            .build();

    return new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
  }

  @Override
  public void customize(RestTemplate restTemplate) {
    restTemplate.setRequestFactory(clientHttpRequestFactory());
  }
}
