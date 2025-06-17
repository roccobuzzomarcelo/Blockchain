import java.security.MessageDigest;

public class Hit4 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java MD5CPU <string_a_hashear>");
            return;
        }
        
        String input = args[0];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());

            // Convertir bytes a hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            System.out.println("MD5 hash: " + sb.toString());
        } catch (Exception e) {
            System.err.println("Error calculando MD5: " + e.getMessage());
        }
    }
}
