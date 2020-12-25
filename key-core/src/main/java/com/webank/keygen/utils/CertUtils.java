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
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
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

	public static PEMKeyPair readKey(String filePath) throws FileNotFoundException {
		Object object = readPEMObject(filePath);
		if (object instanceof PEMKeyPair) {
			return (PEMKeyPair) object;
		}
		return null;
	}

	public static Key readRSAKey(String filePath) throws Exception {
		Object object = readPEMObject(filePath);
		if (object instanceof PEMKeyPair) {
			return KeyFactory.getInstance("RSA").generatePrivate(
					new PKCS8EncodedKeySpec(((PEMKeyPair)object).getPrivateKeyInfo().getEncoded()));
		}
		return null;
	}

	public static void writeKey(Key key, String filePath) {
		writeToFile(key, filePath);
	}

	public static X509Certificate readCrt(String filePath) throws CertificateException, FileNotFoundException {
		Object object = readPEMObject(filePath);
		if (object instanceof X509CertificateHolder) {
			return new JcaX509CertificateConverter().setProvider("BC")
					.getCertificate((X509CertificateHolder) object);
		}
		return null;
	}

	public static X509Certificate convertStrToCert(String crtStr) throws CertificateException {
		Object object = readStringAsPEM(crtStr);
		if (object instanceof X509CertificateHolder) {
			return new JcaX509CertificateConverter().setProvider("BC")
					.getCertificate((X509CertificateHolder) object);
		}
		return null;
	}

	public static PKCS10CertificationRequest convertStrToCsr(String csrStr) {
		Object object = readStringAsPEM(csrStr);
		if (object instanceof PKCS10CertificationRequest) {
			return (PKCS10CertificationRequest) object;
		}
		return null;
	}

	public static void writeCrt(X509Certificate certificate, String filePath) throws FileNotFoundException {
		writeToFile(certificate, filePath);
	}

	public static PKCS10CertificationRequest readCsr(String filePath) throws FileNotFoundException {
		Object object = readPEMObject(filePath);
		if (object instanceof PKCS10CertificationRequest) {
			return (PKCS10CertificationRequest) object;
		}
		return null;
	}

	public static void writeCsr(PKCS10CertificationRequest request, String filePath) throws FileNotFoundException {
		writeToFile(request, filePath);
	}

	public static void writeToFile(Object object, String filePath) {
		try (JcaPEMWriter pw = new JcaPEMWriter(new FileWriter(filePath))) {
			pw.writeObject(object);
		} catch (IOException e) {
			log.error("writeObject failed", e);
		}
	}

	public static Object readPEMObject(String filePath) throws FileNotFoundException {
		if (!FileOperationUtils.exist(filePath)){
			throw new FileNotFoundException("filePath does't exist，path = " + filePath);
		}
		PemReader pemReader = null;
		Object object = null;
		try {
			pemReader = new PemReader(new FileReader(filePath));
			PEMParser pemParser = new PEMParser(pemReader);
			object = pemParser.readObject();
		} catch (IOException e) {
			log.error("readPEMObject failed", e);
		} finally {
			if (pemReader != null) {
				try {
					pemReader.close();
				} catch (IOException e) {
					log.error("pemReader.close failed", e);
				}
			}
		}
		return object;
	}

	public static String readPEMAsString(Object object){
		String result = null;
		StringWriter writer = new StringWriter();
		JcaPEMWriter pw = new JcaPEMWriter(writer);
		try {
			pw.writeObject(object);
		} catch (IOException e) {
			log.error("pw.writeObject failed ",e);
		}finally {
			try {
				writer.close();
				pw.close();
			} catch (IOException e) {
				log.error("io close failed", e);
			}
		}
		if (writer.getBuffer() != null) {
			result = writer.getBuffer().toString();
		}
		return result;
	}

	public static Object readStringAsPEM(String string){
		StringReader reader = new StringReader(string);
		PemReader pemReader = new PemReader(reader);
		PEMParser pemParser = new PEMParser(pemReader);
		Object object = null;
		try {
			object = pemParser.readObject();
		} catch (IOException e) {
			log.error("readPEMObject failed", e);
		} finally {
			try {
				reader.close();
				pemReader.close();
			} catch (IOException e) {
				log.error("pemReader.close failed", e);
			}
		}
		return object;
	}
}
