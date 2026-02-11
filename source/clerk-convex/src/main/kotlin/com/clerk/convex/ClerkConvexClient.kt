package com.clerk.convex

import android.content.Context
import dev.convex.android.ConvexClientWithAuth
import java.io.Closeable

/**
 * A Convex client that automatically authenticates using Clerk.
 *
 * Creates a [ConvexClientWithAuth] bound to a [ClerkConvexAuthProvider] that syncs Clerk session
 * state to Convex. When a user signs in or out via Clerk, the Convex client's auth state updates
 * automatically.
 *
 * Usage:
 * ```kotlin
 * // In Application.onCreate() or a singleton
 * val clerkConvex = ClerkConvexClient(
 *     deploymentUrl = "https://your-deployment.convex.cloud",
 *     context = applicationContext,
 * )
 *
 * // Use the underlying Convex client for queries, mutations, etc.
 * clerkConvex.convex.subscribe<List<MyData>>("myQuery", args)
 *
 * // Observe auth state
 * clerkConvex.convex.authState.collect { state -> ... }
 * ```
 *
 * Call [close] to stop session sync and release resources.
 *
 * **Important:** Call `Clerk.configure(...)` before creating this client.
 */
class ClerkConvexClient(deploymentUrl: String, context: Context) : Closeable {

  private val authProvider = ClerkConvexAuthProvider()

  /** The underlying Convex client. Use this for queries, mutations, actions, and auth state. */
  val convex: ConvexClientWithAuth<String> = ConvexClientWithAuth(deploymentUrl, authProvider)

  init {
    authProvider.bind(convex, context)
  }

  override fun close() {
    authProvider.close()
  }
}
