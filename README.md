# Clothes Management Android App

A clothes management Android application that allows users to organize their wardrobe, manage clothes in laundry, and update profile information. This app provides users with functionalities to upload, view, and remove clothes, manage their laundry, and view profile details. Built with Firebase for real-time database, storage, and authentication support.

---

## Features

- **User Authentication**: Secure login and registration using Firebase Authentication.
- **Clothes Management**:
  - **Add Clothes**: Users can add clothes by providing details such as name, category, and an image.
  - **View Clothes**: Users can view a list of all their clothes, organized by category.
  - **Remove Clothes**: Option to delete clothes from the inventory.
- **Laundry Management**:
  - **Send to Laundry**: Mark clothes as sent to laundry.
  - **View Clothes in Laundry**: View a list of clothes currently in laundry, with a total count.
  - **Take Back**: Mark clothes as retrieved from laundry.
  - **Clear All from Laundry**: Clear all clothes from laundry at once.
- **Profile Management**:
  - **Edit Profile**: Update profile information, including first name, last name, and profile picture.

---

## Screenshots
Include screenshots of the app interface here (e.g., login, add clothes, view clothes, laundry management, and profile screen).

---

## Technologies Used

- **Java**: Main programming language for Android app development.
- **Android Studio**: Integrated Development Environment (IDE) used to build the app.
- **Firebase Firestore**: NoSQL database for storing user profiles, clothes details, and laundry status.
- **Firebase Storage**: Cloud storage to save and retrieve images (e.g., profile pictures, clothes images).
- **Firebase Authentication**: For secure login and registration.
- **Picasso**: Image loading library used to load images from Firebase Storage.
- **RecyclerView**: Used for displaying lists of clothes and laundry items.

---

## Project Structure

    ClothesManager/
    ├── app/
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── java/com/example/clothesmanager/
    │   │   │   │   ├── Clothes.java               # Model for clothes items
    │   │   │   │   ├── ClothesAdapter.java        # Adapter for displaying clothes in RecyclerView
    │   │   │   │   ├── DashboardActivity.java     # Main dashboard screen with navigation options
    │   │   │   │   ├── LaundryActivity.java       # Activity for laundry management
    │   │   │   │   ├── ProfileActivity.java       # Activity for updating and viewing profile
    │   │   │   │   ├── RemoveClothesActivity.java # Activity for removing clothes from inventory
    │   │   │   │   ├── UploadClothesActivity.java # Activity for adding clothes to inventory
    │   │   │   │   ├── ViewClothesActivity.java   # Activity for viewing clothes in inventory
    │   │   │   │   ├── ViewLaundryActivity.java   # Activity for viewing and managing clothes in laundry
    │   │   │   ├── res/
    │   │   │       ├── layout/
    │   │   │       │   ├── activity_dashboard.xml        # Layout for main dashboard
    │   │   │       │   ├── activity_profile.xml          # Layout for profile screen
    │   │   │       │   ├── activity_upload_clothes.xml   # Layout for uploading clothes
    │   │   │       │   ├── activity_view_clothes.xml     # Layout for viewing clothes
    │   │   │       │   ├── activity_laundry.xml          # Layout for laundry management
    │   │   │       │   ├── activity_remove_clothes.xml   # Layout for removing clothes
    │   │   │       │   ├── activity_view_laundry.xml     # Layout for viewing clothes in laundry
    │   │   │       │   └── toolbar.xml                   # Common toolbar layout
    │   │   │       ├── values/
    │   │   │           ├── strings.xml                   # Text strings for the app
    │   │   │           └── colors.xml                    # App color definitions
    │   │   └── AndroidManifest.xml                        # Manifest file declaring app activities and permissions
    ├── build.gradle                                       # Gradle build file
    └── settings.gradle                                    # Project settings file

---

## Setup and Installation

1. **Clone the Repository**: Clone this repository to your local machine.

   ```bash
   git clone https://github.com/yourusername/clothes-management-app.git
   ```

2. **Open in Android Studio**: Launch Android Studio and open the cloned project.

3. **Add Firebase to Your Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - Add an Android app to the Firebase project by providing your app’s package name (e.g., `com.example.clothesmanager`).
   - Download the `google-services.json` file and place it in the `app` directory.
   - Add Firebase dependencies to your `build.gradle` files (app-level and project-level).

4. **Enable Firebase Services**:
   - **Authentication**: Enable Email/Password sign-in in Firebase Authentication.
   - **Firestore Database**: Set up Firestore for storing user data, clothes details, and laundry status.
   - **Storage**: Enable Firebase Storage to allow image uploads for clothes and profile pictures.

5. **Build and Run the App**:
   - Sync the project with Gradle files.
   - Build and run the app on an emulator or physical device.

---

## Usage

- **Sign Up / Log In**: New users can register with an email and password. Existing users can log in with their credentials.
- **Add Clothes**: Go to the **Add Clothes** page to upload a new clothing item with a name, category, and image.
- **View Clothes**: Browse all clothes items in the **View Clothes** section.
- **Remove Clothes**: Delete specific clothes by swiping them in the **Remove Clothes** section.
- **Laundry Management**:
  - **Send to Laundry**: Mark clothes as sent to laundry in the laundry management section.
  - **View Laundry**: Check the list of clothes currently in laundry and see the total count.
  - **Clear All from Laundry**: Use a single button to clear all clothes from laundry.
- **Profile Management**: Update personal details, including first name, last name, and profile picture, in the **Profile** section.

---

## Database Structure

- **Users Collection**: Stores user information such as first name, last name, and profile image URL.
- **Clothes Collection**: Stores each clothing item, with fields for name, category, image URL, and a boolean `inLaundry` to track laundry status.

---

## Future Enhancements

1. **Filter and Sort Options**: Add features to filter clothes by category and sort by name or date added.
2. **Offline Mode**: Enable offline mode so users can add or view clothes without an internet connection.
3. **Push Notifications**: Notify users about laundry updates or reminders.
4. **Sharing Feature**: Allow users to share their wardrobe with friends or on social media.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- **Firebase**: For providing the database, storage, and authentication services.
- **Picasso**: For image loading and caching.
- **Android Studio**: IDE used to develop and test the application.

---

## Contact

For any questions or feedback, feel free to contact [Dev Makwana](mailto:channingfisher7@gmail.com).
