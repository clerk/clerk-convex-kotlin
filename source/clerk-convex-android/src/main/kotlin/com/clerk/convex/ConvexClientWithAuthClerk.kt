package com.clerk.convex

import android.content.Context
import dev.convex.android.ConvexClientWithAuth

/**
 * Creates a [ConvexClientWithAuth] using [ClerkConvexAuthProvider] and starts Clerk session sync.
 *
 * Preserves Android's context requirement for login/logout calls.
 *
 * **Important:** Call `Clerk.initialize(...)` before creating this client.
 * Call [ClerkConvexAuthProvider.close] when the provider is no longer needed.
 */
@Suppress("FunctionName")
fun ConvexClientWithAuth(
  deploymentUrl: String,
  authProvider: ClerkConvexAuthProvider,
  context: Context,
): ConvexClientWithAuth<String> {
  val client = ConvexClientWithAuth<String>(deploymentUrl, authProvider)
  authProvider.bind(client, context)
  return client
}
