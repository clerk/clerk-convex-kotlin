# clerk-convex-kotlin

Android library that bridges [Clerk](https://clerk.com) authentication with [Convex](https://convex.dev) backends.

Implements the Convex `AuthProvider` interface and automatically syncs Clerk session state to Convex — when a user signs in or out via Clerk, the Convex client's auth state updates automatically.

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.clerk:clerk-convex:1.0")
}
```

## Usage

```kotlin
// 1. Configure Clerk (typically in Application.onCreate)
Clerk.configure(publishableKey = "pk_test_...")

// 2. Create the client
val clerkConvex = ClerkConvexClient(
    deploymentUrl = "https://your-deployment.convex.cloud",
    context = applicationContext,
)

// 3. Use the Convex client for queries, mutations, etc.
clerkConvex.convex.subscribe<List<MyData>>("myQuery", args)

// 4. Observe auth state
clerkConvex.convex.authState.collect { state ->
    when (state) {
        is AuthState.Authenticated -> { /* signed in */ }
        is AuthState.Unauthenticated -> { /* signed out */ }
        is AuthState.AuthLoading -> { /* loading */ }
    }
}

// 5. Clean up when done
clerkConvex.close()
```

## How It Works

1. User signs in via Clerk UI components
2. `ClerkConvexAuthProvider` detects the session change via `Clerk.sessionFlow`
3. Automatically calls `loginFromCache()` on the Convex client
4. Fetches a JWT from Clerk and passes it to Convex for backend authentication
5. When the user signs out, Convex auth is cleared automatically

## License

MIT
