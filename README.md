# Landmark Remark

This is an application that allows you to add Notes on Google Maps based on your current location.

  The application uses the Google Maps API to display the map. 
  The application uses Firebase for registration, login, and data storage.

## Time
- I spent about 24 hours on this project :
    + about 18 hours thinking about the solution and coding
    + about 6 hours testing and fixing bugs and setting up the environment or writing related files

- There was an issue while implementing the search method: I tried to use a 3rd party called Algolia to optimize the search ability of the project, but since it was new to me, it took a lot of time to learn and set it up so I didn't use it anymore

## System Requirements

  Android Studio Koala | 2024.1.1
  Minimum Android API level is 26
  Gradle 8.7
  Firebase Firestore = “25.0.0”
  
### Installation Guide
  Clone this project and build it in Android Studio.

#### Usage Guide

- You need to register an account (if you don’t have one). Since this is a simple application, I have skipped the verification steps, making registration very easy.
- Log in to the application.
- You need to grant Location permission to the application: Since the application uses Google Maps, it needs your location to display and perform other features.
- After granting permission, the application will move to your current location and display location information.
    + If you have added a Note for the current location, the Note information will be displayed.
- Clicking on the information box displayed at the location will show a ModalBottomSheetDialog including:
    + Location information.

    + An X button to close the current dialog.
    + A +AddNote button to add the Note information you want.
    + A list of Notes that you and other users have added to that location.
- The MyLocation button (red button in the middle right of the screen) helps you move the camera to your current location.
- Search bar: You can use this search bar to search for usernames and the content of the notes you have added.
    + After entering the search keyword -> The application will display a list of notes related to the keyword you entered. You can click on these Notes to move to the location where the Note is saved.
- UserProfile button (located on the right above the Search bar): When you click this button, a small dialog will appear including :
    + Your information: UserName, UserEmail.
    + SignOut button: Helps you log out of your account.
    + A button to display the list of your notes -> Displays a screen containing the list of notes you have saved -> click on a Note to move to the corresponding location.

