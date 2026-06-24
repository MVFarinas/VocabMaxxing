# Origin
- Initiated during HackED2026... continued April 2026

## To Do:
- Create User Stories
- Refine features desired for MVP
- Assign roles and tasks

- Wireframe + Build Front End
- Backend 
- Test Cases

- Consider Business Model
- Consider Scalability to Educational Institutions (consumer facing vs institution facing)

### Outstanding tasks:
- [ ] Finalize mascot art; surface user info (streak and level) on the Homepage
- [ ] Create page for recent performances (7-day span)
- [ ] Update Performance Dashboard
- [ ] Create Sign-up / Forgot Password page
- [ ] Search grant programs for API credits
- [ ] Finalize Supabase GitHub integration
- [ ] Once testing is done: remove word respawn, enforce 3/day max (see "Before Pilot" below)

## To Do — Before Pilot (deferred during testing phase):
These were intentionally relaxed for the testing phase and must be restored before
piloting with real users:
- [ ] Re-enforce the 3 words/day rule server-side (re-add the daily attempt cap in
      `AttemptRoutes` — max 3 attempts/day, each word once)
- [ ] Remove word respawn: once a category's word is completed, do not spawn a new
      word in that category (slot shows as completed; user picks from the remaining
      words). Requires persisting per-user daily word assignments.
- [ ] Tighten the evaluate rate limit (currently 20/min for testing → lower for pilot)

## Decisions:
- PostGreSql
- Android Studio
- Kotlin
 
- User Stories:

## Infrastructure:
- Backend: Ktor server deployed on Railway
- Database: PostgreSQL hosted on Supabase (accessed directly via JDBC, not Supabase's API/Auth/Storage)
- AI Scoring: Groq (Llama 3.3 70B) via OpenAI-compatible chat completions endpoint

## Project structure:
This repo holds two independent, self-contained Gradle projects. There is no umbrella
Gradle build at the repo root — **do not open the repo root as a Gradle project.**
- `app/` — the Android app (frontend). Open **this folder** directly in Android Studio.
  Android Studio generates the machine-specific `app/local.properties` (`sdk.dir`) on first
  sync; it is gitignored. Set `API_BASE_URL` there to point at a real backend (defaults to
  the emulator loopback `http://10.0.2.2:8080`).
- `server/` — the Ktor backend. Built/deployed by Railway via Docker; no Android SDK needed.
  Open separately if you need to work on it locally.
