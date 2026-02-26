# ClerkConvexKotlin

`clerk-convex-kotlin` is an Android library that bridges Clerk and Convex by automatically syncing Clerk session auth into `ConvexClientWithAuth`.

## Getting Started

If you haven't started a Convex app yet, begin with the
[Convex Android quickstart](https://docs.convex.dev/quickstart/android) to get a working
Android app connected to Convex.

Once you have a working Convex + Android app, use the steps below to integrate Clerk.
Follow the [Clerk Android quickstart](https://clerk.com/docs/android/getting-started/quickstart) for app-side Clerk setup details.

1. Set up Clerk in your Android app (create an app in Clerk, get your publishable key, and add Clerk SDK dependencies).
2. In the Clerk Dashboard, complete the [Convex integration setup](https://dashboard.clerk.com/apps/setup/convex)
3. Configure Convex auth by creating `convex/auth.config.ts`:

```typescript
export default {
  providers: [
    {
      domain: "YOUR_CLERK_ISSUER_URL",
      applicationID: "convex",
    },
  ],
};
```

4. Run `npx convex dev` to sync backend auth configuration.
5. Add `clerk-convex-kotlin` to your app.

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.clerk:clerk-convex-kotlin:<latest-version>")
}
```

6. Wherever you currently create `ConvexClient`, switch to `ConvexClientWithAuth` and pass `ClerkConvexAuthProvider`:

```kotlin
import com.clerk.api.Clerk
import com.clerk.convex.ClerkConvexAuthProvider
import com.clerk.convex.ConvexClientWithAuth

Clerk.initialize(
  context = applicationContext,
  publishableKey = "YOUR_CLERK_PUBLISHABLE_KEY",
)

val authProvider = ClerkConvexAuthProvider()
val client = ConvexClientWithAuth(
  deploymentUrl = "YOUR_CONVEX_DEPLOYMENT_URL",
  authProvider = authProvider,
  context = applicationContext,
)
```

7. Authenticate users via Clerk; auth state is automatically synced to Convex.

### Reacting to authentication state

The `ConvexClientWithAuth.authState` field is a `StateFlow` that contains the latest authentication state from the client. You can set up your UI to react to new `authState` values and show the appropriate screens (e.g. login/logout buttons, loading screens, authenticated content).

When the provider is no longer needed, call `authProvider.close()` to stop session sync.

## Example App

This repo includes a full example app at `samples/workout-tracker`.

Open the project in Android Studio and run the `samples/workout-tracker` app configuration.
