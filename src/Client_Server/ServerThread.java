package Client_Server;

import DataBaseConnect.DBConnect;
import Encrypte_Decrypte_RSA.RSA;
import Encypte_Decrypt_RC4.SymmetricClient;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;
import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;


class ServerThread extends Thread {
    /// get make String privateKey who taked from database as a Real private key
    public static PrivateKey getPrivateKey(String privateKeyAsString, GenerateKeys gk) throws Exception
    {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyAsString);
        PrivateKey privateKey = gk.getPrivate(privateKeyBytes);

        return  privateKey;
    }
    public static PublicKey getPublicKey(String publicKeyAsString, GenerateKeys gk) throws Exception
    {
        byte[] publicKeyBytes =Base64.getDecoder().decode(publicKeyAsString);
        PublicKey publicKey = gk.getPublic(publicKeyBytes);

        return  publicKey;
    }

    public static boolean isPureAscii(String v) {
        return Charset.forName("US-ASCII").newEncoder().canEncode(v);

    }


    private Socket socket;
    private int clientNo;
    DBConnect dbc = new DBConnect();
    RSA rsa = new RSA();
    SymmetricClient rc4 = new SymmetricClient();

    ServerThread(Socket socket, int counter) throws IOException {
        this.socket = socket;
        this.clientNo = counter;
    }

    public void run() {
        try {
            ///////// Get Data From Client
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
            String name = inStream.readUTF();
            DBConnect dbc = new DBConnect();

            GenerateKeys gk = new GenerateKeys(2048);

            PublicKey pubkey = gk.getPublicKey();
            PrivateKey prikey = gk.getPrivateKey();
            String publicKey1=Base64.getEncoder().encodeToString(pubkey.getEncoded());
            String privateKey1=Base64.getEncoder().encodeToString(prikey.getEncoded());

            outStream.writeUTF(publicKey1);
            dbc.insertClientKeysToDB(publicKey1,privateKey1,name);
            // read length of incoming data that encrypted by RC4

            int lengthofRC4 = inStream.readInt();

            // read encrypted data by RC4
            byte[] Transaction = new byte[lengthofRC4];
            inStream.readFully(Transaction, 0, Transaction.length);


            // read length of incoming encrypted (Rc4 Key) Using RSA algorithm
            int lengthofRSA = inStream.readInt();
            // read encrypted key by RSA
            byte[] encryptedRc4KeyUsinRSAprivateKey = new byte[lengthofRSA];
           // System.out.println("we recived length of rsa and its :" + lengthofRSA);
            inStream.readFully(encryptedRc4KeyUsinRSAprivateKey, 0, encryptedRc4KeyUsinRSAprivateKey.length); // read byte array from JSON
            ///

            // decrypte RC4 key using RSA privateKey
            byte[] deycrptedKey = rsa.decrypt(encryptedRc4KeyUsinRSAprivateKey, prikey);

            // dycrpte message Transaction using RC4 key

            String clientMessage = rc4.decrypt(Transaction, deycrptedKey);

            // to makeSure That the user is the Account owner
            boolean checkIfAscci = isPureAscii(clientMessage);
            if (checkIfAscci) {

                //  get data from decrypted JSON Object
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(clientMessage);
                String reciver_id = (String) json.get("reciver_id");
                String sender_id = (String) json.get("send_id");
                String remittance_id = (String) json.get("remittance_value");
                String sender_name = (String) json.get("name");
                String describtion = (String) json.get("describtion");
                String getNameByUserId = dbc.getNamebySenderID(sender_name, sender_id);

                if (getNameByUserId.equals("Done")) {
                    //send data to db
                   String valueTest = dbc.Transfer_money(sender_id, reciver_id, remittance_id);
                   if(valueTest.equals("Done")) {
                       // return server answer to the client
                       outStream.writeUTF(getNameByUserId);
                       dbc.insertTransactionToDB(sender_id, reciver_id, remittance_id, describtion);
                   }
                   else
                   {
                       outStream.writeUTF(valueTest);
                   }
                } else {
                    outStream.writeUTF(getNameByUserId);
                }
            } else outStream.writeUTF("Error ID");

            outStream.flush();
            inStream.close();
            outStream.close();
            socket.close();
            if(prikey == null || pubkey == null){
                System.out.println();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            System.out.println("Client -" + clientNo + " exit!! ");
        }
    }
}