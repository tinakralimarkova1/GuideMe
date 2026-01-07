# GuideMe

**An Accessible Android Learning App for Teaching Essential Smartphone Skills**

GuideMe is an Android application designed to help older adults and first-time smartphone users learn essential mobile skills through **guided, interactive simulations**.
Rather than relying on passive tutorials or videos, GuideMe provides **hands-on practice** inside simulated Phone, Camera, and Wi-Fi interfaces, supported by **step-by-step lessons, real-time feedback, and voice guidance**.

This project was developed as a **Computer Science/Data Science Capstone** for NYUSH and emphasizes accessibility, user-centered design, and scalable lesson architecture.

---

## Key Features

* **Simulated Phone, Camera, and Wi-Fi apps**
  Realistic replicas of Android interfaces that users can safely practice on without risk.

* **Step-driven lesson engine**
  Lessons guide users through actions one step at a time, enforcing correct interactions and preventing accidental errors.

* **Visual UI highlighting**
  The current required action is visually outlined to direct user attention.

* **Text-to-Speech (TTS) guidance**
  All instructions and feedback are spoken aloud to support low-vision users and reduce cognitive load.

* **Voice & text search with NLP**
  Users can ask questions like *“How do I call someone?”* and receive lesson recommendations.

* **Progress tracking & analytics**
  Completion status, time spent, error count, and attempts are stored per user.

* **Accessible UI design**
  Large buttons, high contrast, simplified layouts, and reduced interaction complexity.

---

## System Architecture Overview

GuideMe follows a **clean MVVM architecture** using modern Android libraries.

### Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose + Material 3
* **Architecture:** MVVM
* **Database:** Room (SQLite)
* **Concurrency:** Kotlin Coroutines
* **Navigation:** Jetpack Navigation Compose
* **Accessibility:** Android Text-to-Speech (TTS)
* **NLP:** TensorFlow Lite–based intent classifier
* **Speech-to-Text:** Android SpeechRecognizer

---

## Project Structure

```
com.example.guideme
│
├── MainActivity.kt          # App entry point, DB + TTS initialization
├── GuideMeRoot              # Login / main app routing
│
├── lessons/
│   ├── LessonHost.kt        # Runs lessons + overlays
│   ├── LessonViewModel.kt   # Lesson logic, validation, progress tracking
│   ├── Models.kt            # Instruction, LessonState, StepType
│   ├── DatabaseSeeder.kt   # Seeds lessons, steps, users
│   ├── LessonHighlightOverlay.kt
│
├── phone/
│   ├── PhoneNavHost.kt
│   ├── DialPadScreen.kt
│   ├── ContactsScreen.kt
│   ├── RecentsScreen.kt
│   ├── FavoritesScreen.kt
│
├── camera/
│   └── CameraScreen.kt
│
├── wifi/
│   ├── WifiNavHost.kt
│   ├── WifiHomeScreen.kt
│   ├── WifiConnectScreen.kt
│
├── NLP/
│   ├── IntentClassifier.kt
│   └── STT.kt
│
└── tts/
    └── TTS.kt
```

---

## Lesson Engine Design

Lessons are **data-driven** and stored in Room rather than hard-coded.

### Instruction Model

Each lesson consists of ordered `Instruction` steps:

```kotlin
data class Instruction(
    val stepNo: Int,
    val text: String,
    val anchorId: String?,
    val type: StepType,
    val expectedText: String? = null
)
```

### Step Types

| StepType      | Description                       |
| ------------- | --------------------------------- |
| `TapTarget`   | Tap a specific UI element         |
| `EnterText`   | Type a value (e.g., phone number) |
| `Toggle`      | Switch a setting on/off           |
| `Select`      | Choose from options               |
| `Acknowledge` | Read instruction and press OK     |

### Anchor-Based Interaction

Every interactive UI element registers an **anchorId**.
The lesson engine allows **only the correct anchor** to be interacted with during each step, preventing mistakes and confusion. In the case a user performes an incorrect action, feedback is shown and user is prompted to try again.

---

## Lesson Flow

1. User selects a lesson from the menu
2. `LessonHost` loads lesson steps from Room
3. The simulated app UI is displayed (Phone / Camera / Wi-Fi)
4. The current step is:

   * Spoken via TTS
   * Highlighted visually
5. User interaction is validated by `LessonViewModel`
6. Errors trigger immediate feedback
7. Completion metrics are saved to the database

---

## Simulated Phone App

Users can practice:

* Dialing numbers
* Calling and ending calls
* Viewing recents
* Managing contacts
* Navigating between tabs

Special handling ensures:

* Backspace is allowed during typing steps
* Only the correct key is accepted in guided typing
* Visual error flashing on incorrect taps

---

## Simulated Camera App

Users learn:

* Taking photos
* Using flash modes
* Zooming with a slider
* Switching cameras
* Accessing the gallery

The camera preview is simulated for safety while preserving real interaction patterns.

---

## Simulated Wi-Fi App

Users practice:

* Turning Wi-Fi on/off
* Identifying available networks
* Understanding signal strength
* Connecting to secured networks
* Entering passwords safely

Lesson steps can enforce **default toggle states**, such as Wi-Fi starting enabled.

---

## Search with NLP

GuideMe supports **natural language search** via:

* **Voice input** (Speech-to-Text)
* **Typed questions**

Example queries:

* “How do I connect to Wi-Fi?”
* “How do I take a picture?”
* “How do I call someone?”

If confidence is too low, the query is logged for **future lesson expansion**.

---

## User Accounts & Progress Tracking

Each user has:

* Secure login (salted password hashing)
* Per-lesson completion records
* Time spent per lesson
* Error counts
* Attempt counts
* Prerequisite enforcement

All data is stored locally using Room.

---

## Testing & Reliability

* **Monkey testing** for UI stability (up to 100k events)
* Manual usability testing with younger adults
* Validation testing with elderly users
* Iterative refinement based on feedback

---

## Getting Started

### Requirements

* Android Studio (Giraffe or newer)
* Android SDK 34+
* Emulator or physical device

### Run Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device
5. Login with demo account:

   ```
   Username: demo
   Password: password
   ```

---

## Future Improvements

* More lessons and difficulty levels
* Multilingual TTS support
* Cloud-backed progress syncing
* Adaptive lessons based on user mistakes
* Tablet-optimized layouts
* Deployment 

---

## Summary

GuideMe demonstrates how **data-driven lesson design**, **accessible UI patterns**, and **modern Android architecture** can be combined to create meaningful, inclusive educational software.
It is designed to scale, adapt, and grow alongside real user needs.

---

## Full Project Report

A comprehensive written report detailing the **motivation, user research, system architecture, lesson design, NLP pipeline, testing methodology, and evaluation results** for GuideMe is available below.

The report includes:

* Problem framing and target user analysis
* Accessibility and user-centered design decisions
* Lesson engine architecture and data model
* Simulated Phone, Camera, and Wi-Fi module design
* NLP-based lesson search and error handling
* Testing methodology (monkey testing + user testing)
* Limitations and future work

🔗 **Full GuideMe Project Report (Google Docs):**
https://docs.google.com/document/d/1R5qqgy-2udGPniY9BD_mXO-pXyM5B7DjIWUnDcjk0Yw/edit?usp=sharing


