package com.cys901238.aiapi.workingday;

import com.cys901238.aiapi.jco.JcoFunctionIo;
import com.cys901238.aiapi.jco.ReflectiveJcoFunctionIo;
import org.springframework.stereotype.Component;

@Component
public class ZppGetWorkingDayAiFunctionHandler {
    private final WorkingDayJcoRequestMapper requestMapper;
    private final WorkingDayAiService workingDayAiService;
    private final WorkingDayJcoResponseWriter responseWriter;
    private final WorkingDayResponseMapper responseMapper;

    public ZppGetWorkingDayAiFunctionHandler(
        WorkingDayJcoRequestMapper requestMapper,
        WorkingDayAiService workingDayAiService,
        WorkingDayJcoResponseWriter responseWriter,
        WorkingDayResponseMapper responseMapper
    ) {
        this.requestMapper = requestMapper;
        this.workingDayAiService = workingDayAiService;
        this.responseWriter = responseWriter;
        this.responseMapper = responseMapper;
    }

    public String functionName() {
        return ZppGetWorkingDayAiContract.FUNCTION_NAME;
    }

    public void handle(Object jcoFunction) {
        handle(new ReflectiveJcoFunctionIo(jcoFunction));
    }

    public void handle(JcoFunctionIo functionIo) {
        WorkingDayAiResponse response;
        try {
            WorkingDayAiRequest request = requestMapper.map(functionIo);
            response = workingDayAiService.handle(request);
        } catch (IllegalArgumentException exception) {
            response = responseMapper.insufficientInput(traceIdFrom(functionIo), exception.getMessage());
        } catch (RuntimeException exception) {
            response = responseMapper.failure(traceIdFrom(functionIo), "ZPP_GET_WORKING_DAY_AI handler failed.");
        }
        responseWriter.write(functionIo, response);
    }

    private String traceIdFrom(JcoFunctionIo functionIo) {
        String requestId = functionIo.getImportString("IV_REQUEST_ID");
        if (requestId == null || requestId.isBlank()) {
            return "wd-handler";
        }
        return requestId.replace('\n', ' ').trim();
    }
}
