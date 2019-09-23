# Ap&eacute;rio

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Aperio is the place you go to buy any local paintings or craftsmanship made with passion from 100% local artists.

### App Evaluation

- **Category:** Art & Design
- **Mobile:** This app is primarily developed for mobile in order to be available everywhere and anytime to the user.
- **Story:** Apério is an online market where anyone can buy handicraft work from local artists. The app help you find the closest art shops or artisan nearby, it let's you browse their available products and communicate with them to negotiate. 
- **Market:** Any individual could choose to use this app, and to keep it a safe environment, people would be organized into age groups.
- **Habit:** This app could be used as often or unoften as the user wanted depending on their needs, and what exactly they’re looking for.
- **Scope:** This could start a "*Nearby art seller finder*" type of app but perhaps later could evolve as an online seller exclusively made for atisans. Thus creating a large network of artisans, which would be beneficial to them and to the end user.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Nearby vendors can be found on a map
- [ ] User can browse a vendor's products
- [ ] User can message vendor to negociate
- [x] Vendor can add product(with product details)
- [x] Vendor can create an account
- [x] Vendor can login to account
- [x] Vendor can add account informations(Location, name, profession...) within account creation

**Optional Nice-to-have Stories**

- [ ] User can search by category or region
- [ ] User can search for a specific vendor
- [ ] User can add product to cart
- [ ] User can see cart contents
- [ ] User can pay for product via external payment providers.
- [x] User can signup/login to app
- [x] User stay connect after leaving the app(session)
- [ ] User can save favorited items


### 2. Screen Archetypes

* Login screen
   * Vendor can login to account
* Registration screen
   * Vendor can create an account
* Mapview screen
    * Nearby vendors can be found on a map
* Stream screen
   * user can see cart contents
* Detail screen
   * User can browse a vendor's products
   * User can pay for product via external payment providers.
* Creation screen
   * Vendor can add product(with product details)
   * User can add product to cart
* Profile screen
   * Vendor can add account informations(Location, name, profession...) within account creation 


### 3. Navigation - User

**Tab Navigation** (Tab to Screen)

* Mapview screen

**Flow Navigation** (Screen to Screen)

* Login
   * Mapview screen
   * Registration screen
* Registration
   * Mapview screen
   * Login screen
* Mapview screen
   * Vendor detail screen
   * Cart screen
   * (To user info page, but this is a later feature)
* Search screen
    * Product detail screen
    * Mapview screen

### 4. Navigation - Vendor

**Tab Navigation** (Tab to Screen)

* Mapview screen
* MyProduct screen

**Flow Navigation** (Screen to Screen)

* Login
   * Mapview screen
   * Registration screen
* Registration
   * Mapview screen
   * Login screen
* Mapview screen
   * Vendor detail screen
   * Cart screen
   * (To user info page, but this is a later feature)
* Search screen
    * Product detail screen
    * Mapview screen
* MyProduct screen
    * Add product screen
    * Mapview screen


## Wireframes

<img src="https://i.imgur.com/DX3Y6zU.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups
<span>
<img src="https://i.imgur.com/SK0SPx5.png" width=100>
<img src="https://i.imgur.com/dSDAvk7.png" width=100>
<img src="https://i.imgur.com/y8Vzb0B.png" width=100>
<img src="https://i.imgur.com/pzBc37M.png" width=100>
<img src="https://i.imgur.com/qjUbNzQ.png" width=100>
<img src="https://i.imgur.com/1f24OLJ.png" width=100></span>

### [BONUS] Interactive Prototype
https://www.figma.com/file/dBvg7Cl4O4yo14Hh0Z2X0L/AperioProject

<img src="https://i.imgur.com/xvwUmCx.gif">

## Schema 

### Models

Model: User

| Property  | Type     | Description                           |
| --------  | -------- | --------------------------            |
| ObjectId  | String   | Unique id for User account            |
| createdAt | DateTime | date created user (default field)     |
|updatedAt 	| DateTime | date last updated user (default field)|
| UserName | String   | name of the user                |
| Email     | String   | Email account of the user             |
| Password  | String   | Password of the user                  |
| Phone     | Number   | The phone number of the user          |
| Category  | Boolean   | To determine the type of user                      |
| ProfileImg   | File     | The Profile picture of the user       |
| Address    | String   | Address of the user                  |
|Longitude | Number | the current longitude of the current user |
| Latitude | Number | The current latitude of the current user |

Model: Product

| Property  | Type     | Description                           |
| --------  | -------- | ---------------------------           |
| ObjectId  | String   | Unique Id for a product               |
| createdAt | DateTime | date created user (default field)     |
| updatedAt | DateTime | date last updated user (default field)|
| Product   | File     | Picture of the product                |
| Description| String   | Description of the Product            |
| Category  | Pointer   | pointer to category product                      |
| Vendor | Pointer | To determine who's selling

Model: Message

| Property | Type   | Description |
| -------- | ------ | ----------- |
| ObjectId | String | Unique Id for message |
| createAt | DateTime | date of creation |
| updateAt | Datetime | date last updated (default field) |
| user | Pointer | Pointer of the current user |
| vendor | Pointer | Pointer of the vendor |
| message | String | The message sent |

Model : Category

| Property | Type   | Description |
| -------- | ------ | ----------- |
| ObjectId | String | Unique Id for category |
| createAt | DateTime | date of creation |
| updateAt | Datetime | date last updated (default field) |
| category | String | The category text |

### Networking
LoginPage Screen
- (Read/GET) Query logged in user object

HomeScreen
- (Create/POST) Create a new line to post a new product
- (Read/GET) Query that lists the product poster the artist

Profil
- (Read/GET) Query that displays the profile of the user and their product poster on the application.
- (Update/PUT) Update user profile image

Detail
- (Read/GET) Query that displays the details of a product

Sign In Screen
- (Create/POST) Create a new user object
- (Read/GET) Query that checks if entered email already exists in database

Pre-detail
- (Read/GET) Query that lists the product poster by the artist
