package com.clerk.convex

/** Error types for Clerk-Convex authentication. */
sealed class ClerkConvexAuthError(message: String) : Exception(message) {

  /**
   * Clerk has not finished initializing. Wait for [com.clerk.api.Clerk.isInitialized] to be true.
   */
  data object ClerkNotInitialized :
    ClerkConvexAuthError(
      "Clerk has not finished initializing. Ensure Clerk.isInitialized is true before authenticating."
    )

  /** No active Clerk session exists. The user must sign in first. */
  data object NoActiveSession :
    ClerkConvexAuthError("No active Clerk session. Please sign in first using Clerk.")

  /** Failed to retrieve a JWT token from the Clerk session. */
  data class TokenRetrievalFailed(val reason: String) : ClerkConvexAuthError(reason)
}
