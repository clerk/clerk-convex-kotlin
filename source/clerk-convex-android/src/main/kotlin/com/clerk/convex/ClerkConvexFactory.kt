package com.clerk.convex

import android.content.Context

/**
 * Creates a [dev.convex.android.ConvexClientWithAuth] configured with Clerk authentication and
 * session sync.
 *
 * This mirrors a convenience initializer and is the preferred entry point for creating a
 * Clerk-backed Convex client.
 *
 * **Important:** Call `Clerk.initialize(...)` before creating this client.
 */
@Suppress("FunctionName")
fun ConvexClientWithAuth(
  deploymentUrl: String,
  authProvider: ClerkConvexAuthProvider,
  context: Context,
): dev.convex.android.ConvexClientWithAuth<String> =
  dev.convex.android.ConvexClientWithAuth(deploymentUrl, authProvider).also { client ->
    authProvider.bind(client, context)
  }
