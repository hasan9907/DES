import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender {

    // Round key generation
    public static SecretKey generateRKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        return keyGen.generateKey();
    }

    // Encrypt plaintext using DES
    public static byte[] encrypt(SecretKey key, byte[] plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plainText);
    }

    // Send encrypted message to the receiver.java by using Streams
    public static void sendToReceiver(String ipAddress, SecretKey secretKey, byte[] encrypted, int port)
            throws Exception {

        // Connect to the socket using IP address and port (receiver's)
        Socket socket = new Socket(ipAddress, port);

        // Send the key to receiver
        ObjectOutputStream keyStream = new ObjectOutputStream(socket.getOutputStream());
        keyStream.writeObject(secretKey.getEncoded());

        // Send the encrypted message to receiver
        ObjectOutputStream messageStream = new ObjectOutputStream(socket.getOutputStream());
        messageStream.writeObject(encrypted);

        socket.close();
    }

    public static void main(String[] args) throws Exception {

        // Text message
        System.out.print("Enter your message: ");
        Scanner scan = new Scanner(System.in);
        String plaintext = scan.nextLine();

        // Plaintext to make it a multiple of 8 bytes
        int paddingLength = 8 - (plaintext.length() % 8);
        for (int i = 0; i < paddingLength; i++) {
            plaintext += " ";
        }

        // Ip address of the receiver
        String receiverIPAddress = "192.168.254.115"; // Receiver's Ip adress here
        // Generates a secret key
        SecretKey secretKey = generateRKey();

        // Encrypt plaintext using DES
        byte[] encryptedBytes = encrypt(secretKey, plaintext.getBytes("UTF-8"));
        // Send encrypted message to the receiver (ip adress, secret key, message, port)
        sendToReceiver(receiverIPAddress, secretKey, encryptedBytes, 12345);

        scan.close();
    }
}