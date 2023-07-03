package az.caspian.scrape.templates.pagination.item;

import az.caspian.core.model.DataColumn;
import az.caspian.core.model.DataRow;
import az.caspian.core.tree.DataNode;
import az.caspian.core.tree.DataTree;
import az.caspian.core.tree.ReportDataTable;
import az.caspian.scrape.WebBrowser;
import az.caspian.scrape.WebPage;
import az.caspian.scrape.templates.AbstractScrapeTemplate;
import az.caspian.scrape.templates.ScrapeErrorCallback;
import org.openqa.selenium.WebElement;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaginationItemPageScraper extends AbstractScrapeTemplate<PaginationItemTemplate> {

    private ScrapeErrorCallback callback;

    public PaginationItemPageScraper() {
    }

    public PaginationItemPageScraper(ScrapeErrorCallback callback) {
        this.callback = callback;
    }

    public ReportDataTable scrape(PaginationItemTemplate template) {
        ReportDataTable reportDataTable = new ReportDataTable();

        String url = null;
        int current = 0;
        try (WebBrowser browser = new WebBrowser()) {
            browser.open();

            var pageParameters = template.getPageParameters();
            for (current = pageParameters.getMinPage(); current <= pageParameters.getMaxPage(); current++) {
                url = pageParameters.getPageUrl(current);

                WebPage page = browser.goTo(url, pageParameters.getDelayBetweenPages());

                DataTree<DataNode> tree = template.getTree();
                List<WebElement> elements = page.fetchWebElements(tree.getRoot().getSelector());
                for (WebElement element : elements) {
                    List<DataRow> dataRows = new ArrayList<>();
                    String urlOfSubPage = element.getAttribute("href");
                    WebPage webPage = browser.goTo(urlOfSubPage);

                    DataRow dataRow = collectPageData(tree, webPage);
                    dataRows.add(dataRow);

                    browser.backToPrevPage();

                    reportDataTable.addAll(dataRows);
                }
            }
        } catch (Exception e) {
            String message = MessageFormat.format(
                    "Failed to scrape data from {0} in page {1}, Exception: {2}", url, current, e.getMessage()
            );

            if (callback != null)
                callback.handle(message, reportDataTable);

            throw new RuntimeException(message, e);
        }

        return reportDataTable;
    }

    private DataRow collectPageData(DataTree<DataNode> tree, WebPage page) {
        List<DataColumn> dataColumns = new ArrayList<>();
        List<DataNode> children = tree.getChildren(tree.getRoot());
        for (DataNode node : children) {
            if (!node.isParent()) {
                WebElement element = page.fetchWebElement(node.getSelector());
                String value = element.getText();
                dataColumns.add(new DataColumn(node.getName(), value));
            } else if (node.isKeyValuePair()) {
                String value = page.fetchWebElements(node.getSelector()).stream()
                        .map(WebElement::getText)
                        .collect(Collectors.joining());
                dataColumns.add(new DataColumn(node.getName(), value));
            }
        }

        dataColumns.add(new DataColumn("link", page.getUrl()));

        var dataRow = new DataRow();
        dataRow.addColumns(dataColumns);

        return dataRow;
    }
}
