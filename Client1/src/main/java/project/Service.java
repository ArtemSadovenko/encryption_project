package project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@org.springframework.stereotype.Service
public class Service {
    private final List<String> history = new ArrayList<>();
    @Autowired
    private WebClient webClient;


    public String send(String message, String host){

        webClient.post()
                .uri(host)
                .body(BodyInserters.fromValue(encrypt(message)))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return encrypt(message);
    }

    public List<String> getAll(){
        return history;
    }

    public String get(String message){
        history.add(decrypt(message));
        return decrypt(message);
    }

    public String openFile(String filePath) {
        StringBuilder key = new StringBuilder("");


        try {
            FileReader fileReader = new FileReader(new File(filePath));

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                key.append(line);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            System.err.println("An error occurred while reading from the file: " + e.getMessage());
        }

        return key.toString();
    }

    public String encrypt(String message) {
        String key = openFile("key.txt");
//        String key = "1E3Sa0G8sVX5imjdMo4wur/FQ";
        String randomKey = randomiseKey(key);
        String encryptMessage = (randomKey + replaceChars(key, randomKey, message));
        encryptMessage = charSum(key, encryptMessage);
        return encryptMessage;

    }

    public String randomiseKey(String key) {
        String randomKey = "";
        Random rand = new Random();
        String tmp;
        int index;
        while (true) {
            if (key.length() == 0) {
                break;
            }
            index = rand.nextInt(key.length());
            char ch = key.toCharArray()[index];
            tmp = "";
            tmp += ch;
            if (!randomKey.contains(tmp)) {
                randomKey += tmp;
                key = new StringBuilder(key).deleteCharAt(index).toString();
            }
        }
        return randomKey;
    }

    public String replaceChars(String key, String randomKey, String message) {
        StringBuilder messedMessage = new StringBuilder();
        StringBuilder gapMsg = new StringBuilder(message);
        int i = 0;
        int position;
        int prev_position;
        int modifier = key.length() - (message.length() % key.length());
        for (int k = 0; k < modifier; k++) {
            gapMsg.append(' ');
        }
        char[] msg = gapMsg.toString().toCharArray();

        while (true) {
            if (i == msg.length) {
                break;
            }
            char tmp;
            for (char ch : randomKey.toCharArray()) {
            position = key.indexOf(ch) + i;
            messedMessage.append(msg[position]);
            }
            i += key.length();
        }

        return messedMessage.toString();
    }

    public String charSum(String key,  String message){
        char[] keyChar = key.toCharArray();
        char[] msgChar = message.toCharArray();
        char resChar;
        int index;
        int counter =  0;
        StringBuilder resultMsg = new StringBuilder("");

        for (char ch: msgChar){
            index = counter % key.length();
            resChar = (char) ((int)ch + (int)(keyChar[index]));
            resultMsg.append(resChar);
            counter++;
        }
        return resultMsg.toString();
    }

    public String charMinus(String key, String message) {
        char[] keyChar = key.toCharArray();
        char[] msgChar = message.toCharArray();
        char resChar;
        int index;
        int counter = 0;
        StringBuilder resultMsg = new StringBuilder("");

        for (char ch : msgChar) {
            index = counter % key.length();
            resChar = (char) ((int)ch - (int) (keyChar[index]));
            resultMsg.append(resChar);
            counter++;
        }
        return resultMsg.toString();
    }

    public String replaceCharsBack(String randomKey, String key, String message) {
        StringBuilder encodedMessage = new StringBuilder();
        int i = 0;
        int aim_position;
        int position;
        char tmp;
        char[] msg = message.toCharArray();

        while (true) {
            if (i == msg.length) {
                break;
            }
            for (char ch : key.toCharArray()) {
                position = randomKey.indexOf(ch) + i;
                encodedMessage.append(msg[position]);
            }
            i += randomKey.length();
        }

        return encodedMessage.toString();
    }

    public String decrypt(String message) {
        String decMsg = "";
        String key = openFile("key.txt");
//        String key = "1E3Sa0G8sVX5imjdMo4wur/FQ";
        String randomKey;
        String encMsg;

        decMsg = charMinus(key, message);

        encMsg = decMsg.substring(key.length(), decMsg.length());
        randomKey = decMsg.substring(0, key.length());

        decMsg = replaceCharsBack(randomKey, key, encMsg);

        System.out.println("");
        return decMsg;
    }
}
