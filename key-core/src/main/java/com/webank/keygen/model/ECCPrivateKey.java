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
package com.webank.keygen.model;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.security.PrivateKey;


/**
 * @Description ECCPrivateKey which can transfer private key to pkcs8 thus capacitable with JCA
 * @author yuzhichu
 * @author graysonzhang
 * @date 2019-12-24 
 */
public class ECCPrivateKey implements PrivateKey {

    /** @Fields serialVersionUID : TODO */
    private static final long serialVersionUID = 3158067956204993701L;

    private final String FORMAT = "PKCS#8";
    private final String ALGORITHM = "ECDSA";

    private byte[] privateKey;
    private String eccName;

    public ECCPrivateKey(byte[] privateKey, String eccName) {
        this.privateKey = privateKey;
        this.eccName = eccName;
    }
    
    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    public byte[] getEncoded() {
        ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(eccName);
        X962Parameters params = new X962Parameters(curveOid);
        ECPrivateKey keyStructure = new ECPrivateKey(256, Numeric.toBigInt(privateKey), params);
        try {
            PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(
                    new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), keyStructure);
            return privateKeyInfo.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
