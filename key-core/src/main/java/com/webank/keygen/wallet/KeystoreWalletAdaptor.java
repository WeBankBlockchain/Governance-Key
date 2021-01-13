package com.webank.keygen.wallet;

import com.webank.keygen.utils.KeyPresenter;

import com.webank.keygen.utils.KeyUtils;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.crypto.keypair.SM2KeyPair;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

public class KeystoreWalletAdaptor {
    private static final int N_STANDARD = 1 << 18;
    private static final int P_STANDARD = 1;

    public static WalletFile create(String password, CryptoKeyPair cryptoKeyPair, int n, int p)
            throws CipherException {
        ECKeyPair ecKeyPair = new ECKeyPair(
                KeyPresenter.asBigInteger(KeyPresenter.asBytes(cryptoKeyPair.getHexPrivateKey())),
                KeyPresenter.asBigInteger(KeyPresenter.asBytes(cryptoKeyPair.getHexPublicKey().substring(2)))
        );
        WalletFile walletFile = Wallet.create(password, ecKeyPair, n, p);
        walletFile.setAddress(cryptoKeyPair.getAddress());//Instead of hardcoded secp256k1 address
        return walletFile;
    }

    public static WalletFile createStandard(String password, CryptoKeyPair cryptoKeyPair)
            throws CipherException {
        return create(password, cryptoKeyPair, N_STANDARD, P_STANDARD);
    }

    public static CryptoKeyPair decrypt(String password, WalletFile walletFile)
            throws CipherException{
        ECKeyPair ecKeyPair = Wallet.decrypt(password, walletFile);
        CryptoKeyPair ecdsaKeyPair = new ECDSAKeyPair().createKeyPair(ecKeyPair.getPrivateKey());
        CryptoKeyPair sm2KeyPair = new SM2KeyPair().createKeyPair(ecKeyPair.getPrivateKey());
        String addrECDSA = ecdsaKeyPair.getAddress();
        String addrSM2 = sm2KeyPair.getAddress();
        if(KeyUtils.isAddressEquals(addrECDSA, walletFile.getAddress())){
            return ecdsaKeyPair;
        }
        if(KeyUtils.isAddressEquals(addrSM2, walletFile.getAddress())){
            return sm2KeyPair;
        }
        throw new IllegalArgumentException("wallet file does not have a correct address");
    }
}
