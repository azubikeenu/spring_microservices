package com.azubike.msscbreweryclient.web.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

// @Component
public class NIORestTemplateCustomizer implements RestTemplateCustomizer {
  public ClientHttpRequestFactory clientHttpRequestFactory() throws IOReactorException {
    DefaultConnectingIOReactor defaultConnectingIOReactor =
        new DefaultConnectingIOReactor(
            IOReactorConfig.custom()
                .setConnectTimeout(3000)
                .setIoThreadCount(4)
                .setSoTimeout(3000)
                .build());

    final PoolingNHttpClientConnectionManager poolingHttpClientConnectionManager =
        new PoolingNHttpClientConnectionManager(defaultConnectingIOReactor);

    CloseableHttpAsyncClient closeableHttpAsyncClient =
        HttpAsyncClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
    return new HttpComponentsAsyncClientHttpRequestFactory(closeableHttpAsyncClient);
  }

  @Override
  public void customize(RestTemplate restTemplate) {
    try {
      restTemplate.setRequestFactory(clientHttpRequestFactory());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
