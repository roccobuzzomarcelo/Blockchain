package com.nodo_coordinador_tareas.Nodo_Coordinador.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String calcularHashMD5(String base, long nonce) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String intento = base + nonce;
            byte[] hashBytes = md.digest(intento.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 no soportado", e);
        }
    }

    public static boolean validarHash(String base, long nonce, String prefijoEsperado, String hashEsperado) {
        String hashCalculado = calcularHashMD5(base, nonce);
        return hashCalculado.equals(hashEsperado) && hashCalculado.startsWith(prefijoEsperado);
    }
}

