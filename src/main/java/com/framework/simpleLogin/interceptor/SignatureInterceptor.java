package com.framework.simpleLogin.interceptor;

import com.framework.simpleLogin.exception.InvalidSignatureException;
import com.framework.simpleLogin.exception.MissingRequestHeaderException;
import com.framework.simpleLogin.utils.CONSTANT;
import com.framework.simpleLogin.utils.Encryption;
import com.framework.simpleLogin.utils.Gadget;
import com.framework.simpleLogin.utils.RedisUtil;
import com.framework.simpleLogin.wrapper.ContentCachingRequestWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
public class SignatureInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request);

        String clientSign = cachedRequest.getHeader("X-Signature");
        String timestamp = cachedRequest.getHeader("X-Timestamp");
        String nonce = cachedRequest.getHeader("X-Nonce");

        if (Gadget.StringUtils.isEmpty(clientSign) ||
                Gadget.StringUtils.isEmpty(timestamp) ||
                Gadget.StringUtils.isEmpty(nonce)) {
            throw new MissingRequestHeaderException("Missing signature header");
        }

        /*
         * Checks if a nonce has been used before and prevents duplicate usage.
         *
         * @param nonce the nonce to be checked
         * @throws InvalidSignatureException if the nonce has been used before
         */
        if (redisUtil.hasKey(CONSTANT.CACHE_NAME.API_SIGNATURE + ":" + nonce)) {
            // Throw an exception if the nonce has been used before
            throw new InvalidSignatureException("Duplicate nonce");
        } else {
            // Mark the nonce as used in the cache with a TTL
            redisUtil.set(
                    CONSTANT.CACHE_NAME.API_SIGNATURE + ":" + nonce,
                    "used",
                    CONSTANT.CACHE_EXPIRATION_TIME.API_SIGNATURE
            );
        }

        /*
         * Verifies if the provided timestamp is within the valid time range.
         *
         * @param timestamp the timestamp to be verified
         * @throws InvalidSignatureException if the timestamp is expired or invalid
         */
        try {
            if (Math.abs(System.currentTimeMillis() - Long.parseLong(timestamp)) > CONSTANT.CACHE_EXPIRATION_TIME.API_SIGNATURE) {
                // Throw an exception if the timestamp has expired
                throw new InvalidSignatureException("Timestamp expired");
            }
        } catch (NumberFormatException e) {
            // Throw an exception if the timestamp is not a valid number
            throw new InvalidSignatureException("Invalid timestamp");
        }

        /*
         * Construct the signature structure
         * Query and body cannot exist at the same time.
         *
         * Format: Method/URI?query&body&timestamp&nonce
         * Example:
         *     POST /api/test ?string=Hello&suffix=World &body={"separate":","} &timestamp=1739002152986 &nonce=3d1cff
         *     ---  ---------  -------------------------  --------------------   -----------------------  ------------
         *   Method    URI             Query                     Body                  Timestamp              Nonce
         */
        String dataToSign = Gadget.StringUtils.hideSensitive(
                Gadget.StringUtils.signatureStringFormat(
                        cachedRequest.getMethod(),
                        cachedRequest.getRequestURI(),
                        cachedRequest.getQueryString() == null ? "" : cachedRequest.getQueryString(),
                        new String(cachedRequest.getCachedBody(), StandardCharsets.UTF_8)
                                .replaceAll("[\\r\\n ]", ""),
                        timestamp, nonce
                )
        );

        // Re-encrypt to determine whether the encrypted ciphertext is consistent with the original ciphertext.
        if (!Encryption.hmacSHA256(dataToSign).equals(clientSign)) {
            throw new InvalidSignatureException("Invalid signature");
        }

        return true;
    }
}
