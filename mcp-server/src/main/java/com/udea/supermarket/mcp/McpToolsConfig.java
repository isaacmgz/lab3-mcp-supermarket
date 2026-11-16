package com.udea.supermarket.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolsConfig {

    @Bean
    public ToolCallbackProvider supermarketTools(CatalogTools catalogTools, CustomerTools customerTools, OrderTools orderTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(catalogTools, customerTools, orderTools)
                .build();
    }
}
