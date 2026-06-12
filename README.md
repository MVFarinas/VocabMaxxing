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

## Decisions:
- PostGreSql
- Android Studio
- Kotlin
 
- User Stories:

## Infrastructure:
- Backend: Ktor server deployed on Railway
- Database: PostgreSQL hosted on Supabase (accessed directly via JDBC, not Supabase's API/Auth/Storage)
- AI Scoring: Groq (Llama 3.3 70B) via OpenAI-compatible chat completions endpoint
