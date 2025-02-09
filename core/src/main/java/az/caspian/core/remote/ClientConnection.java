package az.caspian.core.remote;

import az.caspian.core.messaging.Client;
import az.caspian.core.messaging.JoinToProjectMessage;
import az.caspian.core.messaging.ShareTaskMessage;
import az.caspian.core.task.Task;
import az.caspian.core.utils.Asserts;
import az.caspian.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientConnection {
  private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

  private ClientConnection() {
  }

  public static void joinToProject(Client client) {
    Asserts.required(client, "client cannot be null!");

    String serverIpAddress = Session.getServerIpAddress();
    if (StringUtils.isNullOrEmpty(serverIpAddress)) {
      throw new IllegalArgumentException("Server IP address must not be null!");
    }

    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(serverIpAddress, 9090));
      LOG.debug("{} made connection to {}", client.getFullName(), serverIpAddress);

      var outputStream = new ObjectOutputStream(socket.getOutputStream());
      outputStream.writeObject(new JoinToProjectMessage(client));
      outputStream.close();
    } catch (Exception ex) {
      LOG.error("Failed to connect to {}", serverIpAddress);
    }
  }

  public static boolean sendTaskToClient(Client taskSender, Task task) {
    Asserts.required(taskSender, "task sender cannot be null!");
    Asserts.required(task, "task cannot be null!");

    Client client = task.getAssignee();
    if (client == null) {
      throw new IllegalStateException("No assignee found for the task " + task.getId());
    }

    String clientIpAddress = client.getIpAddress();
    if (StringUtils.isNullOrEmpty(clientIpAddress)) {
      LOG.error("ipAddress of Client {} is empty or null!", client.getFullName());
      throw new IllegalArgumentException("ipAddress of Client " + client.getFullName() + " is required to share task.");
    }

    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(clientIpAddress, 9090));
      LOG.debug("Connection is established with {} ({})", client.getFullName(), clientIpAddress);

      try (var outputStream = new ObjectOutputStream(socket.getOutputStream());) {
        outputStream.writeObject(new ShareTaskMessage(taskSender, task));
        LOG.debug("Task shared with {} ({})", client.getFullName(), clientIpAddress);
      }
    } catch (Exception ex) {
      LOG.error("Failed to connect to {}", clientIpAddress);
      return false;
    }

    return true;
  }
}
