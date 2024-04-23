import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Receiver {

    // Decrypt ciphertext using DES
    public static byte[] decrypt(byte[] cipherText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    // Receive encrypted message from the sender
    public static void receiveFromSender(int port) {
        try {

            // Starting by using server socket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Waiting for sender to connect...");
            Socket socket = serverSocket.accept();
            System.out.println("Connected!");

            // Finding key
            ObjectInputStream keyStream = new ObjectInputStream(socket.getInputStream());
            byte[] receivedKeyBytes = (byte[]) keyStream.readObject();
            SecretKey secretKey = new SecretKeySpec(receivedKeyBytes, 0, receivedKeyBytes.length, "DES");

            // Finding the encrypted message
            ObjectInputStream messageStream = new ObjectInputStream(socket.getInputStream());
            byte[] receivedEncryptedBytes = (byte[]) messageStream.readObject();

            // Decrypt the message using DES
            byte[] decryptedBytes = decrypt(receivedEncryptedBytes, secretKey);

            // Decrypted message
            System.out.println("Your message is: " + new String(decryptedBytes, StandardCharsets.UTF_8));

            // Close the socket and serverSocket
            socket.close();
            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Encrypted message from the sender (port)
        receiveFromSender(12345);
    }
}
