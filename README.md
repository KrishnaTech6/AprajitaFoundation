# Aprajita Foundation Android App

This project is an Android application developed for the Aprajita Foundation. The app features a gallery where users can view, upload, and delete images. It also includes a profile section that allows users to contact the foundation through various means, make donations through Razorpay, and more.

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

## Project Structure

- **Fragments**:
  - `ProfileFragment`: Handles user interactions in the profile section, including navigation, contact options, and donation redirection.

- **ViewModel**:
  - `DataViewModel`: Manages UI-related data for the gallery. It fetches images from the server, handles loading states, and logs any errors.

- **Retrofit API**:
  - `GalleryApi`: Interface defining the API endpoints for retrieving, uploading, and deleting images, as well as fetching team members.

- **Networking**:
  - `RetrofitClient`: Singleton object that initializes Retrofit with the base URL and provides an instance of `GalleryApi`.

- **Payment Activity**:
  - `PaymentActivity`: Manages the Razorpay payment flow, including payment initiation and handling success or failure callbacks.

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

## Razorpay Integration

The app integrates with the Razorpay payment gateway to handle donations.

### Payment Flow

1. **Initiating Payment**:
   - In the `ProfileFragment`, clicking the donation button will navigate to the `PaymentActivity`.
   - `PaymentActivity` uses the Razorpay Android SDK to initiate the payment process.

2. **Handling Callbacks**:
   - The payment success and failure callbacks are handled within `PaymentActivity`.
   - On payment success, a success message is displayed, and the user is navigated back to the profile.
   - On payment failure, an error message is shown to the user.
