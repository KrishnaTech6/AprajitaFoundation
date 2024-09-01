# Aprajita Foundation Android App

This project is an Android application developed for the Aprajita Foundation. The app features a gallery where users can view, upload, and delete images. It also includes a profile section that allows users to contact the foundation through various means, make donations through Razorpay, and more.
- I have provided interface for admin, where they can get , post, delete, update gallery images , events , members .
- Razorpay payment is also integrated: You can download and share receipt after payment
- Cloudinary for saving the images on cloud: Found some issues with dependency and helped in stackoverflow problem for that
- Server calls using Retrofit library.
- Creating pdf from code using Itext library: converting payments details list to table and saving in the pdf
- Dark mode/Light mode 
- Firebase google login for users and normal server login for admin.
- Multiple image selection to upload to the server 
- Users have various options in profile like : They can share the apk , contact on wtsp , contact through gmail, 
- Bottom navigation for user and drawer layout for Admins 
- Coroutines used for asynchronous operations spcl: suspendCoroutine used for multiple images upload to cloud
- getting real path from URI implemented
- Interaface used for simultanoes data changes
- sharedPreferneces for saving important data required by the user: object to json string
and much more
## How to Use

1. **Download the App**: [Download APK](https://drive.google.com/file/d/1MdKfK8mmhE6lGTHWpNMpLOAQGzRM2hVl/view?usp=share_link)
2. **Install the App** on your Android device.
3. **Explore the Features**:
    - Visit the Profile section to manage your details.
    - Browse through the Gallery to see images.
    - View the list of Team Members.
    - Make a donation through the secure Razorpay gateway.

## App Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/a6858752-22e5-4def-ba64-553ced8359e3" width="200" />
  <img src="https://github.com/user-attachments/assets/11ffa8f0-032a-40d9-b109-4e851415bebb" width="200" />
  <img src="https://github.com/user-attachments/assets/8484a16a-95b1-467f-b1f3-f00401f34ab1" width="200" />
  <img src="https://github.com/user-attachments/assets/1b188d14-2e30-4491-a639-cf01911763f2" width="200" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/db5e6103-861f-4fed-b831-9f0ca32c6ef4" width="200" />
  <img src="https://github.com/user-attachments/assets/5c8095a1-a017-4829-a09e-41a8f0a66df9" width="200" />
  <img src="https://github.com/user-attachments/assets/db645ce5-5d45-409d-897b-87fff9a7504a" width="200" />
  <img src="https://github.com/user-attachments/assets/93864e5c-0ea2-48ed-be70-660c252015aa" width="200" />
</p>

<p align="center">
  
  <img src="https://github.com/user-attachments/assets/9fe8e1ff-f919-4002-8ed1-9a35f95291cc" width="200" />
   <img src="https://github.com/user-attachments/assets/69f4cc35-ee9d-47a3-8f72-ef54a909f005" width="200" />
  <img src="https://github.com/user-attachments/assets/3a468446-55c9-43b7-9d37-1768fb43fc50" width="200" />

  <img src="https://github.com/user-attachments/assets/5e37fb0a-ee42-4078-bfbe-d276a92640f6" width="200" />
</p>

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
    git clone https://github.com/your-repo/aprajita-foundation.git
    ```

2. **Open the project in Android Studio**.

3. **Build the project**:
    - Ensure you have the latest version of Android Studio.
    - Sync Gradle files if prompted.

4. **Run the app**:
    - Connect an Android device or start an emulator.
    - Click on the "Run" button in Android Studio.




