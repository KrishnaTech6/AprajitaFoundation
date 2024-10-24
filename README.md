# Aprajita Foundation Android App

This Android application is developed for the **Aprajita Foundation**, aimed at empowering women and children. The app is designed with user-friendly features for both users and administrators, providing access to various sections such as the gallery, events, member information, and donation options.


## App Screenshots

<p align="center">
    <img src="https://github.com/user-attachments/assets/164bb127-efd7-479f-a9a9-cf9afe3ced25" width="200" />
    <img src="https://github.com/user-attachments/assets/de30d433-0a29-4a8d-807b-0042d673848c" width="200" />
    <img src="https://github.com/user-attachments/assets/3746c297-5341-4c57-a620-0fbc7b53a62b" width="200" />
   b<img src="https://github.com/user-attachments/assets/ef03e9f9-ae15-4780-8bd7-9d243737aabe" width="200" />
</p>

<p align="center">
    <img src="https://github.com/user-attachments/assets/db5e6103-861f-4fed-b831-9f0ca32c6ef4" width="200" />
    <img src="https://github.com/user-attachments/assets/9fe8e1ff-f919-4002-8ed1-9a35f95291cc" width="200" />
    <img src="https://github.com/user-attachments/assets/5c8095a1-a017-4829-a09e-41a8f0a66df9" width="200" />
    <img src="https://github.com/user-attachments/assets/00f95e89-c085-4d70-9402-f05e86b22cde" width="200" />
</p>

<p align="center">
<img src="https://github.com/user-attachments/assets/1d466df8-17db-4c21-909b-e820344ac569" width="200" />
<img src="https://github.com/user-attachments/assets/30cd3ad6-1ab5-41d8-99b8-fae1b6c9a1f4" width="200" />

<img src="https://github.com/user-attachments/assets/3ec21ee7-4065-4eff-821c-983688b8a942" width="200" />
  <img src="https://github.com/user-attachments/assets/42d6cae8-5f56-41b3-9009-02f7cac6b667" width="200" />
  
</p>

## Key Features

### User Features
- **Gallery**: Users can seamlessly view, browsing through photos shared by the foundation.
- **Razorpay Integration**: A secure and easy-to-use payment gateway for donations, with the ability to download and share receipts after successful payments.
- **Profile Section**:
  - Users can contact the foundation via WhatsApp, email, or other methods.
  - Share the app's APK with others.
  - Sync profile information with Firebase Google login.
- **Dark Mode/Light Mode**: Toggle between dark and light themes according to user preferences.
- **Bottom Navigation**: Intuitive navigation for users, making app exploration smooth and easy.

### Admin Features
- **Admin Dashboard**: Admins can manage gallery images, events, and member information:
  - Upload, update, and delete gallery images.
  - Create, edit, or remove events.
  - Manage member profiles.
- **Multiple Image Upload**: Users can select and upload multiple images to the server in one go.
- **Cloudinary Integration**: Cloud storage solution for saving images, including error handling for any Cloudinary dependency issues (documented on StackOverflow).
- **Server Calls with Retrofit**: Smooth network communication handled through the Retrofit library.
- **Receipt Management**: Admins can convert payment details into table format and generate PDF receipts using the IText library.
- **Drawer Layout**: A dedicated layout for admins to efficiently manage app content.

### Additional Features
- **Firebase Google Login**: Integrated Google authentication for users, while admins log in using a server-based authentication system.
- **Multiple Image Selection**: Users and admins can upload multiple images to the server, with asynchronous handling powered by Kotlin coroutines.
- **SharedPreferences**: Store important user data locally, using JSON strings for object storage.
- **Real Path from URI**: Implemented functionality for retrieving real file paths to facilitate smooth file uploads.
- **Concurrent Data Updates with Interfaces**: Interface-based implementation to ensure simultaneous data updates across different app components.

## How to Use

1. **Download the App**: [Download APK](https://drive.google.com/file/d/1MdKfK8mmhE6lGTHWpNMpLOAQGzRM2hVl/view?usp=share_link)
2. **Install the App** on your Android device.
3. **Explore the Features**:
    - Visit the Profile section to manage your details.
    - Browse through the Gallery to see images.
    - View the list of Team Members.
    - Make a donation through the secure Razorpay gateway.

## Technologies Used

- **Kotlin**: For Android development.
- **Retrofit**: For handling network requests.
- **Itext**: For converting code to pdf doc 
- **MongoDB**: Used as the primary database.
- **Vercel**: Backend server is hosted on Vercel.
- **Razorpay**: Integrated for handling donations.
- **XML**: Used for building the UI components.
- **Cloudinary**: Used for uploading images 

## Project Structure

- ## Admin
  
- **Activities**
  - `AdminLoginActivity` - Custom signin using server calls 
  - `AdminActivity` - For managing drawer layout , and all the fragments
- **Fragments**
  - `AddMemberFragment` - Here you can add , delete , update team members
  - `EditMemberFragment` - For updating team members data i.e- profile image, etc.
  - `EventAdminFragment` - Here you can add , delete , update events data
  - `EditMemberFragment` - For updating events data i.e- title, image, etc.
  - `GalleryFragment` - For uploading multiple images and deleting images from the server.
  - `HomeAdminFragment` - Landing page after admin login , shows stats of donations and total members.
  - `PaymentsFragment` - Shows donwlaodable table of all the donations received by the organisation
  - `RegisterAdminFragment` - To register new admin
  - `ProfileFragment` - For editing users detail(name ,email, profile)
 
- ## User  

- **Fragments**:
  - `ProfileFragment`: Handles user interactions in the profile section, including navigation, contact options, and donation redirection.
  - `HomeFragment`: Shows the automatic slider , members llist and gallery.
  - `BaseFragment`: This is the parent fragment whose properties are inherited by various fragments i- showing progress , opening link function , etc.
  - `EventsFragment`: Shows the list of events rendered from the server.
  - `MemberFragment`: Describes about the members of the organisation.

- **Activities**:
  - `PaymentActivity`: Manages the Razorpay payment flow, including payment initiation and handling success or failure callbacks.
  - `MainActivity`: Manages the fragment navigation using bottom navigationbar.
  - `SigninActivity` `LoginActivity` : Handles user login and signup
 
- ## Common
- **ViewModel**:
  - `DataViewModel`: Manages UI-related data for the gallery adn events. It fetches images from the server, handles loading states, and logs any errors.
  - `AdminAuthViewModel` : Manages events related admin login , fetchAdmindata, logout ,update etc.

- **Retrofit API**:
  - `ApiService`: Interface defining the API endpoints for retrieving, uploading, and deleting images, as well as fetching team members.
  - `RegisterAdminApi`: Interface defining the API endpoints for retrieving event details.

- **Networking**:
  - `RetrofitClient`: Singleton object that initializes Retrofit with the base URL and provides an instance of `ServiceApi` and `RegisterAdminApi`.
 
    and more...

## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/KrishnaTech6/aprajita-foundation.git
    ```

2. **Open the project in Android Studio**.

3. **Build the project**:
    - Ensure you have the latest version of Android Studio.
    - Sync Gradle files if prompted.

4. **Run the app**:
    - Connect an Android device or start an emulator.
    - Click on the "Run" button in Android Studio.




