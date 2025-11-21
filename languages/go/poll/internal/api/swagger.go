package api

import (
    "net/http"
)

const openAPISpec = `{
  "openapi": "3.0.3",
  "info": {
    "title": "Poll Service API",
    "version": "1.0.0",
    "description": "Simple high-throughput poll/voting service."
  },
  "servers": [
    { "url": "/" },
    { "url": "/api/v1" }
  ],
  "tags": [
    { "name": "Public", "description": "Public endpoints for voting and reading polls/options" },
    { "name": "Admin", "description": "Administrative CRUD for polls, options and voters" },
    { "name": "Legacy", "description": "Legacy flat endpoints maintained for backward compatibility" }
  ],
  "paths": {
    "/api/v1/public/vote": {
      "post": {
        "tags": ["Public"],
        "summary": "Submit a vote",
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": { "$ref": "#/components/schemas/VoteRequest" }
            }
          }
        },
        "responses": {
          "202": { "description": "Accepted" },
          "404": { "description": "Poll/Option not found" },
          "409": { "description": "Poll is closed" },
          "503": { "description": "Server busy" }
        }
      }
    },
    "/api/v1/admin/polls": {
      "get": {
        "tags": ["Admin"],
        "summary": "Get a poll or list polls (admin)",
        "parameters": [
          {"name":"id","in":"query","required":false,"schema":{"type":"string"}}
        ],
        "responses": {
          "200": {
            "description": "Poll snapshot or list of polls",
            "content": {"application/json": {"schema": {"oneOf": [
              {"$ref": "#/components/schemas/PollResponse"},
              {"type": "array", "items": {"$ref": "#/components/schemas/PollResponse"}}
            ]}}}
          },
          "404": {"description": "Not found"}
        }
      },
      "post": {
        "tags": ["Admin"],
        "summary": "Create a poll",
        "requestBody": {"required": true, "content": {"application/json": {"schema": {"type":"object","required":["id","question"],"properties":{"id":{"type":"string"},"question":{"type":"string"},"is_open":{"type":"boolean"}}}}}},
        "responses": {"201": {"description": "Created"}, "409": {"description": "Already exists"}}
      },
      "put": {
        "tags": ["Admin"],
        "summary": "Update a poll",
        "requestBody": {"required": true, "content": {"application/json": {"schema": {"type":"object","required":["id","question","is_open"],"properties":{"id":{"type":"string"},"question":{"type":"string"},"is_open":{"type":"boolean"}}}}}},
        "responses": {"204": {"description": "No Content"}, "404": {"description": "Not found"}}
      },
      "delete": {
        "tags": ["Admin"],
        "summary": "Delete a poll",
        "parameters": [{"name":"id","in":"query","required":true,"schema":{"type":"string"}}],
        "responses": {"204": {"description": "No Content"}, "404": {"description": "Not found"}}
      }
    },
    "/api/v1/public/poll": {
      "get": {
        "tags": ["Public"],
        "summary": "Get poll snapshot",
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "required": true,
            "schema": { "type": "string" }
          }
        ],
        "responses": {
          "200": {
            "description": "Poll snapshot",
            "content": {
              "application/json": {
                "schema": { "$ref": "#/components/schemas/PollResponse" }
              }
            }
          },
          "404": { "description": "Not found" }
        }
      }
    },
    "/api/v1/admin/options": {
      "get": {
        "tags": ["Admin"],
        "summary": "Get an option or list options (admin)",
        "parameters": [
          {"name":"id","in":"query","required":false,"schema":{"type":"string"}},
          {"name":"poll_id","in":"query","required":false,"schema":{"type":"string"}}
        ],
        "responses": {
          "200": {
            "description": "Option item or list of options",
            "content": {"application/json": {"schema": {"oneOf": [
              {"$ref": "#/components/schemas/OptionItem"},
              {"type": "array", "items": {"$ref": "#/components/schemas/OptionItem"}}
            ]}}}
          },
          "404": {"description": "Not found"}
        }
      },
      "post": {
        "tags": ["Admin"],
        "summary": "Add option to a poll",
        "requestBody": {"required": true, "content": {"application/json": {"schema": {"type":"object","required":["id","poll_id","label"],"properties":{"id":{"type":"string"},"poll_id":{"type":"string"},"label":{"type":"string"}}}}}},
        "responses": {"201": {"description": "Created"}, "404": {"description": "Poll not found"}, "409": {"description": "Already exists"}}
      },
      "put": {
        "tags": ["Admin"],
        "summary": "Update an option label",
        "requestBody": {"required": true, "content": {"application/json": {"schema": {"type":"object","required":["id","label"],"properties":{"id":{"type":"string"},"label":{"type":"string"}}}}}},
        "responses": {"204": {"description": "No Content"}, "404": {"description": "Not found"}}
      },
      "delete": {
        "tags": ["Admin"],
        "summary": "Delete an option",
        "parameters": [{"name":"id","in":"query","required":true,"schema":{"type":"string"}}],
        "responses": {"204": {"description": "No Content"}, "404": {"description": "Not found"}}
      }
    },
    "/api/v1/public/option_item": {
      "get": {
        "tags": ["Public"],
        "summary": "Get option details",
        "parameters": [
          {
            "name": "id",
            "in": "query",
            "required": true,
            "schema": { "type": "string" }
          }
        ],
        "responses": {
          "200": {
            "description": "Option item",
            "content": {
              "application/json": {
                "schema": { "$ref": "#/components/schemas/OptionItem" }
              }
            }
          },
          "404": { "description": "Not found" }
        }
      }
    },
    "/api/v1/admin/voters": {
      "get": {
        "tags": ["Admin"],
        "summary": "List voters of a poll",
        "parameters": [{"name":"poll_id","in":"query","required":true,"schema":{"type":"string"}}],
        "responses": {"200": {"description": "Voter list"}, "404": {"description": "Poll not found"}}
      },
      "post": {
        "tags": ["Admin"],
        "summary": "Add a voter to a poll",
        "requestBody": {"required": true, "content": {"application/json": {"schema": {"type":"object","required":["poll_id","voter_id"],"properties":{"poll_id":{"type":"string"},"voter_id":{"type":"string"}}}}}},
        "responses": {"201": {"description": "Created"}, "404": {"description": "Poll not found"}, "409": {"description": "Already exists"}}
      },
      "delete": {
        "tags": ["Admin"],
        "summary": "Remove a voter from a poll",
        "parameters": [
          {"name":"poll_id","in":"query","required":true,"schema":{"type":"string"}},
          {"name":"voter_id","in":"query","required":true,"schema":{"type":"string"}}
        ],
        "responses": {"204": {"description": "No Content"}, "404": {"description": "Not found"}}
      }
    },
    "/vote": {
      "post": {
        "tags": ["Legacy"],
        "summary": "Submit a vote (legacy)",
        "deprecated": true,
        "requestBody": {"required": true, "content": {"application/json": {"schema": { "$ref": "#/components/schemas/VoteRequest" }}}},
        "responses": {"202": {"description": "Accepted"}}
      }
    },
    "/poll": {
      "get": {
        "tags": ["Legacy"],
        "summary": "Get poll snapshot (legacy)",
        "deprecated": true,
        "parameters": [{"name":"id","in":"query","required":true,"schema":{"type":"string"}}],
        "responses": {"200": {"description": "OK"}}
      }
    },
    "/options": {
      "get": {"tags": ["Legacy"], "summary": "Get option or list options (legacy)", "deprecated": true, "parameters": [{"name":"id","in":"query","required":false,"schema":{"type":"string"}}, {"name":"poll_id","in":"query","required":false,"schema":{"type":"string"}}], "responses": {"200": {"description": "OK"}}},
      "post": {"tags": ["Legacy"], "summary": "Add option (legacy)", "deprecated": true},
      "put": {"tags": ["Legacy"], "summary": "Update option (legacy)", "deprecated": true},
      "delete": {"tags": ["Legacy"], "summary": "Delete option (legacy)", "deprecated": true}
    },
    "/option_item": {
      "get": {"tags": ["Legacy"], "summary": "Get option (legacy)", "deprecated": true, "parameters": [{"name":"id","in":"query","required":true,"schema":{"type":"string"}}], "responses": {"200": {"description": "OK"}}}
    },
    "/polls": {
      "get": {"tags": ["Legacy"], "summary": "Get poll or list polls (legacy)", "deprecated": true, "parameters": [{"name":"id","in":"query","required":false,"schema":{"type":"string"}}], "responses": {"200": {"description": "OK"}}},
      "post": {"tags": ["Legacy"], "summary": "Create poll (legacy)", "deprecated": true},
      "put": {"tags": ["Legacy"], "summary": "Update poll (legacy)", "deprecated": true},
      "delete": {"tags": ["Legacy"], "summary": "Delete poll (legacy)", "deprecated": true}
    },
    "/voters": {
      "get": {"tags": ["Legacy"], "summary": "List voters (legacy)", "deprecated": true, "parameters": [{"name":"poll_id","in":"query","required":true,"schema":{"type":"string"}}], "responses": {"200": {"description": "OK"}}},
      "post": {"tags": ["Legacy"], "summary": "Add voter (legacy)", "deprecated": true},
      "delete": {"tags": ["Legacy"], "summary": "Delete voter (legacy)", "deprecated": true}
    }
  },
  "components": {
    "schemas": {
      "VoteRequest": {
        "type": "object",
        "required": ["poll_id", "option_id", "voter_id"],
        "properties": {
          "poll_id": { "type": "string" },
          "option_id": { "type": "string" },
          "voter_id": { "type": "string" }
        }
      },
      "OptionItem": {
        "type": "object",
        "properties": {
          "id": { "type": "string" },
          "label": { "type": "string" },
          "votes": { "type": "integer" }
        }
      },
      "PollResponse": {
        "type": "object",
        "properties": {
          "id": { "type": "string" },
          "question": { "type": "string" },
          "is_open": { "type": "boolean" },
          "options": {
            "type": "array",
            "items": { "$ref": "#/components/schemas/OptionItem" }
          },
          "voters": {
            "type": "array",
            "items": { "type": "string" }
          }
        }
      }
    }
  }
}`

func registerSwagger() {
    http.HandleFunc("/swagger.json", func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("Content-Type", "application/json")
        _, _ = w.Write([]byte(openAPISpec))
    })

    http.HandleFunc("/swagger", func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("Content-Type", "text/html; charset=utf-8")
        _, _ = w.Write([]byte(`<!doctype html>
<html>
  <head>
    <meta charset="utf-8" />
    <title>Poll Service API Docs</title>
    <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css" />
    <style>body { margin: 0; } #swagger-ui { height: 100vh; }</style>
  </head>
  <body>
    <div id="swagger-ui"></div>
    <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
    <script>
      window.ui = SwaggerUIBundle({
        url: '/swagger.json',
        dom_id: '#swagger-ui',
        presets: [SwaggerUIBundle.presets.apis],
        layout: 'BaseLayout'
      });
    </script>
  </body>
</html>`))
    })
}
