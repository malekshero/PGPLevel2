package sample;

import java.io.FileInputStream;
import java.security.*;
import java.util.Base64;

import Client_Server.GenerateKeys;
import DataBaseConnect.DBConnect;
import Encrypte_Decrypte_RSA.RSA;
import Encypte_Decrypt_RC4.SymmetricClient;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import javax.crypto.Cipher;


public class Client {
    public static PublicKey getPublicKey(String publicKeyAsString, GenerateKeys gk) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyAsString);
        PublicKey publicKey = gk.getPublic(publicKeyBytes);

        return publicKey;
    }

    public static String generateRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static String randomKey = "";

    @FXML
    private TextField name;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private TextField value;
    @FXML
    private TextField feedBack;
    @FXML
    private TextArea SystemFeedBack;
    @FXML
    private Button sendButton;

    public void sensdInformationToDB(ActionEvent event) {
        try {


            RSA rsa = new RSA();
            SymmetricClient rc4 = new SymmetricClient();
            GenerateKeys gk = new GenerateKeys(2048);
            String name1 = name.getText();
            String from1 = from.getText();
            String to1 = to.getText();
            String value1 = value.getText();
            String feedback1 = feedBack.getText();
            if (name1.equals("") || from1.equals("") || to1.equals("") || value1.equals("") || feedback1.equals("")) {
                SystemFeedBack.setText("Please Fill all fields ");
            } else {
                SystemFeedBack.setText("");
                Socket socket = new Socket("127.0.0.1", 8888);
                DataInputStream inStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
                outStream.writeUTF(name1);

                // Read public key from server in socket
                String PublicKeyFromServer = inStream.readUTF();

                JSONObject remit = new JSONObject();
                remit.put("name", name1);
                remit.put("remittance_value", value1);
                remit.put("send_id", from1);
                remit.put("reciver_id", to1);
                remit.put("describtion", feedback1);


                if (randomKey == "") randomKey = generateRandomString();

                ///encrypted with RC4 randomkey key
                byte[] encrypted = rc4.encrypt(remit.toString(), randomKey);






/////////////////////
                PrivateKey privateKey = gk.getPrivateKey();
                PublicKey signaturePublicKey = gk.getPublicKey();
                String publicKey1 = Base64.getEncoder().encodeToString(signaturePublicKey.getEncoded());
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] messageHash = md.digest(encrypted);


                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                byte[] digitalSignature = cipher.doFinal(messageHash);

                outStream.writeInt(digitalSignature.length);
                outStream.write(digitalSignature);
                outStream.writeUTF(publicKey1);

///////////////////////
                // Encrypt Client Key By Using Public Key Of Client
                // convert from string to public key
                PublicKey publicKey = getPublicKey(PublicKeyFromServer, gk);
                byte[] encryptRC4KeyUsingRSA = rsa.encrypt(randomKey.getBytes(), publicKey);

                //send encrypted data using random key in rc4
                outStream.writeInt(encrypted.length);
                outStream.write(encrypted);

                ////send encrypted rc4 key using public key of rsa
                outStream.writeInt(encryptRC4KeyUsingRSA.length);
                outStream.write(encryptRC4KeyUsingRSA);

                // send digitalSignature

                // Read a message from the server about the state of transactions
                String feedbacktext = inStream.readUTF();
                SystemFeedBack.setText(feedbacktext);
                outStream.flush();
                outStream.close();
                inStream.close();
                socket.close();
                // init fields
                name.setText("");
                from.setText("");
                to.setText("");
                value.setText("");
                feedBack.setText("");

            }


        } catch (Exception e) {
            System.out.println(e);
        }


    }


}
