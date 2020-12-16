package com.webank.keygen.service;

import com.webank.keygen.constants.CertConstants;
import com.webank.keygen.handler.X509CertHandler;
import com.webank.keygen.model.X500NameInfo;
import com.webank.keygen.utils.CertUtils;
import com.webank.keygen.utils.FileOperationUtils;
import com.webank.keygen.utils.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;


/**
 * CertService
 *
 * @author wesleywang
 * @Description CertService
 * @date 2020-05-10
 */
@Slf4j
public class CertService {


    /**
     * generate RSA keyPair and CA certificate by default configuration (signature algorithm is SHA256WITHRSA,
     * valid for 3650 days) , the generated certificate and key will be saved in file that specifies the path
     *
     * @param issuer   issuer information
     * @param savePath path of the generated keys and certificate
     */
    public void generateKPAndRootCert(X500NameInfo issuer, String savePath) {
        try {
            if (!FileOperationUtils.exist(savePath)) {
                throw new FileNotFoundException("savePath does't exist，path = " + savePath);
            }
            Date beginDate = new Date();
            Date endDate = new Date(beginDate.getTime() + CertConstants.DEFAULT_VALIDITY);
            KeyPair keyPair = KeyUtils.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            CertUtils.writeKey(privateKey, savePath + "/" + "ca_pri.key");
            log.info("privateKey save success, file path :~" + savePath + "/" + "ca_pri.key");
            CertUtils.writeKey(publicKey, savePath + "/" + "ca_pub.key");
            log.info("publicKey save success, file path :~" + savePath + "/" + "ca_pub.key");

            X509Certificate certificate = createRootCertificate(CertConstants.DEFAULT_SIGNATURE_ALGORITHM, issuer,
                    null, beginDate, endDate, publicKey, privateKey);
            CertUtils.writeCrt(certificate, savePath + "/" + "ca.crt");
            log.info("CA certificate save success, file path :~" + savePath + "/" + "ca.crt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * generate CA certificate by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     *
     * @param issuer        issuer
     * @param privateKeyStr string of the privateKey
     * @return string of generated certificate
     */
    public String generateRootCertByDefaultConf(X500NameInfo issuer, String privateKeyStr) {
        return generateRootCertByDefaultConf(issuer, privateKeyStr, null);
    }

    /**
     * generate childCert by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     *
     * @param caStr     string of the CA certificate
     * @param csrStr    string of the certificate request
     * @param priKeyStr string of the parent's privateKey
     * @return string of generated certificate
     */
    public String generateChildCertByDefaultConf(String caStr, String csrStr, String priKeyStr) {
        return generateChildCertByDefaultConf(true, null, caStr, csrStr, priKeyStr);
    }


    /**
     * generate childCert by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     * the generated certificate is saved in a file
     *
     * @param caPath         CA certificate file path
     * @param csrPath        path of certificate request
     * @param keyPath        path of the parent's privateKey
     * @param exportFilePath file path of  generated certificate
     * @return string of generated certificate
     */
    public String generateChildCertByDefaultConf(String caPath, String csrPath, String keyPath, String exportFilePath) {
        return generateChildCertByDefaultConf(true, null, caPath, csrPath, keyPath, exportFilePath);
    }

    /**
     * generate certRequest by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     *
     * @param subject subject of the csr
     * @param priKey  string of the child's privateKey
     * @return string of generated certRequest
     */
    public String generateCertRequestByDefaultConf(X500NameInfo subject, String priKey) {
        return generateCertRequestByDefaultConf(subject, priKey, null);
    }

    /**
     * generate CA certificate by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     * the generated certificate is saved in a file
     *
     * @param issuer        issuer
     * @param privateKeyStr string of the privateKey
     * @param certSavePath  save path of generated certificate
     * @return string of generated certificate
     */
    public String generateRootCertByDefaultConf(X500NameInfo issuer, String privateKeyStr, String certSavePath) {
        try {
            if (privateKeyStr == null) {
                throw new NullPointerException("privateKeyStr is null");
            }
            if (certSavePath != null && !FileOperationUtils.exist(certSavePath)) {
                throw new FileNotFoundException("certSavePath does't exist, path = " + certSavePath);
            }
            Date beginDate = new Date();
            Date endDate = new Date(beginDate.getTime() + CertConstants.DEFAULT_VALIDITY);
            PrivateKey privateKey = null;
            PublicKey publicKey = null;
            try {
                privateKey = KeyUtils.getRSAPrivateKey(privateKeyStr);
                publicKey = KeyUtils.getRSAPublicKey(privateKey);
            } catch (Exception e) {
                log.error("KeyUtils.getRSAPrivateKey failed ", e);
            }
            if (privateKey == null || publicKey == null) {
                return null;
            }
            X509Certificate certificate = createRootCertificate(CertConstants.DEFAULT_SIGNATURE_ALGORITHM, issuer,
                    null, beginDate, endDate, publicKey, privateKey);
            if (certSavePath != null) {
                CertUtils.writeCrt(certificate, certSavePath + "/" + "ca.crt");
                log.info("CA certificate save success, file path :~" + certSavePath + "/" + "ca.crt");
            }
            return CertUtils.readPEMAsString(certificate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * generate certRequest by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     *
     * @param subject        subject of the csr
     * @param priKey         string of the child's privateKey
     * @param exportFilePath save path of generated certRequest
     * @return string of generated certRequest
     */
    public String generateCertRequestByDefaultConf(X500NameInfo subject, String priKey, String exportFilePath) {
        try {
            if (exportFilePath != null && !FileOperationUtils.exist(exportFilePath)) {
                throw new FileNotFoundException("exportFilePath does't exist，path = " + exportFilePath);
            }
            PrivateKey privateKey = null;
            PublicKey publicKey = null;
            privateKey = KeyUtils.getRSAPrivateKey(priKey);
            publicKey = KeyUtils.getRSAPublicKey(privateKey);
            PKCS10CertificationRequest request = createCertRequest(subject, publicKey, privateKey,
                    CertConstants.DEFAULT_SIGNATURE_ALGORITHM);
            if (exportFilePath != null) {
                CertUtils.writeCsr(request, exportFilePath);
                log.info("PKCS10CertificationRequest save success, file path :~" + exportFilePath);
            }
            return CertUtils.readPEMAsString(request);
        } catch (Exception e) {
            log.error("Error generate csr",e);
        }
        return null;
    }


    /**
     * generate childCert by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     *
     * @param isCaCert  certificate mark
     * @param keyUsage  scenarios where the certificate can be used
     * @param caStr     string of the CA certificate
     * @param csrStr    string of the certificate request
     * @param priKeyStr string of the parent's privateKey
     * @return string of the generated certificate
     */
    public String generateChildCertByDefaultConf(boolean isCaCert, KeyUsage keyUsage, String caStr, String csrStr,
                                                 String priKeyStr) {
        if (caStr == null || csrStr == null || priKeyStr == null){
            throw new NullPointerException("param null");
        }
        X509Certificate parentCert = null;
        PKCS10CertificationRequest request = null;
        PrivateKey parentPriKey = null;
        try {
            parentCert = CertUtils.convertStrToCert(caStr);
            request = CertUtils.convertStrToCsr(csrStr);
            parentPriKey = KeyUtils.getRSAPrivateKey(priKeyStr);
        } catch (Exception e) {
            log.error("string convert pemObject failed ", e);
        }
        if (parentCert == null || request == null || parentPriKey == null) {
            return null;
        }
        Date beginDate = new Date();
        Date endDate = new Date(beginDate.getTime() + CertConstants.DEFAULT_VALIDITY);
        X509Certificate childCert = createChildCertificate(isCaCert, CertConstants.DEFAULT_SIGNATURE_ALGORITHM,
                parentCert, request, keyUsage, beginDate, endDate, parentPriKey);
        return CertUtils.readPEMAsString(childCert);
    }

    /**
     * generate childCert by default configuration (signature algorithm is SHA256WITHRSA, valid for 3650 days)
     *
     * @param isCaCert       certificate mark
     * @param keyUsage       scenarios where the certificate can be used
     * @param caPath         path of CA certificate
     * @param csrPth         path of certificate request
     * @param keyPath        path of the parent's privateKey
     * @param exportFilePath save path of generated certificate
     * @return string of the generated certificate
     */
    public String generateChildCertByDefaultConf(boolean isCaCert, KeyUsage keyUsage, String caPath, String csrPth,
                                                 String keyPath, String exportFilePath) {
        try {
            if (!FileOperationUtils.exist(caPath)) {
                throw new FileNotFoundException("caPath does't exist，path = " + caPath);
            }
            if (!FileOperationUtils.exist(csrPth)) {
                throw new FileNotFoundException("csrPth does't exist，path = " + csrPth);
            }
            if (!FileOperationUtils.exist(keyPath)) {
                throw new FileNotFoundException("keyPath does't exist，path = " + csrPth);
            }
            if (exportFilePath != null && !FileOperationUtils.exist(exportFilePath)) {
                throw new FileNotFoundException("exportFilePath does't exist，path = " + csrPth);
            }
            Date beginDate = new Date();
            Date endDate = new Date(beginDate.getTime() + CertConstants.DEFAULT_VALIDITY);
            X509Certificate childCert = null;
            try {
                X509Certificate parentCertificate = CertUtils.readCrt(caPath);
                PKCS10CertificationRequest request = CertUtils.readCsr(csrPth);
                PrivateKey parentPriKey = (PrivateKey) CertUtils.readRSAKey(keyPath);
                if (parentCertificate == null || request == null || parentPriKey == null) {
                    return null;
                }
                childCert = createChildCertificate(isCaCert, CertConstants.DEFAULT_SIGNATURE_ALGORITHM,
                        parentCertificate, request, keyUsage,
                        beginDate, endDate, parentPriKey);
            } catch (Exception e) {
                log.error("X509CertHandler.createChildCert failed ", e);
            }
            if (exportFilePath != null) {
                CertUtils.writeCrt(childCert, exportFilePath);
                log.info("CA certificate save success, file path :~" + exportFilePath);
            }
            return CertUtils.readPEMAsString(childCert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * create RootCertificate
     *
     * @param signAlg    signature algorithm,the type of the corresponding key
     * @param issuer     issuer
     * @param keyUsage   scenarios where the certificate can be used
     * @param beginDate  beginDate of the certificate
     * @param endDate    endDate of the certificate
     * @param publicKey  the public key bound by the certificate，used to decrypt the signature
     * @param privateKey the private key used for encryption to generate the signature
     * @return the generated certificate
     */
    public X509Certificate createRootCertificate(String signAlg, X500NameInfo issuer, KeyUsage keyUsage,
                                                 Date beginDate, Date endDate,
                                                 PublicKey publicKey, PrivateKey privateKey) {
        X509Certificate rootCert = null;
        try {
            rootCert = X509CertHandler.createRootCert(signAlg, new X500Name(issuer.toString()), keyUsage,
                    beginDate, endDate, publicKey, privateKey);
        } catch (Exception e) {
            log.error("X509CertHandler.createRootCert failed ", e);
            e.printStackTrace();
        }
        return rootCert;
    }

    /**
     * create ChildCertificate
     *
     * @param isCaCert          root certificate mark
     * @param signAlg           signature algorithm,the type of the corresponding key
     * @param parentCertificate certificate of the issuer
     * @param request           certification request
     * @param keyUsage          scenarios where the certificate can be used
     * @param beginDate         beginDate of the certificate
     * @param endDate           endDate of the certificate
     * @param privateKey        the private key used for encryption to generate the signature
     * @return the generated certificate
     */
    public X509Certificate createChildCertificate(boolean isCaCert, String signAlg, X509Certificate parentCertificate,
                                                  PKCS10CertificationRequest request, KeyUsage keyUsage,
                                                  Date beginDate, Date endDate, PrivateKey privateKey) {
        X509Certificate childCert = null;
        try {
            childCert = X509CertHandler.createChildCert(isCaCert, signAlg, parentCertificate, request, keyUsage,
                    beginDate, endDate, privateKey);
        } catch (Exception e) {
            log.error("X509CertHandler.createChildCert failed ", e);
            e.printStackTrace();
        }
        return childCert;
    }


    /**
     * create CertificationRequest
     *
     * @param subject subject of the csr
     * @param pubKey  the public key bound by the certificate，used to decrypt the signature
     * @param priKey  the private key used for encryption to generate the signature
     * @param signAlg signature algorithm,the type of the corresponding key
     * @return the certificate request
     */
    public PKCS10CertificationRequest createCertRequest(X500NameInfo subject, PublicKey pubKey, PrivateKey priKey,
                                                        String signAlg) {
        PKCS10CertificationRequest request = null;
        try {
            request = X509CertHandler.createCSR(new X500Name(subject.toString()), pubKey, priKey, signAlg);
        } catch (OperatorCreationException e) {
            log.error("X509CertHandler.createCSR failed ", e);
            e.printStackTrace();
        }
        return request;
    }

}
