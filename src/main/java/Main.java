import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.time.Clock;
import java.util.concurrent.CountDownLatch;

public abstract class Main {
    public static void main(String[] args) throws InterruptedException {
        int count = 1;
        CountDownLatch countDown = new CountDownLatch(count);
        long start = Clock.systemUTC().millis();
        CoapClient client = new CoapClient("coap://127.0.0.1:5683/chat/receive?target_user=hdl&message=My+name+is+van!");
        for (int i = 0; i < count; i++) {
            client.post(new CoapHandler() {

                @Override
                public void onLoad(CoapResponse response) {
                    System.out.println(Utils.prettyPrint(response));
                    System.out.println(response.isSuccess());
                    countDown.countDown();
                }

                @Override
                public void onError() {

                }
            }, "message", MediaTypeRegistry.TEXT_PLAIN);
        }
        countDown.await();
        long end = Clock.systemUTC().millis();
        System.out.println(end - start);

    }
}
