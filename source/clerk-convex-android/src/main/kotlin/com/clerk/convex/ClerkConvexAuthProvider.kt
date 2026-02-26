package com.clerk.convex

import android.content.Context
import com.clerk.api.Clerk
import com.clerk.api.network.serialization.ClerkResult
import com.clerk.api.session.Session
import com.clerk.api.session.Session.SessionStatus
import dev.convex.android.AuthProvider
import dev.convex.android.ConvexClientWithAuth
import java.lang.ref.WeakReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * An [AuthProvider] that bridges Clerk authentication with Convex.
 *
 * Uses Clerk's session management and JWT token generation to authenticate with a Convex backend.
 * Automatically listens for session state changes and syncs Convex authentication accordingly.
 *
 * Users must first sign in using Clerk. This provider then syncs Convex authentication
 * automatically — no manual `client.login()` call is required.
 *
 * @see dev.convex.android.ConvexClientWithAuth
 */
class ClerkConvexAuthProvider : AuthProvider<String> {

  private var client: WeakReference<ConvexClientWithAuth<String>>? = null
  private lateinit var applicationContext: Context
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var sessionSyncJob: Job? = null

  /**
   * Binds a Convex client to this auth provider and starts session sync.
   *
   * This performs an initial sync and listens for Clerk session changes, calling `login()` or
   * `logout()` on the client as needed.
   *
   * Clerk must be configured before calling this method.
   */
  fun bind(client: ConvexClientWithAuth<String>, context: Context) {
    this.client = WeakReference(client)
    this.applicationContext = context.applicationContext
    startSessionSync()
  }

  override suspend fun login(context: Context): Result<String> = fetchToken()

  override suspend fun logout(context: Context): Result<Void?> {
    if (Clerk.activeSession != null) {
      when (val result = Clerk.auth.signOut()) {
        is ClerkResult.Failure -> {
          val error = result.throwable ?: Exception("Sign out failed")
          return Result.failure(error)
        }
        is ClerkResult.Success -> Unit
      }
    }
    return Result.success(null)
  }

  override fun extractIdToken(authResult: String): String = authResult

  /** Cancels session sync and releases resources. */
  fun close() {
    scope.cancel()
  }

  private suspend fun fetchToken(): Result<String> =
    when {
      !Clerk.isInitialized.value -> Result.failure(ClerkConvexAuthError.ClerkNotInitialized)
      Clerk.activeSession == null -> Result.failure(ClerkConvexAuthError.NoActiveSession)
      else ->
        when (val result = Clerk.auth.getToken()) {
          is ClerkResult.Success -> Result.success(result.value)
          is ClerkResult.Failure -> {
            val reason = result.throwable?.message ?: "Token retrieval failed"
            Result.failure(ClerkConvexAuthError.TokenRetrievalFailed(reason))
          }
        }
    }

  private fun startSessionSync() {
    sessionSyncJob?.cancel()
    sessionSyncJob =
      scope.launch {
        var previousSession: Session? = null
        Clerk.sessionFlow.collect { newSession ->
          syncSession(previousSession, newSession)
          previousSession = newSession
        }
      }
  }

  private suspend fun syncSession(oldSession: Session?, newSession: Session?) {
    val convexClient = client?.get() ?: return

    if (shouldLogin(oldSession, newSession)) {
      convexClient.login(applicationContext)
    } else if (shouldLogout(oldSession, newSession)) {
      convexClient.logout(applicationContext)
    }
  }

  companion object {
    /**
     * Returns true when we should authenticate Convex from cached Clerk credentials.
     *
     * Triggers when the session transitions from non-active to active, or when the session ID
     * changes (e.g. user switched accounts).
     */
    internal fun shouldLogin(oldSession: Session?, newSession: Session?): Boolean =
      newSession?.status == SessionStatus.ACTIVE &&
        (oldSession?.status != SessionStatus.ACTIVE || oldSession.id != newSession.id)

    /**
     * Returns true when we should clear Convex auth due to session removal.
     *
     * Triggers when an existing session transitions to null.
     */
    internal fun shouldLogout(oldSession: Session?, newSession: Session?): Boolean =
      oldSession?.id != null && newSession == null
  }
}
