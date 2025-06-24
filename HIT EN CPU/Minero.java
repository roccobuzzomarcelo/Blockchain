import java.security.MessageDigest;

public class Minero {

    public static class ResultadoMinado {
        private final String hash;
        private final long nonce;

        public ResultadoMinado(String hash, long nonce) {
            this.hash = hash;
            this.nonce = nonce;
        }

        public String getHash() {
            return hash;
        }

        public long getNonce() {
            return nonce;
        }
    }

    /**
     * Busca un nonce en el rango [minNonce, maxNonce] que genere un hash MD5 con el prefijo dado
     *
     * @param prefijo  Prefijo que debe tener el hash (ej: "0000")
     * @param cadena   Cadena base a la que se le concatena el nonce
     * @param minNonce Rango mínimo de nonce
     * @param maxNonce Rango máximo de nonce
     * @return ResultadoMinado con hash y nonce si se encontró, o null si no
     */
    public ResultadoMinado minar(String prefijo, String cadena, long minNonce, long maxNonce) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            for (long nonce = minNonce; nonce <= maxNonce; nonce++) {
                String intento = cadena + nonce;
                byte[] hashBytes = md.digest(intento.getBytes());

                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }
                String hash = sb.toString();

                if (hash.startsWith(prefijo)) {
                    return new ResultadoMinado(hash, nonce);
                }

                md.reset();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calculando MD5: " + e.getMessage(), e);
        }
        return null;
    }
}

