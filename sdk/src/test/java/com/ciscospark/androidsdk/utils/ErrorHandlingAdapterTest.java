/*
 * Copyright 2016-2017 Cisco Systems Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ciscospark.androidsdk.utils;

import java.io.IOException;

import com.ciscospark.androidsdk.utils.http.ErrorHandlingAdapter;
import com.ciscospark.androidsdk.utils.http.ErrorHandlingAdapter.ErrorHandlingCall;
import com.ciscospark.androidsdk.utils.http.ErrorHandlingAdapter.ErrorHandlingCallAdapterFactory;
import com.ciscospark.androidsdk.utils.http.ErrorHandlingAdapter.ErrorHandlingCallback;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.POST;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created on 10/07/2017.
 */
public class ErrorHandlingAdapterTest {
	private MockWebServer mockWebServer;
	private AuthService mAuthService;
	private ErrorHandlingCall<ResponseBody> mCall;

	interface AuthService {
		@POST("login")
		ErrorHandlingCall<ResponseBody> getError();
	}

	@Before
	public void setUp() throws Exception {
		mockWebServer = new MockWebServer();

		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(mockWebServer.url("").toString())
			.addCallAdapterFactory(new ErrorHandlingCallAdapterFactory())
			.build();
		mAuthService = retrofit.create(AuthService.class);
		mCall = mAuthService.getError();
	}


	@Test
	public void testSuccess() throws Exception {
		ErrorHandlingAdapter adapter = new ErrorHandlingAdapter();
		mockWebServer.enqueue(new MockResponse().setResponseCode(200));
		mCall.clone().enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				System.out.println("success");
				assertTrue(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void clientError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void serverError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void networkError(IOException e) {
				assertFalse(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				assertFalse(true);
			}
		});
		Thread.sleep(2 * 1000);
	}

	@Test
	public void testError401() throws Exception {
		mockWebServer.enqueue(new MockResponse().setResponseCode(401));
		mAuthService.getError().enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				assertFalse(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				System.out.print("unauthenticated");
				assertTrue(true);
			}

			@Override
			public void clientError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void serverError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void networkError(IOException e) {
				assertFalse(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				assertFalse(true);
			}
		});
		Thread.sleep(2 * 1000);
	}

	@Test
	public void testError400() throws Exception {
		mockWebServer.enqueue(new MockResponse().setResponseCode(400));
		mCall.clone().enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				assertFalse(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void clientError(Response<?> response) {
				System.out.print("clientError");
				assertTrue(true);
			}

			@Override
			public void serverError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void networkError(IOException e) {
				assertFalse(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				assertFalse(true);
			}
		});
		Thread.sleep(2 * 1000);
	}

	@Test
	public void testError404() throws Exception {
		mockWebServer.enqueue(new MockResponse().setResponseCode(404));
		mCall.clone().enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				assertFalse(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void clientError(Response<?> response) {
				System.out.print("clientError");
				assertTrue(true);
			}

			@Override
			public void serverError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void networkError(IOException e) {
				assertFalse(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				assertFalse(true);
			}
		});
		Thread.sleep(2 * 1000);
	}

	@Test
	public void testError500() throws Exception {
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mCall.clone().enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				assertFalse(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void clientError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void serverError(Response<?> response) {
				System.out.print("serverError");
				assertTrue(true);
			}

			@Override
			public void networkError(IOException e) {
				assertFalse(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				assertFalse(true);
			}
		});
		Thread.sleep(2 * 1000);
	}

	@Test
	public void testErrorUnexpectedResponse() throws Exception {
		mockWebServer.enqueue(new MockResponse().setResponseCode(999));
		mCall.clone().enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				assertFalse(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void clientError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void serverError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void networkError(IOException e) {
				assertFalse(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				System.out.print("serverError");
				assertTrue(true);
			}
		});
		Thread.sleep(2 * 1000);
	}

	@Test
	public void testErrorIOError() throws Exception {
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
		mCall.enqueue(new ErrorHandlingCallback<ResponseBody>() {
			@Override
			public void success(Response<ResponseBody> response) {
				assertFalse(true);
			}

			@Override
			public void unauthenticated(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void clientError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void serverError(Response<?> response) {
				assertFalse(true);
			}

			@Override
			public void networkError(IOException e) {
				System.out.print("networkError");
				assertTrue(true);
			}

			@Override
			public void unexpectedError(Throwable t) {
				assertFalse(true);
			}
		});
		mCall.cancel();
		Thread.sleep(2 * 1000);
	}
}