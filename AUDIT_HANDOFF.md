# Session Handoff — Design-Fidelity Audit of `signup_page` Auth Screens

**Written:** 2026-07-15, from Mark's Mac (session hit a disk-full wall mid-audit; resuming on PC).
**Branch:** `signup_page` (3 commits ahead of `main`: `c92f2fe` foundation/fonts, `f917aab` Sign Up,
`6195f26` Forgot/Verify/Reset). Working tree was clean — committed code == audited code.

---

## ▶ RESUME PROMPT (paste this into Claude Code on the PC)

> Read `AUDIT_HANDOFF.md` at the repo root in full — it contains all context, completed findings,
> and project decisions from the previous session. Then resume the design-fidelity audit at
> **Step 4 (runtime visual audit)**: build the debug APK, run it on an emulator or attached device,
> screenshot the 4 remaining auth screens (Sign Up, Forgot Password, Verification Code, Reset
> Password), compare against the Figma frames listed in the handoff, run the light functional
> checks (Step 5), and write the findings report (Step 6) exactly as specified in the handoff's
> "Remaining work" section. This is a **report-only** audit — do not modify any source files. Do
> not commit anything; Mark authors all commits.

---

## 1. What this audit is

Verify the five auth screens committed on `signup_page` are accurate to their Figma wireframes.
Approved scope (user-confirmed): **static code-vs-Figma comparison + runtime emulator screenshots**,
**visual design + light functional checks**, **report findings only — no code changes, no commits**.

Figma file key: `aealsPNZGof4CL82nXCGr8` (VocabMaxxing App)

| Figma frame (node-id) | Page | Implementation file |
|---|---|---|
| `242-2808` | Sign In ("Welcome Back") | `app/app/src/main/kotlin/com/vocabmaxxing/app/ui/auth/AuthScreen.kt` |
| `200-30` | Sign Up ("Create An Account") | `.../ui/auth/SignUpScreen.kt` |
| `202-88` | Forgot Password | `.../ui/auth/ForgotPasswordScreen.kt` |
| `281-30` | Enter Verification Code | `.../ui/auth/VerificationCodeScreen.kt` |
| `283-1327` | Enter New Password (reset) | `.../ui/auth/ResetPasswordScreen.kt` |

Shared components: `.../ui/auth/AuthComponents.kt` (`AuthScaffold`, `PillTextField`, `PrimaryButton`,
`SignInRow`, `VerificationCodeInput`); tokens/fonts in `.../ui/theme/Theme.kt`. Navigation:
`.../MainActivity.kt` (routes `auth`/`signup`/`forgot`/`verify`/`reset` + `authFlowRoutes` guard).

Note for Figma MCP: frame `242-2808` is too large for one `get_design_context` call — use its
sublayers `242:2814` (form group) and `242:2827` (title) instead. The other four frames fetch whole.

## 2. Audit progress — COMPLETED (do not redo)

- **Step 1 — Figma ground truth: DONE.** All 5 frames re-pulled 2026-07-15; specs are
  **byte-identical to the June 2026 captures — zero design drift**. Reference values in §8.
- **Step 2 — Implemented spec extracted: DONE.** All screen files + Theme.kt + MainActivity read;
  working tree == HEAD confirmed.
- **Step 3 — Static comparison: DONE.** Full results in §3/§4.
- **Step 4 — Runtime: PARTIAL.** Debug build succeeded (`./gradlew :app:assembleDebug` from `app/`;
  APK at `app/app/build/outputs/apk/debug/app-debug.apk`). Emulator (Pixel 9 Pro AVD, 1280×2856
  @480dpi = 426.7×952dp vs 412×917 design frame) booted; app installed & launched. **Sign In screen
  captured and visually verified: faithful** — Raleway heading, Poppins labels/buttons, olive
  `#D8DFB1` CTA, white-bordered pill fields, statue/wordmark header, dome+laurels footer (dome is
  correct on THIS frame — see F1), correct strings incl. curly apostrophe in "Don’t have an
  account?". Session then died to host disk exhaustion (see §7).

## 3. Static comparison — CONFIRMED MATCHES (design ⇄ code)

