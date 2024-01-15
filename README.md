# MovieBrowser

## Tech Stack
- Kotlin
- Jetpack Compose
- Retrofit & Moshi
- Android Room
- Dagger Hilt

## Architecture
- I used the MVVM Architecture make the project structure easier to navigate and more maintanable.

## UI Design
- Inspiration from [Dribble](https://dribbble.com/shots/4276185-Movie-Application)

## Persistence Functionality
The Persistence functionality relies on the Android Room Library by caching the Search Keyword, Search Results, and Item Displayed in the Details Screen. I used this method of persistence so that the Home Screen list and Details Screen will have a single source of truth, the local database, online or offline.

1. **Home Screen**
    - The Home Screen list display retrieves its data from the local Favorites list and Cache Movies list.
    - The Cache Movies list, or the Search Results list is saved every time the app calls the Search API.
    - This Search Results list is used to determine the user's last state in the app, including the last keywords searched, results returned, and the item displayed in the details screen.
    - When the user clicks the Movie Item, it looks up the Cache Movies table, updates the `currentlyDisplayed` row, and navigates to the Details Screen.
    
2. **Details Screen**
    - The Details screen also listens for changes in the Favorites list, so that when the user adds/removes the movie in the Favorites, the UI will update accordingly.
    - The Details screen also retrieves its movie from the database, looking up the `currentlyDisplayed` item in the Cache Movies table.
    - On Start, in case the Cache Movies list is not empty, and there is atleast one `currentlyDisplayed` row that is **true**, it should automatically restart from the Details Screen.
    - Updating the **Favorite** status of a movie item should also update in the Home Screen list.