/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * Basic CORS REST API tests.
 */
class CorsFilterTest {
  public static String[] headers = new String[8];
  public static Integer count = 0;

  @Test
  @SuppressWarnings("rawtypes")
  void validCorsFilterTest() throws IOException, ServletException {
    CorsFilter filter = new CorsFilter(ZeppelinConfiguration.load());
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    FilterChain mockedFilterChain = mock(FilterChain.class);
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Origin")).thenReturn("http://localhost:8080");
    when(mockRequest.getMethod()).thenReturn("Empty");
    when(mockRequest.getServerName()).thenReturn("localhost");
    count = 0;

    doAnswer(new Answer() {
        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            headers[count] = invocationOnMock.getArguments()[1].toString();
            count++;
            return null;
        }
    }).when(mockResponse).setHeader(anyString(), anyString());

    filter.doFilter(mockRequest, mockResponse, mockedFilterChain);
    assertEquals("http://localhost:8080", headers[0]);
  }

  @Test
  @SuppressWarnings("rawtypes")
  void invalidCorsFilterTest() throws IOException, ServletException {
    CorsFilter filter = new CorsFilter(ZeppelinConfiguration.load());
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    FilterChain mockedFilterChain = mock(FilterChain.class);
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    when(mockRequest.getHeader("Origin")).thenReturn("http://evillocalhost:8080");
    when(mockRequest.getMethod()).thenReturn("Empty");
    when(mockRequest.getServerName()).thenReturn("evillocalhost");

    doAnswer(new Answer() {
        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            headers[count] = invocationOnMock.getArguments()[1].toString();
            count++;
            return null;
        }
    }).when(mockResponse).setHeader(anyString(), anyString());

    filter.doFilter(mockRequest, mockResponse, mockedFilterChain);
    assertEquals("", headers[0]);
  }
}
