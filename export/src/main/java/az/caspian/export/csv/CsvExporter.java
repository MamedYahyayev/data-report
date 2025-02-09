package az.caspian.export.csv;

import az.caspian.core.AzScrapeAppException;
import az.caspian.core.model.DataFile;
import az.caspian.core.model.enumeration.FileType;
import az.caspian.core.tree.DataTable;
import az.caspian.core.utils.Asserts;
import az.caspian.export.AbstractExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CsvExporter extends AbstractExporter {
  private static final Logger LOG = LogManager.getLogger(CsvExporter.class);

  private static final char DEFAULT_DELIMITER = ',';

  private final char delimiter;

  public CsvExporter() {
    this.delimiter = DEFAULT_DELIMITER;
  }

  public CsvExporter(char delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public void export(final DataFile dataFile, final DataTable dataTable) {
    Asserts.required(dataFile, "dataFile is required");
    Asserts.required(dataTable, "dataTable is required");

    if (dataFile.getFiletype() != FileType.CSV) {
      throw new IllegalStateException(
        "CSV file type is expecting, but got %s".formatted(dataFile.getFiletype()));
    }

    File file = constructReportFile(dataFile);

    try {
      var csvWriter = new ApacheCsvWriter();
      csvWriter.writeData(dataTable, file.toPath());
    } catch (IOException ex) {
      String message = "Failed to write into CSV file [" + file.getAbsolutePath() + "]";
      LOG.error(message, ex);
      throw new AzScrapeAppException(message, ex);
    }
  }
}
