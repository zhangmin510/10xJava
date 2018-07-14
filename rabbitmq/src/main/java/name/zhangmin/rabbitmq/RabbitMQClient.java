package name.zhangmin.rabbitmq;

import java.io.*;
import java.security.*;
import javax.net.ssl.*;

import com.rabbitmq.client.*;


public class RabbitMQClient
{
    public static void main(String[] args) throws Exception
    {

      char[] keyPassphrase = "hello".toCharArray();
      KeyStore ks = KeyStore.getInstance("PKCS12");
      ks.load(new FileInputStream("/Users/zhangmin/GitRepo/code.zhangmin.name/nodejs/security/client/keycert.p12"), keyPassphrase);

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, "hello".toCharArray());

      char[] trustPassphrase = "igonnawin".toCharArray();
      KeyStore tks = KeyStore.getInstance("JKS");
      tks.load(new FileInputStream("/Users/zhangmin/GitRepo/code.zhangmin.name/nodejs/security/client/rabbitstore"), trustPassphrase);

      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(tks);

      SSLContext c = SSLContext.getInstance("TLSv1.1");
      c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("59.111.61.31");
      factory.setPort(5672);
      factory.setUsername("kU55ZHXq");
      factory.setPassword("xD8dc7kU3twYjQ7qKHzmmrV8JIqhHUWt");
      factory.useSslProtocol(c);

      Connection conn = factory.newConnection();
      Channel channel = conn.createChannel();

      channel.queueDeclare("rabbitmq-java-test", false, true, true, null);
      channel.basicPublish("", "rabbitmq-java-test", null, "Hello, World".getBytes());


      GetResponse chResponse = channel.basicGet("rabbitmq-java-test", false);
      if(chResponse == null) {
          System.out.println("No message retrieved");
      } else {
          byte[] body = chResponse.getBody();
          System.out.println("Recieved: " + new String(body));
      }


      channel.close();
      conn.close();
  }
}