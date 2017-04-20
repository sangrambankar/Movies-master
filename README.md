# Movies DB App
MoviesDB - The application fetches movie data using themoviedb.org API.

# ScreenShot:

<p align="center">
  <img src="https://raw.githubusercontent.com/sangrambankar/Movies-master/627e86ea9241be500f79e8aa4010614aa8c30239/Screenshot_20170419-174915.png" width="280">
  <img src="https://raw.githubusercontent.com/sangrambankar/Movies-master/627e86ea9241be500f79e8aa4010614aa8c30239/Screenshot_20170419-174924.png" width="280"> 
</p>

## Features:
- Display popular movies using themoviedb.org API
- Detailed View of each movie with trailers, rating , cast, etc
- Favourites movies added to your list using SQLite database

## Used libraries:
- [RxJava](https://github.com/ReactiveX/RxAndroid) and [Retrofit](http://square.github.io/retrofit/) libraries to manage Rest Client
- [ButterKnife](http://jakewharton.github.io/butterknife/) library to bind views and avoid boilerplate views code
- [EventBus](https://github.com/greenrobot/EventBus) library to send data between components and makes code simpler
- [Picasso](http://square.github.io/picasso/) library to manage images


## Design pattern
MVP (Model View Presenter) pattern to keep it simple and make the code testable, robust and easier to maintain

## Build from the source:

In order to build the app you must provide your own API key from themoviedb.org
Open local.properties file and paste your key instead of ***YOUR_API_KEY*** text in this line:
```
dbmovieskey="YOUR_API_KEY"
```

## License:
```
Copyright 2017, Sangram Bankar

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
