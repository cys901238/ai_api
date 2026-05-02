# SAP RFC -> Java JCo Middleware -> OpenAI -> SAP RFC

## Goal
Define a standard architecture where SAP sends production-planning context by RFC to a Java middleware, the middleware calls OpenAI, and the result is mapped back into SAP response structures.

## Recommended flow
1. SAP ABAP RFC caller invokes `Z_OPENAI_PLAN_REQUEST`.
2. Java JCo server receives RFC import and table parameters.
3. Middleware normalizes SAP payload into `PlanningAssistRequest`.
4. Prompt builder creates the OpenAI request.
5. OpenAI returns structured JSON.
6. Response mapper converts the result to SAP export and table structures.
7. JCo server returns the RFC response to SAP.
8. Errors are normalized into a standard SAP return table.

## Logical components
- SAP ABAP caller and follow-up persistence logic
- Java JCo server endpoint
- Request and response mapper
- OpenAI orchestration service
- Validation, timeout, retry, audit logging

## Draft RFC contract
### Import
- `IV_REQUEST_ID` CHAR36
- `IV_SCENARIO` CHAR30
- `IV_WERKS` WERKS_D
- `IV_ARBPL` ARBPL
- `IV_PSTTR` DATS
- `IV_LANGUAGE` LANGU
- `IV_MAX_SUGGESTIONS` INT4
- `IV_MODEL` CHAR40

### Tables
#### `IT_PLAN_ITEMS`
- `SEQNR`
- `MATNR`
- `MAKTX`
- `GSMNG`
- `UPH`
- `ZUPH`
- `SHIFT`
- `OBTYPE`
- `PLNUM`
- `BERID`
- `VERID`
- `LGPRO`

#### `IT_CAPACITY`
- `SHIFT`
- `AVAILABLE_HOURS`
- `LOAD_HOURS`
- `LOAD_RATE`
- `TOOL_CHANGE_HOURS`

### Export
- `EV_STATUS` (`S`, `E`, `W`)
- `EV_MESSAGE`
- `EV_TRACE_ID`

### Return tables
#### `ET_SUGGESTIONS`
- `LINE_NO`
- `ACTION_TYPE`
- `SEQNR`
- `TARGET_SHIFT`
- `TARGET_QTY`
- `TARGET_UPH`
- `REASON_CODE`
- `REASON_TEXT`
- `CONFIDENCE`

#### `ET_RETURN`
- `TYPE`
- `ID`
- `NUMBER`
- `MESSAGE`
- `LOG_NO`
- `LOG_MSG_NO`
- `MESSAGE_V1` to `MESSAGE_V4`

## Non-functional guidance
- SAP -> middleware timeout: about 15s
- Middleware -> OpenAI timeout: about 10s
- Retry only on OpenAI 429 and 5xx
- Keep secrets in env or vault, never in source
- Exclude unnecessary personal data from prompts

## Recommended development order
1. Finalize SAP FM and DDIC structures.
2. Start the Java JCo server skeleton.
3. Validate RFC round-trip with mocked OpenAI output.
4. Attach the structured output schema.
5. Connect SAP result handling.
6. Run timeout, volume, and error tests.
