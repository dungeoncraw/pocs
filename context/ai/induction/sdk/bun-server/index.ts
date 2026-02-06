import { InductionSDK } from "./InductionSDK";
import { HttpProfileFetcher, FileProfileFetcher, CompositeProfileFetcher } from "./services";

const profileServerUrl = process.env.PROFILE_SERVER_URL || "http://localhost:3000";
console.log(`[Bun Server] Using Profile Server at: ${profileServerUrl}`);

const httpFetcher = new HttpProfileFetcher(profileServerUrl);
const fileFetcher = new FileProfileFetcher("./profiles");
const fetcher = new CompositeProfileFetcher([fileFetcher, httpFetcher]);

const sdk = new InductionSDK(fetcher);

const server = Bun.serve({
  port: 4000,
  async fetch(req) {
    const url = new URL(req.url);
    const profileId = req.headers.get("X-Induction-Profile-ID");

    console.log(`[Bun Server] Incoming request: ${req.method} ${url.pathname} (Profile-ID: ${profileId})`);

    if (url.pathname === "/test-induction") {
      const targetUrl = url.searchParams.get("url") || "https://httpbin.org/json";
      try {
        const response = await sdk.instrumentRequest(targetUrl, {
          headers: {
            "X-Induction-Profile-ID": profileId || "",
          },
        });
        
        const body = await response.text();
        return new Response(JSON.stringify({
          status: response.status,
          body: body.startsWith("{") ? JSON.parse(body) : body
        }), {
          headers: { "Content-Type": "application/json" }
        });
      } catch (error: any) {
        return new Response(JSON.stringify({
          error: "Induced or unexpected error",
          message: error.message
        }), {
          status: 500,
          headers: { "Content-Type": "application/json" }
        });
      }
    }

    if (url.pathname === "/ping") {
        return new Response("pong");
    }

    return new Response("Induction Bun Server. Use /test-induction?url=... and set X-Induction-Profile-ID header.", { status: 200 });
  },
});

console.log(`[Bun Server] Listening on http://localhost:${server.port}`);
