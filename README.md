# ai_api

Sanitized SAP RFC -> Java JCo -> OpenAI starter repository.

## What is included
- Architecture notes for RFC-to-OpenAI flow
- A minimal Spring Boot + Maven middleware skeleton
- JSON schema for structured OpenAI responses
- Sanitized deployment notes for a standalone SAP HTTP ping endpoint
- A sanitized JCo function inspection utility template

## What is intentionally excluded
- Real SAP hosts, users, passwords, clients, and transport numbers
- Real OpenAI API keys or secret retrieval code
- Proprietary `sapjco3.jar` binaries and native libraries
- Environment-specific markdown with internal endpoints

## Suggested layout
- `docs/` design and deployment notes
- `src/main/java/` middleware starter classes
- `src/main/resources/` config template and response schema

## Quick start
1. Install Java 21 and Maven.
2. Provide SAP JCo libraries through your approved internal process.
3. Copy `src/main/resources/application.example.yml` to your local runtime config.
4. Replace placeholders for SAP gateway and OpenAI settings.
5. Implement the OpenAI client and JCo server wiring.

## Notes
This repo is intentionally safe to publish. It preserves the design direction and starter code, while removing connection-specific details and secrets.
