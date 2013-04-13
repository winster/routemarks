/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.winster.routemarks.rest.google;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.common.base.Preconditions;

/**
 * Utility class for Google service related tasks, for example JDO persistence, OAuth flow helpers,
 * and others.
 *
 * @author Matthias Linder (mlinder)
 */
public class ServiceUtils {

  /** Global instance of the HTTP transport. */
  static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  /** Global instance of the JSON factory. */
  static final JsonFactory JSON_FACTORY = new JacksonFactory();
  
  /** Global instance of the Credential store. */
  static final AppEngineCredentialStore CREDENTIAL_STORE = new AppEngineCredentialStore();

  private static GoogleClientSecrets clientSecrets = null;

  static GoogleClientSecrets getClientCredential() throws IOException {
    if (clientSecrets == null) {
      clientSecrets = GoogleClientSecrets.load(
          JSON_FACTORY, ServiceUtils.class.getResourceAsStream("/client_secrets.json"));
      Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("Enter ")
          && !clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=bigquery "
          + "into bigquery-appengine-sample/src/main/resources/client_secrets.json");
    }
    return clientSecrets;
  }

  public static String getRedirectUri(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
    url.setRawPath("/oauth2callback");
    return url.build();
  }
  
  public static void deleteCredentials(String userId) throws IOException {
    Credential credential = newFlow().loadCredential(userId);
    if (credential != null) {
      CREDENTIAL_STORE.delete(userId, credential);
    }
  }

  public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
        getClientCredential(), Collections.singleton(PlusScopes.PLUS_ME)).setCredentialStore(
        CREDENTIAL_STORE).setAccessType("offline").build();
  }

  public static Plus loadPlusClient(String userId) throws IOException {
    Credential credential = newFlow().loadCredential(userId);
    return new Plus.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("TheSafeRoute/1.0").build();
  }

  private ServiceUtils() {}
}