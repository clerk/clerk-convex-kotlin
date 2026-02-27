# Clerk-Convex-Kotlin

`clerk-convex-kotlin` is an Android library that bridges Clerk and Convex by automatically syncing Clerk session auth into `ConvexClientWithAuth`.

## Getting Started

If you haven't started a Convex app yet, begin with the
[Convex Android quickstart](https://docs.convex.dev/quickstart/android) to get a working
Android app connected to Convex.

Once you have a working Convex + Android app, use the steps below to integrate Clerk.
Follow the [Clerk Android quickstart](https://clerk.com/docs/android/getting-started/quickstart) for app-side Clerk setup details.

1. Set up Clerk in your Android app (create an app in Clerk, get your publishable key, and add Clerk SDK dependencies).
2. Configure Convex auth by creating `convex/auth.config.ts`:

    ```typescript
    export default {
      providers: [
        {
          domain: "YOUR_CLERK_FRONTEND_API_URL",
          applicationID: "convex",
        },
      ],
    };
    ```

3. Run `npx convex dev` to sync backend auth configuration.
4. Add `clerk-convex-kotlin` to your app:

    ```kotlin
    // build.gradle.kts
    dependencies {
        implementation("com.clerk:clerk-convex-kotlin:<latest-version>")
    }
    ```

5. Wherever you currently create `ConvexClient`, switch to the provided Clerk helper factory.

    ```kotlin
    import com.clerk.api.Clerk
    import com.clerk.convex.createClerkConvexClient
    
    Clerk.initialize(
        context = applicationContext,
        publishableKey = "YOUR_CLERK_PUBLISHABLE_KEY",
    )
    
    val client = createClerkConvexClient(
        deploymentUrl = "YOUR_CONVEX_DEPLOYMENT_URL",
        context = applicationContext
    )
    ```
