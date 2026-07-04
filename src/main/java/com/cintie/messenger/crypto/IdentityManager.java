package com.cintie.messenger.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

// Manages long term identity client
public class IdentityManager {

    // Algorithm name
    private static final String ALGORITHM = "Ed25519";

    // Key file names
    private static final String PRIVATE_KEY_FILE = "identity.private";
    private static final String PUBLIC_KEY_FILE = "identity.public";
    private static final String PEER_ID_FILE = "identity.peerid";

    // Keys
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String peerIdHex;
    private String peerIdBase64;

    // Directory
    private final Path storageDirectory;

    // Constructor with app path
    public IdentityManager(){
        String userHome = System.getProperty("user.home");
        this.storageDirectory = Paths.get(userHome, ".cintie_messenger", "identity");
        createStorageDirectory();
    }

    // Create storage directory
    private void createStorageDirectory(){
        try {
            // Create directory if not exist
            if(!Files.exists(storageDirectory)){
                Files.createDirectories(storageDirectory);
            }
        } catch (IOException e){
            throw new RuntimeException("Failed to create storage directory: " + storageDirectory.toString());
        }
    }

    // Initialization of user
    public void initialize(){

        Path privateKeyPath = storageDirectory.resolve(PRIVATE_KEY_FILE);
        Path publicKeyPath = storageDirectory.resolve(PUBLIC_KEY_FILE);
        Path peerIdPath = storageDirectory.resolve(PEER_ID_FILE);

        if(Files.exists(privateKeyPath) && Files.exists(publicKeyPath)){
            loadIdentity(privateKeyPath, publicKeyPath, peerIdPath);
        }
        else{
            generateAndSaveIdentity(privateKeyPath, publicKeyPath, peerIdPath);
        }
    }

    // Generate new key pair Ed25519
    private void generateAndSaveIdentity(Path privateKeyPath, Path publicKeyPath, Path peerIdPath){

        // Generation of key pair
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("No such algorithm: " + ALGORITHM);
        }

        // PeerId as SHA-256 from public key
        byte[] rawPublicKey = getRawBytesFromPublicKey(this.publicKey);
        byte[] peerIdBytes = computeSha256(rawPublicKey);

        // Convertation of peer id
        this.peerIdHex = bytesToHex(peerIdBytes);
        this.peerIdBase64 = Base64.getEncoder().encodeToString(peerIdBytes);

        // Prepare keys to save
        String privateKeyBase64 = Base64.getEncoder().encodeToString(this.privateKey.getEncoded());
        String publicKeyBase64 = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());

        // Save atomically
        saveFileAtomically(privateKeyPath, privateKeyBase64);
        saveFileAtomically(publicKeyPath, publicKeyBase64);
        saveFileAtomically(peerIdPath, peerIdBase64);
    }

    // Load saved keys
    private void loadIdentity(Path privateKeyPath, Path publicKeyPath, Path peerIdPath){
        // Load Base64 from files
        String privateKeyBase64;
        String publicKeyBase64;
        String peerIdBase64;
        try {
            privateKeyBase64 = Files.readString(privateKeyPath).trim();
            publicKeyBase64 = Files.readString(publicKeyPath).trim();
            peerIdBase64 = Files.readString(peerIdPath).trim();


        } catch (IOException e){
            throw new RuntimeException("Error reading files. " + e.getMessage());
        }

        // Decode to bytes
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        byte[] peerIdBytesRead = Base64.getDecoder().decode(peerIdBase64);

        // Restore crypto objects
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("No such algorithm: " + ALGORITHM);
        } catch (InvalidKeySpecException e){
            throw  new RuntimeException("Invalid key spec " + e.getMessage());
        }

        // Recount peer id
        byte[] rawPublicKey = getRawBytesFromPublicKey(this.publicKey);
        byte[] peerIdBytes = computeSha256(rawPublicKey);

        if(Arrays.equals(peerIdBytesRead, peerIdBytes)) {
            this.peerIdHex = bytesToHex(peerIdBytes);
            this.peerIdBase64 = Base64.getEncoder().encodeToString(peerIdBytes);
        }
        else{
            this.peerIdHex = bytesToHex(peerIdBytes);
            this.peerIdBase64 = Base64.getEncoder().encodeToString(peerIdBytes);
            saveFileAtomically(peerIdPath, this.peerIdBase64);

            throw new RuntimeException("Error: peer id not equals saved. Updated");
        }
    }

    // Get raw bytes from public key
    private byte[] getRawBytesFromPublicKey(PublicKey publicKey){
        byte[] encoded = publicKey.getEncoded();
        return encoded;
    }

    // Get SHA-256 hash
    private byte[] computeSha256(byte[] data){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Convert bytes to hex
    private String bytesToHex(byte[] bytes){
        StringBuilder hexString = new StringBuilder();
        for(byte b: bytes){
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Atomically saving file
    private void saveFileAtomically(Path path, String content){
        try {
            Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");
            Files.writeString(tempPath, content);
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            throw new RuntimeException("Failed to save file: " + path, e);
        }
    }

}
