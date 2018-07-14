package name.zhangmin.rabbitmq;

import java.io.*;
import java.security.*;
import javax.net.ssl.*;

import com.rabbitmq.client.*;


public class RabbitMQClientDomain
{
    public static void main(String[] args) throws Exception
    {

      char[] keyPassphrase = "cn-east-3".toCharArray();
      KeyStore ks = KeyStore.getInstance("PKCS12");
      ks.load(new FileInputStream("/Users/zhangmin/GitRepo/code.zhangmin.name/nodejs/security/client/keycert-client-cn-east-3.p12"), keyPassphrase);

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, "cn-east-3".toCharArray());

      char[] trustPassphrase = "cn-east-3".toCharArray();
      KeyStore tks = KeyStore.getInstance("JKS");
      tks.load(new FileInputStream("/Users/zhangmin/GitRepo/code.zhangmin.name/nodejs/security/client/rabbitmqserverstore-cn-east-3"), trustPassphrase);

      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(tks);

      SSLContext c = SSLContext.getInstance("TLSv1.1");
      c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("mq-cn-east-3.netease.com");
      factory.setPort(5672);
      factory.setUsername("ddosmqadmin");
      factory.setPassword("FV4fnzQHJ");
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