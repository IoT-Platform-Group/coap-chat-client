import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.time.Clock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public abstract class Main {
    public static void main(String[] args) {

        CoapClient client = new CoapClient("coap://127.0.0.1:5683/chat/receive?target_user=hdl&message=My+name+is+van!");
        CoapResponse postResponse = client.post("message", MediaTypeRegistry.TEXT_PLAIN);

        System.out.println(Utils.prettyPrint(postResponse));
        System.out.println(postResponse.isSuccess());

        CoapClient obsClient = new CoapClient("coap://127.0.0.1:5683/obs");
        Executors.newSingleThreadExecutor().execute(() ->
                obsClient.observe(new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse coapResponse) {
                        System.out.println(Utils.prettyPrint(coapResponse));
                    }

                    @Override
                    public void onError() {
                        System.out.println("Error");
                    }
                })
        );
    }
}
