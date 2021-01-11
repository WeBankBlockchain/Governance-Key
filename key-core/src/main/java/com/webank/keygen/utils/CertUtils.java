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
package com.webank.keygen.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * @Description CertUtils
 * @author yuzhichu
 * @author wesleywang
 * @date 2019-12-25 
 */
@Slf4j
public class CertUtils {
	
	private static JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA1withRSA");
	private static JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
	private static SecureRandom random;
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		random = new SecureRandom();
	}
	
	public static X509Certificate generateDummyCertificate()
					throws Exception
	{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
		keyPairGenerator.initialize(4096, random);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		final Instant now = Instant.now();
		final Date notBefore = Date.from(now);
		final Date notAfter = Date.from(now.plus(Duration.ofDays(10000)));

		final ContentSigner contentSigner = signerBuilder.build(keyPair.getPrivate());
		final X500Name x500Name = new X500Name("CN=whatever");
		final X509v3CertificateBuilder certificateBuilder =
				new JcaX509v3CertificateBuilder(x500Name,
						BigInteger.valueOf(now.toEpochMilli()),
						notBefore,
						notAfter,
						x500Name,
						keyPair.getPublic())
				.addExtension(Extension.subjectKeyIdentifier, false, getSubjectKeyId(keyPair.getPublic()))
				.addExtension(Extension.authorityKeyIdentifier, false, getAuthorityKeyId(keyPair.getPublic()))
				.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

		return certConverter
				.setProvider(new BouncyCastleProvider())
				.getCertificate(certificateBuilder.build(contentSigner));
	}

	private static SubjectKeyIdentifier getSubjectKeyId(final PublicKey publicKey) throws OperatorCreationException {
		final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		final DigestCalculator digCalc =
				new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

		return new X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo);
	}


	private static AuthorityKeyIdentifier getAuthorityKeyId(final PublicKey publicKey)
			throws OperatorCreationException
	{
		final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		final DigestCalculator digCalc =
				new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

		return new X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
	}
}
