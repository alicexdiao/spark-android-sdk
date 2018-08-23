/*
 * Copyright 2016-2018 Cisco Systems Inc
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

package com.ciscospark.androidsdk.internal;

import java.io.IOException;

import com.ciscospark.androidsdk.Result;
import com.ciscospark.androidsdk.SparkError;

import me.helloworld.utils.Objects;
import me.helloworld.utils.annotation.StringPart;
import retrofit2.Response;

public class ResultImpl<T> implements Result<T> {

    public static <T> Result<T> success(T data) {
        return new ResultImpl<T>(data, null);
    }

    public static <T> Result<T> error(String message) {
        return new ResultImpl<T>(null, new SparkError(SparkError.ErrorCode.UNEXPECTED_ERROR, message));
    }

    public static <T> Result<T> error(Throwable t) {
        return new ResultImpl<T>(null, makeError(t));
    }

    public static <T> Result<T> error(SparkError error) {
        return new ResultImpl<T>(null, error);
    }

    public static <T> Result<T> error(Response response) {
        return new ResultImpl<T>(null, makeError(response));
    }

    @StringPart
    private T _data;

    @StringPart
    private SparkError _error;

    public ResultImpl(T data, SparkError error) {
        _data = data;
        _error = error;
    }

    public boolean isSuccessful() {
        return _error == null;
    }

    public SparkError getError() {
        return _error;
    }

    public T getData() {
        return _data;
    }

    public String toString() {
        return Objects.toStringByAnnotation(this);
    }

    private static SparkError makeError(Throwable t) {
        return new SparkError(SparkError.ErrorCode.UNEXPECTED_ERROR, t.toString());
    }

    private static SparkError makeError(Response res) {
        StringBuilder message = new StringBuilder().append(res.code()).append("/").append(res.message());
        try {
            String body = res.errorBody().string();
            message.append("/").append(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SparkError(SparkError.ErrorCode.SERVICE_ERROR, message.toString());
    }
}
