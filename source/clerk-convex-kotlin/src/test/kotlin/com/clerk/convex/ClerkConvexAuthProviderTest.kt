package com.clerk.convex

import com.clerk.api.session.Session
import com.clerk.api.session.Session.SessionStatus
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClerkConvexAuthProviderTest {

  // -- shouldLogin --

  @Test
  fun `shouldLogin returns true when transitioning from no session to active`() {
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertTrue(ClerkConvexAuthProvider.shouldLogin(oldSession = null, newSession = newSession))
  }

  @Test
  fun `shouldLogin returns true when transitioning from expired to active`() {
    val oldSession = mockSession(status = SessionStatus.EXPIRED, id = "sess_1")
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertTrue(ClerkConvexAuthProvider.shouldLogin(oldSession, newSession))
  }

  @Test
  fun `shouldLogin returns true when session ID changes`() {
    val oldSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_2")
    assertTrue(ClerkConvexAuthProvider.shouldLogin(oldSession, newSession))
  }

  @Test
  fun `shouldLogin returns false when session remains active with same ID`() {
    val oldSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertFalse(ClerkConvexAuthProvider.shouldLogin(oldSession, newSession))
  }

  @Test
  fun `shouldLogin returns false when new session is null`() {
    val oldSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertFalse(ClerkConvexAuthProvider.shouldLogin(oldSession, newSession = null))
  }

  @Test
  fun `shouldLogin returns false when new session is not active`() {
    val newSession = mockSession(status = SessionStatus.EXPIRED, id = "sess_1")
    assertFalse(ClerkConvexAuthProvider.shouldLogin(oldSession = null, newSession = newSession))
  }

  @Test
  fun `shouldLogin returns false when new session is pending`() {
    val newSession = mockSession(status = SessionStatus.PENDING, id = "sess_1")
    assertFalse(ClerkConvexAuthProvider.shouldLogin(oldSession = null, newSession = newSession))
  }

  // -- shouldLogout --

  @Test
  fun `shouldLogout returns true when session transitions from existing to null`() {
    val oldSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertTrue(ClerkConvexAuthProvider.shouldLogout(oldSession, newSession = null))
  }

  @Test
  fun `shouldLogout returns false when both sessions are null`() {
    assertFalse(ClerkConvexAuthProvider.shouldLogout(oldSession = null, newSession = null))
  }

  @Test
  fun `shouldLogout returns false when new session exists`() {
    val oldSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertFalse(ClerkConvexAuthProvider.shouldLogout(oldSession, newSession))
  }

  @Test
  fun `shouldLogout returns false when session is replaced`() {
    val oldSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_2")
    assertFalse(ClerkConvexAuthProvider.shouldLogout(oldSession, newSession))
  }

  @Test
  fun `shouldLogout returns false when old session is null`() {
    val newSession = mockSession(status = SessionStatus.ACTIVE, id = "sess_1")
    assertFalse(ClerkConvexAuthProvider.shouldLogout(oldSession = null, newSession = newSession))
  }

  // -- ClerkConvexAuthError --

  @Test
  fun `ClerkNotInitialized has descriptive message`() {
    val error = ClerkConvexAuthError.ClerkNotInitialized
    assertTrue(error.message!!.contains("initializing"))
  }

  @Test
  fun `NoActiveSession has descriptive message`() {
    val error = ClerkConvexAuthError.NoActiveSession
    assertTrue(error.message!!.contains("sign in"))
  }

  @Test
  fun `TokenRetrievalFailed carries reason`() {
    val error = ClerkConvexAuthError.TokenRetrievalFailed("network timeout")
    assertTrue(error.message!!.contains("network timeout"))
  }

  private fun mockSession(status: SessionStatus, id: String): Session {
    val session = mockk<Session>()
    io.mockk.every { session.status } returns status
    io.mockk.every { session.id } returns id
    return session
  }
}
