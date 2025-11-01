## Dear_Diary

Dear_Diary is a simple and user-friendly Diary and Task Management Android application designed to help users maintain daily journal entries while also managing their daily tasks. The app allows users to record thoughts, add custom tags, search logs by tags, track time-based activities, and view past logs in an organized manner.

### Features

#### Daily Diary Logging
- Add daily diary entries with a title and detailed description.
- Automatically stores diary entries date-wise.
- View past diary entries at any time.
- Clean and minimal writing interface.

#### Custom Tags for Diary Entries
- Users can add custom tags for each diary entry.
- Tags help in categorizing and organizing diary logs.
- Users can define tags based on mood, event, activity, or any personal category.

#### Search Diary Entries by Tags
- Allows searching of diary entries based on exact tag matches.
- Users can enter a tag name to filter and view only the entries that contain that tag.
- Helps users quickly locate past entries related to specific categories.

#### Daily Task Management
- Add multiple tasks for the day with the following details:
  - Task name
  - Start time
  - End time
- Tasks are saved day-wise for effective time tracking.
- Helps users plan and review daily routines.

#### Daily Data Storage and Reset
- Diary entries and tasks are saved date-wise in a local SQLite database.
- Each new day starts with a fresh space for new tasks and diary entry.
- Previous day data remains saved and accessible.

#### View Past Logs
- View diary entries and tasks from previous days.
- Useful for self-reflection, progress tracking, and habit monitoring.

#### Input Validation and User Experience
- Ensures correct time format entry when adding tasks.
- Simple and smooth navigation between diary, task, and log screens.

### Tech Stack

| Component             | Technology Used          |
|-----------------------|---------------------------|
| Programming Language  | Java                      |
| IDE                   | Android Studio            |
| Database              | SQLite                    |
| UI Design             | XML Layouts, Material Components |
| Architecture          | Activity-based Structure  |

### Project Structure

```
Dear_Diary/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/diary/
│   │   │   │   ├── MainActivity.java               # Home screen
│   │   │   │   ├── AddTaskActivity.java            # Add a new task
│   │   │   │   ├── DiaryDetailsActivity.java       # Add/view diary entry with tags
│   │   │   │   ├── ViewPastLogs.java               # View previous logs
│   │   │   │   ├── DiaryDatabaseHelper.java        # SQLite database operations
│   │   │   ├── res/
│   │   │   │   ├── layout/                         # XML layout files
│   │   │   │   ├── values/                         # Themes, styles and strings
│   │   ├── ...
│   ├── build.gradle
│
├── gradle/
├── .gitignore
└── README.md
```

### How to Run the Project

1. Clone the repository

```
git clone https://github.com/your-username/Dear_Diary.git
```

2. Open the project in Android Studio
- Open Android Studio
- Go to File > Open
- Select the project folder
- Wait for Gradle to sync completely

3. Run the application
- Connect an Android device or start an emulator
- Click the Run button to build and launch the app
