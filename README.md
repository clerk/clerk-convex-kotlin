# ClerkConvex

**Status:** 🚧 Work in Progress — contents are unstable and subject to change.

`ClerkConvex` is an Android library that bridges Clerk and Convex by automatically syncing Clerk session auth into `ConvexClientWithAuth`.

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
      domain: "YOUR_CLERK_ISSUER_URL",
      applicationID: "convex",
    },
  ],
};
```

3. Run `npx convex dev` to sync backend auth configuration.
4. Add `ClerkConvex` to your app:

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.clerk:clerk-convex:1.0")
}
```

5. Wherever you currently create `ConvexClient`, switch to `ClerkConvexClient`:

```kotlin
import com.clerk.api.Clerk
import com.clerk.convex.ClerkConvexClient

Clerk.configure(publishableKey = "YOUR_CLERK_PUBLISHABLE_KEY")

val clerkConvex = ClerkConvexClient(
    deploymentUrl = "YOUR_CONVEX_DEPLOYMENT_URL",
    context = applicationContext,
)
```

6. Authenticate users via Clerk; auth state is automatically synced to Convex.

### Reacting to authentication state

The `ConvexClientWithAuth.authState` field is a `StateFlow` that contains the latest authentication state from the client. You can collect auth state values and show the appropriate screens (e.g. login/logout buttons, loading screens, authenticated content).

```kotlin
clerkConvex.convex.authState.collect { state ->
    when (state) {
        is AuthState.Authenticated -> { /* signed in */ }
        is AuthState.Unauthenticated -> { /* signed out */ }
        is AuthState.AuthLoading -> { /* loading */ }
    }
}
```

## Publishing

Publishing is handled by `.github/workflows/publish.yml` and the `com.vanniktech.maven.publish` plugin configured in `source/clerk-convex-android/build.gradle.kts`.

One-time setup:

1. Create and verify the `com.clerk` namespace in Maven Central Portal.
2. Create a Maven Central user token (username + password).
3. Create an ASCII-armored GPG private key for artifact signing.
4. Add these GitHub repository secrets:
   - `MAVEN_CENTRAL_USERNAME`
   - `MAVEN_CENTRAL_PASSWORD`
   - `MAVEN_SIGNING_KEY`
   - `MAVEN_SIGNING_PASSWORD`
   - `MAVEN_SIGNING_KEY_ID` (optional)

Release flow:

1. Bump `CLERK_CONVEX_VERSION` in `gradle.properties`.
2. Create a release tag using `vX.Y.Z` (example: `v1.2.3`) that matches `CLERK_CONVEX_VERSION`.
3. Publish a GitHub Release for that tag.
4. The publish workflow runs:
   - `:source:clerk-convex-android:publishAndReleaseToMavenCentral`

SDK version configuration:

1. `CLERK_CONVEX_VERSION` in `gradle.properties` controls the published `com.clerk:clerk-convex` version.

Manual publishing:

1. Run the `Publish` workflow from Actions (`workflow_dispatch`).
