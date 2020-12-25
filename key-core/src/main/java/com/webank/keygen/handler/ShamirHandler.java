package com.webank.keygen.handler;
/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.webank.keygen.utils.GF256Util;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ShamirHandler
 *
 * @Description: ShamirHandler
 * @author graysonzhang
 * @date 2019-09-09 17:05:33
 *
 */
public class ShamirHandler {

    /**
     * Splits the given secret into n parts, of which any k or more can be combined to
     * recover the original secret.
     *
     * @param secret the secret to split
     * @return a map of n part IDs and their values
     */
    public static Map<Integer, byte[]> split(byte[] secret, SecureRandom random, int n, int k) {
        checkArgument(k > 1, "K must be > 1");
        checkArgument(n >= k, "N must be >= K");
        checkArgument(n <= 255, "N must be <= 255");
        // generate part values
        final byte[][] values = new byte[n][secret.length];
        for (int i = 0; i < secret.length; i++) {
            // for each byte, generate a random polynomial, p
            final byte[] p = GF256Util.generate(random, k - 1, secret[i]);
            for (int x = 1; x <= n; x++) {
                // each part's byte is p(partId)
                values[x - 1][i] = GF256Util.eval(p, (byte) x);
            }
        }

        // return as a set of objects
        final Map<Integer, byte[]> parts = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            parts.put(i + 1, values[i]);
        }
        return Collections.unmodifiableMap(parts);
    }

    /**
     * Joins the given parts to recover the original secret.
     *
     * <p><b>N.B.:</b> There is no way to determine whether or not the returned value is actually the
     * original secret. If the parts are incorrect, or are under the threshold value used to split the
     * secret, a random value will be returned.
     *
     * @param parts a map of part IDs to part values
     * @return the original secret
     * @throws IllegalArgumentException if parts is empty or contains values of varying lengths
     */
    public static byte[] join(Map<Integer, byte[]> parts) {
        
        checkArgument(parts.size() > 0, "No parts provided");
        final int[] lengths = parts.values().stream().mapToInt(v -> v.length).distinct().toArray();
        checkArgument(lengths.length == 1, "Varying lengths of part values");
        final byte[] secret = new byte[lengths[0]];
        for (int i = 0; i < secret.length; i++) {
            final byte[][] points = new byte[parts.size()][2];
            int j = 0;
            for (Map.Entry<Integer, byte[]> part : parts.entrySet()) {
                points[j][0] = part.getKey().byteValue();
                points[j][1] = part.getValue()[i];
                j++;
            }
            secret[i] = GF256Util.interpolate(points);
        }
        return secret;
    }


    private static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

}
