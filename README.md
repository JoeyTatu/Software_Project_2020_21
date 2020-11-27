# General information
* [National College of Ireland](https://ncirl.ie)
* Project Proposal
* Automatic Retinopathy Detection Using Digital Image Processing via a Smart Device
* November 2020
* BSc. (Honours) in Computing
* Software Development
* 2020/2021
* Joey Tatú
* 15015556
* [joey.tatu@student.ncirl.ie](mailto:joey.tatu@student.ncirl.ie)

---

# Objectives
## General
The objective of this project is to create an app that detects retinopathy. This will be created as an Android application using Android Studio.

## Checking eye health
The user will be able to use the in-built camera on their Android phone to scan their eyes to be able to detect retinopathy and to determine their health. The scan will be completed using artificial intelligence and image processing. It is expected that this app will be provided to medical professionals to assist in their diagnosis of a patient. 

## Features
Apart from checking the patient’s retinopathy, the app will be able to record patient’s general Health, for example; blood sugars, blood pressure, etc. The medical professional will be able to monitor the patient’s health and determine if the retinopathy is improving or worsening.

## Database
The database will be implemented via Google Firebase Realtime Database. This is a No-SQL database that is generally used for Android apps. 
The main tables in the database will be Patients, Health Information and Retinopathy Results.
The Patients table will contain general information on the patient such as their name, age, and medication they are on. The Health Information table will contain the patient’s Health status such as any illnesses, blood sugars, blood pressure, weight and height. The Retinopathy Results table will contain the results of the retinopathy scan, the damage of the retina, the white to yellow discolouration, as well as the date and time the scan was taken. 
Artificial Intelligence
Artificial intelligence and image processing will be used to scan the patient’s eyes to detect retinopathy. Using convolution neural networks (CNNs) for image processing, issues with the retina will be detected and analysed. 

# Background
The idea for this Project began as a vegan health and fitness Android app that would take an image of a person’s eyes and only detect the white to yellow ratio of the sclera. The original idea was founded in late August 2020. As a de facto, there are many fitness and health apps available. A health app was proposed for this project, but it was felt that this idea has been exhausted with little success for less popular apps.
The author decided to become vegan in mid-September after they saw that male chicks are destroyed shortly after birth. This is known as “chick culling”. (Wray, 2020) This was a last straw for them, and they decided to become vegan. While personally eliciting information from switching to a vegan diet, there was not that much information was provided. The information that was provide was just recipes and very general information about veganism. The author felt at a loss on how to correctly transition to veganism.

Being vegan, one asks themselves: “Can I eat this?” After trying a few vegan Android apps (described more in Research below), there was no solid information from these apps on whether a product is suitable for vegan. The main answer that was received was “Not sure”. But that was an impasse, as there was no connection to where information could be retrieved to get information on whether the food is Ok for the vegan diet. This project is to redesign vegan, health and fitness apps that are currently available and go above and beyond with improvements. This is how the idea of a vegan health app was discovered. 

The idea behind identifying the sclera was founded due to a family member of the author having issues with their gut. This caused the person’s sclera to become a tint of yellow and their skin to become sallow. The author thought that a health app with scanning the sclera would be a beneficial idea.
As the project idea progressed, the innovation for the Project was lacking. Creating a general health and fitness app has been done many times previously. It was decided to change the project slightly and to focus on detecting retinopathy in a patient’s eyes using artificial intelligence and image processing. 
