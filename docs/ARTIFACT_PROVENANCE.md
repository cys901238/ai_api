# Artifact provenance

This repository was assembled from recent local SAP RFC, JCo, and OpenAI development work and then sanitized for publication.

## Included source material
- `sjg_abap_planning_webo/docs/SAP_OPENAI_JCO_ARCHITECTURE.md`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/README.md`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/pom.xml`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/src/main/resources/planning-assistant-response.schema.json`
- `sjg_abap_planning_webo/middleware/openai-jco-bridge/src/main/resources/application.yml` (sanitized into a template)
- `zpp-plan-seq/zpp-plan-seq/docs/openai-ping-adt-sicf-deployment-ds4-client400.md` (generalized and sanitized)
- `JcoFunctionCheck.java` (rewritten as a secret-free template)

## Sanitization rules applied
- Removed concrete SAP hosts, users, passwords, clients, and destination names where they were environment-specific.
- Removed transport numbers and internal URLs.
- Preserved architecture, contracts, and starter structure.
- Kept only publishable placeholders for local runtime configuration.
