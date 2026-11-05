# Across The Internet

An offline-first Android application designed for bookmarking, metadata extraction, and full-text searching of web content. Across The Internet intercepts shared links from system applications such as Chrome, YouTube, X (Twitter), Reddit, GitHub, Instagram, and Medium storing them in a local SQLite database with full-text search indexing and offline availability.

---

## Video
https://drive.google.com/file/d/1nkOlCca1mXj-ugKMWB3cnK4z1MA-NWrw/view?usp=sharing

## Technical Overview

Across The Internet uses Modern Android Architecture guidelines following the Unidirectional Data Flow (UDF) pattern. Shared URLs are processed through a two-stage pipeline: an immediate synchronous database write for zero-latency UI updates, followed by an asynchronous background job that extracts OpenGraph and HTML metadata.

### Architecture Components

* **UI Layer**: Built entirely with Jetpack Compose and Material 3. Layouts adaptively scale between standard smartphones, compact foldables, unfolded dual-pane displays, and tablets.
* **State Management**: `MainViewModel` exposes reactive `StateFlow` streams. UI events flow up while updated state flows down.
* **Repository Layer**: `SavedItemRepository` acts as the single source of truth mediating between local Room storage, WorkManager background execution, and user preference data.
* **Persistence Layer**: Room Database 2.6+ integrated with SQLite FTS4 virtual tables (`saved_items_fts`) for sub-millisecond offline full-text queries.
* **Background Processing**: WorkManager 2.9+ schedules background metadata extraction (`ScraperWorker`) with network constraints.
* **Metadata Scraping**: Jsoup parses OpenGraph tags (`og:title`, `og:image`, `og:description`), Twitter Cards, and semantic HTML elements.

---

## Features

* **System Share Sheet Integration**: `ShareActivity` registers as a system-wide intent target for `ACTION_SEND` and `ACTION_VIEW` actions across text, plain links, and clip data payloads.
* **Multi-Link Detection**: Automatically parses space-, line-, or comma-separated URL payloads, splitting them into individual, independent bookmark entries.
* **SQLite FTS4 Full-Text Search**: Inverted B-tree search indexing over titles, descriptions, URLs, tags, and domain names with automatic fallback to substring `LIKE` queries.
* **Automated Taxonomy Tagging**: `AutoTagger` scans domain patterns and text corpora to generate domain-specific and keyword-based taxonomy tags (`#code`, `#video`, `#social`, `#chrome`, `#research`, `#tech`).
* **Adaptive Screen Support**: Dual-pane layouts for foldables and large screens alongside fluid, scrollable compact layouts for flip phones and smaller devices.
* **Data Safety Controls**: Explicit modal prompts confirm single item deletions and full database wipes before executing operations.
* **Interactive Feature Walkthrough**: Guided step-by-step feature walkthrough explaining application controls, platform filter categories, and search capabilities.

---

## Tech Stack & Dependencies

| Layer | Component | Version / Description |
| :--- | :--- | :--- |
| **Language** | Kotlin | 2.2 / Coroutines, `StateFlow`, `Flow`, `Dispatchers.IO` |
| **UI Framework** | Jetpack Compose | Material 3, Material Icons Extended |
| **Database** | Room | 2.6+ with SQLite FTS4 extension (`@Fts4`) |
| **Async Jobs** | WorkManager | 2.9+ with network constraints (`NetworkType.CONNECTED`) |
| **Parser** | Jsoup | HTML DOM and OpenGraph metadata parsing |
| **Image Pipeline** | Coil Compose | `AsyncImage` with memory and disk caching |
| **Testing** | JUnit 4 & Robolectric | Local unit testing for database DAOs, FTS queries, and auto-tagging |

---

## Project Structure

```
app/src/main/java/com/kax/across/
├── MainActivity.kt                  # Entry point activity initializing Compose theme and ViewModel
├── ShareActivity.kt                 # Interceptor activity receiving external share sheet intents
├── OmniSaveApplication.kt           # Application subclass managing repository singletons
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt           # Room Database definition and sample data callback
│   │   ├── SavedItem.kt             # Primary core entity model for bookmarks
│   │   ├── SavedItemDao.kt          # DAO exposing Flow queries, FTS match, and CRUD
│   │   └── SavedItemFts.kt          # Room FTS4 virtual table entity definition
│   └── repository/
│       └── SavedItemRepository.kt   # Repository managing Room operations and WorkManager
├── ui/
│   ├── HomeScreen.kt                # Main screen container and adaptive layout switcher
│   ├── MainViewModel.kt             # ViewModel processing filters, searches, and overlay states
│   ├── components/
│   │   ├── AppTutorialDialog.kt     # Multi-step onboarding and feature guide dialog
│   │   ├── DashboardWidgets.kt      # Hero card, category tiles, and slide action bar
│   │   ├── DeleteConfirmationDialog.kt # Confirmation prompts for single and bulk deletion
│   │   ├── ItemDetailDialog.kt      # Link metadata inspector dialog
│   │   ├── ManualUrlDialog.kt       # Manual single and multi-link entry dialog
│   │   └── SavedItemCard.kt         # OpenGraph preview card with action controls
│   └── theme/                       # Color scheme, typography, and shape definitions
├── util/
│   └── AutoTagger.kt                # Domain scanning and taxonomy hashtag generation
└── worker/
    └── ScraperWorker.kt             # WorkManager worker for HTTP scraping via Jsoup
```

---

## Building and Running

### Prerequisites

* Android Studio Ladybug (2024.2.1) or newer
* JDK 17
* Android SDK 37 (compileSdk 37, targetSdk 37, minSdk 24)

### Commands

1. **Clone the repository**:
   ```bash
   git clone https://github.com/s-k-0-0/Across.git
   cd Across
   ```

2. **Run Unit Tests**:
   ```bash
   ./gradlew app:testDebugUnitTest
   ```

3. **Build Debug APK**:
   ```bash
   ./gradlew app:assembleDebug
   ```

---

## License

