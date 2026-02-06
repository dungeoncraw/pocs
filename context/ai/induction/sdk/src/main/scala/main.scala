
import com.induction.sdk.*
import sttp.client3.*

@main
def main(): Unit = {
  println("--- Induction SDK PoC ---")

  // 1. Setup Composite Profile Fetcher
  val profileServerUrl = sys.env.getOrElse("PROFILE_SERVER_URL", "http://localhost:3000")
  println(s"Using Profile Server at: $profileServerUrl")
  
  val httpFetcher = new HttpProfileFetcher(profileServerUrl)
  val fileFetcher = new FileProfileFetcher("./profiles")
  val fetcher = new CompositeProfileFetcher(List(fileFetcher, httpFetcher))

  // 2. Initialize SDK
  val sdk = new InductionSDK(fetcher)

  // 3. Demonstrate different Variations

  // 3.1 Mock Whole (from Rust API)
  testProfile(sdk, "user-not-paid-profile", "https://api.example.com/user/status")

  // 3.2 Exception (from Rust API)
  testProfile(sdk, "rust-exception-profile", "https://api.example.com/critical")

  // 3.3 Delay (from Local JSON File)
  val start = System.currentTimeMillis()
  testProfile(sdk, "slow-api-profile", "https://api.example.com/slow")
  val duration = System.currentTimeMillis() - start
  println(s"Request took: ${duration}ms")

  // 3.4 Mutate (from Local JSON File)
  println("\n--- Testing Mutation ---")
  val requestMutate = basicRequest
    .get(uri"https://httpbin.org/json") 
    .header("X-Induction-Profile-ID", "mutation-profile")
  
  try {
    val response = sdk.instrumentRequest(requestMutate)
    println(s"Mutated Response Body: ${response.body}")
  } catch {
    case e: Exception => println(s"Mutation test failed: ${e.getMessage}")
  }

  // 4. Real call without profile
  println("\nRequesting without profile (Real Call simulation):")
  val request2 = basicRequest
    .get(uri"https://google.com")

  try {
    val response2 = sdk.instrumentRequest(request2)
    println(s"Real Response Code: ${response2.code}")
  } catch {
    case e: Exception => println(s"Real call failed: ${e.getMessage}")
  }
}

def testProfile(sdk: InductionSDK, profileId: String, url: String): Unit = {
  println(s"\n>>> Testing profile: $profileId")
  val request = basicRequest
    .get(uri"$url")
    .header("X-Induction-Profile-ID", profileId)

  try {
    val response = sdk.instrumentRequest(request)
    println(s"Response Code: ${response.code}")
    println(s"Response Body: ${response.body}")
  } catch {
    case e: Exception => println(s"Induced Exception: ${e.getMessage}")
  }
}

