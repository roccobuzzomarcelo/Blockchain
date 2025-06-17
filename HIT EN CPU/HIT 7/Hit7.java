import java.security.MessageDigest;

public class Hit7 {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Uso: java Hit5 <prefijo_hash> <cadena> <min_num> <max_num>");
            return;
        }

        String prefijo = args[0];
        String cadena = args[1];
        int minNum = 0;
        int maxNum = 0;

        try {
            minNum = Integer.parseInt(args[2]);
            maxNum = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Los parametros min_num y max_num deben ser numeros enteros.");
            return;
        }

        if (minNum > maxNum) {
            System.out.println("El numero minimo no puede ser mayor que el maximo.");
            return;
        }

        long inicio = System.currentTimeMillis();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            boolean encontrado = false;

            for (int numero = minNum; numero <= maxNum; numero++) {
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
                    encontrado = true;
                    break;
                }

                md.reset();
            }

            if (!encontrado) {
                System.out.println("No se encontro ningun hash que empiece con el prefijo dado en el rango especificado.");
            }
        } catch (Exception e) {
            System.err.println("Error calculando MD5: " + e.getMessage());
        }
    }
}

