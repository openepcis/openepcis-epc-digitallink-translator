/*
 * Copyright 2022 benelog GmbH & Co. KG
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package io.openepcis.epc.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.openepcis.epc.translator.exception.UrnDLTransformationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GCPLengthProvider {
  private static final HttpClient client =
      HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .followRedirects(HttpClient.Redirect.NORMAL)
          .connectTimeout(Duration.ofSeconds(50))
          .sslContext(disableSSLCertificateChecking())
          .build();
  private static final Map<String, Integer> mapOfPrefixGcpLength = new HashMap<>();
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().registerModule(new JavaTimeModule());
  private static GCPLengthProvider gcpLengthProvider;

  private GCPLengthProvider() {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "https://www.gs1.org/sites/default/files/docs/gcp_length/gcpprefixformatlist.json"))
            .build();

    Map<String, Object> gcpPrefixFormatList;
    try {
      final HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString());
      gcpPrefixFormatList = OBJECT_MAPPER.readValue(response.body(), Map.class);
      final List<Map<String, Object>> list =
          (List<Map<String, Object>>)
              ((Map<String, Object>) gcpPrefixFormatList.get("GCPPrefixFormatList")).get("entry");

      for (Map<String, Object> m : list) {
        mapOfPrefixGcpLength.put(
            m.get("prefix").toString(), Integer.valueOf(m.get("gcpLength").toString()));
      }
    } catch (IOException | InterruptedException e) {
      log.error("GCPPrefixFormatList cannot be retrieved", e);
      throw new UrnDLTransformationException(e.getLocalizedMessage(), e);
    }
  }

  // static method to create instance of Singleton class
  public static GCPLengthProvider getInstance() {
    if (gcpLengthProvider == null) gcpLengthProvider = new GCPLengthProvider();
    return gcpLengthProvider;
  }

  public int getGcpLength(String id) {
    String theId = id;
    if (theId.length() > 13) {
      theId = theId.substring(1);
    }
    for (Map.Entry<String, Integer> e : mapOfPrefixGcpLength.entrySet()) {
      if (theId.startsWith(e.getKey())) {
        return e.getValue();
      }
    }
    return 7;
  }

  private static SSLContext disableSSLCertificateChecking() {
    SSLContext sc;
    TrustManager[] trustAllCerts =
        new TrustManager[] {
          new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
              // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
              // Not implemented
            }
          }
        };

    try {
      sc = SSLContext.getInstance("TLS");

      sc.init(null, trustAllCerts, new java.security.SecureRandom());
    } catch (KeyManagementException | NoSuchAlgorithmException exception) {
      log.error("While configuring HTTP client", exception);
      throw new RuntimeException("While configuring HTTP client", exception);
    }
    return sc;
  }
}
