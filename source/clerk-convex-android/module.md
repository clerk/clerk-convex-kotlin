# Module Clerk Convex Kotlin

Clerk Convex Kotlin bridges Clerk Android authentication with Convex mobile clients.

Use `createClerkConvexClient(deploymentUrl, context)` to create a `ConvexClientWithAuth` with Clerk-backed auth token refresh and automatic auth-state synchronization. If you need to provide your own provider instance, use `ClerkConvexAuthProvider.createConvexClientWithAuth(deploymentUrl, context)`.
