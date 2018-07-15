import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Main {
    private static final String username = "HansBug";

    private static final Object lockObject = new Object();

    public static void main(String[] args) throws InterruptedException {
        CoapClient clientObs = new CoapClient(String.format("coap://127.0.0.1:5683/chat/obs?user=%s", username));

        Executors.newSingleThreadExecutor().execute(() -> {
            clientObs.observe(new CoapHandler() {
                @Override
                public void onLoad(CoapResponse coapResponse) {
                    System.out.println("f?");
                    System.out.println(Utils.prettyPrint(coapResponse));
                    synchronized (lockObject) {
                        lockObject.notifyAll();
                    }

                    if (coapResponse.isSuccess()) {
                        System.out.println(coapResponse.getResponseText());
                    }
                }

                @Override
                public void onError() {

                }
            }, MediaTypeRegistry.TEXT_PLAIN);
        });
        synchronized (lockObject) {
            lockObject.wait();
        }
        System.out.println("Connection complete!");

        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("^\\s*(?<target>[a-z0-9A-Z_]+)\\s*:\\s*(?<message>[\\s\\S]*?)\\s*$");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("quit".equals(line)) {
                clientObs.shutdown();
                System.exit(0);
            }
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String target = matcher.group("target");
                String message = matcher.group("message");
                CoapClient sendClient = new CoapClient(String.format("coap://127.0.0.1:5683/chat/send?user=%s", target));
                sendClient.post(new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse coapResponse) {
                        if (coapResponse.isSuccess()) {
                            System.out.println(String.format("Message \"%s\" send success!", message));
                            System.out.println(coapResponse.getResponseText());
                        } else {
                            System.err.println(String.format("Message \"%s\" send failed!", message));
                        }
                    }

                    @Override
                    public void onError() {
                        System.err.println(String.format("Error occurred when sending message \"%s\"!", message));
                    }
                }, message, MediaTypeRegistry.TEXT_PLAIN);
                sendClient.shutdown();
            } else {
                System.err.println(String.format("Invalid line : [%s]", line));
            }
        }

        scanner.close();
    }
}
