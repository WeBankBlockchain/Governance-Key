package com.webank.keygen.service;

import com.webank.keygen.BaseTest;
import com.webank.keygen.model.X500NameInfo;
import com.webank.keygen.utils.CertUtils;
import com.webank.keygen.utils.KeyUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

public class CertServiceTest extends BaseTest {


    private CertService certService =new CertService();

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }
    private static final String SIGNATURE_ALGORITHM = "SHA256WITHRSA";

    private String caKey = "MIIEowIBAAKCAQEAp5hBxXPHSlY5KtuBWExboMLboVzDPtlypJ6cEeAT8o8mFFUG\n" +
            "LDgSjtTD+zjFYOaLg1d83GcLl0SDBQoaTdZQzoQ7HQHirSyk25kaHR3vdaVSBLE6\n" +
            "n25lCaenfoqSLEyYfkKipyB4ZXG99M+rtDf3+JZXW5ELEvlJ9IHHV77pn84Inp1x\n" +
            "Gu6k+2tQoE1Yw0cp9yaPhP3qJLj5+kpPgCiS2fqpgamCE9e97ulEZvioHHGGyffb\n" +
            "Bmb3k7ATvXLkow127miaxWHFi/QEJ5h40ByDwWozF5wSe03Ur53wJ1/uTOrt+900\n" +
            "ek8j7JxzfJeEUzi5J+YWen8xNsHQSbvHr5405wIDAQABAoIBAQChptWBy4vlYsdf\n" +
            "VDwtW/FhXbIUsXHNtFXE/QvSngz/gB2drOj4S9lylTy3m2mebqEQvZk8ydO/QyER\n" +
            "Ak6e12I34AlYkFIA8TfObJ1oilBHbH9w8TV3RDcGmgSPpL8bJYJM/p7+ju4yAwTT\n" +
            "FmAqw3VV/EUkmAFTespfobISK54SOUYw5zoJQyOspj/lBlF4i+KKDcTDx6Riuwa7\n" +
            "wLngsYu1n6JBoFBm1e1H1g5SVye4zlQf9gdVzf117N/Xa69nTUPOaHkc2zwnwqI3\n" +
            "z6DIf+uKKGkllarc2ZEh/I4aR2EklusOJxk5baUkvgItMSq2aHbw2X2LKmr8tDB/\n" +
            "5cWN62CBAoGBAPZe+nCUWzSlSgNOOaBYSkDezuQzZnPEURljhqeFcqdlWxwjYnDj\n" +
            "hvDvVRh2fGtw48MuJbYNsrQ9zPrdaB9lT5UnYSIuGnhKh8e13L1g+Zv39UTghwkP\n" +
            "8x7glz9YYsu5wMB/+uohLNsU9fdIZsXp/rrXAkShZd3voEONIW6kyVinAoGBAK4l\n" +
            "GMEYgn+ulxWOzILAIi897nO8rtM1aVNDkklbrWSufENDlDpVhyUCcuCqBmM3Xm0f\n" +
            "wCZjvTIg1E3fqtf0kj+78JAzLqigja75kHIMFFaPkKiEpAFMIwFY7Vi+BbHTEwvx\n" +
            "dQVwTrJF910yoka5mBbP24UkZaKTlRwm5ZyMt4nBAoGAHSVFSVYzp/m51MRHSvHG\n" +
            "7v+syBBQmXdrBK7ieiTuWGFEMwL4nVQ9XXlivr8dnvQ+7ZMjAPOD5ZC+FBtnOveY\n" +
            "P5PmCM4dcYeXooegMoMrZEkkKd7J+sd5QnjdS7AgF+vEosFBJLuB+/Tx2CwnhFhX\n" +
            "OzE+YnIZg/TaJ8OlZdp2u1cCgYBMFArPePyx+T0p/tubl4KXru+4gkrCHMhpxtBm\n" +
            "2fVTUeMZo7FjFrBW284CFmV5/Nt0wvU4EES8XJlDeB5z//XQgDOlW6bbpmCfe4m+\n" +
            "OUa9VjT1WhUoN/HnCcmPBl0IhdUBV7gu6xSGT4i0n4VDbptiA+a8MN1x/BWdWeTf\n" +
            "0p3wQQKBgAlJYogOcZku4B0NIIvH6MGZekr/5p1jeOeO0SBOVSNPaZdtnMqnZBPm\n" +
            "IdWWk5Pykg9FJLJpjobhURO3J4T3Y9SC6lf6fX2Kib2jk843s+4KAlEQhblMOHpl\n" +
            "ZXo/AB1yvdkyYN3uRgQ1cWAP9D5eNtKaIzL3MQoJnMSXDJGDPquV";

    @Test
    public void testGenerateKPAndRootCert() throws Exception {
        X500NameInfo info = X500NameInfo.builder()
                .commonName("chain")
                .organizationName("fisco-bcos")
                .organizationalUnitName("chain")
                .build();
        certService.generateKPAndRootCert(info,"out");
    }

    @Test
    public void testGenerateRootCertByDefaultConf() throws Exception {
        X500NameInfo info = X500NameInfo.builder()
                .commonName("chain")
                .organizationName("fisco-bcos")
                .organizationalUnitName("chain")
                .build();
        String caStr = certService.generateRootCertByDefaultConf(info,caKey);

        System.out.println(caStr);
    }

    @Test
    public void testGenerateChildCertByDefaultConf() throws Exception {
        //填入：ca证书字符串
        String caStr = "";
        //填入：子证书请求字符串
        String csrStr = "";
        //第一种方式：参数为字符串
        String childStr = certService.generateChildCertByDefaultConf(caStr,csrStr,caKey);
        System.out.println(childStr);
        //第二种方式：参数为文件路径
        String childStr2 = certService.generateChildCertByDefaultConf("out/ca.crt","out/child.csr",
                "out/ca_pri.key", "out/childByFile.crt");
        System.out.println(childStr2);
    }

    @Test
    public void testGenerateCertRequestByDefaultConf() throws Exception{
        X500NameInfo info = X500NameInfo.builder()
                .commonName("agency")
                .organizationalUnitName("agency")
                .organizationName("fisco-bcos")
                .build();
        KeyPair keyPair = KeyUtils.generateKeyPair();
        String hex = Numeric.toHexString(keyPair.getPrivate().getEncoded());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Numeric.hexStringToByteArray(hex));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);


        String csrStr = certService.generateCertRequestByDefaultConf(info,
               "asdasd");
