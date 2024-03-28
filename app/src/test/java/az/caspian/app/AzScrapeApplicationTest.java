package az.caspian.app;

import az.caspian.core.constant.TestConstants;
import az.caspian.core.model.DataFile;
import az.caspian.core.model.enumeration.FileType;
import az.caspian.core.task.Task;
import az.caspian.core.template.ScrapeTemplate;
import az.caspian.core.tree.*;
import az.caspian.export.CsvExporter;
import az.caspian.export.ExcelExporter;
import az.caspian.export.Exporter;
import az.caspian.scrape.templates.Scraper;
import az.caspian.scrape.templates.pagination.PageParameters;
import az.caspian.scrape.templates.pagination.PaginationPageScraper;
import az.caspian.scrape.templates.pagination.PaginationTemplate;
import az.caspian.scrape.templates.pagination.item.PaginationItemVisitorScraper;
import az.caspian.scrape.templates.pagination.item.PaginationItemVisitorTemplate;
import az.caspian.scrape.templates.scroll.ScrollablePageParameters;
import az.caspian.scrape.templates.scroll.ScrollablePageScraper;
import az.caspian.scrape.templates.scroll.ScrollablePageTemplate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static az.caspian.scrape.templates.pagination.PageParameters.PAGE_SPECIFIER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AzScrapeApplicationTest {
  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testExporting() {
    var pageParameters = new PageParameters.Builder()
      .url(
        "https://bina.az/baki/alqi-satqi/menziller/yeni-tikili/1-otaqli?page="
          + PAGE_SPECIFIER)
      .pageRange(1, 3)
      .delayBetweenPages(6000)
      .build();

    var listNode = new ListNode("repoItem", ".items-i");
    var location = new DataNode("location", ".card_params .location");
    var price = new DataNode("price", ".card_params .price-val");
    var currency = new DataNode("currency", ".card_params .price-cur");
    var roomCount = new DataNode("room count", ".card_params .name > li:nth-child(1)");
    var area = new DataNode("area", ".card_params .name > li:nth-child(2)");
    var floor = new DataNode("floor", ".card_params .name > li:nth-child(3)");

    listNode.addChild(location);
    listNode.addChild(price);
    listNode.addChild(currency);
    listNode.addChild(roomCount);
    listNode.addChild(area);
    listNode.addChild(floor);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(listNode);

    PaginationTemplate paginationTemplate = new PaginationTemplate(pageParameters, tree);

    Scraper<PaginationTemplate> scraper = new PaginationPageScraper();
    DataTable table = scraper.scrape(paginationTemplate);

    ExcelExporter excelExporter = new ExcelExporter();

    DataFile dataFile =
      new DataFile.Builder()
        .filename("one_room_apartment")
        .fileType(FileType.EXCEL)
        .storeAt(Path.of("C:/Users/User/Desktop").toString())
        .build();

    excelExporter.export(dataFile, table);
  }

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testContactHome() {
    var pageParameters = new PageParameters.Builder()
      .url("https://kontakt.az/telefonlar/mobil-telefonlar/page/" + PAGE_SPECIFIER)
      .pageRange(1, 5)
      .delayBetweenPages(3000)
      .build();

    var listNode = new ListNode("repoItem", ".cart-item");
    var phone = new DataNode("name", ".cart-body-top .name > a");
    var price = new DataNode("price", ".cart-footer > p .nprice");
    var currency = new DataNode("currency", ".cart-footer > p .nprice + small");

    listNode.addChild(phone);
    listNode.addChild(price);
    listNode.addChild(currency);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(listNode);

    PaginationTemplate paginationTemplate = new PaginationTemplate(pageParameters, tree);

    Scraper<PaginationTemplate> scraper = new PaginationPageScraper();
    DataTable table = scraper.scrape(paginationTemplate);

    ExcelExporter excelExporter = new ExcelExporter();

    DataFile dataFile = new DataFile.Builder()
      .filename("smartphones")
      .fileType(FileType.EXCEL)
      .storeAt(System.getProperty("user.home") + File.separator + "export-file")
      .build();

    excelExporter.export(dataFile, table);
    assertNotNull(dataFile.getFileAbsolutePath());
  }

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testScrollablePageTurboAz() {
    var pageParameters = new ScrollablePageParameters.Builder()
      .url("https://turbo.az/")
      .build();

    var listNode = new ListNode("wrapper", ".products-i");
    var car = new DataNode("car", ".products-i__name");
    var price = new DataNode("price", ".products-i__price .product-price");
    var details = new DataNode("details", ".products-i__attributes");
    listNode.addChild(car);
    listNode.addChild(price);
    listNode.addChild(details);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(listNode);

    var template = new ScrollablePageTemplate(pageParameters, tree);

    Scraper<ScrollablePageTemplate> scraper = new ScrollablePageScraper();
    DataTable table = scraper.scrape(template);

    var excelExporter = new ExcelExporter();

    var reportFile = new DataFile.Builder()
      .filename("turbo_az")
      .storeAt(Path.of("C:/Users/User/Desktop").toString())
      .fileType(FileType.EXCEL)
      .build();

    excelExporter.export(reportFile, table);
  }

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testTurboAzWithPaginationTemplate() {
    var pageParameters = new PageParameters.Builder()
      .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
      .pageRange(1, 416)
      .delayBetweenPages(3000)
      .build();

    var listNode = new ListNode("wrapper", ".products-i");
    var car = new DataNode("car", ".products-i__name");
    var price = new DataNode("price", ".products-i__price .product-price");
    var details = new DataNode("details", ".products-i__attributes");

    listNode.addChild(car);
    listNode.addChild(price);
    listNode.addChild(details);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(listNode);

    PaginationTemplate template = new PaginationTemplate(pageParameters, tree);

    Scraper<PaginationTemplate> scraper = new PaginationPageScraper();
    DataTable table = scraper.scrape(template);

    ExcelExporter excelExporter = new ExcelExporter();

    DataFile dataFile =
      new DataFile.Builder()
        .filename("turbo_az")
        .fileType(FileType.EXCEL)
        .build();

    excelExporter.export(dataFile, table);
  }

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testCallbackCalledWhenInternetConnectionGone() {
    var pageParameters =
      new PageParameters.Builder()
        .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
        .pageRange(1, 416)
        .delayBetweenPages(3000)
        .build();

    var listNode = new ListNode("listNode", ".products-i");
    var car = new DataNode("car", ".products-i__name");
    var price = new DataNode("price", ".products-i__price .product-price");
    var details = new DataNode("details", ".products-i__attributes");

    listNode.addChild(car);
    listNode.addChild(price);
    listNode.addChild(details);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(listNode);

    PaginationTemplate template = new PaginationTemplate(pageParameters, tree);

    Scraper<PaginationTemplate> scraper = new PaginationPageScraper(this::callback);
    assertThrows(Exception.class, () -> scraper.scrape(template));
  }

  void callback(String message, DataTable data) {
    System.out.println(message);
    ExcelExporter excelExporter = new ExcelExporter();

    DataFile dataFile =
      new DataFile.Builder()
        .filename("turbo_az_with_callback")
        .fileType(FileType.EXCEL)
        .build();

    excelExporter.export(dataFile, data);
  }

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testPaginationItemVisitorTemplate() {
    var pageParameters =
      new PageParameters.Builder()
        .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
        .pageNum(1)
        .delayBetweenPages(3000)
        .build();

    var link = new ListNode("link", ".products-i__link");
    var carNode = new DataNode("car", ".product-title");
    var price = new DataNode("price", ".product-price > div:first-child");
    var advertisementId = new DataNode("advertisement number", ".product-actions__id");
    var description = new DataNode("description", ".product-description__content");
    var updateTime = new DataNode("update time", ".product-statistics__i:first-child");
    var viewCount = new DataNode("view count", ".product-statistics__i:last-child");

    var propertyWrapper = new ListNode("wrapper", ".product-properties__i");
    var properties =
      new KeyValueDataNode(".product-properties__i-name", ".product-properties__i-value");
    propertyWrapper.addChild(properties);

    link.addChild(carNode);
    link.addChild(price);
    link.addChild(advertisementId);
    link.addChild(description);
    link.addChild(updateTime);
    link.addChild(viewCount);
    link.addChild(propertyWrapper);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(link);

    PaginationItemVisitorTemplate template =
      new PaginationItemVisitorTemplate(pageParameters, tree);

    PaginationItemVisitorScraper scraper = new PaginationItemVisitorScraper();
    DataTable table = scraper.scrape(template);

    ExcelExporter excelExporter = new ExcelExporter();

    DataFile dataFile =
      new DataFile.Builder()
        .filename("turbo_az")
        .storeAt(Path.of("C:/Users/User/Desktop").toString())
        .fileType(FileType.EXCEL)
        .build();

    excelExporter.export(dataFile, table);
  }

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testPaginationItemVisitorTemplateWithExportToCsv() {
    var pageParameters = new PageParameters.Builder()
      .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
      .pageNum(1)
      .delayBetweenPages(3000)
      .build();

    var link = new ListNode("link", ".products-i__link");
    var carNode = new DataNode("car", ".product-title");
    var price = new DataNode("price", ".product-price > div:first-child");
    var advertisementId = new DataNode("advertisement number", ".product-actions__id");
    var description = new DataNode("description", ".product-description__content");
    var updateTime = new DataNode("update time", ".product-statistics__i:first-child");
    var viewCount = new DataNode("view count", ".product-statistics__i:last-child");

    var propertyWrapper = new ListNode("wrapper", ".product-properties__i");
    var properties =
      new KeyValueDataNode(".product-properties__i-name", ".product-properties__i-value");
    propertyWrapper.addChild(properties);

    link.addChild(carNode);
    link.addChild(price);
    link.addChild(advertisementId);
    link.addChild(description);
    link.addChild(updateTime);
    link.addChild(viewCount);
    link.addChild(propertyWrapper);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(link);

    var template = new PaginationItemVisitorTemplate(pageParameters, tree);

    var scraper = new PaginationItemVisitorScraper();
    DataTable table = scraper.scrape(template);

    Exporter csvExporter = new CsvExporter();

    //TODO: We can create multiple DataFileFormat such as CsvDataFile, ExcelDataFile. Each file will has own
    // parameters. And Builder will return exact fileFormat with its own parameters, after fileType()
    DataFile dataFile = new DataFile.Builder()
      .filename("turbo_az")
      .storeAt(Path.of("C:/Users/User/Desktop").toString())
      .fileType(FileType.CSV)
      .build();

    csvExporter.export(dataFile, table);
  }

  // TODO: 2509 - bina.az https://bina.az/alqi-satqi?page=2509

  @Test
  @Tag(TestConstants.LONG_LASTING_TEST)
  void testMultipleChromeInstanceWithThreads() throws InterruptedException, ExecutionException {
    var pageParameters1 = new PageParameters.Builder()
      .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
      .pageNum(1)
      .delayBetweenPages(3000)
      .build();

    var pageParameters2 = new PageParameters.Builder()
      .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
      .pageNum(2)
      .delayBetweenPages(3000)
      .build();

    var pageParameters3 = new PageParameters.Builder()
      .url("https://turbo.az/autos?page=" + PAGE_SPECIFIER)
      .pageNum(3)
      .delayBetweenPages(3000)
      .build();

    var link = new ListNode("link", ".products-i__link");
    var carNode = new DataNode("car", ".product-title");
    var price = new DataNode("price", ".product-price > div:first-child");
    var advertisementId = new DataNode("advertisement number", ".product-actions__id");
    var description = new DataNode("description", ".product-description__content");
    var updateTime = new DataNode("update time", ".product-statistics__i:first-child");
    var viewCount = new DataNode("view count", ".product-statistics__i:last-child");

    var propertyWrapper = new ListNode("wrapper", ".product-properties__i");
    var properties =
      new KeyValueDataNode(".product-properties__i-name", ".product-properties__i-value");
    propertyWrapper.addChild(properties);

    link.addChild(carNode);
    link.addChild(price);
    link.addChild(advertisementId);
    link.addChild(description);
    link.addChild(updateTime);
    link.addChild(viewCount);
    link.addChild(propertyWrapper);

    DataTree<Node> tree = new DataTree<>();
    tree.addNode(link);

    var template1 = new PaginationItemVisitorTemplate(pageParameters1, tree);
    var template2 = new PaginationItemVisitorTemplate(pageParameters2, tree);
    var template3 = new PaginationItemVisitorTemplate(pageParameters3, tree);

    Task task1 = new Task("#1", "turboaz-scraping", template1, null);
    Task task2 = new Task("#2", "turboaz-scraping", template2, null);
    Task task3 = new Task("#3", "turboaz-scraping", template3, null);

    TaskExecutor executor1 = new TaskExecutor(task1);
    TaskExecutor executor2 = new TaskExecutor(task2);
    TaskExecutor executor3 = new TaskExecutor(task3);

    ExecutorService executorService = Executors.newFixedThreadPool(3);
    List<Future<DataTable>> futures = executorService.invokeAll(List.of(executor1, executor2, executor3));

    DataTable table = new DataTable();
    for (Future<DataTable> future : futures) {
      DataTable dataTable = future.get();
      table.addAll(dataTable.rows());
    }

    Exporter csvExporter = new CsvExporter();

    DataFile dataFile = new DataFile.Builder()
      .filename("turbo_az")
      .storeAt(Path.of("C:/Users/Admin/Desktop").toString())
      .fileType(FileType.CSV)
      .build();

    csvExporter.export(dataFile, table);
  }

  static class TaskExecutor implements Callable<DataTable> {
    private final Task task;

    TaskExecutor(Task task) {
      this.task = task;
    }

    @Override
    public DataTable call() throws Exception {
      PaginationItemVisitorTemplate template = (PaginationItemVisitorTemplate) task.getTemplate();
      var scraper = new PaginationItemVisitorScraper();
      DataTable table = scraper.scrape(template);
      System.out.printf("""
        Task %s, completed its job and it collects %d rows.\s
        """, task.getId(), table.rows().size());
      return table;
    }
  }

}
