import java.security.MessageDigest;

public class Hit5 {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java Hit5 <prefijo_hash> <cadena>");
            return;
        }

        String prefijo = args[0];
        String cadena = args[1];

        long inicio = System.currentTimeMillis();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            int numero = 0;
            while (true) {
                String intento = cadena + numero;
                byte[] hashBytes = md.digest(intento.getBytes());

                
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }

                String hash = sb.toString();
                if (hash.startsWith(prefijo)) {
                    long fin = System.currentTimeMillis();
                    System.out.println("Hash encontrado: " + hash);
                    System.out.println("Numero encontrado: " + numero);
                    System.out.println("Tiempo total: " + (fin - inicio) + " ms");
                    break;
                }

                numero++;
                md.reset(); 
            }
        } catch (Exception e) {
            System.err.println("Error calculando MD5: " + e.getMessage());
        }
    }
}
