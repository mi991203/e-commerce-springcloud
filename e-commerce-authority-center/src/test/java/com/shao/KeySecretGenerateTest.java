package com.shao;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author : SH35856
 * @Description: 生成RSA256公钥和私钥
 * @date: 2022/8/29 9:39
 */
@Slf4j
public class KeySecretGenerateTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        // 生成公钥和私钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取公钥和私钥对象
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCDs8+wP5+9hCqX+6qha5AOueHkfWP8FWXTVdDasc3doND3DxVWemVxQl7Va8MN1/EAAJmIECCdERELQ1lvNmAXBremA3cFpM2DJRRHs72jtoqXTjWYZz4Be3Y16CddR8noOxu4U5t2+p1IwzBCaoiWGDINN2MIzXwvYhX0g+RgvyKCVPgs4NKlqt1hIeV8xN7TZjDIgYw9y5KAcSBvw/QjnkMdmZK0irA7m7HvgqjJBxA5YRjDFcs/SiPr+aomOVoLhONOnq+zka+jlJWSWVEiiI+4UcDt60x8ejAvMQ1UnfSYRCA5EAWJGGZ1aLGsEz6d+/ljJPoaLjb3CoXXxj4HAgMBAAECggEAdEjqYvr6RvIQTZs24EnBq07ypbUU9/nIq12FB+OgGkSo3MsjkvSE5wisBRKd6XY140OkS9+5fBUakHBJ4gkHtK0Ir/s1NvV5Q4cUre6Eza0aF1hFuCfINrTU/enZ/+Gn43V1Fz4y8U18XEeDL9Eyghwg7UxvE9GjCY84RWnC1qwIQrDamHdcPsLfaJssYFy3sgTzapfQKqUUBd85KkI3hRI9EiRF2a18PNAp5alXxYvBzg0lFWAI1sPo4FAD6JF8rOLoPEJGRyoYqQf8vWPkg9KSNr+hD4kkrnLM9nbhCJGOd9CQx7ZdmwO2gRImkiMycLhufqRBKb3d64hGsRnceQKBgQC3rq8iITAUf/HBqaaSpkznLZMA5TmbbvaFGqNzhmV30xu1ghwtgPDQVsKC+hjiQrOkhXfNABWywGCYUvUDB9ChYmlpSC75cviMfHvEAB8I/O2SAOuzAhMRlI2nwZHjHxkPIAklQ8Atos8wSR0vD7GocCNxcz+UdSLFOk5VdBhJUwKBgQC3jhL0fae5K3KESEweXZ2F8gOG9LhCsiHP0IZQj52pn+/OMWhPZTvEBVZNa1YU+kQCku8ydCCN5hMIFhMNX+fuOKzjGvvossO9+SQcJFpCcy51iXxzMUDYeeYsdqXH9zmup011BIaTqSOj0zepMAppa8HSOeGaH4+X5vCoYd89/QKBgQCz8Yqaqd6bRphFO/j2U7qlAy2vQ+DCl9sjnGwiAbinwYW0tEj8fwPrZgNaIr578hJhC2vIxdt76SD4ONTUajSF1Y4k2NVEBNDfeBgbdG7y9C2cyhAdptudbmWzwVs1IEMBYUX0XsIHMyc8S3QupXZK58ihHOFdg79TbgJNjtU4BwKBgQCBmcXWbNY8zul3bxiXpB+x0HQfGLD1HpLzCVeckBqRBsUqAAaXcHHncKFD3pKUEzRr4NUU+Zt2tUkgv31KRzoaIFgprgbJgVblzoBpgl1cZ7kY8i81okQX1NHo+QkKjItkvunYwo5GufjZFixJwArJ7nSWe5nH2ZrodP3c1eXq/QKBgEGW2/UtAEOjeaAM/CEr0DHfzJJGOmLwYsw2gfFrD9guKt4RgdR4k6aXqO3YmlTHa5jAnxAB/cmttajxoSCVLJJB2IXyc9vB8elGQu1AUpBWjn8OK+gMHk2+enpj6L2t3qkZRQ7Q1Tn+iGKFPwRfZWAli7qfkcys0uJGEB7nR+3G
        log.info("private key: [{}]", Base64.encode(privateKey.getEncoded()));

        // MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg7PPsD+fvYQql/uqoWuQDrnh5H1j/BVl01XQ2rHN3aDQ9w8VVnplcUJe1WvDDdfxAACZiBAgnRERC0NZbzZgFwa3pgN3BaTNgyUUR7O9o7aKl041mGc+AXt2NegnXUfJ6DsbuFObdvqdSMMwQmqIlhgyDTdjCM18L2IV9IPkYL8iglT4LODSpardYSHlfMTe02YwyIGMPcuSgHEgb8P0I55DHZmStIqwO5ux74KoyQcQOWEYwxXLP0oj6/mqJjlaC4TjTp6vs5Gvo5SVkllRIoiPuFHA7etMfHowLzENVJ30mEQgORAFiRhmdWixrBM+nfv5YyT6Gi429wqF18Y+BwIDAQAB
        log.info("public key: [{}]", Base64.encode(publicKey.getEncoded()));
    }
}
