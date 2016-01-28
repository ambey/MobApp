package com.extenprise.mapp.medico.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by ambey on 1/10/15.
 */
public abstract class EncryptUtil {
    public static String encrypt(String str) {
        return new String(Hex.encodeHex(DigestUtils.sha256(str)));
    }

    public static void main(String[] args) {
        System.out.println("original passwd = " + args[0] + ", encrypted = "
                + encrypt(args[0]));
        System.out.println("original passwd = " + args[0] + ", encrypted = "
                + encrypt(args[0]));
        System.out.println("original passwd = " + args[0] + ", encrypted = "
                + encrypt(args[0]));
        System.out.println("original passwd = " + args[0] + ", encrypted = "
                + encrypt(args[0]));
    }

}
