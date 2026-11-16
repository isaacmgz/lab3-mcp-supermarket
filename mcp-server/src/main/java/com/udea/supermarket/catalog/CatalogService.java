package com.udea.supermarket.catalog;

import com.udea.supermarket.shared.BusinessRuleException;
import com.udea.supermarket.shared.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final CategoryRepository categories;
    private final ProductRepository products;

    public CatalogService(CategoryRepository categories, ProductRepository products) {
        this.categories = categories;
        this.products = products;
    }

    public List<CategoryView> listCategories() {
        return categories.findAll(Sort.by("name")).stream().map(CategoryView::from).toList();
    }

    public List<ProductView> search(String text, String category) {
        String normalizedText = text == null ? "" : text.trim();
        String normalizedCategory = category == null ? "" : category.trim();
        if (normalizedText.isEmpty() && normalizedCategory.isEmpty()) {
            throw new BusinessRuleException("Provide a search text or a category.");
        }
        return products.search(normalizedText, normalizedCategory).stream().map(ProductView::from).toList();
    }

    public ProductView getBySku(String sku) {
        return ProductView.from(findActiveBySku(sku));
    }

    public Product findActiveBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new BusinessRuleException("A product SKU is required.");
        }
        return products.findBySku(sku.trim())
                .filter(Product::isActive)
                .orElseThrow(() -> new NotFoundException("No active product with SKU '" + sku + "'. Search the catalog first."));
    }
}
