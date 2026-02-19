# WorkoutTracker Example

Example Android app for `ClerkConvex` using Clerk authentication with Convex.

## Setup

1. Open this repository in Android Studio.
2. Set your Clerk publishable key and Convex deployment URL in your Gradle properties (for example in `~/.gradle/gradle.properties`):
   - `WORKOUT_CLERK_PUBLISHABLE_KEY=YOUR_CLERK_PUBLISHABLE_KEY`
   - `WORKOUT_CONVEX_DEPLOYMENT_URL=YOUR_CONVEX_DEPLOYMENT_URL`
3. In `samples/workout-tracker/convex/auth.config.ts`, set your Clerk issuer URL.
4. Run `npm install` and `npx convex dev` from `samples/workout-tracker`.
5. Build and run the `samples:workout-tracker` app.
