plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.proyectointegrador.madridindustria"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.proyectointegrador.madridindustria"
        minSdk = 26
        targetSdk = 34
        versionCode = 8
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    implementation("com.google.android.play:core:1.10.3")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.maps:google-maps-services:0.15.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.google.mlkit:translate:17.0.2")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-firestore:25.0.0")
    implementation ("com.google.firebase:firebase-storage:21.0.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.activity:activity:1.9.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.viewpager:viewpager:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.google.firebase:firebase-messaging:24.0.0")
}