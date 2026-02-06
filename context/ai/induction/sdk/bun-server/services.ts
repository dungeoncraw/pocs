import { Profile } from "./models";
import { join } from "path";

export interface ProfileFetcher {
  fetch(profileId: string): Promise<Profile | undefined>;
}

export class HttpProfileFetcher implements ProfileFetcher {
  constructor(private serverUrl: string) {}

  async fetch(profileId: string): Promise<Profile | undefined> {
    try {
      const response = await fetch(`${this.serverUrl}/profiles/${profileId}`);
      if (response.ok) {
        return (await response.json()) as Profile;
      }
    } catch (e) {
      console.error(`[HttpProfileFetcher] Error fetching profile ${profileId}:`, e);
    }
    return undefined;
  }
}

export class FileProfileFetcher implements ProfileFetcher {
  constructor(private basePath: string) {}

  async fetch(profileId: string): Promise<Profile | undefined> {
    const filePath = join(this.basePath, `${profileId}.json`);
    const file = Bun.file(filePath);
    if (await file.exists()) {
      return (await file.json()) as Profile;
    }
    return undefined;
  }
}

export class CompositeProfileFetcher implements ProfileFetcher {
  constructor(private fetchers: ProfileFetcher[]) {}

  async fetch(profileId: string): Promise<Profile | undefined> {
    for (const fetcher of this.fetchers) {
      const profile = await fetcher.fetch(profileId);
      if (profile) {
        return profile;
      }
    }
    return undefined;
  }
}

export const Environment = {
  isProduction: () => process.env.APP_ENV === "production" || process.env.APP_ENV === "prod",
};
