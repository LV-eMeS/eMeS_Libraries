package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.MS_BadSetupException;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

/**
 * REST call handler as utility class, which uses REST assured to perform {@link MS_HttpRequest} and
 * return already processed {@link MS_HttpResponse} corresponding to given request.
 *
 * @author maris.salenieks
 * @version 1.0.
 * @since 2.1.9
 */
public class MS_HttpCallHandler {

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private MS_HttpCallHandler() {
    }

    /**
     * Performs call with given request specifications.
     *
     * @param request any valid {@link MS_HttpRequest}.
     * @return response object.
     * @throws IOException if the request could not be executed due to cancellation,
     *                     a connectivity problem or timeout. Because networks can fail during an exchange,
     *                     it is possible that the remote server accepted the request before the failure.
     */
    public static MS_HttpResponse call(MS_HttpRequest request) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(request.getClientConfigurations());
        Objects.requireNonNull(request.getUrl());
        Objects.requireNonNull(request.getMethod());

        Request.Builder reqBuilder = new Request.Builder();
        switch (request.getMethod()) {
            case GET:
                if (request.getParameters() != null) {
                    HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(request.getUrl())).newBuilder();
                    request.getParameters().forEach(httpBuilder::addQueryParameter);
                    reqBuilder.url(httpBuilder.build());
                } else {
                    reqBuilder.url(request.getUrl());
                }
                reqBuilder.get();
                break;
            case POST:
                reqBuilder.post(formBody(request)).url(request.getUrl());
                break;
            case PUT:
                reqBuilder.put(formBody(request)).url(request.getUrl());
                break;
            case DELETE:
                if (request.getBody() != null) {
                    reqBuilder.delete(formBody(request));
                } else if (request.getBodyAsString() != null) {
                    reqBuilder.delete(formBody(request));
                } else {
                    reqBuilder.delete();
                }
                reqBuilder.url(request.getUrl());
                break;
            default:
                throw new MS_BadSetupException("Unsupported HTTP request method " + request.getMethod());
        }
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(reqBuilder::addHeader);
        }
        Response response = request.getClientConfigurations().newCall(reqBuilder.build()).execute();

        MS_HttpResponse res = new MS_HttpResponse()
                .withUrl(request.getUrl())
                .withMethod(request.getMethod())
                .withResponse(response)
                .withResponseCode(response.code());

        res.withBodyAsString(Objects.requireNonNull(response.body()).string());
        if (isResponseJSON(res.getBodyAsString()))
            res.withBody(new JSONObject(res.getBodyAsString()));
        response.headers().toMultimap().forEach((headerName, headerValues) -> res.withHeader(headerName, headerValues.get(0)));
        return res;
    }

    private static boolean isResponseJSON(String response) {
        if (response != null) {
            String trimmedResponse = response.trim();
            return trimmedResponse.startsWith("{") || trimmedResponse.startsWith("[");
        } else {
            return false;
        }
    }

    private static RequestBody formBody(MS_HttpRequest request) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        MediaType bodyMediaType;
        String content;

        if (request.getBodyAsString() != null) {
            bodyMediaType = MEDIA_TYPE_MARKDOWN;
            content = request.getBodyAsString();
        } else if (request.getBody() != null) {
            bodyMediaType = MEDIA_TYPE_JSON;
            Objects.requireNonNull(request.getBody());
            content = request.getBody().toString();
        } else {
            bodyMediaType = MEDIA_TYPE_MARKDOWN;
            content = "";
        }

        bodyBuilder.addPart(MultipartBody.Part.create(FormBody.create(bodyMediaType, content)));
        if (request.getParameters() != null) {
            request.getParameters().forEach(bodyBuilder::addFormDataPart);
        }
        return bodyBuilder.build();
    }
}
