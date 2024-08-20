# Aprajita Foundation Android App

This project is an Android application developed for the Aprajita Foundation. The app features a gallery where users can view, upload, and delete images. It also includes a profile section that allows users to contact the foundation through various means, make donations through Razorpay, and more.

## How to Use

1. **Download the App**: [Download APK](https://drive.google.com/file/d/1MdKfK8mmhE6lGTHWpNMpLOAQGzRM2hVl/view?usp=share_link)
2. **Install the App** on your Android device.
3. **Explore the Features**:
    - Visit the Profile section to manage your details.
    - Browse through the Gallery to see images.
    - View the list of Team Members.
    - Make a donation through the secure Razorpay gateway.

## Features

- **Profile Section**:
  - WhatsApp contact button.
  - Website link redirection.
  - APK sharing option.
  - Email contact button.
  - Donation button leading to a payment screen using Razorpay.

- **Gallery**:
  - Fetch gallery images from the server.
  - Upload images to the gallery.
  - Delete images from the gallery.
  - Display images in a non-symmetric grid similar to Google Photos.

- **Payment Integration**:
  - Razorpay payment gateway integrated for handling donations.
    
## Technologies Used

- **Kotlin**: For Android development.
- **Retrofit**: For handling network requests.
- **MongoDB**: Used as the primary database.
- **Vercel**: Backend server is hosted on Vercel.
- **Razorpay**: Integrated for handling donations.
- **Jetpack Compose**: Used for building the UI components.

## Project Structure

- **Fragments**:
  - `ProfileFragment`: Handles user interactions in the profile section, including navigation, contact options, and donation redirection.
  - `HomeFragment`: Shows the automatic slider , members llist and gallery.
  - `BaseFragment`: This is the parent fragment whose properties are inherited by various fragments i- showing progress , opening link function , etc.
  - `EventsFragment`: Shows the list of events rendered from the server.
  - `MemberFragment`: Describes about the members of the organisation.

- **ViewModel**:
  - `DataViewModel`: Manages UI-related data for the gallery adn events. It fetches images from the server, handles loading states, and logs any errors.

- **Retrofit API**:
  - `GalleryApi`: Interface defining the API endpoints for retrieving, uploading, and deleting images, as well as fetching team members.
  - `EventsApi`: Interface defining the API endpoints for retrieving event details.

- **Networking**:
  - `RetrofitClient`: Singleton object that initializes Retrofit with the base URL and provides an instance of `GalleryApi`.

- **Activities**:
  - `PaymentActivity`: Manages the Razorpay payment flow, including payment initiation and handling success or failure callbacks.
  - `MainActivity`: Manages the fragment navigation using bottom navigationbar.
  - `SigninActivity` `LoginActivity` : Handles user login and signup

## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/your-repo/aprajita-foundation.git
    ```

2. **Open the project in Android Studio**.

3. **Build the project**:
    - Ensure you have the latest version of Android Studio.
    - Sync Gradle files if prompted.

4. **Run the app**:
    - Connect an Android device or start an emulator.
    - Click on the "Run" button in Android Studio.

## API Endpoints

- **Gallery Images**:
  - `GET /get-gallery-images`: Retrieves the list of gallery images.
  - `POST /upload-gallery-image`: Uploads a new image to the gallery.
  - `DELETE /delete-gallery-image/{id}`: Deletes an image from the gallery.

- **Team Members**:
  - `GET /get-team-members`: Retrieves the list of team members.
    
- **All Events**:
  - `GET /get-events`: Retrieves the list of events.

## Razorpay Integration

The app integrates with the Razorpay payment gateway to handle donations.

## ViewModel & LiveData

The app uses Android's ViewModel and LiveData components to manage UI-related data in a lifecycle-conscious way. The `DataViewModel` handles the fetching of gallery images and manages the loading state.


### Payment Flow

1. **Initiating Payment**:
   - In the `ProfileFragment`, clicking the donation button will navigate to the `PaymentActivity`.
   - `PaymentActivity` uses the Razorpay Android SDK to initiate the payment process.

2. **Handling Callbacks**:
   - The payment success and failure callbacks are handled within `PaymentActivity`.
   - On payment success, a success message is displayed, and the user is navigated back to the profile.
   - On payment failure, an error message is shown to the user.