Fonts: headings Raleway Medium 30sp ✅ · field labels & helper/link text Poppins Regular 16sp ✅ ·
all button text Poppins Medium 20sp ✅ · wordmark is a PNG asset (design: Inria Serif Bold,
ascending 22→44px per letter — matches the exported asset) ✅
Colors: olive `#D8DFB1` (`SignInOlive`) ✅ · button text `#000000` ✅ · field fill `#2F2417` ✅ ·
field border white ✅ · link blue `#9CB8C4` (`SignInLinkBlue`) ✅ · body/heading text white ✅
Dimensions: pill fields 66.52px→67.dp, radius 20 ✅ · CTA full-width 67.dp r20 ✅ · small Sign In/up
button 136.6×51.6→137×52.dp r20 ✅ · OTP cells 54.526→54.5.dp square, r11.684→11.7.dp, border
0.974→1.dp ✅ · statue offset(-31,55) 182×257 ✅ · wordmark h35 ✅ · dome 244:138.41 aspect ✅
Content/order: all titles, labels, button strings, field order, screen element order ✅
("Sign in" lowercase on Sign In CTA; "Sign In" title-case on other screens' small button — both
match their respective frames.)

## 4. FINDINGS (for the report; verify F1/F2 visually at runtime)

- **F1 (medium-low) — Dome/laurels rendered on all 5 screens; Figma shows it ONLY on Sign In.**
  Frame 242-2808 has node `242:4085` "building asset" (y=770); frames 200-30 / 202-88 / 281-30 /
  283-1327 have **no dome node**. `AuthScaffold` draws it unconditionally. Likely a designer
  question for Alesandra (intended chrome everywhere, or Sign-In-only?). Confirm on-device, report.
- **F2 (minor) — Field label color & position.** Figma labels are **white**, top-anchored in the
  box; code uses `SignInMutedOnDark` `#B8AFA6` with M3 floating-label behavior (vertically centered
  when unfocused, floats up on focus). Screenshot both states.
- **F3 (minor) — Vertical spacing is relative, not absolute.** Code uses `SpaceBetween` + fixed
  spacers (16/24/28dp) vs Figma's absolute Y (irregular gaps 13.5–34.8px). Deliberate
  implementation choice; judge overall rhythm from screenshots, not px.
- **F4 (note) — Bottom row position varies per frame in Figma** (y≈663 Sign In, ≈779 Sign Up,
  ≈591–664 others); code anchors it uniformly (SpaceBetween + 160.dp dome clearance).
- **F5 (note) — OTP cell gaps:** Figma irregular (7.8–10.7px, hand-placed); code uniform ~10.2dp
  via SpaceBetween. Uniform is arguably more correct; report as note.
- **F6 (trivial) — Horizontal margins:** Figma content x=19 with ~15.5 right margin (asymmetric);
  code symmetric 17dp.
- **F7 (hygiene) — `fonts_originals/` (46 unused .ttf, ~7MB) committed at repo root** in `c92f2fe`;
  flag as repo-weight cleanup candidate. Also `server/bin/` sits untracked (gitignore candidate).
- **Intentional deviations (report as NOT bugs):** single-spaced "Reset Password" (Figma has double
  space); error texts ("Passwords do not match.", auth errors, 13sp Poppins red) and
  "Processing..." loading label are additive states absent from wireframes; CTA enable/disable
  gating; OTP digit glyphs (Poppins Medium 22sp white — design shows only empty boxes); Name field
  captured but not sent to backend; entire forgot/verify/reset flow is UI-stubbed (see §6).

## 5. REMAINING WORK (the PC session's job)

**Step 4 (finish) — runtime screenshots.** Build, install, launch. Capture, comparing each against
its Figma frame (re-pull reference screenshots via Figma MCP `get_screenshot`):
1. `02_signup` — tap "Sign up" on Sign In. Also capture a validation state (mismatched passwords →
   red message + disabled CTA).
2. `03_forgot` — from Sign In: tap "Forgot your password?" link.
3. `04_verify` — from Forgot: enter any email, tap "Send Verification Code". Capture empty +
   all-6-digits states.
4. `05_reset` — from Verify: enter 6 digits, tap "Verify Code". Capture mismatch + match states.
Derive tap coordinates from a fresh screenshot each time (screen metrics differ per device).
Practical: `adb exec-out screencap -p > file.png`; launch via
`adb shell monkey -p com.vocabmaxxing.app -c android.intent.category.LAUNCHER 1`.

**Step 5 — functional checks (light).**
- Nav: Sign In → Sign up → back (popBackStack); Sign In → Forgot → Verify → Reset → "Reset
  Password" returns to Sign In (popUpTo auth); "Sign In" small button works from each recovery
  screen; unauthenticated user is NOT bounced off signup/forgot/verify/reset (`authFlowRoutes`
  guard, MainActivity.kt:29).
