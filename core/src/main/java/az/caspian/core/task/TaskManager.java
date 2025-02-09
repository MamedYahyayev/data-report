package az.caspian.core.task;

import az.caspian.core.messaging.Client;
import az.caspian.core.remote.ClientConnection;
import az.caspian.core.template.ScrapeTemplate;
import az.caspian.core.template.TemplateExecutor;
import az.caspian.core.tree.DataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class TaskManager {
  private static final Logger LOG = LogManager.getLogger(TaskManager.class);

  private final TemplateExecutor templateExecutor;

  public TaskManager(TemplateExecutor templateExecutor) {
    this.templateExecutor = templateExecutor;
  }

  public void sendTasks(Client taskSender, Collection<Task> tasks) {
    int sharedTasks = 0;
    for (Task task : tasks) {
      boolean isShared = ClientConnection.sendTaskToClient(taskSender, task);
      if (isShared) sharedTasks++;
    }

    LOG.info("{} out of {} tasks shared with clients!", sharedTasks, tasks.size());
  }

  public void executeTask(Task task) {
    ScrapeTemplate template = task.getTemplate();
    DataTable dataTable = templateExecutor.executeTemplate(template);
    LOG.info("DataTable: {}", dataTable);
  }
}
