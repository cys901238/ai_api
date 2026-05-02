# Standalone OpenAI ping handler deployment

## Scope
- No production planning UI changes
- Standalone backend ping endpoint only
- Keep all environment identifiers as placeholders until deployment

## Handler contract
### Supported methods
- `GET`
- `POST`
- `OPTIONS`

### Example request
```http
GET /sap/bc/zpp_openai_ping?sap-client=<client>
```

### Success response example
```json
{
  "ok": true,
  "endpoint": "/sap/bc/zpp_openai_ping",
  "requestMethod": "GET",
  "message": "OpenAI connectivity test succeeded.",
  "httpStatus": 200,
  "openaiStatus": 200,
  "openaiStatusText": "OK",
  "provider": "openai",
  "sapClient": "<client>",
  "responseBody": "{...raw OpenAI response...}",
  "timestampUtc": "2026-04-23T12:30:00.0000000"
}
```

### Failure response example
```json
{
  "ok": false,
  "message": "OpenAI API key is not configured.",
  "httpStatus": 500
}
```

## ADT deployment steps
1. Open ADT against the target SAP system and client.
2. Create ABAP class `ZCL_PP_OPENAI_PING` in the chosen package.
3. Assign the correct transport request for your environment.
4. Add interface `IF_HTTP_EXTENSION`.
5. Use your local implementation source for the class body.
6. Activate the class and verify syntax.
7. Replace placeholder API key logic with secure retrieval.

## SICF deployment steps
1. Open transaction `SICF`.
2. Go to `default_host -> sap -> bc`.
3. Create service `zpp_openai_ping`.
4. Assign handler class `ZCL_PP_OPENAI_PING`.
5. Keep it as a normal HTTP service, not BSP.
6. Apply local authentication and authorization policy.
7. Activate the service.
8. Test the endpoint in browser or REST client.

## Validation checklist
- Class activates successfully
- SICF node is active
- `GET` returns JSON
- Missing key returns controlled `500` JSON
- Valid key returns upstream OpenAI status and response body

## Notes
- The outbound call is a minimal probe such as `GET https://api.openai.com/v1/models`.
- This is a connectivity check, not business logic integration.