- Sign Up: CTA enabled only when all 4 fields filled AND passwords match.
- OTP: digits only, hard cap 6, "Verify Code" enabled only at exactly 6; backspace edits.
- Keyboards: email type on email fields, password masking on all password fields, numeric for OTP.
- Stubs: forgot/verify/reset CTAs must fire NO network/Firebase calls (watch logcat); Sign In/Sign
  Up DO call Firebase by design.

**Step 6 — findings report.** Write `auth-audit-findings.md` (suggest `docs/` in-repo so it
survives, but do NOT commit — Mark commits). Must contain: executive summary with per-screen
fidelity verdict; per-screen comparison matrix (element | Figma | code | verdict | severity);
side-by-side screenshots (Figma ref vs device); the **intentional deviations** list from §4;
prioritized fix list for true mismatches (F1, F2 lead); commit-hygiene note (F7). Report only.

## 6. Project decisions & preferences (from Mark's local memory — the PC won't have these)

- **Never auto-commit.** Stage if useful; Mark authors every commit.
- **Fonts are deliberate** (chosen by Alesandra): Raleway = headings, Poppins = body/labels/buttons,
  Inria Serif = wordmark. Do not "simplify" to one family (other app screens use Inter — that split
  is expected).
- **Auth email strategy (2026-06-24):** pilot uses Firebase's built-in `sendPasswordResetEmail`
  (link-based). The 6-digit code flow in the wireframes is **deferred post-pilot** (would need
  Resend/SMS + custom backend + Firebase Admin). Hence the Verification Code screen is built
  high-fidelity but intentionally unwired; forgot/verify/reset callbacks are UI stubs.
- **This branch is UI-only, high-fidelity to wireframes.** Backend/auth wiring is out of scope.
- Greco-Roman theme is pending a group re-decision (team merging with Schedulater) — don't treat
  the visual theme as immutable, but audit against the current frames.
- During the build phase Mark required screen-by-screen approval gates; for the audit, the approved
  plan is the 6-step flow above — no extra gates requested, but pause and ask if scope questions
  arise.

## 7. Environment lessons from the Mac session (avoid repeating)

- Emulator with default GPU wedged at `adb devices: offline` indefinitely; relaunch with
  `-gpu swiftshader_indirect` booted in 60s. On a stronger PC, hardware GPU should be fine.
- If redirecting emulator output to a log, cap it — no unbounded redirects.
- The Mac session died from **host disk exhaustion** while the Play Store AVD ran (killing qemu
  released ~17GB). Check free disk first; prefer a non-Play (AOSP) image. When the disk filled,
  every tool bricked (ENOSPC on harness output files) — if that happens, the human must free
  space; the agent cannot.
- The Sign In device screenshot + Figma reference PNGs lived in the Mac's session scratchpad —
  **they do not transfer**; re-pull from Figma on the PC.

## 8. Figma ground-truth quick reference (key values)

Frame geometry: 412×917 outer, content authored at 440×954 (compare proportionally).
Fields: x=19, w=377.462, h=66.52, r=20, fill `#2F2417`, 1px white border.
CTAs: same box, fill `#D8DFB1`, text Poppins Medium 20 black.
Small button: 136.595×51.627, r20, olive.
OTP row: 6 cells @ y=371.74, size 54.526, r11.684, border 0.974 white,
x = 19 / 84.24 / 146.55 / 209.84 / 272.16 / 334.47.
Statue: x=-31 y=55 182×257. Wordmark: x=157 y=135 h=35, Inria Serif Bold 22→44px, `#2F2417`.
Headings: Raleway Medium 30 white — "Welcome Back" y297 · "Create An Account" y283 ·
"Forgot Password?" y298 · "Enter Verification Code" y298 · "Enter New Password" y298.
Dome ("building asset"): **Sign In frame only**, x=86 y=770 244×138.4.
Per-screen field Y positions (px): Sign In — email 343.9, password 429.5, CTA 515.1, link 601,
bottom row 663–714. Sign Up — name 329, email 409, password 491.6, confirm 580.1, CTA 662, bottom
row 779–830. Forgot — email 367, CTA 461, row 594–645. Verify — OTP 371.74, CTA 461, row 591–642.
Reset — new pw 367, confirm 450 (x=18), CTA 536 ("Reset  Password" double-space in design), row
664–715.
