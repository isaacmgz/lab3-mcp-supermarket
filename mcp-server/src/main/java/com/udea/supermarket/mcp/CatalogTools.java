package com.udea.supermarket.mcp;

import com.udea.supermarket.catalog.CatalogService;
import com.udea.supermarket.catalog.CategoryView;
import com.udea.supermarket.catalog.ProductView;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CatalogTools {

    private final CatalogService catalog;

    public CatalogTools(CatalogService catalog) {
        this.catalog = catalog;
    }

    @Tool(description = "List the supermarket product categories. Category names are in Spanish.")
    public List<CategoryView> listCategories() {
        return catalog.listCategories();
    }

    @Tool(description = """
            Search active products by text in their name, description or category. The catalog is in Spanish, \
            so search with Spanish words in singular or plural (e.g. 'leche', 'huevos', 'pan tajado', 'arroz'). \
            Accents are optional. Returns up to 20 products with SKU, unit, price in COP and available stock.""")
    public List<ProductView> searchProducts(
            @ToolParam(description = "Text to look for, in Spanish, e.g. 'leche' or 'café'") String query,
            @ToolParam(description = "Optional category name to narrow the search, e.g. 'Lácteos y huevos'", required = false) String category) {
        return catalog.search(query, category);
    }

    @Tool(description = "Get the details of one product by its SKU (e.g. 'LAC-001').")
    public ProductView getProduct(@ToolParam(description = "Product SKU, e.g. 'LAC-001'") String sku) {
        return catalog.getBySku(sku);
    }
}
