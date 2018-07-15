import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Main {
    private static final String username = "HansBug";

    public static void main(String[] args) throws InterruptedException {
        CoapClient clientObs = new CoapClient(String.format("coap://127.0.0.1:5683/test/obs?username=%s", username));
        Thread thread = new Thread() {
            @Override
            public void run() {
                clientObs.observe(new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse coapResponse) {
                        System.out.println(coapResponse);
                    }

                    @Override
                    public void onError() {

                    }
                }, MediaTypeRegistry.TEXT_PLAIN);
            }
        };
        thread.start();
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("^\\s*(?<target>[a-z0-9A-Z_]+)\\s*:\\s*(?<message>[\\s\\S]*?)\\s*$");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String target = matcher.group("target");
                String message = matcher.group("message");

            } else {
                System.err.println(String.format("Invalid line : [%s]", line));
            }
        }

        scanner.close();
    }
}
