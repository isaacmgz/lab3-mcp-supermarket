package com.udea.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;

public class JsonToolErrorProcessor implements ToolExecutionExceptionProcessor {

    private static final Pattern MCP_TEXT = Pattern.compile("text=(.*?)(?:, meta=|\\]\\]$)", Pattern.DOTALL);

    private final ObjectMapper mapper;

    public JsonToolErrorProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String process(ToolExecutionException exception) {
        try {
            return mapper.writeValueAsString(Map.of("error", messageOf(exception)));
        } catch (JsonProcessingException ex) {
            return "{\"error\":\"The tool call failed.\"}";
        }
    }

    static String messageOf(Throwable exception) {
        String message = exception.getMessage() == null ? "The tool call failed." : exception.getMessage();
        Matcher matcher = MCP_TEXT.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : message;
    }
}
