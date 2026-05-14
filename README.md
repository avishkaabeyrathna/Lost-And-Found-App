# Lost and Found App

## Overview
This is an Android application developed using Java in Android Studio. The app allows users to create and manage lost and found item adverts.

Users can create lost or found posts, upload images, select a location, view all adverts, display items on a map, and remove adverts after an item has been returned to its owner.

The application uses SQLite for local storage and Google Maps geo features.

---

## Features

### Lost and Found Management
- Create lost item adverts
- Create found item adverts
- Upload an image for each advert
- View all adverts
- View advert details
- Remove adverts after item recovery

### Category Search
- Filter adverts by category:
  - Electronics
  - Pets
  - Wallets
  - Keys
  - Other

### Time Features
- Automatic timestamp generation
- Users can see how recent a listing is

### Geo Features
- Select location using Google Places Autocomplete
- Use current GPS location
- Store latitude and longitude coordinates
- Display all items on Google Maps
- Radius-based search
- Show only items within X km of the user location

---

## Technologies Used

- Java
- Android Studio
- SQLite Database
- RecyclerView
- Google Maps SDK
- Google Places API
- Fused Location Provider
- Intents and Activities

---

## App Structure

### MainActivity
Main home screen for app navigation.

### CreateAdvertActivity
Allows users to create adverts, upload images and select locations.

### ListAdvertActivity
Displays all adverts and supports category filtering.

### DetailActivity
Displays full advert details and allows users to remove adverts.

### MapsActivity
Displays saved adverts on Google Maps and applies radius filtering.

### DatabaseHelper
Handles SQLite CRUD operations.

### AdvertAdapter
Connects advert data with RecyclerView.

---

## Database Features

SQLite CRUD operations:

- Create: save a new advert
- Read: retrieve adverts
- Delete: remove adverts
- Filter: search adverts by category
- Store geographic coordinates

---

## Geo Features

The app supports location-based functionality through Google Maps:

- Google Places Autocomplete
- Current location detection
- Marker display
- Radius filtering

Distance is calculated using:

Location.distanceBetween()

This allows users to find nearby lost and found items.

---

## How to Run

1. Open project in Android Studio
2. Allow Gradle sync
3. Add Google Maps API key
4. Enable:
   - Maps SDK for Android
   - Places API
5. Run on emulator or Android device

---
