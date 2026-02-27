package com.clerk.convex

import android.content.Context
import dev.convex.android.ConvexClientWithAuth

/**
 * Creates a [dev.convex.android.ConvexClientWithAuth] configured with [ClerkConvexAuthProvider] and
 * session sync.
 *
 * This is the preferred entry point for creating a Clerk-backed Convex client.
 *
 * **Important:** Call `Clerk.initialize(...)` before creating this client.
 */
fun createClerkConvexClient(deploymentUrl: String, context: Context): ConvexClientWithAuth<String> =
  ClerkConvexAuthProvider().createConvexClientWithAuth(deploymentUrl, context)

/**
 * Creates a [dev.convex.android.ConvexClientWithAuth] using this provider and starts Clerk session
 * synchronization.
 */
fun ClerkConvexAuthProvider.createConvexClientWithAuth(
  deploymentUrl: String,
  context: Context,
): ConvexClientWithAuth<String> =
  ConvexClientWithAuth(deploymentUrl, this).also { client -> bind(client, context) }