//        String csrStr = certService.generateCertRequestByDefaultConf(info,
//                CertUtils.readPEMAsString(keyPair.getPrivate()));
        System.out.println(csrStr);
    }

    @Test
    public void testCreateRootCertificate() throws Exception {
        X500NameInfo info = X500NameInfo.builder()
                .commonName("chain")
                .organizationName("fisco-bcos")
                .organizationalUnitName("chain")
                .build();
        KeyPair keyPair = KeyUtils.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Date beginDate = new Date();
        Date endDate = new Date(beginDate.getTime() + 3650 * 24L * 60L * 60L * 1000);
        X509Certificate certificate = certService.createRootCertificate( SIGNATURE_ALGORITHM, info,
                null, beginDate,endDate,publicKey,privateKey);
        certificate.verify(publicKey);
        new File("out").mkdirs();
        CertUtils.writeCrt(certificate,"out/ca.crt");
    }


    @Test
    public void testCreateCertRequest() throws OperatorCreationException, FileNotFoundException {
        X500NameInfo info = X500NameInfo.builder()
                .commonName("agency")
                .organizationalUnitName("agency")
                .organizationName("fisco-bcos")
                .build();
        //ECDSA密钥对，用于节点密钥对
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
//        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
//        keyPairGenerator.initialize(ecGenParameterSpec, SECURE_RANDOM);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();

        KeyPair keyPair = KeyUtils.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        CertUtils.writeKey(privateKey,"out/agency.key");
        PKCS10CertificationRequest request = certService.createCertRequest(info, publicKey, privateKey,
                SIGNATURE_ALGORITHM);
        CertUtils.writeCsr(request, "out/child.csr");
        CertUtils.readCsr("out/child.csr");
    }


    @Test
    public void testCreateChildCertificate() throws Exception {
        Date beginDate = new Date();
        Date endDate = new Date(beginDate.getTime() + 3650 * 24L * 60L * 60L * 1000);

//        PKCS10CertificationRequest request = CertUtils.readCsr("out/child.csr");
//        X509Certificate parentCert = CertUtils.readCrt("out/ca.crt");
//        PEMKeyPair pemKeyPair=  CertUtils.readKey("out/ca.key");
//        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(
//                new PKCS8EncodedKeySpec(pemKeyPair.getPrivateKeyInfo().getEncoded()));
//        X509Certificate childCert = certService.createChildCertificate(true,SIGNATURE_ALGORITHM, parentCert,
//                request,null, beginDate, endDate,privateKey);
//        childCert.verify(parentCert.getPublicKey());
    }
}
