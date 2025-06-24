# 📱 WanderQuest Mobile App

A mobile application built with **Kotlin** for exploring the city of **Poznań**, Poland. It combines real-time event tracking via an external API and curated sightseeing routes, which are managed and stored in **Firebase**.

## ✨ Features

- 🔍 **Event Tracking**  
  Stay up-to-date with city events using external public APIs integrated into the app.

- 🗺️ **Tourist Routes**  
  Discover Poznań using predefined sightseeing routes consisting of various key locations (landmarks, historical sites, etc.).

- 👤 **Admin Panel (Firebase)**  
  An integrated Firebase backend enables authorized administrators to:
  - Add/edit/delete sightseeing **routes**
  - Add/edit/delete **locations**
  - Manage visibility and content for app users

- ☁️ **Firebase Integration**
  - Realtime database for storing and updating route/location data
  - Authentication for administrative access

## 🔧 Technology Stack

| Component     | Technology                |
|---------------|---------------------------|
| Language      | Kotlin                    |
| Architecture  | MVVM                      |
| Backend       | Firebase (Realtime DB)    |
| External API  | City Events API (REST)    |
| UI Framework  | Jetpack Compose / XML     |
