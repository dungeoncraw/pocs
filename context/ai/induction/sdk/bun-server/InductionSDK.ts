import { Profile, Action, Transformation } from "./models";
import { ProfileFetcher, Environment } from "./services";

export class InductionSDK {
  private readonly HEADER_NAME = "X-Induction-Profile-ID";

  constructor(private fetcher: ProfileFetcher) {}

  async instrumentRequest(url: string, init?: RequestInit): Promise<Response> {
    if (Environment.isProduction()) {
      return fetch(url, init);
    }

    const headers = new Headers(init?.headers);
    const profileId = headers.get(this.HEADER_NAME);

    if (profileId) {
      const profile = await this.fetcher.fetch(profileId);
      if (profile) {
        return this.applyProfile(url, init, profile);
      }
    }

    return fetch(url, init);
  }

  private async applyProfile(url: string, init: RequestInit | undefined, profile: Profile): Promise<Response> {
    switch (profile.action) {
      case Action.CallReal:
        return fetch(url, init);

      case Action.MockWhole:
        const body = profile.mockResponse || "";
        return new Response(body, {
          status: 200,
          statusText: "OK",
          headers: { "Content-Type": "application/json" },
        });

      case Action.Mutate:
        const response = await fetch(url, init);
        if (response.ok) {
          try {
            const data = await response.json();
            const mutatedData = this.applyMutations(data, profile.mutations || []);
            return new Response(JSON.stringify(mutatedData), {
              status: response.status,
              statusText: response.statusText,
              headers: response.headers,
            });
          } catch (e) {
            console.error(`[SDK] Cannot mutate body for profile ${profile.id}:`, e);
            return response;
          }
        }
        return response;

      case Action.Exception:
        throw new Error(profile.exceptionMessage || "Induced Exception");

      case Action.Delay:
        const delay = profile.delayMs || 1000;
        console.log(`[SDK] Inducing delay of ${delay}ms`);
        await new Promise((resolve) => setTimeout(resolve, delay));
        return fetch(url, init);

      default:
        return fetch(url, init);
    }
  }

  private applyMutations(data: any, mutations: Transformation[]): any {
    try {
      const result = { ...data };
      for (const m of mutations) {
        // Simple field replacement
        result[m.field] = m.value;
      }
      return result;
    } catch (e) {
      console.error(`[SDK] Failed to apply mutations:`, e);
      return data;
    }
  }
}
