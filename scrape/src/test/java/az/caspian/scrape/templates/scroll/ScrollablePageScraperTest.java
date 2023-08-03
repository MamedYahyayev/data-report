package az.caspian.scrape.templates.scroll;

import az.caspian.core.constant.TestConstants;
import az.caspian.core.tree.Node;
import az.caspian.scrape.templates.Scraper;
import az.caspian.core.tree.DataNode;
import az.caspian.core.tree.DataTree;
import az.caspian.core.tree.ReportDataTable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScrollablePageScraperTest {

    @Test
    @Tag(TestConstants.LONG_LASTING_TEST)
    void testPaginationPageScraper() {
        var pageParameters = new ScrollablePageParameters.Builder()
                .url("https://turbo.az/")
                .build();

        var repoItem = new DataTree<>(new Node("wrapper", ".products-i"));
        var car = new DataTree<>(new Node("car", ".products-i__name"));
        var price = new DataTree<>(new Node("price", ".products-i__price .product-price"));
        var details = new DataTree<>(new Node("details", ".products-i__attributes"));

        repoItem.addSubNode(car);
        repoItem.addSubNode(price);
        repoItem.addSubNode(details);

        ScrollablePageTemplate tree = new ScrollablePageTemplate(pageParameters, repoItem);

        Scraper<ScrollablePageTemplate> scraper = new ScrollablePageScraper();
        ReportDataTable table = scraper.scrape(tree);

        assertTrue(table.rows().size() > 0);
    }


}