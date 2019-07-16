# Project 3 - *SimpleInstagram*

**SimpleInstagram** is a photo sharing app using Parse as its backend.

Time spent: **X** hours spent in total

## Highlight of project: comments

- DetailActivity: has a recycler view that contains the comments on the post that's passed through the intent (can be reached by clicking on a post from a profile or from the feed) 
    - The queryComments method in DetailActivity queries the comments for that post in order of date created from the Parse server. 
    - The recycler view is contained in a SwipeRefreshLayout so you can swipe down to refresh the comments.
    - EditText and Comment button: can type comment and the button has an onclick listener so the comment is saved on the server and the adapter is notified.

- CommentsAdapter: adapts each InstaComment to a cell in the recycler view, setting the profile picture of the user, handle, timestamp, and text. 

- InstaComment: this class extends ParseObject, and has methods to query for a certain post and getters and setters for the comment text, post, user and timestamp.


## User Stories

The following **required** functionality is completed:

- [x] User sees app icon in home screen.
- [x] User can sign up to create a new account using Parse authentication
- [x] User can log in and log out of his or her account
- [x] The current signed in user is persisted across app restarts
- [x] User can take a photo, add a caption, and post it to "Instagram"
- [x] User can view the last 20 posts submitted to "Instagram"
- [x] User can pull to refresh the last 20 posts submitted to "Instagram"
- [x] User can tap a post to view post details, including timestamp and caption.

The following **stretch** features are implemented:

- [x] Style the login page to look like the real Instagram login page.
- [x] Style the feed to look like the real Instagram feed.
- [x] User should switch between different tabs - viewing all posts (feed view), capture (camera and photo gallery view) and profile tabs (posts made) using a Bottom Navigation View.
- [x] User can load more posts once he or she reaches the bottom of the feed using infinite scrolling.
- [x] Show the username and creation time for each post
- [x] After the user submits a new post, show an indeterminate progress bar while the post is being uploaded to Parse
- User Profiles:
  - [x] Allow the logged in user to add a profile photo
  - [x] Display the profile photo with each post
  - [x] Tapping on a post's username or profile photo goes to that user's profile page
- [x] User can comment on a post and see all comments for each post in the post details screen.
- [ ] User can like a post and see number of likes for each post in the post details screen.
- [ ] Create a custom Camera View on your phone.
- [x] Run your app on your phone and use the camera to take the photo

The following **additional** features are implemented:

- [ ] List anything else that you can get done to improve the app functionality!

Please list two areas of the assignment you'd like to **discuss further with your peers** during the next class (examples include better ways to implement something, how to extend your app in certain ways, etc):

1.
2.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/mgarciaferreiro/simple-instagram/blob/master/instagram_walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Credits

List an 3rd party libraries, icons, graphics, or other assets you used in your app.

- [Android Async Http Client](http://loopj.com/android-async-http/) - networking library


## Notes

Describe any challenges encountered while building the app.

I had a isRecyclable decremented below zero in my posts Recycler view when I tried to like posts, which nobody could figure out how to fix, and even though I implemented almost all the like functionality I wasn't able to get it to work well after spending all the last day on it.

## License

    Copyright [yyyy] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
